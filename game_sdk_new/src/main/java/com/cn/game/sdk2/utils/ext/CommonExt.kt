package com.cn.game.sdk2.utils.ext

import android.os.Looper
import com.cn.game.sdk2.utils.PinyinUtils
import com.cn.game.sdk2.websocket.bean.Betting
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat


/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/17 16:52
 **/
object CommonExt {
    fun Int.toPinyin(): String {
        return PinyinUtils.toPinyin(this)
    }

    //是否是主线程
    @JvmStatic
    inline val isMainThread: Boolean
        get() {
            return Looper.myLooper() == Looper.getMainLooper()
        }

    //赔率string化
    @JvmStatic
    @JvmOverloads
    fun multiplierStr(betting: Betting, format: String): String {
        betting.apply {
            val decimalFormat = DecimalFormat(format)
            if (multipliers.isNotEmpty()) {
                return multipliers.joinToString(", ", "x[", "]", transform = {
                    decimalFormat.format(it)
                })
            }
            return decimalFormat.format(multiplier)
        }
    }

    fun Int.formatRealMoney(): String {
        val b1 = BigDecimal(this.toString())
        val b2 = BigDecimal("100")
        return b1.divide(b2,2,RoundingMode.DOWN).stripTrailingZeros().toPlainString()
    }
}