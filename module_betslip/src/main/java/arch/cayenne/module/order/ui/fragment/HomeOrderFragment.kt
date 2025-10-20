package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import androidx.core.os.bundleOf
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.DrawerAction.ACTION_OPEN
import arch.cayenne.lib.common.data.constants.DrawerAction.KEY_ACTION
import arch.cayenne.lib.common.data.constants.DrawerAction.REQUEST_KEY_DRAWER
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.module.betslip.databinding.FragmentHomeOrderBinding
import arch.cayenne.module.order.data.constants.OrderPageEnum
import arch.cayenne.module.order.ui.viewmodel.HomeOrderViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.KClass

class HomeOrderFragment: BaseFragment<HomeOrderViewModel, FragmentHomeOrderBinding>() {

    override val vbClass: KClass<FragmentHomeOrderBinding> = FragmentHomeOrderBinding::class
    override val vmClass: KClass<HomeOrderViewModel> = HomeOrderViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        val page = OrderPageEnum.entries.toTypedArray()
        mBinding.viewPager.adapter = PagerAdapter(childFragmentManager, lifecycle, page.map { it.page })
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager,false) { tab, position ->
            tab.text = page[position].page.title
        }.attach()
        mBinding.tabLayout.post {
            mBinding.tabLayout.removeAllTips()
        }
        mBinding.viewPager.setupHorizontalScrollDegree()
    }
    override fun initListener() {
        with (mBinding) {
            ivHam.clickNoRepeat {
                requireActivity().supportFragmentManager.setFragmentResult(
                    REQUEST_KEY_DRAWER,
                    bundleOf(KEY_ACTION to ACTION_OPEN)
                )
            }
        }
    }

    override suspend fun createObserver() {
    }

    override fun onStart() {
        super.onStart()
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
    }
}