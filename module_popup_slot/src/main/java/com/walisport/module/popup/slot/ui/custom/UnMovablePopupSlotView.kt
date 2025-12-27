package com.walisport.module.popup.slot.ui.custom

import android.animation.AnimatorSet
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.walisport.module.popup.slot.databinding.LayoutPopupSlotBinding

class UnMovablePopupSlotView : LinearLayout  {

    private var downRawX = 0f
    private var downRawY = 0f
    private var dX = 0f
    private var dY = 0f
    var binding: LayoutPopupSlotBinding
    private var performClick: (() -> Unit)? = null
    private var positionCallbacks: ((Float , Float) -> Unit)? = null
    private var mAnimator: AnimatorSet? = null
    private var currentCount = 1

    constructor(context: Context) : super(context)
    constructor(context: Context , attrs: AttributeSet) : super(context , attrs)
    constructor(context: Context , attrs: AttributeSet , defStyleAttr: Int) : super(
        context ,
        attrs ,
        defStyleAttr
    )

    init {
        binding = LayoutPopupSlotBinding.inflate(
            LayoutInflater.from(context) ,
            this ,
            true
        )
    }




    override fun performClick(): Boolean {
        playBounceAnimation()
        performClick?.invoke()
        super.performClick()
        return true
    }

    fun setPerformClick(performClick: () -> Unit) {
        this.performClick = performClick
    }


    private fun playBounceAnimation() {
        this.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(75)
            .withEndAction {
                this.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(75)
                    .start()
            }
            .start()
    }


    private fun playZoomOutAnimation() {
        if (binding.root.scaleX == 0.9f && binding.root.scaleY == 0.9f) return
        binding.root.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .start()
    }

    private fun playZoomInAnimation() {
        if (binding.root.scaleX == 1f && binding.root.scaleY == 1f) return
        binding.root.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(100)
            .start()
    }

    fun setPositionCallbacks(callback: (Float, Float) -> Unit) {
        this.positionCallbacks = callback
    }
}