package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipUnsettledBinding
import arch.cayenne.module.betslip.ui.dialog.BetSlipEarlySettledFragment
import arch.cayenne.module.betslip.utisl.BetSlipUtils
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.betSlipInit
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.initLoadMore
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.showEmptyData
import kotlin.reflect.KClass


//注单未结算
class BetSlipUnsettledFragment :
    BaseBetSlipFragment<FragmentLiveBetslipUnsettledBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipUnsettledBinding> =
        FragmentLiveBetslipUnsettledBinding::class

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
        initLoadRefresh()
    }

    override fun initListener() {
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.orderLiveData.observe(viewLifecycleOwner) {
            betSlipAdapter.submitList(it)
            showEmpty(it.isEmpty())
        }
        mViewModel.earlySettledResultLiveData.observe(viewLifecycleOwner) {
            showToast(if (it == true) getString(R.string.early_settle_success) else getString(R.string.early_settle_faile))
            mViewModel.getOrders(BetSlipEnum.UnSettled)
        }
        mViewModel.isSupportEarlySettleLiveData.observe(viewLifecycleOwner) {
            val price = it.price.toDoubleOrNull()
            if (price == null || price <= 0) {
                showToast(getString(R.string.not_support_early_settle))
                return@observe
            }
            mViewModel.selectOrder?.let { order ->
                val money = BetSlipUtils.earlySettlePrice(
                    order.betAmount, price.toString(), order.earlyBetAmount
                )
                BetSlipEarlySettledFragment.instance(money.toMoney()).apply {
                    setOnEarlySettleListener { money ->
                        mViewModel.earlyPartSettled(
                            it.betId,
                            money.getMoney(),
                            mViewModel.isSupportEarlySettleLiveData.value?.price ?: "0"
                        )
                    }
                }.show(childFragmentManager)
            }
        }
    }

    override fun updateState(state: DynamicStateLayout.States) {
        mBinding.refreshLayout.finishRefresh()
        mBinding.refreshLayout.finishLoadMore()
        mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        if (state == DynamicStateLayout.States.NETWORK_ANOMALY) {
            mBinding.recyclerView.isVisible = false
            mBinding.emptyState.isVisible = true
            mBinding.emptyState.setState(state, getString(arch.cayenne.lib.common.R.string.error_net)
            ) {
                mViewModel.refreshOrder(getBetSlipEnum())
            }
        } else {
            mBinding.recyclerView.isVisible = true
            mBinding.emptyState.isVisible = false
        }
    }

    private fun showEmpty(isEmpty: Boolean) {
        mBinding.emptyState.showEmptyData(isEmpty, mBinding.recyclerView)
    }

    private fun initRecycler() {
        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = betSlipAdapter
            it.betSlipInit()
        }
        betSlipAdapter.setEarlySettleListener(object : RecyclerItemListener<BetSlipData> {
            override fun onItemClick(
                item: BetSlipData?, position: Int
            ) {
                item?.order?.let {
                    mViewModel.isSuppportEarlySettled(it)
                }
            }
        })
        betSlipAdapter.setLiveListener(object : RecyclerItemListener<BetSlipSelectionData> {
            override fun onItemClick(item: BetSlipSelectionData?, position: Int) {

            }
        })
    }

    private fun initLoadRefresh() {
        mBinding.refreshLayout.also {
            it.initLoadMore()
            it.setOnRefreshListener {
                mViewModel.refreshOrder(BetSlipEnum.UnSettled)
            }
            it.setOnLoadMoreListener {
                mViewModel.loadMoreOrder(BetSlipEnum.UnSettled)
            }
        }
    }

    override fun getBetSlipEnum(): BetSlipEnum {
        return BetSlipEnum.UnSettled
    }
}