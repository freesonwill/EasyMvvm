package com.walisport.module.live.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.BaseFragment
import com.walisport.module.live.data.model.LiveBetSlipEnum
import com.walisport.module.live.databinding.FragmentLiveBetslipConfirmBinding
import com.walisport.module.live.ui.adapter.LiveBetSlipAdapter
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import kotlin.reflect.KClass

//注单确认
class LiveBetSlipConfirmFragment :
    BaseFragment<LiveBetSlipViewModel, FragmentLiveBetslipConfirmBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipConfirmBinding> =
        FragmentLiveBetslipConfirmBinding::class
    override val vmClass: KClass<LiveBetSlipViewModel> = LiveBetSlipViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
    }

    private fun initRecycler() {
        val adapter = LiveBetSlipAdapter(LiveBetSlipEnum.UnSettled)
        adapter.submitList(mViewModel.getTestList())
        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}