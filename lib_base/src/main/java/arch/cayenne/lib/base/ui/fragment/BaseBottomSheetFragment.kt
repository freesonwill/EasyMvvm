package arch.cayenne.lib.base.ui.fragment

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
import androidx.annotation.CallSuper
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import arch.cayenne.lib.base.R
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.ui.delegate.UIBindDelegate
import arch.cayenne.lib.base.ui._interface.IView
import org.koin.androidx.viewmodel.ext.android.viewModelForClass
import kotlin.reflect.KClass

abstract class BaseBottomSheetFragment<VM : BaseViewModel,VB : ViewBinding> : BottomSheetDialogFragment(), IView {

    private var mScrollY: Int? = null
    private lateinit var backgroundView: View
    private lateinit var sheetContainer: View
    private var isDismissing = false
    //#region VB,VM
    protected val mBinding: VB get() = uiBind.binding
    protected val mViewModel: VM get() = uiBind.viewModel
    abstract val vbClass: KClass<VB>
    abstract val vmClass: KClass<VM>
    private val uiBind by lazy {
        UIBindDelegate(
            uiOwner = this,
            vmProvider = ::createVM,
            vbProvider = ::createVB,
            keepViewOnNavigation = keepViewOnNavigation
        )
    }
    protected open fun createVB(container: ViewGroup?): VB {
        return getViewBind(vbClass, container, false)
    }

    protected open fun createVM(): VM {
        return viewModelForClass(vmClass).value
    }
    //navigation跳转时是否保留view（true:保留；false：销毁）
    open val keepViewOnNavigation:Boolean = false
    //#endregion VB,VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, theme)
    }

    override fun getTheme(): Int {
        return R.style.ArchBottomSheetDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : BottomSheetDialog(requireContext(), theme) {
            override fun onBackPressed() {
                this@BaseBottomSheetFragment.dismiss()
                super.onBackPressed()
            }
        }

        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val root = d.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.parent as ViewGroup

            backgroundView = root.getChildAt(0) // 通常是背景 View（透明灰）
            sheetContainer = root.findViewById(com.google.android.material.R.id.design_bottom_sheet) // sheet 本體

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

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        uiBind.onCreateView(inflater,container,savedInstanceState)
        setKeyboardEvent()
        return mBinding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiBind.onViewCreated(view,savedInstanceState)
    }
    @CallSuper
    override fun onStart() {
        super.onStart()
        uiBind.onStart()
    }
    @CallSuper
    override fun onResume() {
        super.onResume()
        uiBind.onResume()
    }
    @CallSuper
    override fun onPause() {
        super.onPause()
        uiBind.onPause()
    }
    @CallSuper
    override fun onStop() {
        super.onStop()
        uiBind.onStop()
    }
    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        uiBind.onDestroyView()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        uiBind.onDestroy()
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