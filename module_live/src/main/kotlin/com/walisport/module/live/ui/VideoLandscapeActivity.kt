package com.walisport.module.live.ui

import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.walisport.lib.base.ui.BaseActivity
import com.walisport.lib.base.ui.interface_.StatusBarConfig
import com.walisport.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.databinding.ActivityVideoLandscapeBinding
import com.walisport.module.live.viewmodel.VideoActivityViewModel
import me.jessyan.autosize.internal.CancelAdapt
import tv.danmaku.ijk.media.example.widget.media.AndroidMediaController
import kotlin.reflect.KClass


class VideoLandscapeActivity :
    BaseActivity<VideoActivityViewModel, ActivityVideoLandscapeBinding>(), CancelAdapt {

    override val vbClass: KClass<ActivityVideoLandscapeBinding> = ActivityVideoLandscapeBinding::class
    override val vmClass: KClass<VideoActivityViewModel> = VideoActivityViewModel::class

    private var mBackPressed = false


    override fun configStatusBar(): StatusBarConfig {
        return StatusBarConfig(hideStatusBar = true)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.videoView.setMediaController(AndroidMediaController(this, false))
    }

    override fun initListener() {
        mBinding.ivBack.clickNoRepeat {
            this@VideoLandscapeActivity.finish()
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
            Glide.with(mBinding.tvVideoLandscapeLeagueIcon).load(it)
                .placeholder(com.walisport.lib.common.R.drawable.title_league_icon)
                .into(mBinding.tvVideoLandscapeLeagueIcon)
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
    }

    override fun onBackPressed() {
        mBackPressed = true
        super.onBackPressed()
    }


}

