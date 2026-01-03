package arch.cayenne.module.chat.ui.fragment

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams
import androidx.activity.addCallback
import androidx.core.animation.addListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.ChatMsgType
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavResultExt.observeResult
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.websocket.chat.data.ChatRefUser
import arch.cayenne.lib.websocket.chat.data.ChatType
import arch.cayenne.lib.websocket.data.SocketConnectState
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.module.chat.data.constants.KeyboardActionType
import arch.cayenne.module.chat.data.model.EmojiModel
import arch.cayenne.module.chat.databinding.FragmentLiveChatBinding
import arch.cayenne.module.chat.manager.SoftKeyboardManager
import arch.cayenne.module.chat.manager.interf.SoftKeyBoardMangerListener
import arch.cayenne.module.chat.ui.adapter.EmojiHotItemAdapter
import arch.cayenne.module.chat.ui.viewmodel.ChatHomeViewModel
import arch.cayenne.module.chat.utils.EmojiUtils.BID_EMOJI_REGEX
import com.gyf.immersionbar.ImmersionBar
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import kotlin.reflect.KClass
import arch.cayenne.module.chat.manager.ChatATHelper
import arch.cayenne.module.chat.manager.SoftKeyBoardAnim
import arch.cayenne.module.chat.manager.SoftKeyBoardAnim.getInputAnim
import arch.cayenne.module.chat.utils.ChatMsgUtils
import arch.cayenne.module.order.data.model.BetShareBean
import arch.cayenne.module.order.ui.fragment.ChatChooseBetFragment
import kotlinx.coroutines.delay

