package com.walisport.module.bet.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.walisport.module.bet.databinding.LayoutNumberKeyboardBinding

class NumberKeyboardView : LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val mBinding: LayoutNumberKeyboardBinding
    private var mListener: OnCalculatorClickListener? = null

    init {
        val layoutInflater = LayoutInflater.from(context)
        mBinding = LayoutNumberKeyboardBinding.inflate(layoutInflater, this, true)
        mBinding.apply {
            btnZero.setOnClickListener { mListener?.onNumberClick(0) }
            btnOne.setOnClickListener { mListener?.onNumberClick(1) }
            btnTwo.setOnClickListener { mListener?.onNumberClick(2) }
            btnThree.setOnClickListener { mListener?.onNumberClick(3) }
            btnFour.setOnClickListener { mListener?.onNumberClick(4) }
            btnFive.setOnClickListener { mListener?.onNumberClick(5) }
            btnSix.setOnClickListener { mListener?.onNumberClick(6) }
            btnSeven.setOnClickListener { mListener?.onNumberClick(7) }
            btnEight.setOnClickListener { mListener?.onNumberClick(8) }
            btnNine.setOnClickListener { mListener?.onNumberClick(9) }
            btnDot.setOnClickListener { mListener?.onDotClick() }
            btnOther.setOnClickListener { mListener?.onOtherClick() }
        }
    }

    fun setOnCalculatorClickListener(listener: OnCalculatorClickListener) {
        mListener = listener
        mBinding.btnOther.text = listener.getOtherText()
    }

    interface OnCalculatorClickListener {
        fun onNumberClick(number: Int)
        fun onDotClick()
        fun onOtherClick()
        fun getOtherText(): String
    }
}