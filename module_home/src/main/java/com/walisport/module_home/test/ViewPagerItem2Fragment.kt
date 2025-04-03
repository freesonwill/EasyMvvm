package com.walisport.module.home.test

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.home.databinding.FragmentViewPagerItem2Binding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author: zhangsan
 * @date: 2025/3/30 23:46
 * @description:
 */
class ViewPagerItem2Fragment : BaseFragment<EmptyViewModel, FragmentViewPagerItem2Binding>() {
    override val mBinding: FragmentViewPagerItem2Binding by viewBind()
    override val mViewModel: EmptyViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        val args:ViewPagerItem2FragmentArgs by navArgs()
        mBinding.tv.text = args.title
    }

    override fun initListener() {
        mBinding.tv.setOnClickListener {
            val navController = requireActivity().findNavController(com.walisport.lib.common.R.id.nav_host)
            navController.navigate(ViewPagerFragmentDirections.actionViewPagerItem2FragmentToHomeFragment())
            //findNavController().navigate(ViewPagerItem2FragmentDirections.actionTextViewScreenToHomeFragment3())
        }
    }

    override fun createObserver() {
    }

}
