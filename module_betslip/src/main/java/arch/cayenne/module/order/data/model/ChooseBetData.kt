package arch.cayenne.module.order.data.model

import arch.cayenne.lib.common.data.constants.ChatMsgType
import game.chat.proto.GameChat.ChatMsg

/**
 * @author: wenxi
 * @date: 26/12/25 19:37
 * @description:
 */
data class ChooseBetData(val type:ChatMsgType,val betCode:String) {
}