package com.cn.game.sdk2.websocket.imp

import com.cn.game.sdk2.network.code.GameReqCode
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.GameServerMessageConvertFactory
import com.cn.game.sdk2.websocket.GameSocketClient
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.balance
import com.cn.game.sdk2.websocket.bean.AreaBetBean
import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import com.cn.game.sdk2.websocket.calculateArea
import com.cn.game.sdk2.websocket.calculateUserLotteryResult
import com.cn.game.sdk2.websocket.convertBetting
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.interfaces.GameService
import com.cn.game.sdk2.websocket.interfaces.SDKCallbackListener
import com.cn.game.sdk2.websocket.isBig
import com.cn.game.sdk2.websocket.isCanBetting
import com.cn.game.sdk2.websocket.isDouble
import com.cn.game.sdk2.websocket.isEmpty
import com.cn.game.sdk2.websocket.isNotEmpty
import com.cn.game.sdk2.websocket.mCallback
import com.cn.game.sdk2.websocket.miniGameId
import com.cn.game.sdk2.websocket.previousSuccess
import com.cn.game.sdk2.websocket.sum
import com.cn.game.sdk2.websocket.viewmodel.GameAboutModel
import com.xcjh.base_lib.utils.loge
import game.common.proto.ClientReq
import game.common.proto.ClientRes
import game.mod.proc.yf.proto.req.GameReq
import game.mod.proc.yf.proto.req.GameReq.EnterMiniGame
import game.mod.proc.yf.proto.res.GameRes

/**
 * 提供ui层调用的统一对象
 */
