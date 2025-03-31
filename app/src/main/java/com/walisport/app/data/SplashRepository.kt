package com.walisport.app.data

import com.walisport.lib_base.data.repository.BaseRepository
import com.walisport.lib_common.helper.CountDownHelper
import kotlinx.coroutines.flow.SharedFlow

class SplashRepository : BaseRepository() {

    private val countDownHelper = CountDownHelper()

    internal var countDown: Int by countDownHelper::countDown
    internal val countDownSecondsLD: SharedFlow<Int> by countDownHelper::countDownSecondsLD
    internal val isCountDownStart by countDownHelper::isCountDownStart

    init {
        countDown = 5_000
    }

}