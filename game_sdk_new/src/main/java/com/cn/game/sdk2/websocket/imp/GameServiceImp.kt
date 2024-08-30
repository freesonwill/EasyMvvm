package com.cn.game.sdk2.websocket.imp

import com.cn.game.sdk2.network.code.GameReqCode
import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.utils.ThreadUtils
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.GameServerMessageConvertFactory
import com.cn.game.sdk2.websocket.GameSocketClient
import com.cn.game.sdk2.websocket.againIfMoneyEnough
import com.cn.game.sdk2.websocket.appContext
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.bean.AreaBetBean
import com.cn.game.sdk2.websocket.bean.AreaBetConfigBean
import com.cn.game.sdk2.websocket.bean.BettingResponsesBean
import com.cn.game.sdk2.websocket.bean.BettingStatus
import com.cn.game.sdk2.websocket.bean.ObservableArrayList
import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import com.cn.game.sdk2.websocket.calculateArea
import com.cn.game.sdk2.websocket.calculateUserLotteryResult
import com.cn.game.sdk2.websocket.convertAgainList
import com.cn.game.sdk2.websocket.convertBetting
import com.cn.game.sdk2.websocket.copyFrom
import com.cn.game.sdk2.websocket.doubleIfMoneyEnough
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.getMoneyByState
import com.cn.game.sdk2.websocket.interfaces.GameService
import com.cn.game.sdk2.websocket.isBig
import com.cn.game.sdk2.websocket.isTokenValid
import com.cn.game.sdk2.websocket.isDouble
import com.cn.game.sdk2.websocket.isEmpty
import com.cn.game.sdk2.websocket.isEnterRoom
import com.cn.game.sdk2.websocket.isLogin
import com.cn.game.sdk2.websocket.isNotEmpty
import com.cn.game.sdk2.websocket.nativeLib
import com.cn.game.sdk2.websocket.returnTemp
import com.cn.game.sdk2.websocket.runOnUiThread
import com.cn.game.sdk2.websocket.setCommittedState
import com.cn.game.sdk2.websocket.sum
import com.cn.game.sdk2.websocket.toMapByAreaCode
import com.cn.game.sdk2.websocket.verifyDouble
import com.cn.game.sdk2.websocket.viewmodel.GameAboutModel
import com.xcjh.base_lib2.utils.LogUtilsExt.logd
import com.xcjh.base_lib2.utils.LogUtilsExt.loge
import game.common.proto.ClientReq
import game.common.proto.ClientRes
import game.mod.proc.yf.proto.req.GameReq
import game.mod.proc.yf.proto.req.GameReq.EnterMiniGame
import game.mod.proc.yf.proto.res.GameRes
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * 提供ui层调用的统一对象
 */
