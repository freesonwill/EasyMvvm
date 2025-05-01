package com.walisport.module.feedback.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.feedback.R
import com.walisport.module.feedback.databinding.FragmentFeedbackMainBinding
import com.walisport.module.feedback.ui.viewmodel.FeedbackMainViewModel
import kotlin.reflect.KClass

/**
 * 反馈详情页
 */
class FeedbackMainFragment : BaseFragment<FeedbackMainViewModel, FragmentFeedbackMainBinding>() {

    override val vbClass: KClass<FragmentFeedbackMainBinding> = FragmentFeedbackMainBinding::class
    override val vmClass: KClass<FeedbackMainViewModel> = FeedbackMainViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.feedback_title.getString(), {
                findNavController().navigateUp()
            })


        }
    }


    override fun initListener() {

    }

    override fun createObserver() {

    }


    override fun initData() {
        super.initData()
    }


}