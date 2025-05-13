package com.walisport.module.live.ui

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.live.R
import com.walisport.module.live.data.model.LiveBetSlipData
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import com.walisport.module.live.databinding.FragmentLiveBetslipUnsettledBinding
import com.walisport.module.live.ui.adapter.LiveBetSlipAdapter
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import com.walisport.module.live.utils.LiveBetSlipUtils
import com.walisport.module.live.utils.RecyclerItemListener
import galaxy.common.proto.Common
import kotlin.reflect.KClass

//注单未结算
class LiveBetSlipUnsettledFragment :
    BaseFragment<LiveBetSlipViewModel, FragmentLiveBetslipUnsettledBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipUnsettledBinding> =
        FragmentLiveBetslipUnsettledBinding::class
    override val vmClass: KClass<LiveBetSlipViewModel> = LiveBetSlipViewModel::class
    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
        initLoadRefresh()
    }

    override fun initData() {
        super.initData()
        mViewModel.setIds(mainViewModel.matchId, sportId = mainViewModel.sportId)
        mViewModel.getOrders(LiveBetSlipEnum.UnSettled)
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
            showToast(if (it == true) getString(R.string.early_settle_success) else getString(R.string.early_settle_faile))
            mViewModel.getOrders(LiveBetSlipEnum.UnSettled)
        }
        mViewModel.earlySettlePriceLiveData.observe(viewLifecycleOwner) {
            val price = it.price.toDoubleOrNull()
            if (price == null || price <= 0) {
                showToast(getString(R.string.not_support_early_settle))
                return@observe
            }
            mViewModel.selectOrder?.let { order ->
                val money = LiveBetSlipUtils.earlySettlePrice(
                    order.betId, price.toString(), order.earlyBetAmount
                )
                LiveBetSlipEarlySettledFragment.instance(it.betId, money.toDouble()).apply {
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

    private fun initRecycler() {
        val adapter = LiveBetSlipAdapter(LiveBetSlipEnum.UnSettled)
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
        adapter.setEarlySettleListener(object : RecyclerItemListener<LiveBetSlipData> {
            override fun onItemClick(item: LiveBetSlipData?, position: Int) {
                item?.order?.let {
                    mViewModel.selectOrder = it
                    mViewModel.earlySettledPrice(it.betId)
                }
            }
        })
    }

    private fun initLoadRefresh() {
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.refreshOrder(LiveBetSlipEnum.UnSettled)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.loadMoreOrder(LiveBetSlipEnum.UnSettled)
        }
    }


}