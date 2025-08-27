package com.walisport.module.live.ui

import android.animation.ObjectAnimator
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
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.core.animation.addListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
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
    private var emojiKeyBoardHeight:Int = 0

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

    override fun onStop() {
        super.onStop()
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
            chatViewModel.keyBoardHeight = mBinding.main.height
            emojiKeyBoardHeight = chatViewModel.keyBoardHeight - 62.dp2px -21.dp2px
            emojiContent.layoutParams.height = emojiKeyBoardHeight
            screenContent.layoutParams.height = chatViewModel.keyBoardHeight
            main.layoutParams.height = chatViewModel.keyBoardHeight+emojiKeyBoardHeight
        }
    }

    private fun keyboardListener(){
        val keyBoardInsetsCallBack =
            KeyBoardInsetsCallBack(object : KeyBoardListener {
                override fun onAnimStart(moveDistance: Int) {
                    val animationType = mViewModel.getAnimationType(chatViewModel.clickKeyBoardType, chatViewModel.currentKeyBoardType)
                    "onAnimStart $animationType softKeyBoardListener  ${chatViewModel.clickKeyBoardType} currentSoftKeyboard ${ chatViewModel.currentKeyBoardType}".logd("aaa")
                    if(animationType == KeyboardActionType.NONE){
                        return
                    }
                    when (animationType) {
                        KeyboardActionType.SOFT_TO_EMOJI -> {
                        }
                        KeyboardActionType.EMOJI_TO_SOFT -> {
                            panelAnimateTo(-moveDistance)
                        }
                        KeyboardActionType.CHAT_TO_EMOJI -> {
                        }
                        KeyboardActionType.EMOJI_TO_CHAT -> {
                            panelAnimateTo(0)

                        }
                        KeyboardActionType.CHAT_TO_SOFT -> {
                        }
                        KeyboardActionType.SOFT_TO_SOFT ->{
                            keyboardChangeClick(KeyBoardType.CHAT)
                        }
                        KeyboardActionType.SOFT_TO_CHAT -> {
                        }
                        else ->{}
                    }
                }

                override fun onAnimDoing(offsetX: Int, offsetY: Int) {
                    val animationType = mViewModel.getAnimationType(chatViewModel.clickKeyBoardType, chatViewModel.currentKeyBoardType)
//                    "onDoing type $animationType $offsetY  ".logd("aaa")
                    if(animationType == KeyboardActionType.NONE){
                        return
                    }
                    if (animationType in arrayOf(
                            KeyboardActionType.CHAT_TO_SOFT,
                            KeyboardActionType.SOFT_TO_CHAT,
                            KeyboardActionType.SOFT_TO_SOFT)) {
                        mBinding.main.translationY = offsetY.toFloat()
                    }
                }

                override fun onAnimEnd() {
                    val animationType = mViewModel.getAnimationType(chatViewModel.clickKeyBoardType, chatViewModel.currentKeyBoardType)
                    "onAnimEnd   ${animationType in arrayOf(
                        KeyboardActionType.CHAT_TO_SOFT,
                        KeyboardActionType.SOFT_TO_CHAT,
                        KeyboardActionType.EMOJI_TO_SOFT,
                        KeyboardActionType.SOFT_TO_SOFT)} $animationType softKeyBoardListener  ${chatViewModel.clickKeyBoardType} currentSoftKeyboard ${ chatViewModel.currentKeyBoardType}".logd("aaa")
                    if(animationType == KeyboardActionType.NONE){
                        return
                    }
                    if (animationType in arrayOf(KeyboardActionType.CHAT_TO_SOFT, KeyboardActionType.SOFT_TO_CHAT, KeyboardActionType.EMOJI_TO_SOFT, KeyboardActionType.SOFT_TO_SOFT)) {
                        when (animationType) {
                            KeyboardActionType.EMOJI_TO_SOFT,
                            KeyboardActionType.SOFT_TO_SOFT,
                            KeyboardActionType.CHAT_TO_SOFT -> {
                                changeKeyboardUi(KeyBoardType.SOFT_KEYBOARD)
                            }
                            KeyboardActionType.SOFT_TO_CHAT -> {
                                changeKeyboardUi(KeyBoardType.CHAT)
                            }
                            else -> {}
                        }
                    }
                }
            })
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        ViewCompat.setWindowInsetsAnimationCallback(requireActivity().window.decorView, keyBoardInsetsCallBack)
    }

    private fun panelAnimateTo(offset: Int,onStart:()->Unit ={},onEnd:()->Unit = {}) {
        val panelAnimator = ObjectAnimator.ofFloat(mBinding.main, "translationY", offset.toFloat())
        panelAnimator?.interpolator = FastOutSlowInInterpolator()
        panelAnimator?.addListener(onStart = {onStart.invoke()}, onEnd = {onEnd.invoke()})
        panelAnimator?.start()
    }



    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun initListener() {

        mBinding.liveChatIvEmoji.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                keyboardChangeClick(KeyBoardType.EMOJI)
            }
            return@setOnTouchListener true
        }
        mBinding.liveChatTvSend.setOnClickListener {
            sendText()
        }
        mBinding.liveChatIvKeyboard.setOnClickListener {
            keyboardChangeClick(KeyBoardType.SOFT_KEYBOARD)
        }
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
                "hasFocus $hasFocus  openSoftKeyBoardLiveData ${chatViewModel.softKeyboardStatus}".logd("aaa")
                //如果当前点击事件 softkeyboardlisterner 和 当前状态currentKeyboardListener 一致可以过滤掉聚焦事件
