package com.walisport.app.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.walisport.app.databinding.FragmentFloatingButtonBinding
import com.walisport.app.viewmodel.FloatingButtonViewModel
import com.walisport.lib_base.ui.BaseFragment
import com.walisport.lib_base.ui.viewBind
import org.koin.androidx.viewmodel.ext.android.viewModel

class FloatingButtonFragment : BaseFragment<FloatingButtonViewModel, FragmentFloatingButtonBinding>() {

    override val mBinding: FragmentFloatingButtonBinding by viewBind()
    override val mViewModel: FloatingButtonViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        setFloatingViewPosition(requireActivity().resources.displayMetrics.heightPixels)
    }

    override fun initListener() {
        mBinding.fab.setPerformClick {
            // TODO 跳转到投注记录
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

    override fun lazyLoadData() {
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