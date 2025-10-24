package arch.cayenne.module.chat.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.module.chat.databinding.FragmentLiveSoftkeyboardLayoutBinding
import arch.cayenne.module.chat.manager.SoftKeyboardManager
import arch.cayenne.module.chat.manager.interf.SoftKeyBoardMangerListener
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
    }

    private fun initEmojiFragment(){
        val fragment = EmojiHomeFragment()
        childFragmentManager.beginTransaction().replace(R.id.emoji_content,fragment,EmojiHomeFragment.TAG).commit()
    }


    override suspend fun createObserver() {
        launch(Lifecycle.State.RESUMED) {
            chatViewModel.updateKeyboardUiStatus.observe(viewLifecycleOwner) {
                keyboardChangeClick(it, 1)
            }
        }
        softKeyBoardManager.toastLiveData.observe(viewLifecycleOwner) {
            showToast(it)
        }
        chatViewModel.chatHeightLiveData.observe(viewLifecycleOwner) {
            mViewModel.keyBoardHeight = it
            softKeyBoardManager.initView(
                requireActivity().window.decorView,
                mBinding.main,
                mBinding.liveChatEtInput
            )
            addMainViewListen()
        }
    }


    override fun keyboardChangeClick(keyBoardType: KeyBoardType, flag: Int) {
        if (!chatViewModel.matchStatus) {
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
            if (event.action == MotionEvent.ACTION_DOWN) {
                keyboardChangeClick(KeyBoardType.EMOJI)
            }
            return@setOnTouchListener true
        }
        mBinding.liveChatTvSend.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                sendText()
            }
            return@setOnTouchListener true
        }

        mBinding.inputContent.setOnTouchListener { v, event -> return@setOnTouchListener true }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initInputListener() {
        mBinding.liveChatEtInput.apply {
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

    /**
     * 发送消息
     * */
    private fun sendText() {
        val text: String = mBinding.liveChatEtInput.text?.toString() ?: ""
        if (text.isEmpty()) {
            return
        }
        mBinding.liveChatEtInput.text?.clear()
        keyboardChangeClick(KeyBoardType.CHAT, 4)
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
            softKeyBoardManager.emojiKeyBoardHeight =
                mViewModel.keyBoardHeight - 60.dp2px - 21.dp2px
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
        updateEmojiView(false)
    }

    /**
     * 展示软件盘
     * */
    private fun showSoftKeyBoard() {
        updateEmojiView(false)
    }

    /**
     * 展示表情界面
     * */
    private fun showEmoji() {
        updateEmojiView(true)
        softKeyBoardManager.etRequestFocus()
    }

    private fun updateInputView(isVisible: Boolean){
        mBinding.apply {
            ivLanguage.isVisible = isVisible
            ivAt.isVisible = isVisible
            ivBet.isVisible = isVisible
            ivEmoji.isVisible = isVisible
            liveChatTvSend.isVisible = !isVisible
        }

    }

    private fun updateEmojiView(isVisible: Boolean) {
        mBinding.apply {
            emojiContent.isInvisible = !isVisible
        }
    }
    /**
     * 实时更新chatViewModel的currentKeyBoardType
     * */
    override fun updateChatKeyboardType(keyBoardType: KeyBoardType) {
        chatViewModel.currentKeyBoardType = keyBoardType
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