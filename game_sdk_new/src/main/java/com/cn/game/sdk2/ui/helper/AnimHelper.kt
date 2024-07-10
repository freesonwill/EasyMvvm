package com.cn.game.sdk2.ui.helper

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.core.animation.doOnEnd
import com.cn.game.sdk2.utils.ext.CommonExt.formatRealMoney
import com.xcjh.base_lib2.utils.StringFormatUtil.Companion.decimalFormat2
import com.xcjh.base_lib2.utils.StringFormatUtil.Companion.decimalFormatMax2
import java.math.BigDecimal

object AnimHelper {
    @SuppressLint("SetTextI18n")
    fun doNumberAnim(targetView: TextView, startNum: Long, endNumber: Long, duration1: Long = 500) {
        if (endNumber % 100 == 0L) {
            val realNumber: Long = endNumber / 100L
            ValueAnimator.ofFloat(startNum.toFloat(), realNumber.toFloat()).apply {
                duration = duration1
                addUpdateListener {
                    targetView.text = "¥ ${(it.animatedValue as Float).toLong()}"
                }
                doOnEnd { targetView.text = "¥ ${decimalFormatMax2.format(realNumber)}" }
                start()
            }
        } else {
            ValueAnimator.ofFloat(startNum.toFloat(), endNumber.toFloat()).apply {
                duration = duration1
                addUpdateListener {
                    targetView.text = "¥ ${
                        (decimalFormat2.format(
                            BigDecimal((it.animatedValue as Float).toDouble()).divide(
                                BigDecimal(100)
                            )
                        ))
                    }"
                }
                doOnEnd { targetView.text = "¥ ${endNumber.formatRealMoney()}" }
                start()
            }
        }
    }


    private val SCALE_X = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.3f, 1.0f)
    private val SCALE_Y = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.3f, 1.0f)

    fun doScaleAnim(targetView: View) {
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            targetView,
            SCALE_X,
            SCALE_Y
        )
        animator.duration = 200
        animator.start()
    }
}