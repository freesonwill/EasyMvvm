package arch.cayenne.module.chat.ui.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import androidx.core.animation.addListener
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.module.chat.data.constants.KeyboardActionType
import arch.cayenne.module.chat.databinding.FragmentLiveSoftkeyboardLayoutBinding
import arch.cayenne.module.chat.manager.SoftKeyboardManager
import arch.cayenne.module.chat.manager.interf.SoftKeyBoardMangerListener
import arch.cayenne.module.chat.ui.adapter.EmojiHotItemAdapter
import arch.cayenne.module.chat.ui.viewmodel.ChatHomeViewModel
import arch.cayenne.module.chat.ui.viewmodel.SoftKeyboardViewModel
import arch.cayenne.module.chat.utils.EmojiEditFilter
import kotlin.reflect.KClass


class SoftKeyboardFragment :
    BaseFragment<SoftKeyboardViewModel, FragmentLiveSoftkeyboardLayoutBinding>(),
    SoftKeyBoardMangerListener {
    override val vbClass: KClass<FragmentLiveSoftkeyboardLayoutBinding>
        get() = FragmentLiveSoftkeyboardLayoutBinding::class
    override val vmClass: KClass<SoftKeyboardViewModel>
        get() = SoftKeyboardViewModel::class
    private val chatViewModel: ChatHomeViewModel by sharedViewModel<ChatHomeViewModel, ChatHomeFragment>()
    private lateinit var softKeyBoardManager: SoftKeyboardManager


    override fun initView(savedInstanceState: Bundle?) {
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
            .replace(R.id.emoji_content, fragment, EmojiHomeFragment.TAG).commit()
    }

    override suspend fun createObserver() {
        launch(Lifecycle.State.RESUMED) {
            chatViewModel.updateKeyboardUiStatus.observe(viewLifecycleOwner) {
                keyboardChangeClick(it, 5)
            }
        }
        softKeyBoardManager.toastLiveData.observe(viewLifecycleOwner) {
            showToast(it)
        }
        chatViewModel.chatHeightLiveData.observe(viewLifecycleOwner) {
            mViewModel.keyBoardHeight = it
            softKeyBoardManager.initView(
                requireActivity().window.decorView,
                mBinding.chatEtInput,
                chatViewModel.isMainSoft
            )
            addMainViewListen()
        }
    }


    override fun keyboardChangeClick(keyBoardType: KeyBoardType, flag: Int) {
        if (!chatViewModel.isMainSoft) {
            val flag1 = !chatViewModel.checkSoftKeyboardVisible()
            if (keyBoardType != KeyBoardType.CHAT && flag1) {
                softKeyBoardManager.checkSoftKeyBoardBetAmount(
                    chatViewModel.checkBetAmountFlow.value,
                    keyBoardType,
                    flag
                )
                return
            }
        }

        softKeyBoardManager.addSoftKeyBoardEvent(keyBoardType, flag)
        softKeyBoardManager.showKeyboardAnimation()
    }


    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun initListener() {
        mBinding.ivEmoji.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                keyboardChangeClick(KeyBoardType.EMOJI)
            }
            return@setOnTouchListener true
        }
        mBinding.chatTvSend.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                sendText()
            }
            return@setOnTouchListener true
        }
        mBinding.screenTop.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                keyboardChangeClick(KeyBoardType.CHAT, 6)
            }
            return@setOnTouchListener true
        }


        mBinding.apply {
            ivBottomAt.setOnTouchListener { v, event -> return@setOnTouchListener true }
            ivBottomBet.setOnTouchListener { v, event -> return@setOnTouchListener true }
            ivBottomEmoji.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    chatViewModel.updateKeyBoardUi(KeyBoardType.EMOJI, 6)
                }
                return@setOnTouchListener true
            }
            ivLanguage.setOnClickListener {
                showLanguageDialog()
            }
        }
