package arch.cayenne.lib.base.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowCompat
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.motion.MaterialBackOrchestrator

open class UnhideableBottomSheetDialog(context: Context, @StyleRes style: Int) : AppCompatDialog(context, style) {

    private lateinit var behavior: BottomSheetBehavior<FrameLayout>

    private lateinit var container: FrameLayout
    private lateinit var coordinator: CoordinatorLayout
    private lateinit var bottomSheet: FrameLayout

    private var cancelable = true
    private var canceledOnTouchOutside = true
    private var canceledOnTouchOutsideSet = false
    private var backOrchestrator: MaterialBackOrchestrator? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.let { window ->
            window.clearFlags(LayoutParams.FLAG_DIM_BEHIND)

            window.setFlags(
                LayoutParams.FLAG_NOT_TOUCH_MODAL,
                LayoutParams.FLAG_NOT_TOUCH_MODAL
            )
            window.addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val layoutParams = window.attributes
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

            layoutParams.gravity = android.view.Gravity.BOTTOM
            window.attributes = layoutParams
        }
    }

    fun showDialog() {
        window?.let {
            it.decorView.visibility = View.VISIBLE
            val layoutParams = it.attributes
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

            it.attributes = layoutParams
        }
    }

    fun hideDialog() {
        window?.let {
            it.decorView.visibility = View.INVISIBLE
            val layoutParams = it.attributes
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

            it.attributes = layoutParams
        }
    }

    override fun setContentView(view: View) {
        super.setContentView(wrapInBottomSheet(0, view, null))
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.setContentView(wrapInBottomSheet(0, view, params))
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)
        if (this.cancelable != cancelable) {
            this.cancelable = cancelable
            if (::behavior.isInitialized) {
                behavior.isHideable = cancelable
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (::behavior.isInitialized && behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        window?.let { window ->
            if (::container.isInitialized) {
                container.fitsSystemWindows = true
            }
            if (::coordinator.isInitialized) {
                coordinator.fitsSystemWindows = true
            }
            WindowCompat.setDecorFitsSystemWindows(window, true)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onDetachedFromWindow() {
        backOrchestrator?.stopListeningForBackCallbacks()
        super.onDetachedFromWindow()
    }

    override fun cancel() {
        val behavior = behavior
        if (behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun setCanceledOnTouchOutside(cancel: Boolean) {
        super.setCanceledOnTouchOutside(cancel)
        if (cancel && !cancelable) {
            cancelable = true
        }
        canceledOnTouchOutside = cancel
        canceledOnTouchOutsideSet = true
    }

    /** Creates the container layout which must exist to find the behavior */
    @SuppressLint("RestrictedApi")
    private fun ensureContainerAndBehavior(): FrameLayout {
        if (!::container.isInitialized) {
            container = View.inflate(context, R.layout.design_bottom_sheet_dialog, null) as FrameLayout

            coordinator = container.findViewById(R.id.coordinator)
            bottomSheet = container.findViewById(R.id.design_bottom_sheet)

            behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.isHideable = cancelable
            backOrchestrator = MaterialBackOrchestrator(behavior, bottomSheet)
        }
        return container
    }

    private fun wrapInBottomSheet(
        @LayoutRes layoutResId: Int,
        view: View?,
        params: ViewGroup.LayoutParams?
    ): View {
        ensureContainerAndBehavior()
        val coordinator = container.findViewById<CoordinatorLayout>(R.id.coordinator)
        var newView = view
        if (layoutResId != 0 && newView == null) {
            newView = layoutInflater.inflate(layoutResId, coordinator, false)
        }

        bottomSheet.removeAllViews()
        if (params == null) {
            bottomSheet.addView(newView)
        } else {
            bottomSheet.addView(newView, params)
        }
        return container
    }
}