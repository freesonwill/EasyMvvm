package arch.cayenne.lib.common.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.setFragmentResult
import arch.cayenne.lib.base.ui.fragment.BaseDialogFragment
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.common.ui.view.NumberKeyboardView
import kotlin.reflect.KClass
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import arch.cayenne.lib.common.databinding.FragmentReserveDialogBinding
import arch.cayenne.lib.common.ui.viewmodel.ReserveDialogViewModel
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.SportMoneyOddsExt.getOdds

class ReserveDialogFragment private constructor() : BaseDialogFragment<ReserveDialogViewModel, FragmentReserveDialogBinding>() {

    companion object {
        private const val LOCATION_X = "locationX"
        private const val LOCATION_Y = "locationY"
        private const val VIEW_HEIGHT = "viewHeight"
        private const val ODDS_NUMBER = "oddsNumber"
        const val KEY_RESULT = "key_result"
        const val VALUE_RESERVE_COMPLETE = "value_reserve_complete"
        const val KEY_ODDS_RESULT = "key_odds_result"

        fun newInstance(positionX: Int, positionY: Int, viewHeight: Int, odds: Int): ReserveDialogFragment {
            val b = Bundle()
            b.putInt(LOCATION_X, positionX)
            b.putInt(LOCATION_Y, positionY)
            b.putInt(VIEW_HEIGHT, viewHeight)
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

    override val dialogBackground: Drawable?
        get() = null

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

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {

            val positionX = requireArguments().getInt(LOCATION_X, -1)
            val positionY = requireArguments().getInt(LOCATION_Y, -1)

            if (positionX != -1 && positionY != -1) {
                val viewHeight = requireArguments().getInt(VIEW_HEIGHT, 0)

                val layoutParams = it.attributes
                layoutParams.gravity = Gravity.TOP or Gravity.END

                val pop = mBinding.root
                pop.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                val popHeight = pop.measuredHeight

                val metrics = requireContext().resources.displayMetrics
                val usableWidth = metrics.widthPixels
                val usableHeight = metrics.heightPixels
                val isFull = positionY + popHeight + viewHeight > usableHeight

                mBinding.topTriangle.isVisible = !isFull
                mBinding.bottomTriangle.isVisible = isFull

                val triangle = if (isFull) mBinding.bottomTriangle else mBinding.topTriangle
                triangle.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )

                val triangleWidth = triangle.measuredWidth
                val triangleHeight = triangle.measuredHeight

                (triangle.layoutParams as ConstraintLayout.LayoutParams).marginEnd = usableWidth - positionX - triangleWidth / 2

                layoutParams.x = 0
                layoutParams.y = if (isFull) positionY - popHeight + triangleHeight else positionY - triangleHeight + viewHeight

                it.attributes = layoutParams

                mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        // 動畫初始狀態
                        mBinding.root.pivotX = triangle.x + triangle.width / 2
                        mBinding.root.pivotY = if (isFull) mBinding.root.height.toFloat() else 0f
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
                                removeDim()
                            }
                            .withStartAction {
                                mBinding.root.visibility = View.VISIBLE
                            }
                            .start()
                    }
                })

            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.visibility = View.INVISIBLE

        ViewUtils.hideKeyboard(requireContext(), mBinding.etRate)
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

    override suspend fun createObserver() {
        mViewModel.onEditNumber.observe(viewLifecycleOwner) {
            val text = "@$it"
            mBinding.etRate.setText(text)
            val length = text.length
            mBinding.etRate.setSelection(length)
        }
        mViewModel.isConfirmEnable.observe(viewLifecycleOwner) {
            mBinding.btnConfirm.isEnabled = it
        }
    }

    private fun doExitAnim() {
        if (!mBinding.root.isEnabled) return
        mBinding.root.isEnabled = false
        mBinding.root.animate()
            .scaleX(0f)
            .scaleY(0f)
            .alpha(0f)
            .setDuration(200)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .withStartAction {
                removeDim()
            }
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