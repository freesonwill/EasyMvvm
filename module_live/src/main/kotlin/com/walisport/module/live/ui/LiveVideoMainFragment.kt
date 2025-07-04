package com.walisport.module.live.ui

import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_PX
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDimension
import com.bumptech.glide.Glide
import com.walisport.module.live.data.constants.MatchStatus
import com.walisport.module.live.databinding.FragmentLiveVideoMainBinding
import com.walisport.module.live.ui.viewmodel.LiveVideoMainViewModel
import kotlin.reflect.KClass


/**
 * 竖屏播放视频页， 用在直播详情的首页
 */
class LiveVideoMainFragment : BaseFragment<LiveVideoMainViewModel, FragmentLiveVideoMainBinding>() {
    override val vbClass: KClass<FragmentLiveVideoMainBinding> = FragmentLiveVideoMainBinding::class
    override val vmClass: KClass<LiveVideoMainViewModel> = LiveVideoMainViewModel::class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.model = mViewModel
        mBinding.includedMatchNotInProgress.model = mViewModel
    }


    override fun initListener() {

    }

    override fun createObserver() {
        with(mViewModel) {

            //比赛状态的监听
            matchBeanLiveData.observe(viewLifecycleOwner) {
                it?.let { matchBean ->
                    val matchStatus =
                        MatchStatus.entries.find { status -> status.code == matchBean.basicInfo.status }

                    matchStatus?.let { _ ->
                        when (matchStatus) {
                            MatchStatus.IN_PROGRESS -> {
                                //比赛正在进行中
                                showVideoView()
                            }

                            else -> {
                                //其他情况
                              showStatusView()
                            }
                        }

                    }

                }

            }

            homeTeamName.observe(viewLifecycleOwner) {
                it?.let {
                    mBinding.includedMatchNotInProgress.tvHomeTeam.text = it
                }
            }

            homeTeamIcon.observe(viewLifecycleOwner) {
                it?.let {
                    Glide.with(requireContext())
                        .load(it)
                        .placeholder(arch.cayenne.lib.common.R.color.color_333A45)
                        .error(arch.cayenne.lib.common.R.color.color_333A45)
                        .into(mBinding.includedMatchNotInProgress.ivHomeTeam)
                }
            }

            homeHistoryVs.observe(viewLifecycleOwner) {
//                "homeList:$it".logd("scoreIssue")
                mBinding.includedMatchNotInProgress.homeHistory.setData(it)
            }

            awayTeamName.observe(viewLifecycleOwner) {
                it?.let { mBinding.includedMatchNotInProgress.tvAwayTeam.text = it }
            }

            awayTeamIcon.observe(viewLifecycleOwner) {
                it?.let {
                    Glide.with(requireContext())
                        .load(it)
                        .placeholder(arch.cayenne.lib.common.R.color.color_333A45)
                        .error(arch.cayenne.lib.common.R.color.color_333A45)
                        .into(mBinding.includedMatchNotInProgress.ivAwayTeam)
                }
            }

            awayHistoryVs.observe(viewLifecycleOwner) {
//                "awayList:$it".logd("scoreIssue")
                mBinding.includedMatchNotInProgress.awayHistory.setData(it)
            }

            titleText.observe(viewLifecycleOwner) {
                it?.let { mBinding.includedMatchNotInProgress.tvTitle.text = it }
            }

            titleTextSize.observe(viewLifecycleOwner) {
                it?.let {
                    mBinding.includedMatchNotInProgress.tvTitle.setTextSize(
                        COMPLEX_UNIT_PX,
                        it.getDimension()
                    )
                }
            }

            titleTextColor.observe(viewLifecycleOwner) {
                it?.let { mBinding.includedMatchNotInProgress.tvTitle.setTextColor(it.getColor()) }
            }

            subTitleText.observe(viewLifecycleOwner) {
                it?.let { mBinding.includedMatchNotInProgress.tvSubtitle.text = it }
            }

            subTitleTextSize.observe(viewLifecycleOwner) {
                it?.let {
                    mBinding.includedMatchNotInProgress.tvSubtitle.setTextSize(
                        COMPLEX_UNIT_PX,
                        it.getDimension()
                    )
                }
            }
            subTitleTextColor.observe(viewLifecycleOwner) {
                it?.let { mBinding.includedMatchNotInProgress.tvSubtitle.setTextColor(it.getColor()) }
            }


        }


        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        mViewModel.createObserver()
    }

    private fun showVideoView() {
        childFragmentManager.findFragmentByTag(LiveVideoFragment.TAG) as? LiveVideoFragment
            ?: LiveVideoFragment().also {
                it.arguments = Bundle().apply {
                    mViewModel.matchId.value?.let { value ->
                        putLong(
                            "matchId",
                            value
                        )
                    }
                }
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentVideo.id, it, LiveVideoFragment.TAG).commitNow()
            }
    }

    private fun showStatusView(){
        childFragmentManager.findFragmentByTag(LiveVideoFragment.TAG) as? LiveVideoFragment
            ?: LiveVideoFragment().also {
                it.arguments = Bundle().apply {
                    mViewModel.matchId.value?.let { value ->
                        putLong(
                            "matchId",
                            value
                        )
                    }
                }
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentVideo.id, it, LiveVideoFragment.TAG).commitNow()
            }
    }


    override fun initData() {
        super.initData()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


    companion object {
        const val TAG = "LiveVideoMainFragment"
    }


}