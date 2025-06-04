package com.walisport.module.live.ui

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.google.android.material.tabs.TabLayout
import com.walisport.module.live.R
import com.walisport.module.live.data.model.EmojiData
import com.walisport.module.live.databinding.FragmentLiveSoftkeyboardLayoutBinding
import com.walisport.module.live.ui.adapter.SoftAdapter
import com.walisport.module.live.ui.viewmodel.LiveSoftKeyboardViewModel
import com.walisport.module.live.utils.EditTextUtils
import com.walisport.module.live.utils.SoftKeyboardStateHelper
import kotlin.reflect.KClass

class LiveSoftKeyboardFragment :
    BaseFragment<LiveSoftKeyboardViewModel, FragmentLiveSoftkeyboardLayoutBinding>(),
    SoftKeyboardStateHelper.SoftKeyboardStateListener {
    override val vbClass: KClass<FragmentLiveSoftkeyboardLayoutBinding>
        get() = FragmentLiveSoftkeyboardLayoutBinding::class
    override val vmClass: KClass<LiveSoftKeyboardViewModel>
        get() = LiveSoftKeyboardViewModel::class

    //监听软件盘状态
    lateinit var mKeyboardHelper: SoftKeyboardStateHelper

    //监听软件盘发送事件
    private var softKeyListener: LiveChatSoftKeyListener? = null

    //表情点击
    private val itemListener = object : RecyclerItemListener<EmojiData> {
        override fun onItemClick(item: EmojiData?, position: Int) {
            if (item?.key == "del") {
                val ic = mBinding.liveChatEtInput.onCreateInputConnection(EditorInfo())
                ic?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                ic?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
            } else {
                mBinding.liveChatEtInput.text?.append(item?.key)
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mKeyboardHelper = SoftKeyboardStateHelper((context as Activity).window.decorView)
        mKeyboardHelper.addSoftKeyboardStateListener(this)
        initTab()
        initSoftRecycler()
        showChat()
    }

    fun setSoftKeyListener(listener: LiveChatSoftKeyListener) {
        this.softKeyListener = listener
    }

    override fun initListener() {
        mBinding.liveChatEtInput.setOnClickListener {
            showSoftKeyBoard()
        }
        mBinding.liveChatIvEmoji.setOnClickListener {
            showEmoji()
        }
        mBinding.liveChatTvSend.setOnClickListener {
            showChat()
            sendText()
        }
        mBinding.liveChatIvKeyboard.setOnClickListener {
            showSoftKeyBoard()
            EditTextUtils.showKeyboard(context, mBinding.liveChatEtInput)
        }
        mBinding.liveChatEtInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showSoftKeyBoard()
            }
        }
        mBinding.liveChatEtInput.imeOptions = EditorInfo.IME_ACTION_SEND
        mBinding.liveChatEtInput.setImeActionLabel("发送", EditorInfo.IME_ACTION_SEND)
        mBinding.liveChatEtInput.setOnEditorActionListener { v, actionId, event ->
            "actionId $actionId".logd("aaa")
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                showChat()
                sendText()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    /**
     * 发送消息
     * */
    private fun sendText() {
        mBinding.liveChatEtInput.text?.clear()
    }

    override fun createObserver() {
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
                            mBinding.keyboardEmojiRecycler.scrollToPosition(position)
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
        mBinding.keyboardEmojiRecycler.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            val softAdapter = SoftAdapter()
            softAdapter.setItemListener(itemListener)
            softAdapter.submitList(mViewModel.softData())
            adapter = softAdapter
        }
    }


    /**
     * 展示软件盘
     * */
    private fun showSoftKeyBoard() {
        mBinding.apply {
            liveChatEtInput.requestFocus()
            liveChatEtInput.setSelection(liveChatEtInput.text?.length ?: 0)
            liveChatIvKeyboard.isVisible = false
            liveChatTvSize.isVisible = false
            liveChatTvSend.isVisible = true
            liveChatIvEmoji.isVisible = true
            liveChatTvSize.isVisible = true
            keyboardTb.isVisible = false
            emojiDel.isVisible = false
            keyboardEmojiRecycler.isVisible = false
            line.isVisible = false
            softKeyListener?.showKeyBoard()
        }
    }

    /**
     * 禁用软件盘
     * */
    private fun hideSoftKeyBoard() {
        EditTextUtils.hideKeyboard(context, mBinding.liveChatEtInput)
    }

    /**
     * 展示聊天界面
     * */
    fun showChat() {
        hideSoftKeyBoard()
        mBinding.apply {
            liveChatTvSend.isVisible = false
            liveChatIvKeyboard.isVisible = false
            liveChatTvSize.isVisible = false
            liveChatIvEmoji.isVisible = true
            keyboardTb.isVisible = false
            keyboardEmojiRecycler.isVisible = false
            emojiDel.isVisible = false
            line.isVisible = false
            softKeyListener?.hideKeyboard()
            main.setBackgroundResource(
                SkinnableResourceManager.getTargetResourceId(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.main_background
                )
            )
        }
    }

    /**
     * 展示表情界面
     * */
    private fun showEmoji() {
        hideSoftKeyBoard()
        mBinding.apply {
            liveChatEtInput.requestFocus()
            liveChatEtInput.setSelection(liveChatEtInput.text?.length ?: 0)
            liveChatIvEmoji.isVisible = false
            liveChatTvSend.isVisible = true
            liveChatIvKeyboard.isVisible = true
            liveChatTvSize.isVisible = true
            liveChatIvEmoji.isVisible = false
            keyboardTb.isVisible = true
            keyboardEmojiRecycler.isVisible = true
            emojiDel.isVisible = false
            line.isVisible = true
            main.setBackgroundResource(
                SkinnableResourceManager.getTargetResourceId(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.card_background
                )
            )
            softKeyListener?.showKeyBoard()
        }
    }

    /**
     * 当软件盘弹出时
     * */
    override fun onSoftKeyboardOpened(keyboardHeightInPx: Int) {
        mBinding.main.setBackgroundResource(
            SkinnableResourceManager.getTargetResourceId(
                requireContext(),
                arch.cayenne.lib.common.R.color.card_background
            )
        )

    }

    override fun onSoftKeyboardClosed() {

    }

    interface LiveChatSoftKeyListener {
        fun showKeyBoard()

        fun hideKeyboard()
    }

    companion object {
        const val TAG: String = "LiveSoftKeyboardFragment"
    }

}