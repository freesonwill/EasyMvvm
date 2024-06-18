package com.cn.game.sdk2.websocket.imp

import com.cn.game.sdk2.network.code.GameReqCode
import com.cn.game.sdk2.websocket.Betting
import com.cn.game.sdk2.websocket.BettingRecordBean
import com.cn.game.sdk2.websocket.DEFAULT_BIG
import com.cn.game.sdk2.websocket.GameServerMessageConvertFactory
import com.cn.game.sdk2.websocket.GameSocketClient
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.balance
import com.cn.game.sdk2.websocket.interfaces.GameService
import com.cn.game.sdk2.websocket.isCanBetting
import com.cn.game.sdk2.websocket.isEmpty
import com.cn.game.sdk2.websocket.isNotEmpty
import com.cn.game.sdk2.websocket.previousSuccess
import game.common.proto.ClientReq
import game.common.proto.ClientRes
import game.mod.proc.yf.proto.req.GameReq
import game.mod.proc.yf.proto.req.GameReq.AreaBetReq
import game.mod.proc.yf.proto.req.GameReq.BetReq
import game.mod.proc.yf.proto.res.GameRes

/**
 * 提供ui层调用的统一对象
 */
class GameServiceImp(private val client: GameSocketClient) : GameService,
    GameServerMessageConvertFactory {

    init {
        GameSocketManager.getInstance()?.setGameServerMessageConvertFactory(this)
    }

    override fun enterInfo() {
        send(
            8,
            GameReqCode.SUB_LOGON_REQ__LOGIN.toShort(),
            ClientReq.PingBackReq.newBuilder().build().toByteArray()
        )
    }

    override fun login(req: ClientReq.LoginReq) {
        val mid: Short = 7
        val sid: Short = 7
        send(mid, sid, req.toByteArray())
    }

    override fun enterGroup(req: GameReq.EnterGroup) {
        send(500, GameReqCode.C2S_ENTER_GROUP.toShort(), req.toByteArray())
    }

    override fun levelGroup() {
        send(500, GameReqCode.C2S_LEAVE_GROUP.toShort(), ByteArray(0))
    }

    override fun enterGame(req: GameReq.EnterMiniGame) {
        send(500, GameReqCode.C2S_ENTER_MINI_GAME.toShort(), req.toByteArray())
    }

    override fun levelGame(req: GameReq.LeaveMiniGamesReq) {
        send(500, GameReqCode.C2S_LEAVE_MINI_GAME.toShort(), req.toByteArray())
    }

    override fun bet(req: GameReq.BetReq) {
        send(500, GameReqCode.C2S_BET.toShort(), req.toByteArray())
    }

    override fun refreshScore() {
        send(500, GameReqCode.C2S_REFRESH_SCORE.toShort(), ByteArray(0))
    }

    override fun ping() {
        send(0, 2, ClientReq.PingBackReq.newBuilder().build().toByteArray())
    }

    private fun send(mid: Short, sid: Short, data: ByteArray) {
        kotlin.runCatching {
            val msg = client.newPack(mid, sid, data, data.size)
            client.send(msg)
        }.recoverCatching {
            it.printStackTrace()
        }
    }

    /**
     * 历史记录按钮
     */
    fun pushHistoryOfBetAction() {
        appListener?.historyOfBetAction()
    }

    /**
     * 联系客服按钮
     */
    fun pushCustomerServiceAction() {
        appListener?.customerServiceAction()
    }

    /**
     * token失效通知app
     */
    fun pushTokenLoseEffectiveness() {
        appListener?.getTokenLoseEffectiveness()
    }

    /**
     * 当前临时总下注金额
     */
    private var currentTempCountMoney = 0

    /**
     * 当前确认总下注金额
     */
    private var currentConfirmCountMoney = 0

    /**
     * 当前总下注金额
     */
    private var currentCountMoney = 0

    /**
     * 临时下注列表
     */
    private val bettingListTemp: MutableMap<Betting, BettingRecordBean> = mutableMapOf()

    /**
     * 已确认下注列表
     */
    private val bettingListConfirmed: MutableMap<Betting, BettingRecordBean> = mutableMapOf()

    /**
     * 临时下注
     * 该方法只在服务器状态是游戏中的时候才调用
     * @param recordBean 当前下注对象
     * @param block 下注成功回调
     *          - isMoneyEnough 当次下注余额是否足够
     *          - result 返回当次下注成功的结果，里面有当前的下注的总金额;当此参数为null时，表示上一次下注结果还未返回
     */
    fun addBetting(
        recordBean: BettingRecordBean,
        block: (isMoneyEnough: Boolean, result: BettingRecordBean?) -> Unit
    ) {
        if (previousSuccess) {
            currentTempCountMoney += recordBean.money
            currentCountMoney = currentTempCountMoney + currentConfirmCountMoney

            bettingListTemp.isNotEmpty {
                //已存在同样注区的下注 累计计算已下注金额
                if (it.containsKey(recordBean.bettingArea)) {
                    val existRecord = it[recordBean.bettingArea]
                    val currentMoney = existRecord?.money!! + recordBean.money
                    val countMoney = if (bettingListConfirmed.containsKey(recordBean.bettingArea)) {
                        currentMoney + bettingListConfirmed[recordBean.bettingArea]?.money!!
                    } else {
                        currentMoney
                    }
                    existRecord.money = countMoney
                    block(balance >= currentCountMoney, existRecord)
                }
                //不存在已下注 注区；直接保存当次下注
                else {
                    bettingListTemp[recordBean.bettingArea] = recordBean
                    block(balance >= currentCountMoney, recordBean)
                }
            }.isEmpty {
                //bettingListTemp 临时下注为空 直接保存当次下注
                bettingListTemp[recordBean.bettingArea] = recordBean
                block(balance >= currentCountMoney, recordBean)
            }
        } else {
            block(false, null)
        }
    }

    /**
     *  取消下注
     *   - 清空临时下注集合
     *   - 返回已确认下注集合
     */
    fun cancelBetting(block: (result: List<BettingRecordBean>?) -> Unit) {
        currentTempCountMoney = 0
        bettingListTemp.clear()
        bettingListConfirmed.isNotEmpty {
            val confirmedList = ArrayList<BettingRecordBean>()
            it.forEach { (_, bettingRecordBean) ->
                confirmedList.add(bettingRecordBean)
            }
            block(confirmedList)
        }.isEmpty {
            block(null)
        }
    }

    fun commitBetting() {
        //等有返回结果后 再赋值成true
        previousSuccess = false
        var index = 0
        val betReq = BetReq.newBuilder()
        betReq.setMiniGameId(1)
        bettingListTemp.forEach { (betting, bettingRecordBean) ->
            val areaBetReq = AreaBetReq.newBuilder().setAreaCode(betting.number)
                .setBetScore(bettingRecordBean.money).build()
            betReq.setAreaBet(index, areaBetReq)
            index++
        }
        val build = betReq.build()
        bet(build)
    }

    override fun loginSuccess(afterLoginSuccess: ClientRes.InfoAfterLoginSuccess) {

    }

    override fun loginError(errorMessage: ClientRes.ErrorMessage) {

    }

    override fun enterInfo(enterInfo: GameRes.EnterInfo) {
    }

    override fun groupInfo(groupInfo: GameRes.GroupInfo) {
    }

    override fun leaveGroup(leave: GameRes.LeaveGroup) {
    }

    override fun leaveMiniGameInfo(miniGame: GameRes.LeaveMiniGames) {
    }

    override fun enterMiniGameInfo(miniGame: GameRes.EnterMiniGameInfo) {
        miniGame.miniGameId
    }

    override fun miniGameBetResult(result: GameRes.MyMiniGameBetResult) {
    }

    override fun refreshUserProperties(userScore: GameRes.RefreshUserScore) {
    }

    override fun beginRound(round: GameRes.BeginNewRound) {
        isCanBetting = true
    }

    override fun beginDeal(round: GameRes.BeginDeal) {
        isCanBetting = false
    }

    override fun beginSettle(settle: GameRes.BeginSettle) {
    }

    override fun syncAreaBetInfoBack(syncAreaBetInfo: GameRes.SyncAreaBetInfo) {
    }

    override fun clearTrendsBackBlock(clearTrends: GameRes.ClearTrends) {
    }

    override fun errorMessage(errorMessage: ClientRes.ErrorMessage) {
    }

    override fun tokenLoseEffectiveness() {
    }
}