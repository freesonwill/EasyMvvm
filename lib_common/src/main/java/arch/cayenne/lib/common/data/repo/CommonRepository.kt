package arch.cayenne.lib.common.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.LanguageType
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.SportStringExt.balanceStringToLong
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.entity.BetResultLiteBean
import arch.cayenne.lib.database.entity.BetResultStatusEnum
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.data.LoginTokenFailedError
import arch.cayenne.lib.websocket.data.SocketResponseData
import arch.cayenne.lib.websocket.extension.observeProtoMessage
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import galaxy.common.proto.Common.Setting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class CommonRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val userDataManager: UserDataManager,
    private val infoDao: InfoDao,
    private val betDao: BetDao
) : BaseRepository() {

    private val betResultFlow = MutableSharedFlow<List<BetResultLiteBean>>()

    fun getConnectStateFlow() = socketManager.getConnectStateFlow()
    fun getBetResultFlow(): Flow<List<BetResultLiteBean>> = betResultFlow

    suspend fun sendLogin(): SocketResponseData<Client.LoginResp> {
        val uid = userDataManager.getValue(UserDataKey.KEY_UID, -1)
        val token = userDataManager.getValue(UserDataKey.KEY_TOKEN, "")
        //沒有Token
        if (uid == -1 || token == "") {
            return SocketResponseData(
                mid = ApiCode.LOGIN.mid,
                sid = ApiCode.LOGIN.sid,
                rid = 0,
                data = null,
                error = LoginTokenFailedError()
            )
        }
        val loginResp = socketManager.sendAndWaitProtoMessageResponse<Client.LoginResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.LOGIN
        ) {
            Client.LoginReq.newBuilder().apply {
                this.uid = uid.toLong()
                this.token = token
                this.platform = 5
                this.setting = getSystemSetting()

            }.build()
        }

        if (loginResp.data != null && loginResp.data!!.success) {
            val balanceBean = getBalance()
            infoDao.insert(
                InfoBean(
                    uid,
                    balanceBean.balance,
                    balanceBean.currency,
                    loginResp.data!!.success
                )
            )
        }

        return loginResp
    }

    //登入成功後主動取得餘額
    private suspend fun getBalance(): BalanceBean {
        val resp = socketManager.sendAndWaitProtoMessageResponse<Client.BalanceResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.BALANCE
        ) {
            Client.BalanceReq.newBuilder().build()
        }


        return if (resp.error == null && resp.data != null) {
            BalanceBean(resp.data!!.balance.balanceStringToLong(), resp.data!!.currency)
        } else {
            BalanceBean(0, "")
        }
    }

    //觀察從API來的餘額變化並塞進資料庫
    suspend fun observeBalanceChange() {
        socketManager.observeProtoMessage<Client.BalanceNotify>(ApiCode.BALANCE_NOTIFY).collect {
            if (it.data == null || it.data!!.balance.isNullOrEmpty())
                return@collect
            infoDao.queryInfo()?.apply {
                infoDao.update(
                    InfoBean(
                        this.uid,
                        it.data!!.balance.balanceStringToLong(),
                        this.currency,//账号余额通知中没有币种字段
                        this.login
                    )
                )
            }
        }
    }

    //觀察從API來的餘額變化並塞進資料庫
    suspend fun observeBettingOrderStatus() {
        socketManager.observeProtoMessage<Client.OrderStatusNotify>(ApiCode.ORDER_STATUS_NOTIFY)
            .collect {
                if (it.data == null || it.data!!.orderStatusList.isEmpty())
                    return@collect
                val resultList = mutableListOf<BetResultLiteBean>()
                scope.launch {
                    it.data!!.orderStatusList.forEach { resp ->
                        launch {
                            betDao.updateDetailResult(
                                resp.orderId,
                                BetResultStatusEnum.getStatusByCode(resp.status)
                            )
                        }.join()

                        betDao.getDetailByOrderId(resp.orderId)?.let { detail ->
                            val selection = betDao.getSelections(detail.betId)
                            val matchName = selection.map { s -> s.matchName }
                            val resultLiteBean = BetResultLiteBean(
                                matchName,
                                detail.comboK,
                                detail.comboV,
                                BetResultStatusEnum.getStatusByCode(resp.status) == BetResultStatusEnum.SUCCESS_BET
                            )
                            resultList.add(resultLiteBean)
                        }
                    }
                    betResultFlow.emit(resultList)
                }
            }
    }


    fun reset() {
        socketManager.reset()
    }

    //读取用户系统配置信息
    private fun getSystemSetting(): Setting {
        val language = getLanguageType()
        val oddsType = getOddsType()
        val sysGoal = getNotifyMatchType(MATCH_GOAL)
        val sysKick = getNotifyMatchType(MATCH_KICK)
        val app = getNotifyMatchType(MATCH_APP)
        return Setting.newBuilder().apply {
            lang = language          //语言类型
            oddType = oddsType       //赔率显示类型
            systemGoal = sysGoal     //系统通知-进球
            systemKickOff = sysKick  //系统通知-开球
            appGoal = app            //app内通知-开球
        }.build()
    }

    private fun getNotifyMatchType(type: Int): Common.NotifyMatchType {
        return Common.NotifyMatchType.newBuilder().apply {
            when (type) {
                MATCH_GOAL -> {
                    betMatch = getSystemBet()
                    collectMatch = getSystemFav()
                    allMatch = getSystemAll()
                }

                MATCH_KICK -> {
                    betMatch = getKickBet()
                    collectMatch = getKickFav()
                    allMatch = getKickAll()
                }

                else -> {
                    betMatch = getAppBet()
                    collectMatch = getAppFav()
                    allMatch = getAppAll()
                }
            }
        }.build()
    }

    private fun getLanguageType(): String {
        val lang = userDataManager.getValue(UserDataKey.KEY_LANGUAGE, "")
        return when (lang) {
            LanguageType.LANGUAGE_ENGLISH.value -> "en-US"
            LanguageType.LANGUAGE_ID.value -> "id-ID"
            LanguageType.LANGUAGE_PT.value -> "pt-PT"
            else -> "zh-CN"
        }
    }

    private fun getOddsType(): Int {
        return userDataManager.getValue(UserDataKey.KEY_ODDS, 0)
    }

    private fun getSystemBet(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_SYSTEM_BET, false)
    }

    private fun getSystemFav(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_SYSTEM_FAV, false)
    }

    private fun getSystemAll(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_SYSTEM_ALL, false)
    }

    private fun getKickBet(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_KICK_BET, false)
    }

    private fun getKickFav(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_KICK_FAV, false)
    }

    private fun getKickAll(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_KICK_ALL, false)
    }

    private fun getAppBet(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_APP_BET, false)
    }

    private fun getAppFav(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_APP_FAV, false)
    }

    private fun getAppAll(): Boolean {
        return userDataManager.getValue(UserDataKey.KEY_APP_ALL, false)
    }

    companion object {
        const val MATCH_GOAL = 1
        const val MATCH_KICK = 2
        const val MATCH_APP = 3
    }

    data class BalanceBean(val balance: Long, val currency: String)

    fun observeAppNotifyChange() =
        socketManager.observeProtoMessage<Client.AppNoticeNotify>(ApiCode.APP_NOTIFY)
}