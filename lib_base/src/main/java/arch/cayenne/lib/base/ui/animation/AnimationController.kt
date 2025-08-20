package arch.cayenne.lib.base.ui.animation

import android.view.animation.Animation
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @date: 2025/8/18 19:31
 * @description: 动画全局控制器
 */
object AnimationController {

    init {
        setRouteAnim(300, PathInterpolatorOption(0.36f, 0.66f, 0.04f, 1f))
        setZoomInAnim(330, PathInterpolatorOption(0.5f, 1f, 0.89f, 1f))
        setPopupAnim(300, PathInterpolatorOption(0.33f, 1f, 0.5f, 1f))
        setDrawerAnim(300, PathInterpolatorOption(0.36f, 0.66f, 0.04f, 1f))
        setScrollBarAnim(250, PathInterpolatorOption(0f, 0f, 1f, 1f))
    }

    fun setRouteAnim(duration: Long, interpolator: IInterpolatorOption) {
        //从右往左进入
        routeEnterAnimFlow = updateFlow(routeEnterAnimFlow, TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            duration,
            interpolator)
        )

        //从左往右退出
        routeExiAnimFlow = updateFlow(routeExiAnimFlow, TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, -0.25f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            duration,
            interpolator,
        ))

        //从左往右进入
        routePopEnterAnimFlow = updateFlow(routePopEnterAnimFlow, TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, -0.25f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            duration,
            interpolator,
        ))

        //从右往左退出
        routePopExitAnimFlow = updateFlow(routePopExitAnimFlow, TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            duration,
            interpolator,
        ))
    }

    fun setPopupAnim(duration: Long, interpolator: IInterpolatorOption) {
        popupEnterAnimFlow = updateFlow(popupEnterAnimFlow, TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            duration,
            interpolator,
        ))

        popupExitAnimFlow = updateFlow(popupExitAnimFlow, TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,
            duration,
            interpolator,
        ))
    }

    fun setDrawerAnim(duration: Long, interpolator: IInterpolatorOption) {
        drawerEnterAnimFlow = updateFlow(drawerEnterAnimFlow, SimpleAnimationOption(duration, interpolator))
    }

    fun setScrollBarAnim(duration: Long, interpolator: IInterpolatorOption) {
        scrollBarAnimFlow = updateFlow(scrollBarAnimFlow, SimpleAnimationOption(duration, interpolator))
    }

    fun setZoomInAnim(duration: Long, interpolator: IInterpolatorOption) {
        zoomInAnimFlow = updateFlow(zoomInAnimFlow, SimpleAnimationOption(duration, interpolator))
    }

    var routeEnterAnimFlow: MutableStateFlow<TranslateAnimationOption>? = null;private set
    val routeEnterAnim: TranslateAnimationOption
        get() = routeEnterAnimFlow!!.value

    var routeExiAnimFlow: MutableStateFlow<TranslateAnimationOption>? = null;private set
    val routeExiAnim: TranslateAnimationOption
        get() = routeExiAnimFlow!!.value


    var routePopEnterAnimFlow: MutableStateFlow<TranslateAnimationOption>? = null;private set
    val routePopEnterAnim: TranslateAnimationOption
        get() = routePopEnterAnimFlow!!.value

    var routePopExitAnimFlow: MutableStateFlow<TranslateAnimationOption>? = null;private set
    val routePopExitAnim: TranslateAnimationOption
        get() = routePopExitAnimFlow!!.value


    private var popupEnterAnimFlow: MutableStateFlow<TranslateAnimationOption>? = null
    val popupEnterAnim: TranslateAnimationOption
        get() = popupEnterAnimFlow!!.value


    private var popupExitAnimFlow: MutableStateFlow<TranslateAnimationOption>? = null
    val popupExitAnim: TranslateAnimationOption
        get() = popupExitAnimFlow!!.value


    private var drawerEnterAnimFlow: MutableStateFlow<SimpleAnimationOption>? = null
    val drawerEnterAnim: SimpleAnimationOption
        get() = drawerEnterAnimFlow!!.value

    private var scrollBarAnimFlow: MutableStateFlow<SimpleAnimationOption>? = null
    val scrollBarAnim: SimpleAnimationOption
        get() = scrollBarAnimFlow!!.value

    private var zoomInAnimFlow: MutableStateFlow<SimpleAnimationOption>? = null
    val zoomInAnim: SimpleAnimationOption get() = zoomInAnimFlow!!.value


    private fun <T : IAnimationOption> updateFlow(
        flow: MutableStateFlow<T>?,
        animation: T
    ): MutableStateFlow<T> {
        return if (flow == null) {
            MutableStateFlow(animation)
        } else {
            flow.value = animation
            flow
        }
    }
}