//聊天
abstract class ChatBaseFragment : BaseFragment<ChatHomeViewModel, FragmentLiveChatBinding>(),
    SoftKeyBoardMangerListener {
    companion object {
        const val FRAGMENT_RESULT_KEY = "chat_fragment"
        const val MATCH_ID_KEY = "match_id_key"
        const val MATCH_STATUS_KEY = "match_status_key"
        const val LIVE_START_KEY = "live_start_key"
    }

    override val vbClass: KClass<FragmentLiveChatBinding> = FragmentLiveChatBinding::class
    override val vmClass: KClass<ChatHomeViewModel> = ChatHomeViewModel::class

    private lateinit var softKeyBoardManager: SoftKeyboardManager
    private lateinit var chatAtHelper: ChatATHelper
    abstract val chatType: ChatType
    abstract val isMainSoft: Boolean

    //传给LiveMainFragment,因为直播间的页面上下滑动时，页面扩展或者恢复。在键盘弹出时，禁止页面扩展和收缩
    private var emojiPopupListen: ((isPopup: Boolean) -> Unit)? = null


    abstract fun listenParentFragment()

    override fun initView(savedInstanceState: Bundle?) {
        listenParentFragment()
        initChatPageFragment()
        initSoftKeyBoardFragment()
        initInputListener()
        initHotRecycler()
        initChatHelper()

//        arguments?.let { //TODO  首页过来的 之后需要处理聊天室要matchId的问题
//            val value = it.getBoolean("chat", false)
//            if (value) {
//                setMainChatStatus()
//            }
//        }
    }

    fun addEmojiPopupListen(emojiPopupListen: (isPopup: Boolean) -> Unit) {
        this.emojiPopupListen = emojiPopupListen
    }

    /**
     * 热门表情
     * */
    private fun initHotRecycler() {
        mBinding.hotRecyclerview.also {
            it.layoutManager =
                LinearLayoutManager(it.context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = EmojiHotItemAdapter()
            adapter.setItemListener(object : RecyclerItemListener<EmojiModel> {
                override fun onItemClick(item: EmojiModel?, position: Int) {
                    item?.let { it1 -> addEmojiData(it1) }
                }
            })
            adapter.submitList(mViewModel.getHotRecycler())
            it.adapter = adapter
        }
    }

    /**
     * 表情键盘
     * */
    private fun initEmojiFragment() {
        val fragment = EmojiHomeFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.chat_keyboard, fragment, EmojiHomeFragment.TAG).commit()
    }

    /**
     * 聊天记录
     * */
    private fun initChatPageFragment() {
        val fragment = ChatPageFragment()
        childFragmentManager.beginTransaction()
            .replace(mBinding.liveChatHistory.id, fragment, ChatPageFragment.TAG)
            .commit()
    }

    /**
     * 直播间调用
     * */
//    fun setMatchLiveData(matchId: LiveData<Long>?, mainMatch: LiveData<LiveMatchBean>?) {
//        matchIdLiveData = matchId
//        this.mainMatch = mainMatch
////        TODO 直播间进入聊天室逻辑待定
//        if (matchId?.value != null && mainMatch == null) {
//            mViewModel.setArguments(matchId.value)
//            mViewModel.startChatServer()
//        }
//    }
//
//    /**
//     * 首页调用
//     * */
//    private fun setMainChatStatus() {
//        lifecycleScope.launchWhenResumed {
//            observeMatchId(102)
//            observeLiveMatch(null)
//        }
//    }

    /**
     * 当首页和直播间调用时给
     * */
    fun observeMatchId(matchId: Long) {
        mViewModel.setArguments(matchId, chatType)
    }

    fun observeLiveMatch(liveStart: Boolean, matchStatus: Int) {
        updateChatUi(liveStart, matchStatus)
    }


    override fun onStart() {
        StatusBarConfig.statusBarType =
            StatusBarMode.DRAW_BEHIND(autoPadding = false, autoIsNavigation = true)
        setStatusBar(StatusBarConfig, mBinding.root)
        super.onStart()
        mViewModel.setSoftConfig(false)
    }

    override fun onStop() {
        super.onStop()
        mViewModel.setSoftConfig(true)
    }


    override fun onFragmentAnimEnd(isEnter: Boolean) {
        super.onFragmentAnimEnd(isEnter)
        keyboardChangeClick(KeyBoardType.CHAT, 4)
    }

    override fun onResume() {
        super.onResume()
        chatAtHelper.addTextWatcher()
        //软件盘时获取的高度有误，onResume时获取固定值
//        mBinding.liveChatKeyboard.translationY = -62.dp2px.toFloat()
    }

    override fun onPause() {
        super.onPause()
        keyboardChangeClick(KeyBoardType.CHAT, 3)//移动到其他页面后关闭软件盘表情键盘
        chatAtHelper.removeTextWatcher()
    }


    override suspend fun createObserver() {
        lifecycleScope.launch {
            launch {
                mViewModel.serverFlow().collect {
                    if (it == SocketConnectState.Connecting && mViewModel.loginFlow.value == null) {
                        mViewModel.chatLogin(chatType)
                    }
                }
            }
            launch {
                mViewModel.sendMsgToServerFlow.collect {

                }
            }
            launch {//选择注单返回监听
                observeResult<Bundle>(ChatChooseBetFragment.SHARE_BET_LISTEN) {
                    ChatMsgUtils.checkAndReplaceBetShareInEditable(mBinding.chatEtInput)
                    val data =
                        it.getParcelable<BetShareBean>(ChatChooseBetFragment.SHARE_BET_RESULT)
                    val type = it.getInt(ChatChooseBetFragment.SHARE_BET_TYPE, 0)
                    val tv = data?.content?.let { ChatMsgUtils.addNoDivideCharInBetShar(it) } ?: ""
                    chatAtHelper.addShareBetSpan(
                        tv,
                        if (type == 0) ChatMsgType.BET_GAME else ChatMsgType.BET_SPORT
                    )
                    mViewModel.currentSelectBetShare = data
                    SoftKeyBoardAnim.etAnimWhenEtContentChange(
                        mBinding,
                        mViewModel.currentKeyBoardType,
                        onAnimStart = {
                            updateInputIcon(it)
                        },
                        onAnimEnd = {
                            updateInputIcon(it)
                        })
                    keyboardChangeClick(KeyBoardType.SOFT_KEYBOARD,6)
                }
            }

            launch {
                mViewModel.emojiFlow.collect {
                    it?.let { addEmojiData(it) }
                }
            }
        }

//        mViewModel.chatHistoryIsEmpty.observe(viewLifecycleOwner) {
//            updateChatUi(mainMatch?.value)
//        }
        //input
        launch(Lifecycle.State.RESUMED) {
            mViewModel.updateKeyboardUiStatus.observe(viewLifecycleOwner) {
                keyboardChangeClick(it, 5)
            }
        }
        softKeyBoardManager.toastLiveData.observe(viewLifecycleOwner) {
            showToast(it)
        }

        mViewModel.etDelLiveData.observe(viewLifecycleOwner) {
            delEtInput()
        }
        mViewModel.sendTextLiveData.observe(viewLifecycleOwner) {
            sendText()
        }
        mViewModel.atLiveData.observe(viewLifecycleOwner) {
            mViewModel.myUid
            chatAtHelper.addAtMentionSpan(it.userName, ChatRefUser(it.uid,it.userName,it.avatarId,it.replaceUserName))
            SoftKeyBoardAnim.etAnimWhenEtContentChange(
                mBinding,
                mViewModel.currentKeyBoardType,
                onAnimStart = {
                    updateInputIcon(it)
                },
                onAnimEnd = {
                    updateInputIcon(it)
                })
            keyboardChangeClick(KeyBoardType.SOFT_KEYBOARD,9)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (mViewModel.currentKeyBoardType != KeyBoardType.CHAT) {
                keyboardChangeClick(KeyBoardType.CHAT, 1)
            } else {
                if (activity == null) {
                    return@addCallback
                }
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        val listener = View.OnClickListener { v ->
            when (v?.id) {
                R.id.iv_bet,
                R.id.iv_bottom_bet -> {
                    toChooseBet()
                }

                else -> {}
            }
        }
//        mBinding.ivAt.setOnClickListener(listener)
//        mBinding.ivBottomAt.setOnClickListener(listener)
        mBinding.ivBet.setOnClickListener(listener)
        mBinding.ivBottomBet.setOnClickListener(listener)
    }

    private fun toChooseBet() {
        findNavController().navigate("walisport://module_betslip/chatChooseBetFragment".deeplink())
    }

    private fun initSoftKeyBoardFragment() {
        initEmojiFragment()
        softKeyBoardManager = SoftKeyboardManager(
            lifecycleScope,
            lifecycle,
            mViewModel.userDataManager,
            mViewModel.chatConfigDao,
            this
        )
        mBinding.main.post {
            mViewModel.keyBoardHeight = mBinding.main.height
            softKeyBoardManager.originMainHeight = mViewModel.keyBoardHeight
            addMainViewListen()
        }
    }

    private fun addMainViewListen() {
        softKeyBoardManager.initView(
            requireActivity().window.decorView,
            mBinding.chatEtInput,
            isMainSoft
        )
        calculationLayoutSize()
//        mBinding.main.viewTreeObserver
//            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    mBinding.main.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                    calculationLayoutSize()
//                }
//            })
    }

    /**
     * 进入直播间不成功时修改
     * */
    fun updateChatUi(liveStart: Boolean, matchStatus: Int) {
        updateChatList()

//        if (matchBean?.liveInfo?.charRoom == true || matchBean == null) {
//            updateChatList()
//            return
//        }
//        //比赛状态 0-已结束 1-推迟 2-中断 3-取消 4-未开赛 5-进行中 6-延迟 7-废弃 8-暂停
//        val code = matchBean?.basicInfo?.status
//        val status = MatchStatus.entries.find { status -> status.code == code }
//        mBinding.also {
//            when (status) {
//                MatchStatus.FINISHED, MatchStatus.CANCELED, MatchStatus.ABANDONED -> {
//                    it.liveChatGroupChat.isVisible = false
//
//                    it.dynamicState.setState(DynamicStateLayout.States.CLOSE,R.string.live_chat_end.getString())
//                }
//                else -> {
//                    it.liveChatGroupChat.isVisible = false
//                    it.dynamicState.setState(DynamicStateLayout.States.DATA_EMPTY,R.string.live_chat_empty.getString())
//                }
//            }
//        }
    }

    private fun updateChatList() {
        mBinding.apply {
            liveChatHistory.isVisible = true
            dynamicState.isVisible = false

//            if (mViewModel.chatHistoryIsEmpty.value == true) {
//                liveChatHistory.isInvisible = true
//                dynamicState.setState(DynamicStateLayout.States.DATA_EMPTY, R.string.live_chat_first_chat.getString())
//            } else {
//                liveChatHistory.isInvisible = false
//                dynamicState.isVisible = false
//            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initInputListener() {
        mBinding.apply {
            ivEmoji.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val clickType =
                        if (softKeyBoardManager.currentKeyBoardType == KeyBoardType.EMOJI) {
                            KeyBoardType.CHAT
                        } else {
                            KeyBoardType.EMOJI
                        }
                    keyboardChangeClick(clickType)
                    chatAtHelper.dismissWindow()
                }
                calculationLayoutSize()
                return@setOnTouchListener true
            }
            chatTvSend.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    sendText()
                }
                return@setOnTouchListener true
            }
//            ivAt.setOnTouchListener { v, event ->
//                if (event.action == MotionEvent.ACTION_DOWN) {
//                    chatAtHelper.shouldOpenAtDialog = true
//                    keyboardChangeClick(KeyBoardType.SOFT_KEYBOARD, 9)
////                    lifecycleScope.launch {
////                        chatAtHelper.addAtInEt()
////                    }
//                }
//                true
//            }
//            ivBottomAt.setOnTouchListener { v, event ->
//                if (event.action == MotionEvent.ACTION_DOWN) {
//                    chatAtHelper.addAtInEt()
//                }
//                true
//            }
//            ivBottomAt.setOnTouchListener { v, event -> return@setOnTouchListener true }
//            ivBottomBet.setOnTouchListener { v, event -> return@setOnTouchListener true }
            ivBottomEmoji.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val clickType =
                        if (softKeyBoardManager.currentKeyBoardType == KeyBoardType.EMOJI) {
                            KeyBoardType.CHAT
                        } else {
                            KeyBoardType.EMOJI
                        }
                    mViewModel.updateKeyBoardUi(clickType, 6)
                    chatAtHelper.dismissWindow()
                }
                return@setOnTouchListener true
            }
            ivLanguage.setOnClickListener {
                showLanguageDialog()
            }
            //监听聚焦事件，不合格的展示软件盘一律拦截
            chatEtInput.setOnFocusChangeListener { v, hasFocus ->
//            如果当前点击事件 softkeyboardlisterner 和 当前状态currentKeyboardListener 一致可以过滤掉聚焦事件
                if (softKeyBoardManager.softKeyboardStatus && !softKeyBoardManager.isSoftKeyboardShow) { //要打开软件盘并且软件盘在收缩中
                    softKeyBoardManager.openSoftKeyBoard()
                }
            }
            //监听点击事件
            chatEtInput.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    keyboardChangeClick(KeyBoardType.SOFT_KEYBOARD)
                }
                return@setOnTouchListener false
            }
        }
    }

    private fun initChatHelper() {
        chatAtHelper = ChatATHelper(
            viewLifecycleOwner.lifecycleScope,
            requireContext(),
            mBinding.chatEtInput
        )
        chatAtHelper.initChatEtInput(mViewModel.languageManager.getLanguage()) {
            sendText()
        }
        chatAtHelper.etWatchListen = {
            SoftKeyBoardAnim.etAnimWhenEtContentChange(
                mBinding,
                softKeyBoardManager.clickKeyBoardType,
                onAnimStart = {
                    updateInputIcon(it)
                },
                onAnimEnd = {
                    updateInputIcon(it)
                })
        }
    }


    fun closeChatWebsocket() {
        mViewModel.disConnectChatServer()
    }

    /**
     * 直播间view被截取了statusBarHeight的高度
     * */
    private fun getStatusBarHeight(view: View): Int {
        val windowInsetsCompat = ViewCompat.getRootWindowInsets(view)
        val topInset = windowInsetsCompat?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
        //topInset比statusBarHeight准确（ROG手机）
        val ret = if (topInset == 0) ImmersionBar.getStatusBarHeight(view.context) else topInset
        //"topInset:$topInset,ret:$ret".logd()
        return ret
    }

    private fun calculationLayoutSize() {
        mBinding.apply {
            softKeyBoardManager.emojiKeyBoardHeight = 242.dp2px
//            if (mViewModel.isMainSoft) { //直播间不做设置
//                inputMain.layoutParams.height = mViewModel.keyBoardHeight
//                main.layoutParams.height =
//                    mViewModel.keyBoardHeight + softKeyBoardManager.emojiKeyBoardHeight
//                main.requestLayout()
//            }
        }
    }

    /**
     * 判断键盘是否在显示中
     * */
    fun isSoftKeyboardVisible(): Boolean {
        val flag = mViewModel.currentKeyBoardType != KeyBoardType.CHAT
        if (flag) {
            keyboardChangeClick(KeyBoardType.CHAT, 2)
        }
        return flag
    }

    private fun showLanguageDialog() {
        val viewLocation = IntArray(2)
        mBinding.ivLanguage.getLocationOnScreen(viewLocation)

        ChatLanguageDialogFragment.newInstance(
            viewLocation[0],
            viewLocation[1],
            mViewModel.languageSelectPosition
        ) {
            mViewModel.updateLanguageSelect(it)
        }.show(childFragmentManager)
    }

    /**
     * 发送消息
     * */
    private fun sendText() {
        chatAtHelper.dismissWindow()
        if (mBinding.chatEtInput.text == null || mBinding.chatEtInput.length() == 0) {
            return
        }
        keyboardChangeClick(KeyBoardType.CHAT, 7)
        mViewModel.createLocalMsg(mBinding.chatEtInput.text!!, chatType)
            ?.let { mViewModel.sendMsgToChat(it) }
        mBinding.chatEtInput.text?.clear()
    }

    /**
     * 直播间因为要做滑动，所以每次弹出后要对高度重新设置下
     * */
    private fun emojiLayoutSize(isReset: Boolean) {
//        if (mViewModel.isMainSoft) { //首页不做操作
//            return
//        }
        mBinding.apply {
            llContent.layoutParams.height =
                if (isReset) LayoutParams.MATCH_PARENT else mViewModel.keyBoardHeight
            main.layoutParams.height =
                if (isReset) LayoutParams.MATCH_PARENT else mViewModel.keyBoardHeight + softKeyBoardManager.emojiKeyBoardHeight + 40.dp2px
            main.requestLayout()
//            lifecycleScope.launch {
//                delay(500)
//                "emojiLayoutSize isReset $isReset  main ${main.height}    mainTranslationY ${main.translationY} keyBoardHeight ${mViewModel.keyBoardHeight}".logd(
//                    "aaa"
//                )
//            }
        }
    }

    /**
     * 展示聊天界面
     * */
    fun showChat() {
        chatAtHelper.dismissWindow()
        updateKeyboardView(KeyBoardType.CHAT)
        emojiLayoutSize(true)
        emojiPopupListen?.invoke(false)
        mViewModel.listenCurrentKeyBoardType(KeyBoardType.CHAT)

    }

    /**
     * 展示软件盘
     * */
    private fun showSoftKeyBoard() {
        updateKeyboardView(KeyBoardType.SOFT_KEYBOARD)
        emojiLayoutSize(false)
        emojiPopupListen?.invoke(true)
        mViewModel.listenCurrentKeyBoardType(KeyBoardType.SOFT_KEYBOARD)
    }

    /**
     * 展示表情界面
     * */
    private fun showEmoji() {
        updateKeyboardView(KeyBoardType.EMOJI)
//        softKeyBoardManager.etRequestFocus()
        emojiLayoutSize(false)
        emojiPopupListen?.invoke(true)
        mViewModel.listenCurrentKeyBoardType(KeyBoardType.EMOJI)
    }

    private fun updateKeyboardView(keyBoardType: KeyBoardType) {
        mBinding.topLine.isVisible = keyBoardType != KeyBoardType.CHAT
    }

    private fun updateInputIcon(isVisible: Boolean) {
        mBinding.apply {
//            ivAt.isVisible =  isVisible

            ivBet.isVisible = true //isVisible
            ivEmoji.isVisible = true //isVisible
            ivLanguage.isVisible = isVisible
            ivBottomBet.isVisible = false //!isVisible
            ivBottomEmoji.isVisible = false //!isVisible
        }
    }

    override fun keyboardChangeClick(keyBoardType: KeyBoardType, flag: Int) {
//        "keyboardChangeClick keyBoardType:$keyBoardType,currentKeyBoardType:${mViewModel.currentKeyBoardType},flag:$flag".logd(
//            "aaa"
//        )
        if (keyBoardType == mViewModel.currentKeyBoardType) {
            return
        }
//        if (!mViewModel.isMainSoft) {
//            val flag1 = !mViewModel.checkSoftKeyboardVisible()
//            if (keyBoardType != KeyBoardType.CHAT && flag1) {
//                softKeyBoardManager.checkSoftKeyBoardBetAmount(
//                    mViewModel.checkBetAmountFlow.value,
//                    keyBoardType,
//                    flag
//                )
//                return
//            }
//        }
        mBinding.ivEmoji.setImageResource(if (keyBoardType == KeyBoardType.EMOJI) R.drawable.icon_emoji_color else R.drawable.icon_emoji_grey)

        softKeyBoardManager.addSoftKeyBoardEvent(keyBoardType, flag)
        softKeyBoardManager.showKeyboardAnimation()
    }

    override fun changeKeyboardUi(keyBoardType: KeyBoardType) {
        when (keyBoardType) {
            KeyBoardType.SOFT_KEYBOARD -> showSoftKeyBoard()
            KeyBoardType.EMOJI -> showEmoji()
            KeyBoardType.CHAT -> showChat()
        }
        softKeyBoardManager.updateKeyBoard()
    }

    /**
     * 实时更新chatViewModel的currentKeyBoardType
     * */
    override fun updateChatKeyboardType(keyBoardType: KeyBoardType) {
        mViewModel.currentKeyBoardType = keyBoardType
    }

    /**
     *表情动画输入框动画
     * */
    override fun startAnim(
        actionType: KeyboardActionType,
        offset: Int,
        onStart: () -> Unit,
        onEnd: () -> Unit
    ) {
        softKeyBoardManager.apply {
            mainAnim = AnimatorSet()
            val mainTransYAnim = SoftKeyBoardAnim.mainTransYAnim(offset, mBinding.main)
            val emojiSet = AnimatorSet().apply {
                inputIconShouldUpdate(actionType, call = { // 软件盘和表情键盘互相切换时表情按钮动画不播放
                    mBinding.apply {
                        if (chatEtInput.length() == 0) { //有内容时input按钮不能上下移动
                            playTogether(
                                getInputAnim(
                                    actionType,
                                    offset,
                                    ivAt,
                                    ivBet,
                                    ivEmoji,
                                    ivLanguage,
                                    chatLlInput,
                                    animStart = {
                                        updateInputIcon(true)
                                    },
                                    animEnd = {
                                        updateInputIcon(offset == 0)
                                    }
                                ),
                                mainTransYAnim
                            )
                        } else {
                            play(mainTransYAnim)
                        }
                    }
                }, elCall = {
                    play(mainTransYAnim)
                })
            }
            mainAnim?.play(emojiSet)
            mainAnim?.duration = 200L
            mainAnim?.addListener(onStart = {
                if (mBinding.chatEtInput.length() == 0) {
                    inputIconShouldUpdate(
                        actionType,
                        call = {//键盘切换动画开始时除了软件盘和表情键盘互相切换外，其他键盘切换会有键盘按钮动画
                        })
                }
                onStart.invoke()
            }, onEnd = {
                if (mBinding.chatEtInput.length() == 0) {
                    inputIconShouldUpdate(actionType, call = { //键盘切换动画结束时，根据键盘弹出和缩放判断是否要隐藏输入框按钮
                    })
                }
                onEnd.invoke()
//                if (chatAtHelper.shouldOpenAtDialog) { //如果点击了输入框@btn，动画完成后添加@到输入框框
//                    chatAtHelper.shouldOpenAtDialog = false
//                    lifecycleScope.launch {
//                        delay(500)
//                        chatAtHelper.addAtInEt()
//                    }
//                }
            })
            if (isFirstOpen) {
                mainAnim?.startDelay = 200L
            }
            mainAnim?.start()
        }
    }

    override fun getMainHeight(): Int {
        return mBinding.main.height
    }


    private fun delEtInput() {
        mBinding.chatEtInput.apply {
            if (text?.length == 0) {
                return@apply
            }
            dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
            dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
            softKeyBoardManager.etRequestFocus()
        }
    }

    /**
     * 添加表情数据
     * */
    private fun addEmojiData(emojiData: EmojiModel) {
        val emojiPattern: Pattern = Pattern.compile(BID_EMOJI_REGEX)
        if (emojiPattern.matcher(emojiData.key).find()) {
//            keyboardChangeClick(KeyBoardType.CHAT, 5)
            mViewModel.createBidLocalMsg(emojiData.key, chatType)
                ?.let { mViewModel.sendMsgToChat(it) }
            return
        }
        chatAtHelper.dismissWindow() // 输入表情后at弹框消失
        mBinding.chatEtInput.apply {
            if (selectionStart < 0) text?.append(emojiData.key) else text?.insert(
                selectionStart,
                emojiData.key
            )
        }
        softKeyBoardManager.etRequestFocus()
    }


}