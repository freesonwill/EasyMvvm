package com.walisport.module_home.test

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import com.walisport.lib_base.data.viewmodel.EmptyViewModel
import com.walisport.lib_base.ui.BaseFragment
import com.walisport.lib_base.ui.viewBind
import com.walisport.module_home.R
import com.walisport.module_home.databinding.FragmentViewPagerItemBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ViewPagerItemFragment : BaseFragment<EmptyViewModel, FragmentViewPagerItemBinding>() {
    override val mBinding: FragmentViewPagerItemBinding by viewBind()
    override val mViewModel: EmptyViewModel by viewModel()
    var title: String = ""
    var parentFragment: ViewPagerFragment? = null

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