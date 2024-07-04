package com.cn.game.sdk2.utils.ext

import android.os.Looper
import com.cn.game.sdk2.R
import com.cn.game.sdk2.ui.helper.Fast3ToastHelper
import com.cn.game.sdk2.utils.PinyinUtils
import com.cn.game.sdk2.websocket.bean.AreaBetConfigBean
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.viewmodel.GameAboutModel
import java.math.BigDecimal
import java.math.RoundingMode
import com.xcjh.base_lib.ModuleInitializer
import java.text.DecimalFormat


/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/17 16:52
 **/
object CommonExt {


    inline val Int.dp2px
        get() = run {
            val context = ModuleInitializer.application
            val scale = context.resources.displayMetrics.density
            val dp = this
            (dp * scale + 0.5f).toInt()
        }

    inline val Int.px2dp
        get() = run {
            val context = ModuleInitializer.application
            val scale = context.resources.displayMetrics.density
            val v = this
            (v / scale + 0.5f).toInt()
        }
    inline val Float.dp2px
        get() = run {
            val context = ModuleInitializer.application
            val scale = context.resources.displayMetrics.density
            val dp = this
            (dp * scale + 0.5f).toInt()
        }

    inline val Float.px2dp
        get() = run {
            val context = ModuleInitializer.application
            val scale = context.resources.displayMetrics.density
            val v = this
            (v / scale + 0.5f).toInt()
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
                return multipliers.joinToString(", ", "x[", "]", transform = {
                    decimalFormat.format(it)
                })
            }
            return decimalFormat.format(multiplier)
        }
    }

    fun Any.formatRealMoney(): String {
        val b1 = BigDecimal(this.toString())
        val b2 = BigDecimal("100")
        return b1.divide(b2, 2, RoundingMode.DOWN).stripTrailingZeros().toPlainString()
    }

    fun GameAboutModel.BettingState.isCanGoOn(
        areaLimit: AreaBetConfigBean?,
        areaInfo: Betting? = null,
        goOnAction: () -> Unit
    ) {
        when (this) {
            GameAboutModel.BettingState.GO_ON -> {
                goOnAction.invoke()
            }

            GameAboutModel.BettingState.NO_MONEY_50 ->{
                Fast3ToastHelper.showToastNormal(ModuleInitializer.application.getString(R.string.money_insufficient_50))
            }

            GameAboutModel.BettingState.NO_MONEY -> {
                Fast3ToastHelper.showToastNormal(ModuleInitializer.application.getString(R.string.money_insufficient))
            }

            GameAboutModel.BettingState.OFFSET_MIN -> {
                if (areaInfo == null) {
                    Fast3ToastHelper.showToastNormal(ModuleInitializer.application.getString(R.string.money_min_error))
                } else {
                    Fast3ToastHelper.showToastNormal(
                        ModuleInitializer.application.getString(
                            R.string.money_min_error_with_area,
                            areaInfo.toastStr,
                            "¥${areaLimit?.minLimit?.formatRealMoney()}"
                        )
                    )
                }
            }

            GameAboutModel.BettingState.OFFSET_MAX -> {
                if (areaInfo == null) {
                    Fast3ToastHelper.showToastNormal(
                        ModuleInitializer.application.getString(R.string.money_max_error)
                    )
                } else {
                    Fast3ToastHelper.showToastNormal(
                        ModuleInitializer.application.getString(
                            R.string.money_max_error_with_area,
                            areaInfo.toastStr,
                            "¥${areaLimit?.maxLimit?.formatRealMoney()}"
                        )
                    )
                }
            }
        }
    }
}