package arch.cayenne.lib.common.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import arch.cayenne.lib.common.databinding.LayoutMoneyKeyboardBinding

/**
 * 自定义金额控件
 */

class MoneyKeyboardView : LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    val mBinding: LayoutMoneyKeyboardBinding
    private var mListener: OnClickListener? = null

    init {
        val layoutInflater = LayoutInflater.from(context)
        mBinding = LayoutMoneyKeyboardBinding.inflate(layoutInflater, this, true)
        mBinding.apply {
            setNumberTouch(btnZero) { mListener?.onMoneyClick(0) }
            setNumberTouch(btnOne) { mListener?.onMoneyClick(1) }
            setNumberTouch(btnTwo) { mListener?.onMoneyClick(2) }
            setNumberTouch(btnThree) { mListener?.onMoneyClick(3) }
            setNumberTouch(btnFour) { mListener?.onMoneyClick(4) }
            setNumberTouch(btnFive) { mListener?.onMoneyClick(5) }
            setNumberTouch(btnSix) { mListener?.onMoneyClick(6) }
            setNumberTouch(btnSeven) { mListener?.onMoneyClick(7) }
            setNumberTouch(btnEight) { mListener?.onMoneyClick(8) }
            setNumberTouch(btnNine) { mListener?.onMoneyClick(9) }
            setNumberTouch(btnCustomOne) { mListener?.onCustomClick(600) }
            setNumberTouch(btnCustomTwo) { mListener?.onCustomClick(800) }
            setNumberTouch(btnCustomThree) { mListener?.onCustomClick(1000) }
            setNumberTouch(btnCustomFour) { mListener?.onCustomClick(2000) }
            setNumberTouch(btnClear) { mListener?.onClear() }
            setNumberTouch(btnConfirm) { mListener?.onConfirm() }
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

    fun setOnMoneyClickListener(listener: OnClickListener) {
        mListener = listener
    }

    interface OnClickListener {
        fun onMoneyClick(number: Int)
        fun onCustomClick(number: Int)
        fun onClear()
        fun onConfirm()
    }
}