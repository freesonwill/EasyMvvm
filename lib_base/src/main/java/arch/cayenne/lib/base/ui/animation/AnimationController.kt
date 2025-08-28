package arch.cayenne.lib.base.ui.animation

import android.view.animation.Animation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

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

    operator fun get(key:AnimType) = animMap[key]!!.value
    operator fun set(key:AnimType, animation:IAnimationOption?) {
        animMap[key]!!.value = animation
    }

    /**
     * 获取flow，适合需要监听的情况
     * @param key
     * @return
     */
    fun getFlow(key:AnimType): Flow<IAnimationOption?> {
        return animMap[key]!!.asSharedFlow()
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

