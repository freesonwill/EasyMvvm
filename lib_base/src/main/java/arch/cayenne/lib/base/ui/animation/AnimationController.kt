package arch.cayenne.lib.base.ui.animation

import android.view.animation.Animation
import kotlinx.coroutines.flow.MutableStateFlow
/**
 * @date: 2025/8/18 19:31
 * @description: 动画全局控制器
 */
object AnimationController {
    enum class AnimType {
        routeEnter,routeExit,routePopEnter,routePopExit,
        routeEnterTB,routeExitTB,routePopEnterTB,routePopExitTB,
        popupEnter,popupExit,
        drawerEnter,
        scrollbar,
        zoomIn,
    }
    private val animMap = mutableMapOf<AnimType,MutableStateFlow<IAnimationOption?>>().apply {
        AnimType.entries.forEach { put(it, MutableStateFlow(null)) }
    }

    init {
        setRouteAnim(300, PathInterpolatorOption(0.36f, 0.66f, 0.04f, 1f))
        setZoomInAnim(330, PathInterpolatorOption(0.5f, 1f, 0.89f, 1f))
        setPopupAnim(300, PathInterpolatorOption(0.33f, 1f, 0.5f, 1f))
        setDrawerAnim(300, PathInterpolatorOption(0.36f, 0.66f, 0.04f, 1f))
        setScrollBarAnim(250, PathInterpolatorOption(0f, 0f, 1f, 1f))
    }
    operator fun get(key:AnimType) = animMap[key]!!.value
    operator fun set(key:AnimType, animation:IAnimationOption?) {
        animMap[key]!!.value = animation
    }

    fun setRouteAnim(duration: Long, interpolator: IInterpolatorOption) {
        //从右往左进入
        this[AnimType.routeEnter] = TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            duration,
            interpolator)

        //从左往右退出
        this[AnimType.routeExit] = TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, -0.25f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            duration,
            interpolator,
        )

        //从左往右进入
        this[AnimType.routePopEnter] = TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, -0.25f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            duration,
            interpolator,
        )

        //从右往左退出
        this[AnimType.routePopExit] = TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            duration,
            interpolator,
        )

        //从上往下
        this[AnimType.routeEnterTB] = TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, -1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            duration,
            interpolator,
        )
        this[AnimType.routeExitTB] = null
        this[AnimType.routePopEnterTB] = null
        //从下往上
        this[AnimType.routePopExitTB] = TranslateAnimationOption(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, -1f,
            duration,
            interpolator,
        )
    }

    fun setPopupAnim(duration: Long, interpolator: IInterpolatorOption) {
        this[AnimType.popupEnter] = TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            duration,
            interpolator,
        )

        this[AnimType.popupExit] = TranslateAnimationOption(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,
            duration,
            interpolator,
        )
    }

    fun setDrawerAnim(duration: Long, interpolator: IInterpolatorOption) {
        this[AnimType.drawerEnter] = SimpleAnimationOption(duration, interpolator)
    }

    fun setScrollBarAnim(duration: Long, interpolator: IInterpolatorOption) {
        this[AnimType.scrollbar] = SimpleAnimationOption(duration, interpolator)
    }

    fun setZoomInAnim(duration: Long, interpolator: IInterpolatorOption) {
        this[AnimType.zoomIn] = SimpleAnimationOption(duration, interpolator)
    }
}

