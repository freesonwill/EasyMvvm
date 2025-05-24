package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.live.R
import com.walisport.module.live.data.EventEnum
import com.walisport.module.live.data.model.MatchHalfTeamStats
import com.walisport.module.live.data.model.MatchTrendData
import com.walisport.module.live.data.model.Stat
import com.walisport.module.live.databinding.FragmentLiveOutsBinding
import com.walisport.module.live.ui.dialog.MatchTrendDialog
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import com.walisport.module.live.ui.viewmodel.LiveOutsViewModel
import com.walisport.module.live.ui.widget.TechnicalCountView
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
    }

    override fun initListener() {
        mBinding.viewTechStatic.setOnItemClickListener(object : TechnicalCountView.OnClickListener {
            override fun onClick() {
                showMatchTrendDialog(homeName, awayName, homeLogo, awayLogo, matchTrendData)
            }
        })
    }

    override fun initData() {
        super.initData()
        mViewModel.getMatchTrendData(mainViewModel.matchId)
        mViewModel.getStatisticData(mainViewModel.matchId)
    }

    override fun createObserver() {
        mainViewModel.mainMatch.observe(viewLifecycleOwner) {
            it?.let {
                homeName = it.basicInfo.homeTeam
                homeLogo = it.basicInfo.homeTeamIcon
                awayName = it.basicInfo.awayTeam
                awayLogo = it.basicInfo.awayTeamIcon
                mBinding.viewTechStatic.setScore(it.liveInfo.score)
                mBinding.viewTechStatic.setTeamInfo(homeName, awayName, homeLogo, awayLogo)
                mBinding.viewTechEvent.setTeamInfo(homeName, awayName, homeLogo, awayLogo)
            }
        }
        //比赛技术统计推送数据(WebSocket接口)
        mainViewModel.matchStatisticData.observe(viewLifecycleOwner) {
            it?.let {
                parseData(it.stats)
                parseHalfTeamData(it.team)
            }
        }
        //比赛技术统计接口数据(HTTP接口)
        mViewModel.matchStatisticData.observe(viewLifecycleOwner) {
            it?.let {
                parseData(it.stats)
                parseHalfTeamData(it.team)
            }
        }
        mViewModel.matchTrendData.observe(viewLifecycleOwner) {
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

    private fun parseHalfTeamData(data: List<MatchHalfTeamStats>) {
        val list = listOf(
            EventEnum.EVENT_BALL_CONTROL.type,
            EventEnum.EVENT_PASS_SUC.type,
            EventEnum.EVENT_SHOOT.type,
            EventEnum.EVENT_SHOOT_SUC.type,
            EventEnum.EVENT_PASS.type,
            EventEnum.EVENT_FREE.type,
            EventEnum.EVENT_CORNER.type,
            EventEnum.EVENT_OFFSIDE.type
        )
        mBinding.viewTechStatic.setMatchData(data.filter { it.type in list })
    }

    private fun parseData(list: List<Stat>) {
        list.mapIndexed { _, item ->
            //2角球 3黄牌 4红牌 23进攻 24危险进攻 25控球率
            when (item.type) {
                EventEnum.EVENT_CORNER.type -> {
                    mBinding.viewTechStatic.setCornerBallData(item.home, item.away)
                }

                EventEnum.EVENT_YELLOW_CARD.type -> {
                    mBinding.viewTechStatic.setYellowCardData(item.home, item.away)
                }

                EventEnum.EVENT_RED_CARD.type -> {
                    mBinding.viewTechStatic.setRedCardData(item.home, item.away)
                }

                EventEnum.EVENT_ATTACK.type -> {
                    mBinding.viewTechStatic.setAttackData(item.home, item.away)
                }

                EventEnum.EVENT_DANGER.type -> {
                    mBinding.viewTechStatic.setDangerAttackData(item.home, item.away)
                }

                EventEnum.EVENT_BALL_CONTROL.type -> {
                    mBinding.viewTechStatic.setBallControlData(item.home, item.away)
                }
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