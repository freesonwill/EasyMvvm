package arch.cayenne.lib.common.data.constants

import arch.cayenne.lib.websocket.chat.data.MsgType

/**
 * @author: wenxi
 * @date: 27/11/25 11:26
 * @description:
 */
enum class ChatMsgType(val value:Int) {
    BET_GAME(9),
    BET_SPORT(2),
    SHARE_GAME(3),
    EMOJI(5),
    AT(8),
    TEXT(1),
    SYSTEM(0);

    companion object {

        fun getChatMsgType(value: MsgType): ChatMsgType {
            return when (value) {
                MsgType.MSG_TYPE_TEXT -> TEXT
                MsgType.MSG_TYPE_SHARE_GAME -> SHARE_GAME
                MsgType.MSG_TYPE_SHARE_ORDER -> BET_SPORT// BET_GAME
                MsgType.MSG_TYPE_SYSTEM -> SYSTEM
                MsgType.MSG_TYPE_AT -> AT
                else -> TEXT
            }
        }
    }
}