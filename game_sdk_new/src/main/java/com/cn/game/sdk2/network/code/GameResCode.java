package com.cn.game.sdk2.network.code;

/**
 * response code
 */
public interface GameResCode {
    //登录成功 1
    int SUB_LOGON_RESP__SUCCESS = 106;
    //登录异常 2
    int SUB_LOGON_RESP__LOGIN_ERROR = 107;
    /**
     * 進入, 取得基本資訊 -> Res.EnterInfo 3
     */
    int S2C_ENTER_INFO = 1000;
    /**
     * 進入直播间, 取得直播间資訊 -> Res.GroupInfo 4
     */
    int S2C_GROUP_INFO = 1001;
    /**
     * 進入小遊戲 -> Res.EnterMiniGameInfo 5
     */
    int S2C_ENTER_MINI_GAME_INFO = 1003;
    /**
     * 開始新局 -> Res.BeginNewRound 6
     */
    int S2C_BEGIN_ROUND = 1004;

    /**
     * 开始开牌 7
     */
    int S2C_BEGIN_DEAL = 1010;

    /**
     * 進入結算 -> Res.BeginSettle 8
     */
    int S2C_BEGIN_SETTLE = 1005;
    /**
     * 玩家自己下注結果 -> Res.MyMiniGameBetResult 9
     */
    int S2C_USER_BET_RESULT = 1006;

    /**
     * 同步注區下注信息 -> Res.SyncAreaBetInfo 10
     */
    int S2C_SYNC_AREA_BET_INFO = 1007;

    /**
     * 通知離開group -> Res.LeaveGroup 11
     */
    int S2C_LEAVE_GROUP = 1008;

    /**
     * 通知離開小遊戲 -> Res.LeaveMiniGames 12
     */
    int S2C_LEAVE_MINI_GAME = 1009;

    /**
     * 刷新金錢 -> Res.RefreshUserScore 13
     */
    int S2C_REFRESH_USER_SCORE = 1050;

    /**
     * 除登录外其他操作 14
     */
    int S2C_OTHER_ERROR = 600;

    /**
     * 清除投注历史 15
     */
    int S2C_CLEAR_TRENDS = 1011;

    /**
     * 700-703处理流程统一，token失效，退出登录
     */

    /**
     * 服务器维护返回 17
     */
    int S2C_SERVER_MAINTENANCE = 700;

    /**
     * 房间超时踢出 18
     */
    int S2C_ROOM_TIMEOUT = 701;

    /**
     * 多用户登录 16
     */
    int S2C_MULTI_USER_LOGIN = 702;

    /**
     * token，account验证不通过
     */
    int S2C_MULTI_TOKEN_VERIFY_FAIL = 703;

    int S2C_REFRESH_GAME_CONFIG = 1012;
}
