package arch.cayenne.lib.common.utils.ext

import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.OverScroller
import android.widget.Scroller
import androidx.drawerlayout.widget.DrawerLayout

/**
 * @date: 2025/8/11 16:55
 * @description:
 */

fun DrawerLayout.setDrawerInterpolator(
    fixedDuration: Int? = null,
    interpolator: Interpolator? = null
) {
    val draggerFields = listOf("mLeftDragger", "mRightDragger")
    for (fieldName in draggerFields) {
        try {
            val draggerField = DrawerLayout::class.java.getDeclaredField(fieldName).apply {
                isAccessible = true
            }
            val dragger = draggerField.get(this) ?: continue

            val scrollerField = dragger.javaClass.getDeclaredField("mScroller").apply {
                isAccessible = true
            }
            val oldScroller = scrollerField.get(dragger) ?: continue

            // 获取原插值器
            val interpolatorNew = interpolator ?: run {
                try {
                    val interpField = oldScroller.javaClass.getDeclaredField("mInterpolator")
                    interpField.isAccessible = true
                    (interpField.get(oldScroller) as? Interpolator) ?: DecelerateInterpolator()
                } catch (e: Exception) {
                    DecelerateInterpolator()
                }
            }

            // 创建新 Scroller
            val newScroller = when (oldScroller) {
                is OverScroller -> object : OverScroller(context, interpolatorNew) {
                    override fun startScroll(sx: Int, sy: Int, dx: Int, dy: Int, duration: Int) {
                        super.startScroll(sx, sy, dx, dy, fixedDuration ?: duration)
                    }
                }

                is Scroller -> object : Scroller(context, interpolatorNew) {
                    override fun startScroll(
                        startX: Int,
                        startY: Int,
                        dx: Int,
                        dy: Int,
                        duration: Int
                    ) {
                        super.startScroll(startX, startY, dx, dy, fixedDuration ?: duration)
                    }
                }

                else -> null
            } ?: continue

            scrollerField.set(dragger, newScroller)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}