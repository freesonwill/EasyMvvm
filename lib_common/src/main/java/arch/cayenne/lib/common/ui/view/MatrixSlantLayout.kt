package arch.cayenne.lib.common.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.ViewCompat
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.startSafeAnimateSet

//通过矩阵切斜角,斜角底部画圆
class MatrixSlantLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private val path = Path()

    init {
        setWillNotDraw(false)
        // 强制绘制，子 View 裁剪
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updatePath()
    }

    private fun updatePath() {
        val topLeftX     = 0f
        val bottomLeftX  = 30.dp2px.toFloat() // 切角范围,左边
        val radius       = 15.dp2px.toFloat() // 底部圆角度数
        val w = width.toFloat()
        val h = height.toFloat()

        path.reset()

        // 顺时针绘制
        path.moveTo(topLeftX, 0f)                    // 顶部左起点（斜切顶点）
        path.lineTo(w, 0f)                           // 顶部右侧
        path.lineTo(w, h)                            // 右下角
        path.lineTo(bottomLeftX + radius, h)         //  准备画左下圆角
        //左下角 左边84 顶部90 圆角
        path.arcTo(
            RectF(bottomLeftX, h - 2 * radius, bottomLeftX + 2 * radius, h),
            84f, 90f
        )

        // 从圆角结束点沿左边斜线回到顶部起点
        path.lineTo(topLeftX, 0f)  // 直接回到 (0,0)

        path.close()  // 起点和终点已经重合
    }

    override fun dispatchDraw(canvas: Canvas) {
        val saveCount = canvas.save()
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(saveCount)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    private val marginEndMax = 0.dp2px
    private val marginEndMin = (-60).dp2px
    fun marginEndAnim(){
        if (getMarginEnd()==marginEndMin)return
        startSafeAnimateSet(
            {
                playTogether(
                    ValueAnimator.ofInt(marginEndMax,marginEndMin).apply {
                        addUpdateListener {
                            val value = it.animatedValue as Int
                            (layoutParams as? MarginLayoutParams)?.let {
                                it.marginEnd = value
                                layoutParams = it  // 触发重新布局
                            }
                        }
                        addListener(doOnStart {

                        }
                        )
                        addListener(doOnEnd {

                        })
                    },
                )
            },
            duration = AnimationController[AnimType.popupExit]!!.duration,
            interpolator = AnimationController[AnimType.popupExit]?.interpolator?.toInterpolator()
                ?: LinearInterpolator(),
            start = true
        )
    }

    fun marginStartAnim(){
        if (getMarginEnd()==marginEndMax)return
        startSafeAnimateSet(
            {
                playTogether(
                    ValueAnimator.ofInt(marginEndMin,marginEndMax).apply {
                        addUpdateListener {
                            val value = it.animatedValue as Int
                            (layoutParams as? MarginLayoutParams)?.let {
                                it.marginEnd = value
                                layoutParams = it  // 触发重新布局
                            }
                        }
                        addListener(doOnStart {

                        }
                        )
                        addListener(doOnEnd {

                        })
                    },
                )
            },
            duration = AnimationController[AnimType.popupExit]!!.duration,
            interpolator = AnimationController[AnimType.popupExit]?.interpolator?.toInterpolator()
                ?: LinearInterpolator(),
            start = true
        )
    }
   private fun getMarginEnd(): Int {
        val lp = layoutParams
        return if (lp is MarginLayoutParams) {
            lp.marginEnd
        } else {
            0
        }
    }
}