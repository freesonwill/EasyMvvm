package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.betslip.databinding.FragmentOrderSportPageBinding
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.ui.adapter.OrderBettingAdapter
import arch.cayenne.module.order.ui.viewmodel.OrderSportPageViewModel
import arch.cayenne.module.order.utils.OrderItemDecoration
import kotlin.reflect.KClass

class OrderSportPageFragment :
    BaseFragment<OrderSportPageViewModel, FragmentOrderSportPageBinding>() {

    override val vbClass: KClass<FragmentOrderSportPageBinding> =
        FragmentOrderSportPageBinding::class
    override val vmClass: KClass<OrderSportPageViewModel> = OrderSportPageViewModel::class

    private val type: OrderSportPageEnum
        get() {
            val pageIndex = arguments?.getInt("pageIndex") ?: 0
            return OrderSportPageEnum.entries[pageIndex]
        }

    override fun initView(savedInstanceState: Bundle?) {
        val adapter = OrderBettingAdapter(type)
        mBinding.rvContent.adapter = adapter

        val decoration = OrderItemDecoration(10.dp2px)
        mBinding.rvContent.addItemDecoration(decoration)
    }

    override fun initData() {
        super.initData()
        mViewModel.setType(type)
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
        mViewModel.orderDataListener.observe(viewLifecycleOwner) {
            (mBinding.rvContent.adapter as OrderBettingAdapter).submitList(it)
        }
    }
}