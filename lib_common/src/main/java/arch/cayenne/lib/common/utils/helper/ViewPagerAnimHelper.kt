package arch.cayenne.lib.common.utils.helper

import android.animation.Animator
import android.animation.ObjectAnimator
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.common.utils.ext.startSafeAnimateSet
import kotlinx.coroutines.CoroutineScope
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
        val scope: CoroutineScope = viewPager.findViewTreeLifecycleOwner()!!.lifecycleScope
        job = scope.launch {
            val isPrev = prevPosition < targetPosition
            val width = viewPager.width

            // 截圖目前畫面顯示的內容
            val snapshot = viewPager.drawToBitmap()
            fakeViewPager.apply {
                setImageBitmap(snapshot)
                translationX = 0f
                isVisible = true
            }

            viewPager.setCurrentItem(targetPosition, false)
            viewPager.doOnPreDraw {
                val animationList =
                    listOf(
                        Triple(viewPager, if (isPrev) width * 1f else width * -1f, 0f),
                        Triple(fakeViewPager, 0f, if (isPrev) width * -1f else width * 1f)
                    ).map {
                        ObjectAnimator.ofFloat(it.first, "translationX", it.second, it.third)
                    }
                viewPager.startSafeAnimateSet({
                    startDelay = 100L
                    duration = 300L
                    playTogether(animationList)
                    addListener(object : android.animation.AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            fakeViewPager.isVisible = false
                            fakeViewPager.setImageDrawable(null)
                            job = null
                        }

                        override fun onAnimationCancel(animation: Animator) {
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