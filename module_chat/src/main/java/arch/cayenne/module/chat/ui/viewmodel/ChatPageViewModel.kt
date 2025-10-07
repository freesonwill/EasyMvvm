package arch.cayenne.module.chat.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.lib.websocket.chat.data.MsgNotify

/**
 * @author: wenxi
 * @date: 1/10/25 15:09
 * @description:
 */
class ChatPageViewModel:BaseViewModel() {
    //消息列表
    val msgLists: MutableList<ChatMsg> = mutableListOf()

    fun addLocalMsg(msg: ChatMsg?){
        msg?.let {
            msgLists.add(msgLists.size, msg)
        }
    }

    /**
     * 添加新数据的chatlist
     * */
    fun addNewMsgs(msg: MsgNotify): List<ChatMsg> {
        msgLists.add(msgLists.size, msg.msg)
        return msgLists
    }

    /**
     * 获取历史聊天数据
     * */
    fun getChatHistory(list: List<ChatMsg>?) {
        if (list == null) {
            return
        }
        msgLists.clear()
        msgLists.addAll(list.reversed())
    }


}