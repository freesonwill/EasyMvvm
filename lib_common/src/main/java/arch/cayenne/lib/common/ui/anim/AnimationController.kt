package arch.cayenne.lib.common.ui.anim

import android.view.animation.Animation
import android.view.animation.PathInterpolator
import android.view.animation.TranslateAnimation
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @date: 2025/8/18 19:31
 * @description:
 */
object AnimationController {
    //从右往左进入
    val defaultEnterAnim = MutableStateFlow(
        TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply {
            duration = 240
            interpolator = PathInterpolator(0.4f, 0f, 0.2f, 1f)
        }
    )
    //从左往右退出
    val defaultExiAnim = MutableStateFlow(
        TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, -0.25f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply {
            duration = 240
            interpolator = PathInterpolator(0.4f, 0f, 0.2f, 1f)
        }
    )

    //从左往右进入
    val defaultPopEnterAnim = MutableStateFlow(
        TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -0.25f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply {
            duration = 240
            interpolator = PathInterpolator(0.4f, 0f, 0.2f, 1f)
            fillAfter = true // 动画结束后保持最终位置
        }
    )

    //从右往左退出
    val defaultPopExitAnim = MutableStateFlow(
        TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply {
            duration = 240
            interpolator = PathInterpolator(0.4f, 0f, 0.2f, 1f)
            fillAfter = true // 动画结束后保持最终位置
        }
    )


}