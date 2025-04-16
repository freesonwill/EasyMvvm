package arch.cayenne.module.bet.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import arch.cayenne.module.bet.viewmodel.FloatingButtonViewModel
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.module.bet.Config.KEY_RESULT
import arch.cayenne.module.bet.Config.VALUE_DISMISS
import arch.cayenne.module.bet.databinding.FragmentFloatingButtonBinding
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class FloatingButtonFragment : BaseFragment<FloatingButtonViewModel, FragmentFloatingButtonBinding>() {
    override val vbClass: KClass<FragmentFloatingButtonBinding> = FragmentFloatingButtonBinding::class
    override val vmClass: KClass<FloatingButtonViewModel> = FloatingButtonViewModel::class
    private var isShowBetSheet = false

    override fun initView(savedInstanceState: Bundle?) {
        setFloatingViewPosition(requireActivity().resources.displayMetrics.heightPixels)
    }

    override fun initListener() {
        mBinding.fab.setPerformClick {
            mViewModel.onBettingCount.value?.let { count ->
                isShowBetSheet = true
                parentFragmentManager.setFragmentResultListener(KEY_RESULT, viewLifecycleOwner) { resultKey, bundle ->
                    if (resultKey == KEY_RESULT) {
                        parentFragmentManager.clearFragmentResultListener(KEY_RESULT)
                        val dismissKey = bundle.getString(VALUE_DISMISS)
                        if (dismissKey == VALUE_DISMISS) {
                            isShowBetSheet = false
                            mViewModel.onBettingCount.value?.let {
                                setVisibility(it)
                            }
                        }
                    }
                }
                if (count == 1) {
                    lifecycleScope.launch {
                        val id = mViewModel.getSingleBetById()
                        BetSheetFragment.newInstance(id).show(parentFragmentManager)
                    }
                } else {
                    BetSheetFragment.newInstance().show(parentFragmentManager)
                }
                mBinding.root.visibility = View.GONE
            }
        }
    }

    override fun createObserver() {
        mViewModel.onBettingCount.observe(viewLifecycleOwner) {
            if (!isShowBetSheet) {
                setVisibility(it)
            }

        }
    }

    private fun setVisibility(count: Int) {
        if (count == 0) {
            mBinding.root.visibility = View.GONE
        } else {
            mBinding.fab.setCount(count)
            mBinding.root.visibility = View.VISIBLE
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