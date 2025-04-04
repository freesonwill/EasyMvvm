package com.walisport.module.live.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.live.adapter.LiveBetSlipAdapter
import com.walisport.module.live.data.model.LiveBetSlipBean
import com.walisport.module.live.data.model.LiveBetSlipEnum
import com.walisport.module.live.databinding.FragmentLiveBetslipUnsettledBinding
import com.walisport.module.live.ui.viewmodel.LiveBetSlipUnsettledViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LiveBetSlipUnsettledFragment :
    BaseFragment<LiveBetSlipUnsettledViewModel, FragmentLiveBetslipUnsettledBinding>() {
    override val mBinding: FragmentLiveBetslipUnsettledBinding by viewBind()
    override val mViewModel: LiveBetSlipUnsettledViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

    private fun initRecycler() {
        val manager = LinearLayoutManager(context)
        val adapter = LiveBetSlipAdapter().apply {
            val list = arrayListOf(LiveBetSlipBean("1", LiveBetSlipEnum.UnSettled))
            submitList(list)
        }
        mBinding.recyclerView.itemAnimator = null
        mBinding.recyclerView.layoutManager = manager
        mBinding.recyclerView.adapter = adapter

    }
}