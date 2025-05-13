package com.walisport.module.live.ui

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import arch.cayenne.lib.base.ui.fragment.BaseDialogFragment
import arch.cayenne.lib.common.ui.view.NumberKeyboardView
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveBetslipModifybetBinding
import com.walisport.module.live.ui.viewmodel.LiveBetSlipModifyOddsViewModel
import kotlin.reflect.KClass

class LiveBetSlipModifyOddsFragment private constructor() :
    BaseDialogFragment<LiveBetSlipModifyOddsViewModel, FragmentLiveBetslipModifybetBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipModifybetBinding>
        get() = FragmentLiveBetslipModifybetBinding::class
    override val vmClass: KClass<LiveBetSlipModifyOddsViewModel>
        get() = LiveBetSlipModifyOddsViewModel::class

    private var _confirmClick: ((value: String) -> Unit)? = null

    companion object {
        fun newInstance(): LiveBetSlipModifyOddsFragment {
            return LiveBetSlipModifyOddsFragment()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        setDialogPosition()

        with(mBinding) {
            keyboardNumber.setOnCalculatorClickListener(object :
                NumberKeyboardView.OnCalculatorClickListener {
                override fun onNumberClick(number: Int) {
                    mViewModel.addNumber(number)
                }

                override fun onDotClick() {
                    mViewModel.setDot()
                }

                override fun onOtherClick() {
                    mViewModel.addZeroPointOne()
                }

                override fun getOtherText(): String {
                    return "+0.01"
                }
            })
            btnBack.setOnClickListener { mViewModel.backNumber() }
            btnClear.setOnClickListener { mViewModel.clearNumber() }
            btnConfirm.setOnClickListener {
                _confirmClick?.invoke(mBinding.etOdds.text.toString())
                dismiss()
            }
        }
    }

    fun setConfirmListener(listener: (value: String) -> Unit) {
        this._confirmClick = listener
    }

    override fun initListener() {

    }

    override fun createObserver() {
        mViewModel.editNumber.observe(this) {
            mBinding.etOdds.setText(it)
        }
    }

    override val dialogBackground: Drawable?
        get() = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.black_65))

    private fun setDialogPosition() {
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.apply {
            mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val layoutParams = attributes
                    layoutParams.width = LayoutParams.WRAP_CONTENT
                    layoutParams.height = LayoutParams.WRAP_CONTENT
                    layoutParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
                    attributes = layoutParams
                }
            })
        }
    }

}