package com.walisport.module.live.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.live.data.model.LiveBetSlipEnum
import com.walisport.module.live.databinding.FragmentLiveBetslipUnsettledBinding
import com.walisport.module.live.ui.adapter.LiveBetSlipAdapter
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import com.walisport.module.live.viewmodel.LiveMainViewModel
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
    }

    override fun initListener() {
    }

    override fun createObserver() {
        mViewModel.orderLiveData.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                updateData(it.first())
            }
        }
    }

    private fun updateData(data: Common.Order) {


    }

    private fun initRecycler() {
        val adapter = LiveBetSlipAdapter(LiveBetSlipEnum.UnSettled)
        adapter.submitList(mViewModel.getTestList())
        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.setIds(mainViewModel.matchId, sportId = mainViewModel.sportId)
        mViewModel.getOrders(LiveBetSlipEnum.UnSettled)
    }
}