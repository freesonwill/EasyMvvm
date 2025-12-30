package arch.cayenne.module.chat.data.model

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.lib.common.data.constants.ChatMsgType
import arch.cayenne.lib.websocket.chat.data.ChatRefUser

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
    val onlyForSelf: Int,
    val msgType: ChatMsgType,
    val replaceUserName: String? = null,
    val refUid: List<Long>? = null,
    val refInfos: Map<Long, ChatRefUser>? = null,
    val extraData: Map<String,String>? = null
) : Comparable<ChatMsgPageBean> {

    companion object {

        fun toChatPageBean(
            bean: ChatMsg,
        ): ChatMsgPageBean {
                "toChatPageBean content=${bean.content}".logd("ChatMsgPageBean")
            val content = bean.content
            return ChatMsgPageBean(
                uid = bean.uid,
                userName = bean.userName,
                avatarId = bean.avatarId,
                msgId = bean.msgId,
                content = content,
                timestamp = bean.timestamp,
                refUid = bean.refUid,
                refInfos = bean.refInfos,
                onlyForSelf = bean.onlyForSelf,
                replaceUserName = bean.replaceUserName,
                msgType = ChatMsgType.getChatMsgType(bean.msgType),
                extraData = bean.extraData
            )
        }
    }

    override fun compareTo(other: ChatMsgPageBean): Int {
        return this.timestamp.compareTo(other.timestamp)
    }

}