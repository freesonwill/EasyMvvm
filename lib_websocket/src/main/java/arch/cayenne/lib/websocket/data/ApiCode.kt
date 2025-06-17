package arch.cayenne.lib.websocket.data

enum class ApiCode(val mid: Short, val sid: Short) {
    LOGIN(7, 7),             // 7-7: 登录游戏服
    PING(7, 100),            // 7-100: ping消息, 客户端回传数据。纯回传，server 无业务处理，无返回
    BALANCE(500, 1005),     //500-1005: 查询余额(主動)
    BALANCE_NOTIFY(600, 1002),     //600-1002: 订单余额推送(被動)
    LIST_SPORT(500, 1004),  // 500-1004: 获取球类信息
    STATISTICAL(500, 1000), // 500-1000: 获取赛事统计
    TOURNAMENT(500, 1001),  // 500-1001: 获取联赛信息
    LIST_MATCH(500, 1002),   //500-1002: 获取比赛列表，盘口信息只返回热门盘口
    SUBSCRIBE_HOME_MATCH(500, 1100),  //500-1100: 订阅比赛
    CANCEL_SUBSCRIBE_HOME_MATCH(500, 1101),  //500-1101: 取消订阅比赛
    LIST_OUTRIGHT_MATCH(500, 1008), // 500-1008: 获取首页冠军赛列表
    ADD_COLLECT(500, 1040),     //500-1040: 添加收藏
    REMOVE_COLLECT(500, 1041),  //500-1041: 移除收藏
    MATCH_NOTIFY(600, 1000),   //600-1000: 比赛推送
    COMBO_BET(500, 1010),   //500-1010: 串关下注
    SINGLE_BET(500, 1015),   //500-1015: 單注下注
    RESERVE_BET(500, 1020),  //500-1020: 預約下注
    MATCH_LIVE_STREAM(700, 2005),//700-2005: 比赛直播流
    LISt_COLLECT(500, 1042),  // 500-1042: 收藏列表

    GET_ORDER(500, 1012),    //500-1012: 获取下注记录
    GER_RESERVE_ORDER(500, 1021), //500-1021 获取预约下注记录
    GET_LINEUP(700, 2002),    //700-2002: 比赛阵容数据
    GET_MATCH(500, 1003),    // 500-1003: 获取比赛详情

    SUBSCRIBE_MATCH_INFO(500, 1102),      // 500-1102: 订阅比赛详情
    MATCH_INFO_NOTIFY(600, 1004),      // 600-1004: 比赛INFO推送
    CANCEL_SUBSCRIBE_MATCH_INFO(500, 1103),     // 500-1103: 取消订阅比赛详情

    GET_SINGLE_RISK(500, 1013),    //500-1013: 获取单关下注限额
    GET_COMBO_RISK(500, 1014),     //500-1014: 获取串关下注限额

    MATCH_TREND(700, 2004),       //700-2004: 比赛趋势数据
    MATCH_LIVE(700, 2001),        //700-2001: 比赛统计数据
    GET_STANDINGS(700, 2003),     //700-2003: 积分榜数据
    MATCH_LEAGUE(500, 1006),      //500-1006: 联赛列表
    MATCH_STATICS(700, 1100),     //700-1100: 订阅比赛统计数据推送

    EARLY_SETTLE(500, 1011),       //500-1011: 发起提前结算
    RESERVE_CANCEL(500, 1022),     //500-1021: 获取用户预约下注记录列表
    RESERVE_UPDATE(500, 1023),     //500-1023: 修改预约下注订单

    ORDER_STATUS_NOTIFY(600, 1001), //600-1001: 订单状态推送(被動)
    GET_MARKET_TYPE(500, 1007),   //500-1007: 盘口分类

    EARLY_SETTLE_PRICE(500, 1016),    //500-1016: 提前结算报价
    UPDATE_SYSTEM_SETTING(500, 1051), //500-1051: 修改系统设置

    EARLY_SETTLE_NOTIFY(600, 1012), //600-1012: 提前结算推送(被動)

    CHAT_PING(0, 2), //0 -2 聊天心跳
    CHAT_LOGIN(90, 1101), //7-7 聊天登陆
    CHAT_ENTER_ROOM(500, 1001),//进入聊天室
    CHAT_LEAVE_ROOM(500, 1003),//离开房间
    CHAT_SEND_MSG(500, 1005),//发送消息
    CHAT_HISTORY(500, 1007),//历史聊天信息
    CHAT_USER_STATISTIC(500, 1009),//用户统计消息
    CHAT_SET_USER_AVATAR(500, 1011),//设置用户头像
    CHAT_SYNC_USER(500, 1013),//更新用户信息
    CHAT_CHECK_BETAMOUNT(500, 1015),//校验投注额
    CHAT_MSG_NOTIFY(500, 2001),//用户消息推送

    USER_SYS_MESSAGE(500, 1106),// 500-1106: 获取用户消息列表
    UPDATE_MESSAGE(500, 1107),  //500-1107： 修改消息状态 已读或删除

    SEARCH(500, 1030), // 500-1030: 搜索
    SEARCH_HOT_WORD(500, 1031), // 500-1031: 热门搜索词
    SEARCH_RECOMMEND(500, 1032), // 500-1032: 搜索自动补充词汇

    SUBSCRIBE_MATCH_MARKET(500, 1104), // 500-1104: 订阅比赛盘口
    CANCEL_SUBSCRIBE_MATCH_MARKET(500, 1105), // 500-1105: 取消订阅比赛盘口
    MATCH_MARKET_NOTIFY(600, 1005), // 600-1005: 比赛盘口推送
    RECENTLY_31_MATCH_SCHEDULE_COUNT(500, 1009), // 500-1009: 获取近31日比赛日程count
}