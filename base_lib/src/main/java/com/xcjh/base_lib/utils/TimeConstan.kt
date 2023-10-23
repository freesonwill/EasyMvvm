package com.xcjh.base_lib.utils

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy


object TimeConstant {
    const val MSEC = 1
    const val SEC = 1000
    const val MIN = 60000
    const val HOUR = 3600000
    const val DAY = 86400000
    const val MONTH = 2592000000L
    const val YEAR = 31104000000L

    @IntDef(*[MSEC, SEC, MIN, HOUR, DAY])
    @Retention(RetentionPolicy.SOURCE)
    annotation class Unit
}