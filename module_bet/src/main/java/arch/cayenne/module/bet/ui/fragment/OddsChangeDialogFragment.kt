package arch.cayenne.module.bet.ui.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.fragment.BasePositionDialogFragment
import arch.cayenne.lib.base.ui.fragment.dim.DimView
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.bet.data.OddsChangeEnum
import arch.cayenne.module.bet.databinding.FragmentOddsChangeBinding
import arch.cayenne.module.bet.viewmodel.OddsChangeViewModel
import kotlin.reflect.KClass

class OddsChangeDialogFragment private constructor() :
    BasePositionDialogFragment<OddsChangeViewModel, FragmentOddsChangeBinding>() {

    companion object {
        private const val RECT_KET = "rect_key"

        fun instance(rect: Rect): OddsChangeDialogFragment {
            val fragment = OddsChangeDialogFragment()
            val bundle = Bundle().apply {
                putParcelable(RECT_KET, rect)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override val vbClass: KClass<FragmentOddsChangeBinding> = FragmentOddsChangeBinding::class
    override val vmClass: KClass<OddsChangeViewModel> = OddsChangeViewModel::class

    private var dimView: View? = null
    private var bottomDimView: View? = null
    private var dismissListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun cancel() {
                if (!mBinding.root.isEnabled) return
                // 讓系統其他地方調用 dismiss 時也會觸發動畫
                if (mBinding.root.translationX == 0f) {
                    doExitAnim()
                } else {
                    super.dismiss()
                }
            }
        }.apply {
            window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION)
        }
    }

    override val dialogBackground: Drawable?
        get() = null

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.ivAny.isSelected = true
    }

    override fun initListener() {
        mBinding.clAny.setOnClickListener {
            handleSelection(OddsChangeEnum.ANY)
        }

        mBinding.clBetter.setOnClickListener {
            handleSelection(OddsChangeEnum.BETTER)
        }

        mBinding.clNone.setOnClickListener {
            handleSelection(OddsChangeEnum.NO_CHANGE)
        }
    }


    private fun handleSelection(selection: OddsChangeEnum) {
        mViewModel.saveOddsChange(selection)
        doExitAnim()
    }

    override suspend fun createObserver() {
        super.createObserver()
        mViewModel.oddsChangeListener.observe(viewLifecycleOwner) {
            mBinding.ivAny.isSelected = it == OddsChangeEnum.ANY
            mBinding.ivBetter.isSelected = it == OddsChangeEnum.BETTER
            mBinding.ivNone.isSelected = it == OddsChangeEnum.NO_CHANGE
        }
    }

    override fun onStart() {
        removeDim()
        super.onStart()
        initDim()
    }

    override fun setDialogPosition(w: Window) {

        val screenHeight = resources.displayMetrics.heightPixels
        w.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        w.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        val rect = requireArguments().getParcelable<Rect>(RECT_KET) ?: return
        val positionX = rect.right

        mBinding.root.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val isShowTop = rect.bottom + mBinding.content.measuredHeight + mBinding.triangleTop.measuredHeight + 10.dp2px < screenHeight

        val layoutParams = w.attributes
        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.x = 10.dp2px
        layoutParams.y = if (isShowTop) {
            rect.bottom + 10.dp2px
        } else {
            rect.top - mBinding.content.measuredHeight - mBinding.triangleBottom.measuredHeight - 10.dp2px
        }
        w.attributes = layoutParams

        mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // 先設定 triangle 位置
                setTrianglePosition(isShowTop, positionX)

                mBinding.root.post {

                    val triangle = if (isShowTop) mBinding.triangleTop else mBinding.triangleBottom

                    // 動畫初始狀態
                    mBinding.root.pivotX = triangle.x + triangle.width / 2
                    mBinding.root.pivotY = if (isShowTop) 0f else mBinding.root.height.toFloat()
                    mBinding.root.scaleX = 0f
                    mBinding.root.scaleY = 0f
                    mBinding.root.alpha = 0f


                    // 開始動畫
                    val animator = mBinding.root.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setDuration(200)
                        .setInterpolator(android.view.animation.DecelerateInterpolator())
                        .withStartAction {
                            mBinding.root.visibility = View.VISIBLE
                        }

                    showDim()
                    mBinding.root.post {
                        animator.start()
                    }
                }

            }
        })
    }

    private fun initDim() {

        if (dimView != null && bottomDimView != null) return

        val screenHeight = resources.displayMetrics.heightPixels
        val statusHeight = ViewUtils.getStatusBarHeight(requireContext())
        val navigationHeight = ViewUtils.getNavigationBarHeight(requireContext())

        val dimV = DimView(requireContext()).apply {
            setBackgroundColor(Color.BLACK)
            alpha = 0f
            dimView = this
        }

        val dimParams = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        dimParams.token = dialog?.window?.decorView?.windowToken

        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(dimV, dimParams)

        val rect = requireArguments().getParcelable<Rect>(RECT_KET) ?: return
        val centerX = rect.centerX()
        val centerY = if (Build.VERSION.SDK_INT >= 30) rect.centerY() else rect.centerY() + statusHeight
        val width = rect.width()
        val height = rect.height()

        dimV.setRect(centerX, centerY, width, height, 6.dp2px.toFloat())

        if (Build.VERSION.SDK_INT < 30) {
            val bottomDimV = View(requireContext()).apply {
                setBackgroundColor(Color.BLACK)
                alpha = 0f
                bottomDimView = this
                translationY = screenHeight.toFloat()
            }
            val bottomDimParams = WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                screenHeight + navigationHeight,
                WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
            )
            bottomDimParams.token = dialog?.window?.decorView?.windowToken
            bottomDimParams.gravity = Gravity.TOP or Gravity.START
            windowManager.addView(bottomDimV, bottomDimParams)
        }
    }

    private fun showDim() {
        dimView?.alpha = 0.75f
        bottomDimView?.alpha = 0.75f
    }

    private fun clearDim() {
        val windowManager = requireActivity().windowManager
        dimView?.let {
            windowManager.removeView(it)
            dimView = null
        }
        bottomDimView?.let {
            windowManager.removeView(it)
            bottomDimView = null
        }
    }


    private fun setTrianglePosition(isShowTop: Boolean, targetPositionX: Int) {
        val trangle = if (isShowTop) {
            mBinding.triangleBottom.isVisible = false
            mBinding.triangleTop
        } else {
            mBinding.triangleTop.isVisible = false
            mBinding.triangleBottom
        }
        val triangleLocation = IntArray(2)
        trangle.getLocationOnScreen(triangleLocation)
        val px = targetPositionX - triangleLocation.first() - trangle.width
        val params = trangle.layoutParams as ConstraintLayout.LayoutParams
        params.rightMargin = params.rightMargin - px + 6.dp2px
        trangle.layoutParams = params
    }

    private fun doExitAnim() {
        if (!mBinding.root.isEnabled) return
        mBinding.root.isEnabled = false

        clearDim()

        mBinding.root.animate()
            .scaleX(0f)
            .scaleY(0f)
            .alpha(0f)
            .setDuration(200)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .withEndAction {
                super.dismiss()
            }
            .withStartAction {
                dismissListener?.invoke()
            }
            .start()
    }

    fun setOnDismissListener(listener: () -> Unit) {
        this.dismissListener = listener
    }

    override fun onDestroy() {
        clearDim()
        super.onDestroy()
    }
}