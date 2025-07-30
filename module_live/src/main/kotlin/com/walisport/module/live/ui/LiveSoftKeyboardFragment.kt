package com.walisport.module.live.ui

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isInvisible
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
    BaseFragment<LiveSoftKeyboardViewModel, FragmentLiveSoftkeyboardLayoutBinding>(){
    override val vbClass: KClass<FragmentLiveSoftkeyboardLayoutBinding>
        get() = FragmentLiveSoftkeyboardLayoutBinding::class
    override val vmClass: KClass<LiveSoftKeyboardViewModel>
        get() = LiveSoftKeyboardViewModel::class
    private val chatViewModel: LiveChatViewModel by sharedViewModel<LiveChatViewModel, LiveChatFragment>()
    //监听软件的显示隐藏状态
    private var isSoftKeyBoard:Boolean = false
    //表情点击
    private val itemListener = object : RecyclerItemListener<EmojiData> {
        override fun onItemClick(item: EmojiData?, position: Int) {
            val emojiPattern: Pattern = Pattern.compile(BID_EMOJI_REGEX)
            if(item?.key?.let { emojiPattern.matcher(it).find() } == true){
                chatViewModel.sendMsgToChat(item.key)
                return
            }
            mBinding.liveChatEtInput.text?.append(item?.key)
            mBinding.liveChatEtInput.requestFocus()
            mBinding.liveChatEtInput.setSelection(mBinding.liveChatEtInput.length())
        }
    }


    override fun initView(savedInstanceState: Bundle?) {
        initTab()
        initSoftRecycler()
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun initListener() {
        mBinding.liveChatEtInput.setOnClickListener {
            chatViewModel.addSoftKeyBoardEvent(KeyBoardType.SOFT_KEYBOARD)
        }
        mBinding.liveChatIvEmoji.setOnClickListener {
            chatViewModel.addSoftKeyBoardEvent(KeyBoardType.EMOJI)
        }
        mBinding.liveChatTvSend.setOnClickListener {
            sendText()
            chatViewModel.addSoftKeyBoardEvent(KeyBoardType.NONE)
        }
        mBinding.liveChatIvKeyboard.setOnClickListener {
            chatViewModel.addSoftKeyBoardEvent(KeyBoardType.SOFT_KEYBOARD)
        }

        mBinding.liveChatEtInput.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (chatViewModel.softKeyBoardListener.value != KeyBoardType.SOFT_KEYBOARD) {
                    chatViewModel.addSoftKeyBoardEvent(KeyBoardType.SOFT_KEYBOARD)
                }
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }

        mBinding.liveChatEtInput.imeOptions = EditorInfo.IME_ACTION_SEND
        mBinding.liveChatEtInput.setImeActionLabel(
            SkinnableResourceManager.getString(requireContext(), R.string.live_chat_send, mViewModel.languageManager.getLanguage()), EditorInfo.IME_ACTION_SEND
        )
        mBinding.liveChatEtInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendText()
                chatViewModel.addSoftKeyBoardEvent(KeyBoardType.NONE)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        mBinding.liveChatEtInput.filters = arrayOf(EmojiEditFilter(mBinding.liveChatTvSize))
        ViewCompat.setOnApplyWindowInsetsListener(requireView()) { _: View?, insets: WindowInsetsCompat ->
            if (insets.isVisible(WindowInsetsCompat.Type.ime())) {
                // 键盘显示
                if(isSoftKeyBoard){
                    return@setOnApplyWindowInsetsListener insets
                }
                isSoftKeyBoard = true
                onSoftKeyBoardShow()
            } else {
                // 键盘隐藏
                if(!isSoftKeyBoard){
                    return@setOnApplyWindowInsetsListener insets
                }
                isSoftKeyBoard = false
                onSoftKeyBoardHide()
            }
            insets
        }
    }

    override fun onPause() {
        super.onPause()
    }

    /**
     * 发送消息
     * */
    private fun sendText() {
        val text = mBinding.liveChatEtInput.text.toString()
        chatViewModel.sendMsgToChat(text)
    }

    override fun createObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.currentSoftKeyboard.collect {
                when (it) {
                    KeyBoardType.SOFT_KEYBOARD -> showSoftKeyBoard()
                    KeyBoardType.EMOJI -> showEmoji()
                    KeyBoardType.NONE -> showChat()
                }
            }
        }
        chatViewModel.openSoftKeyBoardLiveData.observe(viewLifecycleOwner){
            if(chatViewModel.softKeyBoardListener.value == KeyBoardType.SOFT_KEYBOARD){
                openSoftKeyBoard()
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
                            mBinding.keyboardEmojiRecycler.isInvisible = false
                        }

                        else -> {
                            mBinding.keyboardEmojiRecycler.isInvisible = true
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
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            val softAdapter = SoftAdapter()
            softAdapter.setItemListener(itemListener)
            softAdapter.delListener = {
                mBinding.liveChatEtInput.apply {
                    if(mBinding.liveChatEtInput.text?.length == 0){
                        return@apply
                    }
                    dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                    dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
                    mBinding.liveChatEtInput.requestFocus()
                    mBinding.liveChatEtInput.setSelection(mBinding.liveChatEtInput.length())
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
            liveChatTvSend.isVisible = false
            liveChatIvKeyboard.isVisible = false
            liveChatTvSize.isVisible = false
            liveChatIvShare.isVisible = true
            liveChatEtInput.text?.clear()
            main.setBackgroundResource(
                SkinnableResourceManager.getTargetResourceId(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.main_background
                )
            )
        }
        updateEmojiView(false)
        updateWhenKeyBoardVisible(KeyBoardType.NONE)
        hideSoftKeyBoard()
    }

    /**
     * 展示软件盘
     * */
    private fun showSoftKeyBoard() {
        mBinding.apply {
            liveChatTvSend.isVisible = true
            liveChatIvEmoji.isVisible = true
            liveChatTvSize.isVisible = true
            liveChatIvKeyboard.isVisible = false
            main.setBackgroundResource(
                SkinnableResourceManager.getTargetResourceId(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.card_background
                )
            )
            liveChatEtInput.requestFocus()
        }
        updateEmojiView(false)
        updateWhenKeyBoardVisible(KeyBoardType.SOFT_KEYBOARD)
    }


    /**
     * 展示表情界面
     * */
    private fun showEmoji() {
        mBinding.apply {
            mBinding.liveChatEtInput.requestFocus()
            mBinding.liveChatEtInput.setSelection(mBinding.liveChatEtInput.text?.length ?: 0)
            liveChatIvEmoji.isVisible = false
            liveChatTvSend.isVisible = true
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
            keyboardTb.isVisible = isVisible
            keyboardTvAll.isVisible = isVisible
            keyboardEmojiRecycler.isVisible = isVisible
            line.isVisible = isVisible
            bottom.isVisible = isVisible
        }
    }



    /**
     * 键盘显示时调整ui
     * */
    private fun updateWhenKeyBoardVisible(type: KeyBoardType) {
        //聊天界面和软件盘、表情键盘的hint展示不同
        mBinding.liveChatEtInput.hint = SkinnableResourceManager.getString(
            requireContext(),
            if (type == KeyBoardType.NONE) R.string.live_chat_talk else R.string.live_chat_speak,
            mViewModel.languageManager.getLanguage()
        )
        //聊天界面、表情键盘和表情键盘对应的topMargin不同
        mBinding.main.apply {
            val lp = layoutParams as FrameLayout.LayoutParams
            lp.topMargin = if (type == KeyBoardType.EMOJI) 21.dp2px else 0
            layoutParams = lp
        }
    }

    private fun openSoftKeyBoard(){
        EditTextUtils.showKeyboard(requireContext(), mBinding.liveChatEtInput)
    }

    /**
     * 禁用软件盘
     * */
    private fun hideSoftKeyBoard() {
        mBinding.liveChatEtInput.clearFocus()
        EditTextUtils.hideKeyboard(context, mBinding.liveChatEtInput)
    }

    private fun onSoftKeyBoardShow() {
        if(chatViewModel.softKeyBoardHeight == 0){
            val rootView: View = requireActivity().window.decorView;
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootView.height
            chatViewModel.softKeyBoardHeight = screenHeight - r.bottom
        }
    }

    private fun onSoftKeyBoardHide(){
        if(chatViewModel.softKeyBoardListener.value == KeyBoardType.SOFT_KEYBOARD){
            chatViewModel.addSoftKeyBoardEvent(KeyBoardType.NONE)
        }
    }

    companion object {
        const val TAG: String = "LiveSoftKeyboardFragment"
    }

}