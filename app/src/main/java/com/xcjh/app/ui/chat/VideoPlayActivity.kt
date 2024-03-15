package com.xcjh.app.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gyf.immersionbar.ImmersionBar
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.xcjh.app.R
import com.xcjh.app.databinding.ActivityMatchDetailBinding
import com.xcjh.app.databinding.ActivityVideoPlayBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.ui.details.common.GSYBaseActivity
import com.xcjh.base_lib.utils.startNewActivity
import com.xcjh.base_lib.utils.view.clickNoRepeat

class VideoPlayActivity  : GSYBaseActivity<DetailVm, ActivityVideoPlayBinding, StandardGSYVideoPlayer>() {
    var playUrl:String=""
    companion object {
        fun open(
            videoUrl: String? = null,
        ) {
            startNewActivity<VideoPlayActivity> {
                putExtra("videoUrl", videoUrl)

            }
        }
    }


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ImmersionBar.with(this).statusBarDarkFont(false)//白色
            .navigationBarColor(R.color.c_181819)
            .navigationBarDarkIcon(false)
            .titleBarMarginTop(mDatabind.rlTitle).init()
        intent.extras?.apply {

            playUrl = getString("videoUrl", null)
        }

        mDatabind.ivBack.clickNoRepeat {
            finish()
        }
        mDatabind.videoPlayer.setUp(playUrl, false, "")
        mDatabind.videoPlayer.startPlayLogic()
    }






    override val gSYVideoPlayer: StandardGSYVideoPlayer
        get() =  mDatabind.videoPlayer
    override val gSYVideoOptionBuilder: GSYVideoOptionBuilder
        get() = GSYVideoOptionBuilder()
            .setLooping(true)
            .setStartAfterPrepared(true)
            .setCacheWithPlay(true)
            .setVideoTitle("视频")
            .setIsTouchWiget(true) //.setAutoFullWithSize(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setShowFullAnimation(false) //打开动画
            .setNeedLockFull(true)
            .setNeedOrientationUtils(false)
            .setSeekRatio(1f)

    override fun clickForFullScreen() {

    }

    override val detailOrientationRotateAuto: Boolean
        get() =false




}