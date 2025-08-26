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
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.*
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
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
import arch.cayenne.lib.skin.widget.SkinnableImageView
import arch.cayenne.lib.skin.widget.SkinnableLinearLayout
import arch.cayenne.lib.skin.widget.SkinnableTextView
import com.google.android.material.tabs.TabLayout
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.KeyBoardType
import com.walisport.module.live.data.model.EmojiData
import com.walisport.module.live.databinding.FragmentLiveSoftkeyboardLayoutBinding
import com.walisport.module.live.ui.adapter.SoftAdapter
import com.walisport.module.live.ui.viewmodel.LiveChatViewModel
import com.walisport.module.live.ui.viewmodel.LiveSoftKeyboardViewModel
import com.walisport.module.live.ui.widget.EmojiEditTextView
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

    //监听软件的显示隐藏状态
    private var isSoftKeyBoard: Boolean = false
//    var softKeyHeightHelper:SoftKeyHeightHelper? = null

    private lateinit var liveChatIvEmoji: SkinnableImageView
    private lateinit var liveChatTvSend: SkinnableTextView
    private lateinit var liveChatIvKeyboard: SkinnableImageView
    private lateinit var liveChatEtInput: EmojiEditTextView
    private lateinit var liveChatTvSize: SkinnableTextView
    private lateinit var liveChatLlInput: SkinnableLinearLayout
    private lateinit var inputContent:ConstraintLayout


    fun setInputView(
        liveChatIvEmoji: SkinnableImageView,
        liveChatTvSend: SkinnableTextView,
        liveChatIvKeyboard: SkinnableImageView,
        liveChatEtInput: EmojiEditTextView,
        liveChatTvSize: SkinnableTextView,
        liveChatLlInput: SkinnableLinearLayout,
        inputContent:ConstraintLayout
    ) {
        this.liveChatIvEmoji = liveChatIvEmoji
        this.liveChatTvSend = liveChatTvSend
        this.liveChatIvKeyboard = liveChatIvKeyboard
        this.liveChatEtInput = liveChatEtInput
        this.liveChatTvSize = liveChatTvSize
        this.liveChatLlInput = liveChatLlInput
        this.inputContent = inputContent
    }

    //表情点击
    private val itemListener = object : RecyclerItemListener<EmojiData> {
        override fun onItemClick(item: EmojiData?, position: Int) {
            val emojiPattern: Pattern = Pattern.compile(BID_EMOJI_REGEX)
            if (item?.key?.let { emojiPattern.matcher(it).find() } == true) {
                chatViewModel.sendMsgToChat(item.key)
                return
            }
            liveChatEtInput.text?.append(item?.key)
        }
    }

    override fun onPause() {
        super.onPause()
        chatViewModel.addSoftKeyBoardEvent(KeyBoardType.CHAT, 2)
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
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun initListener() {

        liveChatIvEmoji.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                chatViewModel.addSoftKeyBoardEvent(KeyBoardType.EMOJI)
            }
            return@setOnTouchListener true
        }
        liveChatTvSend.setOnClickListener {
            sendText()
        }
        liveChatIvKeyboard.setOnClickListener {
            chatViewModel.addSoftKeyBoardEvent(KeyBoardType.SOFT_KEYBOARD)
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


        liveChatEtInput.apply {
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
            filters = arrayOf(EmojiEditFilter(liveChatTvSize))
            //监听聚焦事件，不合格的展示软件盘一律拦截
            setOnFocusChangeListener { v, hasFocus ->
                "hasFocus $hasFocus  openSoftKeyBoardLiveData ${chatViewModel.openSoftKeyBoardLiveData.value}".logd("aaa")
                //如果当前点击事件 softkeyboardlisterner 和 当前状态currentKeyboardListener 一致可以过滤掉聚焦事件
                if (chatViewModel.openSoftKeyBoardLiveData.value == true) { //要打开软件盘并且软件盘在收缩中
                    openSoftKeyBoard()
                }else{
                    hideSoftKeyBoard(1)
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
        val text: String = liveChatEtInput.text?.toString() ?: ""
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
                liveChatEtInput.requestFocus()
            } else if (!it && isSoftKeyBoard) { // 隐藏软件盘状态  it== false 当前软件盘弹出状态
                hideSoftKeyBoard(2)
                liveChatEtInput.clearFocus()
            }
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
                liveChatEtInput.apply {
                    if (liveChatEtInput.text?.length == 0) {
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
        liveChatTvSize.isVisible = false
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
        liveChatEtInput.hint = SkinnableResourceManager.getString(
            requireContext(),
            if (type == KeyBoardType.CHAT) R.string.live_chat_talk else R.string.live_chat_speak,
            mViewModel.languageManager.getLanguage()
        )
        liveChatLlInput.backgroundTintList = SkinnableResourceManager.getColorStateList(
            requireContext(),
            if (type == KeyBoardType.CHAT) arch.cayenne.lib.common.R.color.input_box_2 else arch.cayenne.lib.res.R.color.card_ooo_background
        )
        val mainColor =  if(type == KeyBoardType.CHAT)  arch.cayenne.lib.common.R.color.main_background  else  arch.cayenne.lib.common.R.color.card_background
        mBinding.main.setBackgroundResource(SkinnableResourceManager.getTargetResourceId(requireContext(), mainColor))
        inputContent.setBackgroundResource(SkinnableResourceManager.getTargetResourceId(requireContext(), mainColor))
    }

    /**
     *打开软件盘
     * */
    private fun openSoftKeyBoard() {
        EditTextUtils.showKeyboard(activity, liveChatEtInput)
        liveChatEtInput.requestFocus()
    }

    /**
     * 禁用软件盘
     * */
    private fun hideSoftKeyBoard(flag: Int) {
        liveChatEtInput.clearFocus()
        EditTextUtils.hideKeyboard(activity, liveChatEtInput)
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