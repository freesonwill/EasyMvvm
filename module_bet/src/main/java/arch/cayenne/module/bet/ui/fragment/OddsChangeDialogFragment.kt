package arch.cayenne.module.bet.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.fragment.BasePositionDialogFragment
import arch.cayenne.lib.base.ui.fragment.dim.DimView
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.bet.data.OddsChangeEnum
import arch.cayenne.module.bet.databinding.FragmentOddsChangeBinding
import arch.cayenne.module.bet.viewmodel.OddsChangeViewModel
import kotlin.reflect.KClass

class OddsChangeDialogFragment constructor() :
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

    private var topDimView: View? = null
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
        mBinding.dim.visibility = View.INVISIBLE
        mBinding.main.visibility = View.INVISIBLE
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
        mBinding.dim.setOnClickListener {
            doExitAnim()
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
        digDim()
        super.onStart()
        initDim()
    }

    override fun initDialog() {
        super.initDialog()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            setWindowAnimations(0)
        }
    }

    override fun setDialogPosition(w: Window) {

        val screenHeight = resources.displayMetrics.heightPixels

        val rect = requireArguments().getParcelable<Rect>(RECT_KET) ?: return
        val positionX = rect.right

        mBinding.main.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val isShowTop =
            rect.bottom + mBinding.content.measuredHeight + mBinding.triangleTop.measuredHeight + 10.dp2px < screenHeight

        val layoutParams = mBinding.main.layoutParams as ConstraintLayout.LayoutParams

        layoutParams.topMargin = if (isShowTop) {
            rect.bottom + 10.dp2px
        } else {
            rect.top - mBinding.content.measuredHeight - mBinding.triangleBottom.measuredHeight - 10.dp2px
        }
        mBinding.main.layoutParams = layoutParams

        mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // 先設定 triangle 位置
                setTrianglePosition(isShowTop, positionX)

                mBinding.root.post {

                    val triangle = if (isShowTop) mBinding.triangleTop else mBinding.triangleBottom

                    // 動畫初始狀態
                    mBinding.main.pivotX = triangle.x + triangle.width / 2
                    mBinding.main.pivotY = if (isShowTop) 0f else mBinding.main.height.toFloat()
                    mBinding.main.scaleX = 0f
                    mBinding.main.scaleY = 0f
                    mBinding.main.alpha = 0f


                    // 開始動畫
                    val animator = mBinding.main.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setDuration(200)
                        .setInterpolator(android.view.animation.DecelerateInterpolator())
                        .withStartAction {
                            mBinding.dim.visibility = View.VISIBLE
                            mBinding.main.visibility = View.VISIBLE
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
        if (topDimView != null && bottomDimView != null) return
        mBinding.dim.alpha = 0f

        val screenHeight = resources.displayMetrics.heightPixels
        val statusHeight = ViewUtils.getStatusBarHeight(requireContext())
        val navigationHeight = ViewUtils.getNavigationBarHeight(requireContext())
        val decorViewHeight = requireActivity().window.decorView.height

        val topDimV = DimView(requireContext()).apply {
            setBackgroundColor(Color.BLACK)
            alpha = 0f
            topDimView = this
        }

        val topDimParams = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            statusHeight,
            WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        topDimParams.gravity = Gravity.TOP or Gravity.START
        topDimParams.token = dialog?.window?.decorView?.windowToken

        val windowManager = requireActivity().windowManager
        windowManager.addView(topDimV, topDimParams)

        val bottomDimV = View(requireContext()).apply {
            setBackgroundColor(Color.BLACK)
            alpha = 0f
            bottomDimView = this
        }

        val bottomDimParams = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            navigationHeight,
            WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        bottomDimParams.token = dialog?.window?.decorView?.windowToken
        bottomDimParams.gravity = Gravity.TOP or Gravity.START
        bottomDimParams.y = maxOf(screenHeight, decorViewHeight - navigationHeight)
        windowManager.addView(bottomDimV, bottomDimParams)
    }

    private fun digDim() {
        val rect = requireArguments().getParcelable<Rect>(RECT_KET) ?: return
        val centerX = rect.centerX()
        val centerY = rect.centerY()
        val width = rect.width()
        val height = rect.height()

        mBinding.dim.setRect(centerX, centerY, width, height, 6.dp2px.toFloat())
    }

    private fun showDim() {
        mBinding.dim.alpha = 0.75f
        topDimView?.alpha = 0.75f
        bottomDimView?.alpha = 0.75f
    }

    private fun clearDim() {
        mBinding.dim.alpha = 0f

        val windowManager = requireActivity().windowManager
        topDimView?.let {
            windowManager.removeView(it)
            topDimView = null
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

        mBinding.main.animate()
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