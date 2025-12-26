package arch.cayenne.module.bet.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.base.ui.fragment.BasePositionDialogFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.ui.dialog.NoGapDialog
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.bet.databinding.FragmentBetInfoBinding
import kotlin.reflect.KClass

/**
 * 组合列表弹窗的提示toast弹窗
 */

class BetInfoDialogFragment : BasePositionDialogFragment<EmptyViewModel, FragmentBetInfoBinding>() {

    override val vbClass: KClass<FragmentBetInfoBinding> = FragmentBetInfoBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    companion object {
        const val LOCATION_X = "locationX"
        const val LOCATION_Y = "locationY"
        const val TIP_CONTENT = "content"
        fun newInstance(
            locationX: Int,
            locationY: Int,
            tip: String
        ): BetInfoDialogFragment {
            val b = Bundle()
            b.putInt(LOCATION_X, locationX)
            b.putInt(LOCATION_Y, locationY)
            b.putString(TIP_CONTENT, tip)
            return BetInfoDialogFragment().apply {
                arguments = b
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.visibility = View.INVISIBLE
        removeDim()
    }

    override fun initListener() {

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return NoGapDialog(requireContext(), theme)
    }

    override fun setDialogPosition(w: Window) {
        with(mBinding) {
            root.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val locationX = requireArguments().getInt(LOCATION_X)
            val locationY = requireArguments().getInt(LOCATION_Y)
            val tip = requireArguments().getString(TIP_CONTENT)
            tvBetInfo.text = tip
            val offsetX =
                (((ivBgBottom.layoutParams as? ConstraintLayout.LayoutParams)?.marginStart)
                    ?: 0) + ivBgBottom.measuredWidth / 2
            val layoutParams = w.attributes
            layoutParams.gravity = Gravity.TOP or Gravity.START
            layoutParams.x = locationX - offsetX
            if (tip?.length!! > 16) {
                layoutParams.y = locationY - clRoot.measuredHeight - 47.dp2px
            } else {
                layoutParams.y = locationY - clRoot.measuredHeight - 27.dp2px
            }
            w.attributes = layoutParams
            mBinding.root.visibility = View.VISIBLE
        }
    }
}