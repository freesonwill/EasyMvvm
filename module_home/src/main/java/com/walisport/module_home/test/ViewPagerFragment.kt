package com.walisport.module.home.test

import android.os.Bundle
import com.walisport.lib_base.data.viewmodel.EmptyViewModel
import com.walisport.lib_base.ui.BaseFragment
import com.walisport.lib_base.ui.viewBind
import com.walisport.module.home.databinding.FragmentViewPagerBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ViewPagerFragment : BaseFragment<EmptyViewModel, FragmentViewPagerBinding>() {
    override val mBinding: FragmentViewPagerBinding by viewBind()
    override val mViewModel: EmptyViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {
            val gameList = List(10) { index ->
                PagerBean("${index + 1}") { ViewPagerItemFragment().apply {
                    title = "${index + 1}"
                    parentFragment = this@ViewPagerFragment
                } }
            }
            viewPagerNew.adapter = PagerAdapter(childFragmentManager, lifecycle, gameList)
        }
    }

    override fun initListener() {

    }

    override fun createObserver() {

    }
}