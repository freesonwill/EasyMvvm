package com.cn.game.sdk2.network.code


enum class GameResCode(val code:Int) {
    /**
     * 未知命令 0
     */
    CMD_ID_UNKNOWN(-1),
    /**
     * 登录成功 1
     */
    SUB_LOGON_RESP__SUCCESS(106),
    /**
     * 登录异常 2
     */
    SUB_LOGON_RESP__LOGIN_ERROR(107),
    /**
     * 進入, 取得基本資訊 -> Res.EnterInfo 3
     */
    S2C_ENTER_INFO(1000),
    /**
     * 進入直播间, 取得直播间資訊 -> Res.GroupInfo 4
     */
    S2C_GROUP_INFO(1001),
    /**
     * 進入小遊戲 -> Res.EnterMiniGameInfo 5
     */
    S2C_ENTER_MINI_GAME_INFO(1003),
    /**
     * 開始新局 -> Res.BeginNewRound 6
     */
    S2C_BEGIN_ROUND(1004),

    /**
     * 开始开牌 7
     */
    S2C_BEGIN_DEAL(1010),

    /**
     * 進入結算 -> Res.BeginSettle 8
     */
    S2C_BEGIN_SETTLE(1005),
    /**
     * 玩家自己下注結果 -> Res.MyMiniGameBetResult 9
     */
    S2C_USER_BET_RESULT(1006),

    /**
     * 同步注區下注信息 -> Res.SyncAreaBetInfo 10
     */
    S2C_SYNC_AREA_BET_INFO(1007),

    /**
     * 通知離開group -> Res.LeaveGroup 11
     */
    S2C_LEAVE_GROUP(1008),

    /**
     * 通知離開小遊戲 -> Res.LeaveMiniGames 12
     */
    S2C_LEAVE_MINI_GAME(1009),

    /**
     * 刷新金錢 -> Res.RefreshUserScore 13
     */
    S2C_REFRESH_USER_SCORE(1050),

    /**
     * 除登录外其他操作 14
     */
    S2C_OTHER_ERROR(600),

    /**
     * 清除投注历史 15
     */
    S2C_CLEAR_TRENDS(1011),

    /**
     * 700-703处理流程统一，token失效，退出登录
     */

    /**
     * 服务器维护返回 17
     */
    S2C_SERVER_MAINTENANCE(700),

    /**
     * 房间超时踢出 18
     */
    S2C_ROOM_TIMEOUT(701),

    /**
     * 多用户登录 16
     */
    S2C_MULTI_USER_LOGIN(702),

    /**
     * token，account验证不通过
     */
    S2C_MULTI_TOKEN_VERIFY_FAIL(703),

    S2C_REFRESH_GAME_CONFIG(1012),

    /**
     * 刷新瓦力遊戲配置 -> Res.RefreshWaliGameConfig
     */
    S2C_REFRESH_WALI_GAME_CONFIG(1051),

    /**
     * 刷新瓦力遊戲模擬人數 -> Res.RefreshWaliGamePlayerCount
     */
    S2C_REFRESH_WALI_GAME_PLAYER_COUNT(1052);

    companion object  {
        fun of(v: Int?): GameResCode {
            for (one in GameResCode.values()) {
                if (one.code == v) return one
            }
            return CMD_ID_UNKNOWN
        }
    }
}