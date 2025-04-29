package com.walisport.module.live.utils

import java.math.RoundingMode

object LiveBetSlipUtils {
     fun calculateMaxWin(value1: String, value2: String): Double {
        val d1 = value1.toBigDecimalOrNull()
        val d2 = value2.toBigDecimalOrNull()
        var value = 0.0
        if (d1 != null && d2 != null) {
            value = d1.multiply(d2).setScale(2, RoundingMode.HALF_UP).toDouble()
        }
        return value
    }

     fun winOrLoseAmount(value1: String, value2: String): Double {
        val d1 = value1.toDoubleOrNull()
        val d2 = value2.toDoubleOrNull()
        if (d1 != null && d2 != null) {
            return d1 - d2
        }
        return 0.0;
    }

}