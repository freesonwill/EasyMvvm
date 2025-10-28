package com.walisport.module.gamedetail.ui.view

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.VideoView

class VideoCarouselItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), CarouselScrollView.CarouselItem {

    private val videoView: VideoView = VideoView(context).apply {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        )
        setOnErrorListener { mediaPlayer, i, i2 ->
            true
        }
    }

    init {
        addView(videoView)
    }

    override fun onCentered() {
        if (!videoView.isPlaying) {
            videoView.start()
        }
    }

    override fun onLostFocus() {
        if (videoView.isPlaying) {
            videoView.pause()
        }
    }

    fun setVideoURI(uri: Uri) {
        videoView.setVideoURI(uri)
        videoView.start()
    }
}