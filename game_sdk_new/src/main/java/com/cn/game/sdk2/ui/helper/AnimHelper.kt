package com.cn.game.sdk2.ui.helper

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.cn.game.sdk2.utils.ext.CommonExt.formatRealMoney
import com.xcjh.base_lib2.utils.StringFormatUtil.Companion.decimalFormat2
import com.xcjh.base_lib2.utils.StringFormatUtil.Companion.decimalFormatMax2
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
     *  @param fragmentManger FragmentManager
     *  @param prevFragment 前一個Fragment
     *  @param viewPager ViewPager
     *  @param fakeViewPager 用來顯示前一個Fragment的View
     */
    fun doDirectViewPagerAnim(
        targetPosition: Int,
        fragmentManger: FragmentManager,
        prevFragment: Fragment,
        viewPager: ViewPager2,
        fakeViewPager: FragmentContainerView
    ) {
        val prevPosition = viewPager.currentItem
        if(prevPosition == targetPosition) return

        val isPrev = prevPosition < targetPosition
        val width = viewPager.width

        // 把要移除畫面的fragment放到fakeViewPager上
        fragmentManger.beginTransaction()
            .replace(fakeViewPager.id, prevFragment)
            .commitNow()
        fakeViewPager.isVisible = true

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
                    fakeViewPager.isVisible = false
                    fragmentManger.beginTransaction()
                        .remove(prevFragment)
                        .commitNow()
                }
            )
            start()
        }
    }

}