internal abstract class GameServiceImp(private val client: GameSocketClient) : GameService,
    GameServerMessageConvertFactory {

    protected open var bettingStepList: ObservableArrayList<BettingRecordBean> =
        ObservableArrayList()

    //注区限额配置
    protected open var configMap: MutableMap<Int, List<AreaBetConfigBean>> = mutableMapOf()

    //当前局的限额配置 新的一局开始才给它赋值 保证当前局的配置不变
    protected open var currentConfig: List<AreaBetConfigBean>? = null

    //游戏id
    protected open var miniGameId: Int = 0

    //上一次的下注结果
    protected open var previousSuccess: Boolean = true

    //保存当前阶段 本地计算checkAgain
    private var curStage: GameAboutModel.Stage = GameAboutModel.Stage.NEW

    // 临时最后点击
    protected open var tempLastBetting: Betting? = null


    // 续压下注列表;
    // 会保存到下一局结算时被下一句数据覆盖
    protected open var againBettingMap: MutableMap<Betting, BettingRecordBean> = ConcurrentHashMap()

    private val tag = GameServiceImp::class.java.name


    override fun enterInfo() {
        send(
            8, GameReqCode.SUB_LOGON_REQ__LOGIN.toShort(), ByteArray(0)
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
        "send()->mid:$mid-sid:$sid".logd(tag)
        try {
            val msg = nativeLib.newPack(mid, sid, data, data.size)
            client.send(msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun loginSuccess(afterLoginSuccess: ClientRes.InfoAfterLoginSuccess) {
        isLogin = true
        "loginSuccess：${afterLoginSuccess}".logd(tag)
        gameAboutModel.setLoginResult(true)
        refreshScore()
        enterInfo()
        appListener?.runOnUiThread {
            onLoginGame(1, "")
        }
    }

    override fun loginError(errorMessage: ClientRes.ErrorMessage) {
        isLogin = false
        appListener?.runOnUiThread {
            onLoginGame(errorMessage.code, errorMessage.desc)
        }
        gameAboutModel.loginErrorMessage = errorMessage.desc
        gameAboutModel.setLoginResult(false)

        "loginError-->${errorMessage}".loge(tag)/*  when (errorMessage.code) {
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
          }*/
    }

    //进入房间坐下成功，待进入直播间
    override fun enterInfo(enterInfo: GameRes.EnterInfo) {
        "进入房间坐下成功".logd(tag)
        gameAboutModel.isSitDown(true)
        val balance = enterInfo.self.score
        gameAboutModel.changeBalance(balance)
        gameAboutModel.changeTempBalance(balance)
        val betAreaConfigs = enterInfo.gameConfigsList
        betAreaConfigs.forEach {
            val areaBetConfigBeans = ArrayList<AreaBetConfigBean>()
            it.betAreaConfigsList.forEach { bean ->
                areaBetConfigBeans.add(
                    AreaBetConfigBean(
                        bean.areaCode.convertBetting()!!, bean.minLimit, bean.maxLimit
                    )
                )
            }
            configMap[it.miniGameId] = areaBetConfigBeans
        }


        //重连时 直接进入直播间
        if (isEnterRoom) {
            "重连,直接进入直播间,liveId:${gameAboutModel.liveId}".loge(tag)
            GameApp.enterLive(gameAboutModel.liveId, gameAboutModel.gameIds, gameAboutModel.data)
        }

    }

    //进入直播间成功，待进入游戏
    override fun groupInfo(groupInfo: GameRes.GroupInfo) {
        isEnterRoom = true
        "进入直播间成功:$groupInfo".logd(tag)
        appListener?.runOnUiThread {
            onEnterLive(1, "")
            /*测试游戏大厅在线人数代码
            ThreadUtils.mainScope.launch {
                while (true){
                    delay(1000)
                    gameAboutModel.setMoreGameOnlines(listOf(Random.nextInt(10000)))
                }
            }*/
        }
        gameAboutModel.isEnterGroup(true)

        gameAboutModel.gameList = groupInfo.miniGameBasicInfoListList
        val miniGameBasicInfo = gameAboutModel.gameList?.get(0)
        miniGameId = miniGameBasicInfo?.miniGameId!!
        currentConfig = configMap[miniGameId]
        gameAboutModel.countDown = miniGameBasicInfo.countDown
        val roundInfoListList = miniGameBasicInfo.trend.roundInfoListList
        val roundHistoryList = ArrayList<RoundInfoBean>()
        roundInfoListList.forEach {
            val elements = it.performsList[0].elementsList
            roundHistoryList.add(
                RoundInfoBean(
                    it.roundId, elements, elements.sum(), elements.isBig(), elements.isDouble()
                )
            )
        }
        roundInfoListList?.let {
            if (it.isNotEmpty()){
                val elements = it.last().performsList[0].elementsList
                val roundInfoBean = RoundInfoBean(
                    it.last().roundId, elements, elements.sum(), elements.isBig(), elements.isDouble()
                )
                gameAboutModel.currentSettleResult = roundInfoBean
            }else{
                setCurrentHistory(miniGameBasicInfo.lastRoundInfo)
            }
        } ?: run {
            setCurrentHistory(miniGameBasicInfo.lastRoundInfo)
        }

        gameAboutModel.addHistoryRounds(roundHistoryList)
        when (miniGameBasicInfo.stage) {
            1 -> {
                gameAboutModel.changeStage(GameAboutModel.Stage.NEW)
            }

            2 -> {
                gameAboutModel.changeStage(GameAboutModel.Stage.DEAL)
            }

            3 -> {
                gameAboutModel.changeStage(GameAboutModel.Stage.SETTLE)
            }
        }

        enterGame(
            EnterMiniGame.newBuilder().setMiniGameId(miniGameId).build()
        )
    }

    private fun setCurrentHistory(roundInfo:GameRes.RoundInfo){
        val elements = roundInfo.performsList[0].elementsList
        val roundInfoBean = RoundInfoBean(
            roundInfo.roundId,
            elements,
            elements.sum(),
            elements.isBig(),
            elements.isDouble()
        )
        gameAboutModel.currentSettleResult = roundInfoBean
    }

    //离开直播间成功
    override fun leaveGroup(leave: GameRes.LeaveGroup) {
        isEnterRoom = false
        gameAboutModel.liveId = ""
        gameAboutModel.isLeaveGroup(true)
        appListener?.runOnUiThread {
            onLeaveLive(gameAboutModel.liveId, 1,"")
        }
        "离开直播间：$leave".logd(tag)
    }

    override fun leaveMiniGameInfo(miniGame: GameRes.LeaveMiniGames) {
        "离开游戏：$miniGame".logd(tag)
    }

    override fun enterMiniGameInfo(miniGame: GameRes.EnterMiniGameInfo) {
        miniGameId = miniGame.miniGameId
        gameAboutModel.roundId = miniGame.roundId
        "进入游戏 ->${miniGame}".logd(tag)
        gameAboutModel.isEnterGameSuccess = true
        checkAgainNew()
        appListener?.runOnUiThread {
            onEnterGame()
        }
    }

    override fun miniGameBetResult(result: GameRes.MyMiniGameBetResult) {
        previousSuccess = true
        "服务器下注结果：$result".logd()
        //result = 0 成功 1 余额不住 3超时
        when (result.betResultInfoListList[0].result) {
            0 -> {
                gameAboutModel.lastBetting = tempLastBetting
                gameAboutModel.setBettingSuccess(
                    BettingResponsesBean(
                        true, bettingStepList.getMoneyByState(BettingStatus.COMMITTING)
                    )
                )
                bettingStepList.setCommittedState()
                gameAboutModel.tempMap.clear()
                gameAboutModel.tempMap.putAll(bettingStepList.toMapByAreaCode())
                gameAboutModel.previousRoundId = gameAboutModel.roundId
            }

            1 -> {
                bettingStepList.returnTemp()
                gameAboutModel.bettingMessage = "余额不住"
                gameAboutModel.setToastErrorMessage(gameAboutModel.bettingMessage)
                gameAboutModel.setBettingSuccess(BettingResponsesBean(false, 0))
            }

            2 -> {
                bettingStepList.returnTemp()
                gameAboutModel.bettingMessage = "押注超时"
                gameAboutModel.setToastErrorMessage(gameAboutModel.bettingMessage)
                gameAboutModel.setBettingSuccess(BettingResponsesBean(false, 0))
            }

            4 -> {
                bettingStepList.returnTemp()
                isTokenValid = false
                gameAboutModel.bettingMessage = "网络连接超时"
                gameAboutModel.setToastErrorMessage(gameAboutModel.bettingMessage)
                appListener?.runOnUiThread {
                    onTokenLoseEffectiveness()
                }
            }

            5 -> {
                bettingStepList.returnTemp()
                isTokenValid = false
                gameAboutModel.bettingMessage = "账号在其他设备登录，您已下线"
                gameAboutModel.setToastErrorMessage(gameAboutModel.bettingMessage)
                appListener?.runOnUiThread {
                    onTokenLoseEffectiveness()
                }
            }

            else -> {
                bettingStepList.returnTemp()
                gameAboutModel.bettingMessage = "网络连接超时"
                gameAboutModel.setToastErrorMessage(gameAboutModel.bettingMessage)
                gameAboutModel.setBettingSuccess(BettingResponsesBean(false, 0))
            }
        }
    }

    override fun refreshUserProperties(userScore: GameRes.RefreshUserScore) {
        "刷新余额:$userScore".logd(tag)
        val balance = userScore.score
        gameAboutModel.changeBalance(balance)
        gameAboutModel.changeTempBalance(balance)
    }

    override fun beginRound(round: GameRes.BeginNewRound) {
        "Round".logd(tag)
        previousSuccess = true
        gameAboutModel.tempMap.clear()
        gameAboutModel.roundId = round.roundId //期号
        "Round roundId->${gameAboutModel.roundId}".loge("roundId")
        gameAboutModel.countDown = round.countDown //当前阶段剩余时间倒计时
        gameAboutModel.changeStage(GameAboutModel.Stage.NEW)
        curStage = GameAboutModel.Stage.NEW
        checkAgainNew()
        resetPanel()
        currentConfig = configMap[miniGameId]
    }

    /**
     * 新的一局开始
     */
    private fun resetPanel() {
        //重置上一局的所有钱
        gameAboutModel.netIncome = 0
        bettingStepList.clear()
    }

    override fun beginDeal(round: GameRes.BeginDeal) {
        "Deal".logd(tag)
        curStage = GameAboutModel.Stage.DEAL
        gameAboutModel.roundId = round.roundId //期号
        gameAboutModel.countDown = round.countDown //当前阶段剩余时间倒计时
        gameAboutModel.changeStage(GameAboutModel.Stage.DEAL)
        gameAboutModel.changeTempBalance(gameAboutModel.balance.value ?: 0)
    }

    override fun beginSettle(settle: GameRes.BeginSettle) {
        "Settle".logd(tag)
        curStage = GameAboutModel.Stage.SETTLE
        gameAboutModel.roundId = settle.roundInfo.roundId //期号
        gameAboutModel.countDown = settle.countDown //当前阶段剩余时间倒计时
        val confirmMoney = bettingStepList.getMoneyByState(BettingStatus.COMMITTED)

        if (settle.winScore > 0) {
            //如果中奖 就计算净收入
            gameAboutModel.netIncome = settle.winScore - confirmMoney
            ViewHelper.showFastViewPopWhenWin()
        }
        //开奖号码
        //主动设置豹子
        val lotteryNumbers = if (gameAboutModel.manualLeopard) {
            gameAboutModel.manualLeopard = false
            listOf(6, 6, 6)
        } else {
            settle.roundInfo.performsList[0].elementsList
        }

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

        gameAboutModel.lotteryResultList = lotteryResultList
        //计算用户中奖注区及金额
        gameAboutModel.userLotteryResult =
            lotteryResultList.calculateUserLotteryResult(bettingStepList)
        //跟新阶段
        gameAboutModel.changeStage(GameAboutModel.Stage.SETTLE)
        //清空本局已下注数据，并复制到续压集合里
        if (bettingStepList.isNotEmpty()) {
            againBettingMap.clear()
            againBettingMap.putAll(bettingStepList.convertAgainList())
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
        "服务器返回的error:code = ${errorMessage.code},msg = ${errorMessage.desc}".loge(tag)
        gameAboutModel.setToastErrorMessage(desc)
    }

    override fun refreshGameConfig(configs: GameRes.RefreshGameConfig) {
        val newConfigMap: MutableMap<Int, List<AreaBetConfigBean>> = mutableMapOf()
        configs.gameConfigsList?.forEach {
            val areaBetConfigBeans = ArrayList<AreaBetConfigBean>()
            it.betAreaConfigsList.forEach { bean ->
                areaBetConfigBeans.add(
                    AreaBetConfigBean(
                        bean.areaCode.convertBetting()!!, bean.minLimit, bean.maxLimit
                    )
                )
            }
            newConfigMap[it.miniGameId] = areaBetConfigBeans
        }
        configMap.clear()
        configMap copyFrom newConfigMap
    }

    /**
     * token失效通知app
     */
    override fun tokenLoseEffectiveness() {
        "登录失效，请重新登录".logd(tag)
        isTokenValid = false
        gameAboutModel.setToastErrorMessage("登录失效，请重新登录")
        appListener?.runOnUiThread {
            onTokenLoseEffectiveness()
        }
    }

    protected fun checkAgainNew() {
        if (curStage == GameAboutModel.Stage.NEW) {
            againBettingMap.isNotEmpty {
                if (bettingStepList.isEmpty()) {
                    if (it.againIfMoneyEnough()) {
                        if (gameAboutModel.balance.value!! < 5000) {
                            "BALANCE < 50".logd("checkAgainAndDouble")
                            gameAboutModel.changeAgainDoubleState(GameAboutModel.AgainDoubleState.AGAIN_CAN_NOT_50)
                        } else {
                            "again不为空 并且余额足够 并且牌面上也为空 -> 返回AGAIN".logd("checkAgainAndDouble")
                            gameAboutModel.changeAgainDoubleState(GameAboutModel.AgainDoubleState.AGAIN)
                        }
                    } else {
                        "again不为空 牌面上为空 并且余额不足够 -> 进入checkDouble".logd("checkAgainAndDouble")
                        gameAboutModel.changeAgainDoubleState(GameAboutModel.AgainDoubleState.NUll)
                    }
                } else {
                    "again不为空,但牌面上不为空".logd("checkAgainAndDouble")
                    checkDoubleNew()
                }
            }.isEmpty {
                if (bettingStepList.isEmpty()) {
                    "again为空,并且牌面上也为空".logd("checkAgainAndDouble")
                    gameAboutModel.changeAgainDoubleState(GameAboutModel.AgainDoubleState.NUll)
                } else {
                    "again为空,但牌面上不为空 -> 进入checkDouble".logd("checkAgainAndDouble")
                    checkDoubleNew()
                }
            }
        } else {
            "checkAgain()->不是新阶段->NUll".logd("checkAgainAndDouble")
            gameAboutModel.changeAgainDoubleState(GameAboutModel.AgainDoubleState.NUll)
        }
    }

    private fun checkDoubleNew() {
        if (bettingStepList.doubleIfMoneyEnough()) {
            bettingStepList.verifyDouble(currentConfig)?.let {
                if (it.noMoney) {
                    "double 钱不够".logd("checkAgainAndDouble")
                    gameAboutModel.changeAgainDoubleState(GameAboutModel.AgainDoubleState.NUll)
                } else {
                    "checkDouble()->${it.limitBean?.areaCode}号注区超限->DOUBLE_CAN_NOT".logd("checkAgainAndDouble")
                    gameAboutModel.changeAgainDoubleState(GameAboutModel.AgainDoubleState.DOUBLE_CAN_NOT)
                }
            } ?: run {
                "满足double".logd("checkAgainAndDouble")
                gameAboutModel.changeAgainDoubleState(GameAboutModel.AgainDoubleState.DOUBLE)
            }
        } else {
            "既不满足续压 钱也不够加倍".logd("GameServiceImpl")
            gameAboutModel.changeAgainDoubleState(GameAboutModel.AgainDoubleState.DOUBLE_CAN_NOT)
        }
    }

    override fun refreshGamePlayerCount(parseFrom: GameRes.RefreshWaliGamePlayerCount) {
        ThreadUtils.mainScope.launch {
            gameAboutModel.setMoreGameOnlines(parseFrom.playerCountsList)
        }
    }
}