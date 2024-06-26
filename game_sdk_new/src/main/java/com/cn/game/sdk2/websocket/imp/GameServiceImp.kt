package com.cn.game.sdk2.websocket.imp

import com.cn.game.sdk2.network.code.GameReqCode
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.GameServerMessageConvertFactory
import com.cn.game.sdk2.websocket.GameSocketClient
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.balance
import com.cn.game.sdk2.websocket.bean.AreaBetBean
import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import com.cn.game.sdk2.websocket.calculateArea
import com.cn.game.sdk2.websocket.calculateUserLotteryResult
import com.cn.game.sdk2.websocket.convertBetting
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.interfaces.GameService
import com.cn.game.sdk2.websocket.interfaces.SDKEnterLiveCallbackListener
import com.cn.game.sdk2.websocket.isBig
import com.cn.game.sdk2.websocket.isCanBetting
import com.cn.game.sdk2.websocket.isDouble
import com.cn.game.sdk2.websocket.isEmpty
import com.cn.game.sdk2.websocket.isEnterRoom
import com.cn.game.sdk2.websocket.isLogin
import com.cn.game.sdk2.websocket.isNotEmpty
import com.cn.game.sdk2.websocket.mEnterLiveCallback
import com.cn.game.sdk2.websocket.mLeaveLiveCallback
import com.cn.game.sdk2.websocket.mLoginCallback
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 提供ui层调用的统一对象
 */
