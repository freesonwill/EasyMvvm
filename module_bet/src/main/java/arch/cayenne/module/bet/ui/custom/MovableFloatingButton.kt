package arch.cayenne.module.bet.ui.custom

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import arch.cayenne.lib.common.utils.ViewUtils.getNavigationBarHeight
import arch.cayenne.lib.common.utils.ViewUtils.getStatusBarHeight
import arch.cayenne.module.bet.databinding.LayoutMovableFloatingButtonBinding
import kotlin.math.abs

class MovableFloatingButton : LinearLayout, View.OnTouchListener {

    private var downRawX = 0f
    private var downRawY = 0f
    private var dX = 0f
    private var dY = 0f
    val binding: LayoutMovableFloatingButtonBinding
    private var performClick: (() -> Unit)? = null
    private var mAnimator: AnimatorSet? = null
    private var currentCount = 1
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        val layoutInflater = LayoutInflater.from(context)
        binding = LayoutMovableFloatingButtonBinding.inflate(layoutInflater, this, true)
        setOnTouchListener(this)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                playZoomOutAnimation()
                downRawX = event.rawX
                downRawY = event.rawY
                dX = v.x - downRawX
                dY = v.y - downRawY
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val vWidth = v.width
                val vHeight = v.height

                val viewParent = v.parent as View
                val parentWidth = viewParent.width
                val parentHeight = viewParent.height

                // 取得狀態欄 & 底部導航欄高度
                val statusBarHeight = getStatusBarHeight(context)
                val navigationBarHeight = getNavigationBarHeight(context)

                // 計算新的 X 座標 (限制在 0 ~ (parentWidth - vWidth))
                var newX = event.rawX + dX
                newX = 0f.coerceAtLeast(newX)
                newX = (parentWidth - vWidth).toFloat().coerceAtMost(newX)

                // 計算新的 Y 座標 (限制在 狀態欄底部 ~ 底部導航欄上方)
                var newY = event.rawY + dY
                newY = statusBarHeight.toFloat().coerceAtLeast(newY)
                newY = (parentHeight - navigationBarHeight - vHeight).toFloat().coerceAtMost(newY)

                // 設定位置
                v.animate()
                    .x(newX)
                    .y(newY)
                    .setDuration(0)
                    .start()

                return true
            }

            MotionEvent.ACTION_UP -> {
                playZoomInAnimation()
                val upRawX = event.rawX
                val upRawY = event.rawY

                val upDx = upRawX - downRawX
                val upDy = upRawY - downRawY
                if (abs(upDx) < 10 && abs(upDy) < 10) {
                    performClick()
                }
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                playZoomInAnimation()
                downRawX = 0f
                downRawY = 0f
                dX = 0f
                dY = 0f
                return true
            }

            else -> return super.onTouchEvent(event)
        }
    }


    override fun performClick(): Boolean {
        playBounceAnimation()
        performClick?.invoke()
        super.performClick()
        return true
    }

    fun setPerformClick(performClick: () -> Unit) {
        this.performClick = performClick
    }

    fun setCount(newCount: Int) {
        if (currentCount == newCount) return
        mAnimator?.cancel()

        val direction = if (newCount > currentCount) -1 else 1
        val animTextView = createAnimatedTextView(newCount, direction)

        (binding.tvFloatPin.parent as ViewGroup).apply {
            this.clipChildren = false
            this.clipToPadding = false
        }.addView(animTextView)

        playBounceAnimation()
        playTextAnimation(direction, newCount, animTextView)
        currentCount = newCount
    }

    private fun createAnimatedTextView(newCount: Int, direction: Int): TextView {
        val textView = TextView(context).apply {
            text = newCount.toString()
            textSize = binding.tvFloatPin.textSize / resources.displayMetrics.scaledDensity
            setTextColor(binding.tvFloatPin.currentTextColor)
            typeface = binding.tvFloatPin.typeface
            gravity = binding.tvFloatPin.gravity
            layoutParams = binding.tvFloatPin.layoutParams
            translationY = direction * binding.tvFloatPin.height.toFloat()
        }
        return textView
    }

    private fun playBounceAnimation() {
        this.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(75)
            .withEndAction {
                this.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(75)
                    .start()
            }
            .start()
    }

    private fun playTextAnimation(direction: Int, newCount: Int, animTextView: TextView) {
        val translationDistance = direction * binding.tvFloatPin.height.toFloat()

        val parent = binding.tvFloatPin.parent as ViewGroup

        val textAnimation =
            ObjectAnimator.ofFloat(binding.tvFloatPin, "translationY", 0f, translationDistance)
        val newTextAnimation =
            ObjectAnimator.ofFloat(animTextView, "translationY", -translationDistance, 0f)

        fun finish() {
            binding.tvFloatPin.text = newCount.toString()
            binding.tvFloatPin.translationY = 0f
            mAnimator = null
            parent.removeView(animTextView)
        }
        mAnimator = AnimatorSet().apply {
            duration = 100
            playTogether(textAnimation, newTextAnimation)
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    finish()
                }

                override fun onAnimationCancel(animation: Animator) {
                    finish()
                }

                override fun onAnimationRepeat(animation: Animator) {
                }

            })
            start()
        }


    }

    private fun playOldTextOutAnimation(direction: Int, newCount: Int, animTextView: TextView) {
        val translationDistance = direction * binding.tvFloatPin.height.toFloat()
        val parent = binding.tvFloatPin.parent as ViewGroup

        binding.tvFloatPin.animate()
            .translationYBy(translationDistance)
            .alpha(0f)
            .setDuration(100)
            .withEndAction {
                binding.tvFloatPin.text = newCount.toString()
                binding.tvFloatPin.translationY = 0f
                binding.tvFloatPin.alpha = 1f
                parent.removeView(animTextView)
            }
            .start()
    }

    private fun playNewTextInAnimation(animTextView: TextView, translationDistance: Float) {
        animTextView.animate()
            .translationYBy(translationDistance)
            .alpha(1f)
            .setDuration(100)
            .start()
    }

    private fun playZoomOutAnimation() {
        if (binding.root.scaleX == 0.9f && binding.root.scaleY == 0.9f) return
        binding.root.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .start()
    }

    private fun playZoomInAnimation() {
        if (binding.root.scaleX == 1f && binding.root.scaleY == 1f) return
        binding.root.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(100)
            .start()
    }
}