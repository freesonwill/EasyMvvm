package com.walisport.module.live.ui

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import arch.cayenne.lib.base.ui.BaseFragment
import com.walisport.module.live.data.model.EmojiData
import com.walisport.module.live.databinding.FragmentSoftkeyboardEmojiBinding
import com.walisport.module.live.ui.adapter.LiveEmojiAdapter
import com.walisport.module.live.ui.viewmodel.EmojiViewModel
import com.walisport.module.live.utils.RecyclerItemListener
import kotlin.reflect.KClass

class EmojiFragment(type: Int) : BaseFragment<EmojiViewModel, FragmentSoftkeyboardEmojiBinding>() {
    override val vbClass: KClass<FragmentSoftkeyboardEmojiBinding>
        get() = FragmentSoftkeyboardEmojiBinding::class
    override val vmClass: KClass<EmojiViewModel>
        get() = EmojiViewModel::class
    private var emojiType: Int = type
    private var itemListener: RecyclerItemListener<EmojiData>? = null

    override fun initView(savedInstanceState: Bundle?) {
        initEmoji()
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

    fun setEmojiItemClick(listener: RecyclerItemListener<EmojiData>) {
        itemListener = listener
    }

    private fun initEmoji() {
        when (emojiType) {
            0 -> {
                mBinding.keyboardTvAll.isVisible = true
                val layoutManager = GridLayoutManager(context, 8)
                mBinding.keyboardEmoji.layoutManager = layoutManager
                val adapter = LiveEmojiAdapter()
                adapter.setItemListener(itemListener)
                adapter.setType(0)
                adapter.submitList(mViewModel.getNormalEmojis())
                mBinding.keyboardEmoji.adapter = adapter
            }

            1 -> {
                val layoutManager = GridLayoutManager(context, 4)
                mBinding.keyboardEmoji.layoutManager = layoutManager
                val adapter = LiveEmojiAdapter()
                adapter.setItemListener(itemListener)
                adapter.setType(1)
                adapter.submitList(mViewModel.getBidEmojis())
                mBinding.keyboardEmoji.adapter = adapter
            }


        }
    }

}