package arch.cayenne.lib.base.ui.animation

import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.Interpolator
import android.view.animation.PathInterpolator
import android.view.animation.TranslateAnimation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KMutableProperty0

/**
 * @date: 2025/8/18 19:31
 * @description: 动画全局控制器
 */
object AnimationController {

    init {
        setRouteAnim(300, PathInterpolator(0.36f, 0.66f, 0.04f, 1f))
        setZoomInAnim(330, PathInterpolator(0.5f, 1f, 0.89f, 1f))
        setPopupAnim(300, PathInterpolator(0.33f, 1f, 0.5f, 1f))
        setDrawerAnim(300, PathInterpolator(0.36f, 0.66f, 0.04f, 1f))
        setScrollBarAnim(250, PathInterpolator(0f, 0f, 1f, 1f))
    }

    fun setRouteAnim(duration: Long, interpolator: Interpolator) {
        //从右往左进入
        updateFlow(::routeEnterAnim, TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply { this.duration = duration; this.interpolator = interpolator })

        //从左往右退出
        updateFlow(::routeExiAnim, TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, -0.25f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply { this.duration = duration; this.interpolator = interpolator })

        //从左往右进入
        updateFlow(::routePopEnterAnim, TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -0.25f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply { this.duration = duration; this.interpolator = interpolator })

        //从右往左退出
        updateFlow(::routePopExitAnim, TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply { this.duration = duration; this.interpolator = interpolator })
    }

    fun setPopupAnim(duration: Long, interpolator: Interpolator) {
        updateFlow(::popupEnterAnim, TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_PARENT, 0f
        ).apply { this.duration = duration; this.interpolator = interpolator })


        updateFlow(::popupExitAnim, TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f
        ).apply { this.duration = duration; this.interpolator = interpolator })

    }

    fun setDrawerAnim(duration: Long, interpolator: Interpolator) {
        updateFlow(::drawerEnterAnim, AnimationSet(false)
            .apply { this.duration = duration; this.interpolator = interpolator })
    }

    fun setScrollBarAnim(duration: Long, interpolator: Interpolator) {
        updateFlow(::scrollBarAnim, AnimationSet(false)
            .apply { this.duration = duration; this.interpolator = interpolator })
    }

    fun setZoomInAnim(duration: Long, interpolator: Interpolator) {
        updateFlow(::zoomInAnim, AnimationSet(false)
            .apply { this.duration = duration; this.interpolator = interpolator })
    }


    lateinit var routeEnterAnim: MutableStateFlow<Animation>
        private set


    lateinit var routeExiAnim: MutableStateFlow<Animation>
        private set


    lateinit var routePopEnterAnim: MutableStateFlow<Animation>
        private set

    lateinit var routePopExitAnim: MutableStateFlow<Animation>
        private set

    lateinit var popupEnterAnim: MutableStateFlow<Animation>
        private set

    lateinit var popupExitAnim: MutableStateFlow<Animation>
        private set

    lateinit var drawerEnterAnim: MutableStateFlow<Animation>
        private set

    lateinit var scrollBarAnim: MutableStateFlow<Animation>
        private set
    lateinit var zoomInAnim: MutableStateFlow<Animation>
        private set

    private fun <T : Animation> updateFlow(
        flowRef: KMutableProperty0<MutableStateFlow<T>>,
        animation: T
    ) {
        // 这里必须确保 flowRef 是 lateinit var 的引用
        if ((flowRef as KMutableProperty0<*>).isLateinit) {
            flowRef.set(MutableStateFlow(animation))
        } else {
            flowRef.get().value = animation
        }
    }
}