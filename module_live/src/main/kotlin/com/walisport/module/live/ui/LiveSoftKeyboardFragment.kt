package com.walisport.module.live.ui

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.module.betslip.utisl.RecyclerItemListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.live.R
import com.walisport.module.live.data.model.EmojiData
import com.walisport.module.live.databinding.FragmentLiveSoftkeyboardLayoutBinding
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

    lateinit var mKeyboardHelper: SoftKeyboardStateHelper
    private var softKeyListener: LiveChatSoftKeyListener? = null
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
    }


    private fun sendText() {
        mBinding.liveChatEtInput.text?.clear()
    }

    override fun createObserver() {
    }

    private fun initTab() {

        val list = mViewModel.tabMenus()
        val tabs = list.map { it ->
            PagerBean("${it.id}") {
                EmojiFragment(it.id).also { frag ->
                    frag.setEmojiItemClick(itemListener)
                }
            }
        }.toList()

        mBinding.keyboardEmoji.adapter = null
        mBinding.keyboardEmoji.adapter = PagerAdapter(childFragmentManager, lifecycle, tabs)

        TabLayoutMediator(mBinding.keyboardTb, mBinding.keyboardEmoji) { tab, position ->
            val view = LayoutInflater.from(context).inflate(R.layout.item_keyboard_tab_layout, null)
            val iv: ImageView = view.findViewById(R.id.iv)
            iv.setImageResource(if (position == 0) list[position].select else list[position].normal)
            tab.setCustomView(view)
        }.attach()
        mBinding.keyboardTb.removeAllTips()

        mBinding.keyboardTb.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = it.position
                    val iv = tab.view.findViewById<ImageView>(R.id.iv)
                    iv.setImageResource(list[position].select)
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

    private fun showSoftKeyBoard() {

        mBinding.apply {
            liveChatIvKeyboard.isVisible = false
            liveChatTvSize.isVisible = false
            liveChatTvSend.isVisible = true
            liveChatIvEmoji.isVisible = true
            liveChatTvSize.isVisible = true
            keyboardTb.isVisible = false
            keyboardEmoji.isVisible = false
            line.isVisible = false
            softKeyListener?.showKeyBoard()
        }
    }

    private fun hideSoftKeyBoard() {
        EditTextUtils.hideKeyboard(context, mBinding.liveChatEtInput)
    }

    fun showChat() {
        hideSoftKeyBoard()
        mBinding.apply {
            liveChatTvSend.isVisible = false
            liveChatIvKeyboard.isVisible = false
            liveChatTvSize.isVisible = false
            liveChatIvEmoji.isVisible = true
            keyboardTb.isVisible = false
            keyboardEmoji.isVisible = false
            softKeyListener?.hideKeyboard()
        }
    }

    private fun showEmoji() {

        hideSoftKeyBoard()
        mBinding.apply {
            liveChatIvEmoji.isVisible = false
            liveChatTvSend.isVisible = true
            liveChatIvKeyboard.isVisible = true
            liveChatTvSize.isVisible = true
            liveChatIvEmoji.isVisible = false
            keyboardTb.isVisible = true
            keyboardEmoji.isVisible = true
            softKeyListener?.showKeyBoard()
        }
    }


    override fun onSoftKeyboardOpened(keyboardHeightInPx: Int) {
//        showSoftKeyBoard()
//        softKeyListener?.showKeyBoard()
    }

    override fun onSoftKeyboardClosed() {
        if (!mBinding.keyboardEmoji.isVisible) {
            softKeyListener?.hideKeyboard()
        }
    }

    interface LiveChatSoftKeyListener {
        fun showKeyBoard()

        fun hideKeyboard()
    }

    companion object {
        const val TAG: String = "LiveSoftKeyboardFragment"
    }

}