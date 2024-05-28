package com.xcjh.app.utils

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import com.xcjh.app.R
import com.xcjh.base_lib.appContext
import java.io.IOException


/**
 * 提示音
 */
object SoundManager {
    private var mediaPlayer: MediaPlayer? = null
    //初始化
    fun initialize(context:Context){
        if(mediaPlayer==null){
            mediaPlayer = MediaPlayer.create(context, R.raw.sx_common_click)
        }

    }


    // 播放媒体资源
//     fun playMedia() {
//        if (mediaPlayer != null) {
//            //是否正在播放
//            if(mediaPlayer!!.isPlaying){
//                // 停止当前播放
//                mediaPlayer!!.stop()
//                // 重置 MediaPlayer 对象
//                mediaPlayer!!.reset()
//                // 重新初始化并设置要播放的媒体资源
//                mediaPlayer!!.start()
//            }else{
//
//                mediaPlayer!!.reset()
//
//                // 添加短暂的延迟，例如100毫秒
//                Handler().postDelayed({
//                    // 重新初始化并设置要播放的媒体资源
//                    mediaPlayer!!.start()
//                }, 100)
//
//            }
//
//
//        }
//    }
    var lastClickTime = 0L

    /**
     * 播放提示音
     */
    fun playMedia() {
        val currentTime = System.currentTimeMillis()

        if(!isPhoneSilent(appContext)){

            if (lastClickTime != 0L && (currentTime -  lastClickTime > 500)) {
                if (mediaPlayer != null) {
                    // 检查播放器状态
                    if(mediaPlayer!!.isPlaying){
                        // 如果正在播放，先停止播放
                        mediaPlayer!!.stop()
                    }
                    // 重置 MediaPlayer 对象
                    mediaPlayer!!.reset()

                    try {
                        // 设置要播放的媒体资源
                        mediaPlayer!!.setDataSource(appContext, getResourceUri(appContext,R.raw.sx_common_click))
                        // 准备MediaPlayer
                        mediaPlayer!!.prepare()
                        // 启动播放
                        mediaPlayer!!.start()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

        }


        lastClickTime = currentTime


    }
    // 获取资源文件的URI
    private fun getResourceUri(context: Context, resId: Int): Uri{
        return Uri.parse("android.resource://" + context.packageName + "/" + resId)
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