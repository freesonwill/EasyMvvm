package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.base.ui.fragment.BasePositionDialogFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import com.walisport.module.hall.databinding.FragmentGameRankingInfoDialogBinding
import kotlin.reflect.KClass

class GameRankingInfoDialogFragment : BasePositionDialogFragment<EmptyViewModel, FragmentGameRankingInfoDialogBinding>() {
    override val vbClass: KClass<FragmentGameRankingInfoDialogBinding> = FragmentGameRankingInfoDialogBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    companion object {
        const val LOCATION_X = "locationX"
        const val LOCATION_Y = "locationY"
        fun newInstance(
            locationX: Int,
            locationY: Int,
        ) : GameRankingInfoDialogFragment {
            val b = Bundle()
            b.putInt(LOCATION_X, locationX)
            b.putInt(LOCATION_Y, locationY)
            return GameRankingInfoDialogFragment().apply {
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

    override fun setDialogPosition(w: Window) {
        with(mBinding) {
            root.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val locationX = requireArguments().getInt(LOCATION_X)
            val locationY = requireArguments().getInt(LOCATION_Y)
            val offsetX = (((ivBgBottom.layoutParams as? ConstraintLayout.LayoutParams)?.marginStart)?:0) + ivBgBottom.measuredWidth / 2
            val layoutParams = w.attributes
            layoutParams.gravity = Gravity.TOP or Gravity.START
            layoutParams.x = locationX - offsetX
            layoutParams.y = locationY - clRoot.measuredHeight
            w.attributes = layoutParams
            mBinding.root.visibility = View.VISIBLE
        }
    }
}