//        mBinding.ivBottomEmoji.setOnTouchListener { v, event ->
//            if (event.action == MotionEvent.ACTION_UP) {
//                keyboardChangeClick(KeyBoardType.EMOJI)
//            }
//            return@setOnTouchListener true
//        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initInputListener() {
        mBinding.chatEtInput.apply {
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
        chatViewModel.sendMsgToChat(text)
    }

    private fun addMainViewListen() {
        mBinding.main.viewTreeObserver
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
            emojiContent.layoutParams.height = softKeyBoardManager.emojiKeyBoardHeight
            screenContent.layoutParams.height = mViewModel.keyBoardHeight
            main.layoutParams.height =
                mViewModel.keyBoardHeight + softKeyBoardManager.emojiKeyBoardHeight
            mBinding.main.requestLayout()
        }
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
            emojiContent.isInvisible = !isVisible
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

    /**
     * 实时更新chatViewModel的currentKeyBoardType
     * */
    override fun updateChatKeyboardType(keyBoardType: KeyBoardType) {
        chatViewModel.currentKeyBoardType = keyBoardType
    }

    override fun startAnim(
        animationType: KeyboardActionType,
        offset: Int,
        onStart: () -> Unit,
        onEnd: () -> Unit
    ) {
        "panelAnimateTo  offset $offset ".logd("aaa")
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
            if (offset == 0)
                mainAnim?.playSequentially(emojiSet, hotViewAnim(offset))
            else
                mainAnim?.playSequentially(hotViewAnim(offset), emojiSet)
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

    //              输入框  底部tab
//at         left  101     96
//           top   18      65
//注单        left   66      54
//表情        left   30      12
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

    //    input width 307  -> 282  0.918   left 58 -> 12   right 10 -> 81
//    language  widith 40  left 10
//    send  width 60     right 10
//    et   width 159 -> 250 157
    private fun inputWidthAnim(offset: Int): AnimatorSet {
        val animSet = AnimatorSet()
        mBinding.apply {
            val ivLanTransXParam = if (offset == 0) 0f else -50.dp2px.toFloat()
            val chatLLTransXParam = if (offset == 0) 0f else -46.dp2px.toFloat()

            val scaleXParam = if (offset == 0) 1f else 0.92f

            val ivLangeAnim = ObjectAnimator.ofFloat(ivLanguage, "translationX", ivLanTransXParam)
            val chatLLTransXAnim =
                ObjectAnimator.ofFloat(ivLanguage, "translationX", chatLLTransXParam)

            chatLlInput.pivotX = 0f
            val chatLlScaleXAnim = ObjectAnimator.ofFloat(chatLlInput, "scaleX", scaleXParam)
            chatTvSend.pivotX = 60.dp2px.toFloat()//chatTvSend.width.toFloat()
            val tvSenAnim =
                ObjectAnimator.ofFloat(chatTvSend, "scaleX", if (offset == 0) 0f else 1f)

            animSet.duration = 1000
            animSet.addListener(onStart = {
                if (offset != 0) {
                    chatTvSend.scaleX = 0f
                }
            }, onEnd = {
                chatLlInput.layoutParams.width = if (offset == 0) 307.dp2px else 282.dp2px
            })
            animSet.playTogether(ivLangeAnim, chatLLTransXAnim)
        }
        return animSet
    }


//    private fun initTab() {
//        val list = mViewModel.tabMenus()
//        list.indices.forEach {
//            mBinding.keyboardTb.apply {
//                val tab = newTab()
//                val view =
//                    LayoutInflater.from(context).inflate(R.layout.item_keyboard_tab_layout, null)
//                val iv: ImageView = view.findViewById(R.id.iv)
//                iv.setImageResource(if (it == 0) list[it].select else list[it].normal)
//                tab.setCustomView(view)
//                addTab(tab)
//            }
//        }
//        mBinding.keyboardTb.removeAllTips()
//
//        mBinding.keyboardTb.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
//            override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
//                tab.let {
//                    val position = it.position
//                    val iv = tab.view.findViewById<ImageView>(R.id.iv)
//                    iv.setImageResource(list[position].select)
//                    mBinding.keyboardEmojiRecycler.smoothScrollToPosition(position)
//
//                    when (position) {
//                        0 -> {
//                            if (mBinding.keyboardTvAll.isInvisible) {
//                                tabChaneAnim(true, onStart = {
//                                    mBinding.keyboardTvAll.isInvisible = false
//                                })
//                            }
//                        }
//
//                        else -> {
//                            if (!mBinding.keyboardTvAll.isInvisible) {
//                                tabChaneAnim(false, onEnd = {
//                                    mBinding.keyboardTvAll.isInvisible = true
//                                })
//                            }
//                        }
//                    }
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab, isTabClick: Boolean) {
//                tab.let {
//                    val position = it.position
//                    val iv = tab.view.findViewById<ImageView>(R.id.iv)
//                    iv.setImageResource(list[position].normal)
//                }
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) {
//            }
//        })
//    }

//    private fun tabChaneAnim(
//        tvShow: Boolean,
//        onStart: (() -> Unit)? = null,
//        onEnd: (() -> Unit)? = null
//    ) {
//        val tvAnimAlpha =
//            ObjectAnimator.ofFloat(mBinding.keyboardTvAll, "alpha", if (tvShow) 1f else 0f)
//        val tvAnimTransY = ObjectAnimator.ofFloat(
//            mBinding.keyboardTvAll,
//            "translationY",
//            if (tvShow) 0f else -18.dp2px.toFloat()
//        )
//        val emojiAnim = ObjectAnimator.ofFloat(
//            mBinding.keyboardEmojiRecycler,
//            "translationY",
//            if (tvShow) 0f else -18.dp2px.toFloat()
//        )
//        val animSet = AnimatorSet()
//        animSet.duration = 250L
//        animSet.playTogether(tvAnimTransY, tvAnimAlpha, emojiAnim)
//        animSet.addListener(onStart = { onStart?.invoke() }, onEnd = { onEnd?.invoke() })
//        animSet.start()
//    }

    override fun onStart() {
        super.onStart()
        mViewModel.setSoftConfig(false)
        mBinding.inputContent.translationY = 42.dp2px.toFloat()
    }

    override fun onStop() {
        super.onStop()
        mViewModel.setSoftConfig(true)
    }

    override fun onDestroy() {
//        mBinding.keyboardEmojiRecycler.adapter?.let {
//            (it as SoftAdapter).animHelper?.cleanup()
//        }
        super.onDestroy()
    }

    companion object {
        const val TAG: String = "LiveSoftKeyboardFragment"
    }

}