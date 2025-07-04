package com.walisport.module.live.ui

import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_PX
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDimension
import com.bumptech.glide.Glide
import com.walisport.module.live.data.constants.MatchStatus
import com.walisport.module.live.databinding.FragmentVideoMainBinding
import com.walisport.module.live.ui.viewmodel.VideoMainViewModel
import kotlin.reflect.KClass


/**
 * 竖屏播放视频页， 用在直播详情的首页
 */
class VideoMainFragment : BaseFragment<VideoMainViewModel, FragmentVideoMainBinding>() {
    override val vbClass: KClass<FragmentVideoMainBinding> = FragmentVideoMainBinding::class
    override val vmClass: KClass<VideoMainViewModel> = VideoMainViewModel::class

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

        }


        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        mViewModel.createObserver()
    }

    private fun showVideoView() {
        childFragmentManager.findFragmentByTag(VideoPlayerFragment.TAG) as? VideoPlayerFragment
            ?: VideoPlayerFragment().also {
                it.arguments = Bundle().apply {
                    mViewModel.matchId.value?.let { value ->
                        putLong(
                            "matchId",
                            value
                        )
                    }
                }
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentVideo.id, it, VideoPlayerFragment.TAG).commitNow()
            }
    }

    private fun showStatusView(){
        childFragmentManager.findFragmentByTag(MatchStatusFragment.TAG) as? MatchStatusFragment
            ?: MatchStatusFragment().also {
                it.arguments = Bundle().apply {
                    mViewModel.matchId.value?.let { value ->
                        putLong(
                            "matchId",
                            value
                        )
                    }
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
        const val TAG = "VideoMainFragment"
    }


}