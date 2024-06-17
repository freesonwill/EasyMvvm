package com.cn.game.sdk2.utils.tool

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.cn.game.sdk2.R
import java.io.IOException

/**
 * 提示音 Gold
 */
object PromptSoundPlay {
    /**
     * 金币声音
     */
    private var goldMediaPlayer: MediaPlayer? = null

    /**
     * 按钮声音声音
     */
    private var btnMediaPlayer: MediaPlayer? = null

    /**
     * 金币提示音  要关闭前一个
     */
    fun goldPlayMedia(context: Context) {
        if (goldMediaPlayer == null) {
            goldMediaPlayer = MediaPlayer.create(context, R.raw.jinbi_ying)
        }

        if (goldMediaPlayer != null) {

            // 检查播放器状态
            if (goldMediaPlayer!!.isPlaying) {
                // 如果正在播放，先停止播放
                goldMediaPlayer!!.stop()
            }
            // 重置 MediaPlayer 对象
            goldMediaPlayer!!.reset()

            try {
                // 设置要播放的媒体资源
                goldMediaPlayer!!.setDataSource(context, getResourceUri(context, R.raw.jinbi_ying))
                // 准备MediaPlayer
                goldMediaPlayer!!.prepare()
                // 启动播放
                goldMediaPlayer!!.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }


        }

    }



    /**
     * 金币提示音~~可以一直提示
     */
    fun playAudio(context: Context) {
        if(!isPhoneSilent(context)){
            val newMediaPlayer = MediaPlayer()
            try {
                newMediaPlayer!!.setDataSource(context, getResourceUri(context, R.raw.jinbi_ying))
                newMediaPlayer.setOnCompletionListener(OnCompletionListener { mp ->
                    mp.release() // 在播放完成后释放MediaPlayer
                })
                newMediaPlayer.setOnPreparedListener { mp -> mp.start() }
                newMediaPlayer.prepareAsync()
            } catch (e: IOException) {
                e.printStackTrace()
            }


        }
    }

    /**
     * 开始游戏声音
     */
    fun startGameTip(context: Context) {
        if(!isPhoneSilent(context)){
            val startMediaPlayer = MediaPlayer()
            try {
                startMediaPlayer!!.setDataSource(context, getResourceUri(context, R.raw.sx_common_start))
                startMediaPlayer.setOnCompletionListener(OnCompletionListener { mp ->
                    mp.release() // 在播放完成后释放MediaPlayer
                })
                startMediaPlayer.setOnPreparedListener { mp -> mp.start() }
                startMediaPlayer.prepareAsync()
            } catch (e: IOException) {
                e.printStackTrace()
            }


        }
    }

    /**
     * 还有五秒快要结束的时候
     */
    fun countdownGameTip(context: Context) {
        if(!isPhoneSilent(context)){
            val endMediaPlayer = MediaPlayer()
            try {
                endMediaPlayer!!.setDataSource(context, getResourceUri(context, R.raw.sx_common_countdown))
                endMediaPlayer.setOnCompletionListener(OnCompletionListener { mp ->
                    mp.release() // 在播放完成后释放MediaPlayer
                })
                endMediaPlayer.setOnPreparedListener { mp -> mp.start() }
                endMediaPlayer.prepareAsync()
            } catch (e: IOException) {
                e.printStackTrace()
            }


        }
    }

    /**
     * 结束语音
     */
    fun endGameTip(context: Context) {
        if(!isPhoneSilent(context)){
            val endMediaPlayer = MediaPlayer()
            try {
                endMediaPlayer!!.setDataSource(context, getResourceUri(context, R.raw.sx_common_stop))
                endMediaPlayer.setOnCompletionListener(OnCompletionListener { mp ->
                    mp.release() // 在播放完成后释放MediaPlayer
                })
                endMediaPlayer.setOnPreparedListener { mp -> mp.start() }
                endMediaPlayer.prepareAsync()
            } catch (e: IOException) {
                e.printStackTrace()
            }


        }
    }

    /**
     * 按钮提示音
     */
    fun btnPlayMedia(context: Context) {
        if (btnMediaPlayer == null) {
            btnMediaPlayer = MediaPlayer.create(context, R.raw.btn_ying_click)
        }
        if(!isPhoneSilent(context)){
            if (btnMediaPlayer != null) {
                // 检查播放器状态
                if (btnMediaPlayer!!.isPlaying) {
                    // 如果正在播放，先停止播放
                    btnMediaPlayer!!.stop()
                }
                // 重置 MediaPlayer 对象
                btnMediaPlayer!!.reset()

                try {
                    // 设置要播放的媒体资源
                    btnMediaPlayer!!.setDataSource(
                        context,
                        getResourceUri(context, R.raw.btn_ying_click)
                    )
                    // 准备MediaPlayer
                    btnMediaPlayer!!.prepare()
                    // 启动播放
                    btnMediaPlayer!!.start()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

    }


    // 获取资源文件的URI
    private fun getResourceUri(context: Context, resId: Int): Uri {
        return Uri.parse("android.resource://" + context.packageName + "/" + resId)
    }

    private var lastClickTime: Long = 0
    private val debounceInterval: Long = 200 // 防抖间隔
    private var isWaitingForClear = false

    private val handler = Handler(Looper.getMainLooper())

    fun handleClick(): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastClickTime >= debounceInterval) {
            // 距离上次点击时间超过防抖间隔，立即执行点击操作
            lastClickTime = now // 更新上次点击时间
            return true
        }

        // 等待清除点击时间
        if (!isWaitingForClear) {
            isWaitingForClear = true
            handler.postDelayed(clearLastClickRunnable, debounceInterval)
        }
        return false
    }

    private val clearLastClickRunnable = Runnable {
        lastClickTime = 0
        isWaitingForClear = false
    }

    /**
     * 是否是静音或者震动模式
     */
    fun isPhoneSilent(context: Context): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_SILENT, AudioManager.RINGER_MODE_VIBRATE -> true // 静音或振动模式
            else -> false // 声音模式
        }
    }
}