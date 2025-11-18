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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.fragment.app.FragmentContainerView
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.startSafeAnimateSet
import arch.cayenne.module.chat.R
import com.google.android.material.animation.AnimatorSetCompat.playTogether
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
    private var initialViewHeight: Float = 334f * density
    private var initMinViewHeight: Float = 140f * density
    private var minViewHeight: Float = 140f * density
    private var maxViewHeight: Float = 334f * density
    private var isVerticalScroll = true
    private var animation = true
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


    fun adjustLayoutViewTopAnim( direction: ChatInfoSlideDirection,onAnimEnd: () -> Unit?){
        val currentHeight = viewTop.layoutParams.height.toFloat()
        //最大,不执行动画
        if (currentHeight.toInt()==maxViewHeight.toInt())return
        if (direction==ChatInfoSlideDirection.DOWN&&animation){
            if (!animation)return
            viewTop.startSafeAnimateSet(
                {
                    animation =false
                    playTogether(
                        ValueAnimator.ofInt(currentHeight.toInt(), maxViewHeight.toInt()).apply {
                            addUpdateListener {
                                val lp = viewTop.layoutParams
                                lp.height = it.animatedValue as Int
                                viewTop.layoutParams = lp
                                onAnimEnd.invoke()
                            }
                            addListener(doOnStart {

                            }
                            )
                            addListener(doOnEnd {

                                animation = true
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
    }

    fun adjustLayout(deltaY: Float, direction: ChatInfoSlideDirection,isAnim: Boolean = true) {
        val currentHeight = viewTop.layoutParams.height.toFloat()
        // 获取当前高度
        if (direction==ChatInfoSlideDirection.UP&&animation&&isAnim){
            if (!animation)return
            viewTop.startSafeAnimateSet(
                {
                    animation =false
                    playTogether(
                        ValueAnimator.ofInt(currentHeight.toInt(), minViewHeight.toInt()).apply {
                            addUpdateListener {
                                val lp = viewTop.layoutParams
                                lp.height = it.animatedValue as Int
                                viewTop.layoutParams = lp
                            }
                            addListener(doOnStart {

                            }
                            )
                            addListener(doOnEnd {
                                animation = true
                            })
                        },
                    )
                },
                duration = AnimationController[AnimType.popupExit]!!.duration,
                interpolator = AnimationController[AnimType.popupExit]?.interpolator?.toInterpolator()
                    ?: LinearInterpolator(),
                start = true
            )
        }else{
            // 计算目标高度，限制在 minVideoHeight 和 maxVideoHeight 之间  如果是折叠状态,minVideoHeight没有大小限制
            val newHeight = (currentHeight + deltaY).coerceIn(minViewHeight, maxViewHeight)
            if (newHeight>=initMinViewHeight){
                minViewHeight = initMinViewHeight
            }
            // 根据宽高比例计算目标宽度
            val paramsLin = viewTop.layoutParams as LayoutParams
            paramsLin.height = newHeight.toInt()
            viewTop.layoutParams = paramsLin

            // 更新初始值
            initialViewHeight = newHeight
        }
    }




    fun setOnGestureListener(gestureListener: ChatInfoGestureListener) {
        mLiveMainGesture = gestureListener
    }


}
