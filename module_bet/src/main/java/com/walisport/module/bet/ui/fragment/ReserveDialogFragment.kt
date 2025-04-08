package com.walisport.module.bet.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.walisport.lib.base.ui.interface_.IView
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.bet.databinding.FragmentReserveDialogBinding
import com.walisport.module.bet.ui.custom.NumberKeyboardView
import com.walisport.module.bet.viewmodel.ReserveDialogViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReserveDialogFragment private constructor() : DialogFragment(), IView {

    companion object {
        private const val POSITION_X = "positionX"
        private const val POSITION_Y = "positionY"
        private const val RATE_NUMBER = "rateNumber"

        fun newInstance(positionX: Int?, positionY: Int?, rateNumber: Float): ReserveDialogFragment {
            val b = Bundle()
            positionX?.let {
                b.putInt(POSITION_X, it)
            }
            positionY?.let {
                b.putInt(POSITION_Y, it)
            }
            b.putFloat(RATE_NUMBER, rateNumber)
            return ReserveDialogFragment().apply {
                arguments = b
            }
        }
    }

    private val mBinding: FragmentReserveDialogBinding by viewBind()
    private val mViewModel: ReserveDialogViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView(savedInstanceState)
        initListener()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createObserver()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val positionX = requireArguments().getInt(POSITION_X, -1)
            val positionY = requireArguments().getInt(POSITION_Y, -1)

            if (positionX != -1 && positionY != -1) {
                mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        val layoutParams = it.attributes
                        layoutParams.gravity = Gravity.TOP or Gravity.START

                        val triangleLocation = IntArray(2)
                        mBinding.triangle.getLocationInWindow(triangleLocation)

                        val px = triangleLocation.first() + mBinding.triangle.width / 2
                        layoutParams.x = positionX - px
                        layoutParams.y = positionY - (mBinding.triangle.height * 1.8).toInt()

                        it.attributes = layoutParams
                    }
                })
            }
        }
    }

    fun show(manager: FragmentManager) {
        val f = manager.findFragmentByTag(this::class.java.simpleName)
        if (f == null || !f.isAdded) {
            super.show(manager, this::class.java.simpleName)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.etRate.requestFocus()

        val rate = requireArguments().getFloat(RATE_NUMBER)
        mViewModel.setNumber(rate)

        mBinding.numberKeyboard.setOnCalculatorClickListener(object : NumberKeyboardView.OnCalculatorClickListener {
            override fun onNumberClick(number: Int) {
                mViewModel.addNumber(number)
            }

            override fun onDotClick() {
                mViewModel.setDot()
            }

            override fun onOtherClick() {
                mViewModel.addMixRate()
            }

            override fun getOtherText(): String {
                return "+${mViewModel.mixRate}"
            }

        })
    }

    override fun initListener() {
        mBinding.btnBack.setOnClickListener {
            mViewModel.backNumber()
        }
        mBinding.btnClear.setOnClickListener {
            mViewModel.clearNumber()
        }
        mBinding.btnConfirm.setOnClickListener {
            mViewModel.reserve()
            dismiss()
        }
    }

    override fun createObserver() {
        mViewModel.onEditMoney.observe(viewLifecycleOwner) {
            val text = "@$it"
            mBinding.etRate.setText(text)
            val length = text.length
            mBinding.etRate.setSelection(length)
        }
    }
}