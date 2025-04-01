package com.walisport.module_live.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.walisport.module_live.R
import tv.danmaku.ijk.media.example.widget.media.AndroidMediaController
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView

class VideoLandscapeActivity : Activity() {
    private var mBackPressed = false
    private var mMediaController: AndroidMediaController? = null
    private var mVideoView: IjkVideoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_landscape)
        hideSystemUI()

        val testUrl =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"

//        val url =
//            "https://jjghvku4.jmyuyu.com/sport/201_3455709_1.flv?auth_key=1742356008-0-0-cebbd8cd6498a8af12b4d1962956fa99"

        mVideoView = findViewById<IjkVideoView>(R.id.videoView)
        mMediaController = AndroidMediaController(this, false)
        mVideoView?.setMediaController(mMediaController)
        mVideoView?.setVideoURI(Uri.parse(testUrl))
        mVideoView?.start()

        findViewById<ImageView>(R.id.video_landscape_back).setOnClickListener { this@VideoLandscapeActivity.finish() }

    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val insetsController: WindowInsetsControllerCompat =
            WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.statusBars())
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
    }


    override fun onBackPressed() {
        mBackPressed = true

        super.onBackPressed()
    }

    override fun onStop() {
        super.onStop()

        if (mBackPressed || !mVideoView?.isBackgroundPlayEnabled!!) {
            mVideoView?.stopPlayback()
            mVideoView?.release(true)
            mVideoView?.stopBackgroundPlay()
        } else {
            mVideoView?.enterBackground()
        }
    }

}

