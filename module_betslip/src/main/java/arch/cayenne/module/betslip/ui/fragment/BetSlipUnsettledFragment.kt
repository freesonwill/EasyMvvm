package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.helper.showToast
import galaxy.common.proto.Common
import kotlin.reflect.KClass
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipUnsettledBinding
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.ui.dialog.BetSlipEarlySettledFragment
import arch.cayenne.module.betslip.utisl.BetSlipUtils
import arch.cayenne.module.betslip.utisl.RecyclerItemListener


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
        mViewModel.orderLiveData.observe(viewLifecycleOwner) {
            mBinding.refreshLayout.finishRefresh()
            mBinding.refreshLayout.finishLoadMore()
            if (!it.isNullOrEmpty()) {
                updateData(it)
            }
            showEmpty()
        }
        mViewModel.earlySettledResultLiveData.observe(viewLifecycleOwner) {
            showToast(if (it == true) getString(R.string.early_settle_success) else getString(R.string.early_settle_faile))
            mViewModel.getOrders(BetSlipEnum.UnSettled)
        }
        mViewModel.earlySettlePriceLiveData.observe(viewLifecycleOwner) {
            val price = it.price.toDoubleOrNull()
            if (price == null || price <= 0) {
                showToast(getString(R.string.not_support_early_settle))
                return@observe
            }
            mViewModel.selectOrder?.let { order ->
                val money = BetSlipUtils.earlySettlePrice(
                    order.betAmount, price.toString(), order.earlyBetAmount
                )
                BetSlipEarlySettledFragment.instance(it.betId, money.toDouble()).apply {
                    setOnEarlySettleListener { betId, money ->
                        mViewModel.earlyPartSettled(
                            betId,
                            money.toString(),
                            mViewModel.earlySettlePriceLiveData.value?.price ?: "0"
                        )
                    }
                }.show(childFragmentManager)
            }
        }
    }

    private fun showEmpty() {
        val flag = mBinding.recyclerView.adapter?.let {
            val adapter = it as BetSlipAdapter
            adapter.currentList.isEmpty()
        } ?: true
        if (flag) {
            mBinding.emptyState.isVisible = true
            mBinding.refreshLayout.isVisible = false
            mBinding.emptyState.setState(
                DynamicStateLayout.States.DATA_EMPTY,
                getString(R.string.lineup_empty)
            )
        } else {
            mBinding.emptyState.isVisible = false
            mBinding.refreshLayout.isVisible = true
        }
    }

    private fun updateData(orders: List<Common.Order>) {
        val list = orders.map {
            val expandedEnum =
                if (it.selectionsList.size <= 3) BetSlipExpandedEnum.Hide else BetSlipExpandedEnum.Fold
            BetSlipData(
                order = it, expandedEnum = expandedEnum
            )
        }.toList()
        mBinding.recyclerView.adapter?.let {
            val adapter = it as BetSlipAdapter
            adapter.submitList(list)
        }
    }

    private fun initRecycler() {
        val adapter =
            BetSlipAdapter(BetSlipEnum.UnSettled)
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.item_divide_live_bet_recycler
            )!!
        )
        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter
            it.setItemViewCacheSize(10)
            it.addItemDecoration(divider)
            it.setRecycledViewPool(RecyclerView.RecycledViewPool())
        }
        adapter.setEarlySettleListener(object : RecyclerItemListener<BetSlipData> {
            override fun onItemClick(
                item: BetSlipData?, position: Int
            ) {
                item?.order?.let {
                    mViewModel.selectOrder = it
                    mViewModel.earlySettledPrice(it.betId)
                }
            }
        })
    }

    private fun initLoadRefresh() {
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.refreshOrder(BetSlipEnum.UnSettled)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.loadMoreOrder(BetSlipEnum.UnSettled)
        }
    }

    override fun getBetSlipEnum(): BetSlipEnum {
        return BetSlipEnum.UnSettled
    }
}