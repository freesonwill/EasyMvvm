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
import com.walisport.module.live.databinding.FragmentLiveBetslipConfirmBinding
import com.walisport.module.live.ui.adapter.LiveBetSlipAdapter
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import com.walisport.module.live.viewmodel.LiveMainViewModel
import galaxy.common.proto.Common
import kotlin.reflect.KClass

//注单确认‰‰
class LiveBetSlipConfirmFragment :
    BaseFragment<LiveBetSlipViewModel, FragmentLiveBetslipConfirmBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipConfirmBinding> =
        FragmentLiveBetslipConfirmBinding::class
    override val vmClass: KClass<LiveBetSlipViewModel> = LiveBetSlipViewModel::class
    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
    }

    private fun initRecycler() {
        val adapter = LiveBetSlipAdapter(LiveBetSlipEnum.Confirming)
        val divider = DividerItemDecoration(context,DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.item_divide_live_bet_recycler)!!)
        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.addItemDecoration(divider)
            it.adapter = adapter
            it.setItemViewCacheSize(10)
            it.setRecycledViewPool(RecyclerView.RecycledViewPool())
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
        mViewModel.orderLiveData.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
//                updateData(it)
            } else {
                showEmpty()
            }
        }
    }

    private fun showEmpty() {

    }

    private fun updateData(orders: List<Common.Order>) {
        val list = orders.map { LiveBetSlipData(order = it) }.toList()
        mBinding.recyclerView.adapter?.let {
            val adapter = it as LiveBetSlipAdapter
            adapter.submitList(list)
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.setIds(mainViewModel.matchId, sportId = mainViewModel.sportId)
        mViewModel.getOrders(LiveBetSlipEnum.Confirming)
    }
}