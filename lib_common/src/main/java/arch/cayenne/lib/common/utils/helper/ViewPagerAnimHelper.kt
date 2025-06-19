package arch.cayenne.lib.common.utils.helper

import android.animation.Animator
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
}