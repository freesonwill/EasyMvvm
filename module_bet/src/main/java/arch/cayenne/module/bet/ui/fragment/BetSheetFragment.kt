package arch.cayenne.module.bet.ui.fragment

import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.common.ui.view.BlockSlideConstrainLayout
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_DISMISS
import arch.cayenne.module.bet.data.Config.VALUE_TO_RESULT
import arch.cayenne.module.bet.databinding.FragmentBetSheetBinding
import arch.cayenne.module.bet.viewmodel.BetSheetViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
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

        fun show(activity: FragmentActivity, withOtherSheetHide: ObjectAnimator? = null) {
            val manager = activity.supportFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                BetSheetFragment().show(manager, TAG)
            } else if (f is BasePreLoadBottomSheetFragment<*, *>) {
                if (withOtherSheetHide == null) {
                    f.customShow()
                } else {
                    f.customShow(withOtherSheetHide)
                }
            }
        }

        fun show(activity: FragmentActivity, doStart: () -> Unit) {
            val manager = activity.supportFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                BetSheetFragment().show(manager, TAG)
            } else if (f is BetSheetFragment) {
                f.setDoStart(doStart)
                f.customShow()
            }
        }

        fun show(activity: FragmentActivity, listener: ShowListener) {
            val manager = activity.supportFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                BetSheetFragment().show(manager, TAG)
            } else if (f is BetSheetFragment) {
                f.setShowListener(listener)
                f.customShow()
            }
        }
    }

    override val vbClass: KClass<FragmentBetSheetBinding>
        get() = FragmentBetSheetBinding::class
    override val vmClass: KClass<BetSheetViewModel>
        get() = BetSheetViewModel::class

    private var doStart: (() -> Unit)? = null
    private var listener: ShowListener? = null

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
        BetResultFragment.create(requireActivity())
        mBinding.root.setBlockSlideListener(object : BlockSlideConstrainLayout.BlockSlideListener {
            override fun getBlockingRect(): View? {
                if (!singleFragment.isHidden) {
                    return singleFragment.getBlockingSlideView()
                }
                return null
            }
        })
    }

    private fun initFragment() {
        childFragmentManager.beginTransaction()
            .add(mBinding.main.id, comboFragment, ComboBetFragment::class.java.simpleName)
//            .hide(comboFragment)
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
            } else if (result == VALUE_TO_RESULT) {
                val sheetAnimator = getHideAnimator() ?: return@setFragmentResultListener
                BetResultFragment.show(requireActivity(), sheetAnimator)
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
            if (it == 0 && !isDismissing) {
                customHide()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        mViewModel.removeSingleBet()
        super.onDismiss(dialog)
    }

    override fun customShow(other: ObjectAnimator?) {
        mViewModel.register()
        super.customShow(other)
        childFragmentManager.fragments.forEach {
            if (it is BetSheetListener) {
                it.doCustomShow()
            }
        }
    }

    fun setDoStart(doStart: (() -> Unit)?) {
        this.doStart = doStart
    }

    fun setShowListener(listener: ShowListener) {
        this.listener = listener
    }

    override fun playEnterAnimations(
        doStart: (() -> Unit)?,
        doCancel: (() -> Unit)?,
        doEnd: (() -> Unit)?
    ) {
        super.playEnterAnimations({
            doStart?.invoke()
            listener?.onShow()
        }, {
            mViewModel.cancel()
            listener?.onCancel()
            doCancel?.invoke()
        }, doEnd)
    }

    override fun playExitAnimations(doStart: (() -> Unit)?, doEnd: (() -> Unit)?) {
        lifecycleScope.launch {
            val type = mViewModel.getBetType()
            if (type != BetTypeEnum.COMBO) {
                listener?.onDismiss()
            }
        }
        super.playExitAnimations(doStart, doEnd)
    }

    override fun customHide() {
        lifecycleScope.launch {
            val type = mViewModel.getBetType()
            if (type != BetTypeEnum.COMBO) {
                listener?.onHide()
            }
        }
        mViewModel.unregister()
        mViewModel.removeSingleBet()
        super.customHide()
    }

    interface ShowListener {
        fun onShow()
        fun onCancel()
        fun onDismiss()
        fun onHide()
    }
}

interface BetSheetListener {
    fun dismiss(key: String = KEY_RESULT, value: String = VALUE_DISMISS)
    fun navToResult(key: String = KEY_RESULT, value: String = VALUE_TO_RESULT)
    fun doCustomHideEnd()
    fun doCustomShow() {

    }
    fun getBlockingSlideView(): View? {
        return null
    }
}