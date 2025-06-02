package arch.cayenne.module.betslip.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import arch.cayenne.lib.base.ui.fragment.BaseDialogFragment
import arch.cayenne.lib.common.ui.view.NumberKeyboardView
import arch.cayenne.lib.common.utils.ViewUtils
import kotlin.reflect.KClass
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipModifybetBinding
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipModifyOddsViewModel


class BetSlipModifyOddsFragment private constructor() :
    BaseDialogFragment<BetSlipModifyOddsViewModel, FragmentLiveBetslipModifybetBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipModifybetBinding>
        get() = FragmentLiveBetslipModifybetBinding::class
    override val vmClass: KClass<BetSlipModifyOddsViewModel>
        get() = BetSlipModifyOddsViewModel::class

    private var _confirmClick: ((value: String) -> Unit)? = null

    companion object {
        const val ODDS_KEY: String = "odds_key"

        fun newInstance(odds: String): BetSlipModifyOddsFragment {
            return BetSlipModifyOddsFragment().apply {
                arguments = Bundle().apply {
                    putString(ODDS_KEY, odds)
                }
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        setDialogPosition()
        ViewUtils.hideKeyboard(requireContext(), mBinding.etOdds)
        mBinding.etOdds.requestFocus()
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
            main.setOnClickListener {
                dismiss()
            }
            val odds = arguments?.getString(ODDS_KEY)
            mViewModel.setArgument(odds ?: "")
        }
    }

    fun setConfirmListener(listener: (value: String) -> Unit) {
        this._confirmClick = listener
    }

    override fun initListener() {

    }

    override fun createObserver() {
        mViewModel.editNumber.observe(viewLifecycleOwner) {
            mBinding.etOdds.setText(it)
            mBinding.etOdds.setSelection(it.length)
        }
    }

    override val dialogBackground: Drawable?
        get() = ColorDrawable(Color.TRANSPARENT)

    private fun setDialogPosition() {
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.apply {
            mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val layoutParams = attributes
                    layoutParams.width = LayoutParams.MATCH_PARENT
                    layoutParams.height = LayoutParams.MATCH_PARENT
                    attributes = layoutParams
                }
            })
        }
    }

}