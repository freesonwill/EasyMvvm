package com.walisport.module.live.ui

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout.VISIBLE
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.database.entity.MatchBasicInfoBean.MatchStatus
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.startSafeAnimateSet
import com.walisport.module.live.compare.MediaSourceBeanCompare
import com.walisport.module.live.data.constants.VideoAnimatorConstants.Companion.HIDE_BUTTONS_TIMER
import com.walisport.module.live.data.model.MediaSource
import com.walisport.module.live.data.model.MediaSourceType
import com.walisport.module.live.databinding.FragmentLiveMatchMediaBinding
import com.walisport.module.live.ui.adapter.LiveMediaSourceSimpleAdapter
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import com.walisport.module.live.ui.viewmodel.LiveMatchMediaViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    //是否自动显示过视频源bar
    private var hasAutoShownMediaSourceBar: Boolean = false

    private var mediaSourceBarShowing: Boolean = false

    private var dismissBarJob: Job? = null

    private var animating = false

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.model = mViewModel

        val showVideo = arguments?.getBoolean("showVideo")
        val showAnim = arguments?.getBoolean("showAnim")

        initMediaSourceBanner()

        if (showVideo == true) {
            showVideoView()
        } else if (showAnim == true) {
            showAnimationView(true)
        }

    }

    private fun initMediaSourceBanner() {
        //init media source recyclerview
        with(mBinding) {
            rvSource.apply {
                itemAnimator = null
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(NewMediaSourceFragment.HorizontalItemDecoration())
                adapter = LiveMediaSourceSimpleAdapter(MediaSourceBeanCompare())
            }
        }
    }


    override fun initListener() {

    }

    override suspend fun createObserver() {
        delay(320)
        //监听比赛id变化
        mainViewModel.matchId.observe(viewLifecycleOwner) {
            hasAutoShownMediaSourceBar = false  //切换比赛时， 需要重置hasAutoShownVideoSourceBar
            mViewModel.setMatchId(it)
            mViewModel.createObserver()
        }

        mainViewModel.scorll.observe(viewLifecycleOwner) {
            scheduleHideVideoSourceBanner(0)
        }
        with(mViewModel) {

            //比赛状态的监听
            matchBeanLiveData.observe(viewLifecycleOwner) {
                it?.let { matchBean ->
                    val matchStatus =
                        MatchStatus.entries.find { status -> status.code == matchBean.basicInfo.status }
                    mainViewModel.setStatus(matchBean.basicInfo.status)
                    matchStatus?.let { _ ->
                        when (matchStatus) {
                            MatchStatus.IN_PROGRESS -> {
                                //比赛正在进行中
                                mViewModel.queryLiveStream()
                            }

                            else -> {
                                if (it.liveInfo.animationLiveUrl.isNotBlank()) {
                                    showAnimationView(true)
                                } else {
                                    showStatusView()
                                }
                            }
                        }

                    }

                }

            }

            liveVideoBean.observe(viewLifecycleOwner) {
                it?.let {
                    if (it.source.isEmpty()) {
                        if (animationLiveUrl.value?.isNotBlank() == true) {
                            switchToAnimation()
                        } else {
                            switchToMatchStatus()
                        }
                    } else {
                        //首次收到视频源数据时，需要展示媒体源banner
                        if (!hasAutoShownMediaSourceBar) {
                            mBinding.rvSource.visibility = VISIBLE
                            hasAutoShownMediaSourceBar = true

                            (mBinding.rvSource.adapter as LiveMediaSourceSimpleAdapter).apply {

                                val list: MutableList<MediaSource> = mutableListOf()
                                list.add(
                                    MediaSource(
                                        MediaSourceType.ANIMATION,
                                        mViewModel.animationLiveUrl.value, null, false
                                    )
                                )
                                mViewModel.liveVideoBean.value?.source?.map { bean ->
                                    MediaSource(
                                        MediaSourceType.VIDEO,
                                        null,
                                        bean, bean.isPlaying
                                    )
                                }
                                    ?.let { it1 -> list.addAll(it1) }

                                submitList(list)

                                setOnClickListener { mediaSourceItem ->
                                    onMediaSourceItemClicked(mediaSourceItem)
                                }
                            }
                        }
                        showVideoView()
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
                showAnimationView(false)
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
        mainViewModel.setVideoInitHeight(mBinding.rvSource.height + mBinding.fragmentMedia.height)
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
                    .replace(mBinding.fragmentMedia.id, it, LiveVideoPlayerFragment.TAG).commitNow()
            }
    }

    private fun showStatusView() {
        mainViewModel.setVideoInitHeight(mBinding.fragmentMedia.height)
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
                    .replace(mBinding.fragmentMedia.id, it, LiveMatchStatusFragment.TAG).commitNow()
            }
    }

    //动画展示
    private fun showAnimationView(showMediaSourceBanner: Boolean) {
        "showAnimationView".logd(TAG)

        if (!hasAutoShownMediaSourceBar && showMediaSourceBanner) {
            mBinding.rvSource.visibility = VISIBLE
            hasAutoShownMediaSourceBar = true
            mainViewModel.setVideoInitHeight(mBinding.rvSource.height + mBinding.fragmentMedia.height)
            (mBinding.rvSource.adapter as LiveMediaSourceSimpleAdapter).apply {
                val list: MutableList<MediaSource> = mutableListOf()
                list.add(
                    MediaSource(
                        MediaSourceType.ANIMATION,
                        mViewModel.animationLiveUrl.value, null, true
                    )
                )
                submitList(list)

                setOnClickListener { mediaSourceItem ->
                    onMediaSourceItemClicked(mediaSourceItem)
                }
            }
            scheduleHideVideoSourceBanner()
        }
        childFragmentManager.findFragmentByTag(LiveMatchAnimationFragment.TAG) as? LiveMatchAnimationFragment
            ?: LiveMatchAnimationFragment().also {
                it.arguments = Bundle().apply {
                    putLong(
                        "matchId",
                        mViewModel.matchId()
                    )
                }
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentMedia.id, it, LiveMatchAnimationFragment.TAG)
                    .commitNow()
            }
    }

    private fun isAnimationViewShowing(): Boolean {
        val flag =
            childFragmentManager.findFragmentByTag(LiveMatchAnimationFragment.TAG) is LiveMatchAnimationFragment
        "isAnimationViewShowing:$flag".logd(TAG)
        return flag
    }


    private fun scheduleHideVideoSourceBanner(duration: Long = HIDE_BUTTONS_TIMER) {
        if (mBinding.rvSource.visibility == View.GONE) return
        dismissBarJob?.cancel()
        dismissBarJob = lifecycleScope.launch {
            delay(duration)
            mediaSourceBarShowing = false
            val height = mBinding.rvSource.height

            if (animating) {
                return@launch
            }
            mBinding.root.startSafeAnimateSet(
                {
                    playTogether(
                        ValueAnimator.ofInt(height, 0).apply {
                            addUpdateListener {
                                val lp = mBinding.rvSource.layoutParams
                                lp.height = it.animatedValue as Int
                                mBinding.rvSource.layoutParams = lp
                                mainViewModel.setVideoInitHeight(
                                    it.animatedValue as Int + mBinding.fragmentMedia.height,
                                    true
                                )
                            }
                            addListener(doOnStart {
                                animating = true
                            }
                            )
                            addListener(doOnEnd {

                                mBinding.rvSource.visibility = View.GONE
                                animating = false
                                mainViewModel.setVideoInitHeight(
                                    mBinding.fragmentMedia.height,
                                    false
                                )
                            })
                        },
                    )
                },
                duration = AnimationController[AnimType.popupExit]!!.duration,
                interpolator = AnimationController[AnimType.popupExit]?.interpolator?.toInterpolator()
                    ?: LinearInterpolator(),
                start = true
            )
        }

    }

    private fun onMediaSourceItemClicked(item: MediaSource) {
        if (item.mediaSourceType == MediaSourceType.ANIMATION) {
            item.isPlaying = true
            (mBinding.rvSource.adapter as LiveMediaSourceSimpleAdapter).currentList.forEach {
                if (it.mediaSourceType == MediaSourceType.VIDEO) {
                    it.isPlaying = false
                }
            }
        } else if (item.mediaSourceType == MediaSourceType.VIDEO) {
            item.isPlaying = true
            mViewModel.setPlayingVideoId(item.videoSourceBean!!.id)
            (mBinding.rvSource.adapter as LiveMediaSourceSimpleAdapter).currentList.filter {
                it.mediaSourceType == MediaSourceType.VIDEO
            }.forEach { it.isPlaying = false }

        }
    }


    companion object {
        const val TAG = "LiveMatchMediaFragment"
    }

}