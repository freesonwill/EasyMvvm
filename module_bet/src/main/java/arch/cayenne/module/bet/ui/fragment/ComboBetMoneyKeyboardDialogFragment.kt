package arch.cayenne.module.bet.ui.fragment

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import arch.cayenne.lib.base.ui.BaseDialogFragment
import arch.cayenne.lib.base.ui.viewBind
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.bet.Config.KEY_RESULT
import arch.cayenne.module.bet.Config.VALUE_MONEY_INPUT
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentComboBetMoneyKeyboardDialogBinding
import arch.cayenne.module.bet.ui.custom.NumberKeyboardView
import arch.cayenne.module.bet.viewmodel.ComboBetMoneyKeyboardDialogViewModel

class ComboBetMoneyKeyboardDialogFragment private constructor():
    BaseDialogFragment<FragmentComboBetMoneyKeyboardDialogBinding>() {

    companion object {
        private const val POSITION_X = "positionX"
        private const val POSITION_Y = "positionY"
        private const val RATE_NUMBER = "rateNumber"

        fun newInstance(positionX: Int?, positionY: Int?, rateNumber: String? = null): ComboBetMoneyKeyboardDialogFragment {
            val b = Bundle()
            positionX?.let {
                b.putInt(POSITION_X, it)
            }
            positionY?.let {
                b.putInt(POSITION_Y, it)
            }
            b.putString(RATE_NUMBER, rateNumber)
            return ComboBetMoneyKeyboardDialogFragment().apply {
                arguments = b
            }
        }
    }

    override val mBinding: FragmentComboBetMoneyKeyboardDialogBinding by viewBind()
    private val mViewModel: ComboBetMoneyKeyboardDialogViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // 將 margin 設為 16dp
            val marginInPx = 16.dp2px

            // 螢幕寬度 - 左右 margin
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
            setLayout(screenWidth - marginInPx * 2, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val positionX = requireArguments().getInt(POSITION_X, -1)
            val positionY = requireArguments().getInt(POSITION_Y, -1)

            if (positionX != -1 && positionY != -1) {
                mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        val layoutParams = attributes
                        layoutParams.gravity = Gravity.TOP
                        layoutParams.y = positionY - mBinding.root.height - mBinding.triangle.height / 4
                        attributes = layoutParams

                        setTrianglePosition(positionX)
                    }
                })
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        isCancelable = false

        ViewUtils.hideKeyboard(requireContext(), mBinding.etMoney)
        mBinding.etMoney.requestFocus()
        requireArguments().getString(RATE_NUMBER)?.let {
            if (it.contains('.')) {
                mViewModel.setNumber(it.toFloat())
            } else if (it.isNotEmpty()){
                mViewModel.setNumber(it.toInt())
            }
        }

        mBinding.numberKeyboard.setOnCalculatorClickListener(object :
            NumberKeyboardView.OnCalculatorClickListener {
            override fun onNumberClick(number: Int) {
                mViewModel.addNumber(number)
            }

            override fun onDotClick() {
                mViewModel.setDot()
            }

            override fun onOtherClick() {
                mViewModel.setMaxMoney()
            }

            override fun getOtherText(): String {
                return getString(R.string.btn_max)
            }

        })
    }

    override fun initListener() {
        mBinding.btnConfirm.setOnClickListener {
            dismiss()
        }
        mBinding.btnBack.setOnClickListener {
            mViewModel.backNumber()
        }
        mBinding.btnClear.setOnClickListener {
            mViewModel.clearNumber()
        }
        mBinding.btnDouble.setOnClickListener {
            mViewModel.doubleNumber()
        }
        // TODO 有時間改成adapter
        mBinding.btn100.setOnClickListener {
            mViewModel.setNumber(100)
        }
        mBinding.btn500.setOnClickListener {
            mViewModel.setNumber(500)
        }
        mBinding.btn1000.setOnClickListener {
            mViewModel.setNumber(1000)
        }
        mBinding.btn2000.setOnClickListener {
            mViewModel.setNumber(2000)
        }
        mBinding.btn5000.setOnClickListener {
            mViewModel.setNumber(5000)
        }
    }


    override fun createObserver() {
        mViewModel.onEditNumber.observe(viewLifecycleOwner) {
            mBinding.etMoney.setText(it)
            val length = it.length
            mBinding.etMoney.setSelection(length)
        }
    }

    private fun setTrianglePosition(targetPositionX: Int) {
        val triangleLocation = IntArray(2)
        mBinding.triangle.getLocationOnScreen(triangleLocation)
        val px = targetPositionX - triangleLocation.first() - mBinding.triangle.width / 2
        val params = mBinding.triangle.layoutParams as ConstraintLayout.LayoutParams
        params.rightMargin = params.rightMargin - px
        mBinding.triangle.layoutParams = params
    }

    override fun dismiss() {
        super.dismiss()
        val money = mBinding.etMoney.text.toString()
        val bundle = Bundle().apply {
            putString(VALUE_MONEY_INPUT, money)
        }
        setFragmentResult(KEY_RESULT, bundle)
    }
}