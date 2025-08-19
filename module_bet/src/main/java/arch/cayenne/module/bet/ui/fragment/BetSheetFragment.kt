package arch.cayenne.module.bet.ui.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_DISMISS
import arch.cayenne.module.bet.databinding.FragmentBetSheetBinding
import arch.cayenne.module.bet.viewmodel.BetSheetViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.reflect.KClass

class BetSheetFragment private constructor() :
    BasePreLoadBottomSheetFragment<BetSheetViewModel, FragmentBetSheetBinding>() {

    companion object {

        private const val TAG = "BetSheetFragment"

        fun create(activity: FragmentActivity) {
            val manager = activity.supportFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                BetSheetFragment().customAttach(activity, TAG)
            }
        }

        fun show(activity: FragmentActivity, showAnimEndListener: (() -> Unit)? = null) {
            val manager = activity.supportFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                BetSheetFragment().show(manager, TAG)
            } else if (f is BasePreLoadBottomSheetFragment<*, *>) {
                showAnimEndListener?.let {
                    f.setShowAnimEndListener(showAnimEndListener)
                }
                f.customShow()
            }
        }
    }

    override val vbClass: KClass<FragmentBetSheetBinding>
        get() = FragmentBetSheetBinding::class
    override val vmClass: KClass<BetSheetViewModel>
        get() = BetSheetViewModel::class

    private val singleFragment by lazy {
        SingleBetFragment()
    }

    private val comboFragment by lazy {
        ComboBetFragment()
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val contextThemeWrapper = ContextThemeWrapper(requireContext(), R.style.BetModuleTheme)
        return super.onGetLayoutInflater(savedInstanceState).cloneInContext(contextThemeWrapper)
    }

    override fun initView(savedInstanceState: Bundle?) {
        initFragment()
    }

    private fun initFragment() {
        childFragmentManager.beginTransaction()
            .add(mBinding.main.id, comboFragment, ComboBetFragment::class.java.simpleName)
            .hide(comboFragment)
            .add(mBinding.main.id, singleFragment, SingleBetFragment::class.java.simpleName)
            .commit()
    }

    override fun initListener() {
        setOnEndListener {
            childFragmentManager.fragments.forEach {
                if (it is BetSheetListener) {
                    it.doCustomHideEnd()
                }
            }
        }
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

    override suspend fun createObserver() {
        childFragmentManager.setFragmentResultListener(
            KEY_RESULT,
            viewLifecycleOwner
        ) { _, bundle ->
            val result = bundle.getString(KEY_RESULT)
            if (result == VALUE_DISMISS) {
                customHide()
            }
        }
        mViewModel.betSheetSizeListener.observe(viewLifecycleOwner) {
            if (it <= 1) {
                childFragmentManager.beginTransaction()
                    .hide(comboFragment)
                    .show(singleFragment)
                    .commit()
            } else {
                childFragmentManager.beginTransaction()
                    .hide(singleFragment)
                    .show(comboFragment)
                    .commit()
            }
            if (it == 0) {
                customHide()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        mViewModel.removeSingleBet()
        super.onDismiss(dialog)
    }

    override fun customShow() {
        mViewModel.register()
        super.customShow()
        childFragmentManager.fragments.forEach {
            if (it is BetSheetListener) {
                it.doCustomShow()
            }
        }
    }

    override fun customHide() {
        super.customHide()
        mViewModel.unregister()
    }

    override fun setCustomCollapseSetting() {
        super.setCustomCollapseSetting()
        mViewModel.removeSingleBet()
    }


}

interface BetSheetListener {
    fun dismiss(key: String = KEY_RESULT, value: String = VALUE_DISMISS)
    fun doCustomHideEnd()
    fun doCustomShow() {

    }
}