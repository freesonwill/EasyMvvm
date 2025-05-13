package com.walisport.module.live.ui

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.live.R
import com.walisport.module.live.data.model.LiveBetSlipData
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import com.walisport.module.live.databinding.FragmentLiveBetslipSettledLayoutBinding
import com.walisport.module.live.ui.adapter.LiveBetSlipAdapter
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import galaxy.common.proto.Common
import kotlin.reflect.KClass

//注单已结算
class LiveBetSlipSettledFragment :
    BaseFragment<LiveBetSlipViewModel, FragmentLiveBetslipSettledLayoutBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipSettledLayoutBinding> =
        FragmentLiveBetslipSettledLayoutBinding::class
    override val vmClass: KClass<LiveBetSlipViewModel> = LiveBetSlipViewModel::class
    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
        initLoadRefresh()
    }

    private fun initRecycler() {
        val adapter = LiveBetSlipAdapter(LiveBetSlipEnum.Settled)
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
            mViewModel.refreshOrder(LiveBetSlipEnum.Settled)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.loadMoreOrder(LiveBetSlipEnum.Settled)
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
            } else {
                showEmpty()
            }
        }
        mViewModel.earlySettledResultLiveData.observe(viewLifecycleOwner) {
            mViewModel.getOrders(LiveBetSlipEnum.UnSettled)
        }
    }

    private fun showEmpty() {

    }

    private fun updateData(orders: List<Common.Order>) {
        val list = orders.map {
            val expandedEnum =
                if (it.selectionsList.size <= 3) LiveBetSlipExpandedEnum.Hide else LiveBetSlipExpandedEnum.Fold
            LiveBetSlipData(order = it, expandedEnum = expandedEnum)
        }.toList()
        mBinding.recyclerView.adapter?.let {
            val adapter = it as LiveBetSlipAdapter
            adapter.submitList(list)
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.setIds(mainViewModel.matchId, sportId = mainViewModel.sportId)
        mViewModel.getOrders(LiveBetSlipEnum.Settled)
    }
}