//                if (chatViewModel.softKeyboardStatus) { //要打开软件盘并且软件盘在收缩中
//                    openSoftKeyBoard()
//                }
            }
            //监听点击事件
            setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (chatViewModel.clickKeyBoardType != KeyBoardType.SOFT_KEYBOARD) {
                        keyboardChangeClick(KeyBoardType.SOFT_KEYBOARD)
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
        keyboardChangeClick(KeyBoardType.CHAT, 4)
        chatViewModel.sendMsgToChat(text)
    }

    override suspend fun createObserver() {
        chatViewModel.updateKeyboardUiStatus.observe(viewLifecycleOwner){
            keyboardChangeClick(it,1)
        }

//        viewLifecycleOwner.lifecycleScope.launch {
//            chatViewModel.currentSoftKeyboard.collect {
//                when (it) {
//                    KeyBoardType.SOFT_KEYBOARD -> showSoftKeyBoard()
//                    KeyBoardType.EMOJI -> showEmoji()
//                    KeyBoardType.CHAT -> showChat()
//                }
//            }
//        }
//        chatViewModel.openSoftKeyBoardLiveData.observe(viewLifecycleOwner) {
//            if (it && !isSoftKeyBoard) {  //显示软件盘状态 it == true  当前软件盘没有收缩状态
//                openSoftKeyBoard()
//                mBinding.liveChatEtInput.requestFocus()
//            } else if (!it && isSoftKeyBoard) { // 隐藏软件盘状态  it== false 当前软件盘弹出状态
//                hideSoftKeyBoard(2)
//                mBinding.liveChatEtInput.clearFocus()
//            }
//        }

//        chatViewModel.softKeyBoardListener.collect {
//            val flag1 = !chatViewModel.checkSoftKeyboardVisible()
//            if (it != KeyBoardType.CHAT && flag1) {
//                chatViewModel.checkSoftKeyBoardBetAmount()
//                return@collect
//            }
//        showKeyboardAnimation()
//        }
    }

    private fun softKeyboardChange(value:Boolean,flag: Int){
        chatViewModel.softKeyboardStatus = value
        "softKeyboardChange  value $value $isSoftKeyBoard".logd("aaa")
        if (value && !isSoftKeyBoard) {  //显示软件盘状态 it == true  当前软件盘没有收缩状态
            openSoftKeyBoard()
        } else if (!value && isSoftKeyBoard) { // 隐藏软件盘状态  it== false 当前软件盘弹出状态
            hideSoftKeyBoard(2)
        }
    }

    private fun keyboardChangeClick(keyBoardType: KeyBoardType,flag:Int = 0){
        chatViewModel.addSoftKeyBoardEvent(keyBoardType, flag)
        showKeyboardAnimation()
//        showKeyboardAnimation()
    }

    private fun changeKeyboardUi(keyBoardType: KeyBoardType) {
        when (keyBoardType) {
            KeyBoardType.SOFT_KEYBOARD -> showSoftKeyBoard()
            KeyBoardType.EMOJI -> showEmoji()
            KeyBoardType.CHAT -> showChat()
        }
        chatViewModel.updateKeyBoard()
    }

    private fun showKeyboardAnimation() {
        val animationType = mViewModel.getAnimationType(
            chatViewModel.clickKeyBoardType,
            chatViewModel.currentKeyBoardType
        )
        "showChangeAnimation $animationType listener: ${chatViewModel.clickKeyBoardType} current ${ chatViewModel.currentKeyBoardType}".logd("aaa")
        when (animationType) {
            KeyboardActionType.CHAT_TO_CHAT -> changeKeyboardUi(KeyBoardType.CHAT)
            //展示软件盘
            KeyboardActionType.CHAT_TO_SOFT -> {
                softKeyboardChange(true,1)
            }
            //软件盘切换到聊天
            KeyboardActionType.SOFT_TO_CHAT -> {
                softKeyboardChange(false, 2)
            }
//            //软件盘切换到表情键盘
            KeyboardActionType.SOFT_TO_EMOJI -> {
                softKeyboardChange(false, 3)
                panelAnimateTo(-emojiKeyBoardHeight, onStart = {
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
                softKeyboardChange(true, 4)
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

        mBinding.keyboardTb.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = it.position
                    val iv = tab.view.findViewById<ImageView>(R.id.iv)
                    iv.setImageResource(list[position].select)

                    when (position) {
                        0 ->{
                            mBinding.keyboardTvAll.isVisible = true
                        }
                        1 -> {
                            mBinding.keyboardTvAll.isVisible = false
                            mBinding.keyboardEmojiRecycler.smoothScrollToPosition(position)
                        }

                        else -> {
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

    /**
     * 展示聊天界面
     * */
    fun showChat() {
        mBinding.apply {
            liveChatIvEmoji.isVisible = true
//            liveChatTvSend.isVisible = false
            liveChatIvKeyboard.isVisible = false
//            liveChatIvShare.isVisible = true
            liveChatEtInput.text?.clear()
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
//            liveChatTvSend.isVisible = true
            liveChatIvEmoji.isVisible = true
            liveChatTvSize.isVisible = true
            liveChatIvKeyboard.isVisible = false

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
        mBinding.liveChatEtInput.hint = SkinnableResourceManager.getString(requireContext(), if (type == KeyBoardType.CHAT) R.string.live_chat_talk else R.string.live_chat_speak, mViewModel.languageManager.getLanguage())
        mBinding.liveChatLlInput.backgroundTintList = SkinnableResourceManager.getColorStateList(requireContext(),
            if (type == KeyBoardType.CHAT) arch.cayenne.lib.common.R.color.input_box_2 else arch.cayenne.lib.res.R.color.card_ooo_background
        )
        val mainColor =  if(type == KeyBoardType.CHAT)  arch.cayenne.lib.common.R.color.main_background  else  arch.cayenne.lib.common.R.color.card_background
        mBinding.inputContent.setBackgroundResource(SkinnableResourceManager.getTargetResourceId(requireContext(), mainColor))
        mBinding.emojiContent.setBackgroundResource(SkinnableResourceManager.getTargetResourceId(requireContext(), mainColor))
    }

    /**
     *打开软件盘
     * */
    private fun openSoftKeyBoard() {
        "打开软件盘".logd("aaa")
        mBinding.liveChatEtInput.requestFocus()
        EditTextUtils.showKeyboard(activity, mBinding.liveChatEtInput)
    }

    /**
     * 禁用软件盘
     * */
    private fun hideSoftKeyBoard(flag: Int) {
        "关闭软件盘 $flag".logd("aaa")
        EditTextUtils.hideKeyboard(activity, mBinding.liveChatEtInput)
    }

    /**
     *软键盘打开时调用
     * */
    private fun onSoftKeyBoardShow() {
//        if (chatViewModel.softKeyBoardHeight == 0) {
//            val height = getSupportSoftInputHeight()
//            chatViewModel.saveUpdateSoftKeyBoardHeight(height)
//        }
    }

    /**
     * 软件盘关闭时调用
     * */
    private fun onSoftKeyBoardHide() {
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