package com.cn.game.sdk.common;

/**
 * request code
 */
public interface GameReqCode {
    /**
     * 進入直播間, 不可切到DEFAULT_GROUPID("") -> Req.EnterGroup
     */
    int C2S_ENTER_GROUP = 1001;
    /**
     * 離開直播間: GroupId=DEFAULT_GRUOPID("") -> no payload
     * P.S. 重置GroupId時也一併離開小遊戲
     */
    int C2S_LEAVE_GROUP = 1003;
    /**
     * 進入小遊戲 -> Req.EnterMiniGame
     */
    int C2S_ENTER_MINI_GAME = 1005;
    /**
     * 離開小遊戲 -> no payload
     */
    int C2S_LEAVE_MINI_GAME = 1007;
    /**
     * 下注单 -> Req.BetReq
     */
    int C2S_BET = 1030;

    /**
     * 刷新金錢 -> no payload
     */
    int C2S_REFRESH_SCORE = 1035;
    /**
     * 盘面指定 -> Req.TestAssign
     */
    int C2S_TEST_ASSIGN = 1050;
}
