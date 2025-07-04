package arch.cayenne.module.bet.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.setFragmentResult
import arch.cayenne.lib.base.ui.fragment.BaseDialogFragment
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.module.bet.data.Config.KEY_ODDS_RESULT
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_RESERVE_COMPLETE
import arch.cayenne.module.bet.databinding.FragmentReserveDialogBinding
import arch.cayenne.lib.common.ui.view.NumberKeyboardView
import arch.cayenne.module.bet.viewmodel.ReserveDialogViewModel
import kotlin.reflect.KClass
import android.content.DialogInterface
import androidx.constraintlayout.widget.ConstraintLayout

class ReserveDialogFragment private constructor() : BaseDialogFragment<ReserveDialogViewModel, FragmentReserveDialogBinding>() {

    companion object {
        private const val POSITION_X = "positionX"
        private const val POSITION_Y = "positionY"
        private const val ODDS_NUMBER = "oddsNumber"

        fun newInstance(positionX: Int?, positionY: Int?, odds: Int): ReserveDialogFragment {
            val b = Bundle()
            positionX?.let {
                b.putInt(POSITION_X, it)
            }
            positionY?.let {
                b.putInt(POSITION_Y, it)
            }
            b.putInt(ODDS_NUMBER, odds)
            return ReserveDialogFragment().apply {
                arguments = b
            }
        }
    }

    override val vbClass: KClass<FragmentReserveDialogBinding>
        get() = FragmentReserveDialogBinding::class
    override val vmClass: KClass<ReserveDialogViewModel>
        get() = ReserveDialogViewModel::class

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun dismiss() {
                // 讓系統其他地方調用 dismiss 時也會觸發動畫
                if (mBinding.root.translationX == 0f) {
                    doExitAnim()
                } else {
                    super.dismiss()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.setDimAmount(0.75f)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val positionX = requireArguments().getInt(POSITION_X, -1)
            val positionY = requireArguments().getInt(POSITION_Y, -1)

            if (positionX != -1 && positionY != -1) {
                val layoutParams = it.attributes
                layoutParams.gravity = Gravity.TOP or Gravity.START

                val pop = mBinding.root
                pop.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                val popWidth = pop.measuredWidth

                val triangle = mBinding.triangle
                triangle.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )

                val triangleWidth = triangle.measuredWidth
                val triangleHeight = triangle.measuredHeight

                val endMargin = (mBinding.triangle.layoutParams as ConstraintLayout.LayoutParams).marginEnd

                val px = popWidth - (endMargin + triangleWidth / 2)

                layoutParams.x = positionX - px
                layoutParams.y = positionY - triangleHeight

                it.attributes = layoutParams

                // 設置初始位置在螢幕右側
                mBinding.root.translationX = pop.measuredWidth.toFloat()

                mBinding.root.post {
                    mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            // 執行滑入動畫
                            mBinding.root.animate()
                                .translationX(0f)
                                .setDuration(300)
                                .setInterpolator(android.view.animation.DecelerateInterpolator())
                                .withStartAction {
                                    mBinding.root.visibility = View.VISIBLE
                                }
                                .start()
                        }
                    })
                }
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.visibility = View.INVISIBLE
        mBinding.etRate.requestFocus()

        val odds = requireArguments().getInt(ODDS_NUMBER, -1)
        if (odds != -1) {
            mViewModel.init(odds)
        }

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
                return "+${mViewModel.minOdds.getOdds()}"
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
            val odds = mViewModel.onEditNumber.value?.toOdds() ?: -1
            if (odds != -1) {
                arguments = Bundle().apply {
                    putString(KEY_RESULT, VALUE_RESERVE_COMPLETE)
                    putInt(KEY_ODDS_RESULT, odds)
                }
            }
            doExitAnim()
        }
    }

    override fun createObserver() {
        mViewModel.onEditNumber.observe(viewLifecycleOwner) {
            val text = "@$it"
            mBinding.etRate.setText(text)
            val length = text.length
            mBinding.etRate.setSelection(length)
        }
    }

    private fun doExitAnim() {
        mBinding.root.animate()
            .translationX(mBinding.root.width.toFloat())
            .setDuration(300)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .withEndAction {
                super.dismiss()
            }
            .start()
    }

    override fun onDismiss(dialog: DialogInterface) {
        setFragmentResult(KEY_RESULT, arguments ?: Bundle())
        super.onDismiss(dialog)
    }
}