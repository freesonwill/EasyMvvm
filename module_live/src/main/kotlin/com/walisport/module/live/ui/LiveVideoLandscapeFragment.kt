package com.walisport.module.live.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.interface_.StatusBarConfig
import com.walisport.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.databinding.FragmentLiveVideoLandscapeBinding
import com.walisport.module.live.viewmodel.VideoActivityViewModel
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.internal.CancelAdapt
import kotlin.reflect.KClass


class LiveVideoLandscapeFragment :
    BaseFragment<VideoActivityViewModel, FragmentLiveVideoLandscapeBinding>(), CancelAdapt {

    override val vbClass: KClass<FragmentLiveVideoLandscapeBinding> =
        FragmentLiveVideoLandscapeBinding::class
    override val vmClass: KClass<VideoActivityViewModel> = VideoActivityViewModel::class

    private var mBackPressed = false

    private var videoViewFullScreen = true

    private var buttonsDisplaying = true


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
//        "onConfigurationChanged".logd(TAG)
    }


    override fun initView(savedInstanceState: Bundle?) {

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
                showButtons()
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
        }

        mBinding.ivChooseSource.clickNoRepeat {
            hideButtons()
        }

        mBinding.tvChooseVideoSource.clickNoRepeat {
            hideButtons()
        }

        mBinding.tvMatchStatus.clickNoRepeat {
            hideButtons()
        }

    }

    private fun showButtons() {
        mBinding.topArea.visibility = View.VISIBLE
        mBinding.bottomArea.visibility = View.VISIBLE
    }

    private fun hideButtons() {
        mBinding.topArea.visibility = View.GONE
        mBinding.bottomArea.visibility = View.GONE
    }

    private fun showButtonsAnimated() {
        with(AnimatorSet()) {
            playTogether(
                ObjectAnimator.ofFloat(
                    mBinding.topArea,
                    "translationY",
                    *floatArrayOf(-100f.dp2px.toFloat(), 0f)
                ),
                ObjectAnimator.ofFloat(
                    mBinding.topArea,
                    "alpha",
                    *floatArrayOf(0.5f, 1f)
                ),
                ObjectAnimator.ofFloat(
                    mBinding.bottomArea,
                    "translationY",
                    *floatArrayOf(100f.dp2px.toFloat(), 0f)
                ),
                ObjectAnimator.ofFloat(
                    mBinding.bottomArea,
                    "alpha",
                    *floatArrayOf(0.5f, 1f)
                ),

                )
            setDuration(300)
            start()
        }
    }

    private fun hideButtonsAnimated() {
        with(AnimatorSet()) {
            playTogether(
                ObjectAnimator.ofFloat(
                    mBinding.topArea,
                    "translationY",
                    0f, -100f.dp2px.toFloat()
                ),
                ObjectAnimator.ofFloat(mBinding.topArea, "alpha", 1f, 0.5f),
                ObjectAnimator.ofFloat(
                    mBinding.bottomArea,
                    "translationY",
                    *floatArrayOf(0f, 100f.dp2px.toFloat())
                ),
                ObjectAnimator.ofFloat(mBinding.bottomArea, "alpha", 1f, 0.5f),
            )
            setDuration(300)

            start()
        }
    }



    override fun createObserver() {
        mViewModel.url.observe(this) {
            mBinding.videoView.setVideoURI(Uri.parse(it))
            mBinding.videoView.start()
        }

        mViewModel.leagueIconUrl.observe(this) {
            Glide.with(mBinding.ivVideoLandscapeLeagueIcon).load(it)
                .placeholder(com.walisport.lib.common.R.drawable.title_league_icon)
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
        AutoSizeConfig.getInstance().setDesignWidthInDp(812)
        AutoSizeConfig.getInstance().setDesignHeightInDp(375)

        mBinding.root.fitsSystemWindows = false
        setStatusBar(StatusBarConfig(hideStatusBar = true))

    }

    override fun onPause() {
//        "onPause".logd(TAG)
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        //恢复竖屏，宽高也要回到竖屏时到宽高
        AutoSizeConfig.getInstance().setDesignWidthInDp(375)
        AutoSizeConfig.getInstance().setDesignHeightInDp(812)

        mBinding.root.fitsSystemWindows = true
        setStatusBar(StatusBarConfig(hideStatusBar = false))
    }


//    override fun onBackPressed() {
//        mBackPressed = true
//        super.onBackPressed()
//    }

    private fun jumpToLeagueFragment() {
        findNavController().navigate(LiveVideoLandscapeFragmentDirections.actionLiveVideoLandscapeFragmentToLeagueFragment())
    }

}