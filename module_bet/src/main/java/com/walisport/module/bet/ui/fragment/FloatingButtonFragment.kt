package com.walisport.module.bet.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.walisport.module.bet.viewmodel.FloatingButtonViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.module.bet.databinding.FragmentFloatingButtonBinding
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class FloatingButtonFragment : BaseFragment<FloatingButtonViewModel, FragmentFloatingButtonBinding>() {
    override val vbClass: KClass<FragmentFloatingButtonBinding> = FragmentFloatingButtonBinding::class
    override val vmClass: KClass<FloatingButtonViewModel> = FloatingButtonViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        setFloatingViewPosition(requireActivity().resources.displayMetrics.heightPixels)
    }

    override fun initListener() {
        mBinding.fab.setPerformClick {
            mViewModel.onBettingCount.value?.let { count ->
                if (count == 1) {
                    lifecycleScope.launch {
                        val id = mViewModel.getSingleBetId()
                        BetSheetFragment.newInstance(id).show(parentFragmentManager)
                    }
                } else {
                    BetSheetFragment.newInstance().show(parentFragmentManager)
                }
            }
        }
    }

    override fun createObserver() {
        mViewModel.onBettingCount.observe(viewLifecycleOwner) {
            if (it == 0) {
                mBinding.root.visibility = View.GONE
            } else {
                mBinding.fab.setCount(it)
                mBinding.root.visibility = View.VISIBLE
            }
        }
    }

    private fun setFloatingViewPosition(screenHeight: Int) {
        val floatingView = mBinding.fab
        val layoutParams = floatingView.layoutParams as ConstraintLayout.LayoutParams

        // 設定懸浮按鈕的縱向位置，將其放在螢幕高度的2/3處
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.topMargin = (screenHeight * 2 / 3) - floatingView.height / 2

        floatingView.layoutParams = layoutParams
    }
}