open class GameServiceImp(private val client: GameSocketClient) : GameService,
    GameServerMessageConvertFactory {

    /**
     * 当前临时总下注金额
     * - 取消下注 清零
     * - 新的一局开始 清零
     */
    protected var currentTempCountMoney = 0

    /**
     * 当前确认总下注金额 已下注部分无法取消
     * - 新的一局开始 清零
     */
    protected var currentConfirmCountMoney = 0

    /**
     * 当前总下注金额 界面需要显示
     */
    protected var currentCountMoney = 0

    /**
     * 临时下注列表
     */
    protected val bettingListTemp: MutableMap<Betting, BettingRecordBean> = mutableMapOf()

    /**
     * 已确认下注列表;
     *  - 仅当前局有效，当前局结算后会被清空
     */
    protected val bettingListConfirmed: MutableMap<Betting, BettingRecordBean> = mutableMapOf()

    /**
     * 续压下注列表;
     *  - 会保存到下一局结算时被下一句数据覆盖
     */
    protected var againBettingList: MutableMap<Betting, BettingRecordBean> = mutableMapOf()

    protected var againCountMoney = 0

    private val tag = GameServiceImp::class.java.name



    override fun enterInfo() {
        send(
            8, GameReqCode.SUB_LOGON_REQ__LOGIN.toShort(), ByteArray(0)
        )
    }

    override fun login(req: ClientReq.LoginReq) {
        val mid: Short = 7
        val sid: Short = 7
//        "请求登录 login".loge()
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
        var bytearray = ByteArray(1)
        bytearray.set(0,1)
        send(0, 2, bytearray)
    }

    private fun send(mid: Short, sid: Short, data: ByteArray) {
        "send()->mid:$mid-sid:$sid".loge(tag)
        try {
            val msg = client.newPack(mid, sid, data, data.size)
            client.send(msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun loginSuccess(afterLoginSuccess: ClientRes.InfoAfterLoginSuccess) {
        "loginSuccess：${afterLoginSuccess.isInitialized}".loge()
        gameAboutModel.setLoginResult(true)
        //初始化step2:登录成功后坐下
        enterInfo()
        mCallback?.callback(1)
    }

    override fun loginError(errorMessage: ClientRes.ErrorMessage) {
        mCallback?.callback(errorMessage.code, errorMessage.desc)
        gameAboutModel.loginErrorMessage = errorMessage.desc
        gameAboutModel.setLoginResult(false)
        "loginError：${errorMessage.desc}".loge(tag)
        when (errorMessage.code) {
            1000 -> {//其他服有正在进行的游戏，应跳转过去
//          desc = 您当前还在其他游戏中，是否立刻回到该游戏？ // 713
            }

            1001 -> {//token验证失败
//          desc = Token验证失败，请您重新登录 // 712
//          desc = 您的帐号不存在或者密码输入有误，请查证后再次尝试登录！(S715) // 账号无效
//          desc = 您的帐号不存在或者密码输入有误，请查证后再次尝试登录！(S705) // 平台参数无效
            }

            1002 -> {//余额不足
//          desc = 当前房间需要%s金币才可进入，请您充值 // 720, 731
//          desc = 金币不够%s元，请先充值 // 732 黑名单渠道入场限制
            }

            1005 -> {//游戏服即将关闭，client 应换一个服
                //desc = 当前服务器正在维护
            }

            200 -> {
                //code = 200   其他情况
                //desc = 服务器已满 (S704)
                //desc = 登录游戏失败，请稍后重试。(S706)

            }
        }
    }

    override fun enterInfo(enterInfo: GameRes.EnterInfo) {
        mCallback?.callback(1)
        gameAboutModel.isSitDown(true)
        balance = enterInfo.self.score.toInt()
        gameAboutModel.changeBalance(enterInfo.self.score.toInt())
        //初始化step3:进入直播间
        "enterInfo Success: enterLive".loge()
        GameSDK.enterLive("1213", listOf(1), "", object : SDKCallbackListener {
            override fun callback(code: Int, message: String?) {

            }
        })
    }

    override fun groupInfo(groupInfo: GameRes.GroupInfo) {
        mCallback?.callback(1)
        gameAboutModel.isEnterGroup(true)

        val miniGameBasicInfo = groupInfo.miniGameBasicInfoListList[0]
        miniGameId = miniGameBasicInfo.miniGameId
        gameAboutModel.miniGameId = miniGameId
        gameAboutModel.countDown = miniGameBasicInfo.countDown
        val roundInfoListList = miniGameBasicInfo.trend.roundInfoListList
        val roundHistoryList = ArrayList<RoundInfoBean>()
        roundInfoListList.forEach {
            val elements = it.performsList[0].performResultList
            roundHistoryList.add(
                RoundInfoBean(
                    it.roundId, elements, elements.sum(), elements.isBig(), elements.isDouble()
                )
            )
        }
        gameAboutModel.addHistoryRounds(roundHistoryList)
        when (miniGameBasicInfo.stage) {
            1 -> gameAboutModel.changeStage(GameAboutModel.Stage.NEW)
            2 -> gameAboutModel.changeStage(GameAboutModel.Stage.DEAL)
            3 -> gameAboutModel.changeStage(GameAboutModel.Stage.SETTLE)
        }
        val miniGame: EnterMiniGame = EnterMiniGame.newBuilder().setMiniGameId(3).build()
        enterGame(miniGame)
    }

    override fun leaveGroup(leave: GameRes.LeaveGroup) {
        gameAboutModel.isLeaveGroup(true)
        mCallback?.callback(1)
    }

    override fun leaveMiniGameInfo(miniGame: GameRes.LeaveMiniGames) {

    }

    override fun enterMiniGameInfo(miniGame: GameRes.EnterMiniGameInfo) {
        miniGameId = miniGame.miniGameId
    }

    override fun miniGameBetResult(result: GameRes.MyMiniGameBetResult) {
        previousSuccess = true
        //result = 0 成功 1 余额不住 3超时
        when (result.betResultInfoListList[0].result) {
            0 -> {
                gameAboutModel.setBettingSuccess(true)
                //下注成功后 保存当前下注总额为已确认下注金额；并将当前下注总额清空
                //currentCountMoney包含之前确认的和现在临时的，所以可以直接覆盖已提交的
                currentConfirmCountMoney = currentCountMoney
                //提交成功后临时总和清空
                currentCountMoney = 0
                previousSuccess = true
                bettingListTemp.forEach { (betting, temBean) ->
                    if (bettingListConfirmed.containsKey(betting)) {
                        temBean.money += bettingListConfirmed[betting]?.money!!
                    }
                    bettingListConfirmed[betting] = temBean
                }
                bettingListTemp.clear()
            }

            1 -> {
                gameAboutModel.bettingMessage = "余额不住"
                gameAboutModel.setBettingSuccess(false)
            }

            2 -> {
                gameAboutModel.bettingMessage = "押注超时"
                gameAboutModel.setBettingSuccess(false)
            }

            else -> {
                gameAboutModel.bettingMessage = "超时"
                gameAboutModel.setBettingSuccess(false)

            }
        }

    }

    override fun refreshUserProperties(userScore: GameRes.RefreshUserScore) {
        balance = userScore.score.toInt()
        gameAboutModel.changeBalance(balance)
    }

    override fun beginRound(round: GameRes.BeginNewRound) {
        isCanBetting = true
        gameAboutModel.miniGameId = round.miniGameId
        gameAboutModel.roundId = round.roundId //期号
        gameAboutModel.countDown = round.countDown //当前阶段剩余时间倒计时
        gameAboutModel.changeStage(GameAboutModel.Stage.NEW)

        againBettingList.isNotEmpty {
            if (againCountMoney <= balance) gameAboutModel.changeCanAgain(true)
            else gameAboutModel.changeCanAgain(false)
        }.isEmpty {
            gameAboutModel.changeCanAgain(false)
        }
        //重置上一局的所有钱
        currentCountMoney = 0
        currentConfirmCountMoney = 0
        currentTempCountMoney = 0

    }

    override fun beginDeal(round: GameRes.BeginDeal) {
        isCanBetting = false
        gameAboutModel.miniGameId = round.miniGameId
        gameAboutModel.roundId = round.roundId //期号
        gameAboutModel.countDown = round.countDown //当前阶段剩余时间倒计时
        gameAboutModel.changeStage(GameAboutModel.Stage.DEAL)
    }

    override fun beginSettle(settle: GameRes.BeginSettle) {
        isCanBetting = false
        gameAboutModel.miniGameId = settle.miniGameId
        gameAboutModel.roundId = settle.roundInfo.roundId //期号
        gameAboutModel.countDown = settle.countDown //当前阶段剩余时间倒计时
        if (settle.winScore > 0) {
            //如果中奖 就计算净收入
            gameAboutModel.netIncome = settle.winScore - (currentConfirmCountMoney * 100)
        }
        //结束时更新余额
        balance = balance + settle.winScore - (currentConfirmCountMoney * 100)
        //开奖号码
        val lotteryNumbers = settle.roundInfo.performsList[0].performResultList
        //中奖注区
        val lotteryResultList = lotteryNumbers.calculateArea()
        //添加历史记录
        gameAboutModel.addHistoryRound(
            RoundInfoBean(
                settle.roundInfo.roundId,
                lotteryNumbers,
                lotteryNumbers.sum(),
                lotteryNumbers.isBig(),
                lotteryNumbers.isDouble()
            )
        )

        gameAboutModel.lotteryResultList = lotteryResultList
        //计算用户中奖注区及金额
        gameAboutModel.userLotteryResult =
            lotteryResultList.calculateUserLotteryResult(bettingListConfirmed)
        //跟新阶段
        gameAboutModel.changeStage(GameAboutModel.Stage.SETTLE)
        //清空本局已下注数据，并复制到续压集合里
        againBettingList = bettingListConfirmed
        againCountMoney = currentConfirmCountMoney
        bettingListConfirmed.clear()
    }

    override fun syncAreaBetInfoBack(syncAreaBetInfo: GameRes.SyncAreaBetInfo) {
        val syncAreaList = ArrayList<AreaBetBean>()
        syncAreaBetInfo.areaBetsList.forEach {
            syncAreaList.add(AreaBetBean(it.areaCode.convertBetting()!!, it.betScore, it.userCount))
        }
        gameAboutModel.changeAreaBetInfo(syncAreaList)
    }

    override fun clearTrendsBackBlock(clearTrends: GameRes.ClearTrends) {
        gameAboutModel.clearTrends(clearTrends.miniGameIdsList)
        gameAboutModel.addHistoryRounds(listOfNotNull())
    }

    override fun errorMessage(errorMessage: ClientRes.ErrorMessage) {
        val desc = errorMessage.desc
        gameAboutModel.setToastErrorMessage(desc)
    }

    /**
     * token失效通知app
     */
    override fun tokenLoseEffectiveness() {
        appListener?.getTokenLoseEffectiveness()
    }

    override fun roomTimeout() {

    }

    override fun serverMaintenance() {

    }

}