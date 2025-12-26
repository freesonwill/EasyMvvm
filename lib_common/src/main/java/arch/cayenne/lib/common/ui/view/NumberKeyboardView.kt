package arch.cayenne.lib.common.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.doOnAttach
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.databinding.LayoutNumberKeyboardBinding

class NumberKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val TAG = NumberKeyboardView::class.java.simpleName
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

        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int = 0) {
        if (attrs == null) return
        val a = context.obtainStyledAttributes(attrs, R.styleable.NumberKeyboardView, defStyleAttr, 0)
        try {
            val spacing = a.getDimensionPixelSize(R.styleable.NumberKeyboardView_android_spacing, Int.MAX_VALUE)
            val h = a.getDimensionPixelSize(R.styleable.NumberKeyboardView_android_horizontalSpacing, Int.MAX_VALUE)
            val v = a.getDimensionPixelSize(R.styleable.NumberKeyboardView_android_verticalSpacing, Int.MAX_VALUE)
            val spacingX = if (h != Int.MAX_VALUE) h else spacing
            val spacingY = if (v != Int.MAX_VALUE) v else spacing
            applyItemSpacing(spacingX,spacingY)
        } finally {
            a.recycle()
        }

    }

    private fun applyItemSpacing(spacingX: Int, spacingY: Int) {
        //"applyItemSpacing----$spacingX,spacingY:$spacingY".logd(TAG)
        if(spacingX == Int.MAX_VALUE || spacingY == Int.MAX_VALUE) return
        val halfH = spacingX / 2
        val halfV = spacingY / 2
        doOnAttach {
            val grid = children.first() as GridLayout
            val rowCount = grid.rowCount
            val colCount = grid.columnCount

            grid.children.forEachIndexed { index, child ->
                val lp = (child.layoutParams as? MarginLayoutParams) ?: MarginLayoutParams(child.layoutParams)

                val col = index % colCount
                val row = index / colCount

                lp.leftMargin = if (col == 0) 0 else halfH
                lp.rightMargin = if (col == colCount - 1) 0 else halfH
                lp.topMargin = if (row == 0) 0 else halfV
                lp.bottomMargin = if (row == rowCount - 1) 0 else halfV

                child.layoutParams = lp
            }
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