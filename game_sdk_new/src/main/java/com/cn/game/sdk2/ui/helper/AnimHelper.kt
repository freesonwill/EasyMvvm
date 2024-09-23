package com.cn.game.sdk2.ui.helper

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.cn.game.sdk2.utils.ext.CommonExt.formatRealMoney
import com.xcjh.base_lib2.utils.StringFormatUtil.Companion.decimalFormat2
import com.xcjh.base_lib2.utils.StringFormatUtil.Companion.decimalFormatMax2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigDecimal

object AnimHelper {
    @SuppressLint("SetTextI18n")
    fun doNumberAnim(targetView: TextView, startNum: Long, endNumber: Long, duration1: Long = 500) {
        if (endNumber % 100 == 0L) {
            val realNumber: Long = endNumber / 100L
            ValueAnimator.ofFloat(startNum / 100F, realNumber.toFloat()).apply {
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

    fun doScaleAnimRecovery(
        targetView: View,
        startScaleX: Float = 1.0f,
        targetScaleX: Float = 1.3f,
        startScaleY: Float = 1.0f,
        targetScaleY: Float = 1.3f,
        duration: Long = 200
    ) {
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            targetView,
            PropertyValuesHolder.ofFloat(View.SCALE_X, startScaleX, targetScaleX, startScaleX),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, startScaleY, targetScaleY, startScaleY)
        )
        animator.duration = duration
        animator.start()
    }

    /**
     *  執行客制ViewPager轉場動畫
     *
     *  @param targetPosition 欲轉跳的頁面Index
     *  @param viewPager ViewPager
     *  @param fakeViewPager 用來顯示前一個Fragment的View
     */
    fun doDirectViewPagerAnim(
        targetPosition: Int,
        viewPager: ViewPager2,
        fakeViewPager: ImageView
    ): Job? {
        val prevPosition = viewPager.currentItem
        if(prevPosition == targetPosition) return null

        return CoroutineScope(Dispatchers.Main).launch {
            val isPrev = prevPosition < targetPosition
            val width = viewPager.width

            // 將ViewPager截圖並設置給FakeViewPager
            fakeViewPager.apply {
                setImageBitmap(viewPager.drawToBitmap())
                isVisible = true
            }

            // 將ViewPager移動到指定位置
            viewPager.setCurrentItem(targetPosition, false)

            // 構建動畫
            val animationList =
                listOf(
                    Triple(viewPager, if (isPrev) width * 1f else width * -1f, 0f),
                    Triple(fakeViewPager, 0f, if (isPrev) width * -1f else width * 1f)
                ).map {
                    ObjectAnimator.ofFloat(it.first, "translationX", it.second, it.third)
                }

            // 執行動畫
            AnimatorSet().apply {
                playTogether(animationList)
                addListener(
                    onStart = {
                        fakeViewPager.translationX = 0f
                        viewPager.translationX =
                            if(isPrev) width * -1f
                            else width * 1f
                    },
                    onEnd = {
                        fakeViewPager.apply {
                            isVisible = false
                            setImageResource(android.R.color.transparent)
                        }
                    }
                )
                start()
            }
        }
    }

}