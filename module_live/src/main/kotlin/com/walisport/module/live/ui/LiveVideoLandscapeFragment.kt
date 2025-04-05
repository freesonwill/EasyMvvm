package com.walisport.module.live.ui

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.interface_.StatusBarConfig
import com.walisport.lib.base.utils.LogUtilsExt.logd
import com.walisport.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.databinding.FragmentLiveVideoLandscapeBinding
import com.walisport.module.live.viewmodel.VideoActivityViewModel
import me.jessyan.autosize.internal.CancelAdapt
import tv.danmaku.ijk.media.example.widget.media.AndroidMediaController
import kotlin.reflect.KClass

class LiveVideoLandscapeFragment :
    BaseFragment<VideoActivityViewModel, FragmentLiveVideoLandscapeBinding>(), CancelAdapt {
    override val vbClass: KClass<FragmentLiveVideoLandscapeBinding> =
        FragmentLiveVideoLandscapeBinding::class
    override val vmClass: KClass<VideoActivityViewModel> = VideoActivityViewModel::class

    private var mBackPressed = false


    override fun configStatusBar(): StatusBarConfig {
        return StatusBarConfig(hideStatusBar = true)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.videoView.setMediaController(AndroidMediaController(activity, false))
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
        super.onStop()

        if (mBackPressed || !mBinding.videoView.isBackgroundPlayEnabled) {
            mBinding.videoView.stopPlayback()
            mBinding.videoView.release(true)
            mBinding.videoView.stopBackgroundPlay()
        } else {
            mBinding.videoView.enterBackground()
        }

        "onStop".logd(TAG)
    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        "onResume".logd(TAG)

    }

    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        "onPause".logd(TAG)
    }

//    override fun onBackPressed() {
//        mBackPressed = true
//        super.onBackPressed()
//    }

    private fun jumpToLeagueFragment() {
        findNavController().navigate(LiveVideoLandscapeFragmentDirections.actionLiveVideoLandscapeFragmentToLeagueFragment())
    }


    companion object {
        const val TAG = "LiveVideoLandscapeFragment"
    }
}