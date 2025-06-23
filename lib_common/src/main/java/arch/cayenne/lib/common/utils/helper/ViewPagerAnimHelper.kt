package arch.cayenne.lib.common.utils.helper

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.common.utils.ext.startSafeAnimateSet
import arch.cayenne.lib.common.utils.ext.startSafeObjectAnimator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.core.animation.addListener
class ViewPagerAnimHelper {

    private var job: Job? = null

    fun doDirectViewPagerAnim(
        targetPosition: Int,
        viewPager: ViewPager2,
        fakeViewPager: ImageView
    ) {
        job?.cancel()
        val prevPosition = viewPager.currentItem
        if (prevPosition == targetPosition) return

        job = CoroutineScope(Dispatchers.Main).launch {
            val isPrev = prevPosition < targetPosition
            val width = viewPager.width

            // 先隱藏 ViewPager，避免閃爍
            viewPager.alpha = 0f

            // 截圖目前畫面顯示的內容
            val snapshot = viewPager.drawToBitmap()
            fakeViewPager.setImageBitmap(snapshot)
            fakeViewPager.translationX = 0f
            fakeViewPager.isVisible = true

            // 直接設置到目標頁面
            viewPager.setCurrentItem(targetPosition, false)

            // 等待下一個 frame 畫面更新完再做動畫
            viewPager.post {
                // 設置初始位置
                fakeViewPager.translationX = 0f

                // 創建動畫
                val fakeAnim = fakeViewPager.startSafeObjectAnimator(
                    "translationX",
                    if (isPrev) -width.toFloat() else width.toFloat()
                )

                // 設置動畫
                viewPager.startSafeAnimateSet({
                    duration = 300L
                    play(fakeAnim)
                    addListener(object : android.animation.AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            // 動畫結束後顯示 ViewPager
                            viewPager.alpha = 1f
                            fakeViewPager.isVisible = false
                            fakeViewPager.setImageDrawable(null)
                            job = null
                        }

                        override fun onAnimationCancel(animation: Animator) {
                            viewPager.alpha = 1f
                            fakeViewPager.isVisible = false
                            fakeViewPager.setImageDrawable(null)
                            job = null
                        }
                    })
                }, start = true)
            }
        }
    }

    /**
     *  ViewPager转场动画
     *  @param targetPosition
     *  @param viewPager ViewPager
     *  @param fakeViewPager
     */
    fun doViewPagerAnim(
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