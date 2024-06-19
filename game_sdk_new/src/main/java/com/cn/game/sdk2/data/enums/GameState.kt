package com.cn.game.sdk2.data.enums

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/17 10:17
 **/
enum class GameState(var code:Int) {
    //初始化
    Init(-1),
    //下注中
    Betting(1),
    //结算中
    Settling(2),
    //开奖中
    Drawing(3),
    //开奖结束
    DrawFinish(4),
}