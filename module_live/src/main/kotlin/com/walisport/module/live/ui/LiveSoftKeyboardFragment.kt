package com.walisport.module.live.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
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
import com.walisport.module.live.utils.SoftKeyboardStateHelper
import kotlin.reflect.KClass

class LiveSoftKeyboardFragment :
    BaseFragment<LiveSoftKeyboardViewModel, FragmentLiveSoftkeyboardLayoutBinding>(),
    SoftKeyboardStateHelper.SoftKeyboardStateListener {
    override val vbClass: KClass<FragmentLiveSoftkeyboardLayoutBinding>
        get() = FragmentLiveSoftkeyboardLayoutBinding::class
    override val vmClass: KClass<LiveSoftKeyboardViewModel>
        get() = LiveSoftKeyboardViewModel::class
    private val chatViewModel: LiveChatViewModel by sharedViewModel<LiveChatViewModel, LiveChatFragment>()
    private var isEmojiKeyBoard:Boolean = false // 当点击显示表情键盘时，防止focus后弹出软件盘

    //监听软件盘状态
    var mKeyboardHelper: SoftKeyboardStateHelper? = null

    //监听软件盘发送事件
    private var softKeyListener: LiveChatSoftKeyListener? = null

    //表情点击
    private val itemListener = object : RecyclerItemListener<EmojiData> {
        override fun onItemClick(item: EmojiData?, position: Int) {
            mBinding.liveChatEtInput.text?.append(item?.key)
        }
    }


    override fun initView(savedInstanceState: Bundle?) {
        mKeyboardHelper = SoftKeyboardStateHelper((context as Activity).window.decorView)
        mKeyboardHelper?.addSoftKeyboardStateListener(this)
        initTab()
        initSoftRecycler()
    }

    fun setSoftKeyListener(listener: LiveChatSoftKeyListener) {
        this.softKeyListener = listener
    }

    @SuppressLint("SetTextI18n")
    override fun initListener() {
        mBinding.emojiDel.setOnClickListener {
            val ic = mBinding.liveChatEtInput.onCreateInputConnection(EditorInfo())
            ic?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
            ic?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
        }

        mBinding.liveChatEtInput.setOnClickListener {
            chatViewModel.addSoftKeyBoardEvent(KeyBoardType.SOFT_KEYBOARD)
        }
        mBinding.liveChatIvEmoji.setOnClickListener {
            isEmojiKeyBoard = true
            mBinding.liveChatEtInput.requestFocus()
            mBinding.liveChatEtInput.setSelection(mBinding.liveChatEtInput.text?.length ?: 0)
            chatViewModel.addSoftKeyBoardEvent(KeyBoardType.EMOJI)
        }
        mBinding.liveChatTvSend.setOnClickListener {
            chatViewModel.addSoftKeyBoardEvent(KeyBoardType.NONE)
            sendText()
        }
        mBinding.liveChatIvKeyboard.setOnClickListener {
            chatViewModel.addSoftKeyBoardEvent(KeyBoardType.SOFT_KEYBOARD)
        }
        mBinding.liveChatEtInput.setOnFocusChangeListener { _, hasFocus ->
             // 1.当前softKeyBoard = emoji 拦截 2.当focus == emoji 拦截 3.当前softkeyboard是emoji 并且current是emoji 不拦截
            if (!hasFocus || isEmojiKeyBoard) {
                return@setOnFocusChangeListener
            }
            if(chatViewModel.currentSoftKeyboard.value != KeyBoardType.SOFT_KEYBOARD){
                EditTextUtils.hideKeyboard(requireContext(), mBinding.liveChatEtInput)
            }
            chatViewModel.addSoftKeyBoardEvent(KeyBoardType.SOFT_KEYBOARD)
        }
        mBinding.liveChatEtInput.imeOptions = EditorInfo.IME_ACTION_SEND
        mBinding.liveChatEtInput.setImeActionLabel(
            SkinnableResourceManager.getString(
                requireContext(),
                R.string.live_chat_send,
                mViewModel.languageManager.getLanguage()
            ), EditorInfo.IME_ACTION_SEND
        )
        mBinding.liveChatEtInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                chatViewModel.addSoftKeyBoardEvent(KeyBoardType.NONE)
                sendText()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        mBinding.liveChatEtInput.filters = arrayOf(EmojiEditFilter(mBinding.liveChatTvSize))
    }

    override fun onPause() {
        super.onPause()
        chatViewModel.addSoftKeyBoardEvent(KeyBoardType.NONE)
    }

    /**
     * 发送消息
     * */
    private fun sendText() {
        val text = mBinding.liveChatEtInput.text.toString()
        chatViewModel.sendMsgToChat(text)
        mBinding.liveChatEtInput.text?.clear()
    }

    override fun createObserver() {
        chatViewModel.currentSoftKeyboard.observe(viewLifecycleOwner) {
            when (it) {
                KeyBoardType.SOFT_KEYBOARD -> showSoftKeyBoard()
                KeyBoardType.EMOJI -> showEmoji()
                KeyBoardType.NONE -> showChat()
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
                    mBinding.emojiDel.isVisible = position == 0

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
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            val softAdapter = SoftAdapter()
            softAdapter.setItemListener(itemListener)
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
        hideSoftKeyBoard()
        mBinding.apply {
            liveChatIvEmoji.isVisible = true
            liveChatTvSend.isVisible = false
            liveChatIvKeyboard.isVisible = false
            liveChatTvSize.isVisible = false
            main.setBackgroundResource(
                SkinnableResourceManager.getTargetResourceId(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.main_background
                )
            )
        }
        updateEmojiView(false)
        updateWhenKeyBoardVisible(KeyBoardType.NONE)
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
        }
        updateEmojiView(false)
        updateWhenKeyBoardVisible(KeyBoardType.SOFT_KEYBOARD)
        EditTextUtils.showKeyboard(requireContext(),mBinding.liveChatEtInput)
    }


    /**
     * 展示表情界面
     * */
    private fun showEmoji() {
        mBinding.apply {
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
        isEmojiKeyBoard = false
    }

    private fun updateEmojiView(isVisible: Boolean) {
        mBinding.apply {
            liveChatIvKeyboard.isVisible = isVisible
            keyboardTb.isVisible = isVisible
            keyboardEmojiRecycler.isVisible = isVisible
            emojiDel.isVisible = isVisible
            line.isVisible = isVisible
            bottom.isVisible = isVisible
        }
    }


    override fun onDestroyView() {
        mKeyboardHelper?.removeSoftKeyboardStateListener(this)
        mKeyboardHelper = null
        super.onDestroyView()
    }

    /**
     * 当软件盘弹出时
     * */
    override fun onSoftKeyboardOpened(keyboardHeightInPx: Int) {
    }

    override fun onSoftKeyboardClosed() {
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

    /**
     * 禁用软件盘
     * */
    private fun hideSoftKeyBoard() {
        EditTextUtils.hideKeyboard(context, mBinding.liveChatEtInput)
    }

    interface LiveChatSoftKeyListener {
        fun showKeyBoard(isEmoji: Boolean)

        fun hideKeyboard()
    }

    companion object {
        const val TAG: String = "LiveSoftKeyboardFragment"
    }

}