open class GameServiceImp(private val client: GameSocketClient) : GameService,
    GameServerMessageConvertFactory {

    /**
     * 临时最后点击
     */
    private var tempLastBetting: Betting? = null

    /**
     * 加倍时需要的
     */
    protected var doubleMoney: Int = 0

    /**
     * 当前临时总下注金额
     * - 取消下注 清零
     * - 提交 清零
     * - 新的一局开始 清零
     */
    protected var tempMoney = 0

    /**
     * 当前确认总下注金额 已下注部分无法取消
     * - 新的一局开始 清零
     */
    protected var confirmMoney = 0

    /**
     * 临时保存提交的钱
     * 提交时   赋值
     * 提交成功 清零 并加入 currentConfirmCountMoney
     * 提交失败 清零 并加入 currentTempCountMoney
     */
    protected var confirmTempMoney = 0

    /**
     * 临时下注列表
     */
    protected val bettingListTemp: MutableMap<Betting, BettingRecordBean> = mutableMapOf()

    /**
     * 临时确认下注列表
     */
    protected var bettingListTempConfirmed: MutableMap<Betting, BettingRecordBean> = mutableMapOf()

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

    override fun enterGame(req: EnterMiniGame) {
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
        send(0, 2, ByteArray(0))
    }

    private fun send(mid: Short, sid: Short, data: ByteArray) {
        //messageViewModel?.setSendData(SendDataBean(mid, sid, data))
        client.handler.post{
            "send()->mid:$mid-sid:$sid".loge(tag)
            try {
                val msg = client.newPack(mid, sid, data, data.size)
                client.send(msg)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun loginSuccess(afterLoginSuccess: ClientRes.InfoAfterLoginSuccess) {
        isLogin = true
        "loginSuccess：${afterLoginSuccess}".loge()
        gameAboutModel.setLoginResult(true)
        //初始化step2:登录成功后坐下
        enterInfo()
        mLoginCallback?.callback(1)
    }

    override fun loginError(errorMessage: ClientRes.ErrorMessage) {
        isLogin = false
        mLoginCallback?.callback(errorMessage.code, errorMessage.desc)
        gameAboutModel.loginErrorMessage = errorMessage.desc
        gameAboutModel.setLoginResult(false)
        "loginError：$errorMessage".loge(tag)
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
    //进入房间坐下成功，待进入直播间
    override fun enterInfo(enterInfo: GameRes.EnterInfo) {
        enterInfo.toString().loge("enterInfo")
        gameAboutModel.isSitDown(true)
        balance = enterInfo.self.score.toInt()
        gameAboutModel.changeBalance(enterInfo.self.score.toInt())
        if(isEnterRoom){
            GameSDK.enterLive("1213", listOf(1), "", object : SDKEnterLiveCallbackListener {
                override fun callback(code: Int, message: String?) {
                    "enterLive:code-$code,message$message".loge()
                }
            })
        }
    }

    //进入直播间成功，待进入游戏
    override fun groupInfo(groupInfo: GameRes.GroupInfo) {
        isEnterRoom = true
        groupInfo.toString().loge("groupInfo")
        mEnterLiveCallback?.callback(1)
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
            2 -> {
                gameAboutModel.changeStage(GameAboutModel.Stage.DEAL)
            }

            3 -> gameAboutModel.changeStage(GameAboutModel.Stage.SETTLE)
        }
        val miniGame: EnterMiniGame = EnterMiniGame.newBuilder().setMiniGameId(3).build()
        enterGame(miniGame)
    }

    //离开直播间成功
    override fun leaveGroup(leave: GameRes.LeaveGroup) {
        isEnterRoom = false
        gameAboutModel.isLeaveGroup(true)
        mLeaveLiveCallback?.callback(1)
    }

    override fun leaveMiniGameInfo(miniGame: GameRes.LeaveMiniGames) {
        miniGame.toString().loge("leaveMiniGameInfo")
    }

    override fun enterMiniGameInfo(miniGame: GameRes.EnterMiniGameInfo) {
        miniGameId = miniGame.miniGameId
        miniGame.toString().loge("enterMiniGameInfo")
    }

    /**
     * 下注返回失败
     *  将临时确认下注的重新加临时集合里 便于取消和二次确认
     */
    private fun returnTemp() {
        bettingListTemp.isNotEmpty { temp ->
            bettingListTempConfirmed.forEach {
                if (temp.containsKey(it.key)) {
                    temp[it.key]!!.money += it.value.money
                    bettingListTemp[it.key] = temp[it.key]!!
                } else {
                    bettingListTemp[it.key] = it.value
                }
            }
        }.isEmpty {
            bettingListTemp.putAll(bettingListTempConfirmed)
        }
        tempMoney += confirmTempMoney
        bettingListTempConfirmed.clear()
    }

    override fun miniGameBetResult(result: GameRes.MyMiniGameBetResult) {
        previousSuccess = true
        //result = 0 成功 1 余额不住 3超时
        result.toString().loge("miniGameBetResult")
        when (result.betResultInfoListList[0].result) {
            0 -> {
                gameAboutModel.lastBetting = tempLastBetting
                gameAboutModel.setBettingSuccess(true)
                //下注成功后 保存当前下注总额为已确认下注金额；并将当前下注总额清空
                //currentCountMoney包含之前确认的和现在临时的，所以可以直接覆盖已提交的
                confirmMoney += confirmTempMoney

                //跟新again和double
                gameAboutModel.setOnceCountMoney(confirmMoney)
                previousSuccess = true
                bettingListTempConfirmed.forEach { (betting, temBean) ->
                    if (bettingListConfirmed.containsKey(betting)) {
                        temBean.money += bettingListConfirmed[betting]?.money!!
                    }
                    bettingListConfirmed[betting] = temBean
                }
                bettingListTempConfirmed.clear()
                confirmTempMoney = 0
            }

            1 -> {
                returnTemp()
                gameAboutModel.bettingMessage = "余额不住"
                gameAboutModel.setBettingSuccess(false)
            }

            2 -> {
                returnTemp()
                gameAboutModel.bettingMessage = "押注超时"
                gameAboutModel.setBettingSuccess(false)
            }

            else -> {
                returnTemp()
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
            //新的一局开始，并且上一局有数据，并且余额足够
            if (againCountMoney <= balance) {
                gameAboutModel.changeMeetAgain(true)
            } else {
                //新的一局开始，并且上一局有数据，但是余额不足
                gameAboutModel.changeMeetAgain(false)
            }
        }.isEmpty {
            gameAboutModel.changeMeetAgain(false)
        }
        resetPanel()
    }

    /**
     * 新的一局开始
     */
    private fun resetPanel() {
        //重置上一局的所有钱
        confirmMoney = 0
        tempMoney = 0
        confirmTempMoney = 0
        bettingListTempConfirmed.clear()
        bettingListConfirmed.clear()
        bettingListTemp.clear()
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
            gameAboutModel.netIncome = settle.winScore - confirmMoney
        }
        //结束时更新余额
        balance = balance + settle.winScore - confirmMoney
        //开奖号码
        val lotteryNumbers = settle.roundInfo.performsList[0].elementsList
        //中奖注区
        val lotteryResultList = lotteryNumbers.calculateArea()
        //添加历史记录
        val currentRound = RoundInfoBean(
            settle.roundInfo.roundId,
            lotteryNumbers,
            lotteryNumbers.sum(),
            lotteryNumbers.isBig(),
            lotteryNumbers.isDouble()
        )
        gameAboutModel.addHistoryRound(currentRound)
        gameAboutModel.currentSettleResult = currentRound

        gameAboutModel.lotteryResultList = lotteryResultList
        //计算用户中奖注区及金额
        gameAboutModel.userLotteryResult =
            lotteryResultList.calculateUserLotteryResult(bettingListConfirmed)
        //跟新阶段
        gameAboutModel.changeStage(GameAboutModel.Stage.SETTLE)
        //清空本局已下注数据，并复制到续压集合里
        bettingListConfirmed.isNotEmpty {
            againBettingList = bettingListConfirmed
        }
        if (confirmMoney > 0) {
            againCountMoney = confirmMoney
        }
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

    protected var curStage: GameAboutModel.Stage = GameAboutModel.Stage.NEW
    protected var onceCountMoney = 0
    protected var isMeetAgain = true

    protected fun checkAgain() {
        if (curStage == GameAboutModel.Stage.NEW) {
            if (isMeetAgain) {
                //满足基本需要要求
                if (onceCountMoney == 0) {
                    //牌面上没有下注才能 需要
                    gameAboutModel.changeAgainDoubleState(GameAboutModel.AgainDoubleState.AGAIN)
                } else {
                    //牌面上已有下注
                    checkDouble()
                }
            } else {
                if (onceCountMoney > 0) {
                    checkDouble()
                } else {
                    //不满足续压 牌面为空
                    gameAboutModel.changeAgainDoubleState(GameAboutModel.AgainDoubleState.NUll)
                }
            }
        } else {
            gameAboutModel.changeAgainDoubleState(GameAboutModel.AgainDoubleState.NUll)
        }
    }

    private fun checkDouble() {
        //不满足续压 计算加倍
        doubleMoney = tempMoney * 2 + confirmMoney +confirmTempMoney
        if (doubleMoney < balance) gameAboutModel.changeAgainDoubleState(GameAboutModel.AgainDoubleState.DOUBLE)
        else
        //既不满足续压 钱也不够加倍
            gameAboutModel.changeAgainDoubleState(GameAboutModel.AgainDoubleState.NUll)
    }

    protected fun getPanelAllMoney(): Int {
        return tempMoney + confirmTempMoney + confirmMoney
    }

    protected fun isMoneyEnough(): Boolean {
        return tempMoney + confirmTempMoney <= balance
    }

}