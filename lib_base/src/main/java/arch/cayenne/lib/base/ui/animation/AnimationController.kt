package arch.cayenne.lib.base.ui.animation

import android.view.animation.Animation
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @date: 2025/8/18 19:31
 * @description: 动画全局控制器
 */
object AnimationController {
    /***** 路由默认动画 *****/
    var routeEnterAnimFlow: MutableStateFlow<IAnimationOption?> = MutableStateFlow(null)
    val routeEnterAnim: IAnimationOption?
        get() = routeEnterAnimFlow.value

    var routeExiAnimFlow: MutableStateFlow<IAnimationOption?> = MutableStateFlow(null)
    val routeExitAnim: IAnimationOption?
        get() = routeExiAnimFlow.value


    var routePopEnterAnimFlow: MutableStateFlow<IAnimationOption?> = MutableStateFlow(null)
    val routePopEnterAnim: IAnimationOption?
        get() = routePopEnterAnimFlow.value

    var routePopExitAnimFlow: MutableStateFlow<IAnimationOption?> = MutableStateFlow(null)
    val routePopExitAnim: IAnimationOption?
        get() = routePopExitAnimFlow.value

    /***** 路由上下动画 *****/
    var routeEnterAnimTBFlow: MutableStateFlow<IAnimationOption?> = MutableStateFlow(null)
    val routeEnterAnimTB: IAnimationOption?
        get() = routeEnterAnimTBFlow.value

    var routeExitAnimTBFlow: MutableStateFlow<IAnimationOption?> = MutableStateFlow(null)
    val routeExitAnimTB: IAnimationOption?
        get() = routeExitAnimTBFlow.value


    var routePopEnterAnimTBFlow: MutableStateFlow<IAnimationOption?> = MutableStateFlow(null)
    val routePopEnterAnimTB: IAnimationOption?
        get() = routePopEnterAnimTBFlow.value

    var routePopExitAnimTBFlow: MutableStateFlow<IAnimationOption?> = MutableStateFlow(null)
    val routePopExitAnimTB: IAnimationOption?
        get() = routePopExitAnimTBFlow.value

    /****** popup动画 ****/
    private var popupEnterAnimFlow: MutableStateFlow<IAnimationOption?> = MutableStateFlow(null)
    val popupEnterAnim: IAnimationOption?
        get() = popupEnterAnimFlow.value


    private var popupExitAnimFlow: MutableStateFlow<IAnimationOption?> = MutableStateFlow(null)
    val popupExitAnim: IAnimationOption?
        get() = popupExitAnimFlow.value

    /****** drawer动画 ****/
    private var drawerEnterAnimFlow: MutableStateFlow<IAnimationOption?> = MutableStateFlow(null)
    val drawerEnterAnim: IAnimationOption?
        get() = drawerEnterAnimFlow.value

    /****** scrollBar动画 ****/
    private var scrollBarAnimFlow: MutableStateFlow<IAnimationOption?> = MutableStateFlow(null)
    val scrollBarAnim: IAnimationOption?
        get() = scrollBarAnimFlow.value

    /****** zoomIn动画 ****/
    private var zoomInAnimFlow: MutableStateFlow<IAnimationOption?> = MutableStateFlow(null)
    val zoomInAnim: IAnimationOption?
        get() = zoomInAnimFlow.value

    init {
        setRouteAnim(300, PathInterpolatorOption(0.36f, 0.66f, 0.04f, 1f))
        setZoomInAnim(330, PathInterpolatorOption(0.5f, 1f, 0.89f, 1f))
        setPopupAnim(300, PathInterpolatorOption(0.33f, 1f, 0.5f, 1f))
        setDrawerAnim(300, PathInterpolatorOption(0.36f, 0.66f, 0.04f, 1f))
        setScrollBarAnim(250, PathInterpolatorOption(0f, 0f, 1f, 1f))
    }

    fun setRouteAnim(duration: Long, interpolator: IInterpolatorOption) {
        //从右往左进入
        routeEnterAnimFlow.value = TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            duration,
            interpolator)

        //从左往右退出
        routeExiAnimFlow.value = TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, -0.25f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            duration,
            interpolator,
        )

        //从左往右进入
        routePopEnterAnimFlow.value = TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, -0.25f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            duration,
            interpolator,
        )

        //从右往左退出
        routePopExitAnimFlow.value = TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            duration,
            interpolator,
        )

        //从上往下
        routeEnterAnimTBFlow.value = TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, -1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            duration,
            interpolator,
        )
        routeExitAnimTBFlow.value = null
        routePopEnterAnimTBFlow.value = null
        //从下往上
        routePopExitAnimTBFlow.value = TranslateAnimationOption(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, -1f,
            duration,
            interpolator,
        )
    }

    fun setPopupAnim(duration: Long, interpolator: IInterpolatorOption) {
        popupEnterAnimFlow.value = TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            duration,
            interpolator,
        )

        popupExitAnimFlow.value = TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,
            duration,
            interpolator,
        )
    }

    fun setDrawerAnim(duration: Long, interpolator: IInterpolatorOption) {
        drawerEnterAnimFlow.value = SimpleAnimationOption(duration, interpolator)
    }

    fun setScrollBarAnim(duration: Long, interpolator: IInterpolatorOption) {
        scrollBarAnimFlow.value = SimpleAnimationOption(duration, interpolator)
    }

    fun setZoomInAnim(duration: Long, interpolator: IInterpolatorOption) {
        zoomInAnimFlow.value = SimpleAnimationOption(duration, interpolator)
    }
}