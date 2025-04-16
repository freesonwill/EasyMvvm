package com.walisport.module.live.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.interface_.StatusBarConfig
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.res.SportSkinResourceManager.getDrawable
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveVideoLandscapeBinding
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.internal.CancelAdapt
import kotlin.reflect.KClass

/**
 * 视频横屏播放页
 */
class LiveVideoLandscapeFragment :
    BaseFragment<LiveVideoViewModel, FragmentLiveVideoLandscapeBinding>(), CancelAdapt {

    override val vbClass: KClass<FragmentLiveVideoLandscapeBinding> =
        FragmentLiveVideoLandscapeBinding::class
    override val vmClass: KClass<LiveVideoViewModel> = LiveVideoViewModel::class

    private var mBackPressed = false

    private var videoViewFullScreen = true

    private var buttonsDisplaying = true


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }


    override fun initView(savedInstanceState: Bundle?) {
//        mViewModel.addMockData()
    }

    override fun initListener() {
        mBinding.ivBack.clickNoRepeat {
            findNavController().navigateUp()
        }

        mBinding.root.clickNoRepeat {
            if (videoViewFullScreen) {
                if (buttonsDisplaying) {
                    buttonsDisplaying = false

                    hideButtonsAnimated()
                } else {
                    buttonsDisplaying = true

                    showButtonsAnimated()
                }
            } else {
                enlarge {
                    showButtons()
                    videoViewFullScreen = true
                }

                //隐藏子fragment
                hideFragment()
            }
        }

        mBinding.ivVideoLandscapeLeagueIcon.clickNoRepeat {
            jumpToLeagueFragment()
        }

        mBinding.tvCompetitionName.clickNoRepeat {
            jumpToLeagueFragment()
        }

        mBinding.ivShare.clickNoRepeat {
            hideButtons()
            reduce {
                videoViewFullScreen = false
            }
            setShareView()
            showShareView()
        }

        mBinding.llChooseSource.clickNoRepeat {
            hideButtons()
            reduce {
                videoViewFullScreen = false
            }

            setChooseSourceView()
            showChooseSourceView()
        }


        mBinding.tvStatistics.clickNoRepeat {
            hideButtons()
            reduce(
                targetWidth = 376.dp2px,
                targetHeight = 209.dp2px,
                targetMarginStart = 32.dp2px
            ) {
                videoViewFullScreen = false
            }

            setStatisticsView()
            showStatisticsView()
        }

    }

    /**
     * 展示上边和下边的操作按钮，不带动画
     */
    private fun showButtons() {
        mBinding.topArea.visibility = View.VISIBLE
        mBinding.bottomArea.visibility = View.VISIBLE
    }

    /**
     * 隐藏上边和下边的操作按钮，不带动画
     */
    private fun hideButtons() {
        mBinding.topArea.visibility = View.GONE
        mBinding.bottomArea.visibility = View.GONE
    }

    /**
     * 展示上边和下边的操作按钮，带动画
     */
    private fun showButtonsAnimated() {
        val operateAreaHeight =
            resources.getDimensionPixelSize(R.dimen.video_landscape_operate_area_height).toFloat()

        with(AnimatorSet()) {
            playTogether(
                ObjectAnimator.ofFloat(
                    mBinding.topArea,
                    "translationY",
                    *floatArrayOf(-operateAreaHeight, 0f)
                ),
                ObjectAnimator.ofFloat(
                    mBinding.topArea,
                    "alpha",
                    *floatArrayOf(0.5f, 1f)
                ),
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

    /**
     * 隐藏上边和下边的操作按钮，带动画
     */
    private fun hideButtonsAnimated() {
        val operateAreaHeight =
            resources.getDimensionPixelSize(R.dimen.video_landscape_operate_area_height).toFloat()

        with(AnimatorSet()) {
            playTogether(
                ObjectAnimator.ofFloat(
                    mBinding.topArea,
                    "translationY",
                    0f, -operateAreaHeight
                ),
                ObjectAnimator.ofFloat(mBinding.topArea, "alpha", 1f, 0.5f),
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
     * 放大视频播放区
     */
    private fun enlarge(onEndAction: () -> Unit) {
        //width， height， marginStart, marginTop
        val currentHeight = mBinding.videoView.measuredHeight
        val targetWidth = mBinding.root.measuredWidth
        val currentWidth = mBinding.videoView.measuredWidth
        val targetHeight = mBinding.root.measuredHeight
        val currentMarginTop =
            (mBinding.videoView.layoutParams as ConstraintLayout.LayoutParams).topMargin
        val targetMarginTop = 0
        val currentMarginStart =
            (mBinding.videoView.layoutParams as ConstraintLayout.LayoutParams).marginStart
        val targetMarginStart = 0

        with(AnimatorSet()) {
            playTogether(
                ValueAnimator.ofInt(currentHeight, targetHeight).apply {
                    addUpdateListener {
                        val lp = mBinding.videoView.layoutParams
                        lp.height = it.animatedValue as Int

                        mBinding.videoView.layoutParams = lp
                    }
                },
                ValueAnimator.ofInt(currentWidth, targetWidth).apply {
                    addUpdateListener {
                        val lp = mBinding.videoView.layoutParams
                        lp.width = it.animatedValue as Int

                        mBinding.videoView.layoutParams = lp
                    }
                },
                ValueAnimator.ofInt(currentMarginTop, targetMarginTop).apply {
                    addUpdateListener {
                        val lp = mBinding.videoView.layoutParams as ConstraintLayout.LayoutParams
                        lp.topMargin = it.animatedValue as Int

                        mBinding.videoView.layoutParams = lp

                    }
                },
                ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
                    addUpdateListener {
                        val lp = mBinding.videoView.layoutParams as ConstraintLayout.LayoutParams
                        lp.marginStart = it.animatedValue as Int

                        mBinding.videoView.layoutParams = lp

                    }
                })
            setDuration(ANIMATION_DURATION)
            doOnEnd {
                mBinding.videoView.background =
                    getDrawable(requireContext(), arch.cayenne.lib.common.R.color.black)
                onEndAction()
            }
            start()
        }
    }

    /**
     * 缩小视频播放区
     */
    private fun reduce(
        targetWidth: Int = 495.dp2px,
        targetHeight: Int = 275.dp2px,
        targetMarginStart: Int = 32.dp2px,
        onEndAction: () -> Unit
    ) {
        //width， height， marginStart, marginTop
        val currentHeight = mBinding.root.measuredHeight
        val currentWidth = mBinding.root.measuredWidth
        val currentMarginTop = 0
        val targetMarginTop = (currentHeight - targetHeight) / 2
        val currentMarginStart = 0

        with(AnimatorSet()) {
            playTogether(
                ValueAnimator.ofInt(currentHeight, targetHeight).apply {
                    addUpdateListener {
                        val lp = mBinding.videoView.layoutParams
                        lp.height = it.animatedValue as Int

                        mBinding.videoView.layoutParams = lp
                    }
                },
                ValueAnimator.ofInt(currentWidth, targetWidth).apply {
                    addUpdateListener {
                        val lp = mBinding.videoView.layoutParams
                        lp.width = it.animatedValue as Int

                        mBinding.videoView.layoutParams = lp
                    }
                },
                ValueAnimator.ofInt(currentMarginTop, targetMarginTop).apply {
                    addUpdateListener {
                        val lp = mBinding.videoView.layoutParams as ConstraintLayout.LayoutParams
                        lp.topMargin = it.animatedValue as Int

                        mBinding.videoView.layoutParams = lp

                    }
                },
                ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
                    addUpdateListener {
                        val lp = mBinding.videoView.layoutParams as ConstraintLayout.LayoutParams
                        lp.marginStart = it.animatedValue as Int

                        mBinding.videoView.layoutParams = lp

                    }
                })
            setDuration(ANIMATION_DURATION)
            doOnEnd {
                mBinding.videoView.background =
                    getDrawable(requireContext(), R.drawable.bg_shape_video_view_reduced)
                onEndAction()
            }
            start()
        }

    }


    override fun createObserver() {
        with(mViewModel) {
            liveVideoBean.observe(viewLifecycleOwner) {
                mBinding.videoView.setVideoURI(Uri.parse(it.url))
                mBinding.videoView.start()
            }
        }



        mViewModel.leagueIconUrl.observe(this) {
            Glide.with(mBinding.ivVideoLandscapeLeagueIcon).load(it)
                .placeholder(R.drawable.title_league_icon)
                .into(mBinding.ivVideoLandscapeLeagueIcon)
        }

        mViewModel.playerAName.observe(this) {
            mBinding.tvCompetitionName.text = "$it vs ${mViewModel.playerBName.value}"
        }
    }

    override fun onStop() {
//        "onStop".logd(TAG)
        super.onStop()

        if (mBackPressed || !mBinding.videoView.isBackgroundPlayEnabled) {
            mBinding.videoView.stopPlayback()
            mBinding.videoView.release(true)
            mBinding.videoView.stopBackgroundPlay()
        } else {
            mBinding.videoView.enterBackground()
        }
    }

    override fun onResume() {
//        "onResume".logd(TAG)
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        //使用横屏时到宽高
        AutoSizeConfig.getInstance().setDesignWidthInDp(LANDSCAPE_WIDTH)
        AutoSizeConfig.getInstance().setDesignHeightInDp(LANDSCAPE_HEIGHT)

        mBinding.root.fitsSystemWindows = false
        setStatusBar(StatusBarConfig(hideStatusBar = true))

    }

    override fun onPause() {
//        "onPause".logd(TAG)
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        //恢复竖屏，宽高也要回到竖屏时到宽高
        AutoSizeConfig.getInstance().setDesignWidthInDp(PORTRAIT_WIDTH)
        AutoSizeConfig.getInstance().setDesignHeightInDp(PORTRAIT_HEIGHT)

        mBinding.root.fitsSystemWindows = true
        setStatusBar(StatusBarConfig(hideStatusBar = false))
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayer()
    }

    private fun destroyPlayer() {
        mBinding.videoView.stopPlayback()
        mBinding.videoView.release(true)
        mBinding.videoView.stopBackgroundPlay()
    }


//    override fun onBackPressed() {
//        mBackPressed = true
//        super.onBackPressed()
//    }

    /**
     * 跳转到联赛赛程页
     */
    private fun jumpToLeagueFragment() {
        navigate(LiveVideoLandscapeFragmentDirections.actionLiveVideoLandscapeFragmentToLeagueFragment())
    }

    /**
     *  创建视频分享页
     */
    private fun setShareView() {
        childFragmentManager.findFragmentByTag(LiveVideoShareFragment.TAG)
                as? LiveVideoShareFragment ?: LiveVideoShareFragment().also {
            childFragmentManager.beginTransaction()
                .replace(mBinding.fragmentShare.id, it, LiveVideoShareFragment.TAG)
                .commitNow()
        }

    }

    /**
     * 视频分享页入场动画
     */
    private fun showShareView() {
        val currentMarginStart =
            (mBinding.fragmentShare.layoutParams as ConstraintLayout.LayoutParams).marginStart
        val targetMarginStart = -mBinding.fragmentShare.measuredWidth
        ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
            addUpdateListener {
                val lp = mBinding.fragmentShare.layoutParams as ConstraintLayout.LayoutParams
                lp.marginStart = it.animatedValue as Int

                mBinding.fragmentShare.layoutParams = lp

            }
            setDuration(ANIMATION_DURATION)
            start()
        }
    }

    /**
     * 视频分享页退场动画
     */
    private fun hideShareView(onEndAction: () -> Unit) {
        val currentMarginStart =
            (mBinding.fragmentShare.layoutParams as ConstraintLayout.LayoutParams).marginStart
        val targetMarginStart = 0
        ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
            addUpdateListener {
                val lp = mBinding.fragmentShare.layoutParams as ConstraintLayout.LayoutParams
                lp.marginStart = it.animatedValue as Int

                mBinding.fragmentShare.layoutParams = lp

            }
            doOnEnd { onEndAction() }
            setDuration(ANIMATION_DURATION)
            start()
        }
    }

    /**
     * 创建选择视频源页
     */
    private fun setChooseSourceView() {
        childFragmentManager.findFragmentByTag(LiveVideoSourceLandscapeFragment.TAG)
                as? LiveVideoSourceLandscapeFragment ?: LiveVideoSourceLandscapeFragment().also {
            childFragmentManager.beginTransaction()
                .replace(mBinding.fragmentChooseSource.id, it, LiveVideoSourceLandscapeFragment.TAG)
                .commitNow()
        }

    }

    /**
     *视频源页入场动画
     */
    private fun showChooseSourceView() {
        val currentMarginStart =
            (mBinding.fragmentChooseSource.layoutParams as ConstraintLayout.LayoutParams).marginStart
        val targetMarginStart = -mBinding.fragmentChooseSource.measuredWidth

        ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
            addUpdateListener {
                val lp = mBinding.fragmentChooseSource.layoutParams as ConstraintLayout.LayoutParams
                lp.marginStart = it.animatedValue as Int

                mBinding.fragmentChooseSource.layoutParams = lp

            }
            setDuration(ANIMATION_DURATION)
            start()
        }
    }

    /**
     * 视频源页面退场动画
     */
    private fun hideChooseSourceView(onEndAction: () -> Unit) {
        val currentMarginStart =
            (mBinding.fragmentChooseSource.layoutParams as ConstraintLayout.LayoutParams).marginStart
        val targetMarginStart = 0
        ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
            addUpdateListener {
                val lp = mBinding.fragmentChooseSource.layoutParams as ConstraintLayout.LayoutParams
                lp.marginStart = it.animatedValue as Int

                mBinding.fragmentChooseSource.layoutParams = lp

            }
            doOnEnd { onEndAction() }
            setDuration(ANIMATION_DURATION)
            start()
        }
    }

    /**
     * 创建赛况统计页
     */
    private fun setStatisticsView() {
        childFragmentManager.findFragmentByTag(LiveVideoStatisticsFragment.TAG)
                as? LiveVideoStatisticsFragment ?: LiveVideoStatisticsFragment().also {
            childFragmentManager.beginTransaction()
                .replace(mBinding.fragmentStatistics.id, it, LiveVideoStatisticsFragment.TAG)
                .commitNow()
        }

    }

    /**
     * 赛况页入场动画
     */
    private fun showStatisticsView() {
        val currentMarginStart =
            (mBinding.fragmentStatistics.layoutParams as ConstraintLayout.LayoutParams).marginStart
        val targetMarginStart = -mBinding.fragmentStatistics.measuredWidth
        ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
            addUpdateListener {
                val lp = mBinding.fragmentStatistics.layoutParams as ConstraintLayout.LayoutParams
                lp.marginStart = it.animatedValue as Int

                mBinding.fragmentStatistics.layoutParams = lp

            }
            setDuration(ANIMATION_DURATION)
            start()
        }
    }

    /**
     * 赛况页退场动画
     */
    private fun hideStatisticsView(onEndAction: () -> Unit) {
        val currentMarginStart =
            (mBinding.fragmentStatistics.layoutParams as ConstraintLayout.LayoutParams).marginStart
        val targetMarginStart = 0
        ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
            addUpdateListener {
                val lp = mBinding.fragmentStatistics.layoutParams as ConstraintLayout.LayoutParams
                lp.marginStart = it.animatedValue as Int

                mBinding.fragmentStatistics.layoutParams = lp

            }
            doOnEnd { onEndAction() }
            setDuration(ANIMATION_DURATION)
            start()
        }
    }

    /**
     * 移除分享页，视频源页，赛况页
     */
    private fun hideFragment() {
        val fragment = childFragmentManager.findFragmentByTag(LiveVideoShareFragment.TAG)

        if (fragment is LiveVideoShareFragment) {
            hideShareView {
                childFragmentManager.beginTransaction().remove(fragment).commit()
            }
        }

        val fragment2 = childFragmentManager.findFragmentByTag(LiveVideoSourceLandscapeFragment.TAG)

        if (fragment2 is LiveVideoSourceLandscapeFragment) {
            hideChooseSourceView {
                childFragmentManager.beginTransaction().remove(fragment2).commit()
            }
        }

        val fragment3 = childFragmentManager.findFragmentByTag(LiveVideoStatisticsFragment.TAG)

        if (fragment3 is LiveVideoStatisticsFragment) {
            hideStatisticsView {
                childFragmentManager.beginTransaction().remove(fragment3).commit()
            }
        }


    }

    companion object {
        const val ANIMATION_DURATION = 300L

        const val LANDSCAPE_WIDTH = 812
        const val LANDSCAPE_HEIGHT = 375

        const val PORTRAIT_WIDTH = 375
        const val PORTRAIT_HEIGHT = 812
    }

}