package arch.cayenne.lib.common.data.constants

import arch.cayenne.lib.websocket.chat.data.MsgType

/**
 * @author: wenxi
 * @date: 27/11/25 11:26
 * @description:
 */
enum class ChatMsgType(val value:Int) {
    BET_GAME(2),
    BET_SPORT(4),
    EMOJI(5),
    AT(3),
    TEXT(1),
    SYSTEM(0);
//    MSG_TYPE_SYSTEM(0),
//    MSG_TYPE_TEXT(1),
//    MSG_TYPE_SHARE(2),
//    MSG_TYPE_AT(3);
    companion object {

        fun getChatMsgType(value: MsgType): ChatMsgType {
            return when (value) {
                MsgType.MSG_TYPE_TEXT -> TEXT
                MsgType.MSG_TYPE_SHARE -> BET_GAME
                MsgType.MSG_TYPE_SYSTEM -> SYSTEM
                MsgType.MSG_TYPE_AT -> AT
                else -> TEXT
            }
        }
    }
}