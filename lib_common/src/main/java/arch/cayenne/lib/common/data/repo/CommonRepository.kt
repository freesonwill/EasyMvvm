package arch.cayenne.lib.common.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.LogUtilsExt.logi
import arch.cayenne.lib.common.data.UserDataKey
import arch.cayenne.lib.common.data.UserDataManager
import arch.cayenne.lib.common.utils.ext.SportStringExt.balanceStringToLong
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.lib.socket.WebSocketManager
import arch.cayenne.lib.socket.data.ApiCode
import arch.cayenne.lib.socket.data.LoginTokenFailedError
import arch.cayenne.lib.socket.data.SocketResponseData
import arch.cayenne.lib.socket.extension.observeProtoMessage
import arch.cayenne.lib.socket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class CommonRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val userDataManager: UserDataManager,
    private val infoDao: InfoDao,
) : BaseRepository() {

    fun getConnectStateFlow() = socketManager.getConnectStateFlow()

    suspend fun sendLogin(): SocketResponseData<Client.LoginResp> {
        val uid = userDataManager.getIntValue(UserDataKey.KEY_UID, -1)
        val token = userDataManager.getStringValue(UserDataKey.KEY_TOKEN, "")
        //沒有Token
        if (uid == -1 || token == "") {
            return SocketResponseData(
                mid = ApiCode.LOGIN.mid,
                sid = ApiCode.LOGIN.sid,
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
                this.lang = "zh-CN"
                this.platform = 5
                this.oddType = 0
            }.build()
        }

        if (loginResp.data != null && loginResp.data!!.success) {
            val balance = getBalance()
            infoDao.insert(InfoBean(uid, balance.balanceStringToLong(), loginResp.data!!.success))
        }

        return loginResp
    }

    //登入成功後主動取得餘額
    private suspend fun getBalance(): String {
        val balanceResp = socketManager.sendAndWaitProtoMessageResponse<Client.BalanceResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.BALANCE
        ){
            Client.BalanceReq.newBuilder().build()
        }
        return if (balanceResp.data != null) {
            balanceResp.data!!.balance
        } else {
            ""
        }
    }

    //觀察從API來的餘額變化並塞進資料庫
    suspend fun observeBalanceChange() {
        socketManager.observeProtoMessage<Client.BalanceNotify>(ApiCode.BALANCE_NOTIFY).collect {
            if (it.data == null || it.data!!.balance.isNullOrEmpty())
                return@collect
            infoDao.queryInfo()?.apply {
                infoDao.update(InfoBean(this.uid, it.data!!.balance.balanceStringToLong(), this.login))
            }
        }
    }

    fun reset() {
        socketManager.reset()
    }
}