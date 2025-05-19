package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipConfirmBinding
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import galaxy.common.proto.Common
import kotlin.reflect.KClass


//注单确认‰‰
class BetSlipConfirmFragment :
    BaseBetSlipFragment<FragmentLiveBetslipConfirmBinding>() {

    override val vbClass: KClass<FragmentLiveBetslipConfirmBinding> =
        FragmentLiveBetslipConfirmBinding::class

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
    }

    private fun initRecycler() {
        val adapter =
            BetSlipAdapter(BetSlipEnum.Confirming)
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.item_divide_live_bet_recycler
            )!!
        )
        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.addItemDecoration(divider)
            it.adapter = adapter
            it.setItemViewCacheSize(10)
            it.setRecycledViewPool(RecyclerView.RecycledViewPool())
        }
        initLoadRefresh()
    }

    private fun initLoadRefresh() {
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.refreshOrder(BetSlipEnum.Confirming)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.loadMoreOrder(BetSlipEnum.Confirming)
        }
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
    }

    private fun showEmpty() {
        val flag = mBinding.recyclerView.adapter?.let {
            val adapter = it as BetSlipAdapter
            adapter.currentList.isEmpty()
        } ?: true
        if (flag) {
            mBinding.emptyState.isVisible = true
            mBinding.refreshLayout.isVisible = false
            mBinding.emptyState.setState(DynamicStateLayout.States.DATA_EMPTY,getString(R.string.lineup_empty))
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
                order = it,
                expandedEnum = expandedEnum
            )
        }.toList()
        mBinding.recyclerView.adapter?.let {
            val adapter = it as BetSlipAdapter
            adapter.submitList(list)
        }
    }

    override fun getBetSlipEnum(): BetSlipEnum {
        return BetSlipEnum.UnSettled
    }
}