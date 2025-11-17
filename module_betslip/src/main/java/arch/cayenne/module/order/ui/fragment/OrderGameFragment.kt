package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.betslip.databinding.FragmentOrderGameBinding
import arch.cayenne.module.order.data.constants.GamePageEnum
import arch.cayenne.module.order.ui.viewmodel.OrderGameViewModel
import kotlin.reflect.KClass

class OrderGameFragment : BaseFragment<OrderGameViewModel, FragmentOrderGameBinding>() {

    override val vbClass: KClass<FragmentOrderGameBinding> = FragmentOrderGameBinding::class
    override val vmClass: KClass<OrderGameViewModel> = OrderGameViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            val page = GamePageEnum.entries.toTypedArray()
            viewPager.adapter = PagerAdapter(childFragmentManager, lifecycle, page.map { it.page })
        }
    }

    override fun initListener() {
        mBinding.clAll.clickNoRepeat {
            mBinding.viewPager.setCurrentItem(0,false)
        }
        mBinding.tvMultiple.clickNoRepeat {
            mBinding.viewPager.setCurrentItem(1,false)
        }
        mBinding.tvBonus.clickNoRepeat {
            mBinding.viewPager.setCurrentItem(2,false)
        }
        mBinding.clFilter.clickNoRepeat {
            showGameFilter()
        }
    }

    override suspend fun createObserver() {
    }

    private fun showGameFilter() {
        OrderSportFilterDialogFragment.newInstance().show(childFragmentManager)
    }
}