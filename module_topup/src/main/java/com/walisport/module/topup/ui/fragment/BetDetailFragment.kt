package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentBetDetailBinding
import com.walisport.module.topup.ui.adapter.BetDetailAdapter
import com.walisport.module.topup.ui.viewmodel.BetDetailViewModel
import kotlin.reflect.KClass

/**
 * 投注额详情页面
 */

class BetDetailFragment : BaseFragment<BetDetailViewModel, FragmentBetDetailBinding>() {

    override val vbClass: KClass<FragmentBetDetailBinding> = FragmentBetDetailBinding::class
    override val vmClass: KClass<BetDetailViewModel> = BetDetailViewModel::class
    private val betDetailAdapter by lazy { BetDetailAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.bet_detail, {
            findNavController().navigateUp()
        })
        mBinding.tvBetTip.text = getTipText()
        mBinding.recyclerBet.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = betDetailAdapter
        }
        mBinding.root.touchBackPressed()
    }

    override fun initData() {
        super.initData()
        mViewModel.getBetDetailList()
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {
        mViewModel.onBetDetailListener.observe(viewLifecycleOwner) {
            betDetailAdapter.submitList(it)
        }
    }

    private fun getTipText(): String {
        val detail = R.string.tip_bet_detail.getString()
        return String.format(detail, "¥700.00")
    }
}