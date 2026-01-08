package arch.cayenne.module.chat.data.model

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.lib.common.data.constants.ChatMsgType
import arch.cayenne.lib.websocket.chat.data.ChatRefUser
import com.google.gson.Gson

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
    val refUid: List<String>? = null,
    val refInfos: Map<String, ChatRefUser>? = null,
    val extraData: Map<String,String>? = null,
    var flashFlag:Boolean = false
) : Comparable<ChatMsgPageBean> {

    fun toChatRefUsers(): ChatRefUser {

        return ChatRefUser(
            uid = uid,
            userName = userName,
            avatarId = avatarId,
            replaceRefUserName = replaceUserName
        )
    }

    companion object {

        fun toChatPageBean(
            bean: ChatMsg,
            myUid:String,
        ): ChatMsgPageBean {
//            "toChatMsg ${Gson().toJson(bean)}".logd("ChatMsgPageBean")
            val content = bean.content
            return ChatMsgPageBean(
                uid = bean.uid,
                userName = bean.userName,
                avatarId = bean.avatarId,
                msgId = bean.msgId,
                content = content,
                timestamp = bean.timestamp,
                refUid = bean.refUids,
                refInfos = bean.refInfos,
                onlyForSelf = bean.onlyForSelf,
                replaceUserName = bean.replaceUserName,
                msgType = ChatMsgType.getChatMsgType(bean.msgType),
                extraData = bean.extraData,
                flashFlag = bean.refUids?.contains(myUid) ?: false
            )
        }
    }

    override fun compareTo(other: ChatMsgPageBean): Int {
        return this.timestamp.compareTo(other.timestamp)
    }

}