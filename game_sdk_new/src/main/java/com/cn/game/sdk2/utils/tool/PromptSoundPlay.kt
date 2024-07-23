package com.cn.game.sdk2.utils.tool

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.SparseArray
import com.cn.game.sdk2.R
import com.cn.game.sdk2.utils.ThreadUtils
import com.cn.game.sdk2.websocket.isEnableSound
import com.xcjh.base_lib2.ModuleInitializer
import com.xcjh.base_lib2.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * 提示音 Gold
 */
object PromptSoundPlay {
    private val soundPool by lazy { SoundPool.Builder()
        .setMaxStreams(5)
        .build() }
    private val soundPoolIds = SparseArray<Int>()
    private var lastClickTime: Long = 0
    private val debounceInterval: Long = 200 // 防抖间隔
    private var isWaitingForClear = false
    private const val TAG = "PromptSoundPlay"
    /******************************* Method *******************************************/

    /**
     * 金币提示音  要关闭前一个
     */
    fun goldPlayMedia() {
        playSound(R.raw.jinbi_ying)
    }



    /**
     * 金币提示音~~可以一直提示
     */
    fun playGoldCoinAudio() {
        playSound(R.raw.jinbi_ying)
    }

    /**
     * 开始游戏声音
     */
    fun startGameTip() {
        playSound(R.raw.sx_common_start)
    }

    /**
     * 还有五秒快要结束的时候
     */
    fun countdownGameTip() {
        playSound(R.raw.sx_common_countdown)
    }

    /**
     * 结束语音
     */
    fun endGameTip() {
        playSound(R.raw.sx_common_stop)
    }

    /**
     * 按钮提示音
     */
    fun btnPlayMedia() {
        playSound(R.raw.btn_ying_click)
    }

    /**
     * 播放胜利音效
     */
    fun playWinEffect(){
        playSound(R.raw.sx_common_win_bet,R.raw.sx_common_win_game)
    }

    /**
     * 同时播放音效：soundIds
     */
    private fun playSound(vararg soundRawIds:Int){
        if(!isEnableSound) {
            LogUtils.e("isEnableSound false, ignore playSound:${soundRawIds}")
            return
        }
        if(isPhoneSilent(ModuleInitializer.application)) {
            LogUtils.e("isPhoneSilent true, ignore playSound:${soundRawIds}")
            return
        }
        ThreadUtils.mainScope.launch(Dispatchers.Main) {
            val (volume, maxVolume, percent) = systemVolume
            //load sounds
            val soundIds = loadSound(soundRawIds.toList())
            //play sounds
            playSoundOnly(soundIds,percent, percent, 0, 0, 1f)
        }
    }

    /**
     * 播放音效
     * @musicIds  音乐id
     * @parallel  是否同时播放
     */
    private suspend fun playMusic(musicIds:List<Int>,parallel:Boolean = true){
        TODO("play music")
    }

    /**
     * 同时播放音效：soundIds（仅仅播放）
     * @param soundIds 音效id
     * @param leftVolume
     * @param rightVolume
     * @param priority
     * @param rate
     * @param parallel 是否同时播放
     */
    private suspend fun playSoundOnly(soundIds:List<Int>, leftVolume:Float, rightVolume:Float, priority:Int, loop:Int, rate:Float,parallel:Boolean=true){
        coroutineScope {
            soundIds.forEach {
                if(parallel){
                    async {
                        soundPool.stop(it)
                        soundPool.play(it, leftVolume, rightVolume, priority, loop, rate)
                    }
                } else {
                    soundPool.stop(it)
                    soundPool.play(it, leftVolume, rightVolume, priority, loop, rate)
                }
            }
        }
    }

    /**
     * 加载音频
     */
    private suspend fun loadSound(rawIds:List<Int>):List<Int>{
        val rst = suspendCoroutine { continuation->
            var count1 = 0;var count2 = 0
            val context = ModuleInitializer.application
            //LogUtils.dTag(TAG, "loadSound:$rawIds,rawIds.size:${rawIds.size}")
            rawIds.forEachIndexed { _, item->
                if(soundPoolIds[item] != null) return@forEachIndexed //相当于continue
                val id = soundPool.load(context, item, 1)
                soundPoolIds[item] = id
                count1++
            }
            //LogUtils.dTag(TAG, "loadSound:$rawIds,count:$count1")
            //no meed load
            if(count1 == 0) return@suspendCoroutine continuation.resume(rawIds.map { soundPoolIds[it]})
            soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
                count2++
                LogUtils.dTag(TAG, "loadSound:complete,sampleId:$sampleId,status:$status,count1:$count1,count2:$count2")
                if(count2 == count1) continuation.resume(rawIds.map { soundPoolIds[it]})
            }
        }
        return rst
    }

    // 获取资源文件的URI
    private fun getResourceUri(context: Context, resId: Int): Uri {
        return Uri.parse("android.resource://" + context.packageName + "/" + resId)
    }



    /** < volume,maxVolume,volume/maxVolume > */
    private val systemVolume:Triple<Int,Int,Float> get() = run{
        val context = ModuleInitializer.application
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        Triple(volume, maxVolume,1f*volume/maxVolume)
    }

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
            AudioManager.RINGER_MODE_SILENT,
            AudioManager.RINGER_MODE_VIBRATE -> true // 静音或振动模式
            else -> false // 声音模式
        }
    }
}