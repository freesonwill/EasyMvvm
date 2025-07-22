package arch.cayenne.lib.base.utils.monitor

import android.view.Choreographer
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd

/**
 * @date: 2025/7/22 15:16
 * @description: FPS监控
 */

class FPSMonitor {
    private var frameCount = 0
    private var startTime = 0L
    private var mFps = 0
    private val TAG = this::class.java.simpleName

    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (startTime == 0L) {
                startTime = frameTimeNanos
            }
            frameCount++

            val duration = (frameTimeNanos - startTime) / 1_000_000_000f
            if (duration >= 1f) {
                val fps = Math.round(frameCount / duration)
                if(mFps < 60 || (mFps != fps) ) "FPS: $fps".logd(TAG)
                mFps = fps
                frameCount = 0
                startTime = frameTimeNanos
            }
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    fun start() {
        Choreographer.getInstance().postFrameCallback(frameCallback)
    }

    fun stop() {
        Choreographer.getInstance().removeFrameCallback(frameCallback)
    }

    fun fps(): Int = mFps

}