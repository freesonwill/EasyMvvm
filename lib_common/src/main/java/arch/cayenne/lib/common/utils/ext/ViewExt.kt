package arch.cayenne.lib.common.utils.ext

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.doOnAttach
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.R

private var lastClickTime: Long = 0L
/**
 * 将view转为bitmap
 */
@Deprecated("use View.drawToBitmap()")
fun View.toBitmap(scale: Float = 1f, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
    if (this is ImageView) {
        if (drawable is BitmapDrawable) return (drawable as BitmapDrawable).bitmap
    }
    this.clearFocus()
    val bitmap = createBitmapSafely(
        (width * scale).toInt(),
        (height * scale).toInt(),
        config,
        1
    )
    if (bitmap != null) {
        Canvas().run {
            setBitmap(bitmap)
            save()
            drawColor(Color.WHITE)
            scale(scale, scale)
            this@toBitmap.draw(this)
            restore()
            setBitmap(null)
        }
    }
    return bitmap
}

/**
 * 防止重复点击事件 默认0.5秒内不可重复点击
 * @param interval 时间间隔 默认0.5秒
 * @param action 执行方法
 */
fun View.clickNoRepeat(
    interval: Long = 500,
    action: (view: View) -> Unit
) {
    setOnClickListener {
        val lastTime = getTag(R.id.tag_last_click_time) as? Long ?: 0L
        val currentTime = System.currentTimeMillis()
        if (lastTime != 0L && (currentTime - lastTime < interval)) {
            return@setOnClickListener
        }
        setTag(R.id.tag_last_click_time, currentTime)
        action(it)
    }
}
/**
 * 防止重复点击事件，0.5秒内所有控件不可同时触发
 * @param interval 时间间隔 默认500毫秒（0.5秒）
 * @param action 执行方法
 */
fun View.clickNoRepeatSingle(
    interval: Long = 500,
    action: (view: View) -> Unit
) {
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (lastClickTime != 0L && (currentTime - lastClickTime < interval)) {
            return@setOnClickListener
        }
        lastClickTime = currentTime
        action(it)
    }
}

fun createBitmapSafely(width: Int, height: Int, config: Bitmap.Config, retryCount: Int): Bitmap? {
    try {
        return Bitmap.createBitmap(width, height, config)
    } catch (e: OutOfMemoryError) {
        e.printStackTrace()
        if (retryCount > 0) {
            System.gc()
            return createBitmapSafely(width, height, config, retryCount - 1)
        }
        return null
    }
}


/**
 * 获取view所在Activity
 * @return
 */
fun View.requireActivity(): AppCompatActivity {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    throw IllegalStateException("View $this not attached to an activity.")
}

/**
 * 为 Fragment 的 View 安全执行 ObjectAnimator，生命周期感知，避免内存泄漏。
 */
fun View.startSafeObjectAnimator(
    property: String,
    vararg values: Float,
    duration: Long = 300,
    interpolator: TimeInterpolator,
    repeatCount: Int = 0,
    repeatMode: Int = ObjectAnimator.RESTART,
    start:Boolean
): ObjectAnimator {
    val animator = ObjectAnimator.ofFloat(this, property, *values).apply {
        this.duration = duration
        this.repeatCount = repeatCount
        this.repeatMode = repeatMode
        this.interpolator = interpolator
        if(start) start()
    }

    // 绑定生命周期，在 viewLifecycleOwner 销毁时 cancel 动画
    doOnAttach {
        findViewTreeLifecycleOwner()!!.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                animator.cancel()
            }
        })
    }
    return animator
}

fun View.startSafeObjectAnimator(
    property: String,
    vararg values: Float
): ObjectAnimator {
    val animator = ObjectAnimator.ofFloat(this, property, *values)
    // 绑定生命周期，在 viewLifecycleOwner 销毁时 cancel 动画
    doOnAttach {
        findViewTreeLifecycleOwner()!!.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                animator.cancel()
            }
        })
    }
    return animator
}

/**
 * 安全启动AnimatorSet
 *
 * @param animators
 * @param duration
 * @param interpolator
 * @param start
 * @return
 */
fun View.startSafeAnimateSet(
    config: AnimatorSet.() -> Unit,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    start:Boolean,
): AnimatorSet {
    val animator = AnimatorSet().apply {
        this.config()
        if(duration != null) this.duration = duration
        if(interpolator != null) this.interpolator = interpolator
        if(start) start()
    }
    // 绑定生命周期，在 viewLifecycleOwner 销毁时 cancel 动画
    doOnAttach {
        findViewTreeLifecycleOwner()!!.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                animator.cancel()
            }
        })
    }
    return animator
}
/**
 * 為 View 添加觸摸時縮放的動畫效果。
 * @param targetView 實際要進行縮放動畫的 View，預設為觸摸的 View 本身
 * @param scaleRatio 按下時縮放的比例，預設為 0.9f
 * @param duration 動畫的持續時間（毫秒），預設為 100L
 */
@SuppressLint("ClickableViewAccessibility")
fun View.addScaleOnTouchAnimation(
    targetView: View = this, // 預設情況下，被觸摸的 View 就是被縮放的 View
    scaleRatio: Float = 0.9f,
    duration: Long = 100L,
) {
    this.setOnTouchListener { _, event ->
        if (!this.isEnabled || !targetView.isEnabled) return@setOnTouchListener false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 按下時，取消任何正在進行的動畫，並開始縮小動畫
                targetView.animate().cancel() // 取消正在執行的動畫
                targetView.animate()
                    .scaleX(scaleRatio)
                    .scaleY(scaleRatio)
                    .setDuration(duration)
                    .start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 放開或取消觸摸時，取消任何正在進行的動畫，並開始恢復動畫
                targetView.animate().cancel()
                targetView.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(duration)
                    .start()
            }
        }
        // 返回 false，表示事件未被完全消費，允許其他監聽器（如 OnClickListener）繼續處理此事件
        return@setOnTouchListener false
    }
}
/**
 * 仿iOS滑動列表底部回彈效果
 *
 * @param maxOverscroll 最大拉伸距離（預設為 200f）
 */
fun RecyclerView.enableBottomBounce(
    maxOverscroll: Float = 200f
) {
    var lastY = 0f
    var isDragging = false
    var isEligible = false // 是否進入底部拉伸狀態

    setOnTouchListener { _, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastY = event.rawY
                isDragging = false
                isEligible = false
                children.forEach { it.translationY = 0f }
            }

            MotionEvent.ACTION_MOVE -> {
                val currentY = event.rawY
                val dy = currentY - lastY

                val canScrollDown = canScrollVertically(1)

                if (!canScrollDown && dy < 0) {
                    if (!isEligible) {
                        lastY = currentY
                        isEligible = true
                        return@setOnTouchListener true
                    }

                    isDragging = true
                    lastY = currentY

                    val offset = dy / 2f
                    children.forEach { child ->
                        val newTranslation = child.translationY + offset
                        child.translationY = newTranslation.coerceIn(-maxOverscroll, 0f)
                    }
                    return@setOnTouchListener true
                } else {
                    lastY = currentY
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    children.forEach { child ->
                        child.animate()
                            .translationY(0f)
                            .setInterpolator(DecelerateInterpolator())
                            .setDuration(250)
                            .start()
                    }
                    isDragging = false
                    isEligible = false
                    return@setOnTouchListener true
                } else {
                    performClick()
                }
            }
        }
        false
    }
}