package com.walisport.module.live.ui

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.interface_.StatusBarConfig
import com.walisport.lib.base.utils.LogUtilsExt.logd
import com.walisport.lib.common.utils.ext.NavigationExt.navigate
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

        mBinding.ivVideoLandscapeLeagueIcon.clickNoRepeat {
            jumpToLeagueFragment()
        }

        mBinding.tvCompetitionName.clickNoRepeat {
            jumpToLeagueFragment()
        }

        mBinding.ivShare.clickNoRepeat { }

        mBinding.ivChooseSource.clickNoRepeat { }

        mBinding.tvChooseVideoSource.clickNoRepeat { }

        mBinding.tvMatchStatus.clickNoRepeat { }

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
        navigate(LiveVideoLandscapeFragmentDirections.actionLiveVideoLandscapeFragmentToLeagueFragment())
    }

}