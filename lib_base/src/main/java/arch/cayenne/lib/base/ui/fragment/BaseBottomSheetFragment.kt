package arch.cayenne.lib.base.ui.fragment

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.R
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui._interface.IStatusBar
import arch.cayenne.lib.base.ui._interface.IView
import arch.cayenne.lib.base.ui.delegate.StatusBarDelegate
import arch.cayenne.lib.base.ui.delegate.UIBindDelegate
import arch.cayenne.lib.base.ui.gesture.TikTokGesture
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModelForClass
import java.lang.ref.WeakReference
import java.lang.reflect.Field
import kotlin.reflect.KClass


abstract class BaseBottomSheetFragment<VM : BaseViewModel, VB : ViewBinding> :
    BottomSheetDialogFragment(), IView {

    protected val TAG by lazy { this::class.java.simpleName }
    private var mScrollY: Int? = null
    protected var backgroundView: View? = null
    protected var sheetContainer: View? = null
    protected var isDismissing = false
    //#region VB,VM
    protected val mBinding: VB get() = uiBind.binding
    protected val mViewModel: VM get() = uiBind.viewModel
    abstract val vbClass: KClass<VB>
    abstract val vmClass: KClass<VM>
    private val uiBind by lazy { UIBindDelegate(
        uiOwner = this,
        vmProvider = ::createVM,
        vbProvider = ::createVB
    ) }

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
                if (!isResumed) {
                    super.onBackPressed()
                }
            }

            override fun onStart() {
                super.onStart()
                hideSheet()
            }

            override fun hide() {
            }
        }

        dialog.setOnShowListener {
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

    protected fun playEnterAnimations() {
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
                    setRvTouch()
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            scv.startAnimation(sheetAnim)
        }
    }

    protected open fun playExitAnimations() {
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
        setBehavior(view)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        uiBind.onStart()
        setSheetContainer()
        setBackGroundOnclick()
        setDim(0.75f)
        setStatusBar()
        setGesture()
    }

    protected open fun setBackGroundOnclick() {
        backgroundView?.setOnClickListener {
            if (isCancelable) {
                dismiss()
            }
        }
    }

    protected fun hideSheet() {
        backgroundView?.visibility = View.INVISIBLE
        sheetContainer?.visibility = View.INVISIBLE
    }

    private fun setSheetContainer() {
        val d = dialog as AppCompatDialog
        val root = d.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.parent as ViewGroup

        backgroundView = root.getChildAt(0)
        sheetContainer = root.findViewById(com.google.android.material.R.id.design_bottom_sheet)
    }

    protected open fun setBehavior(view: View) {
        val bottomSheet = (view.parent as? View) ?: return
        val params = bottomSheet.layoutParams as? CoordinatorLayout.LayoutParams ?: return
        val scrollBehavior = ScrollBottomSheetBehavior<View>(requireContext(), null)
        params.behavior = scrollBehavior
        bottomSheet.layoutParams = params
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

    override suspend fun createObserver() {
    }

    fun show(manager: FragmentManager) {
        val f = manager.findFragmentByTag(this::class.java.simpleName)
        if (f == null || !f.isAdded) {
            super.show(manager, this::class.java.simpleName)
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val f = manager.findFragmentByTag(tag)
        if (f == null || !f.isAdded) {
            super.show(manager, tag)
        }
    }


    override fun dismiss() {
        if (isDismissing || sheetContainer == null) {
            superDismiss()
            return
        }
        isDismissing = true
        playExitAnimations()
    }

    open fun superDismiss() {
        isDismissing = true
        super.dismiss()
    }

    protected fun hideDim() {
        dialog?.window?.setDimAmount(0f)
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    protected fun showDim() {
        dialog?.window?.setDimAmount(0.75f)
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    protected fun setDim(amount: Float) {
        dialog?.window?.setDimAmount(amount)
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setRvTouch() {
        val rv = findAllRecyclerViews(mBinding.root)
        rv.forEach {
            it.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    setScrollable(rv)
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                }

            })
        }
    }

    private fun setScrollable(recyclerView: RecyclerView) {
        val params = sheetContainer?.layoutParams
        if (params is CoordinatorLayout.LayoutParams) {
            val behavior = params.behavior
            if (behavior != null && behavior is BottomSheetBehaviorInterface) {
                behavior.setNestedScrollingChildRef(recyclerView)
            }
        }
    }

    private fun findAllRecyclerViews(root: View): List<RecyclerView> {
        val result = mutableListOf<RecyclerView>()

        if (root is RecyclerView) {
            result.add(root)
        }

        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                result.addAll(findAllRecyclerViews(root.getChildAt(i)))
            }
        }

        return result
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setGesture() {
        val v = mBinding.root
        val tikTokGesture = TikTokGesture(v)
        tikTokGesture.setListener(object : TikTokGesture.TikTokGestureListener {
            override fun onFlingToRight() {
                dialog?.onBackPressed()
            }

            override fun onHorizontalScroll(offsetX: Float) {
                sheetContainer?.translationY = offsetX * 1.5f
            }

            override fun onActionUp() {
                val translationY = sheetContainer?.translationY ?: 0f
                val height = sheetContainer?.height ?: 0
                if (translationY >= height / 2) {
                    dialog?.onBackPressed()
                } else {
                    resetSheetTranslation()
                }
            }
        })
    }

    private fun resetSheetTranslation() {
        sheetContainer?.let {
            ValueAnimator.ofFloat(it.translationY, 0f).apply {
                duration = 100
                addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    it.translationY = value
                }
                start()
            }
        }

    }
}

