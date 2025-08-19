package arch.cayenne.lib.base.ui.animation

import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.PathInterpolator
import android.view.animation.TranslateAnimation
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @date: 2025/8/18 19:31
 * @description: 动画全局控制器
 */
object AnimationController {
    //从右往左进入
    val routeEnterAnim = MutableStateFlow(
        TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply {
            duration = 300
            interpolator = PathInterpolator(0.36f, 0.66f, 0.04f, 1f)
        }
    )

    //从左往右退出
    val routeExiAnim = MutableStateFlow(
        TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, -0.25f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply {
            duration = 300
            interpolator = PathInterpolator(0.36f, 0.66f, 0.04f, 1f)
        }
    )

    //从左往右进入
    val routePopEnterAnim = MutableStateFlow(
        TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -0.25f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply {
            duration = 300
            interpolator = PathInterpolator(0.36f, 0.66f, 0.04f, 1f)
            fillAfter = true // 动画结束后保持最终位置
        }
    )

    //从右往左退出
    val routePopExitAnim = MutableStateFlow(
        TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply {
            duration = 300
            interpolator = PathInterpolator(0.36f, 0.66f, 0.04f, 1f)
            fillAfter = true // 动画结束后保持最终位置
        }
    )

    val popupEnterAnim = MutableStateFlow(
        TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_PARENT, 0f
        ).apply {
            duration = 300
            interpolator = PathInterpolator(0.33f, 1f, 0.5f, 1f)
        }
    )

    val popupExitAnim = MutableStateFlow(
        TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f
        ).apply {
            duration = 300
            interpolator = PathInterpolator(0.33f, 1f, 0.5f, 1f)
        }
    )

    val drawerEnterAnim = MutableStateFlow(
        AnimationSet(false).apply {
            duration = 300
            interpolator = PathInterpolator(0.36f, 0.66f, 0.04f,1f)
        }
    )

}