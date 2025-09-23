package com.walisport.module.live.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.google.android.material.tabs.TabLayout
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.KeyBoardType
import com.walisport.module.live.data.constants.KeyboardActionType
import com.walisport.module.live.data.model.EmojiData
import com.walisport.module.live.databinding.FragmentLiveSoftkeyboardLayoutBinding
import com.walisport.module.live.ui.adapter.SoftAdapter
import com.walisport.module.live.ui.viewmodel.LiveChatViewModel
import com.walisport.module.live.ui.viewmodel.LiveSoftKeyboardViewModel
import com.walisport.module.live.utils.EditTextUtils
import com.walisport.module.live.utils.EmojiEditFilter
import com.walisport.module.live.utils.EmojiUtils.BID_EMOJI_REGEX
import com.walisport.module.live.utils.softkeyboard.KeyBoardInsetsCallBack
import com.walisport.module.live.utils.softkeyboard.KeyBoardListener
import com.walisport.module.live.utils.softkeyboard.NavigationBarHelper
import com.walisport.module.live.utils.softkeyboard.NavigationListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import kotlin.reflect.KClass


class LiveSoftKeyboardFragment :
    BaseFragment<LiveSoftKeyboardViewModel, FragmentLiveSoftkeyboardLayoutBinding>() {
    override val vbClass: KClass<FragmentLiveSoftkeyboardLayoutBinding>
        get() = FragmentLiveSoftkeyboardLayoutBinding::class
    override val vmClass: KClass<LiveSoftKeyboardViewModel>
        get() = LiveSoftKeyboardViewModel::class
    private val chatViewModel: LiveChatViewModel by sharedViewModel<LiveChatViewModel, LiveChatFragment>()
    private var emojiKeyBoardHeight: Int = 0
    private var mainAnim: ObjectAnimator? = null
    private var isSoftKeyboardShow = false
    private var navigationBarHelper:NavigationBarHelper? = null

    //表情点击
    private val itemListener = object : RecyclerItemListener<EmojiData> {
        override fun onItemClick(item: EmojiData?, position: Int) {
            val emojiPattern: Pattern = Pattern.compile(BID_EMOJI_REGEX)
            if (item?.key?.let { emojiPattern.matcher(it).find() } == true) {
                keyboardChangeClick(KeyBoardType.CHAT,5)
                chatViewModel.sendMsgToChat(item.key)
                return
            }
            mBinding.liveChatEtInput.text?.append(item?.key)
            etRequestFocus()
        }
    }

    override fun onStop() {
        super.onStop()
//        ViewCompat.setWindowInsetsAnimationCallback(requireActivity().window.decorView, null)
        navigationBarHelper = null
        hideSoftKeyBoard(4)
    }

    override fun onDestroy() {
        mBinding.keyboardEmojiRecycler.adapter?.let {
            (it as SoftAdapter).animHelper?.cleanup()
        }
        super.onDestroy()
    }

    override fun initView(savedInstanceState: Bundle?) {
        initTab()
        initSoftRecycler()
        initInputListener()
        addMainViewListen()
    }

    override suspend fun createObserver() {
        launch (Lifecycle.State.RESUMED){
            chatViewModel.updateKeyboardUiStatus.observe(viewLifecycleOwner) {
                "updateKeyboardUiStatus $it".logd("aaa")
                keyboardChangeClick(it, 1)
            }
        }
    }


    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun initListener() {
        mBinding.liveChatIvEmoji.setOnTouchListener { v, event ->
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
        mBinding.liveChatIvKeyboard.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                keyboardChangeClick(KeyBoardType.SOFT_KEYBOARD)
            }
            return@setOnTouchListener true
        }
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
            filters = arrayOf(EmojiEditFilter(mBinding.liveChatTvSize))
            //监听聚焦事件，不合格的展示软件盘一律拦截
            setOnFocusChangeListener { v, hasFocus ->
//            如果当前点击事件 softkeyboardlisterner 和 当前状态currentKeyboardListener 一致可以过滤掉聚焦事件
//                "setOnFocusChangeListener ${chatViewModel.softKeyboardStatus}  isSoftKeyboardShow $isSoftKeyboardShow".logd("aaa")
                if (chatViewModel.softKeyboardStatus && !isSoftKeyboardShow) { //要打开软件盘并且软件盘在收缩中
                    openSoftKeyBoard()
                }
            }
            //监听点击事件
            setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (chatViewModel.clickKeyBoardType != KeyBoardType.SOFT_KEYBOARD) {
                        if(!isSoftKeyboardShow){
                            keyboardChangeClick(KeyBoardType.SOFT_KEYBOARD)
                        }
                    }
                    return@setOnTouchListener true
                }
                return@setOnTouchListener true
            }
        }
    }


    /**
     * 发送消息
     * */
    private fun sendText() {
        val text: String = mBinding.liveChatEtInput.text?.toString() ?: ""
        "text.isEmpty ${text.isEmpty()}".logd("aaa")
        if (text.isEmpty()) {
            return
        }
        mBinding.liveChatEtInput.text?.clear()
        keyboardChangeClick(KeyBoardType.CHAT, 4)
        chatViewModel.sendMsgToChat(text)
    }


    private fun addMainViewListen(){
        mBinding.main.viewTreeObserver
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mBinding.main.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    calculationLayoutSize()
                    keyboardListener()
                }
            })
    }

    private fun calculationLayoutSize() {
        mBinding.apply {
            "calculationLayoutSize  chatKeyBoardHeight:${chatViewModel.keyBoardHeight} ".logd("aaa")
            emojiKeyBoardHeight = chatViewModel.keyBoardHeight - 62.dp2px - 21.dp2px
            emojiContent.layoutParams.height = emojiKeyBoardHeight
            screenContent.layoutParams.height = chatViewModel.keyBoardHeight
            main.layoutParams.height = chatViewModel.keyBoardHeight + emojiKeyBoardHeight
        }
    }

    private fun keyboardListener() {
        navigationBarHelper = NavigationBarHelper(
            requireActivity().window.decorView,
            lifecycle,
            object : NavigationListener {
                override fun setNavigationStatus(hasNavigation: Boolean, navigationHeight: Int) {

                }

                override fun onSoftKeyBoardHide() {
                    isSoftKeyboardShow = false
                    if (chatViewModel.clickKeyBoardType == KeyBoardType.SOFT_KEYBOARD) {
                        keyboardChangeClick(KeyBoardType.CHAT)
                    }
                }

                override fun onSoftKeyBoardShow(keyboardHeight: Int) {
//                "onSoftKeyBoardShow ${keyboardHeight}  ${chatViewModel.softKeyBoardHeight}".logd("aaa")
                    isSoftKeyboardShow = true
                    if (chatViewModel.softKeyBoardHeight == keyboardHeight) {
                        return
                    }
                    mainAnim?.cancel()
                    val animationType = mViewModel.getKeyBoardActionType(
                        chatViewModel.clickKeyBoardType,
                        chatViewModel.currentKeyBoardType
                    )
//                "onSoftKeyBoardShow1  $animationType".logd("aaa")

                    when (animationType) {
                        KeyboardActionType.CHAT_TO_SOFT -> {
                            chatViewModel.softKeyBoardHeight = keyboardHeight
                            panelAnimateTo(-chatViewModel.softKeyBoardHeight, onStart = {
                                changeKeyboardUi(KeyBoardType.SOFT_KEYBOARD)
                            }, onEnd = {
                                chatViewModel.saveUpdateSoftKeyBoardHeight()
                            })
                        }

                        KeyboardActionType.EMOJI_TO_SOFT -> {
                            chatViewModel.softKeyBoardHeight = keyboardHeight
                            panelAnimateTo(-chatViewModel.softKeyBoardHeight, onStart = {
                                changeKeyboardUi(KeyBoardType.SOFT_KEYBOARD)
                            }, onEnd = {
                                chatViewModel.saveUpdateSoftKeyBoardHeight()
                            })
                        }

                        KeyboardActionType.SOFT_TO_SOFT -> {
                            chatViewModel.softKeyBoardHeight = keyboardHeight
                            panelAnimateTo(-chatViewModel.softKeyBoardHeight, onStart = {
                                changeKeyboardUi(KeyBoardType.SOFT_KEYBOARD)
                            }, onEnd = {
                                chatViewModel.saveUpdateSoftKeyBoardHeight()
                            })
                        }

                        else -> {}
                    }
                }
            })
        navigationBarHelper?.setDbKeyBoardHeight(chatViewModel.softKeyBoardHeight)

    }

    private fun panelAnimateTo(offset: Int, onStart: () -> Unit = {}, onEnd: () -> Unit = {}) {
        mainAnim = ObjectAnimator.ofFloat(mBinding.main, "translationY", offset.toFloat())
        mainAnim?.interpolator = FastOutSlowInInterpolator()
        mainAnim?.duration = 170
        mainAnim?.addListener(onStart = { onStart.invoke() }, onEnd = {
            onEnd.invoke()
        })
        mainAnim?.start()
    }

    private fun softKeyboardChange(value: Boolean, flag: Int) {
        chatViewModel.softKeyboardStatus = value

        if (value) {  //显示软件盘状态 it == true  当前软件盘没有收缩状态
            openSoftKeyBoard()
        } else if (!value) { // 隐藏软件盘状态  it== false 当前软件盘弹出状态
            hideSoftKeyBoard(2)
        }
    }

    private fun keyboardChangeClick(keyBoardType: KeyBoardType, flag: Int = 0) {
//        val flag1 = !chatViewModel.checkSoftKeyboardVisible()
//        if (keyBoardType != KeyBoardType.CHAT && flag1) {
//            chatViewModel.checkSoftKeyBoardBetAmount(keyBoardType, flag)
//            return
//        }
        chatViewModel.addSoftKeyBoardEvent(keyBoardType, flag)
        showKeyboardAnimation()
    }

    private fun changeKeyboardUi(keyBoardType: KeyBoardType) {
        "changeKeyboardUi keyBoardType $keyBoardType".logd("aaa")
        when (keyBoardType) {
            KeyBoardType.SOFT_KEYBOARD -> showSoftKeyBoard()
            KeyBoardType.EMOJI -> showEmoji()
            KeyBoardType.CHAT -> showChat()
        }
        chatViewModel.updateKeyBoard()
    }

    private fun showKeyboardAnimation() {
        val animationType = mViewModel.getKeyBoardActionType(
            chatViewModel.clickKeyBoardType,
            chatViewModel.currentKeyBoardType
        )
//        "showKeyboardAnimation $animationType ${chatViewModel.softKeyBoardHeight}".logd("aaa")
        when (animationType) {
            KeyboardActionType.CHAT_TO_CHAT -> changeKeyboardUi(KeyBoardType.CHAT)
            //展示软件盘
            KeyboardActionType.CHAT_TO_SOFT -> {
                if (chatViewModel.softKeyBoardHeight == 0) {
                    softKeyboardChange(true, 1)
                    return
                }
                panelAnimateTo(-chatViewModel.softKeyBoardHeight, onStart = {
                    softKeyboardChange(true, 1)
                    changeKeyboardUi(KeyBoardType.SOFT_KEYBOARD)
                })
            }
            //软件盘切换到聊天
            KeyboardActionType.SOFT_TO_CHAT -> {
                panelAnimateTo(0, onStart = {
                    softKeyboardChange(false, 2)
                    changeKeyboardUi(KeyBoardType.CHAT)
                })
            }
//            //软件盘切换到表情键盘
            KeyboardActionType.SOFT_TO_EMOJI -> {
//                isSoftKeyBoardBack = true
                panelAnimateTo(-emojiKeyBoardHeight, onStart = {
                    softKeyboardChange(false, 3)
                    changeKeyboardUi(KeyBoardType.EMOJI)
                })
            }
            //展示表情键盘
            KeyboardActionType.CHAT_TO_EMOJI -> {
                panelAnimateTo(-emojiKeyBoardHeight, onStart = {
                    changeKeyboardUi(KeyBoardType.EMOJI)
                })
            }
            //表情键盘切换到软件盘
            KeyboardActionType.EMOJI_TO_SOFT -> {
                if (chatViewModel.softKeyBoardHeight == 0) {
                    softKeyboardChange(true, 4)
                    return
                }
                panelAnimateTo(-chatViewModel.softKeyBoardHeight, onStart = {
                    softKeyboardChange(true, 4)
                    changeKeyboardUi(KeyBoardType.SOFT_KEYBOARD)
                }, onEnd = {
//                    isSoftKeyBoardBack = true
                })
            }
            //表情键盘切换到聊天
            KeyboardActionType.EMOJI_TO_CHAT -> {
                panelAnimateTo(0, onEnd = {
                    changeKeyboardUi(KeyBoardType.CHAT)
                })
            }

            else -> {}
        }
    }


    private fun initTab() {
        val list = mViewModel.tabMenus()
        list.indices.forEach {
            mBinding.keyboardTb.apply {
                val tab = newTab()
                val view =
                    LayoutInflater.from(context).inflate(R.layout.item_keyboard_tab_layout, null)
                val iv: ImageView = view.findViewById(R.id.iv)
                iv.setImageResource(if (it == 0) list[it].select else list[it].normal)
                tab.setCustomView(view)
                addTab(tab)
            }
        }
        mBinding.keyboardTb.removeAllTips()

        mBinding.keyboardTb.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
            override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
                tab.let {
                    val position = it.position
                    val iv = tab.view.findViewById<ImageView>(R.id.iv)
                    iv.setImageResource(list[position].select)
                    mBinding.keyboardEmojiRecycler.smoothScrollToPosition(position)

                    when (position) {
                        0 -> {
                            if (mBinding.keyboardTvAll.isInvisible) {
                                tabChaneAnim(true, onStart = {
                                    mBinding.keyboardTvAll.isInvisible = false
                                })
                            }
                        }

                        else -> {
                            if (!mBinding.keyboardTvAll.isInvisible) {
                                tabChaneAnim(false, onEnd = {
                                    mBinding.keyboardTvAll.isInvisible = true
                                })
                            }
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                tab.let {
                    val position = it.position
                    val iv = tab.view.findViewById<ImageView>(R.id.iv)
                    iv.setImageResource(list[position].normal)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) {
            }
        })
    }

    private fun tabChaneAnim(
        tvShow: Boolean,
        onStart: (() -> Unit)? = null,
        onEnd: (() -> Unit)? = null
    ) {
        val tvAnimAlpha =
            ObjectAnimator.ofFloat(mBinding.keyboardTvAll, "alpha", if (tvShow) 1f else 0f)
        val tvAnimTransY = ObjectAnimator.ofFloat(
            mBinding.keyboardTvAll,
            "translationY",
            if (tvShow) 0f else -18.dp2px.toFloat()
        )
        val emojiAnim = ObjectAnimator.ofFloat(
            mBinding.keyboardEmojiRecycler,
            "translationY",
            if (tvShow) 0f else -18.dp2px.toFloat()
        )
        val animSet = AnimatorSet()
        animSet.duration = 250L
        animSet.playTogether(tvAnimTransY, tvAnimAlpha, emojiAnim)
        animSet.addListener(onStart = { onStart?.invoke() }, onEnd = { onEnd?.invoke() })
        animSet.start()
    }


    private fun initSoftRecycler() {
        val snapHelper = PagerSnapHelper()
        mBinding.keyboardEmojiRecycler.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            val softAdapter = SoftAdapter()
            softAdapter.setItemListener(itemListener)
            softAdapter.delListener = {
                mBinding.liveChatEtInput.apply {
                    if (mBinding.liveChatEtInput.text?.length == 0) {
                        return@apply
                    }
                    dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                    dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
                    etRequestFocus()
                }
            }
            softAdapter.submitList(mViewModel.softData())
            adapter = softAdapter
            snapHelper.attachToRecyclerView(this)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val currentView = snapHelper.findSnapView(recyclerView.layoutManager)
                        currentView?.let {
                            val position = recyclerView.getChildAdapterPosition(currentView)
                            mBinding.keyboardTb.selectTab(mBinding.keyboardTb.getTabAt(position))
                        }
                    }
                }
            })
        }
    }

    /**
     * 展示聊天界面
     * */
    fun showChat() {
        mBinding.apply {
            liveChatIvEmoji.isVisible = true
            liveChatIvKeyboard.isVisible = false
        }
        mBinding.liveChatTvSize.isVisible = false
        updateEmojiView(false)
        updateWhenKeyBoardVisible(KeyBoardType.CHAT)
//        hideSoftKeyBoard(3)
    }

    /**
     * 展示软件盘
     * */
    private fun showSoftKeyBoard() {
        mBinding.apply {
            liveChatIvEmoji.isVisible = true
            liveChatTvSize.isVisible = true
            liveChatIvKeyboard.isVisible = false
        }
        updateEmojiView(false)
        updateWhenKeyBoardVisible(KeyBoardType.SOFT_KEYBOARD)
    }


    /**
     * 展示表情界面
     * */
    private fun showEmoji() {
        mBinding.apply {
            liveChatIvEmoji.isVisible = false
            liveChatTvSize.isVisible = true
        }
        updateEmojiView(true)
        updateWhenKeyBoardVisible(KeyBoardType.EMOJI)
        etRequestFocus()
    }

    private fun updateEmojiView(isVisible: Boolean) {
        mBinding.apply {
            liveChatIvKeyboard.isVisible = isVisible
//            emojiContent.isInvisible = !isVisible
        }
    }

    /**
     * 键盘显示时调整ui
     * */
    private fun updateWhenKeyBoardVisible(type: KeyBoardType) {
        if (context == null) {
            return
        }
//        //聊天界面和软件盘、表情键盘的hint展示不同
        mBinding.liveChatEtInput.hint = SkinnableResourceManager.getString(
            requireContext(),
            if (type == KeyBoardType.CHAT) R.string.live_chat_talk else R.string.live_chat_speak,
            mViewModel.languageManager.getLanguage()
        )
        mBinding.liveChatLlInput.backgroundTintList = SkinnableResourceManager.getColorStateList(
            requireContext(),
            if (type == KeyBoardType.CHAT) arch.cayenne.lib.common.R.color.input_box_2 else arch.cayenne.lib.res.R.color.card_ooo_background
        )
        val mainColor =
            if (type == KeyBoardType.CHAT) arch.cayenne.lib.common.R.color.main_background else arch.cayenne.lib.common.R.color.card_background
        mBinding.inputContent.setBackgroundResource(
            SkinnableResourceManager.getTargetResourceId(
                requireContext(),
                mainColor
            )
        )
        mBinding.emojiContent.setBackgroundResource(
            SkinnableResourceManager.getTargetResourceId(
                requireContext(),
                mainColor
            )
        )
    }

    /**
     *打开软件盘
     * */
    private fun openSoftKeyBoard() {
        EditTextUtils.showKeyboard(activity, mBinding.liveChatEtInput)
        etRequestFocus()
    }

    /**
     * 禁用软件盘
     * */
    private fun hideSoftKeyBoard(flag: Int) {
        EditTextUtils.hideKeyboard(activity, mBinding.liveChatEtInput)
    }

    private fun etRequestFocus() {
        lifecycleScope.launch {
            delay(200)
            mBinding.liveChatEtInput.requestFocus()
            mBinding.liveChatEtInput.setSelection(mBinding.liveChatEtInput.length())
        }
    }

    companion object {
        const val TAG: String = "LiveSoftKeyboardFragment"
    }

}