interface BottomSheetBehaviorInterface {
    fun setNestedScrollingChildRef(v: View)
}
open class ScrollBottomSheetBehavior<V : View>(context: Context, attrs: AttributeSet?) : BottomSheetBehavior<V>(context, attrs), BottomSheetBehaviorInterface {

    private var mNestedScrollingChildRef: WeakReference<View>? = null

    override fun setNestedScrollingChildRef(v: View) {
        val prev = mNestedScrollingChildRef?.get()
        if (prev === v) {
            return
        }

        // 更新本地引用
        mNestedScrollingChildRef = WeakReference(v)

        // 停掉上一個 child 的 nested scroll，避免競爭
        if (prev != null) {
            try {
                ViewCompat.stopNestedScroll(prev)
                ViewCompat.stopNestedScroll(prev, ViewCompat.TYPE_TOUCH)
                ViewCompat.stopNestedScroll(prev, ViewCompat.TYPE_NON_TOUCH)
            } catch (_: Throwable) {}
        }

        // 強制覆寫父類私有欄位 nestedScrollingChildRef
        setParentNestedChildByReflection(v)
    }

    private fun updateNestedScrollingChildRef(v: View) {
        val prev = mNestedScrollingChildRef?.get()
        // 更新本地引用
        mNestedScrollingChildRef = WeakReference(v)

        // 停掉上一個 child 的 nested scroll，避免競爭
        if (prev != null) {
            try {
                ViewCompat.stopNestedScroll(prev)
                ViewCompat.stopNestedScroll(prev, ViewCompat.TYPE_TOUCH)
                ViewCompat.stopNestedScroll(prev, ViewCompat.TYPE_NON_TOUCH)
            } catch (_: Throwable) {}
        }

        // 強制覆寫父類私有欄位 nestedScrollingChildRef
        setParentNestedChildByReflection(v)
    }

    private fun setParentNestedChildByReflection(v: View) {
        try {
            val field: Field =
                BottomSheetBehavior::class.java.getDeclaredField("nestedScrollingChildRef")
            field.isAccessible = true
            field.set(this, WeakReference(v))
        } catch (_: Throwable) {
            // 版本差異時可加 log
        }
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        val accepted = super.onStartNestedScroll(
            coordinatorLayout, child, directTargetChild, target, axes, type
        )
        // 父類會在這裡重設 nestedScrollingChildRef → 再覆寫一次我們指定的 child
        mNestedScrollingChildRef?.get()?.let { updateNestedScrollingChildRef(it) }
        return accepted
    }

}