package arch.cayenne.lib.socket.data

enum class ApiCode(val mid: Short, val sid: Short) {
    LOGIN(7,7),             // 7-7: 登录游戏服
    PING(7,100),            // 7-100: ping消息, 客户端回传数据。纯回传，server 无业务处理，无返回
    BALANCE(500, 1005),     //500-1005: 查询余额(主動)
    LIST_SPORT(500, 1004),  // 500-1004: 获取球类信息
    STATISTICAL(500, 1000), // 500-1000: 获取赛事统计
    TOURNAMENT(500, 1001),  // 500-1001: 获取联赛信息
    LIST_MATCH(500, 1002),   //500-1002: 获取比赛列表，盘口信息只返回热门盘口
    COMBO_BET(500, 1010),   //500-1010: 串关下注
    SINGLE_BET(500, 1015),   //500-1015: 單注下注
    RESERVE_BET(500, 1020),  //500-1020: 預約下注
    MATCH_LIVE_STREAM(700, 2005),//700-2005: 比赛直播流

    GET_ORDER(500,1012),    //500-1012: 获取下注记录
    GER_RESERVE_ORDER(500,1021), //500-1021 获取预约下注记录
    GET_LINEUP(700,2002),    //700-2002: 比赛阵容数据
}