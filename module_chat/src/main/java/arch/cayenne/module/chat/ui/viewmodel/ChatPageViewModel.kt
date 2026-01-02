package arch.cayenne.module.chat.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.lib.websocket.chat.data.MsgNotify
import arch.cayenne.lib.common.data.constants.ChatMsgType
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.chat.data.MsgType
import arch.cayenne.module.chat.data.model.ChatMsgPageBean
import org.koin.core.component.inject

/**
 * @author: wenxi
 * @date: 1/10/25 15:09
 * @description:
 */
class ChatPageViewModel : BaseViewModel() {
    //消息列表
    val msgLists: MutableList<ChatMsgPageBean> = mutableListOf()
    val userDataManager: UserDataManager by inject()
    var myUid:String = ""

    override fun initViewModel() {
        super.initViewModel()
       myUid = userDataManager.getValue(UserDataKey.KEY_UID,-1L).toString()
    }

    fun addLocalMsg(msg: ChatMsgPageBean?) {
        msg?.let {
            msgLists.add(0, msg)
        }
    }

    /**
     * 添加新数据的chatlist
     * */
    fun addNewMsgs(msg: MsgNotify): List<ChatMsgPageBean> {
        //TODO 系统消息暂不处理
        if (msg.msg.msgType == MsgType.MSG_TYPE_SYSTEM) {
            return msgLists
        }
        msgLists.add(0, ChatMsgPageBean.toChatPageBean(msg.msg,myUid))
        return msgLists
    }

    /**
     * 获取历史聊天数据
     * */
    fun getChatHistory(list: List<ChatMsg>?) {
        if (list == null) {
            return
        }
        val nList = list.filter { it.msgType != MsgType.MSG_TYPE_SYSTEM }
            .map { ChatMsgPageBean.toChatPageBean(it,"") }.toList()
        msgLists.clear()
        msgLists.addAll(nList)
    }


}