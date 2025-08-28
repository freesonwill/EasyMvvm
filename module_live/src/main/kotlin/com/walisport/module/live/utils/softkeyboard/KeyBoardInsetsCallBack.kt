package com.walisport.module.live.utils.softkeyboard

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsAnimationCompat.BoundsCompat
import androidx.core.view.WindowInsetsCompat
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px

class KeyBoardInsetsCallBack(dispatchMode: Int, private val keyboardListener: KeyBoardListener) :
    RootViewDeferringInsetsCallback(dispatchMode) {
    constructor(keyboardListener: KeyBoardListener) : this(DISPATCH_MODE_STOP, keyboardListener)

    private var navigationBarHeight: Int = 0
    private var hasNavigationBar: Boolean = false
    //监听软件的显示隐藏状态
    private var isSoftKeyBoard: Boolean = false

    override fun onPrepare(animation: WindowInsetsAnimationCompat) {

    }

    override fun onStart(
        animation: WindowInsetsAnimationCompat,
        bounds: BoundsCompat
    ): BoundsCompat {
        if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {

            // 计算目标键盘高度（从bounds推断）
          val  targetKeyboardHeight = calculateTargetKeyboardHeight(bounds)
            val moveDistance = calculateMoveDistance(bounds)
            "键盘高度 $targetKeyboardHeight   移动距离 $moveDistance".logd("aaa")
            keyboardListener.onAnimStart(moveDistance)

        }

        keyboardListener.onAnimStart(bounds.upperBound.bottom - bounds.lowerBound.bottom)
        return super.onStart(animation, bounds)
    }

    override fun onProgress(
        insets: WindowInsetsCompat,
        runningAnimations: List<WindowInsetsAnimationCompat>
    ): WindowInsetsCompat {
        val typesInset = insets.getInsets(KEYBOARD_TYPE)
        // Then we get the persistent inset types which are applied as padding during layout
        val otherInset = insets.getInsets(SYSTEM_BAR_TYPE)

        // Now that we subtract the two insets, to calculate the difference. We also coerce
        // the insets to be >= 0, to make sure we don't use negative insets.
        val subtract = Insets.subtract(typesInset, otherInset)
        val diff = Insets.max(subtract, Insets.NONE)
        keyboardListener.onAnimDoing(diff.left - diff.right, diff.top - diff.bottom)
        return insets
    }

    override fun onEnd(animation: WindowInsetsAnimationCompat) {
        keyboardListener.onAnimEnd()
    }

    /**
     * 计算目标键盘高度
     */
    private fun calculateTargetKeyboardHeight(bounds: BoundsCompat): Int {
        val totalHeightChange = bounds.upperBound.bottom - bounds.lowerBound.bottom
        return if (totalHeightChange > 0) {
            // 键盘弹出，高度为正
            if (hasNavigationBar) {
                (totalHeightChange - navigationBarHeight).coerceAtLeast(0)
            } else {
                totalHeightChange
            }
        } else {
            // 键盘收起，高度为0
            0
        }
    }

    /**
     * 计算移动距离
     */
    private fun calculateMoveDistance(bounds: BoundsCompat): Int {
        val rawMoveDistance = bounds.upperBound.bottom - bounds.lowerBound.bottom
        return if (hasNavigationBar) {
            // 减去导航栏高度的影响
            (rawMoveDistance - navigationBarHeight).coerceAtLeast(0)
        } else {
            rawMoveDistance.coerceAtLeast(0)
        }
    }

    /**
     * 计算当前键盘高度
     */
    private fun calculateCurrentKeyboardHeight(insets: WindowInsetsCompat): Int {
        val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())

        return if (imeInsets.bottom > 0) {
            if (hasNavigationBar && imeInsets.bottom >= navigationBarHeight) {
                // 减去导航栏高度
                (imeInsets.bottom - navigationBarHeight).coerceAtLeast(0)
            } else {
                imeInsets.bottom
            }
        } else {
            0
        }
    }

    /**
     * 计算垂直偏移量（用于动画）
     */
    private fun calculateVerticalOffset(insets: WindowInsetsCompat): Int {
        val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

        return if (imeInsets.bottom > 0) {
            // 键盘显示时的偏移量
            -(imeInsets.bottom - systemBars.bottom)
        } else {
            // 键盘隐藏时的偏移量
            systemBars.bottom
        }
    }

    /**
     * 更新系统栏高度信息
     */
    private fun updateSystemBarHeights(insets: WindowInsetsCompat) {
        // 获取导航栏高度
        val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        navigationBarHeight = navBars.bottom
        hasNavigationBar = navigationBarHeight > 0

        // 获取状态栏高度
        val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
     val   statusBarHeight = statusBars.top
    }



    companion object {
        val KEYBOARD_TYPE: Int = WindowInsetsCompat.Type.ime()
        val SYSTEM_BAR_TYPE: Int = WindowInsetsCompat.Type.systemBars()
    }
}

