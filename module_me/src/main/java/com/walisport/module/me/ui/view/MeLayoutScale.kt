package arch.cayenne.module.chat.ui.widget

import android.animation.ValueAnimator


import android.view.View

import arch.cayenne.lib.base.utils.LogUtils

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.marginTop
import com.walisport.module.live.ui.widget.MeLayoutInterceptTouch.MeSlideDirection
import com.walisport.module.me.R
import kotlin.math.abs

/*
负责大小变化
 */
class MeLayoutScale @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private lateinit var viewTop: View

    private val density = resources.displayMetrics.density
    private var minViewHeight: Float = 0f * density
    private var maxViewHeight: Float = 0f * density


    override fun onFinishInflate() {
        super.onFinishInflate()
        // 初始化视图
        viewTop = findViewById(R.id.clTop)
    }

    fun initViewHeight(initHeight: Float,banHeight: Float) {
        val paramsLin = viewTop.layoutParams as LayoutParams
        paramsLin.height = initHeight.toInt()
        viewTop.layoutParams = paramsLin
        maxViewHeight = -initHeight+banHeight
    }

    fun adjustLayout(deltaY: Float, direction: MeSlideDirection,onViewTop: (Int) -> Unit?) {
        val paramsLin = viewTop.layoutParams as MarginLayoutParams
        val currentMarginTop = paramsLin.topMargin.toFloat()
        val newMarginTop = (currentMarginTop + deltaY).coerceIn(maxViewHeight, minViewHeight)
        paramsLin.topMargin = newMarginTop.toInt()
        viewTop.layoutParams = paramsLin
        onViewTop.invoke(abs(currentMarginTop).toInt())
        LogUtils.e("MeLayoutScale----------deltaY,${deltaY}---------direction,${direction}-------newMarginTop,${newMarginTop}--currentMarginTop,${currentMarginTop}")
    }

}
