package arch.cayenne.lib.common.utils.ext

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.ui.view.CustomTabIndicator
import arch.cayenne.lib.common.ui.view.OnSwipeTouchListener
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.reflect.Field

private var lastClickTime: Long = 0L
private const val TAG = "ViewExt"
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
    start: Boolean
): ObjectAnimator {
    val animator = ObjectAnimator.ofFloat(this, property, *values).apply {
        this.duration = duration
        this.repeatCount = repeatCount
        this.repeatMode = repeatMode
        this.interpolator = interpolator
        if (start) start()
    }

    // 绑定生命周期，在 viewLifecycleOwner 销毁时 cancel 动画
    addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {}
        override fun onViewDetachedFromWindow(v: View) {
            removeOnAttachStateChangeListener(this)
            animator.cancel()
        }
    })
    return animator
}

fun View.startSafeObjectAnimator(
    property: String,
    vararg values: Float
): ObjectAnimator {
    val animator = ObjectAnimator.ofFloat(this, property, *values)
    // 绑定生命周期，在 viewLifecycleOwner 销毁时 cancel 动画
    addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {}
        override fun onViewDetachedFromWindow(v: View) {
            removeOnAttachStateChangeListener(this)
            animator.cancel()
        }
    })
    return animator
}

/**
 * 安全启动AnimatorSet
 *
 * @param config
 * @param duration
 * @param interpolator
 * @param start
 * @return
 */
fun View.startSafeAnimateSet(
    config: AnimatorSet.() -> Unit,
    duration: Long? = null,
    interpolator: TimeInterpolator? = null,
    start: Boolean,
): AnimatorSet {
    val animator = AnimatorSet().apply {
        this.config()
        if (duration != null) this.duration = duration
        if (interpolator != null) this.interpolator = interpolator
        if (start) start()
    }
    // 绑定生命周期，在 viewLifecycleOwner 销毁时 cancel 动画
    addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {}
        override fun onViewDetachedFromWindow(v: View) {
            removeOnAttachStateChangeListener(this)
            animator.cancel()
        }
    })
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
 * 仿iOS滑動列表頂/底部回彈效果
 *
 * @param maxOverscroll 最大拉伸距離（預設為 200f）
 */
fun RecyclerView.enableRecyclerViewBounce(
    maxOverscroll: Float = 200f
) {
    var lastY = 0f
    var isDragging = false
    var isEligible = false // 是否進入底部拉伸狀態
    var isTopDragging = false
    var isTopEligible = false // 是否進入頂部拉伸狀態

    setOnTouchListener { _, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastY = event.rawY
                isDragging = false
                isEligible = false
                isTopDragging = false
                isTopEligible = false
                translationY = 0f // 頂部回彈時重置位移
                children.forEach { it.translationY = 0f }
            }

            MotionEvent.ACTION_MOVE -> {
                val currentY = event.rawY
                val dy = currentY - lastY

                val canScrollDown = canScrollVertically(1)
                val canScrollUp = canScrollVertically(-1)

                // 底部回彈
                if (!canScrollDown && dy < 0 && !isTopDragging) {
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
                }
                // 頂部回彈 - 移動整個 RecyclerView
                else if (!canScrollUp && dy > 0 && !isDragging) {
                    if (!isTopEligible) {
                        lastY = currentY
                        isTopEligible = true
                        return@setOnTouchListener true
                    }

                    isTopDragging = true
                    lastY = currentY

                    val offset = dy / 2f
                    val newTranslation = translationY + offset
                    translationY = newTranslation.coerceIn(0f, maxOverscroll)
                    return@setOnTouchListener true
                } else {
                    lastY = currentY
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging || isTopDragging) {
                    // 頂部回彈動畫
                    if (isTopDragging) {
                        animate()
                            .translationY(0f)
                            .setInterpolator(DecelerateInterpolator())
                            .setDuration(250)
                            .start()
                    } else {
                        children.forEach { child ->
                            child.animate()
                                .translationY(0f)
                                .setInterpolator(DecelerateInterpolator())
                                .setDuration(250)
                                .start()
                        }
                    }
                    isDragging = false
                    isEligible = false
                    isTopDragging = false
                    isTopEligible = false
                    return@setOnTouchListener true
                } else {
                    performClick()
                }
            }
        }
        false
    }
}

/**
 * 水波纹效果，支持自定义圆角和颜色
 *
 * @param cornerRadiusDp
 * @param backgroundColor
 * @param rippleColor
 */
