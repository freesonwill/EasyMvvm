package arch.cayenne.lib.base.ui.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.CallSuper
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.R
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

abstract class BasePreLoadBottomSheerFragment<VM : BaseViewModel, VB : ViewBinding> : BaseBottomSheetFragment<VM, VB>() {

    private var onEndListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : BottomSheetDialog(requireContext(), theme) {
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
                dialog?.window?.decorView?.visibility = View.INVISIBLE
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
        dialog?.window?.decorView?.visibility = View.VISIBLE

        sheetContainer?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.isHideable = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun setBehavior(view: View) {
        val bottomSheet = (view.parent as? View) ?: return
        val params = bottomSheet.layoutParams as? CoordinatorLayout.LayoutParams ?: return
        val unhideableBehavior = UnhideableBottomSheetBehavior<View>(requireContext(), null)
        unhideableBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if ((newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN)) {
                    if (this@BasePreLoadBottomSheerFragment.isResumed) {
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

        if (sheetContainer?.visibility == View.VISIBLE) {
            onEndListener?.invoke()
            dialog?.window?.decorView?.visibility = View.INVISIBLE

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

    override fun playExitAnimations() {
        val sheetContainerSheetAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_bottom_sheet_down)
        sheetContainerSheetAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                backgroundView?.visibility = View.INVISIBLE
            }
            override fun onAnimationEnd(animation: Animation?) {
                setCustomCollapseSetting()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        sheetContainer?.startAnimation(sheetContainerSheetAnim)
    }

    fun customDetach() {
        superDismiss()
    }

    fun customAttach(activity: FragmentActivity, newTag: String) {
        show(activity.supportFragmentManager, newTag)
    }

    fun customAttach(fragment: Fragment, newTag: String) {
        show(fragment.requireActivity().supportFragmentManager, newTag)
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                superDismiss()
            }
        })
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

    @CallSuper
    open fun customHide()  {
        if (!isDismissing) {
            isDismissing = true
            playExitAnimations()
        } else {
            setCustomCollapseSetting()
        }
    }

    fun setOnEndListener(listener: (() -> Unit)?) {
        onEndListener = listener
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