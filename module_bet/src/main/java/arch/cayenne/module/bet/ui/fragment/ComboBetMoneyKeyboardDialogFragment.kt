package arch.cayenne.module.bet.ui.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.setFragmentResult
import arch.cayenne.lib.base.ui.fragment.BaseDialogFragment
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_MONEY_INPUT
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentComboBetMoneyKeyboardDialogBinding
import arch.cayenne.lib.common.ui.view.NumberKeyboardView
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

    private val resultBundle: Bundle by lazy {
        Bundle()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun dismiss() {
                if (mBinding.root.scaleX == 0f) {
                    super.dismiss()
                } else {
                    mBinding.root.pivotX = mBinding.triangle.x + mBinding.triangle.width / 2
                    mBinding.root.pivotY = mBinding.root.height.toFloat()

                    mBinding.root.animate()
                        .scaleX(0f)
                        .scaleY(0f)
                        .alpha(0f)
                        .setDuration(200)
                        .setInterpolator(android.view.animation.DecelerateInterpolator())
                        .withEndAction {
                            super.dismiss()
                        }
                        .start()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setDialogPosition()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.visibility = View.INVISIBLE

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
            sendMoney()
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
            mBinding.etMoney.hint =
                getString(R.string.et_money_hint).format(it.first.getMoney(), it.second.getMoney())
        }
        mViewModel.onOverNumberListener.observe(viewLifecycleOwner) {
            it.msg?.let { msg ->
                showToast(msg)
            }
        }
        mViewModel.onBalanceListener.observe(viewLifecycleOwner) {
            mBinding.tvMoney.text = CurrencySymbols.getSymbol(it.currency)
        }
    }

    private fun initKeyboard() {
        val minNumber = requireArguments().getLong(MIN_NUMBER, -1L)
        val maxNumber = requireArguments().getLong(MAX_NUMBER, -1L)
        if (minNumber != -1L && maxNumber != -1L) {
            mViewModel.setNumberLimit(minNumber, maxNumber)
        }

        val remainingMoney = requireArguments().getLong(REMAINING_MONEY_NUMBER, -1L)
        if (remainingMoney != -1L) {
            mViewModel.setRemainingNumber(remainingMoney)
        }

        val currentMoney = requireArguments().getLong(CURRENT_MONEY_NUMBER, -1L)
        if (currentMoney != -1L) {
            mViewModel.setNumber(currentMoney)
        }
    }

    private fun setDialogPosition() {
        dialog?.window?.let { window ->
            window.setDimAmount(0.6f)
            val marginInPx = 16.dp2px
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
            val maxWidth = screenWidth - marginInPx * 2
            window.setLayout(maxWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val positionX = requireArguments().getInt(POSITION_X, -1)
            val positionY = requireArguments().getInt(POSITION_Y, -1)

            if (positionX != -1 && positionY != -1) {
                mBinding.root.measure(
                    View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                val dialogHeight = mBinding.root.measuredHeight

                val layoutParams = window.attributes
                layoutParams.gravity = Gravity.TOP or Gravity.START
                val triangleWidth = mBinding.triangle.width.takeIf { it > 0 } ?: 20.dp2px // 預設寬度
                layoutParams.x = positionX - triangleWidth / 2
                layoutParams.y = positionY - dialogHeight
                window.attributes = layoutParams

                mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                        // 先設定 triangle 位置
                        setTrianglePosition(positionX)

                        mBinding.root.post {

                            // 動畫初始狀態
                            mBinding.root.pivotX = mBinding.triangle.x + mBinding.triangle.width / 2
                            mBinding.root.pivotY = dialogHeight.toFloat()
                            mBinding.root.scaleX = 0f
                            mBinding.root.scaleY = 0f
                            mBinding.root.alpha = 0f

                            // 開始動畫
                            mBinding.root.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .alpha(1f)
                                .setDuration(200)
                                .setInterpolator(android.view.animation.DecelerateInterpolator())
                                .withStartAction {
                                    mBinding.root.visibility = View.VISIBLE
                                }
                                .start()
                        }

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

    private fun sendMoney() {
        val minAmount = mViewModel.mixMoney
        val curAmount = mViewModel.editValue.toMoney()
        if (curAmount < minAmount) {
            showToast(getString(R.string.hint_less_min_amount))
        } else {
            resultBundle.putLong(VALUE_MONEY_INPUT, curAmount)
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        setFragmentResult(KEY_RESULT, resultBundle.apply {
            if (!this.containsKey(VALUE_MONEY_INPUT)) {
                putLong(VALUE_MONEY_INPUT, requireArguments().getLong(CURRENT_MONEY_NUMBER, 0L))
            }
        })
        super.onDismiss(dialog)
    }
}