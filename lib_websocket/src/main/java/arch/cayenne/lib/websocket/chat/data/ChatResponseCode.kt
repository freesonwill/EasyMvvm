package arch.cayenne.lib.websocket.chat.data

enum class ChatResponseCode(val sid: Short, val mid: Short = 500) {

    HEARTBEATS(1), //心跳消息 = 1,

    LOGIN(1102), //登录结果 = 1102,

    ENTER_CHAT_ROOM_RESP(1102), //进入房间结果 = 1002

    LEAVE_CHAT_ROOM_RESP(1004), //离开房间结果 = 1004

    SEND_MSG_RESP(1006), //发送消息结果 = 1006

    CHAT_HISTORY(1008), //消息历史消息 = 1008

    USER_STATISTIC(1010), //用户统计信息 = 1010

    SET_AVATAR_RESP(1012), //头像设置结果 = 1012

    UPDATE_USER_INFO_RESP(1014), //更新用户信息 = 1014

    MSG_NOTIFY(2001),//用户消息推送 = 2001,

    LOGGED_IN_ELSEWHERE(2002), //账号异地登录 = 2002

    CHECK_BET_AMOUNT_RESP(1016), //查询用户投注额 = 1016

}