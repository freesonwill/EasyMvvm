package com.walisport.module.live.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.animation.addListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.google.android.material.tabs.TabLayout
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.KeyBoardType
import com.walisport.module.live.data.model.EmojiData
import com.walisport.module.live.databinding.FragmentLiveSoftkeyboardLayoutBinding
import com.walisport.module.live.ui.adapter.SoftAdapter
import com.walisport.module.live.ui.viewmodel.LiveChatViewModel
import com.walisport.module.live.ui.viewmodel.LiveSoftKeyboardViewModel
import com.walisport.module.live.utils.EditTextUtils
import com.walisport.module.live.utils.EmojiEditFilter
import com.walisport.module.live.utils.EmojiUtils.BID_EMOJI_REGEX
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

    //监听软件的显示隐藏状态
    private var isSoftKeyBoard: Boolean = false
//    var softKeyHeightHelper:SoftKeyHeightHelper? = null


    //表情点击
    private val itemListener = object : RecyclerItemListener<EmojiData> {
        override fun onItemClick(item: EmojiData?, position: Int) {
            val emojiPattern: Pattern = Pattern.compile(BID_EMOJI_REGEX)
            if (item?.key?.let { emojiPattern.matcher(it).find() } == true) {
                chatViewModel.sendMsgToChat(item.key)
                return
            }
            mBinding.liveChatEtInput.text?.append(item?.key)
        }
    }

    override fun onPause() {
        super.onPause()
        chatViewModel.addSoftKeyBoardEvent(KeyBoardType.CHAT, 2)
    }

    override fun onStop() {
        super.onStop()
//        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
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
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun initListener() {
        mBinding.liveChatIvEmoji.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                chatViewModel.addSoftKeyBoardEvent(KeyBoardType.EMOJI)
            }
            return@setOnTouchListener true
        }
        mBinding.liveChatTvSend.setOnClickListener {
            sendText()
        }
        mBinding.liveChatIvKeyboard.setOnClickListener {
            chatViewModel.addSoftKeyBoardEvent(KeyBoardType.SOFT_KEYBOARD)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initInputListener() {
        // 监听键盘的显示和隐藏
        ViewCompat.setOnApplyWindowInsetsListener(requireView()) { _: View?, insets: WindowInsetsCompat ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val isSoftVisible = imeInsets.bottom > 100.dp2px
            if (isSoftVisible) {
                // 键盘显示
                if (isSoftKeyBoard) {
                    return@setOnApplyWindowInsetsListener insets
                }
                isSoftKeyBoard = true
//                chatViewModel.softKeyBoardHeight = imeInsets.bottom
                onSoftKeyBoardShow()
            } else {
                // 键盘隐藏
                if (!isSoftKeyBoard) {
                    return@setOnApplyWindowInsetsListener insets
                }
                isSoftKeyBoard = false
                onSoftKeyBoardHide()
            }
            insets
        }


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
                //如果当前点击事件 softkeyboardlisterner 和 当前状态currentKeyboardListener 一致可以过滤掉聚焦事件
                if (chatViewModel.openSoftKeyBoardLiveData.value == true) { //要打开软件盘并且软件盘在收缩中
                    openSoftKeyBoard()
                }
            }
            //监听点击事件
            setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (chatViewModel.softKeyBoardListener.value != KeyBoardType.SOFT_KEYBOARD) {
                        chatViewModel.addSoftKeyBoardEvent(KeyBoardType.SOFT_KEYBOARD)
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
        if (text.isEmpty()) {
            return
        }
        chatViewModel.addSoftKeyBoardEvent(KeyBoardType.CHAT, 4)
        chatViewModel.sendMsgToChat(text)
    }

    override suspend fun createObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.currentSoftKeyboard.collect {
                when (it) {
                    KeyBoardType.SOFT_KEYBOARD -> showSoftKeyBoard()
                    KeyBoardType.EMOJI -> showEmoji()
                    KeyBoardType.CHAT -> showChat()
                }
            }
        }
        chatViewModel.openSoftKeyBoardLiveData.observe(viewLifecycleOwner) {
            if (it && !isSoftKeyBoard) {  //显示软件盘状态 it == true  当前软件盘没有收缩状态
                openSoftKeyBoard()
                mBinding.liveChatEtInput.requestFocus()
            } else if (!it && isSoftKeyBoard) { // 隐藏软件盘状态  it== false 当前软件盘弹出状态
                hideSoftKeyBoard(2)
                mBinding.liveChatEtInput.clearFocus()

            }
        }
        chatViewModel.softKeyBoardListener.collect {
//            val flag1 = !chatViewModel.checkSoftKeyboardVisible()
//            if (it != KeyBoardType.CHAT && flag1) {
//                chatViewModel.checkSoftKeyBoardBetAmount()
//                return@collect
//            }
            showChangeAnimation()
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

        mBinding.keyboardTb.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = it.position
                    val iv = tab.view.findViewById<ImageView>(R.id.iv)
                    iv.setImageResource(list[position].select)

                    when (position) {
                        0,
                        1 -> {
                            mBinding.keyboardEmojiRecycler.smoothScrollToPosition(position)
//                            mBinding.keyboardEmojiRecycler.isInvisible = false
                            mBinding.keyboardTvAll.isVisible = true
                        }

                        else -> {
//                            mBinding.keyboardEmojiRecycler.isInvisible = true
                            mBinding.keyboardTvAll.isVisible = false
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = it.position
                    val iv = tab.view.findViewById<ImageView>(R.id.iv)
                    iv.setImageResource(list[position].normal)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
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
//                    mBinding.liveChatEtInput.requestFocus()
//                    mBinding.liveChatEtInput.setSelection(mBinding.liveChatEtInput.length())
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

    private fun showChangeAnimation() {
        val animationType: Int = mViewModel.getAnimationType(
            chatViewModel.softKeyBoardListener.value,
            chatViewModel.currentSoftKeyboard.value
        )
        when (animationType) {
            //展示软件盘
            mViewModel.CHAT_TO_SOFT -> {
                chatViewModel.updateSoftKeyBoard(true, 1)
                chatViewModel.updateKeyBoard()
            }
            //软件盘切换到聊天
            mViewModel.SOFT_TO_CHAT -> {
                chatViewModel.updateSoftKeyBoard(false, 2)
                chatViewModel.updateKeyBoard()
            }
            //软件盘切换到表情键盘
            mViewModel.SOFT_TO_EMOJI -> {
                startAnimation(animationType)
            }
            //展示表情键盘
            mViewModel.CHAT_TO_EMOJI -> {
                startAnimation(animationType)
            }

            //表情键盘切换到软件盘
            mViewModel.EMOJI_TO_SOFT -> {
                startAnimation(animationType)
            }
            //表情键盘切换到聊天
            mViewModel.EMOJI_TO_CHAT -> {
                startAnimation(animationType)
            }

            else -> {}
        }
    }


    @SuppressLint("Recycle")
    private fun startAnimation(animationType: Int) {
        val topValue = 21.dp2px //  21 输入框到顶部的距离
        val bottomValue = chatViewModel.keyBoardHeight - 62.dp2px
        val mainMaxHeight = chatViewModel.keyBoardHeight - 21.dp2px //有21dp到顶部的距离
        val mainMinHeight = 62.dp2px
        var mainStartHeight: Int = mBinding.main.height
        var mainEndHeight: Int = 0
        var translationStart: Float = 0f
        var translationEnd: Float = 0f

        when (animationType) {
            mViewModel.CHAT_TO_EMOJI -> {
                translationStart = bottomValue.toFloat()
                translationEnd = topValue.toFloat()
                mainEndHeight = mainMaxHeight
            }
            // 起始点为软件盘的高度 动画开始高度为整个表情键盘的高度下降到软件盘高度 因此topDistance - softKeyBoardHeight 截止点为topDistance
            mViewModel.SOFT_TO_EMOJI -> {
                translationStart = (chatViewModel.softKeyBoardHeight+62.dp2px).toFloat()
                translationEnd = 0f
                mainStartHeight = chatViewModel.softKeyBoardHeight+62.dp2px
                mainEndHeight = mainMaxHeight
            }

            mViewModel.EMOJI_TO_SOFT -> {
                translationStart = topValue.toFloat()
                translationEnd =
                    (chatViewModel.keyBoardHeight - chatViewModel.softKeyBoardHeight - 21.dp2px - 62.dp2px).toFloat()
                mainEndHeight = mainMinHeight
            }

            mViewModel.EMOJI_TO_CHAT -> {
                translationStart = topValue.toFloat()
                translationEnd = (chatViewModel.keyBoardHeight - 21.dp2px - 62.dp2px).toFloat()
                mainEndHeight = mainMinHeight
            }

            else -> {}
        }


        val alphaParam = if (animationType in intArrayOf(mViewModel.CHAT_TO_EMOJI, mViewModel.SOFT_TO_EMOJI)) floatArrayOf(0f, 1f) else floatArrayOf(1f, 0f)

        val translationAnim = ObjectAnimator.ofFloat(mBinding.main, "translationY", translationStart, translationEnd)
        val alphaAnim = ObjectAnimator.ofFloat(mBinding.emojiContent, "alpha", *alphaParam)
        val valueAnim = ValueAnimator.ofInt(mainStartHeight,mainEndHeight)
        valueAnim.addUpdateListener {
            mBinding.main.layoutParams.height = it.animatedValue as Int
        }
        val animSet = AnimatorSet().apply {
            duration = 300
            playTogether(translationAnim, alphaAnim)
            addListener(onStart = {
                when (animationType) {
                    mViewModel.CHAT_TO_EMOJI -> {
                        mBinding.main.layoutParams.height = mainMaxHeight
                        chatViewModel.updateKeyBoard()
                    }
                    mViewModel.SOFT_TO_EMOJI -> {
                        mBinding.main.layoutParams.height = mainMaxHeight
//                        mBinding.emojiContent.layoutParams.height = chatViewModel.softKeyBoardHeight
                        chatViewModel.updateKeyBoard()
                        chatViewModel.updateSoftKeyBoard(false, 3)
                    }
                    mViewModel.EMOJI_TO_SOFT -> {
                        chatViewModel.updateSoftKeyBoard(true, 6)
                    }
                    else -> {}
                }

            }, onEnd = {
                if (animationType == mViewModel.EMOJI_TO_SOFT) {
                    mBinding.main.layoutParams.height = mainMinHeight
                    chatViewModel.updateKeyBoard()
                } else if (animationType == mViewModel.EMOJI_TO_CHAT) {
                    mBinding.main.layoutParams.height = mainMinHeight
                    chatViewModel.updateKeyBoard()
                }else if(animationType == mViewModel.SOFT_TO_EMOJI){
//                    val emojiHeight = chatViewModel.keyBoardHeight-21.dp2px -62.dp2px
//                    mBinding.main.layoutParams.height = mainMaxHeight
//                    mBinding.emojiContent.layoutParams.height = emojiHeight
//                    "end soft_to_emoji mainMaxHeight $mainMaxHeight   emojiHeight $emojiHeight  ".logd("aaa")
//               "${mBinding.keyboardEmojiRecycler.layoutParams.height}   softKeyBoard ${ chatViewModel.softKeyBoardHeight} emojiContent ${mBinding.emojiContent.layoutParams.height}  tranY ${mBinding.emojiContent.translationY}  top ${mBinding.emojiContent.top}  ${mBinding.emojiContent.bottom}".logd("aaa")
                }
                mBinding.main.translationY = 0f
            })
            start()
        }
    }


    override fun onResume() {
        super.onResume()
    }

    /**
     * 展示聊天界面
     * */
    fun showChat() {
        mBinding.apply {
            liveChatIvEmoji.isVisible = true
//            liveChatTvSend.isVisible = false
            liveChatIvKeyboard.isVisible = false
            liveChatTvSize.isVisible = false
//            liveChatIvShare.isVisible = true
            liveChatEtInput.text?.clear()
            main.setBackgroundResource(
                SkinnableResourceManager.getTargetResourceId(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.main_background
                )
            )
        }
        updateEmojiView(false)
        updateWhenKeyBoardVisible(KeyBoardType.CHAT)
//        hideSoftKeyBoard(3)
    }

    /**
     * 展示软件盘
     * */
    private fun showSoftKeyBoard() {
        mBinding.apply {
//            liveChatTvSend.isVisible = true
            liveChatIvEmoji.isVisible = true
            liveChatTvSize.isVisible = true
            liveChatIvKeyboard.isVisible = false
            main.setBackgroundResource(
                SkinnableResourceManager.getTargetResourceId(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.card_background
                )
            )
//            liveChatEtInput.requestFocus()
        }
        updateEmojiView(false)
        updateWhenKeyBoardVisible(KeyBoardType.SOFT_KEYBOARD)
    }


    /**
     * 展示表情界面
     * */
    private fun showEmoji() {
        mBinding.apply {
//            mBinding.liveChatEtInput.requestFocus()
//            mBinding.liveChatEtInput.setSelection(mBinding.liveChatEtInput.text?.length ?: 0)
            liveChatIvEmoji.isVisible = false
//            liveChatTvSend.isVisible = true
            liveChatTvSize.isVisible = true
            main.setBackgroundResource(
                SkinnableResourceManager.getTargetResourceId(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.card_background
                )
            )
        }
        updateEmojiView(true)
        updateWhenKeyBoardVisible(KeyBoardType.EMOJI)
    }

    private fun updateEmojiView(isVisible: Boolean) {
        mBinding.apply {
            liveChatIvKeyboard.isVisible = isVisible
            emojiContent.isVisible = isVisible
        }
    }

    /**
     * 键盘显示时调整ui
     * */
    private fun updateWhenKeyBoardVisible(type: KeyBoardType) {
        //聊天界面和软件盘、表情键盘的hint展示不同
        mBinding.liveChatEtInput.hint = SkinnableResourceManager.getString(
            requireContext(),
            if (type == KeyBoardType.CHAT) R.string.live_chat_talk else R.string.live_chat_speak,
            mViewModel.languageManager.getLanguage()
        )
        //聊天界面、表情键盘和表情键盘对应的topMargin不同
//        mBinding.main.apply {
//            val lp = layoutParams as FrameLayout.LayoutParams
//            lp.topMargin = if (type == KeyBoardType.EMOJI) 21.dp2px else 0
//            layoutParams = lp
//        }
        mBinding.liveChatLlInput.backgroundTintList = SkinnableResourceManager.getColorStateList(
            requireContext(),
            if (type == KeyBoardType.CHAT) arch.cayenne.lib.common.R.color.input_box_2 else arch.cayenne.lib.res.R.color.card_ooo_background
        )
    }

    /**
     *打开软件盘
     * */
    private fun openSoftKeyBoard() {
        EditTextUtils.showKeyboard(activity, mBinding.liveChatEtInput)
        mBinding.liveChatEtInput.requestFocus()
    }

    /**
     * 禁用软件盘
     * */
    private fun hideSoftKeyBoard(flag: Int) {
        mBinding.liveChatEtInput.clearFocus()
        EditTextUtils.hideKeyboard(activity, mBinding.liveChatEtInput)
    }

    /**
     *软键盘打开时调用
     * */
    private fun onSoftKeyBoardShow() {
        chatViewModel.softKeyBoardHeight = getSupportSoftInputHeight()
//        mBinding.apply {
//            mBinding.emojiContent.isVisible = false
//            mBinding.emojiContent.layoutParams.height = chatViewModel.softKeyBoardHeight
//            noneContent.isVisible = true
//            val height = chatViewModel.keyBoardHeight - chatViewModel.softKeyBoardHeight - 62.dp2px
//            noneContent.layoutParams.height = height
//            emojiContent.layoutParams.height = chatViewModel.softKeyBoardHeight
//        }
    }

    /**
     * 软件盘关闭时调用
     * */
    private fun onSoftKeyBoardHide() {

//        if (chatViewModel.softKeyBoardListener.value == KeyBoardType.SOFT_KEYBOARD) {
//            chatViewModel.addSoftKeyBoardEvent(KeyBoardType.CHAT, 5)
//        }
    }

    interface SoftKeyHeightHelper {
        fun changeSoftKeyBoardHeight(height: Int, isSoftToEmoji: Boolean = false)
    }

    companion object {
        const val TAG: String = "LiveSoftKeyboardFragment"
    }

    /**
     * 获取软件盘的高度
     * @return
     */
    private fun getSupportSoftInputHeight(): Int {
        val r = Rect()
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        requireActivity().window.decorView.getWindowVisibleDisplayFrame(r)
        //获取屏幕的高度
        val screenHeight: Int = requireActivity().window.decorView.getRootView().height
        //计算软件盘的高度
        var softInputHeight = screenHeight - r.bottom

        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            val barHeight = getSoftButtonsBarHeight()
            softInputHeight = softInputHeight - barHeight
        }

        if (softInputHeight < 0) {
        }
        //存一份到本地
//        if (softInputHeight > 0) {
//
//        }
        return softInputHeight
    }

    /**
     * 底部虚拟按键栏的高度
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun getSoftButtonsBarHeight(): Int {
        val metrics = DisplayMetrics()
        //这个方法获取可能不是真实屏幕的高度
        requireActivity().windowManager.defaultDisplay.getMetrics(metrics)
        val usableHeight = metrics.heightPixels
        //获取当前屏幕的真实高度
        requireActivity().windowManager.defaultDisplay.getRealMetrics(metrics)
        val realHeight = metrics.heightPixels
        return if (realHeight > usableHeight) {
            realHeight - usableHeight
        } else {
            0
        }
    }

}