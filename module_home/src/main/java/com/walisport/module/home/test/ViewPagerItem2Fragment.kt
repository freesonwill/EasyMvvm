package com.walisport.module.home.test

import android.os.Bundle
import androidx.navigation.fragment.navArgs
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.utils.LogUtilsExt.logd
import com.walisport.lib.common.utils.ext.NavigationExt.navigate
import com.walisport.module.home.databinding.FragmentTestViewPagerItem2Binding
import kotlin.reflect.KClass

/**
 * @author: zhangsan
 * @date: 2025/3/30 23:46
 * @description:
 */
class ViewPagerItem2Fragment : BaseFragment<EmptyViewModel, FragmentTestViewPagerItem2Binding>() {
    private val args: ViewPagerItem2FragmentArgs by navArgs()
    override val vbClass: KClass<FragmentTestViewPagerItem2Binding> = FragmentTestViewPagerItem2Binding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        "initView~~~~~~>args:$args,arguments:$arguments,$this".logd(TAG)
        mBinding.tv.text = args.title
    }

    override fun initListener() {
        mBinding.tv.setOnClickListener {
            //navigate(ViewPagerItem2FragmentDirections.actionTextViewScreenToHomeFragment3())
            //val navController = (requireActivity() as BaseNavActivity).findNavController()
            //navController.navigate(ViewPagerFragmentDirections.actionViewPagerItem2FragmentToHomeFragment())
            requireActivity().navigate(ViewPagerFragmentDirections.actionViewPagerItem2FragmentToHomeFragment())
        //            navigate(ViewPagerItem2FragmentDirections.actionTextViewScreenToHomeFragment3())
        }
    }

    override fun createObserver() {
    }
}
