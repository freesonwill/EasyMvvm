package arch.cayenne.module.chat.data.model

import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.lib.common.data.constants.MsgType

/**
 * @author: wenxi
 * @date: 27/11/25 11:22
 * @description:
 */
data class ChatMsgPageBean(
    val uid: String,
    val userName: String,
    val avatarId: Int,
    val msgId: String,
    val content: String,
    val timestamp: String,
    val refUid: String,
    val refUserName: String,
    val refAvatarId: Int,
    val onlyForSelf: Int,
    val platform: Int,
    val msgType: MsgType,
    val atRange:List<IntRange>? = null
) :Comparable<ChatMsgPageBean>{
    companion object {

        fun toChatPageBean(bean: ChatMsg, msgType: MsgType, atRange: List<IntRange>? = null): ChatMsgPageBean {
            return ChatMsgPageBean(
                uid = bean.uid,
                userName = bean.userName,
                avatarId = bean.avatarId,
                msgId = bean.msgId,
                content = bean.content,
                timestamp = bean.timestamp,
                refUid = bean.refUid,
                refUserName = bean.refUserName,
                refAvatarId = bean.refAvatarId,
                onlyForSelf = bean.onlyForSelf,
                platform = bean.platform,
                msgType = msgType,
                atRange = atRange
            )
        }




    }

    override fun compareTo(other: ChatMsgPageBean): Int {
        return this.timestamp.compareTo(other.timestamp)
    }

}