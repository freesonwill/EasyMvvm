package arch.cayenne.lib.base.ui.animation

import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.Interpolator
import android.view.animation.PathInterpolator
import android.view.animation.TranslateAnimation
import kotlinx.coroutines.flow.MutableStateFlow

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
        _routeEnterAnim = updateFlow(_routeEnterAnim, TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply { this.duration = duration; this.interpolator = interpolator })

        //从左往右退出
        _routeExiAnim = updateFlow(_routeExiAnim, TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, -0.25f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply { this.duration = duration; this.interpolator = interpolator })

        //从左往右进入
        _routePopEnterAnim = updateFlow(_routePopEnterAnim, TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -0.25f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply { this.duration = duration; this.interpolator = interpolator })

        //从右往左退出
        _routePopExitAnim = updateFlow(_routePopExitAnim, TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply { this.duration = duration; this.interpolator = interpolator })
    }

    fun setPopupAnim(duration: Long, interpolator: Interpolator) {
        _popupEnterAnim = updateFlow(_popupEnterAnim, TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_PARENT, 0f
        ).apply { this.duration = duration; this.interpolator = interpolator })


        _popupExitAnim = updateFlow(_popupExitAnim, TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f
        ).apply { this.duration = duration; this.interpolator = interpolator })

    }

    fun setDrawerAnim(duration: Long, interpolator: Interpolator) {
        _drawerEnterAnim = updateFlow(_drawerEnterAnim, AnimationSet(false)
            .apply { this.duration = duration; this.interpolator = interpolator })
    }

    fun setScrollBarAnim(duration: Long, interpolator: Interpolator) {
        _scrollBarAnim = updateFlow(_scrollBarAnim, AnimationSet(false)
            .apply { this.duration = duration; this.interpolator = interpolator })
    }

    fun setZoomInAnim(duration: Long, interpolator: Interpolator) {
        _zoomInAnim = updateFlow(_zoomInAnim, AnimationSet(false)
            .apply { this.duration = duration; this.interpolator = interpolator })
    }


    private var _routeEnterAnim: MutableStateFlow<Animation>? = null
    val routeEnterAnim: MutableStateFlow<Animation>
        get() = _routeEnterAnim!!

    private var _routeExiAnim: MutableStateFlow<Animation>? = null
    val routeExiAnim: MutableStateFlow<Animation>
        get() = _routeExiAnim!!


    private var _routePopEnterAnim: MutableStateFlow<Animation>? = null
    val routePopEnterAnim: MutableStateFlow<Animation>
        get() = _routePopEnterAnim!!

    private var _routePopExitAnim: MutableStateFlow<Animation>? = null
    val routePopExitAnim: MutableStateFlow<Animation>
        get() = _routePopExitAnim!!


    private var _popupEnterAnim: MutableStateFlow<Animation>? = null
    val popupEnterAnim: MutableStateFlow<Animation>
        get() = _popupEnterAnim!!


    private var _popupExitAnim: MutableStateFlow<Animation>? = null
    val popupExitAnim: MutableStateFlow<Animation>
        get() = _popupExitAnim!!


    private var _drawerEnterAnim: MutableStateFlow<Animation>? = null
    val drawerEnterAnim: MutableStateFlow<Animation>
        get() = _drawerEnterAnim!!

    private var _scrollBarAnim: MutableStateFlow<Animation>? = null
    val scrollBarAnim: MutableStateFlow<Animation>
        get() = _scrollBarAnim!!

    private var _zoomInAnim: MutableStateFlow<Animation>? = null
    val zoomInAnim: MutableStateFlow<Animation> get() =_zoomInAnim!!


    private fun <T : Animation> updateFlow(
        flow: MutableStateFlow<Animation>?,
        animation: T
    ): MutableStateFlow<Animation> {
        return if (flow == null) {
            MutableStateFlow(animation)
        } else {
            flow.value = animation
            flow
        }
    }
}