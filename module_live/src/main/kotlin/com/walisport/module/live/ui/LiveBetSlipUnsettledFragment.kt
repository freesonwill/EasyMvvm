package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.extension.sharedViewModel
import com.walisport.module.live.data.model.LiveBetSlipEnum
import com.walisport.module.live.databinding.FragmentLiveBetslipUnsettledBinding
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
        mViewModel.setIds(mainViewModel.matchId, mainViewModel.sportId)
        mViewModel.getOrders(LiveBetSlipEnum.UnSettled)
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

        mBinding.apply {
            betUnsettledTvRace.text =
                "${data.getSelections(0).matchBasic.homeTeam}vs${data.getSelections(0).matchBasic.awayTeam}"
        }

    }

    private fun initRecycler() {


    }
}