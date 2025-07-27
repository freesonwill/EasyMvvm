package com.walisport.module.message.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import com.bumptech.glide.Glide
import com.walisport.module.message.R
import com.walisport.module.message.databinding.FragmentMessageDetailBinding
import com.walisport.module.message.ui.viewmodel.MessageMainViewModel
import kotlin.reflect.KClass

/**
 * 系统消息详情
 */

class MessageDetailFragment : BaseFragment<MessageMainViewModel, FragmentMessageDetailBinding>() {

    override val vbClass: KClass<FragmentMessageDetailBinding> = FragmentMessageDetailBinding::class
    override val vmClass: KClass<MessageMainViewModel> = MessageMainViewModel::class

    companion object {
        const val MSG_SYS = 1
        const val MSG_ACT = 2
        const val MSG_MAT = 3
        const val MSG_PAY = 4
    }

    override fun initView(savedInstanceState: Bundle?) {
        val content = arguments?.getString("content") ?: ""
        val url = arguments?.getString("url") ?: ""
        val title = arguments?.getString("title") ?: ""
        val time = arguments?.getString("time") ?: ""
        val type = arguments?.getInt("type") ?: MSG_SYS
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.detail, {
                findNavController().navigateUp()
            })
            tvDetailTitle.text = title
            tvDetailContent.text = content
            tvDetailTime.text = time
            if (TextUtils.isEmpty(url)) {
                ivDetailImage.visibility = View.GONE
            } else {
                ivDetailImage.visibility = View.VISIBLE
                Glide.with(this@MessageDetailFragment).load(url).into(ivDetailImage)
            }
            when (type) {
                MSG_SYS -> ivDetailIcon.background = R.drawable.icon_message_wh.getDrawable()
                MSG_ACT -> ivDetailIcon.background = R.drawable.icon_message_hd.getDrawable()
                MSG_MAT -> ivDetailIcon.background = R.drawable.icon_message_rm.getDrawable()
                MSG_PAY -> ivDetailIcon.background = R.drawable.icon_message_cz.getDrawable()
            }
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}