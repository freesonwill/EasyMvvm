package arch.cayenne.lib.base.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import arch.cayenne.lib.base.R
import arch.cayenne.lib.base.ui.interface_.IView

abstract class BaseBottomSheetFragment<VB : ViewBinding> : BottomSheetDialogFragment(), IView {

    protected abstract val mBinding: VB
    private var mScrollY: Int? = null
    private lateinit var backgroundView: View
    private lateinit var sheetContainer: View
    private var isDismissing = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, theme)
    }

    override fun getTheme(): Int {
        return R.style.WlsBottomSheetDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)

        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val root = d.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.parent as ViewGroup

            backgroundView = root.getChildAt(0) // 通常是背景 View（透明灰）
            sheetContainer = root.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) // sheet 本體

            backgroundView.setOnClickListener {
                if (isCancelable) {
                    dismiss()
                }
            }

            // 彈出動畫
            playEnterAnimations()
        }

        return dialog
    }

    private fun playEnterAnimations() {
        // bottom sheet 上滑動畫
        val sheetAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_bottom_sheet_up)

        backgroundView.startAnimation(sheetAnim)
        sheetContainer.startAnimation(sheetAnim)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView(savedInstanceState)
        setScrollView()
        setKeyboardEvent()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        createObserver()
    }

    private fun setScrollView() {
        mBinding.root.let { view ->
            if (view is NestedScrollView) {
                view.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                    mScrollY = scrollY
                })
                mScrollY?.let {
                    view.post {
                        view.scrollTo(0, it)
                    }
                } ?: run {
                    mScrollY = 0
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setKeyboardEvent() {
        mBinding.root.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val manager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
            false
        }
    }

    fun show(manager: FragmentManager) {
        val f = manager.findFragmentByTag(this::class.java.simpleName)
        if (f == null || !f.isAdded) {
            super.show(manager, this::class.java.simpleName)
        }
    }

    override fun createObserver() {
    }


    override fun dismiss() {
        if (isDismissing) return
        isDismissing = true

        val sheetAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_bottom_sheet_down)
        sheetAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                try {
                    superDismiss()
                } catch (e: Exception) {
                    dismissAllowingStateLoss()
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        backgroundView.startAnimation(sheetAnim)
        sheetContainer.startAnimation(sheetAnim)
    }

    protected open fun superDismiss() {
        isDismissing = false
        super.dismiss()
    }
}