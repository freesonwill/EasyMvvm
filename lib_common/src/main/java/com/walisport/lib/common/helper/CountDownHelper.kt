package com.walisport.lib.common.helper

import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.base.utils.LogUtilsExt.logd
import com.walisport.lib.common.utils.ThreadUtils.launchWithCustomContext
import com.walisport.lib.common.utils.ThreadUtils.mainScope
import com.walisport.lib.common.utils.ext.CommonExt.isMainThread
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * 倒计时辅助类
 */
class CountDownHelper {
    private val TAG get() = this.javaClass.simpleName

    private var countDownJob: Job? = null
    private val _countDownSecondsLD: MutableSharedFlow<Int> = MutableSharedFlow(replay = 1, extraBufferCapacity = 1)
    private var _countDownSetStampTime: Long = 0L

    var countDown: Int = 0 //阶段倒计时
        set(value) {
            field = value //服务器有延时
            LogUtils.dTag(TAG, "countDown set:${value}-${field},isMainThread:$isMainThread")
            _countDownSetStampTime = System.currentTimeMillis()

            stopCountDown()
            countDownJob = mainScope.launchWithCustomContext(TAG) {
                var remainingTime = field
                var seconds:Int = Math.round(remainingTime / 1000f)
                while(remainingTime > 0) {
                    seconds = Math.round(remainingTime / 1000f)
                    //"emit--->$seconds,remainingTime:$remainingTime,${this@CountDownHelper}".logd(TAG)
                    _countDownSecondsLD.emit(seconds)
                    delay(Math.min(1000L,remainingTime*1L))
                    remainingTime -= 1000
                }
                //"emit--remain->$remainingTime,seconds:$seconds".logd(TAG)
                if(seconds != 0) _countDownSecondsLD.emit(0)
            }
        }
        get() {
            val elapsed = System.currentTimeMillis() - _countDownSetStampTime
            LogUtils.dTag(TAG, "countDown elapsed:${elapsed}")
            return (field - elapsed).toInt()
        }

    val countDownSecondsLD: SharedFlow<Int> = _countDownSecondsLD
    val isCountDownStart get() = (System.currentTimeMillis() - _countDownSetStampTime) < 50

    /******************* Method ******************/
    fun stopCountDown(isManual:Boolean=false){
        if(isManual) "stopCountDown~~~$isManual".logd(TAG)
        countDownJob?.cancel()
    }
}
