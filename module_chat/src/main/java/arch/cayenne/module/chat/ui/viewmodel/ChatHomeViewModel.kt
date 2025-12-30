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
import arch.cayenne.lib.common.data.constants.ChatMsgType
import arch.cayenne.lib.websocket.chat.data.ChatType
import arch.cayenne.lib.websocket.chat.data.MsgType
import arch.cayenne.module.chat.data.model.ChatMsgPageBean
import arch.cayenne.module.chat.data.model.EmojiModel
import arch.cayenne.module.chat.data.model.MentionSpan
import arch.cayenne.module.chat.manager.ChatServerController
import arch.cayenne.module.chat.utils.ChatMsgUtils
import arch.cayenne.module.order.data.model.BetShareBean
import com.google.gson.Gson
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class ChatHomeViewModel() : BaseViewModel() {
    private var matchId: Long? = null

    //    private val _currentSoftKeyboard = MutableStateFlow(KeyBoardType.CHAT)
    private val _updateKeyboardUiStatus = MutableLiveData(KeyBoardType.CHAT)
    private val _sendMsgLiveData = MutableLiveData<ChatMsgPageBean>()

    //    private val _chatHistoryIsEmpty = MutableLiveData<Boolean>()
    private val chatServer: ChatServerController by inject { parametersOf(viewModelScope) }
    private val _emojiFlow: MutableSharedFlow<EmojiModel?> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val _etDelLiveDta: MutableLiveData<Boolean> = MutableLiveData()
    private val _sendTextLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val _currentKeyBoardType = MutableLiveData<KeyBoardType>(KeyBoardType.CHAT)
    private val _atLiveData: MutableLiveData<ChatMsgPageBean> = MutableLiveData<ChatMsgPageBean>()


    var currentKeyBoardType: KeyBoardType = KeyBoardType.CHAT

    //键盘发送过来的消息
    val sendMsgLiveData: LiveData<ChatMsgPageBean> = _sendMsgLiveData

    //更新键盘盘状态
    val updateKeyboardUiStatus: LiveData<KeyBoardType> = _updateKeyboardUiStatus

//    //判断聊天记录是不是空的
//    val chatHistoryIsEmpty: LiveData<Boolean> = _chatHistoryIsEmpty

    //聊天api相关
    val chatHistoryFlow = chatServer.historyFlow
    val sendMsgToServerFlow = chatServer.sendMsgResultFlow
    val loginFlow = chatServer.getManagerLoginFlow()
    val checkBetAmountFlow = chatServer.checkBetAmountFlow

    //emojiFragment 发送emoji到et显示
    val emojiFlow: SharedFlow<EmojiModel?> = _emojiFlow
    val etDelLiveData: LiveData<Boolean> = _etDelLiveDta
    val sendTextLiveData: LiveData<Boolean> = _sendTextLiveData
    val atLiveData: LiveData<ChatMsgPageBean> = _atLiveData

    val languageManager: LanguageManager by inject { parametersOf(viewModelScope) }
    val userDataManager: UserDataManager by inject()

    //聊天设置
    val chatConfigDao: ChatConfigDao by inject()
    var keyBoardHeight: Int = 0

    //聊天键盘切换监听
    val currentKeyBoardTypeLiveData: LiveData<KeyBoardType> = _currentKeyBoardType
    var languageSelectPosition: Int = 1

    var currentSelectBetShare: BetShareBean? = null


    fun setArguments(matchId: Long?, chatType: ChatType) {
        //直播间重新从联赛进入时，刷新matchId 重新进入聊天室
        if (this.matchId != null && this.matchId != matchId) {
            this.matchId = matchId
            chatServer.enterRoom(matchId!!, chatType)
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
    fun chatLogin(chatType: ChatType) {
        chatServer.chatLogin(chatType)
    }

    fun enterRoom(chatType: ChatType) {
        matchId?.let {
            chatServer.enterRoom(it, chatType)
        }
    }


    /**
     *推出聊天室
     * */
    fun leaveRoom(chatType: ChatType) {
        matchId?.let {
            chatServer.leaveRoom(it, chatType)
        }
    }

    /**
     *发送消息
     * */
    fun sendMsgToServer(
        content: String,
        refUid: List<Long>? = null,
        chatType: ChatType,
        msgType: MsgType,
        extraData: Map<String,String>?,
    ) {
        matchId?.let {
            chatServer.sendMsgToServer(it, content, chatType, msgType, extraData, refUid)
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
     * @用户：[**]
     *分享：[***]
     * */
    fun createLocalMsg(editable: Editable, chatType: ChatType): ChatMsgPageBean? {
        if (loginFlow.value == null) {
            "chat is not login ".logd(TAG)
            return null
        }
        var msgBean: ChatMsgPageBean? = null

        val spannable = SpannableStringBuilder(editable)
        val spans = spannable.getSpans(0, editable.length, MentionSpan::class.java)
        "sendMsg spans size:${spans.size}".logd(TAG)
        if (spans.isNotEmpty()) {
            val atIntRanges = mutableListOf<IntRange>()
            spans.forEach {
                val start = spannable.getSpanStart(it)
                val end = spannable.getSpanEnd(it)
                atIntRanges.add(IntRange(start, end))
            }
            val users = ChatMsgUtils.createUserInfo(spans)
            val refUids = users?.map { it.key }?.toList()
            val localMsg = chatServer.addLocalMsg(
                ChatMsgUtils.createContent(spannable, spans),
                chatType,
                MsgType.getMsgType(spans.first().msgType.value),
                ChatMsgUtils.createExtraData(currentSelectBetShare, spans),
                refUids,
                users
            ) ?: return null
            msgBean = ChatMsgPageBean.toChatPageBean(localMsg)
            "localMsg msgBean:${Gson().toJson(localMsg)}".logd(TAG)
        } else {
            val localMsg =
                chatServer.addLocalMsg(
                    editable.toString(),
                    chatType,
                    MsgType.MSG_TYPE_TEXT,
                    null,
                    null,
                    null
                )
                    ?: return null
            msgBean = ChatMsgPageBean.toChatPageBean(localMsg)
        }
//        "createLocalMsg msgBean:${Gson().toJson(msgBean)}".logd(TAG)
        return msgBean
    }

    /**
     * 发送赛事表情
     * */

    fun createBidLocalMsg(emojiKey: String, chatType: ChatType): ChatMsgPageBean? {
        if (loginFlow.value == null) {
            "chat is not login ".logd(TAG)
            return null
        }
        val chatMsg =
            chatServer.addLocalMsg(emojiKey, chatType, MsgType.MSG_TYPE_TEXT, null, null,null)
                ?: return null
        return ChatMsgPageBean.toChatPageBean(chatMsg)
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
            EmojiEnum.Dizzy,
            EmojiEnum.Duh,
        ).map {
            EmojiModel(it.resId, it.key)
        }.toList()
    }


    fun etDelFunction() {
        val value = _etDelLiveDta.value?.let { !it } ?: false
        _etDelLiveDta.value = value
    }

    fun addEmojiToChat(emojiData: EmojiModel) {
        _emojiFlow.tryEmit(emojiData)
    }

    fun sendTextToChat() {
        val value = _sendTextLiveData.value?.let { !it } ?: false
        _sendTextLiveData.value = value
    }

    fun listenCurrentKeyBoardType(keyBoardType: KeyBoardType) {
        _currentKeyBoardType.value = keyBoardType
    }

    fun updateLanguageSelect(position: Int) {
        languageSelectPosition = position
    }

    fun addAtMsgToChat(msg: ChatMsgPageBean) {
        _atLiveData.value = msg
    }

}