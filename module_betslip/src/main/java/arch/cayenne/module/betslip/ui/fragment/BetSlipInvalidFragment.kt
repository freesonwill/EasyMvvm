package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import galaxy.common.proto.Common
import kotlin.reflect.KClass
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipInvalidBinding
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipPageViewModel
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipViewModel

//注单失效
class BetSlipInvalidFragment :
    BaseFragment<BetSlipViewModel, FragmentLiveBetslipInvalidBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipInvalidBinding> =
        FragmentLiveBetslipInvalidBinding::class
    override val vmClass: KClass<BetSlipViewModel> = BetSlipViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

        initRecycler()
        initLoadRefresh()
    }

    private fun initRecycler() {
        val adapter = BetSlipAdapter(BetSlipEnum.Invalid)
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.item_divide_live_bet_recycler
            )!!
        )
        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter
            it.setItemViewCacheSize(10)
            it.addItemDecoration(divider)
            it.setRecycledViewPool(RecyclerView.RecycledViewPool())
        }
    }

    private fun initLoadRefresh() {
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.refreshOrder(BetSlipEnum.Invalid)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.loadMoreOrder(BetSlipEnum.Invalid)
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
                order = it,
                expandedEnum = expandedEnum
            )
        }.toList()
        mBinding.recyclerView.adapter?.let {
            val adapter = it as BetSlipAdapter
            adapter.submitList(list)
        }
    }


    override fun initData() {
        super.initData()
        val matchId = arguments?.getLong(BetSlipFragment.matchKey,-1) ?:-1
        val sportId = arguments?.getInt(BetSlipFragment.sportKey,-1) ?: -1
        mViewModel.setIds(matchId, sportId = sportId)
        mViewModel.getOrders(BetSlipEnum.Invalid)
    }
}