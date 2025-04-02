package com.walisport.module.bet.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.walisport.module_bet.viewmodel.FloatingButtonViewModel
import com.walisport.lib_base.ui.BaseFragment
import com.walisport.lib_base.ui.viewBind
import com.walisport.module.bet.databinding.FragmentFloatingButtonBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FloatingButtonFragment : BaseFragment<FloatingButtonViewModel, FragmentFloatingButtonBinding>() {

    override val mBinding: FragmentFloatingButtonBinding by viewBind()
    override val mViewModel: FloatingButtonViewModel by viewModel()
    private var onClickListener: (() -> Unit)? = null

    override fun initView(savedInstanceState: Bundle?) {
        setFloatingViewPosition(requireActivity().resources.displayMetrics.heightPixels)
    }

    override fun initListener() {
        mBinding.fab.setPerformClick {
            onClickListener?.invoke()
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

    fun setOnClickListener(onClickListener: () -> Unit) {
        this.onClickListener = onClickListener
    }
}