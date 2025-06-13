package arch.cayenne.module.bet.ui.fragment

import android.animation.ValueAnimator
import android.graphics.Path
import android.graphics.PathMeasure
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_DISMISS
import arch.cayenne.module.bet.databinding.FragmentFloatingButtonBinding
import arch.cayenne.module.bet.viewmodel.FloatingButtonViewModel
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class FloatingButtonFragment private constructor(): BaseFragment<FloatingButtonViewModel, FragmentFloatingButtonBinding>() {
    override val vbClass: KClass<FragmentFloatingButtonBinding> = FragmentFloatingButtonBinding::class
    override val vmClass: KClass<FloatingButtonViewModel> = FloatingButtonViewModel::class
    private var isShowBetSheet = false

    private val dotViews = ConcurrentHashMap<Int, View>()
    private var dotCounter = 0

    companion object {
        fun newInstance(): FloatingButtonFragment {
            return FloatingButtonFragment()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        setFloatingViewPosition(requireActivity().resources.displayMetrics.heightPixels)
    }

    override fun initListener() {
        mBinding.fab.setPerformClick {
            mViewModel.onBettingCount.value?.let { count ->
                isShowBetSheet = true
                parentFragmentManager.setFragmentResultListener(KEY_RESULT, viewLifecycleOwner) { resultKey, bundle ->
                    if (resultKey == KEY_RESULT) {
                        parentFragmentManager.clearFragmentResultListener(KEY_RESULT)
                        val dismissKey = bundle.getString(VALUE_DISMISS)
                        if (dismissKey == VALUE_DISMISS) {
                            isShowBetSheet = false
                            mViewModel.onBettingCount.value?.let {
                                setVisibility(it)
                            }
                        }
                    }
                }
                BetSheetFragment.newInstance().show(parentFragmentManager)
                mBinding.root.visibility = View.GONE
            }
        }
    }

    override fun createObserver() {
        mViewModel.onBettingCount.observe(viewLifecycleOwner) {
            if (!isShowBetSheet) {
                setVisibility(it)
            }
        }
    }

    private fun setVisibility(count: Int) {
        if (count == 0) {
            mBinding.root.visibility = View.GONE
        } else {
            mBinding.fab.setCount(count)
            mBinding.root.visibility = View.VISIBLE
        }
    }

    private fun setFloatingViewPosition(screenHeight: Int) {
        val floatingView = mBinding.fab
        val layoutParams = floatingView.layoutParams as ConstraintLayout.LayoutParams

        // 設定懸浮按鈕的縱向位置，將其放在螢幕高度的2/3處
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.topMargin = (screenHeight * 2 / 3) - floatingView.height / 2

        floatingView.layoutParams = layoutParams
    }

    fun show(activity: AppCompatActivity) {
        activity.supportFragmentManager.beginTransaction()
            .add(android.R.id.content, this, this.javaClass.simpleName)
            .commit()
    }

    fun showDotAnimation(x: Float, y: Float) {
        createDotAnimation(x, y)
    }

    private fun createDotAnimation(startX: Float, startY: Float) {
        val fabView = mBinding.fab
        val fabLocation = IntArray(2)
        fabView.getLocationOnScreen(fabLocation)

        // 計算最短路徑
        val fabCenterX = fabLocation[0] + fabView.width / 2
        val fabCenterY = fabLocation[1] + fabView.height / 2

        // 創建直線路徑
        val path = Path()
        path.moveTo(startX, startY)
        path.lineTo(fabCenterX.toFloat(), fabCenterY.toFloat())

        // 創建動畫
        val pathMeasure = PathMeasure(path, false)
        val pathLength = pathMeasure.length

        // 根據距離計算動畫時間，保持速度一致
        // 假設速度為 1000dp/s
        val speed = 1000f
        val duration = (pathLength / speed * 1000).toLong()

        // 創建圓點視圖
        val dotId = dotCounter++
        val dotView = View(requireContext()).apply {
            id = dotId
            setBackgroundResource(arch.cayenne.module.bet.R.drawable.shape_dot_anim)
            layoutParams = ViewGroup.LayoutParams(40, 40)
            (requireActivity().window.decorView as ViewGroup).addView(this)
        }
        dotViews[dotId] = dotView

        // 設置動畫
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()

            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                val pos = FloatArray(2)
                pathMeasure.getPosTan(pathLength * value, pos, null)

                dotView.apply {
                    x = pos[0] - width / 2
                    y = pos[1] - height / 2
                }
            }

            doOnEnd {
                dotView.parent?.let { (it as ViewGroup).removeView(dotView) }
                dotViews.remove(dotId)
            }
        }

        animator.start()
    }
}