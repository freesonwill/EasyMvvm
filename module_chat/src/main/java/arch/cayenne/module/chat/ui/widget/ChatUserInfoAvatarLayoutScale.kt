package arch.cayenne.module.chat.ui.widget


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import arch.cayenne.lib.base.utils.LogUtils

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import arch.cayenne.module.chat.R
import com.walisport.module.live.ui.widget.ChatInfoGestureListener
import com.walisport.module.live.ui.widget.ChatUserInfoLayoutInterceptTouch.ChatInfoSlideDirection
import kotlin.math.abs

/*
负责大小变化
 */
class ChatUserInfoAvatarLayoutScale @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private lateinit var headTitle: LinearLayoutCompat //顶部头像
    private lateinit var LlBottom: LinearLayoutCompat //底部名称
    private lateinit var avatarImageView: AppCompatImageView //顶部头像
    private lateinit var topName: AppCompatTextView //顶部名称
    private var mLiveMainGesture: ChatInfoGestureListener? = null
    private val density = resources.displayMetrics.density
    private var leftMax: Float = 0f

    private var avatarMaxPosition: Float = 0f //移动头像最大 (当前位置)
    private var avatarMinPosition: Float = 0f //头像移动最小(avatarMinPosition-50)


    private var minAvatarTitleHeight: Float = 52f * density //顶部头像布局 最小
    private var mAvatarTitleHeight: Float = 62f * density //顶部头像布局 最小
    private var maxAvatarTitleHeight: Float = 84f * density //顶部头像布局 最大

    private var minLlBottomHeight: Float = 0f * density //顶部头像布局 最小
    private var maxLlBottomHeight: Float = 84f * density //顶部头像布局 最大

    private var alphaPx: Float = 0f //根据底部名称的高度进行 顶部名称的渐变
    override fun onFinishInflate() {
        super.onFinishInflate()
        // 初始化视图
        headTitle = findViewById(R.id.headTitle)
        LlBottom = findViewById(R.id.LlBottom)
        topName = findViewById(R.id.topName)
        avatarImageView = findViewById(R.id.avatarImageView)
        avatarImageView.post {
            alphaPx = 100 / (maxLlBottomHeight)
            avatarMinPosition =
                (avatarImageView.x - (30 * resources.displayMetrics.density))
            avatarMaxPosition = avatarImageView.x
            // 设置初始高度和宽度
            headTitle.layoutParams.height = maxAvatarTitleHeight.toInt()
            maxLlBottomHeight = LlBottom.height.toFloat()
            LlBottom.layoutParams.height =  LlBottom.height
        }
    }



    //顶部是否可滑动
    fun isDirectionToScroll(): Boolean {
        val currentHeight = headTitle.layoutParams.height
        val currentBottomHeight = LlBottom.height
        // 如果当前高度在 80-211 范围内，返回 true，表示可以滑动
        return (currentHeight == maxAvatarTitleHeight.toInt() && currentBottomHeight == maxLlBottomHeight.toInt())
    }


    fun adjustLayout(y: Float, direction: ChatInfoSlideDirection) {
        val deltaY = y.coerceIn(-30f, 30f)

        val currentX = avatarImageView.x
        // 计算目标高度，限制在 minVideoHeight 和 maxVideoHeight 之间  如果是折叠状态,minVideoHeight没有大小限制
        val newScale = (currentX + deltaY).coerceIn(avatarMinPosition, avatarMaxPosition)
        // 获取当前高度
        val currentHeight = headTitle.layoutParams.height.toFloat()
        // 计算目标高度，限制在 minVideoHeight 和 maxVideoHeight 之间  如果是折叠状态,minVideoHeight没有大小限制
        val newHeight =
            (currentHeight + deltaY).coerceIn(minAvatarTitleHeight, maxAvatarTitleHeight)
        // 根据宽高比例计算目标宽度
        val paramsLin = headTitle.layoutParams as FrameLayout.LayoutParams
        paramsLin.height = newHeight.toInt()
        headTitle.layoutParams = paramsLin
        avatarImageView.x = newScale
        // 计算缩放比例（基于高度变化）
        val targetScale = (newHeight) / maxAvatarTitleHeight

        // 设置缩放中心：只设置宽度中心（X轴）
        // 当高度达到 minVideoHeight 时，pivotX 设置为 0，否则为宽度中心
        avatarImageView.pivotX = 0f
        avatarImageView.pivotY = avatarImageView.width.toFloat() / 2 // 高度中心
        // 应用等比缩放
        avatarImageView.post {
            avatarImageView.scaleX = targetScale
            avatarImageView.scaleY = targetScale
        }
        //30
        if (direction == ChatInfoSlideDirection.UP) {
            if(newHeight==minAvatarTitleHeight){
                val currentBottomHeight = LlBottom.layoutParams.height.toFloat()
                val bottomHeight =
                    (currentBottomHeight + deltaY).coerceIn(minLlBottomHeight, maxLlBottomHeight)
                val paramsBottom = LlBottom.layoutParams as LayoutParams
                paramsBottom.height = bottomHeight.toInt()
                LlBottom.layoutParams = paramsBottom
                //顶部头像名称渐变  渐变根据中间名称滑动距离进行渐变
                topName.alpha = abs(((bottomHeight - maxLlBottomHeight) * alphaPx) / 100).toFloat()
                if (currentHeight <= minAvatarTitleHeight) {
                    //中间名称往上滑动
                    topName.visibility = VISIBLE
                }
            }
        }

        if (direction == ChatInfoSlideDirection.DOWN) {
            if (currentHeight<=mAvatarTitleHeight){
                topName.visibility = INVISIBLE
            }
            if(newHeight==maxAvatarTitleHeight){
                val currentBottomHeight = LlBottom.layoutParams.height.toFloat()
                val bottomHeight =
                    (currentBottomHeight + deltaY).coerceIn(minLlBottomHeight, maxLlBottomHeight)
                val paramsBottom = LlBottom.layoutParams as LayoutParams
                paramsBottom.height = bottomHeight.toInt()
                LlBottom.layoutParams = paramsBottom
                alpha = alpha.coerceIn(0f, 1f)
            }
        }
    }

    fun View.getTopMargin(): Int {
        return (layoutParams as? MarginLayoutParams)?.topMargin ?: 0
    }
    //上滚-弹窗-头像-名字-列表
    //下滚-列表拉到顶部-头像-名字-弹窗

    fun setOnGestureListener(gestureListener: ChatInfoGestureListener) {
        mLiveMainGesture = gestureListener
    }

    fun hideTextViewWithGradient(textView: TextView) {
        val alphaAnimator = ValueAnimator.ofFloat(1f, 0f)
        val colorAnimator = ValueAnimator.ofArgb(
            ContextCompat.getColor(context, android.R.color.white),
            ContextCompat.getColor(context, android.R.color.transparent)
        )

        alphaAnimator.duration = 100
        colorAnimator.duration = 100

        alphaAnimator.addUpdateListener { animation ->
            val alpha = animation.animatedValue as Float
            textView.alpha = alpha
        }

        colorAnimator.addUpdateListener { animation ->
            textView.setTextColor(animation.animatedValue as Int)
        }

        AnimatorSet().apply {
            playTogether(alphaAnimator, colorAnimator)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    textView.visibility = View.GONE
                }
            })
            start()
        }
    }
}
