package com.cn.game.sdk.game

class GameEmit(mid:Int,sid:Int, data:ByteArray) {
    var mid:Int;
    var sid:Int;
    var data:ByteArray;
    init {
        this.mid = mid;
        this.sid = sid;
        this.data = data;
    }
}