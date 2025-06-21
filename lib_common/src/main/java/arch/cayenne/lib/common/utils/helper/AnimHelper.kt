package com.cn.game.sdk2.ui.helper

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.animation.addListener
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object AnimHelper {

    /**
     *  ViewPager转场动画
     *  @param targetPosition
     *  @param viewPager ViewPager
     *  @param fakeViewPager
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
            viewPager.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewPager.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    // 在佈局完成後執行 drawToBitmap
                    val bitmap = viewPager.drawToBitmap()
                    fakeViewPager.apply {
                        post {
                            setImageBitmap(bitmap)
                            isVisible = true
                        }
                    }
                }
            })

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
                this.duration = 100L
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