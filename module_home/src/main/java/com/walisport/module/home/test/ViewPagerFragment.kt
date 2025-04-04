package com.walisport.module.home.test

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.module.home.databinding.FragmentTestViewPagerBinding
import kotlin.reflect.KClass

class ViewPagerFragment : BaseFragment<EmptyViewModel, FragmentTestViewPagerBinding>() {
    override val vbClass: KClass<FragmentTestViewPagerBinding> = FragmentTestViewPagerBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {
            val gameList = List(10) { index ->
                PagerBean("${index + 1}",
                    ViewPagerItem2Fragment().apply {
                        arguments = ViewPagerItem2FragmentArgs(title = "${index + 1}").toBundle()
                    }
                    ) {
                    //创建navHostFragment
                    /*NavHostFragment.create(
                        R.navigation.nav_graph_vp_home,
                        ViewPagerItem2FragmentArgs(title = "${index + 1}").toBundle()
                    )*/
                    ViewPagerItem2Fragment().apply {
                        arguments = ViewPagerItem2FragmentArgs(title = "${index + 1}").toBundle()
                    }
                }
            }
            viewPagerNew.adapter = PagerAdapter(childFragmentManager, lifecycle, gameList)
        }
    }

    override fun initListener() {
        mBinding.tv1.setOnClickListener {
            val navController = findNavController()
            navController.navigate(ViewPagerFragmentDirections.actionViewPagerItem2FragmentToHomeFragment())
        }
    }

    override fun createObserver() {
    }

   /* override fun onDestroyView() {
        super.onDestroyView()
        mBinding.viewPagerNew.adapter = null
        *//*val fragmentManager = childFragmentManager
        val fragments = fragmentManager.fragments

        if (fragments.isNotEmpty()) {
            val transaction = fragmentManager.beginTransaction()
            for (fragment in fragments) {
                transaction.remove(fragment)
            }
            transaction.commitNowAllowingStateLoss()
        }*//*
    }*/

}