package com.cn.game.sdk.common;

/**
 * response code
 */
public interface GameResCode {
    //登录成功
    int SUB_LOGON_RESP__SUCCESS = 106;
    //登录异常
    int SUB_LOGON_RESP__LOGIN_ERROR = 107;
    /**
     * 進入房間, 取得房間資訊 -> Res.EnterInfo
     */
    int S2C_ENTER_INFO = 1000;
    /**
     * 進入直播间, 取得直播间資訊 -> Res.GroupInfo
     */
    int S2C_GROUP_INFO = 1001;
    /**
     * 進入小遊戲 -> Res.EnterMiniGameInfo
     */
    int S2C_ENTER_MINI_GAME_INFO = 1003;
    /**
     * 開始新局 -> Res.BeginNewRound
     */
    int S2C_BEGIN_ROUND = 1004;
    /**
     * 進入結算 -> Res.BeginSettle
     */
    int S2C_BEGIN_SETTLE = 1005;
    /**
     * 玩家自己下注結果 -> Res.MyMiniGameBetResult
     */
    int S2C_USER_BET_RESULT = 1006;

    /**
     * 同步注區下注信息 -> Res.SyncAreaBetInfo
     */
    int S2C_SYNC_AREA_BET_INFO = 1007;

    /**
     * 通知離開group -> Res.LeaveGroup
     */
    int S2C_LEAVE_GROUP = 1008;

    /**
     * 通知離開小遊戲 -> Res.LeaveMiniGames
     */
    int S2C_LEAVE_MINI_GAME = 1009;

    /**
     * 刷新屬性 -> Res.RefreshUserProperties
     */
    int S2C_REFRESH_USER_PROPS = 1050;


}