fun View.addRippleEffect(
    rippleColor: String = "#ff0000",
    backgroundColor: String = "#00000000",
    cornerRadiusDp: Float,
) {
    val shapeAppearanceModel = ShapeAppearanceModel.builder()
        .setAllCornerSizes(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                cornerRadiusDp,
                context.resources.displayMetrics
            )
        )
        .build()

    val backgroundDrawable = MaterialShapeDrawable(shapeAppearanceModel).apply {
        fillColor = ColorStateList.valueOf(Color.parseColor(backgroundColor))
    }

    val rippleColorState = ColorStateList.valueOf(Color.parseColor(rippleColor))
    val rippleDrawable = RippleDrawable(rippleColorState, backgroundDrawable, null)
    this.background = rippleDrawable
}

/**
 * 水波纹效果，支持自定义圆角和颜色
 *
 * @param topLeftDp
 * @param topRightDp
 * @param bottomRightDp
 * @param bottomLeftDp
 * @param backgroundColor
 * @param rippleColor
 */
fun View.addRippleEffect(
    rippleColor: String = "#ff0000",
    backgroundColor: String = "#00000000",
    bottomLeftDp: Float = 0f,
    topLeftDp: Float = 0f,
    topRightDp: Float = 0f,
    bottomRightDp: Float = 0f,
) {
    val dm = context.resources.displayMetrics
    val topLeftPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topLeftDp, dm)
    val topRightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topRightDp, dm)
    val bottomRightPx =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottomRightDp, dm)
    val bottomLeftPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottomLeftDp, dm)

    val shapeAppearanceModel = ShapeAppearanceModel.builder()
        .setTopLeftCorner(CornerFamily.ROUNDED, topLeftPx)
        .setTopRightCorner(CornerFamily.ROUNDED, topRightPx)
        .setBottomRightCorner(CornerFamily.ROUNDED, bottomRightPx)
        .setBottomLeftCorner(CornerFamily.ROUNDED, bottomLeftPx)
        .build()

    val backgroundDrawable = MaterialShapeDrawable(shapeAppearanceModel).apply {
        fillColor = ColorStateList.valueOf(Color.parseColor(backgroundColor))
    }

    val rippleColorState = ColorStateList.valueOf(Color.parseColor(rippleColor))
    val rippleDrawable = RippleDrawable(rippleColorState, backgroundDrawable, null)

    this.background = rippleDrawable
}

fun View.startZoomInAnim(vararg otherViews: View) {
    val views = listOf(this, *otherViews)

    // Step1: 计算平均中心点（屏幕坐标）
    val unionRect = views.fold(Rect()) { acc,view->
        val rect = Rect()
        view.getGlobalVisibleRect(rect)
        acc.union(rect)
        acc
    }
    val avgX = unionRect.centerX().toFloat()
    val avgY = unionRect.centerY().toFloat()

    // Step2: 保存原始 pivot，创建动画
    val originalPivots = views.map { it.pivotX to it.pivotY }
    val animators = views.map { view ->
        val location = view.locationOnScreen
        view.pivotX = avgX - location[0]
        view.pivotY = avgY - location[1]
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "alpha", 0.3f, 1f),
                ObjectAnimator.ofFloat(view, "scaleX", 0.995f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0.995f, 1f)
            )
            duration = AnimationController[AnimType.zoomIn]!!.duration
            interpolator = AnimationController[AnimType.zoomIn]!!.interpolator.toInterpolator()
        }
    }

    // Step3: 统一执行动画并还原 pivot
    views.forEach { it.animate().cancel() }
    AnimatorSet().apply {
        playTogether(animators)
        addListener(onEnd = {
            views.forEachIndexed { index, view ->
                val (pivotX,pivotY) = originalPivots[index]
                view.pivotX = pivotX
                view.pivotY = pivotY
            }
        })
        start()
    }
}

