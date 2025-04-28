package com.walisport.module.live.ui

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.live.databinding.FragmentLiveBetslipReserveBinding
import com.walisport.module.live.ui.adapter.LiveBetSlipReserveAdapter
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import com.walisport.module.live.viewmodel.LiveMainViewModel
import kotlin.reflect.KClass

//注单预约
class LiveBetSlipReserveFragment :
    BaseFragment<LiveBetSlipViewModel, FragmentLiveBetslipReserveBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipReserveBinding> =
        FragmentLiveBetslipReserveBinding::class
    override val vmClass: KClass<LiveBetSlipViewModel> = LiveBetSlipViewModel::class
    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()
    private val adapter = LiveBetSlipReserveAdapter()

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
    }

    private fun initRecycler() {
        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter
            it.setItemViewCacheSize(10)
            it.setRecycledViewPool(RecyclerView.RecycledViewPool())
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
        mViewModel.reserveLiveData.observe(this) {
            LogUtils.dTag("aaa", "reserveLiveData ${it?.size}")
            it?.let {
                adapter.submitList(it)
            }
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.setIds(mainViewModel.matchId, sportId = mainViewModel.sportId)
        mViewModel.getReserveOrder()
    }
}