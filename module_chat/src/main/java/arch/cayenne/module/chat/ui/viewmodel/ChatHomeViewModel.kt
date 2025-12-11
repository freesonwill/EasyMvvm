package arch.cayenne.module.chat.ui.viewmodel

import android.text.Editable
import android.text.SpannableStringBuilder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.dao.ChatConfigDao
import arch.cayenne.lib.skin.LanguageManager
import arch.cayenne.lib.websocket.chat.data.MsgNotify
import arch.cayenne.lib.websocket.data.SocketConnectState
import arch.cayenne.module.chat.data.constants.CheckBetResultEnum
import arch.cayenne.module.chat.data.constants.EmojiEnum
import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.module.chat.data.constants.MsgType
import arch.cayenne.module.chat.data.model.ChatMsgPageBean
import arch.cayenne.module.chat.data.model.EmojiModel
import arch.cayenne.module.chat.data.model.MentionSpan
import arch.cayenne.module.chat.manager.ChatServerController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class ChatHomeViewModel() : BaseViewModel() {
    private var matchId: Long? = null

    //    private val _currentSoftKeyboard = MutableStateFlow(KeyBoardType.CHAT)
    private val _updateKeyboardUiStatus = MutableLiveData(KeyBoardType.CHAT)
    private val _sendMsgLiveData = MutableLiveData<ChatMsgPageBean>()
    private val _chatHistoryIsEmpty = MutableLiveData<Boolean>()
    private val chatServer: ChatServerController by inject { parametersOf(viewModelScope) }
    private val _emojiLiveData: MutableLiveData<EmojiModel> = MutableLiveData()
    private val _etDelLiveDta: MutableLiveData<Boolean> = MutableLiveData()


    var currentKeyBoardType: KeyBoardType = KeyBoardType.CHAT

    //键盘发送过来的消息
    val sendMsgLiveData: LiveData<ChatMsgPageBean> = _sendMsgLiveData

    //更新键盘盘状态
    val updateKeyboardUiStatus: LiveData<KeyBoardType> = _updateKeyboardUiStatus

    //判断聊天记录是不是空的
    val chatHistoryIsEmpty: LiveData<Boolean> = _chatHistoryIsEmpty

    //聊天api相关
    val chatHistoryFlow = chatServer.historyFlow
    val sendMsgToServerFlow = chatServer.sendMsgResultFlow
    val loginFlow = chatServer.loginFlow
    val checkBetAmountFlow = chatServer.checkBetAmountFlow

    //emojiFragment 发送emoji到et显示
    val emojiLiveData: LiveData<EmojiModel> = _emojiLiveData
    val etDelLiveData: LiveData<Boolean> = _etDelLiveDta


    val languageManager: LanguageManager by inject { parametersOf(viewModelScope) }
    val userDataManager: UserDataManager by inject()

    //聊天设置
    val chatConfigDao: ChatConfigDao by inject()

    var keyBoardHeight: Int = 0
    var isMainSoft: Boolean = false

    fun setArguments(matchId: Long?) {
        //直播间重新从联赛进入时，刷新matchId 重新进入聊天室
        if (this.matchId != null && this.matchId != matchId) {
            this.matchId = matchId
            chatServer.enterRoom(matchId!!)
        } else {
            this.matchId = matchId
        }
    }

    /**
     * 开启聊天服务
     * */
    fun startChatServer() {
        chatServer.connectChatServer()
    }

    /**
     * 关闭聊天服务
     * */
    fun disConnectChatServer() {
        chatServer.disconnectChatServer()
    }

    suspend fun serverFlow(): StateFlow<SocketConnectState> {
        return chatServer.serverConnectFlow()
    }

    suspend fun registerNewMsgFlow(): Flow<MsgNotify> {
        return chatServer.registerMsgFlowToServer()
    }

    /**
     * 聊天登陆
     * */
    fun chatLogin() {
        matchId?.let {
            chatServer.chatLogin(it)
        }
    }

    /**
     *推出聊天室
     * */
    fun leaveRoom() {
        chatServer.leaveRoomFlow
    }

    /**
     *发送消息
     * */
    fun sendMsgToServer(content: String, refUid: String? = null, refPlatform: Int? = null) {
        matchId?.let {
            chatServer.sendMsgToServer(it, content, refUid, refPlatform)
        }
    }

    /**
     * 弹出软件盘 表情键盘时检查是否可以继续弹出对应键盘
     * */
    fun checkSoftKeyboardVisible(): Boolean {
        return when (chatServer.checkBetAmountFlow.value) { //聊天权限不足时每弹出都需要检查权限
            CheckBetResultEnum.BET_AMOUNT_INVALID, CheckBetResultEnum.BALANCE_INVALID -> {
                false
            }

            CheckBetResultEnum.SUCCESS -> true
            null -> false
        }
    }


    /**
     * 发送文本，@，普通表情消息
     * */
    fun createLocalMsg(editable: Editable): ChatMsgPageBean? {
        if (loginFlow.value == null) {
            "chat is not login ".logd(TAG)
            return null
        }
        var msgBean: ChatMsgPageBean? = null
        val localMsg = chatServer.addLocalMsg(editable.toString()) ?: return null

        if (editable.length == 4 && editable.toString() in arrayOf("注单体育", "注单游戏")) {
            val betRanges = mutableListOf<IntRange>()
            betRanges.add(IntRange(0,4))
            msgBean = ChatMsgPageBean.toChatPageBean(localMsg,MsgType.BET,betRanges)
        } else {
            val spannable = SpannableStringBuilder(editable)
            val spans = spannable.getSpans(0, editable.length, MentionSpan::class.java)
            if (spans.isNotEmpty()) {
                val atIntRanges = mutableListOf<IntRange>()
                spans.forEach {
                    val start = spannable.getSpanStart(it)
                    val end = spannable.getSpanEnd(it)
                    atIntRanges.add(IntRange(start, end))
                }
                msgBean = ChatMsgPageBean.toChatPageBean(localMsg, MsgType.AT, atIntRanges)
            } else {
                msgBean = ChatMsgPageBean.toChatPageBean(localMsg, MsgType.TEXT)
            }
        }


        return msgBean
    }

    /**
     * 发送赛事表情
     * */

    fun createBidLocalMsg(emojiKey: String): ChatMsgPageBean? {
        if (loginFlow.value == null) {
            "chat is not login ".logd(TAG)
            return null
        }
        val chatMsg = chatServer.addLocalMsg(emojiKey) ?: return null
        return ChatMsgPageBean.toChatPageBean(chatMsg, MsgType.EMOJI)
    }


    /**
     * 软件et传递消息
     * */
    fun sendMsgToChat(msg: ChatMsgPageBean) {
        _sendMsgLiveData.value = msg
    }

    fun updateKeyBoardUi(keyBoardType: KeyBoardType, flag: Int) {
        _updateKeyboardUiStatus.value = keyBoardType
    }

    fun setSoftConfig(value: Boolean) {
        userDataManager.setKeyValue(UserDataKey.KEY_SOFT_CONFIG, value)
    }

    fun getHotRecycler(): List<EmojiModel> {
        return arrayOf(
            EmojiEnum.Gin,
            EmojiEnum.Smile,
            EmojiEnum.Boring,
            EmojiEnum.Scrowl,
            EmojiEnum.Dizzy
        ).map {
            EmojiModel(it.resId, it.key)
        }.toList()
    }


    fun etDelFunction() {
        _etDelLiveDta.value = _etDelLiveDta.value?.let { !it } ?: false
    }

    fun addEmojiToChat(emojiData: EmojiModel) {
        _emojiLiveData.value = emojiData
    }

}