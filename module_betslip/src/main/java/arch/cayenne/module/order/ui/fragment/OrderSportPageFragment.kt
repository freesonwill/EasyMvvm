package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import androidx.core.view.isVisible
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.MsgType
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.FragmentOrderSportPageBinding
import arch.cayenne.module.betslip.utisl.BetSlipUtils
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.ui.adapter.OrderBettingAdapter
import arch.cayenne.module.order.ui.viewmodel.ChatChooseViewModel
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
    private var chooseViModel: ChatChooseViewModel? = null


    override fun initView(savedInstanceState: Bundle?) {
        val adapter = if (type == OrderSportPageEnum.UNSETTLED) {
            OrderBettingAdapter(
                type, object : OrderBettingAdapter.OnOrderClickListener {
                    override fun onDateClick() {
                        showDateDialog()
                    }

                    override fun onItemSingleClick(bean: BetSlipSelectionData) {
                        chooseViModel?.clickBtn(MsgType.BET_SPORT)
                    }

                    override fun onShareClick(bean: BetSlipData) {
                        TODO("Not yet implemented")
                    }

                },
                object : OrderBettingAdapter.OrderEarlySettleListener {
                    override fun onEarlySettle(bean: BetSlipOrderBean) {
                        mViewModel.isSupportEarlySettled(bean)
                    }
                })
        } else {
            OrderBettingAdapter(type)
        }
        mBinding.rvContent.adapter = adapter

        val decoration = OrderItemDecoration(10.dp2px)
        mBinding.rvContent.addItemDecoration(decoration)
        checkChooseFragment()
    }

    override fun initData() {
        super.initData()
        mViewModel.setOrderData(type)
    }

    override fun initListener() {
        mBinding.btnNoData.setOnClickListener {
            showDateDialog()
        }
    }

    override suspend fun createObserver() {
        mViewModel.orderDataListener.observe(viewLifecycleOwner) {
            (mBinding.rvContent.adapter as OrderBettingAdapter).submitList(it)
        }
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
        mViewModel.intentEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled(viewLifecycleOwner)?.let {
                mBinding.groupNoData.isVisible = it == DataState.DataEmpty
            }
        }
    }

    private fun showDateDialog() {
        val dialog = OrderDateDialogFragment()
        dialog.setListener { v1, v2 ->
            mViewModel.setOrderData(type, v1, v2)
        }
        dialog.show(childFragmentManager)
    }

    private fun checkChooseFragment() {
        if (parentFragment?.parentFragment is ChatChooseBetFragment) {
            chooseViModel = sharedViewModel<ChatChooseViewModel, ChatChooseBetFragment>().value
        }
    }
}