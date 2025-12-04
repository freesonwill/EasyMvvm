package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.FragmentOrderSportPageBinding
import arch.cayenne.module.betslip.utisl.BetSlipUtils
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
        val adapter = if (type == OrderSportPageEnum.UNSETTLED) {
            OrderBettingAdapter(type, object : OrderBettingAdapter.OrderEarlySettleListener {
                override fun onEarlySettle(bean: BetSlipOrderBean) {
                    mViewModel.isSupportEarlySettled(bean)
                }
            }, object : OrderBettingAdapter.OrderDataSelectorListener {
                override fun onDateClicked() {
                    // TODO 測試
                    OrderDateDialogFragment().show(childFragmentManager)
                }
            })
        } else {
            OrderBettingAdapter(type)
        }
        mBinding.rvContent.adapter = adapter

        val decoration = OrderItemDecoration(10.dp2px)
        mBinding.rvContent.addItemDecoration(decoration)
    }

    override fun initData() {
        super.initData()
        mViewModel.setType(type)
    }

    override fun initListener() {
        mViewModel.isSupportEarlySettleLiveData.observe(viewLifecycleOwner) {
            val price = it.price.toDoubleOrNull()
            if (price == null || price <= 0) {
                showToast(getString(R.string.not_support_early_settle))
                return@observe
            }
            mViewModel.selectOrder?.let { order ->
                val money = BetSlipUtils.earlySettlePrice(
                    order.betAmount, order.earlyBetAmount, order.earlySettlePrice.price
                )
                val f = OrderSportEarlySettleFragment.newInstance(money, order.currency).apply {
                    setOnConfirmListener {
                        mViewModel.earlyPartSettled(it.betId, money, it.price)
                    }
                }
                f.show(childFragmentManager)
            }
        }
    }

    override suspend fun createObserver() {
        mViewModel.orderDataListener.observe(viewLifecycleOwner) {
            (mBinding.rvContent.adapter as OrderBettingAdapter).submitList(it)
        }
    }
}