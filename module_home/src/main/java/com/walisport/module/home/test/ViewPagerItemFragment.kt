package com.walisport.module.home.test

import android.os.Bundle
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.getViewBind
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.home.R
import com.walisport.module.home.databinding.FragmentViewPagerBinding
import com.walisport.module.home.databinding.FragmentViewPagerItemBinding
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

class ViewPagerItemFragment : BaseFragment<EmptyViewModel, FragmentViewPagerItemBinding>() {
    override val vbClass: KClass<FragmentViewPagerItemBinding> = FragmentViewPagerItemBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    var title: String = ""

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.post {
            // 创建一个 Bundle 传递参数
            val bundle = bundleOf("title" to title)
            val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_vp) as NavHostFragment
            // 传递参数到 `NavGraph`
            val navController = navHostFragment.navController
            navController.setGraph(R.navigation.nav_graph_vp_home, bundle)

            // 4. 手动 navigate 到 `startDestination`
            val startDestination = navController.graph.startDestinationId
            navController.navigate(startDestination, bundle)
        }
    }

    override fun initListener() {

    }

    override fun createObserver() {

    }

}