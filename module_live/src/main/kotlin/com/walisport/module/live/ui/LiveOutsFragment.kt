package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.live.R
import com.walisport.module.live.data.model.MatchTrendData
import com.walisport.module.live.databinding.FragmentLiveOutsBinding
import com.walisport.module.live.ui.dialog.MatchTrendDialog
import com.walisport.module.live.ui.viewmodel.LiveOutsViewModel
import com.walisport.module.live.ui.widget.TechnicalCountView
import kotlin.reflect.KClass

/**
 * 赛况Tab页
 */

class LiveOutsFragment : BaseFragment<LiveOutsViewModel, FragmentLiveOutsBinding>() {

    override val vbClass: KClass<FragmentLiveOutsBinding> = FragmentLiveOutsBinding::class
    override val vmClass: KClass<LiveOutsViewModel> = LiveOutsViewModel::class
    lateinit var matchTrendData: MatchTrendData

    override fun initView(savedInstanceState: Bundle?) {
        val matchId = arguments?.getLong("matchId") ?: 0L
        mViewModel.getMatchTrendData(matchId)
    }

    override fun initListener() {
        mBinding.viewTechStatic.setOnItemClickListener(object : TechnicalCountView.OnClickListener {
            override fun onClick() {
                showMatchTrendDialog("法国", "阿根廷", "", "", matchTrendData)
            }
        })
    }

    override fun createObserver() {
        mViewModel.liveOutsData.observe(this) {
            if (it != null) {
                matchTrendData = it
                mBinding.mainLayout.setVisibilityGone()
                mBinding.viewTechStatic.setTrendData(it)
            } else {
                mBinding.mainLayout.setState(
                    DynamicStateLayout.States.DATA_EMPTY,
                    R.string.outs_empty.getString()
                )
            }
        }
    }

    private fun showMatchTrendDialog(
        home: String,
        away: String,
        homeUrl: String,
        awayUrl: String,
        data: MatchTrendData
    ) {
        val homeStr = home + getString(R.string.live_out_gy)
        val awayStr = away + getString(R.string.live_out_dy)
        val fragmentManager = requireActivity().supportFragmentManager
        MatchTrendDialog().apply {
            arguments = Bundle().apply {
                putString(homeName, homeStr)
                putString(homeLogo, homeUrl)
                putString(awayName, awayStr)
                putString(awayLogo, awayUrl)
                putSerializable(matchTrend, data)
            }
        }.show(fragmentManager)
    }
}