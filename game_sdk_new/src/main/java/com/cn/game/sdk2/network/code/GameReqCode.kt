package com.cn.game.sdk2.network.code

enum class GameReqCode(val code:Int) {
    CMD_ID_UNKNOWN(-1),
    /**
     * 用户登录
     */
    SUB_LOGON_REQ__LOGIN(7),
    /**
     * 進入直播間, 不可切到DEFAULT_GROUPID("") -> Req.EnterGroup
     */
    C2S_ENTER_GROUP(1001),
    /**
     * 離開直播間: GroupId=DEFAULT_GRUOPID("") -> no payload
     * P.S. 重置GroupId時也一併離開小遊戲
     */
    C2S_LEAVE_GROUP(1003),
    /**
     * 進入小遊戲 -> Req.EnterMiniGame
     */
    C2S_ENTER_MINI_GAME(1005),
    /**
     * 離開小遊戲 -> Req.LeaveMiniGamesReq
     */
    C2S_LEAVE_MINI_GAME(1007),
    /**
     * 下注单 -> Req.BetReq
     */
    C2S_BET(1030),

    /**
     * 刷新金錢 -> no payload
     */
    C2S_REFRESH_SCORE(1035),
    /**
     * 盘面指定 -> Req.TestAssign
     */
    C2S_TEST_ASSIGN(1050);

    companion object  {
        fun of(v: Int?): GameReqCode {
            for (one in GameReqCode.values()) {
                if (one.code == v) return one
            }
            return CMD_ID_UNKNOWN
        }
    }
}