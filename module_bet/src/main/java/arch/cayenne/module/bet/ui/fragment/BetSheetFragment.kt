package arch.cayenne.module.bet.ui.fragment

import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.animation.addListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.Config
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_DISMISS
import arch.cayenne.module.bet.databinding.FragmentBetSheetBinding
import arch.cayenne.module.bet.viewmodel.BetSheetViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class BetSheetFragment private constructor() :
    BaseBottomSheetFragment<BetSheetViewModel, FragmentBetSheetBinding>() {

    companion object {

        /***
         * 調起投注彈窗
         * 調起前需先將注單加入到資料庫 (BetBean)
         */
        fun newInstance(): BetSheetFragment {
            return BetSheetFragment()
        }
    }

    override val vbClass: KClass<FragmentBetSheetBinding>
        get() = FragmentBetSheetBinding::class
    override val vmClass: KClass<BetSheetViewModel>
        get() = BetSheetViewModel::class

    private val dismissObserver = Observer<String> { value ->
        if (value == VALUE_DISMISS) {
            dismiss()
        }
    }

    private var lastLiveData: LiveData<String>? = null
    private var isAnimating: Boolean = false

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val contextThemeWrapper = ContextThemeWrapper(requireContext(), R.style.BetModuleTheme)
        return super.onGetLayoutInflater(savedInstanceState).cloneInContext(contextThemeWrapper)
    }

    override fun initView(savedInstanceState: Bundle?) {
        initMaxHeight()
        setFitToContents()
        lifecycleScope.launch {
            mViewModel.getSelectionSize().let { size ->
                if (size == 0) {
                    dismiss()
                } else {
                    setStartDestination(size)
                }
            }
        }
    }

    override fun initListener() {
    }

    private fun setFitToContents() {
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
        bottomSheet?.let { sheet ->
            val behavior = BottomSheetBehavior.from(sheet)

            behavior.isDraggable = false
            behavior.skipCollapsed = false  // ← 允許收合
            behavior.isHideable = false      // ← 允許向下滑關閉
            behavior.isFitToContents = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.saveFlags = BottomSheetBehavior.SAVE_ALL
        }
    }

    private fun initMaxHeight() {
        val screenHeight = resources.displayMetrics.heightPixels
        val maxFragmentHeight = (screenHeight * 0.75).toInt()
        mBinding.root.maxHeight = maxFragmentHeight
    }

    private fun setStartDestination(size: Int) {
        val navController = NavHostFragment.findNavController(mBinding.mainNav.getFragment())
        val navGraph = navController.navInflater.inflate(R.navigation.nav_bet)

        if (size == 1) {
            navGraph.setStartDestination(R.id.singleBetFragment)
        } else {
            navGraph.setStartDestination(R.id.comboBetFragment)
        }
        navController.setGraph(navGraph, Bundle())
    }

    override fun createObserver() {
        // navigation的fragment沒有收起彈窗方法，必須靠回調頂層bottom sheet收起彈窗
        val navController = NavHostFragment.findNavController(mBinding.mainNav.getFragment())
        navController.addOnDestinationChangedListener { _, destination, bundle ->
            if (bundle != null && bundle.containsKey(Config.KEY_NON_ANIM)) {
                bundle.remove(Config.KEY_NON_ANIM)
            } else {
                animateLayoutChange()
            }
            removeLastObserver()
            handleDismissObserve(navController, destination.id)
        }
    }

    private fun removeLastObserver() {
        lastLiveData?.removeObserver(dismissObserver)
        lastLiveData = null
    }

    private fun handleDismissObserve(navController: NavController, destinationId: Int) {
        val backStackEntry = navController.getBackStackEntry(destinationId)

        lastLiveData = backStackEntry.savedStateHandle.getLiveData<String>(KEY_RESULT).apply {
            observe(viewLifecycleOwner, dismissObserver)
        }
    }

    private fun animateLayoutChange() {
        if (isAnimating || lastLiveData == null) return
        isAnimating = true
        val collapseAnim = ObjectAnimator.ofFloat(
            mBinding.root,
            "translationY",
            0f,
            mBinding.root.height.toFloat()
        )
        collapseAnim.duration = 300
        collapseAnim.interpolator = LinearInterpolator()
        collapseAnim.addListener(onEnd = {
            val expendAnim = ObjectAnimator.ofFloat(
                mBinding.root,
                "translationY",
                mBinding.root.height.toFloat(),
                0f
            )
            expendAnim.duration = 300
            expendAnim.interpolator = LinearInterpolator()
            expendAnim.addListener(
                onEnd = {
                    isAnimating = false
                }
            )
            expendAnim.start()
        })
        collapseAnim.start()

    }

    override fun superDismiss() {
        mViewModel.unregister()
        super.superDismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        mViewModel.removeSingleBet()
        parentFragmentManager.setFragmentResult(KEY_RESULT, Bundle().apply {
            putString(VALUE_DISMISS, VALUE_DISMISS)
        })
        super.onDismiss(dialog)
    }
}

interface BetSheetListener {
    fun dismiss(key: String = KEY_RESULT, value: String = VALUE_DISMISS)
}