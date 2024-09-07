package com.cn.game.sdk2.websocket.helper

import com.cn.game.sdk2.utils.ThreadUtils.launchWithCustomContext
import com.cn.game.sdk2.utils.ThreadUtils.mainScope
import com.cn.game.sdk2.utils.ext.CommonExt.isMainThread
import com.xcjh.base_lib2.callback.livedata.UnPeekLiveData
import com.xcjh.base_lib2.utils.LogUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlin.math.round

/**
 * 倒计时辅助类
 */
class CountDownHelper {
    private val TAG get() = this.javaClass.simpleName

    private var countDownJob: Job? = null
    private val _countDownSecondsLD: UnPeekLiveData<Int> = UnPeekLiveData()
    private var _countDownSetStampTime: Long = 0L

    var countDown: Int = 0 //阶段倒计时
        set(value) {
            field = value - 1000 //服务器有延时
            LogUtils.dTag(TAG, "countDown set:${value}-${field},isMainThread:$isMainThread")
            _countDownSetStampTime = System.currentTimeMillis()

            stopCountDown()
            countDownJob = mainScope.launchWithCustomContext(TAG) {
                var remainingTime = field
                while(remainingTime > 0) {
                    _countDownSecondsLD.value = round(remainingTime / 1000f).toInt()
                    delay(1000L)
                    remainingTime -= 1000
                }
                _countDownSecondsLD.value = 0
            }
        }
        get() {
            val elapsed = System.currentTimeMillis() - _countDownSetStampTime
            LogUtils.dTag(TAG, "countDown elapsed:${elapsed}")
            return (field - elapsed).toInt()
        }

    val countDownSecondsLD: UnPeekLiveData<Int> = _countDownSecondsLD
    val isCountDownStart get() = (System.currentTimeMillis() - _countDownSetStampTime) < 50

    /******************* Method ******************/
    fun stopCountDown(){
        countDownJob?.cancel()
    }
}