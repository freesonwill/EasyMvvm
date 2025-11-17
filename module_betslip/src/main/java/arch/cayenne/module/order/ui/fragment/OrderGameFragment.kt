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
            mViewModel.setTabType(GamePageEnum.ALL)
        }
        mBinding.tvMultiple.clickNoRepeat {
            mViewModel.setTabType(GamePageEnum.MULTIPLE)
        }
        mBinding.tvBonus.clickNoRepeat {
            mViewModel.setTabType(GamePageEnum.BONUS)
        }
        mBinding.clFilter.clickNoRepeat {
            showGameFilter()
        }
    }

    override suspend fun createObserver() {
        mViewModel.tabType.observe(viewLifecycleOwner) {
            changeTabType(it)
        }
    }

    private fun changeTabType(type: GamePageEnum) {
        when (type) {
            GamePageEnum.ALL -> {
                mBinding.ivAllArrow.isSelected = true
                mBinding.tvAll.isSelected = true
                mBinding.tvBonus.isSelected = false
                mBinding.tvMultiple.isSelected = false
                mBinding.viewPager.setCurrentItem(0, false)
            }

            GamePageEnum.MULTIPLE -> {
                mBinding.ivAllArrow.isSelected = false
                mBinding.tvAll.isSelected = false
                mBinding.tvBonus.isSelected = false
                mBinding.tvMultiple.isSelected = true
                mBinding.viewPager.setCurrentItem(1, false)
            }

            GamePageEnum.BONUS -> {
                mBinding.ivAllArrow.isSelected = false
                mBinding.tvAll.isSelected = false
                mBinding.tvBonus.isSelected = true
                mBinding.tvMultiple.isSelected = false
                mBinding.viewPager.setCurrentItem(2, false)
            }
        }
    }

    private fun showGameFilter() {
        OrderSportFilterDialogFragment.newInstance().show(childFragmentManager)
    }
}