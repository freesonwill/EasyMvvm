package arch.cayenne.lib.base.ui.fragment

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.annotation.CallSuper
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.view.UnhideableBottomSheetDialog
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.lang.ref.WeakReference

abstract class BasePreLoadBottomSheetFragment<VM : BaseViewModel, VB : ViewBinding> : BaseBottomSheetFragment<VM, VB>() {

    private var onEndListener: (() -> Unit)? = null
    private var unhideableDialog: UnhideableBottomSheetDialog? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : UnhideableBottomSheetDialog(requireContext(), theme) {
            override fun onBackPressed() {
                if (!isDismissing) {
                    customHide()
                }

                if (!isResumed) {
                    super.onBackPressed()
                }
            }

            override fun onStart() {
                super.onStart()
                hideSheet()
                setCustomExpendSetting()
                hideDialog()
                isDismissing = true
            }

            var systemHide = false

            override fun show() {
                if (systemHide) {
                    systemHide = false
                    return
                }
                super.show()

            }

            override fun hide() {
                systemHide = true
            }
        }
        unhideableDialog = dialog
        return dialog
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBehavior(view)
    }

    override fun setBackGroundOnclick() {
        backgroundView?.setOnClickListener {
            if (isCancelable) {
                customHide()
            }
        }
    }

    private fun setCustomExpendSetting() {
        unhideableDialog?.showDialog()
        mBinding.root.post {
            sheetContainer?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.isHideable = true
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun setBehavior(view: View) {
        val bottomSheet = (view.parent as? View) ?: return
        val params = bottomSheet.layoutParams as? CoordinatorLayout.LayoutParams ?: return
        val unhideableBehavior = UnhideableBottomSheetBehavior<View>(requireContext(), null)
        unhideableBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if ((newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN)) {
                    if (this@BasePreLoadBottomSheetFragment.isResumed) {
                        isDismissing = true
                        customHide()
                    }

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
        params.behavior = unhideableBehavior
        bottomSheet.layoutParams = params
    }

    @CallSuper
    protected open fun setCustomCollapseSetting() {
        sheetContainer?.translationY = 0f

        onEndListener?.invoke()
        unhideableDialog?.hideDialog()
        mBinding.root.post {
            backgroundView?.visibility = View.INVISIBLE
            sheetContainer?.visibility = View.INVISIBLE
            mBinding.root.visibility = View.INVISIBLE

            sheetContainer?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.isHideable = false
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    override fun playExitAnimations(doStart: (() -> Unit)?, doEnd: (() -> Unit)?) {
        super.playExitAnimations({
            backgroundView?.visibility = View.INVISIBLE
        }, {
            setCustomCollapseSetting()
        })
    }

    fun customDetach() {
        superDismiss()
    }

    fun customAttach(activity: FragmentActivity, newTag: String) {
        show(activity.supportFragmentManager, newTag)
    }

    fun customAttach(fragment: Fragment, newTag: String) {
        show(fragment.childFragmentManager, newTag)
    }

    @CallSuper
    open fun customShow() {
        if (sheetContainer?.visibility == View.INVISIBLE) {
            isDismissing = true
        }
        if (isDismissing) {
            isDismissing = false
            setCustomExpendSetting()
            playEnterAnimations()
        }
    }

    fun customShow(other: ObjectAnimator) {
        if (sheetContainer?.visibility == View.INVISIBLE) {
            isDismissing = true
        }
        if (isDismissing) {
            isDismissing = false
            setCustomExpendSetting()
            otherViewAnimation = WeakReference(other)
            playEnterAnimationWithOtherSheetDialogEnd()
        }
    }

    @CallSuper
    open fun customHide()  {
        if (!isDismissing) {
            isDismissing = true
            this.playExitAnimations()
        } else {
            setCustomCollapseSetting()
        }
    }

    fun setOnEndListener(listener: (() -> Unit)?) {
        onEndListener = listener
    }

    override fun dismiss() {
        customHide()
    }

    override fun getHideAnimator(): ObjectAnimator? {
        val sheet = sheetContainer ?: return null
        return ObjectAnimator.ofFloat(
            sheet, "translationY", 0f, sheet.height.toFloat()
        ).apply {
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                sheet.translationY = value
            }
            doOnEnd {
                setCustomCollapseSetting()
            }
        }
    }
}

class UnhideableBottomSheetBehavior<V : View>(context: Context, attrs: AttributeSet?) :
    ScrollBottomSheetBehavior<V>(context, attrs) {

    // 覆寫 setState 方法，這是最直接的攔截點
    override fun setState(state: Int) {
        if (state == STATE_HIDDEN) {
            // 當狀態要變成 STATE_HIDDEN 時，我們強制將其設回 STATE_COLLAPSED
            super.setState(STATE_COLLAPSED)
        } else {
            super.setState(state)
        }
    }
}