package arch.cayenne.lib.common.utils.helper

import android.animation.Animator
import android.widget.ImageView
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.common.utils.ext.startSafeAnimateSet
import arch.cayenne.lib.common.utils.ext.startSafeObjectAnimator

class ViewPagerAnimHelper {

    private var isAnimating = false

    fun doDirectViewPagerAnim(
        targetPosition: Int,
        viewPager: ViewPager2,
        fakeViewPager: ImageView
    ) {
        val prevPosition = viewPager.currentItem
        if (prevPosition == targetPosition || isAnimating) return

        isAnimating = true

        val isPrev = prevPosition < targetPosition
        val width = viewPager.width

        // 截圖目前畫面顯示的內容
        val snapshot = viewPager.drawToBitmap()
        fakeViewPager.setImageBitmap(snapshot)
        fakeViewPager.translationX = 0f
        fakeViewPager.isVisible = true

        // 預先把 ViewPager 移到目標頁面（不動畫）
        viewPager.setCurrentItem(targetPosition, false)

        // 等待下一個 frame 畫面更新完再做動畫
        viewPager.post {
            viewPager.translationX = if (isPrev) width.toFloat() else -width.toFloat()
            val vpAnim = viewPager.startSafeObjectAnimator("translationX",
                0f)
            val fakeAnim = fakeViewPager.startSafeObjectAnimator("translationX",
                0f,
                if (isPrev) -width.toFloat() else width.toFloat())
            viewPager.startSafeAnimateSet({
                duration = 300L
                playTogether(vpAnim, fakeAnim)
                addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationCancel(animation: Animator) {
                        super.onAnimationCancel(animation)
                        isAnimating = false
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        fakeViewPager.isVisible = false
                        fakeViewPager.setImageDrawable(null)
                        isAnimating = false
                    }
                }) },
                start = true)
        }
    }
}