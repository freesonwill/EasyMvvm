package arch.cayenne.lib.common.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import arch.cayenne.lib.base.ui.fragment.BasePositionDialogFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.databinding.FragmentAllGameInfoDialogBinding
import arch.cayenne.lib.common.ui.dialog.NoGapDialog
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import kotlin.reflect.KClass

class AllInfoDialogFragment :
    BasePositionDialogFragment<EmptyViewModel , FragmentAllGameInfoDialogBinding>() {
    override val vbClass: KClass<FragmentAllGameInfoDialogBinding> =
        FragmentAllGameInfoDialogBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    companion object {
        const val TAG = "AllInfoDialogFragment"
        const val LOCATION_X = "locationX"
        const val LOCATION_Y = "locationY"
        const val TEXT_MAX_LINES = "TEXT_MAX_LINES"
        const val TEXT_MAX_WIDTH = "TEXT_MAX_WIDTH"
        const val ARROW_MARGIN_START = "ARROW_MARGIN_START"
        const val ARROW_MARGIN_END = "ARROW_MARGIN_END"
        const val TEXT = "text"
        fun newInstance(
            locationX: Int,
            locationY: Int,
            text: String,
            textMaxLines:Int? = null,
            textMaxWidth:Int? = null,
            arrowMarginStart:Int? = null,
            arrowMarginEnd:Int? = null,
        ): AllInfoDialogFragment {
            val b = Bundle()
            b.putInt(LOCATION_X , locationX)
            b.putInt(LOCATION_Y , locationY)
            textMaxLines?.let { b.putInt(TEXT_MAX_LINES, it) }
            textMaxWidth?.let { b.putInt(TEXT_MAX_WIDTH, it) }
            arrowMarginStart?.let { b.putInt(ARROW_MARGIN_START, it) }
            arrowMarginEnd?.let { b.putInt(ARROW_MARGIN_END, it) }
            b.putString(TEXT , text)
            return AllInfoDialogFragment().apply {
                arguments = b
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.visibility = View.INVISIBLE
        mBinding.tvText.text = requireArguments().getString(TEXT)
        removeDim()
    }

    override fun initListener() {

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return NoGapDialog(requireContext() , theme)
    }

    override fun setDialogPosition(w: Window) {
        with(mBinding) {
            val textMaxLines = requireArguments().getInt(TEXT_MAX_LINES,-1)
            val textMaxWidth = requireArguments().getInt(TEXT_MAX_WIDTH,-1)
            val arrowMarginStart = requireArguments().getInt(ARROW_MARGIN_START,-1)
            val arrowMarginEnd = requireArguments().getInt(ARROW_MARGIN_END,-1)
            if(textMaxLines != -1){
                tvText.maxLines = textMaxLines
            }
            if(textMaxWidth != -1){
                tvText.maxWidth = textMaxWidth
            }
            if(arrowMarginStart != -1 || arrowMarginEnd != -1){
                val lp = ivBgBottom.layoutParams as ViewGroup.MarginLayoutParams
                arrowMarginStart.takeIf { it != -1 }?.let {
                    lp.marginStart = it
                }
                arrowMarginEnd.takeIf { it != -1 }?.let {
                    lp.marginEnd = it
                }
                ivBgBottom.layoutParams = lp
            }

            root.measure(
                View.MeasureSpec.makeMeasureSpec(0 , View.MeasureSpec.UNSPECIFIED) ,
                View.MeasureSpec.makeMeasureSpec(0 , View.MeasureSpec.UNSPECIFIED)
            )
            val locationX = requireArguments().getInt(LOCATION_X)
            val locationY = requireArguments().getInt(LOCATION_Y)
            val offsetX = root.measuredWidth / 2
            val layoutParams = w.attributes
            layoutParams.gravity = Gravity.TOP or Gravity.START
            layoutParams.x = locationX-offsetX
            val y = locationY - clRoot.measuredHeight
            ivBgBottom.visibility = View.VISIBLE
            layoutParams.y = y - 3.dp2px
            w.attributes = layoutParams
            mBinding.root.visibility = View.VISIBLE
        }
    }


}