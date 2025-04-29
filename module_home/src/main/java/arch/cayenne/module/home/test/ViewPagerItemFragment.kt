package arch.cayenne.module.home.test

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentTestViewPagerItemBinding
import kotlin.reflect.KClass

class ViewPagerItemFragment : BaseFragment<EmptyViewModel, FragmentTestViewPagerItemBinding>() {
    override val vbClass: KClass<FragmentTestViewPagerItemBinding> = FragmentTestViewPagerItemBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    var title: String = ""

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.post {
            // 创建一个 Bundle 传递参数
            val bundle = bundleOf("title" to title)
            val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_vp) as NavHostFragment
            // 传递参数到 `NavGraph`
            val navController = navHostFragment.navController
            navController.setGraph(R.navigation.nav_graph_test_vp_home, bundle)

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