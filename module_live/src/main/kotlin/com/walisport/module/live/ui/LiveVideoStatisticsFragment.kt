package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.walisport.module.live.databinding.FragmentLiveStatisticsBinding
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import me.jessyan.autosize.internal.CancelAdapt
import kotlin.reflect.KClass

/**
 * 视频横屏播放时的赛况页
 */
class LiveVideoStatisticsFragment :
    BaseFragment<LiveVideoViewModel, FragmentLiveStatisticsBinding>(), CancelAdapt {

    override val vbClass: KClass<FragmentLiveStatisticsBinding> =
        FragmentLiveStatisticsBinding::class
    override val vmClass: KClass<LiveVideoViewModel> = LiveVideoViewModel::class


    override fun initView(savedInstanceState: Bundle?) {
        val matchId = arguments?.getLong("matchId") ?: 0L
        mViewModel.setMatchId(matchId)
        mBinding.viewTechStatic.setFullScreenMode()
        mBinding.viewTechEvent.setFullScreenMode()
    }

    override fun initData() {
        super.initData()
        mViewModel.getMainMatch(mViewModel.matchId())
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
        mViewModel.liveVideoBean.observe(viewLifecycleOwner) {
            //推送websocket数据发生变化时更新界面数据
            //mBinding.viewTechStatic.setTeamInfo("法国", "阿根廷")
            //mBinding.viewTechStatic.setScore("2:2")
            //mBinding.viewTechStatic.setAttackData(8, 5)
            //mBinding.viewTechStatic.setDangerAttackData(10, 12)
            //mBinding.viewTechStatic.setBallControlData(20, 13)
            //mBinding.viewTechStatic.setHomeAwayData(3, 4, 5, 3, 2, 4)
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.root.fitsSystemWindows = false
    }

    companion object {
        const val TAG = "LiveVideoShareFragment"
    }

}