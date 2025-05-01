package com.walisport.module.feedback.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
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

    }


    override fun initListener() {

    }

    override fun createObserver() {

    }


    override fun initData() {
        super.initData()
    }


}