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
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.common.utils.ext.startSafeAnimateSet
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
    private lateinit var LlBottom: LinearLayoutCompat //底部名称/投注次数和额度
    private lateinit var llcName: LinearLayoutCompat //名称

    private lateinit var avatarImageView: AppCompatImageView //顶部头像
    private lateinit var topName: AppCompatTextView //顶部名称
    private var mLiveMainGesture: ChatInfoGestureListener? = null
    private val density = resources.displayMetrics.density
    private var leftMax: Float = 0f
    private var quickAnimating: Boolean = false //快速滑动动画是否在执行 头像
    private var quickAnimatingName: Boolean = false //快速滑动动画是否在执行 名字
    private var avatarMaxPosition: Float = 0f //移动头像最大 (当前位置)
    private var avatarMinPosition: Float = 0f //头像移动最小(avatarMinPosition-50)


    private var minAvatarTitleHeight: Float = 40f * density //顶部头像布局 最小
    private var mAvatarTitleHeight: Float = 62f * density //顶部头像布局 最小
    private var maxAvatarTitleHeight: Float = 84f * density //顶部头像布局 最大

    private var mMinNameHeight: Float = 0f * density //顶部头像布局 最小
    private var mMaxNameHeight: Float = 60f * density //顶部头像布局 最大

    private var minLlBottomHeight: Float = 0f * density //底部投注数量和投注额 最小
    private var maxLlBottomHeight: Float = 67f * density //底部投注数量和投注额 最大

    private var alphaPx: Float = 0f //根据底部名称的高度进行 顶部名称的渐变
    override fun onFinishInflate() {
        super.onFinishInflate()
        // 初始化视图
        headTitle = findViewById(R.id.headTitle)
        LlBottom = findViewById(R.id.LlBottom)
        topName = findViewById(R.id.topName)
        llcName = findViewById(R.id.llcName)
        avatarImageView = findViewById(R.id.avatarImageView)
        avatarImageView.post {
            alphaPx = 100 / (mMaxNameHeight)
            avatarMinPosition =
                (avatarImageView.x - (30 * resources.displayMetrics.density))
            avatarMaxPosition = avatarImageView.x
            // 设置初始高度和宽度
            headTitle.layoutParams.height = maxAvatarTitleHeight.toInt()
            maxLlBottomHeight = LlBottom.height.toFloat()
            LlBottom.layoutParams.height = LlBottom.height
        }
    }

    //顶部是否可滑动
    fun isDirectionToScroll(): Boolean {
        val currentHeight = headTitle.layoutParams.height
        val currentBottomHeight = LlBottom.height
        // 如果当前高度在 80-211 范围内，返回 true，表示可以滑动
        return (currentHeight == maxAvatarTitleHeight.toInt() && currentBottomHeight == maxLlBottomHeight.toInt())
    }

    //滑动资料区域
    fun adjustLayoutTop (y: Float, direction: ChatInfoSlideDirection) {
        val deltaY = y.coerceIn(-30f, 30f)
        //动画在执行
        if (quickAnimating || quickAnimatingName) return
        val currentX = avatarImageView.x
        val newScale = (currentX + deltaY).coerceIn(avatarMinPosition, avatarMaxPosition)
        val currentHeight = headTitle.layoutParams.height.toFloat()
        if (direction == ChatInfoSlideDirection.UP) {
            if (currentHeight.toInt()== minAvatarTitleHeight.toInt()){
                //名字最小才能拉动投注额和数量
                val nameHeight = llcName.layoutParams.height.toFloat()
                val bottomNameHeight =
                    (nameHeight + deltaY).coerceIn(mMinNameHeight, mMaxNameHeight)
                val paramsBottomName = llcName.layoutParams as LayoutParams
                paramsBottomName.height = bottomNameHeight.toInt()
                llcName.layoutParams = paramsBottomName
                topName.alpha =(abs(((bottomNameHeight - mMaxNameHeight) * alphaPx) / 100).toFloat())
                if (nameHeight.toInt()==mMinNameHeight.toInt()){
                    val currentBottomHeight = LlBottom.layoutParams.height.toFloat()
                    val bottomHeight =
                        (currentBottomHeight + deltaY).coerceIn(minLlBottomHeight, maxLlBottomHeight)
                    val paramsBottom = LlBottom.layoutParams as LayoutParams
                    paramsBottom.height = bottomHeight.toInt()
                    LlBottom.layoutParams = paramsBottom
                }
            }else{
                val newHeight =
                    (currentHeight + deltaY).coerceIn(minAvatarTitleHeight, maxAvatarTitleHeight)
                val paramsLin = headTitle.layoutParams as FrameLayout.LayoutParams
                paramsLin.height = newHeight.toInt()
                headTitle.layoutParams = paramsLin
                avatarImageView.x = newScale
                val targetScale = (newHeight) / maxAvatarTitleHeight
                avatarImageView.pivotX = 0f
                avatarImageView.pivotY = avatarImageView.width.toFloat() / 2 // 高度中心
                // 应用等比缩放
                avatarImageView.post {
                    avatarImageView.scaleX = targetScale
                    avatarImageView.scaleY = targetScale
                }
            }
        }

        //下滑,投注额,名字,头像,名字根据滑动距离进行渐变
        if (direction == ChatInfoSlideDirection.DOWN) {
            val betNumberHeight = LlBottom.layoutParams.height.toFloat()
            //投注额和数量高度为最大范围内
            if (betNumberHeight < maxLlBottomHeight) {
                val bottomHeight =
                    (betNumberHeight + deltaY).coerceIn(minLlBottomHeight, maxLlBottomHeight)
                val paramsBottom = LlBottom.layoutParams as LayoutParams
                paramsBottom.height = bottomHeight.toInt()
                LlBottom.layoutParams = paramsBottom

            } else {
                val nameHeight = llcName.layoutParams.height.toFloat()
                //名称高度为最大范围内
                if (nameHeight.toInt() < mMaxNameHeight.toInt()) {
                    val bottomNameHeight =
                        (nameHeight + deltaY).coerceIn(mMinNameHeight, mMaxNameHeight)
                    val paramsBottomName = llcName.layoutParams as LayoutParams
                    paramsBottomName.height = bottomNameHeight.toInt()
                    llcName.layoutParams = paramsBottomName
                    topName.alpha =(abs(((bottomNameHeight - mMaxNameHeight) * alphaPx) / 100).toFloat())
                }else{
                    val newHeight =
                        (currentHeight + deltaY).coerceIn(minAvatarTitleHeight, maxAvatarTitleHeight)
                    val paramsLin = headTitle.layoutParams as FrameLayout.LayoutParams
                    paramsLin.height = newHeight.toInt()
                    headTitle.layoutParams = paramsLin
                    avatarImageView.x = newScale
                    val targetScale = (newHeight) / maxAvatarTitleHeight
                    avatarImageView.pivotX = 0f
                    avatarImageView.pivotY = avatarImageView.width.toFloat() / 2 // 高度中心
                    // 应用等比缩放
                    avatarImageView.post {
                        avatarImageView.scaleX = targetScale
                        avatarImageView.scaleY = targetScale
                    }
                }
            }
        }

    }

    fun adjustLayout(y: Float, direction: ChatInfoSlideDirection,onViewTopAnim: () -> Unit?) {
        val deltaY = y.coerceIn(-30f, 30f)
//        val currentX = avatarImageView.x
//        val newScale = (currentX + deltaY).coerceIn(avatarMinPosition, avatarMaxPosition)
//        val currentHeight = headTitle.layoutParams.height.toFloat()
//        val newHeight =
//            (currentHeight + deltaY).coerceIn(minAvatarTitleHeight, maxAvatarTitleHeight)
//        val paramsLin = headTitle.layoutParams as FrameLayout.LayoutParams
//        paramsLin.height = newHeight.toInt()
//        headTitle.layoutParams = paramsLin
//        avatarImageView.x = newScale
//        val targetScale = (newHeight) / maxAvatarTitleHeight
//        avatarImageView.pivotX = 0f
//        avatarImageView.pivotY = avatarImageView.width.toFloat() / 2 // 高度中心
//        // 应用等比缩放
//        avatarImageView.post {
//            avatarImageView.scaleX = targetScale
//            avatarImageView.scaleY = targetScale
//        }
//        if (direction == ChatInfoSlideDirection.UP) {
//            if(newHeight==minAvatarTitleHeight){
//                val currentBottomHeight = LlBottom.layoutParams.height.toFloat()
//                val bottomHeight =
//                    (currentBottomHeight + deltaY).coerceIn(minLlBottomHeight, maxLlBottomHeight)
//                val paramsBottom = LlBottom.layoutParams as LayoutParams
//                paramsBottom.height = bottomHeight.toInt()
//                LlBottom.layoutParams = paramsBottom
//                //顶部头像名称渐变  渐变根据中间名称滑动距离进行渐变
//                topName.alpha = abs(((bottomHeight - maxLlBottomHeight) * alphaPx) / 100).toFloat()
//                if (currentHeight <= minAvatarTitleHeight) {
//                    //中间名称往上滑动
//                    topName.visibility = VISIBLE
//                }
//            }
//        }

        //动画在执行
        if (quickAnimating || quickAnimatingName) return
        val currentHeight = headTitle.layoutParams.height.toFloat()
        if (direction == ChatInfoSlideDirection.UP) {
            if (currentHeight.toInt() == maxAvatarTitleHeight.toInt()) {
                quickAdjustLayoutUp()
            }else{
                //名字最小才能拉动投注额和数量
                val nameHeight = llcName.layoutParams.height.toFloat()
                val bottomNameHeight =
                    (nameHeight + deltaY).coerceIn(mMinNameHeight, mMaxNameHeight)
                val paramsBottomName = llcName.layoutParams as LayoutParams
                paramsBottomName.height = bottomNameHeight.toInt()
                llcName.layoutParams = paramsBottomName
                topName.alpha =(abs(((bottomNameHeight - mMaxNameHeight) * alphaPx) / 100).toFloat())

                if (nameHeight.toInt()==mMinNameHeight.toInt()){
                    val currentBottomHeight = LlBottom.layoutParams.height.toFloat()
                    val bottomHeight =
                        (currentBottomHeight + deltaY).coerceIn(minLlBottomHeight, maxLlBottomHeight)
                    val paramsBottom = LlBottom.layoutParams as LayoutParams
                    paramsBottom.height = bottomHeight.toInt()
                    LlBottom.layoutParams = paramsBottom
                }
            }
        }

        //下滑,投注额,名字,头像,名字根据滑动距离进行渐变
        if (direction == ChatInfoSlideDirection.DOWN) {
            val betNumberHeight = LlBottom.layoutParams.height.toFloat()
            //投注额和数量高度为最大范围内
            if (betNumberHeight < maxLlBottomHeight) {
                val bottomHeight =
                    (betNumberHeight + deltaY).coerceIn(minLlBottomHeight, maxLlBottomHeight)
                val paramsBottom = LlBottom.layoutParams as LayoutParams
                paramsBottom.height = bottomHeight.toInt()
                LlBottom.layoutParams = paramsBottom

            } else {
                val nameHeight = llcName.layoutParams.height.toFloat()
                LogUtils.e("nameHeight------>nameHeight${nameHeight},mMaxNameHeight${mMaxNameHeight}")
                //名称高度为最大范围内
                if (nameHeight.toInt() < mMaxNameHeight.toInt()) {
                    val bottomNameHeight =
                        (nameHeight + deltaY).coerceIn(mMinNameHeight, mMaxNameHeight)
                    val paramsBottomName = llcName.layoutParams as LayoutParams
                    paramsBottomName.height = bottomNameHeight.toInt()
                    llcName.layoutParams = paramsBottomName
                    topName.alpha =(abs(((bottomNameHeight - mMaxNameHeight) * alphaPx) / 100).toFloat())
                    LogUtils.e("topName.alpha------>${abs(((bottomNameHeight - mMaxNameHeight) * alphaPx) / 100).toFloat()}")
                }else{
                   //高度变大(viewTop,头像,名字)动画
                        onViewTopAnim.invoke()
                        animTingDow()
                }
           }
        }
    }

    //上滑顶部高度自动变小
    private fun upToTopView() {

    }

    fun quickAdjustLayoutUp() {
        val currentHeight = headTitle.height
        val llcNameHeight = llcName.height
        if (currentHeight >= minAvatarTitleHeight){
            headTitle.startSafeAnimateSet(
                {
                    quickAnimating = true
                    playTogether(
                        ValueAnimator.ofInt(currentHeight, minAvatarTitleHeight.toInt()).apply {
                            addUpdateListener {
                                val lp = headTitle.layoutParams
                                lp.height = it.animatedValue as Int
                                headTitle.layoutParams = lp
                                // 计算缩放比例（基于高度变化）
                                val currentX = avatarImageView.x
                                val newScale = (currentX - it.animatedValue as Int).coerceIn(
                                    avatarMinPosition,
                                    avatarMaxPosition
                                )
                                avatarImageView.x = newScale
                                val targetScale = (it.animatedValue as Int) / maxAvatarTitleHeight
                                avatarImageView.pivotX = 0f
                                avatarImageView.pivotY = avatarImageView.width.toFloat() / 2 // 高度中心
                                // 应用等比缩放
                                avatarImageView.post {
                                    avatarImageView.x = newScale
                                    avatarImageView.scaleX = targetScale
                                    avatarImageView.scaleY = targetScale
                                }
                            }
                            addListener(doOnStart {

                            }
                            )
                            addListener(doOnEnd {
                                topName.alpha = 1f
                                quickAnimating = false
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

        if (llcNameHeight > mMinNameHeight) {
            llcName.startSafeAnimateSet(
                {
                    quickAnimatingName = true
                    playTogether(
                        ValueAnimator.ofInt(llcNameHeight, mMinNameHeight.toInt()).apply {
                            addUpdateListener {
                                val lp = llcName.layoutParams
                                lp.height = it.animatedValue as Int
                                llcName.layoutParams = lp
                                topName.alpha = abs(((it.animatedValue as Int - mMaxNameHeight) * alphaPx) / 100).toFloat()

                            }
                            addListener(doOnStart {
                            }
                            )
                            addListener(doOnEnd {
                                quickAnimatingName = false
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



    fun animTingDow() {
        val currentHeight = headTitle.height
        val llcNameHeight = llcName.height
        val llcBottomHeight = LlBottom.height
        if (currentHeight==maxAvatarTitleHeight.toInt()&&llcNameHeight==mMaxNameHeight.toInt()&&llcBottomHeight==maxLlBottomHeight.toInt())return
        if (currentHeight < maxAvatarTitleHeight) {
            headTitle.startSafeAnimateSet(
                {
                    quickAnimating = true
                    playTogether(
                        ValueAnimator.ofInt(currentHeight, maxAvatarTitleHeight.toInt()).apply {
                            addUpdateListener {
                                val lp = headTitle.layoutParams
                                lp.height = it.animatedValue as Int
                                headTitle.layoutParams = lp
                                // 计算缩放比例（基于高度变化）
                                val currentX = avatarImageView.x
                                val newScale = (currentX + it.animatedValue as Int).coerceIn(
                                    avatarMinPosition,
                                    avatarMaxPosition
                                )
                                avatarImageView.x = newScale
                                val targetScale = (it.animatedValue as Int) / maxAvatarTitleHeight
                                avatarImageView.pivotX = 0f
                                avatarImageView.pivotY = avatarImageView.width.toFloat() / 2 // 高度中心
                                // 应用等比缩放
                                avatarImageView.post {
                                    avatarImageView.x = newScale
                                    avatarImageView.scaleX = targetScale
                                    avatarImageView.scaleY = targetScale
                                }
                            }
                            addListener(doOnStart {

                            }
                            )
                            addListener(doOnEnd {
                                quickAnimating = false
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

        if (llcNameHeight < mMaxNameHeight){
            llcName.startSafeAnimateSet(
                {
                    quickAnimatingName = true
                    playTogether(
                        ValueAnimator.ofInt(llcNameHeight, mMaxNameHeight.toInt()).apply {
                            addUpdateListener {
                                val lp = llcName.layoutParams
                                lp.height = it.animatedValue as Int
                                llcName.layoutParams = lp
                                topName.alpha = abs(((it.animatedValue as Int - mMaxNameHeight) * alphaPx) / 100).toFloat()

                            }
                            addListener(doOnStart {
                            }
                            )
                            addListener(doOnEnd {
                                topName.alpha = 0f
                                quickAnimatingName = false
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

        if (llcBottomHeight < maxLlBottomHeight){
            LlBottom.startSafeAnimateSet(
                {
                    quickAnimatingName = true
                    playTogether(
                        ValueAnimator.ofInt(llcBottomHeight, maxLlBottomHeight.toInt()).apply {
                            addUpdateListener {
                                val lp = LlBottom.layoutParams
                                lp.height = it.animatedValue as Int
                                LlBottom.layoutParams = lp
                            }
                            addListener(doOnStart {
                            }
                            )
                            addListener(doOnEnd {
                                quickAnimatingName = false
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
