package arch.cayenne.lib.test.ui.fragment

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.test.ui.adapter.PagerAdapter
import arch.cayenne.lib.test.data.bean.PagerBean
import arch.cayenne.lib.test.databinding.FragmentTestViewPagerBinding
import kotlin.reflect.KClass

class ViewPagerFragment : BaseFragment<EmptyViewModel, FragmentTestViewPagerBinding>() {
    override val vbClass: KClass<FragmentTestViewPagerBinding> = FragmentTestViewPagerBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {
            val gameList = List(10) { index ->
                PagerBean("${index + 1}",
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

            //navHost2.findNavController().setGraph(R.navigation.nav_graph_host2)
        }

    }

    override fun initListener() {
        mBinding.apply {
            tv1.setOnClickListener {
                val navController = findNavController()
                navController.navigate(ViewPagerFragmentDirections.actionViewPagerItem2FragmentToHomeFragment())
            }

        }
    }

    override fun onBackPressed(): Boolean {
        if(mBinding.navHost2.findNavController().navigateUp()) return true
        if(mBinding.navHost3.findNavController().navigateUp()) return true
        return super.onBackPressed()
    }

    override suspend fun createObserver() {
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