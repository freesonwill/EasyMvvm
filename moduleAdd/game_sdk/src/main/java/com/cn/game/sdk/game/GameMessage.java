package com.cn.game.sdk.game;

import game.mod.proc.yf.proto.req.GameReq;

public interface GameMessage {
    //用户登录
    void login(String agentName, String version, int server, String nikeName, String token);
    //进入直播间
    void enterGroup(GameReq.EnterGroup req);
    //离开直播间
    void leavelGroup();
    //进入游戏
    void enterGame(GameReq.EnterMiniGame req);
    //离开游戏
    void leavelGame(GameReq.LeaveMiniGamesReq req);
    //下注
    void bet(GameReq.BetReq req);
    //刷新金币
    void refreshScore();
}
