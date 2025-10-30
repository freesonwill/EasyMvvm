package arch.cayenne.module.chat.ui.widget

import android.animation.ValueAnimator


import android.view.View

import arch.cayenne.lib.base.utils.LogUtils

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentContainerView
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.chat.R
import com.walisport.module.live.ui.widget.ChatInfoGestureListener
import com.walisport.module.live.ui.widget.ChatUserInfoLayoutInterceptTouch.ChatInfoSlideDirection

/*
负责大小变化
 */
class ChatUserInfoLayoutScale @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private lateinit var viewTop: View

    private var mLiveMainGesture: ChatInfoGestureListener? = null
    private val density = resources.displayMetrics.density
    private var initialViewHeight: Float = 259f * density
    private var initMinViewHeight: Float = 140f * density
    private var minViewHeight: Float = 140f * density
    private var maxViewHeight: Float = 259f * density
    private var isVerticalScroll = true

    private var isDowScroll = true // 标记是否往下滑动
    override fun onFinishInflate() {
        super.onFinishInflate()
        // 初始化视图
        viewTop = findViewById(R.id.viewTop)
        // 设置初始高度和宽度
        viewTop.layoutParams.height = initialViewHeight.toInt()
    }

    fun setIsDowScroll(isDowScroll: Boolean) {
        isVerticalScroll = !isDowScroll
        this.isDowScroll = isDowScroll
    }

    fun isDirectionToScroll(): Boolean {
        val currentHeight = viewTop.layoutParams.height.toFloat()
        // 如果当前高度在 80-211 范围内，返回 true，表示可以滑动
        return currentHeight in (minViewHeight+1)..maxViewHeight
    }


    fun adjustLayout(deltaY: Float, direction: ChatInfoSlideDirection) {

        // 获取当前高度
        val currentHeight = viewTop.layoutParams.height.toFloat()
        // 计算目标高度，限制在 minVideoHeight 和 maxVideoHeight 之间  如果是折叠状态,minVideoHeight没有大小限制
        val newHeight = (currentHeight + deltaY).coerceIn(minViewHeight, maxViewHeight)
        if (newHeight>=initMinViewHeight){
            minViewHeight = initMinViewHeight
        }
        LogUtils.e("ChatUserInfoFragment---------newHeight------>${newHeight}")
        // 根据宽高比例计算目标宽度
        val paramsLin = viewTop.layoutParams as LayoutParams
        paramsLin.height = newHeight.toInt()
        viewTop.layoutParams = paramsLin

        // 更新初始值
        initialViewHeight = newHeight
    }

    fun setOnGestureListener(gestureListener: ChatInfoGestureListener) {
        mLiveMainGesture = gestureListener
    }


}
