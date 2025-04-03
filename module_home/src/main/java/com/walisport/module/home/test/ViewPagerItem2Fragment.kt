package com.walisport.module.home.test

import android.os.Bundle
import androidx.navigation.fragment.navArgs
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment2
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.base.utils.LogUtilsExt.logd
import com.walisport.module.home.databinding.FragmentViewPagerItem2Binding
import com.walisport.module_home.test.ViewPagerItem2FragmentArgs
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author: zhangsan
 * @date: 2025/3/30 23:46
 * @description:
 */
class ViewPagerItem2Fragment : BaseFragment2<EmptyViewModel, FragmentViewPagerItem2Binding>() {
    override fun createVB(): FragmentViewPagerItem2Binding = viewBind<FragmentViewPagerItem2Binding>().value
    override fun createVM(): EmptyViewModel = viewModel<EmptyViewModel>().value

    private val args: ViewPagerItem2FragmentArgs by navArgs()

    override fun initView(savedInstanceState: Bundle?) {
        "initView~~~~~~>args:$args,arguments:$arguments,$this".logd(TAG)
        mBinding.tv.text = args.title
    }

    override fun initListener() {
        mBinding.tv.setOnClickListener {
            //findNavController().navigate(ViewPagerItem2FragmentDirections.actionTextViewScreenToHomeFragment3())
            //val navController = (requireActivity() as BaseNavActivity).findNavController()
            //navController.navigate(ViewPagerFragmentDirections.actionViewPagerItem2FragmentToHomeFragment())
            findActivityNavController().navigate(ViewPagerFragmentDirections.actionViewPagerItem2FragmentToHomeFragment())
//            findNavController().navigate(ViewPagerItem2FragmentDirections.actionTextViewScreenToHomeFragment3())
        }
    }

    override fun createObserver() {
    }
}
