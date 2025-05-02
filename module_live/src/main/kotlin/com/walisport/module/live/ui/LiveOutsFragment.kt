package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.live.R
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.live.data.model.MatchTrendData
import com.walisport.module.live.databinding.FragmentLiveOutsBinding
import com.walisport.module.live.ui.dialog.MatchTrendDialog
import com.walisport.module.live.ui.viewmodel.LiveOutsViewModel
import com.walisport.module.live.ui.widget.TechnicalCountView
import com.walisport.module.live.viewmodel.LiveMainViewModel
import kotlin.reflect.KClass

/**
 * 赛况Tab页
 */

class LiveOutsFragment : BaseFragment<LiveOutsViewModel, FragmentLiveOutsBinding>() {

    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()
    override val vbClass: KClass<FragmentLiveOutsBinding> = FragmentLiveOutsBinding::class
    override val vmClass: KClass<LiveOutsViewModel> = LiveOutsViewModel::class
    private lateinit var matchTrendData: MatchTrendData
    private lateinit var homeName: String
    private lateinit var homeLogo: String
    private lateinit var awayName: String
    private lateinit var awayLogo: String

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.getMatchTrendData(mainViewModel.matchId)
    }

    override fun initListener() {
        mBinding.viewTechStatic.setOnItemClickListener(object : TechnicalCountView.OnClickListener {
            override fun onClick() {
                showMatchTrendDialog(homeName, awayName, homeLogo, awayLogo, matchTrendData)
            }
        })
    }

    override fun createObserver() {
        mainViewModel.mainMatch.observe(viewLifecycleOwner) {
            it?.let {
                homeName = it.basicInfo.homeTeam
                homeLogo = it.basicInfo.homeTeamIcon
                awayName = it.basicInfo.awayTeam
                awayLogo = it.basicInfo.awayTeamIcon
                mBinding.viewTechStatic.setTeamInfo(homeName, awayName, homeLogo, awayLogo)
            }
        }
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