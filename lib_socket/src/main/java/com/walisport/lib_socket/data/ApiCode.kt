package com.walisport.lib_socket.data

enum class ApiCode(val mid: Short, val sid: Short) {
    LOGIN(7,7),             // 7-7: 登录游戏服
    PING(7,100),            // 7-100: ping消息, 客户端回传数据。纯回传，server 无业务处理，无返回
    LIST_SPORT(500, 1004),  // 500-1004: 获取球类信息
    STATISTICAL(500, 1000), // 500-1000: 获取赛事统计
    Tournament(500, 1001)   // 500-1001: 获取联赛信息
}