package arch.cayenne.module.chat.ui.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.websocket.data.SocketConnectState
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.module.chat.data.constants.KeyboardActionType
import arch.cayenne.module.chat.databinding.FragmentLiveChatBinding
import arch.cayenne.module.chat.manager.SoftKeyboardManager
import arch.cayenne.module.chat.manager.interf.SoftKeyBoardMangerListener
import arch.cayenne.module.chat.ui.adapter.EmojiHotItemAdapter
import arch.cayenne.module.chat.ui.viewmodel.ChatHomeViewModel
import arch.cayenne.module.chat.utils.EmojiEditFilter
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

//聊天
class ChatHomeFragment : BaseFragment<ChatHomeViewModel, FragmentLiveChatBinding>(),
    SoftKeyBoardMangerListener {
    override val vbClass: KClass<FragmentLiveChatBinding> = FragmentLiveChatBinding::class
    override val vmClass: KClass<ChatHomeViewModel> = ChatHomeViewModel::class
    var mainMatch: LiveData<LiveMatchBean>? = null
    var matchIdLiveData: LiveData<Long>? = null
    private lateinit var softKeyBoardManager: SoftKeyboardManager

    override fun initView(savedInstanceState: Bundle?) {
        initChatPageFragment()
        initSoftKeyBoardFragment()
        arguments?.let { //TODO  首页过来的 之后需要处理聊天室要matchId的问题
            val value = it.getBoolean("chat", false)
            setMainChatStatus()
        }
    }

    override fun onStart() {
//        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
//        setStatusBar(StatusBarConfig, mBinding.root)
        super.onStart()
        mViewModel.setSoftConfig(false)
        mBinding.main.post {
            mViewModel.keyBoardHeight = mBinding.main.height
            setMainHeight()
        }
    }

    override fun onStop() {
        mViewModel.leaveRoom()
        super.onStop()
        mViewModel.setSoftConfig(true)
    }


    override fun onFragmentAnimEnd(isEnter: Boolean) {
        super.onFragmentAnimEnd(isEnter)
        keyboardChangeClick(KeyBoardType.CHAT,4)
    }

    override fun onResume() {
        super.onResume()
        //软件盘时获取的高度有误，onResume时获取固定值
//        mBinding.liveChatKeyboard.translationY = -62.dp2px.toFloat()
    }

    override fun onPause() {
        super.onPause()
        keyboardChangeClick(KeyBoardType.CHAT, 3)//移动到其他页面后关闭软件盘表情键盘
    }

    private fun setMainHeight() {
        softKeyBoardManager.initView(
            requireActivity().window.decorView,
            mBinding.chatEtInput,
            mViewModel.isMainSoft
        )
        addMainViewListen()
    }

    override suspend fun createObserver() {
        matchIdLiveData?.observe(viewLifecycleOwner) {
            observeMatchId(it)
        }
        mainMatch?.observe(viewLifecycleOwner) {
            observeLiveMatch(it)
        }
        mViewModel.chatHistoryIsEmpty.observe(viewLifecycleOwner) {
            updateChatUi(mainMatch?.value)
        }
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
        //input
        launch(Lifecycle.State.RESUMED) {
            mViewModel.updateKeyboardUiStatus.observe(viewLifecycleOwner) {
                keyboardChangeClick(it, 5)
            }
        }
        softKeyBoardManager.toastLiveData.observe(viewLifecycleOwner) {
            showToast(it)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {

//        mBinding.liveChatHistory.setOnTouchListener { v, event ->
//            if (event.action == MotionEvent.ACTION_DOWN) {
//                if (mViewModel.currentKeyBoardType != KeyBoardType.CHAT) {
//                    keyboardChangeClick(KeyBoardType.CHAT, 0)
//                }
//            }
//            return@setOnTouchListener false
//        }

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
    }

    private fun initSoftKeyBoardFragment() {

        softKeyBoardManager = SoftKeyboardManager(
            lifecycleScope,
            lifecycle,
            mViewModel.userDataManager,
            mViewModel.chatConfigDao,
            this
        )
        initEmojiFragment()
        initInputListener()
        initHotRecycler()
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

    /**
     * 显示聊天界面时隐藏键盘界面
     * */
//    private fun showChat(flag: Int) {
//        mViewModel.updateKeyBoardUi(KeyBoardType.CHAT, flag)
//    }

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

    fun closeChatWebsocket() {
        mViewModel.disConnectChatServer()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initInputListener() {

        mBinding.apply {
            ivEmoji.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    keyboardChangeClick(KeyBoardType.EMOJI)
                }
                return@setOnTouchListener true
            }
            chatTvSend.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    sendText()
                }
                return@setOnTouchListener true
            }

            ivBottomAt.setOnTouchListener { v, event -> return@setOnTouchListener true }
            ivBottomBet.setOnTouchListener { v, event -> return@setOnTouchListener true }
            ivBottomEmoji.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    mViewModel.updateKeyBoardUi(KeyBoardType.EMOJI, 6)
                }
                return@setOnTouchListener true
            }
            ivLanguage.setOnClickListener {
                showLanguageDialog()
            }
            chatEtInput.apply {
                //设置发送按钮
                imeOptions = EditorInfo.IME_ACTION_SEND
                setImeActionLabel(
                    SkinnableResourceManager.getString(
                        requireContext(),
                        R.string.live_chat_send,
                        mViewModel.languageManager.getLanguage()
                    ), EditorInfo.IME_ACTION_SEND
                )
                setOnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        sendText()
                        return@setOnEditorActionListener true
                    }
                    return@setOnEditorActionListener false
                }
                //输入拦截
                filters = arrayOf(EmojiEditFilter())
                //监听聚焦事件，不合格的展示软件盘一律拦截
                setOnFocusChangeListener { v, hasFocus ->
//            如果当前点击事件 softkeyboardlisterner 和 当前状态currentKeyboardListener 一致可以过滤掉聚焦事件
                    if (softKeyBoardManager.softKeyboardStatus && !softKeyBoardManager.isSoftKeyboardShow) { //要打开软件盘并且软件盘在收缩中
                        softKeyBoardManager.openSoftKeyBoard()
                    }
                }
                //监听点击事件
                setOnTouchListener { v, event ->
                    keyboardChangeClick(KeyBoardType.SOFT_KEYBOARD)
                    return@setOnTouchListener true
                }
            }
        }
    }

    private fun showLanguageDialog() {
        ChatLanguageDialogFragment.newInstance(
            mBinding.ivLanguage.x.toInt(),
            mBinding.ivLanguage.y.toInt()
        ).show(childFragmentManager)
    }

    /**
     * 发送消息
     * */
    private fun sendText() {
        val text: String = mBinding.chatEtInput.text?.toString() ?: ""
        if (text.isEmpty()) {
            return
        }
        mBinding.chatEtInput.text?.clear()
        keyboardChangeClick(KeyBoardType.CHAT, 7)
        mViewModel.sendMsgToChat(text)
    }

    private fun addMainViewListen() {
        mBinding.screenContent.viewTreeObserver
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mBinding.main.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    calculationLayoutSize()
                }
            })
    }

    private fun calculationLayoutSize() {
        mBinding.apply {
            softKeyBoardManager.emojiKeyBoardHeight = 242.dp2px
            chatKeyboard.layoutParams.height = softKeyBoardManager.emojiKeyBoardHeight
            inputMain.layoutParams.height = mViewModel.keyBoardHeight
            screenContent.layoutParams.height =
                mViewModel.keyBoardHeight + softKeyBoardManager.emojiKeyBoardHeight
            mBinding.screenContent.requestLayout()
        }
    }

    /**
     * 展示聊天界面
     * */
    fun showChat() {
        updateKeyboardView(false)
//        updateKeyboardView(mBinding.chatEtInput.text.isNotEmpty())
    }

    /**
     * 展示软件盘
     * */
    private fun showSoftKeyBoard() {
        updateKeyboardView(true)
    }

    /**
     * 展示表情界面
     * */
    private fun showEmoji() {
        updateKeyboardView(true)
        softKeyBoardManager.etRequestFocus()
    }

    private fun updateKeyboardView(isVisible: Boolean) {
        mBinding.apply {
//            emojiContent.isInvisible = !isVisible
            ivLanguage.isVisible = !isVisible
//            ivAt.isVisible = !isVisible
//            ivBet.isVisible = !isVisible
//            ivEmoji.isVisible = !isVisible
//            chatTvSend.isVisible = isVisible
        }
    }

    private fun updateInputIcon(isVisible: Boolean) {
        mBinding.apply {
            ivAt.isVisible = isVisible
            ivBet.isVisible = isVisible
            ivEmoji.isVisible = isVisible
            ivBottomAt.isVisible = !isVisible
            ivBottomBet.isVisible = !isVisible
            ivBottomEmoji.isVisible = !isVisible
            chatTvSend.isVisible = !isVisible
        }
    }

    override fun keyboardChangeClick(keyBoardType: KeyBoardType, flag: Int) {
        if (!mViewModel.isMainSoft) {
            val flag1 = !mViewModel.checkSoftKeyboardVisible()
            if (keyBoardType != KeyBoardType.CHAT && flag1) {
                softKeyBoardManager.checkSoftKeyBoardBetAmount(
                    mViewModel.checkBetAmountFlow.value,
                    keyBoardType,
                    flag
                )
                return
            }
        }

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

    override fun startAnim(
        animationType: KeyboardActionType,
        offset: Int,
        onStart: () -> Unit,
        onEnd: () -> Unit
    ) {

//        "panelAnimateTo  offset $offset ".logd("aaa")
        softKeyBoardManager.apply {
            mainAnim = AnimatorSet()
            val mainTransYAnim =
                ObjectAnimator.ofFloat(mBinding.main, "translationY", offset.toFloat())
            mainTransYAnim?.interpolator = FastOutSlowInInterpolator()

            val emojiSet = AnimatorSet().apply {
                duration = 170L
                inputIconShouldUpdate(animationType, call = {
                    playTogether(mainTransYAnim, *inputIconAnim(offset))
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
                inputIconShouldUpdate(animationType, call = {
                    updateInputIcon(true)
                })
                onStart.invoke()
            }, onEnd = {
                inputIconShouldUpdate(animationType, call = {
                    updateInputIcon(offset == 0)
                })
                onEnd.invoke()
            })
            if (isFirstOpen) {
                mainAnim?.startDelay = 200L
            }
            mainAnim?.start()
        }

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
        ) {
            call.invoke()
        } else {
            elCall?.invoke()
        }
    }

    private fun hotViewAnim(offset: Int) = ObjectAnimator.ofFloat(
        mBinding.inputContent,
        "translationY",
        if (offset != 0) 0f else 42.dp2px.toFloat()
    ).apply {
        duration = 30
    }

    private fun inputIconAnim(offset: Int): Array<ObjectAnimator> {

        val atTransYParam = if (offset == 0) 0f else 47.dp2px.toFloat()

        val atTransXParam = if (offset == 0) 0f else 5.dp2px.toFloat()
        val betTransXParam = if (offset == 0) 0f else 12.dp2px.toFloat()
        val emojiTransXParam = if (offset == 0) 0f else 18.dp2px.toFloat()

        val scaleParam = if (offset == 0) floatArrayOf(1.16f, 1f) else floatArrayOf(1f, 1.16f)

        val atTransXAnim = ObjectAnimator.ofFloat(mBinding.ivAt, "translationX", atTransXParam)
        val atTransYAnim = ObjectAnimator.ofFloat(mBinding.ivAt, "translationY", atTransYParam)
        val atScaleXParam = ObjectAnimator.ofFloat(mBinding.ivAt, "scaleX", *scaleParam)
        val atScaleYParam = ObjectAnimator.ofFloat(mBinding.ivAt, "scaleY", *scaleParam)

        val betTransXAnim = ObjectAnimator.ofFloat(mBinding.ivBet, "translationX", betTransXParam)
        val betTransYAnim = ObjectAnimator.ofFloat(mBinding.ivBet, "translationY", atTransYParam)
        val betScaleXParam = ObjectAnimator.ofFloat(mBinding.ivBet, "scaleX", *scaleParam)
        val betScaleYParam = ObjectAnimator.ofFloat(mBinding.ivBet, "scaleY", *scaleParam)

        val emojiTransXAnim =
            ObjectAnimator.ofFloat(mBinding.ivEmoji, "translationX", emojiTransXParam)
        val emojiTransYAnim =
            ObjectAnimator.ofFloat(mBinding.ivEmoji, "translationY", atTransYParam)
        val emojiScaleXParam = ObjectAnimator.ofFloat(mBinding.ivEmoji, "scaleX", *scaleParam)
        val emojiScaleYParam = ObjectAnimator.ofFloat(mBinding.ivEmoji, "scaleY", *scaleParam)


        return arrayOf(
            atTransXAnim,
            atTransYAnim,
            atScaleXParam,
            atScaleYParam,
            betTransXAnim,
            betTransYAnim,
            betScaleXParam,
            betScaleYParam,
            emojiTransXAnim,
            emojiTransYAnim,
            emojiScaleXParam,
            emojiScaleYParam
        )
    }


}