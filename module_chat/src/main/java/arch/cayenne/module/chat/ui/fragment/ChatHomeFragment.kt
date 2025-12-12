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
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.FragmentExt.setFragmentResultListener
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.MsgType
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavResultExt.observeResult
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.LiveMatchBean
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
import arch.cayenne.module.chat.manager.SoftKeyBoardAnim.etInputContentAnim
import arch.cayenne.module.chat.manager.SoftKeyBoardAnim.getInputAnim
import arch.cayenne.module.chat.manager.SoftKeyBoardAnim.inputIconAnim

//聊天
class ChatHomeFragment : BaseFragment<ChatHomeViewModel, FragmentLiveChatBinding>(),
    SoftKeyBoardMangerListener {
    override val vbClass: KClass<FragmentLiveChatBinding> = FragmentLiveChatBinding::class
    override val vmClass: KClass<ChatHomeViewModel> = ChatHomeViewModel::class
    var mainMatch: LiveData<LiveMatchBean>? = null
    var matchIdLiveData: LiveData<Long>? = null
    private lateinit var softKeyBoardManager: SoftKeyboardManager
    private lateinit var chatAtHelper: ChatATHelper


    override fun initView(savedInstanceState: Bundle?) {
        initChatPageFragment()
        initSoftKeyBoardFragment()
        initInputListener()
        initHotRecycler()
        initChatHelper()

        arguments?.let { //TODO  首页过来的 之后需要处理聊天室要matchId的问题
            val value = it.getBoolean("chat", false)
            setMainChatStatus()
        }
    }

    /**
     * 直播间调用
     * */
    fun setMatchLiveData(matchId: LiveData<Long>?, mainMatch: LiveData<LiveMatchBean>?) {
        matchIdLiveData = matchId
        this.mainMatch = mainMatch
    }

    /**
     * 首页调用
     * */
    private fun setMainChatStatus() {
        lifecycleScope.launchWhenResumed {
            observeMatchId(-1)
            observeLiveMatch(null)
        }
    }

    private fun observeMatchId(matchId: Long) {
        mViewModel.setArguments(matchId)
    }

    private fun observeLiveMatch(match: LiveMatchBean?) {
        mViewModel.isMainSoft = match == null
        updateChatUi(match)
        //比赛开始后开启聊天服务
        if (match?.liveInfo?.charRoom == true || match == null) {
            mViewModel.startChatServer()
        }
    }


    override fun onStart() {
        StatusBarConfig.statusBarType =
            StatusBarMode.DRAW_BEHIND(autoPadding = false, autoIsNavigation = true)
        setStatusBar(StatusBarConfig, mBinding.root)
        super.onStart()
        mViewModel.setSoftConfig(false)
    }

    override fun onStop() {
        mViewModel.leaveRoom()
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
                        mViewModel.chatLogin()
                    }
                }
            }
            launch {
                mViewModel.sendMsgToServerFlow.collect {

                }
            }
        }
        matchIdLiveData?.observe(viewLifecycleOwner) {
            observeMatchId(it)
        }
        mainMatch?.observe(viewLifecycleOwner) {
            observeLiveMatch(it)
        }
        mViewModel.chatHistoryIsEmpty.observe(viewLifecycleOwner) {
            updateChatUi(mainMatch?.value)
        }
        //input
        launch(Lifecycle.State.RESUMED) {
            mViewModel.updateKeyboardUiStatus.observe(viewLifecycleOwner) {
                keyboardChangeClick(it, 5)
            }
        }
        softKeyBoardManager.toastLiveData.observe(viewLifecycleOwner) {
            showToast(it)
        }
        mViewModel.emojiLiveData.observe(viewLifecycleOwner) {
            addEmojiData(it)
        }
        mViewModel.etDelLiveData.observe(viewLifecycleOwner) {
            delEtInput()
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
        mBinding.ivBet.setOnClickListener {
            toChooseBet()
        }
        mBinding.ivBottomBet.setOnClickListener {
            toChooseBet()

        }
        observeResult<Bundle>("choose_bet") {
            val type = it.getInt("key")
            val text =
                if (type == 0) "#游戏订单:D1k19[赢100x,\$9331]" else "#体育订单:D1k19[赢100x,\$9331]"
            chatAtHelper.addShareBetSpan(
                text,
                if (type == 0) MsgType.BET_GAME else MsgType.BET_SPORT
            )
            addBetToEtInputAnim()
        }
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

    private fun initHotRecycler() {
        mBinding.hotRecyclerview.also {
            it.layoutManager =
                LinearLayoutManager(it.context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = EmojiHotItemAdapter()
            adapter.submitList(mViewModel.getHotRecycler())
            it.adapter = adapter
        }
    }

    private fun initEmojiFragment() {
        val fragment = EmojiHomeFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.chat_keyboard, fragment, EmojiHomeFragment.TAG).commit()
    }

    private fun initChatPageFragment() {
        val fragment = ChatPageFragment()
        childFragmentManager.beginTransaction()
            .replace(mBinding.liveChatHistory.id, fragment, ChatPageFragment.TAG)
            .commit()
    }

    private fun addMainViewListen() {
        softKeyBoardManager.initView(
            requireActivity().window.decorView,
            mBinding.chatEtInput,
            mViewModel.isMainSoft
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
    fun updateChatUi(matchBean: LiveMatchBean? = null) {
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
                    keyboardChangeClick(KeyBoardType.EMOJI)
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

//            ivBottomAt.setOnTouchListener { v, event -> return@setOnTouchListener true }
//            ivBottomBet.setOnTouchListener { v, event -> return@setOnTouchListener true }
            ivBottomEmoji.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    mViewModel.updateKeyBoardUi(KeyBoardType.EMOJI, 6)
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
            etContentChangeAnim()
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
            softKeyBoardManager.emojiKeyBoardHeight =
                if (mViewModel.isMainSoft) 242.dp2px else 242.dp2px
//            chatKeyboard.layoutParams.height = softKeyBoardManager.emojiKeyBoardHeight
//            inputContent.translationY = 44.dp2px.toFloat()
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
            viewLocation[1]
        ).show(childFragmentManager)
    }

    /**
     * 发送消息
     * */
    private fun sendText() {
        if (mBinding.chatEtInput.text == null || mBinding.chatEtInput.length() == 0) {
            return
        }
        keyboardChangeClick(KeyBoardType.CHAT, 7)
        mViewModel.createLocalMsg(mBinding.chatEtInput.text!!)?.let { mViewModel.sendMsgToChat(it) }
        mBinding.chatEtInput.text?.clear()
    }

    /**
     * 直播间因为要做滑动，所以每次弹出后要对高度重新设置下
     * */
    private fun emojiLayoutSize(isReset: Boolean) {
        if (mViewModel.isMainSoft) {
            return
        }
        mBinding.apply {
            val height = main.layoutParams.height
            inputMain.layoutParams.height =
                if (isReset) LayoutParams.MATCH_PARENT else mViewModel.keyBoardHeight
            main.layoutParams.height =
                if (isReset) LayoutParams.MATCH_PARENT else mViewModel.keyBoardHeight + softKeyBoardManager.emojiKeyBoardHeight
            main.requestLayout()
        }
    }

    /**
     * 展示聊天界面
     * */
    fun showChat() {
        chatAtHelper.dismissWindow()
        updateKeyboardView(false)
//        updateKeyboardView(mBinding.chatEtInput.text.isNotEmpty())
        emojiLayoutSize(true)
    }

    /**
     * 展示软件盘
     * */
    private fun showSoftKeyBoard() {
        updateKeyboardView(true)
        emojiLayoutSize(false)
    }

    /**
     * 展示表情界面
     * */
    private fun showEmoji() {
        updateKeyboardView(true)
//        softKeyBoardManager.etRequestFocus()
        emojiLayoutSize(false)
    }

    private fun updateKeyboardView(isVisible: Boolean) {
        mBinding.apply {
        }
    }

    private fun updateInputIcon(isVisible: Boolean) {
        mBinding.apply {
            ivAt.isVisible = isVisible
            ivBet.isVisible = isVisible
            ivEmoji.isVisible = isVisible
            ivLanguage.isVisible = isVisible
            ivBottomAt.isVisible = !isVisible
            ivBottomBet.isVisible = !isVisible
            ivBottomEmoji.isVisible = !isVisible
        }
    }

    override fun keyboardChangeClick(keyBoardType: KeyBoardType, flag: Int) {
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

    @SuppressLint("Recycle")
    private fun etContentChangeAnim() {
        mBinding.apply {
            when {
                chatEtInput.length() == 0 && chatTvSend.isVisible && mViewModel.currentKeyBoardType != KeyBoardType.CHAT -> { //键盘弹出的时候发送 有内容到无内容
                    etInputContentAnim(
                        true,
                        mViewModel.currentKeyBoardType,
                        chatLlInput,
                        chatTvSend
                    ).apply {
                        duration = 170L
                        addListener(onEnd = {
                            chatTvSend.isVisible = false
                        })
                        start()
                    }
                }

                chatEtInput.length() == 0 && chatTvSend.isVisible && mViewModel.currentKeyBoardType == KeyBoardType.CHAT -> {//键盘收缩的时候发送，有内容到无内容
//                    input输入框扩展 -> 按钮动画
                    val btnAnim = AnimatorSet().apply {
                        playTogether(
                            *SoftKeyBoardAnim.inputIconAnim(
                                true,
                                ivAt,
                                ivBet,
                                ivEmoji,
                                ivLanguage
                            )
                        )
                        addListener(onStart = {
                            updateInputIcon(true)
                        })
                    }
                    val inputAnim = etInputContentAnim(
                        true,
                        mViewModel.currentKeyBoardType,
                        chatLlInput,
                        chatTvSend
                    ).apply {
                        addListener(onEnd = {
                            chatTvSend.isVisible = false
                        })
                    }
                    AnimatorSet().apply {
                        duration = 170
                        playSequentially(inputAnim, btnAnim)
                        start()
                    }

                }

                chatEtInput.length() > 0 && !chatTvSend.isVisible -> { //无内容到有内容
                    etInputContentAnim(
                        false,
                        mViewModel.currentKeyBoardType,
                        chatLlInput,
                        chatTvSend
                    ).apply {
                        duration = 170L
                        addListener(onStart = {
                            chatTvSend.isVisible = true
                        })
                        start()
                    }
                }

                else -> {

                }
            }
        }

    }

    /**
     * 游戏注单中插入
     * **/
    private fun addBetToEtInputAnim() {
        mBinding.apply {
            val btnAnim = AnimatorSet().apply {
                playTogether(
                    *inputIconAnim(
                        true,
                        ivAt,
                        ivBet,
                        ivEmoji,
                        ivLanguage
                    )
                )
                addListener(onStart = {
                    updateInputIcon(true)
                }, onEnd = {
                    updateInputIcon(false)
                })
            }
            val inputEtAnim =
                etInputContentAnim(false, mViewModel.currentKeyBoardType, chatLlInput, chatTvSend)
            val animSet = AnimatorSet()
            animSet.playSequentially(btnAnim, inputEtAnim)
            animSet.duration = 170L
            animSet.addListener(onStart = {
                chatTvSend.isVisible = true
            })
            animSet.start()
        }
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
                duration = 170L
                inputIconShouldUpdate(actionType, call = {
                    mBinding.apply {
                        if (chatEtInput.length() == 0) { //有内容时input按钮不能上下移动
                            playTogether(
                                mainTransYAnim,
                                getInputAnim(
                                    actionType,
                                    offset,
                                    ivAt,
                                    ivBet,
                                    ivEmoji,
                                    ivLanguage,
                                    chatLlInput
                                )
                            )
                        } else {
                            play(mainTransYAnim)
                        }
                    }
                }, elCall = {
                    play(mainTransYAnim)
                })
            }
            //TODO 测试键盘切换anim
            mainAnim?.play(emojiSet)
//            if (offset == 0)
//                mainAnim?.playSequentially(emojiSet, hotViewAnim(offset))
//            else
//                mainAnim?.playSequentially(hotViewAnim(offset), emojiSet)
//            mainAnim?.duration = 170L

            mainAnim?.addListener(onStart = {
                inputIconShouldUpdate(actionType, call = {
                    updateInputIcon(true) //初始动画时要input bt可见
                })

                onStart.invoke()
            }, onEnd = {
                inputIconShouldUpdate(actionType, call = {
                    updateInputIcon(offset == 0) //根据上下移判断是否隐藏input bt
                })
                onEnd.invoke()
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

    //防止软件盘和表情键盘切换的时候跳动
    private fun inputIconShouldUpdate(
        animationType: KeyboardActionType,
        call: () -> Unit,
        elCall: (() -> Unit)? = null
    ) {
        if (animationType in arrayOf(
                KeyboardActionType.CHAT_TO_SOFT,
                KeyboardActionType.CHAT_TO_EMOJI,
                KeyboardActionType.EMOJI_TO_CHAT,
                KeyboardActionType.SOFT_TO_CHAT
            )
        ) { //chat 和 键盘切换时@ 注单 emoji 等按钮需要上下移动
            call.invoke()
        } else {
            elCall?.invoke()
        }
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
            keyboardChangeClick(KeyBoardType.CHAT, 5)
            mViewModel.createBidLocalMsg(emojiData.key)?.let { mViewModel.sendMsgToChat(it) }
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