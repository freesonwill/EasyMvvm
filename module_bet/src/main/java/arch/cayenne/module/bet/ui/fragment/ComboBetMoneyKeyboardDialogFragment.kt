package arch.cayenne.module.bet.ui.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.setFragmentResult
import arch.cayenne.lib.base.ui.fragment.BasePositionDialogFragment
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.ui.view.NumberKeyboardView
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_MONEY_INPUT
import arch.cayenne.module.bet.databinding.FragmentComboBetMoneyKeyboardDialogBinding
import arch.cayenne.module.bet.viewmodel.ComboBetMoneyKeyboardDialogViewModel
import kotlin.reflect.KClass
import androidx.core.graphics.drawable.toDrawable
import arch.cayenne.lib.common.data.constants.QuickAmountEnum
import arch.cayenne.lib.common.data.constants.QuickAmountKeyboardEnum
import arch.cayenne.lib.common.ui.adapter.QuickAmountAdapter
import arch.cayenne.lib.common.utils.ext.SportStringExt.isGreaterThanValue
import arch.cayenne.lib.common.utils.ext.setOnClickOrLongPressListener

class ComboBetMoneyKeyboardDialogFragment private constructor():
    BasePositionDialogFragment<ComboBetMoneyKeyboardDialogViewModel, FragmentComboBetMoneyKeyboardDialogBinding>() {

    companion object {
        private const val POSITION_X = "positionX"
        private const val POSITION_Y = "positionY"
        private const val CURRENT_MONEY_NUMBER = "currentMoneyNumber"
        private const val MIN_NUMBER = "minNumber"
        private const val MAX_NUMBER = "maxNumber"

        fun newInstance(positionX: Int?, positionY: Int?, currentMoney: String, minNumber: Long, maxNumber: Long): ComboBetMoneyKeyboardDialogFragment {
            val b = Bundle()
            positionX?.let {
                b.putInt(POSITION_X, it)
            }
            positionY?.let {
                b.putInt(POSITION_Y, it)
            }
            currentMoney.let {
                b.putString(CURRENT_MONEY_NUMBER, it)
            }
            b.putLong(MIN_NUMBER, minNumber)
            b.putLong(MAX_NUMBER, maxNumber)
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

    override val dialogBackground: Drawable?
        get() = null

    private val quickAmountAdapter: QuickAmountAdapter by lazy {
        QuickAmountAdapter(QuickAmountKeyboardEnum.COMBO) {
            mViewModel.setNumber(it)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun cancel() {
                if (!mBinding.root.isEnabled) return
                // 讓系統其他地方調用 dismiss 時也會觸發動畫
                if (mBinding.root.translationX == 0f) {
                    doExitAnim()
                } else {
                    super.dismiss()
                }
            }
        }
    }

    override fun setDialogPosition(w: Window) {
        val marginInPx = 16.dp2px
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val maxWidth = screenWidth - marginInPx * 2
        w.setLayout(maxWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        w.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        val positionX = requireArguments().getInt(POSITION_X, -1)
        val positionY = requireArguments().getInt(POSITION_Y, -1)

        if (positionX != -1 && positionY != -1) {
            mBinding.root.measure(
                View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val dialogHeight = mBinding.root.measuredHeight

            val statusBarHeight =  ViewUtils.getStatusBarHeight(requireContext())

            val layoutParams = w.attributes
            layoutParams.gravity = Gravity.TOP or Gravity.END
            val triangleHeight = mBinding.triangle.measuredHeight // 預設高度
            layoutParams.x = 10.dp2px
            layoutParams.y = positionY - dialogHeight - statusBarHeight - triangleHeight
            w.attributes = layoutParams

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

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.visibility = View.INVISIBLE

        ViewUtils.hideKeyboard(requireContext(), mBinding.etMoney)
        initKeyboard()

        mBinding.rvQuickAmount.adapter = quickAmountAdapter
        quickAmountAdapter.submitList(QuickAmountEnum.entries)

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
        mBinding.btnBack.setOnClickOrLongPressListener (onClick = {
            mViewModel.backNumber()
        }, onLongPressRepeat = {
            mViewModel.backNumber()
        })
        mBinding.btnClear.setOnClickListener {
            mViewModel.clearNumber()
        }
        mBinding.btnDouble.setOnClickListener {
            mViewModel.doubleNumber()
        }
    }

    override suspend fun createObserver() {
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
        mViewModel.onCurrencyListener.observe(viewLifecycleOwner) {
            mBinding.tvMoney.text = CurrencySymbols.getSymbol(it)
        }
    }

    private fun initKeyboard() {
        val minNumber = requireArguments().getLong(MIN_NUMBER, -1L)
        val maxNumber = requireArguments().getLong(MAX_NUMBER, -1L)
        if (minNumber != -1L && maxNumber != -1L) {
            mViewModel.setNumberLimit(minNumber, maxNumber)
        }

        val currentMoney = requireArguments().getString(CURRENT_MONEY_NUMBER, "")
        mViewModel.setNumber(currentMoney)
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
        val minNumber = mViewModel.minMoney.getMoney()
        val maxNumber = mViewModel.maxMoney.getMoney()
        val curAmount = mViewModel.editValue
        if (minNumber.isGreaterThanValue(curAmount)) {
            showToast(getString(R.string.hint_less_min_amount))
        } else if (curAmount.isGreaterThanValue(maxNumber)) {
            showToast(getString(arch.cayenne.lib.common.R.string.toast_over_max))
        } else {
            resultBundle.putString(VALUE_MONEY_INPUT, curAmount)
            doExitAnim()
        }
    }

    private fun doExitAnim() {
        if (!mBinding.root.isEnabled) return
        mBinding.root.isEnabled = false

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

    override fun onDismiss(dialog: DialogInterface) {
        setFragmentResult(KEY_RESULT, resultBundle.apply {
            if (!this.containsKey(VALUE_MONEY_INPUT)) {
                putString(VALUE_MONEY_INPUT, requireArguments().getString(CURRENT_MONEY_NUMBER, ""))
            }
        })
        super.onDismiss(dialog)
    }
}