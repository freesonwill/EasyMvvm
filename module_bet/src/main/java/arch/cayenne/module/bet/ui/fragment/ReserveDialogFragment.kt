package arch.cayenne.module.bet.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewTreeObserver
import androidx.fragment.app.setFragmentResult
import arch.cayenne.lib.base.ui.BaseDialogFragment
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_RESERVE_COMPLETE
import arch.cayenne.module.bet.databinding.FragmentReserveDialogBinding
import arch.cayenne.module.bet.ui.custom.NumberKeyboardView
import arch.cayenne.module.bet.viewmodel.ReserveDialogViewModel
import kotlin.reflect.KClass

class ReserveDialogFragment private constructor() : BaseDialogFragment<ReserveDialogViewModel,FragmentReserveDialogBinding>() {

    companion object {
        private const val POSITION_X = "positionX"
        private const val POSITION_Y = "positionY"
        private const val MATCH_ID = "matchId"
        private const val ODDS_NUMBER = "oddsNumber"

        fun newInstance(positionX: Int?, positionY: Int?, id: Int, odds: String): ReserveDialogFragment {
            val b = Bundle()
            positionX?.let {
                b.putInt(POSITION_X, it)
            }
            positionY?.let {
                b.putInt(POSITION_Y, it)
            }
            b.putInt(MATCH_ID, id)
            b.putString(ODDS_NUMBER, odds)
            return ReserveDialogFragment().apply {
                arguments = b
            }
        }
    }

    override val vbClass: KClass<FragmentReserveDialogBinding>
        get() = FragmentReserveDialogBinding::class
    override val vmClass: KClass<ReserveDialogViewModel>
        get() = ReserveDialogViewModel::class

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

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.etRate.requestFocus()

        val rate = requireArguments().getString(ODDS_NUMBER)
        if (!rate.isNullOrEmpty()) {
            mViewModel.setNumber(rate.toFloat())
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
            val id = requireArguments().getInt(MATCH_ID, -1)
            if (id != -1) {
                mViewModel.reserve(id)
                arguments = Bundle().apply {
                    putString(KEY_RESULT, VALUE_RESERVE_COMPLETE)
                }
            }
            dismiss()
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

    override fun dismiss() {
        super.dismiss()
        setFragmentResult(KEY_RESULT, arguments ?: Bundle())
    }
}