package com.walisport.module.live.utils.softkeyboard

import android.content.res.Resources
import android.os.Build
import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsAnimationCompat.BoundsCompat
import androidx.core.view.WindowInsetsCompat
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DimensionExt.px2dp
import kotlin.math.abs

class KeyBoardInsetsCallBack(dispatchMode: Int, private val keyboardListener: KeyBoardListener) :
    RootViewDeferringInsetsCallback(dispatchMode) {
    constructor(keyboardListener: KeyBoardListener) : this(DISPATCH_MODE_STOP, keyboardListener)

    private var navigationBarHeight: Int = 0
    private var hasNavigationBar: Boolean = false

    override fun onPrepare(animation: WindowInsetsAnimationCompat) {

    }


    override fun onStart(
        animation: WindowInsetsAnimationCompat,
        bounds: BoundsCompat
    ): BoundsCompat {
        if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
            // 计算准确的键盘高度（只在onAnimStart中计算）
            val keyboardHeight = calculateKeyboardHeightFromBounds(bounds)
            // 计算移动距离
            val moveDistance = calculateMoveDistanceFromBounds(bounds)
            keyboardListener.onAnimStart(keyboardHeight,animation.durationMillis)
        }
        return super.onStart(animation, bounds)
    }

    override fun onProgress(
        insets: WindowInsetsCompat,
        runningAnimations: List<WindowInsetsAnimationCompat>
    ): WindowInsetsCompat {

        return insets
    }

    override fun onEnd(animation: WindowInsetsAnimationCompat) {
        keyboardListener.onAnimEnd()
    }

    /**
     * 从bounds计算键盘高度
     */
    private fun calculateKeyboardHeightFromBounds(bounds: BoundsCompat): Int {
        val totalHeightChange = abs(bounds.upperBound.bottom - bounds.lowerBound.bottom)

        return if (hasNavigationBar) {
            // 有导航栏时，减去导航栏高度
            (totalHeightChange - navigationBarHeight).coerceAtLeast(0)
        } else {
            // 全面屏设备，直接使用总高度变化
            totalHeightChange
        }
    }

    /**
     * 从bounds计算移动距离
     */
    private fun calculateMoveDistanceFromBounds(bounds: BoundsCompat): Int {
        val rawMoveDistance = bounds.upperBound.bottom - bounds.lowerBound.bottom

        return if (hasNavigationBar) {
            // 有导航栏时，减去导航栏影响
            (rawMoveDistance - navigationBarHeight).coerceAtLeast(0)
        } else {
            // 全面屏设备，直接使用原始距离
            rawMoveDistance.coerceAtLeast(0)
        }
    }


    companion object {
        val KEYBOARD_TYPE: Int = WindowInsetsCompat.Type.ime()
        val SYSTEM_BAR_TYPE: Int = WindowInsetsCompat.Type.systemBars()
    }
}

