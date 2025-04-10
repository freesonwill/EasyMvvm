package com.walisport.module.bet.ui.fragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import androidx.core.animation.doOnEnd
import androidx.navigation.fragment.NavHostFragment
import com.walisport.lib.base.ui.BaseBottomSheetFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.bet.databinding.FragmentBetSheetBinding

class BetSheetFragment private constructor(): BaseBottomSheetFragment<FragmentBetSheetBinding>() {

    companion object {
        const val MATCH_ID = "matchId"
        fun newInstance(matchId: Int? = null): BetSheetFragment {
            val b = Bundle()
            return if (matchId == null) {
                BetSheetFragment().apply {
                    arguments = b
                }
            } else {

                b.putInt(MATCH_ID, matchId)
                BetSheetFragment().apply {
                    arguments = b
                }
            }
        }
    }

    override val mBinding: FragmentBetSheetBinding by viewBind()

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {

//        val navController = NavHostFragment.findNavController(mBinding.mainNav.getFragment())
//        navController.addOnDestinationChangedListener { _, d, b ->
//            val curHeight = mBinding.root.height
//            if (curHeight == 0) {
//                mBinding.mainNav.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//                    override fun onGlobalLayout() {
//                        mBinding.mainNav.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                        val nextHeight = mBinding.root.measuredHeight
//                        if (curHeight != nextHeight) {
//                            animateBottomSheetHeight(nextHeight)
//                        }
//                    }
//                })
//            }
//
//        }
    }

    private fun animateBottomSheetHeight(to: Int) {
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?: return

        val anim = AnimationUtils.loadAnimation(requireContext(), com.walisport.lib.base.R.anim.slide_bottom_sheet_up)
        bottomSheet.startAnimation(anim)

        val valueAnimator = ValueAnimator.ofInt(0, to).apply {
            duration = anim.duration
            addUpdateListener { valueAnimator ->
                val newHeight = valueAnimator.animatedValue as Int
                bottomSheet.layoutParams = bottomSheet.layoutParams.apply {
                    height = newHeight
                }
                bottomSheet.requestLayout()
            }
            doOnEnd {
                bottomSheet.layoutParams = bottomSheet.layoutParams.apply {
                    height = LayoutParams.WRAP_CONTENT
                }
            }
        }
        valueAnimator.start()
    }
}