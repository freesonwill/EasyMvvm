package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.data.constants.MatchStatus
import com.walisport.module.live.databinding.FragmentLiveMatchMediaBinding
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import com.walisport.module.live.ui.viewmodel.LiveMatchMediaViewModel
import kotlinx.coroutines.delay
import kotlin.reflect.KClass


/**
 * 竖屏播放时的媒体页， 用来展示比赛视频，比赛动画或者比赛信息
 */
class LiveMatchMediaFragment :
    BaseFragment<LiveMatchMediaViewModel, FragmentLiveMatchMediaBinding>() {
    override val vbClass: KClass<FragmentLiveMatchMediaBinding> =
        FragmentLiveMatchMediaBinding::class
    override val vmClass: KClass<LiveMatchMediaViewModel> = LiveMatchMediaViewModel::class

    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.model = mViewModel

        val showVideo = arguments?.getBoolean("showVideo")
        val showAnim = arguments?.getBoolean("showAnim")

        if (showVideo == true) {
            showVideoView()
        } else if (showAnim == true) {
            showAnimationView()
        }

    }


    override fun initListener() {

    }

    override suspend fun createObserver() {
        delay(320)
        //监听比赛id变化
        mainViewModel.matchId.observe(viewLifecycleOwner) {
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
                                if (!isAnimationViewShowing()) {
                                    showVideoView()
                                }
                            }

                            else -> {
                                //其他情况
                                if (animationLiveUrl.value?.isNotBlank() == true && !isAnimationViewShowing()) {
                                    switchToAnimation()
                                } else {
                                    showStatusView()
                                }
                            }
                        }

                    }

                }

            }

            animationLiveUrl.observe(viewLifecycleOwner) {
                if (it != null) {
                    val matchBean = matchBeanLiveData.value

                    if (matchBean != null) {
                        val matchStatus =
                            MatchStatus.entries.find { status -> status.code == matchBean.basicInfo.status }

                        if (matchStatus != null && matchStatus != MatchStatus.IN_PROGRESS) {
                            if (it.isNotBlank() && !isAnimationViewShowing()) {
                                switchToAnimation()
                            }
                        }
                    }
                }
            }

            animationSwitch.observe(viewLifecycleOwner) {
                showAnimationView()
            }

            chooseSource.observe(viewLifecycleOwner) {
                showChooseSourceView()
            }

            switchToVideo.observe(viewLifecycleOwner) {
                showVideoView()
            }

            switchToMatchStatus.observe(viewLifecycleOwner) {
                showStatusView()
            }

        }


        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        mViewModel.createObserver()
    }

    private fun showVideoView() {
        "showVideoView".logd(TAG)
        childFragmentManager.findFragmentByTag(LiveVideoPlayerFragment.TAG) as? LiveVideoPlayerFragment
            ?: LiveVideoPlayerFragment().also {
                it.arguments = Bundle().apply {
                    putLong(
                        "matchId",
                        mViewModel.matchId()
                    )
                }
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentVideo.id, it, LiveVideoPlayerFragment.TAG).commitNow()
            }
    }

    private fun showStatusView() {
        "showStatusView".logd(TAG)
        childFragmentManager.findFragmentByTag(LiveMatchStatusFragment.TAG) as? LiveMatchStatusFragment
            ?: LiveMatchStatusFragment().also {
                it.arguments = Bundle().apply {
                    putLong(
                        "matchId",
                        mViewModel.matchId()
                    )
                }
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentVideo.id, it, LiveMatchStatusFragment.TAG).commitNow()
            }
    }

    private fun showAnimationView() {
        "showAnimationView".logd(TAG)
        childFragmentManager.findFragmentByTag(LiveMatchAnimationFragment.TAG) as? LiveMatchAnimationFragment
            ?: LiveMatchAnimationFragment().also {
                it.arguments = Bundle().apply {
                    putLong(
                        "matchId",
                        mViewModel.matchId()
                    )
                }
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentVideo.id, it, LiveMatchAnimationFragment.TAG)
                    .commitNow()
            }
    }

    private fun isAnimationViewShowing(): Boolean {
        val flag =
            childFragmentManager.findFragmentByTag(LiveMatchAnimationFragment.TAG) is LiveMatchAnimationFragment
        "isAnimationViewShowing:$flag".logd(TAG)
        return flag
    }

    private fun showChooseSourceView() {
        val location = IntArray(2)
        mBinding.root.getLocationOnScreen(location)
        val y =
            location[1] + mBinding.root.measuredHeight

        val height = requireActivity().resources.displayMetrics.heightPixels - y

        LiveSourceFragment().apply {
            arguments = Bundle().apply {
                putLong("matchId", mViewModel.matchId())
                putInt(
                    LiveSourceFragment.HEIGHT,
                    height
                )
            }
            show(this@LiveMatchMediaFragment.childFragmentManager)

        }
    }


    companion object {
        const val TAG = "LiveMatchMediaFragment"
    }


}