package arch.cayenne.lib.base.ui.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.R
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui._interface.IStatusBar
import arch.cayenne.lib.base.ui._interface.IView
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.delegate.StatusBarDelegate
import arch.cayenne.lib.base.ui.delegate.UIBindDelegate
import arch.cayenne.lib.base.ui.fragment.dim.DimController
import arch.cayenne.lib.base.ui.fragment.dim.DimInterface
import arch.cayenne.lib.base.ui.gesture.TikTokGesture
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModelForClass
import java.lang.ref.WeakReference
import java.lang.reflect.Field
import kotlin.math.abs
import kotlin.reflect.KClass


abstract class BaseBottomSheetFragment<VM : BaseViewModel, VB : ViewBinding> :
    BottomSheetDialogFragment(), IView, DimInterface {

    protected val TAG by lazy { this::class.java.simpleName }
    protected var backgroundView: View? = null
    protected var sheetContainer: View? = null
    var isDismissing = false
        protected set
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

    protected var otherViewAnimation: ObjectAnimator? = null
    private val dimController by lazy { DimController.getInstance(this) }
    protected open var isHorizontalGestureEnable = true
    protected open var isVerticalGestureEnable = true
    private var popupAnimatorSet: AnimatorSet? = null

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

        return dialog.apply {
            window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL)
        }
    }

    private fun setStatusBar() {
        StatusBarConfig.statusBarType = StatusBarMode.DEFAULT
        statusBar.setStatusBar(StatusBarConfig, mBinding.root)
        statusBar.configStatusBar().statusBarColor = R.color.black_75
    }


    protected open fun enterAnimation(): Animation = AnimationController[AnimType.popupEnter]!!.toAnimation()

    protected open fun exitAnimation(): Animation = AnimationController[AnimType.popupExit]!!.toAnimation()

    protected open fun playEnterAnimations(
        doStart: (() -> Unit)? = null,
        doEnd: (() -> Unit)? = null
    ) {
        prepareShowDim()
        val sheet = sheetContainer ?: return
        val otherSheetAnimator = otherViewAnimation

        val sheetAnim = enterAnimation()
        val offY = sheet.translationY
        val startY = sheet.height.toFloat()
        if (offY != startY) {
            sheet.translationY = sheet.height.toFloat()
        }
        val animation = ObjectAnimator.ofFloat(sheet, "translationY", sheet.height.toFloat(), 0f)
        val animatorSet = AnimatorSet().apply {
            duration = sheetAnim.duration
            interpolator = sheetAnim.interpolator
            if (otherSheetAnimator == null) {
                play(animation)
            } else {
                playTogether(animation, otherSheetAnimator)
            }
            doOnStart {
                backgroundView?.visibility = View.VISIBLE
                sheet.visibility = View.VISIBLE
                mBinding.root.visibility = View.VISIBLE
                mBinding.root.post {
                    showDim()
                    doStart?.invoke()
                }
            }
            doOnEnd {
                doEnd?.invoke()
                setRvTouch()
                otherViewAnimation = null
                popupAnimatorSet = null
            }
        }
        popupAnimatorSet?.cancel()
        popupAnimatorSet = animatorSet
        sheet.post {
            animatorSet.start()
        }
    }

    protected open fun playExitAnimations(
        doStart: (() -> Unit)? = {
            backgroundView?.visibility = View.INVISIBLE
        },
        doEnd: (() -> Unit)? = {
            try {
                superDismiss()
            } catch (_: Exception) {
                dismissAllowingStateLoss()
            }
        }
    ) {
        val sheet = sheetContainer ?: return

        val sheetContainerSheetAnim = exitAnimation()

        val sheetAnimator = ObjectAnimator.ofFloat(sheet, "translationY", sheet.translationY, sheet.height.toFloat())

        val dimAnimator = dimController.getHideAnimator()

        val animatorSet = AnimatorSet().apply {
            playTogether(sheetAnimator, dimAnimator)
            duration = sheetContainerSheetAnim.duration
            interpolator = sheetContainerSheetAnim.interpolator
            doOnStart {
                doStart?.invoke()
            }
            doOnEnd {
                doEnd?.invoke()
                popupAnimatorSet = null
            }
        }
        popupAnimatorSet?.cancel()
        popupAnimatorSet = animatorSet
        sheet.post {
            animatorSet.start()
        }
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        uiBind.onCreateView(inflater, container, savedInstanceState)
        return mBinding.root.apply {
            this.visibility = View.INVISIBLE
        }
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiBind.onViewCreated(view, savedInstanceState)
        setBehavior(view)
        setBehaviorOnScroll(view)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        uiBind.onStart()
        mBinding.root.isFocusable = true
        mBinding.root.isFocusableInTouchMode = true
        mBinding.root.isClickable = true
        setSheetContainer()
        setBackGroundOnclick()
        initDim()
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

    private fun setBehaviorOnScroll(view: View) {
        if (!isVerticalGestureEnable) return
        val bottomSheet = (view.parent as? View) ?: return
        val params = bottomSheet.layoutParams as? CoordinatorLayout.LayoutParams ?: return
        val scrollBehavior = params.behavior as? ScrollBottomSheetBehavior ?: return
        scrollBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            var isDragging = false
            var offsetY = 0f
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    isDragging = true
                    offsetY = bottomSheet.y
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_EXPANDED || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    isDragging = false
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (isDragging) {
                    val y = bottomSheet.y - offsetY
                    Log.d("abcd", "${this@BaseBottomSheetFragment.javaClass.simpleName}   $y")
                    setDimByScroll(abs(y.toInt()))
                }
            }
        })
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

    override suspend fun createObserver() {
    }

    fun show(manager: FragmentManager) {
        val f = manager.findFragmentByTag(this::class.java.simpleName)
        if (f == null || !f.isAdded) {
            super.show(manager, this::class.java.simpleName)
        }
    }

    fun showWithOtherSheetDialogHide(manager: FragmentManager, animator: ObjectAnimator?) {
        otherViewAnimation = animator
        show(manager)
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

    private fun initDim() {
        dialog?.window?.setDimAmount(0f)
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    }

    override fun getHostFragment(): Fragment {
        return this
    }

    override fun getIsDismissing(): Boolean {
        return isDismissing
    }

    protected open fun getHideAnimator(): ObjectAnimator? {
        val sheet = sheetContainer ?: return null
        val anim = exitAnimation()
        return ObjectAnimator.ofFloat(
            sheet, "translationY", 0f, sheet.height.toFloat()
        ).apply {
            duration = anim.duration
            interpolator = anim.interpolator
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                sheet.translationY = value
            }
            doOnEnd {
                superDismiss()
            }
        }
    }

    private fun setRvTouch() {
        val rv = findAllRecyclerViews(mBinding.root)
        rv.forEach {
            it.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    if (rv.isNestedScrollingEnabled) {
                        setScrollable(rv)
                    }
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
        if (!isHorizontalGestureEnable) return
        val v = mBinding.root
        val tikTokGesture = TikTokGesture(v)
        tikTokGesture.setListener(object : TikTokGesture.TikTokGestureListener {
            override fun onFlingToRight() {
                dialog?.onBackPressed()
            }

            override fun onHorizontalScroll(offsetX: Float) {
                sheetContainer?.let {
                    val offset = (offsetX * 1.3).toInt()
                    it.scrollTo(0, -offset)
                    setDimByScroll(offset)
                }
            }

            override fun onActionUp() {
                sheetContainer?.let {
                    val offsetY = abs(it.scrollY)
                    if (offsetY == 0) return

                    val height = it.height
                    if (offsetY >= height / 2) {
                        dialog?.onBackPressed()
                    } else {
                        resetSheetTranslation()
                    }
                }

            }
        })
    }

    private fun resetSheetTranslation() {
        sheetContainer?.let {
            ValueAnimator.ofInt(it.scrollY, 0).apply {
                duration = 100
                addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    it.scrollY = value
                    setDimByScroll(abs(value))
                }
                start()
            }
        }
    }

    private fun setDimByScroll(scrollY: Int) {
        val h = sheetContainer?.height ?: return
        val dimAlpha = DimController.TARGET_DIM
        val targetDim = dimAlpha - dimAlpha * (scrollY.toFloat() / h.toFloat())
        dimController.setDimAlpha(targetDim)
    }

    protected fun hideDim() {
        if (dimController.findAnyShowing(this) || !isDismissing) {
            return
        }
        dimController.hideDim()
    }

    protected fun prepareShowDim() {
        dimController.prepareShowDim()
    }

    protected fun showDim() {
        if (isDismissing) return
        dimController.showDim()
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
            } catch (_: Throwable) {
            }
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
            } catch (_: Throwable) {
            }
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