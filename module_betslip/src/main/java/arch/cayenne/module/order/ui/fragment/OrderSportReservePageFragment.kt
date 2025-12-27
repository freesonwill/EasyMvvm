package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import androidx.core.view.isVisible
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.ui.fragment.ReserveDialogFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.FragmentOrderSportPageBinding
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.ui.adapter.OrderBettingAdapter
import arch.cayenne.module.order.ui.viewmodel.OrderReserveViewModel
import arch.cayenne.module.order.utils.OrderItemDecoration
import kotlin.reflect.KClass

class OrderSportReservePageFragment :
    BaseFragment<OrderReserveViewModel, FragmentOrderSportPageBinding>() {

    override val vbClass: KClass<FragmentOrderSportPageBinding> =
        FragmentOrderSportPageBinding::class
    override val vmClass: KClass<OrderReserveViewModel> = OrderReserveViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        val adapter =
            OrderBettingAdapter(
                OrderSportPageEnum.RESERVE, object : OrderBettingAdapter.OnOrderClickListener {
                    override fun onDateClick() {
                        showDateDialog()
                    }

                    override fun onItemSingleClick(bean: BetSlipOrderBean) {
                    }

                    override fun onShareClick(bean: BetSlipData) {
                    }

                },
                reserveListener = object : OrderBettingAdapter.OrderReserveListener {
                    override fun onCancelReserve(bean: BetSlipReserveBean) {
                        if (mViewModel.checkNetwork()) {
                            CommonDialog.newInstance(
                                "",
                                getString(R.string.confirm_cancel_reserve),
                                getString(R.string.cancel_reserve),
                                getString(R.string.not_yet)
                            ).also {
                                it.setOnOkClickListener {
                                    mViewModel.cancelReserve(bean)
                                }
                                it.show(childFragmentManager)
                            }
                        }
                    }

                    override fun onModifyReserve(
                        bean: BetSlipReserveBean,
                        locationX: Int,
                        locationY: Int,
                        viewHeight: Int
                    ) {
                        if (mViewModel.checkNetwork()) {
                            childFragmentManager.setFragmentResultListener(
                                ReserveDialogFragment.KEY_RESULT,
                                viewLifecycleOwner
                            ) { _, bundle ->
                                childFragmentManager.clearFragmentResultListener(
                                    ReserveDialogFragment.KEY_RESULT
                                )
                                if (bundle.getString(ReserveDialogFragment.KEY_RESULT) == ReserveDialogFragment.VALUE_RESERVE_COMPLETE) {
                                    val odds = bundle.getInt(ReserveDialogFragment.KEY_ODDS_RESULT)
                                    mViewModel.modifyReserveOdds(
                                        bean,
                                        odds.getDisplayOdds().toOdds()
                                    )
                                }
                            }
                            ReserveDialogFragment.newInstance(
                                locationX,
                                locationY,
                                viewHeight,
                                odds = bean.selection.odds.getDisplayOdds().toOdds()
                            ).show(childFragmentManager)
                        }
                    }
                })
        mBinding.rvContent.adapter = adapter

        val decoration = OrderItemDecoration(10.dp2px)
        mBinding.rvContent.addItemDecoration(decoration)
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
        mViewModel.cancelReserveResultLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled(viewLifecycleOwner)?.let {
                showToast(if (it) getString(R.string.cancel_reserve_success) else getString(R.string.cancel_reserve_fail))
            }
        }
        mViewModel.modifyReserveResultLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled(viewLifecycleOwner)?.let {
                showToast(if (it) getString(R.string.modify_odds_success) else getString(R.string.modify_odds_fail))
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
            mViewModel.setReserveData(v1, v2)
        }
        dialog.show(childFragmentManager)
    }
}