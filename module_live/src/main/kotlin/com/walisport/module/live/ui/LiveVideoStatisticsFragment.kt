package com.walisport.module.live.ui

import android.os.Bundle
import android.view.View
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.live.R
import com.walisport.module.live.data.EventEnum
import com.walisport.module.live.data.model.Incident
import com.walisport.module.live.data.model.MatchHalfTeamStats
import com.walisport.module.live.data.model.MatchTrendData
import com.walisport.module.live.data.model.Stat
import com.walisport.module.live.databinding.FragmentLiveStatisticsBinding
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import com.walisport.module.live.ui.viewmodel.VideoPlayerViewModel
import me.jessyan.autosize.internal.CancelAdapt
import kotlin.reflect.KClass

/**
 * 视频横屏播放时的赛况页
 */

class LiveVideoStatisticsFragment :
    BaseFragment<VideoPlayerViewModel, FragmentLiveStatisticsBinding>(), CancelAdapt {

    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()
    override val vbClass: KClass<FragmentLiveStatisticsBinding> =
        FragmentLiveStatisticsBinding::class
    override val vmClass: KClass<VideoPlayerViewModel> = VideoPlayerViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        val matchId = arguments?.getLong("matchId") ?: 0L
        mViewModel.setMatchId(matchId)
        mBinding.viewTechStatic.setFullScreenMode()
        mBinding.viewTechEvent.setFullScreenMode()
    }

    override fun initData() {
        super.initData()
        mViewModel.getMainMatch(mViewModel.matchId())
        mainViewModel.registerStatisticsNotify(mViewModel.matchId())
        mainViewModel.observeMatchStaticsNotify()
    }

    override fun initListener() {
    }

    override fun createObserver() {
        mViewModel.mainMatch.observe(viewLifecycleOwner) {
            it?.let {
                val homeName = it.basicInfo.homeTeam
                val awayName = it.basicInfo.awayTeam
                val homeLogo = it.basicInfo.homeTeamIcon
                val awayLogo = it.basicInfo.awayTeamIcon
                mBinding.viewTechStatic.setTeamInfo(homeName, awayName, homeLogo, awayLogo)
            }
        }
        mainViewModel.statisticData.observe(viewLifecycleOwner) {
            it?.let {
                mBinding.mainLayout.setVisibilityGone()
                if (it.matchTrendData.data.isEmpty()) {
                    mBinding.llContent.visibility = View.INVISIBLE
                    mBinding.mainLayout.setState(
                        DynamicStateLayout.States.DATA_EMPTY,
                        R.string.lineup_empty.getString()
                    )
                } else {
                    mBinding.llContent.visibility = View.VISIBLE
                    parseTrendData(it.matchTrendData)          //比赛趋势信息
                    parseStatsData(it.stats)                   //统计进球红黄牌等信息
                    parseHalfTeamData(it.team)                 //统计进度条相关信息
                    parseTextLive(it.incidents)                //文字直播相关信息
                }
            }
        }
    }

    private fun parseTrendData(data: MatchTrendData) {
        mBinding.viewTechStatic.setTrendData(data)
    }

    private fun parseHalfTeamData(data: List<MatchHalfTeamStats>) {
        mBinding.viewTechStatic.setMatchData(data)
    }

    private fun parseStatsData(list: List<Stat>) {
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

    private fun parseTextLive(incidents: List<Incident>) {
        mBinding.viewTechEvent.setData(incidents)
    }

    override fun onResume() {
        super.onResume()
        mBinding.root.fitsSystemWindows = false
    }

    companion object {
        const val TAG = "LiveVideoShareFragment"
    }
}