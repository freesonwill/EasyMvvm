package arch.cayenne.lib.base.ui.animation

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import android.animation.ValueAnimator
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType

class CustomCurveTransformer : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        val animator = ValueAnimator.ofFloat(0f, position)
        animator.interpolator =AnimationController[AnimType.popupExit]?.interpolator?.toInterpolator()  // 设置自定义曲线
        animator.duration =  AnimationController[AnimType.popupExit]!!.duration
        animator.start()
    }
}