//改变ViewPager2 內部的 RecyclerView动画时间
private fun ViewPager2.setViewPagerAnimationDuration(duration: Long) {
    try {
        // 通過反射獲取 ViewPager2 內部的 RecyclerView
        val recyclerViewField: Field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true
        val recyclerView = recyclerViewField.get(this) as RecyclerView

        // 設置自定義 ItemAnimator
        val animator = DefaultItemAnimator().apply {
            addDuration = duration // 添加動畫時長
            removeDuration = duration // 移除動畫時長
            moveDuration = duration // 移動動畫時長
            changeDuration = duration // 改變動畫時長
        }
        recyclerView.itemAnimator = animator
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
fun CustomTabIndicator.animateIndicatorToPosition(position: Int,smoothScroll:Boolean = true) {
    val duration:Long = if(smoothScroll) AnimationController[AnimType.scrollbar]!!.duration else 0
    val interpolator: TimeInterpolator = AnimationController[AnimType.scrollbar]!!.interpolator.toInterpolator()
    val animator = ValueAnimator.ofFloat(this.getCurrentPosition().toFloat(), position.toFloat())
    animator.duration = duration // 动画持续时间
    animator.interpolator = interpolator
    animator.addUpdateListener { animation ->
        val progress = animation.animatedValue as Float
        setIndicatorPosition(progress.toInt(), progress % 1f)
    }
    animator.start()
}

//侧滑退出当前fragment
fun View.touchBackPressed(boo: Boolean = true){
    setOnTouchListener(object : OnSwipeTouchListener() {
        override fun onSwipeRight() {
            if (boo) { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }
    })
}

fun View.getTouchListener(): View.OnTouchListener? {
    return try {
        val listenerInfoField = View::class.java.getDeclaredField("mListenerInfo")
        listenerInfoField.isAccessible = true
        val listenerInfo = listenerInfoField.get(this)

        val touchListenerField = listenerInfo.javaClass.getDeclaredField("mOnTouchListener")
        touchListenerField.isAccessible = true
        touchListenerField.get(listenerInfo) as? View.OnTouchListener
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

inline val View.locationOnScreen: IntArray
    get() {
        val pos = IntArray(2)
        this.getLocationOnScreen(pos)
        return pos
    }

inline val View.locationInWindow: IntArray
    get() {
        val pos = IntArray(2)
        this.getLocationInWindow(pos)
        return pos
    }

inline val View.locationInSurface: IntArray
    @RequiresApi(Build.VERSION_CODES.Q) get() {
        val pos = IntArray(2)
        this.getLocationInSurface(pos)
        return pos
    }

//是否在View区域内
fun View.isInArea(rawX: Float, rawY: Float): Boolean {
    val rawXY = IntArray(2)
    getLocationOnScreen(rawXY)
    return rawX >= rawXY[0] && rawX <= (rawXY[0] + width) && rawY >= rawXY[1] && rawY <= (rawXY[1] + height)
}



/**
 * 切换页面时淡入淡出动画
 * @param doSwitchPage 执行切换页面的操作，传入一个回调函数 onComplete，在页面切换完成后调用该回调函数以触发淡入动画
 */
fun View.startFadeAnim(doSwitchPage: (onComplete: () -> Unit) -> Unit) {
    animate().cancel()
    animate()
        .alpha(0.5f)
        .setDuration(125)
        .withEndAction {
            doSwitchPage.invoke {
                // 等待畫面已經完成繪製後，再執行淡入動畫
                doOnPreDraw {
                    alpha = 0.5f
                    animate()
                        .alpha(1f)
                        .setDuration(125)
                        .start()
                }
            }
        }
        .start()
}
@SuppressLint("ClickableViewAccessibility")
fun View.setOnClickOrLongPressListener(
    // --- 可選參數，用於自訂速率 ---
    longPressDelay: Long = 500L,   // 長按判定時間
    repeatDelay: Long = 75L,     // 長按重複速率
    // --- 兩個核心的回呼 ---
    onClick: () -> Unit,
    onLongPressRepeat: () -> Unit
) {
    // 使用 setOnTouchListener 來監聽完整的觸摸事件
    setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 1. 獲取 LifecycleOwner 以安全地啟動協程
                val lifecycleOwner = findViewTreeLifecycleOwner()
                    ?: return@setOnTouchListener true // 如果找不到，則不執行任何操作

                // 用於追蹤長按是否已觸發的旗標
                var isLongPressTriggered = false

                // 2. 啟動一個新的協程來處理長按邏輯
                val job = lifecycleOwner.lifecycleScope.launch {
                    // 等待長按判定時間
                    delay(longPressDelay)

                    // 如果協程到這裡還活著 (沒有在 UP 事件中被取消)
                    // 就表示長按已成立
                    isLongPressTriggered = true

                    // 進入連續觸發的迴圈
                    while (isActive) {
                        onLongPressRepeat()
                        delay(repeatDelay)
                    }
                }

                // 將 job 和旗標存入 tag，以便在 UP 事件中可以存取
                setTag(R.id.long_press_job_tag, job)
                setTag(R.id.is_long_press_triggered_tag, isLongPressTriggered)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 3. 手指抬起時，立即取消長按的協程
                (getTag(R.id.long_press_job_tag) as? Job)?.cancel()

                // 4. 檢查長按旗標，以判斷這是否是一次單次點擊
                val isLongPressTriggered = getTag(R.id.is_long_press_triggered_tag) as? Boolean ?: false
                if (!isLongPressTriggered) {
                    // 如果長按從未被觸發，這就是一次有效的「單次點擊」
                    onClick()
                }

                // 清理 tag
                setTag(R.id.long_press_job_tag, null)
                setTag(R.id.is_long_press_triggered_tag, null)
            }
        }
        true
    }
}