package com.cn.game.sdk2.utils.ext

import android.os.Looper
import com.cn.game.sdk2.R
import com.cn.game.sdk2.ui.helper.Fast3ToastHelper
import com.cn.game.sdk2.utils.PinyinUtils
import com.cn.game.sdk2.websocket.bean.AreaBetConfigBean
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.constants.BettingState
import com.cn.game.sdk2.websocket.gameAboutModel
import com.xcjh.base_lib2.ModuleInitializer
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat


/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/17 16:52
 **/
object CommonExt {

    /***
     *  無Context狀態下取得String
     */
    fun Int.getString(vararg formatArgs: Any): String {
        return ModuleInitializer.application.getString(this, formatArgs)
    }

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
                return multipliers.joinToString(",", "x[","]", transform = {
                    decimalFormat.format(it)
                })
            }
            return decimalFormat.format(multiplier)
        }
    }

    fun <T> Iterable<T>.some(predicate: (T) -> Boolean): Boolean {
        return find(predicate) != null
    }

    fun <T> Iterable<T>.every(predicate: (T) -> Boolean): Boolean {
        val it = iterator()
        while (it.hasNext()) {
            val item = it.next()
            if (!predicate(item)) return false
        }
        return true
    }

    fun IntArray.every(predicate: (Int) -> Boolean): Boolean {
        val it = iterator()
        while (it.hasNext()) {
            val item = it.next()
            if (!predicate(item)) return false
        }
        return true
    }

    fun IntArray.some(predicate: (Int) -> Boolean): Boolean {
        return find(predicate) != null
    }

    fun Any.formatRealMoney(): String {
        val b1 = BigDecimal(this.toString())
        val b2 = BigDecimal("100")
        return b1.divide(b2, 2, RoundingMode.DOWN).stripTrailingZeros().toPlainString()
    }

    internal fun BettingState.isCanGoOn(
        areaLimit: AreaBetConfigBean?,
        goOnAction: () -> Unit
    ) {
        when (this) {
            BettingState.GO_ON -> {
                goOnAction.invoke()
            }

            BettingState.NO_MONEY_50 -> {
                Fast3ToastHelper.showToastNormal(ModuleInitializer.application.getString(R.string.money_insufficient_50))
            }

            BettingState.NO_MONEY -> {
                if ((gameAboutModel.balance.value ?: 0) < 50) {
                    Fast3ToastHelper.showToastNormal(ModuleInitializer.application.getString(R.string.money_insufficient_50))
                } else {
                    Fast3ToastHelper.showToastNormal(ModuleInitializer.application.getString(R.string.money_insufficient))
                }
            }

            BettingState.OFFSET_MIN -> {
                Fast3ToastHelper.showToastNormal(
                    ModuleInitializer.application.getString(
                        R.string.money_min_error_with_area,
                        areaLimit?.areaCode?.toastStr,
                        "¥${areaLimit?.minLimit?.formatRealMoney()}"
                    )
                )
            }

            BettingState.OFFSET_MAX -> {
                Fast3ToastHelper.showToastNormal(
                    ModuleInitializer.application.getString(
                        R.string.money_max_error_with_area,
                        areaLimit?.areaCode?.toastStr,
                        "¥${areaLimit?.maxLimit?.formatRealMoney()}"
                    )
                )
            }

            BettingState.NO_NETWORK -> {
                Fast3ToastHelper.showToastNormal(ModuleInitializer.application.getString(R.string.money_no_network))
            }
        }
    }
}