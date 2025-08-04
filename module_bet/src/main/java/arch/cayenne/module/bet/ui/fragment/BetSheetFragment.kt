package arch.cayenne.module.bet.ui.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.Config
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_DISMISS
import arch.cayenne.module.bet.databinding.FragmentBetSheetBinding
import arch.cayenne.module.bet.util.ViewHelper
import arch.cayenne.module.bet.viewmodel.BetSheetViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.reflect.KClass

class BetSheetFragment private constructor() :
    BaseBottomSheetFragment<BetSheetViewModel, FragmentBetSheetBinding>() {

    companion object {

        private const val TAG = "BetSheetFragment"

        fun create(activity: FragmentActivity) {
            val manager = activity.supportFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                BetSheetFragment().customCreate(activity, TAG)
            }
        }

        fun show(activity: FragmentActivity) {
            val manager = activity.supportFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                BetSheetFragment().show(manager, TAG)
            } else if (f is BaseBottomSheetFragment<*, *>) {
                f.customShow()
            }
        }
    }

    override val vbClass: KClass<FragmentBetSheetBinding>
        get() = FragmentBetSheetBinding::class
    override val vmClass: KClass<BetSheetViewModel>
        get() = BetSheetViewModel::class

    private lateinit var controller: NavController

    private val dismissObserver = Observer<String> { value ->
        val v = mBinding.root
        when (value) {
            VALUE_DISMISS -> customHide()
            Config.VALUE_SINGLE_TO_RESULT -> {
                ViewHelper.collapseView(v) {
                    controller.navigate(SingleBetFragmentDirections.actionSingleBetFragmentToBetResultFragment(), null)
                }
            }
            Config.VALUE_COMBO_TO_RESULT -> {
                ViewHelper.collapseView(v) {
                    controller.navigate(ComboBetFragmentDirections.actionComboBetFragmentToBetResultFragment(), null)
                }
            }
            Config.VALUE_RESULT_TO_SINGLE -> {
                ViewHelper.collapseView(v) {
                    controller.navigate(BetResultFragmentDirections.actionBetResultFragmentToSingleBetFragment(), null)
                }
            }
            Config.VALUE_RESULT_TO_COMBO -> {
                ViewHelper.collapseView(v) {
                    controller.navigate(BetResultFragmentDirections.actionBetResultFragmentToComboBetFragment(), null)
                }
            }
        }
    }

    private var lastLiveData: LiveData<String>? = null

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val contextThemeWrapper = ContextThemeWrapper(requireContext(), R.style.BetModuleTheme)
        return super.onGetLayoutInflater(savedInstanceState).cloneInContext(contextThemeWrapper)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            initMaxHeight()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initDestination()
        super.onViewCreated(view, savedInstanceState)
    }


    override fun initView(savedInstanceState: Bundle?) {

    }

    private fun initDestination() {
        setStartDestination(mViewModel.count)
    }

    override fun initListener() {
    }

    override fun onStart() {
        super.onStart()
        setFitToContents()
    }

    private fun setFitToContents() {
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
        bottomSheet?.let { sheet ->
            val behavior = BottomSheetBehavior.from(sheet)

            behavior.isDraggable = true
            behavior.skipCollapsed = true  // ← 允許收合
            behavior.isHideable = true      // ← 允許向下滑關閉
            behavior.isFitToContents = true
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            behavior.saveFlags = BottomSheetBehavior.SAVE_HIDEABLE
        }
    }

    private fun initMaxHeight() {
        val screenHeight = resources.displayMetrics.heightPixels
        val maxFragmentHeight = (screenHeight * 0.75).toInt()
        mBinding.root.maxHeight = maxFragmentHeight
    }

    private fun setStartDestination(size: Int) {
        val navController = NavHostFragment.findNavController(mBinding.mainNav.getFragment()).apply {
            controller = this
        }
        val navGraph = navController.navInflater.inflate(R.navigation.nav_bet)

        if (size <= 1) {
            navGraph.setStartDestination(R.id.singleBetFragment)
        } else {
            navGraph.setStartDestination(R.id.comboBetFragment)
        }
        navController.setGraph(navGraph, Bundle())
    }

    override suspend fun createObserver() {
        // navigation的fragment沒有收起彈窗方法，必須靠回調頂層bottom sheet收起彈窗
        val navController = NavHostFragment.findNavController(mBinding.mainNav.getFragment())
        navController.addOnDestinationChangedListener { _, destination, bundle ->
            showEnterAnim()
            removeLastObserver()
            handleDismissObserve(navController, destination.id)
        }
        var lastCount = 0
        mViewModel.betSheetSizeListener.observe(viewLifecycleOwner) {
            if (lastCount == it) return@observe
            if (isDismissing) {
                if (lastCount < 2 && it >= 2) {
                    if (checkCurrentDir(it)) {
                        removeLastObserver()
                        mBinding.root.postDelayed({
                            if (lastLiveData == null) {
                                setStartDestination(2)
                            }
                        }, 300L)
                    }
                } else if (it <= 1) {
                    if (checkCurrentDir(it)) {
                        removeLastObserver()
                        mBinding.root.postDelayed({
                            if (lastLiveData == null) {
                                setStartDestination(1)
                            }
                        }, 300L)
                    }
                }
            }
            lastCount = it
        }
    }

    private fun checkCurrentDir(size: Int): Boolean {
        return if (size <= 1) {
            controller.currentDestination?.label != "SingleBetFragment"
        } else {
            controller.currentDestination?.label != "ComboBetFragment"
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

    private fun showEnterAnim() {
        if (lastLiveData == null) return
        val v = mBinding.root
        if (v.translationY == 0f) return
        val h = v.height.toFloat()
        ViewHelper.expandView(v, h)
    }

    override fun superDismiss() {
        mViewModel.unregister()
        super.superDismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        mViewModel.removeSingleBet()
        super.onDismiss(dialog)
    }

    override fun customShow() {
        if (lastLiveData == null) {
            initDestination()
        }
        super.customShow()
    }

    override fun customHide(onEnd: (() -> Unit)?) {
        super.customHide {
            val f = mBinding.mainNav.getFragment<Fragment>().childFragmentManager.primaryNavigationFragment
            if (f is BetSheetListener) {
                f.doCustomHideEnd()
            }
        }
    }

    override fun setCustomCollapseSetting() {
        super.setCustomCollapseSetting()
        mViewModel.removeSingleBet()
    }
}

interface BetSheetListener {
    fun dismiss(key: String = KEY_RESULT, value: String = VALUE_DISMISS)
    fun showExitAnim(key: String = KEY_RESULT, value: String)
    fun doCustomHideEnd()
}