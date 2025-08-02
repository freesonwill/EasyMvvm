package arch.cayenne.lib.base.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import arch.cayenne.lib.base.R
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui._interface.IStatusBar
import arch.cayenne.lib.base.ui._interface.IView
import arch.cayenne.lib.base.ui.delegate.StatusBarDelegate
import arch.cayenne.lib.base.ui.delegate.UIBindDelegate
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModelForClass
import kotlin.reflect.KClass

abstract class BaseBottomSheetFragment<VM : BaseViewModel, VB : ViewBinding> :
    BottomSheetDialogFragment(), IView {

    protected val TAG by lazy { this::class.java.simpleName }
    private var mScrollY: Int? = null
    private var backgroundView: View? = null
    private var sheetContainer: View? = null
    private var isDismissing = false
    //#region VB,VM
    protected val mBinding: VB get() = uiBind.binding
    protected val mViewModel: VM get() = uiBind.viewModel
    abstract val vbClass: KClass<VB>
    abstract val vmClass: KClass<VM>
    private val uiBind by lazy { UIBindDelegate(uiOwner = this, vmProvider = ::createVM, vbProvider = ::createVB,) }

    protected open fun createVB(container: ViewGroup?): VB {
        return getViewBind(vbClass, container, false)
    }

    protected open fun createVM(): VM {
        return viewModelForClass(vmClass).value
    }

    //navigation跳转时是否保留view（true:保留；false：销毁）
    open val keepViewOnNavigation: Boolean = false
    //#endregion VB,VM
    //#endregion VB,VM
    //设置颜色，默认根据主题颜色设定
    private val statusBar: IStatusBar by lazy { StatusBarDelegate(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, theme)
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
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

            backgroundView = root.getChildAt(0).apply {
                visibility = View.INVISIBLE
//                setBackgroundColor(Color.BLACK)
//                alpha = 0.75f
                setOnClickListener {
                    if (isCancelable) {
                        dismiss()
                    }
                }
            }
            sheetContainer = root.findViewById<View?>(com.google.android.material.R.id.design_bottom_sheet).apply {
                visibility = View.INVISIBLE
            }
            // 彈出動畫
            playEnterAnimations()

        }

        return dialog
    }

    private fun setStatusBar() {
        StatusBarConfig.statusBarType = StatusBarMode.DEFAULT
        statusBar.setStatusBar(StatusBarConfig, mBinding.root)
        statusBar.configStatusBar().statusBarColor = R.color.black_75
    }


    protected open fun enterAnimation():Animation = AnimationUtils.loadAnimation(requireContext(),R.anim.slide_bottom_sheet_up)

    private fun playEnterAnimations() {
        sheetContainer?.let {  scv ->
            // bottom sheet 上滑動畫
            val sheetAnim = enterAnimation()
            sheetAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    backgroundView?.visibility = View.VISIBLE
                    scv.visibility = View.VISIBLE
                    mBinding.root.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animation?) {
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            scv.startAnimation(sheetAnim)
        }

    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        uiBind.onCreateView(inflater, container, savedInstanceState)
        setKeyboardEvent()
        return mBinding.root.apply {
            this.visibility = View.INVISIBLE
        }
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiBind.onViewCreated(view, savedInstanceState)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        removeDim()
        uiBind.onStart()
        setStatusBar()
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

    @CallSuper
    override fun onNewIntent(intent: Intent) {
        uiBind.onNewIntent(intent)
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

    override suspend fun createObserver() {
    }


    override fun dismiss() {
        if (isDismissing || sheetContainer == null) {
            superDismiss()
            return
        }
        isDismissing = true

        val sheetContainerSheetAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_bottom_sheet_down)
        sheetContainerSheetAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                backgroundView?.visibility = View.INVISIBLE
            }
            override fun onAnimationEnd(animation: Animation?) {
                try {
                    superDismiss()
                } catch (e: Exception) {
                    dismissAllowingStateLoss()
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        sheetContainer?.startAnimation(sheetContainerSheetAnim)
    }

    protected open fun superDismiss() {
        isDismissing = false
        super.dismiss()
    }

    protected fun removeDim() {
        dialog?.window?.setDimAmount(0f)
    }

    private fun setDim(amount: Float) {
        dialog?.window?.setDimAmount(amount)
    }
}