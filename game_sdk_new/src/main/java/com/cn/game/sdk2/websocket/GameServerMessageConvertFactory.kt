package com.cn.game.sdk2.websocket

import game.common.proto.ClientRes
import game.common.proto.ClientRes.ErrorMessage
import game.mod.proc.yf.proto.res.GameRes
import game.mod.proc.yf.proto.res.GameRes.BeginSettle
import game.mod.proc.yf.proto.res.GameRes.ClearTrends
import game.mod.proc.yf.proto.res.GameRes.EnterInfo
import game.mod.proc.yf.proto.res.GameRes.RefreshGameConfig
import game.mod.proc.yf.proto.res.GameRes.RefreshUserScore

interface GameServerMessageConvertFactory {
    /**
     * 登陆成功
     */
    fun loginSuccess(afterLoginSuccess: ClientRes.InfoAfterLoginSuccess)

    /**
     * 登陆出错
     */
    fun loginError(errorMessage: ErrorMessage)

    /**
     * 进入房间
     */
    fun enterInfo(enterInfo: EnterInfo)

    /**
     * 进入直播间
     */
    fun groupInfo(groupInfo: GameRes.GroupInfo)

    /**
     * 离开直播间
     */
    fun leaveGroup(leave: GameRes.LeaveGroup)

    /**
     * 离开小游戏
     */
    fun leaveMiniGameInfo(miniGame: GameRes.LeaveMiniGames)

    /**
     * 进入小游戏
     */
    fun enterMiniGameInfo(miniGame: GameRes.EnterMiniGameInfo)

    /**
     * 投注结果返回
     */
    fun miniGameBetResult(result: GameRes.MyMiniGameBetResult)

    /**
     * 刷新金币
     */
    fun refreshUserProperties(userScore: RefreshUserScore)

    /**
     * 开始新的一局
     */
    fun beginRound(round: GameRes.BeginNewRound)

    /**
     * 开始开牌
     */
    fun beginDeal(round: GameRes.BeginDeal)

    /**
     * 进入结算
     */
    fun beginSettle(settle: BeginSettle)

    /**
     * 同步注区下注信息
     */
    fun syncAreaBetInfoBack(syncAreaBetInfo: GameRes.SyncAreaBetInfo)

    /**
     * 清除历史记录
     */
    fun clearTrendsBackBlock(clearTrends: ClearTrends)


    fun errorMessage(errorMessage: ErrorMessage)

    fun tokenLoseEffectiveness()

    fun refreshGameConfig(configs: RefreshGameConfig)

    /**
     * 刷新瓦利游戏人数
     */
    fun refreshGamePlayerCount(parseFrom: GameRes.RefreshWaliGamePlayerCount)
}