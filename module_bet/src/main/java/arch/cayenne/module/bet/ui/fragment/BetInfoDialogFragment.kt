package arch.cayenne.module.bet.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.fragment.BasePositionDialogFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.dialog.NoGapDialog
import arch.cayenne.lib.common.utils.ViewUtils
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
        const val VIEW_HEIGHT = "VIEW_HEIGHT"
        const val TIP_CONTENT = "content"
        fun newInstance(
            locationX: Int,
            locationY: Int,
            viewHeight: Int,
            tip: String
        ): BetInfoDialogFragment {
            val b = Bundle()
            b.putInt(LOCATION_X, locationX)
            b.putInt(LOCATION_Y, locationY)
            b.putInt(VIEW_HEIGHT, viewHeight)
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
            val locationX = requireArguments().getInt(LOCATION_X)
            val locationY = requireArguments().getInt(LOCATION_Y) - ViewUtils.getStatusBarHeight(requireContext())
            val viewHeight = requireArguments().getInt(VIEW_HEIGHT)
            val tip = requireArguments().getString(TIP_CONTENT) ?: ""
            tvBetInfo.text = tip

            if (tip.length > 16) {
                mBinding.tvBetInfo.setPadding(16.dp2px,11.5.dp2px,17.dp2px,11.dp2px)
                mBinding.tvBetInfo.minHeight = 60.dp2px
            } else {
                mBinding.tvBetInfo.setPadding(12.dp2px,10.dp2px,12.dp2px,10.dp2px)
                mBinding.tvBetInfo.height = 41.5.dp2px
            }
            root.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val offsetX = (((ivBgBottom.layoutParams as? ConstraintLayout.LayoutParams)?.marginStart) ?: 0) + ivBgBottom.measuredWidth / 2
            val popHeight =  clRoot.measuredHeight
            val x = locationX - offsetX
            var y = locationY - popHeight
            //"aaaa---measuredHeight,${root.measuredHeight},height:${root.height},x:$x,y:$y,viewHeight:$viewHeight".logd(TAG)
            if (tip.length > 16) {
                x + 1.dp2px
                y -= 4.dp2px
            } else {
                y -= 6.dp2px
            }
            if(y < 0){
                mBinding.ivBgTop.isVisible = true
                mBinding.ivBgBottom.isVisible = false
                y = locationY + viewHeight + 4.dp2px
            }
            w.attributes = w.attributes.apply {
                this.gravity = Gravity.TOP or Gravity.START
                this.x = x
                this.y = y
            }
            mBinding.root.visibility = View.VISIBLE
        }
    }

}