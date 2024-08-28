package com.cn.game.sdk2.websocket.helper

import android.os.CountDownTimer
import com.cn.game.sdk2.utils.ThreadUtils
import com.cn.game.sdk2.utils.ext.CommonExt.isMainThread
import com.xcjh.base_lib2.callback.livedata.UnPeekLiveData
import com.xcjh.base_lib2.utils.LogUtils

/**
 * 倒计时辅助类
 */
class CountDownHelper {
    private val TAG get() = this.javaClass.simpleName

    var countDown: Int = 0 //阶段倒计时
        set(value) {
            field = value - 0 //减去500ms延时
            LogUtils.dTag(TAG, "countDown set:${value},isMainThread:$isMainThread")
            _countDownSetStampTime = System.currentTimeMillis()
            ThreadUtils.runOnUiThread {
                countDownTimer?.cancel()
                countDownTimer = object : CountDownTimer(field.toLong(), 1000L) {
                    override fun onTick(time: Long) {
                        val t = Math.round(time / 1000f)
                        _countDownSecondsLD.value = t
                    }

                    override fun onFinish() {
                        _countDownSecondsLD.value = 0
                    }
                }
                countDownTimer?.start()
            }
        }
        get() {
            val elapsed = System.currentTimeMillis() - _countDownSetStampTime
            LogUtils.dTag(TAG, "countDown elapsed:${elapsed}")
            return (field - elapsed).toInt()
        }
    private var countDownTimer: CountDownTimer? = null
    private val _countDownSecondsLD: UnPeekLiveData<Int> = UnPeekLiveData()
    private var _countDownSetStampTime: Long = 0L

    val countDownSecondsLD: UnPeekLiveData<Int> = _countDownSecondsLD
    val isCountDownStart get() = (System.currentTimeMillis() - _countDownSetStampTime) < 50

    /******************* Method ******************/
    fun stopCountDown(){
        countDownTimer?.cancel()
    }
}