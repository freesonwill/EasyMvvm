package com.cn.game.sdk2.websocket.interfaces;


import game.common.proto.ClientReq;
import game.mod.proc.yf.proto.req.GameReq;

public  interface GameService {
    void enterInfo();
    //用户登录
    void login(ClientReq.LoginReq req);
    //进入直播间
    void enterGroup(GameReq.EnterGroup req);
    //离开直播间
    void levelGroup();
    //进入游戏
    void enterGame(GameReq.EnterMiniGame req);
    //离开游戏
    void levelGame(GameReq.LeaveMiniGamesReq req);
    //下注
    void bet(GameReq.BetReq req);
    //刷新金币
    void refreshScore();
    //心跳
    void ping();
}
