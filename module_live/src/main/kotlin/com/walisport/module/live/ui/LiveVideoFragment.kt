package com.walisport.module.live.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ViewUtils.getStatusBarHeight
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDimension
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.qyplayer.GlobalConfig
import arch.cayenne.lib.qyplayer.transformFromPlayerConfig
import arch.cayenne.lib.qyplayer.transformToPlayerConfig
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.MatchStatus
import com.walisport.module.live.databinding.FragmentLiveVideoBinding
import com.walisport.module.live.ui.LiveVideoLandscapeFragment.Companion.ANIMATION_DURATION
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import com.xxx.qyplayer.PlayerMode
import kotlin.reflect.KClass


/**
 * 竖屏播放视频页， 用在直播详情的首页
 */
class LiveVideoFragment : BaseFragment<LiveVideoViewModel, FragmentLiveVideoBinding>() {
    override val vbClass: KClass<FragmentLiveVideoBinding> = FragmentLiveVideoBinding::class
    override val vmClass: KClass<LiveVideoViewModel> = LiveVideoViewModel::class

    private var buttonsDisplaying = true

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.model = mViewModel
        mBinding.includedMatchNotInProgress.model = mViewModel

        initVideoView()
    }

    private fun initVideoView() {
        mBinding.videoView.apply {
            init(PlayerMode.FLUENCY, R.layout.layout_live_player_view)
            keepScreenOn = true
            setConfig(GlobalConfig(requireContext()).also {
                if (!it.inited) { // 首次启动从本地播放器获取默认配置
                    it.transformFromPlayerConfig(mBinding.videoView.getConfig())

                    // 更改底层默认配置。默认加密流，需要开启解密
                    it.isAudioDecrypt = false
                    it.isVideoDecrypt = false
                    it.isHWDecode = false

                    it.inited = true
                }
            }.transformToPlayerConfig())

            setOnSingleTapListener {
                //单击事件
                if (buttonsDisplaying) {
                    buttonsDisplaying = false

                    hideButtonsAnimated()
                } else {
                    buttonsDisplaying = true

                    showButtonsAnimated()
                }
            }

        }

    }

    override fun initListener() {

        with(mBinding) {
            ivChooseSource.setOnClickListener {
                val location = IntArray(2)
                videoView.getLocationOnScreen(location)
                val x = location[0]
                val y =
                    location[1] + videoView.measuredHeight - getStatusBarHeight(requireContext())
                LiveVideoSourcePortraitFragment().apply {
                    arguments = Bundle().apply {
                        putLong("matchId", mViewModel.matchId())
                        putInt(
                            arch.cayenne.lib.base.ui.fragment.LocationFixedDialogFragment.POSITION_X,
                            x
                        )
                        putInt(
                            arch.cayenne.lib.base.ui.fragment.LocationFixedDialogFragment.POSITION_Y,
                            y
                        )
                        putInt(
                            arch.cayenne.lib.base.ui.fragment.LocationFixedDialogFragment.WIDTH,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        putInt(
                            arch.cayenne.lib.base.ui.fragment.LocationFixedDialogFragment.HEIGHT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }
                    show(this@LiveVideoFragment.childFragmentManager)
                }

            }

            ivToFullscreen.clickNoRepeat {
                destroyPlayer()
                navigate(
                    LiveMainFragmentDirections.actionLiveMainFragmentToVideoLandscapeFragment()
                        .apply { arguments.putLong("matchId", mViewModel.matchId()) })
            }

            ivSoundToggle.clickNoRepeat { mViewModel.changeMuteStatus() }
        }

    }

    override fun createObserver() {
        with(mViewModel) {
            liveVideoBean.observe(viewLifecycleOwner) {
                it?.let {

                    val playUrl = it.source.firstOrNull { ele -> ele.isPlaying }?.playUrl()
                    playUrl?.takeIf { url -> url.isNotEmpty() }?.let { url ->
//                        "url:${url}".logd("LiveVideoFragment")
                        mBinding.videoView.setDataSource(url)
                        mBinding.videoView.prepare()
                    }

                }
            }

            mutedData().observe(viewLifecycleOwner) {
                mBinding.ivSoundToggle.setImageResource(
                    if (it) R.drawable.shape_muted else R.drawable.shape_immuted
                )

                mBinding.videoView.setMute(it)
            }

            //比赛状态的监听
            matchBeanLiveData.observe(viewLifecycleOwner) {
                it?.let { matchBean ->
                    val matchStatus =
                        MatchStatus.entries.find { status -> status.code == matchBean.basicInfo.status }

                    matchStatus?.let { _ ->
                        when (matchStatus) {
                            MatchStatus.IN_PROGRESS -> {
                                //比赛正在进行中
                                mBinding.ctVideoPlay.visibility = View.VISIBLE
                                //比赛正在进行中才会拉取视频流
                                mViewModel.queryLiveStream()
                            }

                            else -> {
                                //其他情况
                                mBinding.ctVideoPlay.visibility = View.GONE
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
                        .placeholder(arch.cayenne.lib.res.R.color.color_333A45)
                        .error(arch.cayenne.lib.res.R.color.color_333A45)
                        .into(mBinding.includedMatchNotInProgress.ivHomeTeam)
                }
            }

            awayTeamName.observe(viewLifecycleOwner) {
                it?.let { mBinding.includedMatchNotInProgress.tvAwayTeam.text = it }
            }

            awayTeamIcon.observe(viewLifecycleOwner) {
                it?.let {
                    Glide.with(requireContext())
                        .load(it)
                        .placeholder(arch.cayenne.lib.res.R.color.color_333A45)
                        .error(arch.cayenne.lib.res.R.color.color_333A45)
                        .into(mBinding.includedMatchNotInProgress.ivAwayTeam)
                }
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


    override fun initData() {
        super.initData()
    }

    override fun onPause() {
        super.onPause()
        mBinding.videoView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mBinding.videoView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.videoView.onDestroy()
    }

    override fun onDestroyView() {
        destroyPlayer()
        super.onDestroyView()
    }

    private fun destroyPlayer() {

        //todo: destroyPlayer
//        mBinding.videoView.stopPlayback()
//        mBinding.videoView.release(true)
//        mBinding.videoView.stopBackgroundPlay()
    }

    /**
     * 隐藏底部操作栏
     */
    private fun hideButtonsAnimated(){
        val operateAreaHeight =
            resources.getDimensionPixelSize(R.dimen.video_operate_area_height).toFloat()

        with(AnimatorSet()) {
            playTogether(
                ObjectAnimator.ofFloat(
                    mBinding.bottomArea,
                    "translationY",
                    *floatArrayOf(0f, operateAreaHeight)
                ),
                ObjectAnimator.ofFloat(mBinding.bottomArea, "alpha", 1f, 0.5f),
            )
            setDuration(ANIMATION_DURATION)

            start()
        }
    }

    /**
     * 展示底部操作栏
     */
    private fun showButtonsAnimated(){
        val operateAreaHeight =
            resources.getDimensionPixelSize(R.dimen.video_operate_area_height).toFloat()

        with(AnimatorSet()) {
            playTogether(
                ObjectAnimator.ofFloat(
                    mBinding.bottomArea,
                    "translationY",
                    *floatArrayOf(operateAreaHeight, 0f)
                ),
                ObjectAnimator.ofFloat(
                    mBinding.bottomArea,
                    "alpha",
                    *floatArrayOf(0.5f, 1f)
                ),

                )
            setDuration(ANIMATION_DURATION)
            start()
        }
    }

    companion object {
        const val TAG = "LiveVideoFragment"
    }
}