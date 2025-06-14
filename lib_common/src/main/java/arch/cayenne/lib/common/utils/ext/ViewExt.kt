package arch.cayenne.lib.common.utils.ext

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnAttach
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
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
    start:Boolean = true
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
    duration: Long = -1,
    interpolator: TimeInterpolator? = null,
    start:Boolean = true,
): AnimatorSet {
    val animator = AnimatorSet().apply {
        this.config()
        this.duration = duration
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