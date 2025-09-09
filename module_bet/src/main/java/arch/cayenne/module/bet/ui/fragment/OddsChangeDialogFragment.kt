package arch.cayenne.module.bet.ui.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.base.ui.fragment.BaseDialogFragment
import arch.cayenne.lib.base.ui.fragment.dim.DimView
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.bet.databinding.FragmentOddsChangeBinding
import arch.cayenne.module.bet.viewmodel.OddsChangeViewModel
import kotlin.reflect.KClass
import androidx.core.graphics.drawable.toDrawable
import arch.cayenne.lib.common.utils.ViewUtils

class OddsChangeDialogFragment private constructor() :
    BaseDialogFragment<OddsChangeViewModel, FragmentOddsChangeBinding>() {

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
//    private var bottomDimView: View? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION)
            setOnDismissListener {
                dimView?.alpha = 0f
//                bottomDimView?.alpha = 0f
            }
        }
    }

    override val dialogBackground: Drawable?
        get() = null

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun onStart() {
        removeDim()
        super.onStart()
        initDim()
        setDialogPosition()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initDim() {

        val screenHeight = resources.displayMetrics.heightPixels
        val statusHeight = ViewUtils.getStatusBarHeight(requireContext())
        val navigationHeight = ViewUtils.getNavigationBarHeight(requireContext())

        val dimV = DimView(requireContext()).apply {
            setBackgroundColor(Color.BLACK)
            alpha = 0f
            dimView = this
        }

        val bottomParams = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        bottomParams.token = dialog?.window?.decorView?.windowToken

        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(dimV, bottomParams)

        val rect = requireArguments().getParcelable<Rect>(RECT_KET) ?: return
        val centerX = rect.centerX()
        val centerY = rect.centerY()
        val width = rect.width()
        val height = rect.height()

        dimV.setRect(centerX, centerY, width, height, 6.dp2px.toFloat())
    }

    private fun clearDim() {
        val windowManager = requireActivity().windowManager
        dimView?.let {
            windowManager.removeView(it)
            dimView = null
        }
    }

    override fun onDestroy() {
        clearDim()
        super.onDestroy()
    }

    private fun setDialogPosition() {
        dialog?.window?.let { window ->

            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

            val rect = requireArguments().getParcelable<Rect>(RECT_KET) ?: return
            val positionX = rect.right

            mBinding.root.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            val layoutParams = window.attributes
            layoutParams.gravity = Gravity.TOP or Gravity.START
            layoutParams.x = 10.dp2px
            layoutParams.y = rect.bottom + 10.dp2px - ViewUtils.getStatusBarHeight(requireContext())
            window.attributes = layoutParams

            mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    // 先設定 triangle 位置
                    setTrianglePosition(positionX)

                    mBinding.root.post {

                        // 動畫初始狀態
                        mBinding.root.pivotX = mBinding.triangle.x + mBinding.triangle.width / 2
                        mBinding.root.pivotY = 0f
                        mBinding.root.scaleX = 0f
                        mBinding.root.scaleY = 0f
                        mBinding.root.alpha = 0f

                        // 開始動畫
                        mBinding.root.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .alpha(1f)
                            .setDuration(200)
                            .setInterpolator(android.view.animation.DecelerateInterpolator())
                            .withStartAction {
                                mBinding.root.visibility = View.VISIBLE
                            }
                            .withStartAction {
                                dimView?.alpha = 0.75f
                            }
                            .start()
                    }

                }
            })
        }
    }


    private fun setTrianglePosition(targetPositionX: Int) {
        val triangleLocation = IntArray(2)
        mBinding.triangle.getLocationOnScreen(triangleLocation)
        val px = targetPositionX - triangleLocation.first() - mBinding.triangle.width
        val params = mBinding.triangle.layoutParams as ConstraintLayout.LayoutParams
        params.rightMargin = params.rightMargin - px + 6.dp2px
        mBinding.triangle.layoutParams = params
    }
}