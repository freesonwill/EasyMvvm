package com.walisport.module.hall.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import arch.cayenne.lib.base.ui.fragment.BasePositionDialogFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.ui.dialog.NoGapDialog
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.hall.databinding.FragmentAllGameInfoDialogBinding
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
        const val TEXT = "text"
        fun newInstance(
            locationX: Int ,
            locationY: Int ,
            text: String
        ): AllInfoDialogFragment {
            val b = Bundle()
            b.putInt(LOCATION_X , locationX)
            b.putInt(LOCATION_Y , locationY)
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
            root.measure(
                View.MeasureSpec.makeMeasureSpec(0 , View.MeasureSpec.UNSPECIFIED) ,
                View.MeasureSpec.makeMeasureSpec(0 , View.MeasureSpec.UNSPECIFIED)
            )
            val locationX = requireArguments().getInt(LOCATION_X)
            val locationY = requireArguments().getInt(LOCATION_Y)
//            val offsetX =
//                (((ivBgBottom.layoutParams as? ConstraintLayout.LayoutParams)?.marginStart)
//                    ?: 0) + ivBgBottom.measuredWidth / 2
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