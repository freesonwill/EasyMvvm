package com.walisport.module.message.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.bumptech.glide.Glide
import com.walisport.module.message.databinding.FragmentMessageDetailBinding
import com.walisport.module.message.ui.viewmodel.MessageMainViewModel
import kotlin.reflect.KClass

/**
 * 系统消息详情
 */

class MessageDetailFragment : BaseFragment<MessageMainViewModel, FragmentMessageDetailBinding>() {

    override val vbClass: KClass<FragmentMessageDetailBinding> = FragmentMessageDetailBinding::class
    override val vmClass: KClass<MessageMainViewModel> = MessageMainViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        val content = arguments?.getString("content") ?: ""
        val url = arguments?.getString("url") ?: ""
        with(mBinding) {
            tvContent.text = content
            if(TextUtils.isEmpty(url)){
                ivImage.visibility = View.GONE
            }else {
                ivImage.visibility = View.VISIBLE
                Glide.with(this@MessageDetailFragment).load(url).into(ivImage)
            }
        }
    }

    override fun initListener() {

    }

    override fun createObserver() {

    }
}