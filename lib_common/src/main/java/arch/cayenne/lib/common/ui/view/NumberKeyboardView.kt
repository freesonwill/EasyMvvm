package arch.cayenne.lib.common.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import arch.cayenne.lib.common.databinding.LayoutNumberKeyboardBinding

class NumberKeyboardView : LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val mBinding: LayoutNumberKeyboardBinding
    private var mListener: OnCalculatorClickListener? = null

    init {
        val layoutInflater = LayoutInflater.from(context)
        mBinding = LayoutNumberKeyboardBinding.inflate(layoutInflater, this, true)
        mBinding.apply {
            setNumberTouch(btnZero) { mListener?.onNumberClick(0) }
            setNumberTouch(btnOne) { mListener?.onNumberClick(1) }
            setNumberTouch(btnTwo) { mListener?.onNumberClick(2) }
            setNumberTouch(btnThree) { mListener?.onNumberClick(3) }
            setNumberTouch(btnFour) { mListener?.onNumberClick(4) }
            setNumberTouch(btnFive) { mListener?.onNumberClick(5) }
            setNumberTouch(btnSix) { mListener?.onNumberClick(6) }
            setNumberTouch(btnSeven) { mListener?.onNumberClick(7) }
            setNumberTouch(btnEight) { mListener?.onNumberClick(8) }
            setNumberTouch(btnNine) { mListener?.onNumberClick(9) }
            setNumberTouch(btnDot) { mListener?.onDotClick() }
            setNumberTouch(btnOther) { mListener?.onOtherClick() }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setNumberTouch(v: View, actionUp: () -> Unit) {
        v.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    v.isPressed = true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.isPressed = false
                    actionUp.invoke()
                }
            }
            true
        }
    }

    fun setOnCalculatorClickListener(listener: OnCalculatorClickListener) {
        mListener = listener
        mBinding.btnOther.text = listener.getOtherText()
    }

    fun setOtherTextSize(sizeSp: Float) {
        mBinding.btnOther.textSize = sizeSp
    }

    interface OnCalculatorClickListener {
        fun onNumberClick(number: Int)
        fun onDotClick()
        fun onOtherClick()
        fun getOtherText(): String
    }
}