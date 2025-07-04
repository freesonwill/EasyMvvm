package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.live.data.constants.MatchStatus
import com.walisport.module.live.databinding.FragmentLiveMatchMediaBinding
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import com.walisport.module.live.ui.viewmodel.LiveMatchMediaViewModel
import kotlin.reflect.KClass


/**
 * 竖屏播放时的媒体页， 用来展示比赛视频，比赛动画或者比赛信息
 */
class LiveMatchMediaFragment : BaseFragment<LiveMatchMediaViewModel, FragmentLiveMatchMediaBinding>() {
    override val vbClass: KClass<FragmentLiveMatchMediaBinding> = FragmentLiveMatchMediaBinding::class
    override val vmClass: KClass<LiveMatchMediaViewModel> = LiveMatchMediaViewModel::class

    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()

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
    }


    override fun initListener() {

    }

    override fun createObserver() {
        //监听比赛id变化
        mainViewModel.matchId.observe(viewLifecycleOwner){
            mViewModel.setMatchId(it)
            mViewModel.createObserver()
        }

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

        }


        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        mViewModel.createObserver()
    }

    private fun showVideoView() {
        "showVideoView".logd(TAG)
        childFragmentManager.findFragmentByTag(VideoPlayerFragment.TAG) as? VideoPlayerFragment
            ?: VideoPlayerFragment().also {
                it.arguments = Bundle().apply {
                    putLong(
                        "matchId",
                        mViewModel.matchId()
                    )
                }
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentVideo.id, it, VideoPlayerFragment.TAG).commitNow()
            }
    }

    private fun showStatusView(){
        "showStatusView".logd(TAG)
        childFragmentManager.findFragmentByTag(MatchStatusFragment.TAG) as? MatchStatusFragment
            ?: MatchStatusFragment().also {
                it.arguments = Bundle().apply {
                    putLong(
                        "matchId",
                        mViewModel.matchId()
                    )
                }
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentVideo.id, it, MatchStatusFragment.TAG).commitNow()
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
        const val TAG = "LiveMatchMediaFragment"
    }


}