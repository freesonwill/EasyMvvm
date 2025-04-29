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
import arch.cayenne.lib.base.ui.fragment.BaseDialogFragment
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_MONEY_INPUT
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentComboBetMoneyKeyboardDialogBinding
import arch.cayenne.module.bet.ui.custom.NumberKeyboardView
import arch.cayenne.module.bet.viewmodel.ComboBetMoneyKeyboardDialogViewModel
import kotlin.reflect.KClass

class ComboBetMoneyKeyboardDialogFragment private constructor():
    BaseDialogFragment<ComboBetMoneyKeyboardDialogViewModel, FragmentComboBetMoneyKeyboardDialogBinding>() {

    companion object {
        private const val POSITION_X = "positionX"
        private const val POSITION_Y = "positionY"
        private const val CURRENT_MONEY_NUMBER = "currentMoneyNumber"
        private const val MIN_NUMBER = "minNumber"
        private const val MAX_NUMBER = "maxNumber"
        private const val REMAINING_MONEY_NUMBER = "remainingMoney"

        fun newInstance(positionX: Int?, positionY: Int?, currentMoney: Long?, minNumber: Long, maxNumber: Long, remainingMoney: Long): ComboBetMoneyKeyboardDialogFragment {
            val b = Bundle()
            positionX?.let {
                b.putInt(POSITION_X, it)
            }
            positionY?.let {
                b.putInt(POSITION_Y, it)
            }
            currentMoney?.let {
                b.putLong(CURRENT_MONEY_NUMBER, it)
            }
            b.putLong(MIN_NUMBER, minNumber)
            b.putLong(MAX_NUMBER, maxNumber)
            b.putLong(REMAINING_MONEY_NUMBER, remainingMoney)
            return ComboBetMoneyKeyboardDialogFragment().apply {
                arguments = b
            }
        }
    }

    override val vbClass: KClass<FragmentComboBetMoneyKeyboardDialogBinding>
        get() = FragmentComboBetMoneyKeyboardDialogBinding::class
    override val vmClass: KClass<ComboBetMoneyKeyboardDialogViewModel>
        get() = ComboBetMoneyKeyboardDialogViewModel::class

    override fun onStart() {
        super.onStart()
        setDialogPosition()
    }

    override fun initView(savedInstanceState: Bundle?) {
        isCancelable = false

        ViewUtils.hideKeyboard(requireContext(), mBinding.etMoney)
        mBinding.etMoney.requestFocus()
        initKeyboard()

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
            mViewModel.setNumber(10000)
        }
        mBinding.btn500.setOnClickListener {
            mViewModel.setNumber(50000)
        }
        mBinding.btn1000.setOnClickListener {
            mViewModel.setNumber(100000)
        }
        mBinding.btn2000.setOnClickListener {
            mViewModel.setNumber(200000)
        }
        mBinding.btn5000.setOnClickListener {
            mViewModel.setNumber(500000)
        }
    }

    override fun createObserver() {
        mViewModel.onEditNumber.observe(viewLifecycleOwner) {
            mBinding.etMoney.setText(it)
            val length = it.length
            mBinding.etMoney.setSelection(length)
        }
        mViewModel.onNumberLimit.observe(viewLifecycleOwner) {
            mBinding.etMoney.hint = getString(R.string.et_money_hint).format(it.first.getMoney(), it.second.getMoney())
        }
        mViewModel.onOverNumberListener.observe(viewLifecycleOwner) {
            // TODO show toast
        }
    }

    private fun initKeyboard() {
        val currentMoney = requireArguments().getLong(CURRENT_MONEY_NUMBER, -1L)
        if (currentMoney != -1L) {
            mViewModel.setNumber(currentMoney)
        }

        val minNumber = requireArguments().getLong(MIN_NUMBER, -1L)
        val maxNumber = requireArguments().getLong(MAX_NUMBER, -1L)
        if (minNumber != -1L && maxNumber != -1L) {
            mViewModel.setNumberLimit(minNumber, maxNumber)
        }

        val remainingMoney = requireArguments().getLong(REMAINING_MONEY_NUMBER, -1L)
        if (remainingMoney != -1L) {
            mViewModel.setRemainingNumber(remainingMoney)
        }
    }

    private fun setDialogPosition() {
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
        val bundle = Bundle()
        mViewModel.onEditNumber.value?.let {
            if (it.isNotEmpty()) {
                val money = it.toMoney()
                bundle.putLong(VALUE_MONEY_INPUT, money)
            }
        }
        setFragmentResult(KEY_RESULT, bundle)
    }
}