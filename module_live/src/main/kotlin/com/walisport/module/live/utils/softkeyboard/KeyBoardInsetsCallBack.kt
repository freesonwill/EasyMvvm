package com.walisport.module.live.utils.softkeyboard

import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsAnimationCompat.BoundsCompat
import androidx.core.view.WindowInsetsCompat

class KeyBoardInsetsCallBack(dispatchMode: Int, private val keyboardListener: KeyBoardListener) :
    RootViewDeferringInsetsCallback(dispatchMode) {
    constructor(keyboardListener: KeyBoardListener) : this(DISPATCH_MODE_STOP, keyboardListener)

    override fun onPrepare(animation: WindowInsetsAnimationCompat) {
    }

    override fun onStart(
        animation: WindowInsetsAnimationCompat,
        bounds: BoundsCompat
    ): BoundsCompat {
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

    companion object {
        val KEYBOARD_TYPE: Int = WindowInsetsCompat.Type.ime()
        val SYSTEM_BAR_TYPE: Int = WindowInsetsCompat.Type.systemBars()
    }
}

