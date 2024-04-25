package com.cn.game.sdk.game;

public interface GameMessage {
    //用户登录
    void login(String agentName, String version, int server, String nikeName, String token);
    //进入直播间
    void enterGroup();
    //离开直播间
    void leavelGroup();
    //进入游戏
    void enterGame();
    //离开游戏
    void leavelGame();
    //下注
    void bet();
    //刷新金币
    void refreshScore();
}
