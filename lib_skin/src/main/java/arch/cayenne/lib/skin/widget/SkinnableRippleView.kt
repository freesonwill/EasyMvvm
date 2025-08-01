package arch.cayenne.lib.skin.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.AdapterView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.R
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableRippleHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper

class SkinnableRippleView : ConstraintLayout {

    private lateinit var backGroundHelper: SkinnableBackGroundHelper
    private lateinit var rippleColorHelper: SkinnableRippleHelper
    private val flowHelper = SkinnableViewFlowHelper()

    /**
     * 水波纹的颜色
     */
    private var rippleColor = 0

    /**
     * 水波纹扩散类型
     */
    private var rippleType: Int? = null

    /**
     * 背景样式
     */
    private var bgType: Int? = null

    /**
     * 放大持续时间
     */
    private var zoomDuration = 0

    /**
     * 放大比例
     */
    private var zoomScale = 0f

    /**
     * 放大动画类
     */
    private var scaleAnimation: ScaleAnimation? = null


    /**
     * 视图是否放大
     */
    private var hasToZoom: Boolean? = null

    /**
     * 是否从视图中心开始动画
     */
    private var isCentered: Boolean? = null


    /**
     * 帧速率
     */
    private var frameRate = 10

    /**
     * 水波纹持续时间
     */
    private var rippleDuration = 100


    /**
     * 水波纹透明度
     */
    private var rippleAlpha = 80


    /**
     * canvas画布执行Handler
     */
    private var canvasHandler: Handler? = null

    /**
     * 水波纹画笔
     */
    private var paint: Paint? = null


    /**
     * 水波纹扩散内边距
     */
    private var ripplePadding = 0

    /**
     * 控件背景圆角半径
     */
    private var rippleCorner = 0f


    /**
     * 手势监听类
     */
    private var gestureDetector: GestureDetector? = null


    /**
     * 水波纹动画是否开始
     */
    private var animationRunning = false


    /**
     * 时间统计
     */
    private var timer = 0


    /**
     * 时间间隔
     */
    private var timerEmpty = 0

    /**
     * 水波纹持续时间间隔
     */
    private var durationEmpty = -1

    /**
     * 最大圆半径
     */
    private var radiusMax = 0f

    /**
     * 水波纹圆的坐标点
     */
    private var x = -1f
    private var y = -1f

    private var originBitmap: Bitmap? = null

    private var onCompletionListener: OnRippleCompleteListener? = null


    /**
     * 视图的宽和高
     */
    private var WIDTH = 0

    private var HEIGHT = 0

    /**
     * 定义水波纹类型
     */
    enum class RippleType(var type: Int) {
        SIMPLE(0),
        DOUBLE(1),
        RECTANGLE(2)
    }

    /**
     * 水波纹更新波纹Runnable
     */
    private val runnable = Runnable { invalidate() }

    /**
     * 定义回调函数，当水波纹效果完成时调用
     */
    interface OnRippleCompleteListener {
        fun onComplete(rippleView: SkinnableRippleView?)
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {
        initView(context, attrs, defStyleAttr)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        flowHelper.startSkinFlow(findViewTreeLifecycleOwner()?.lifecycleScope) {
            backGroundHelper.updateSkin()
            rippleColorHelper.updateSkin()
        }
    }

    override fun onDetachedFromWindow() {
        flowHelper.destroyFlow()
        super.onDetachedFromWindow()
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        backGroundHelper = SkinnableBackGroundHelper(this)
        rippleColorHelper = SkinnableRippleHelper(this)
        backGroundHelper.loadFromAttributes(attrs, defStyleAttr)
        rippleColorHelper.loadFromAttributes(attrs, defStyleAttr)
        if (isInEditMode) {
            return
        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleView)
        rippleType = typedArray.getInt(R.styleable.RippleView_rv_type, 0)
        bgType = rippleColorHelper.bgType
        rippleCorner = rippleColorHelper.rippleCorner.toFloat()
        hasToZoom = typedArray.getBoolean(R.styleable.RippleView_rv_zoom, false)
        isCentered = typedArray.getBoolean(R.styleable.RippleView_rv_centered, false)
        rippleDuration =
            typedArray.getInteger(R.styleable.RippleView_rv_rippleDuration, rippleDuration)
        rippleAlpha = typedArray.getInteger(R.styleable.RippleView_rv_alpha, rippleAlpha)
        ripplePadding = typedArray.getDimensionPixelSize(R.styleable.RippleView_rv_ripplePadding, 0)
        canvasHandler = Handler()
        zoomScale = typedArray.getFloat(R.styleable.RippleView_rv_zoomScale, 1.03f)
        zoomDuration = typedArray.getInt(R.styleable.RippleView_rv_zoomDuration, 50)
        typedArray.recycle()
        paint = Paint()
        paint!!.isAntiAlias = true
        paint!!.style = Paint.Style.FILL
        paint!!.color = rippleColorHelper.rippleColor
        paint!!.alpha = rippleAlpha
        this.setWillNotDraw(false)
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onLongPress(event: MotionEvent) {
                super.onLongPress(event)
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                return true
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }
        })
        //开启cache来绘制视图
        this.isDrawingCacheEnabled = true
        this.isClickable = true
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (animationRunning) {
            canvas.save()
            val path = Path()
            val rect = RectF(0f, 0f, WIDTH.toFloat(), HEIGHT.toFloat())
            when (bgType) {
                0 -> {
                    path.addRoundRect(rect, rippleCorner, rippleCorner, Path.Direction.CW)
                }

                1 -> {
                    path.addRoundRect(
                        rect,
                        floatArrayOf(
                            rippleCorner,
                            rippleCorner,
                            rippleCorner,
                            rippleCorner,
                            0f,
                            0f,
                            0f,
                            0f
                        ),
                        Path.Direction.CW
                    )
                }

                2 -> {
                    path.addRect(
                        RectF(0f, 0f, WIDTH.toFloat(), HEIGHT.toFloat()),
                        Path.Direction.CW
                    )
                }

                else -> {
                    path.addRoundRect(
                        rect,
                        floatArrayOf(
                            0f,
                            0f,
                            0f,
                            0f,
                            rippleCorner,
                            rippleCorner,
                            rippleCorner,
                            rippleCorner
                        ),
                        Path.Direction.CW
                    )
                }
            }
            canvas.clipPath(path)
            if (rippleDuration <= timer * frameRate) {
                animationRunning = false
                timer = 0
                durationEmpty = -1
                timerEmpty = 0
                //android 23 会自动调用canvas.restore()；
                if (Build.VERSION.SDK_INT != 23) {
                    canvas.restore()
                }
                invalidate()
                onCompletionListener?.onComplete(this)
                return
            } else {
                canvasHandler!!.postDelayed(runnable, frameRate.toLong())
            }

            if (timer == 0) {
                canvas.save()
            }
            canvas.drawCircle(
                x, y, (radiusMax * ((timer.toFloat() * frameRate) / rippleDuration)),
                paint!!
            )
            paint!!.color = Color.parseColor("#ffff4444")


            if ((rippleType == 1) && originBitmap != null && ((timer.toFloat() * frameRate) / rippleDuration) > 0.4f) {
                if (durationEmpty == -1) {
                    durationEmpty = rippleDuration - timer * frameRate
                }
                timerEmpty++
                val tmpBitmap =
                    getCircleBitmap(((radiusMax) * ((timerEmpty.toFloat() * frameRate) / (durationEmpty))).toInt())
                canvas.drawBitmap(tmpBitmap, 0f, 0f, paint)
                tmpBitmap.recycle()
            }
            paint!!.color = rippleColorHelper.rippleColor

            if (rippleType == 1) {
                if (((timer.toFloat() * frameRate) / rippleDuration) > 0.6f) {
                    paint!!.alpha =
                        (rippleAlpha - ((rippleAlpha) * ((timerEmpty.toFloat() * frameRate) / (durationEmpty)))).toInt()
                } else {
                    paint!!.alpha = rippleAlpha
                }
            } else {
                paint!!.alpha =
                    (rippleAlpha - ((rippleAlpha) * ((timer.toFloat() * frameRate) / rippleDuration))).toInt()
            }
            timer++
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        WIDTH = w
        HEIGHT = h
        scaleAnimation =
            ScaleAnimation(1.0f, zoomScale, 1.0f, zoomScale, (w / 2).toFloat(), (h / 2).toFloat())
        scaleAnimation!!.duration = zoomDuration.toLong()
        scaleAnimation!!.repeatMode = Animation.REVERSE
        scaleAnimation!!.repeatCount = 1
    }


    /**
     * 启动水波纹动画，通过MotionEvent事件
     *
     * @param event
     */
    fun animateRipple(event: MotionEvent) {
        createAnimation(event.x, event.y)
    }

    /**
     * 启动水波纹动画，通过x，y坐标
     *
     * @param x
     * @param y
     */
    fun animateRipple(x: Float, y: Float) {
        createAnimation(x, y)
    }


    private fun createAnimation(x: Float, y: Float) {
        if (this.isEnabled && !animationRunning) {
            if (hasToZoom!!) {
                this.startAnimation(scaleAnimation)
            }
            radiusMax = WIDTH.toFloat()
            if (isCentered!! || rippleType == 1) {
                this.x = (measuredWidth / 2).toFloat()
                this.y = (measuredHeight / 2).toFloat()
            } else {
                this.x = x
                this.y = y
            }
            animationRunning = true
            if (rippleType == 1 && originBitmap == null) {
                originBitmap = getDrawingCache(true)
            }
            invalidate()
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gestureDetector!!.onTouchEvent(event)) {
            animateRipple(event)
            sendClickEvent(false)
        }
        return super.onTouchEvent(event)
    }


    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        this.onTouchEvent(event)
        return super.onInterceptTouchEvent(event)
    }


    /**
     * 发送一个点击事件，如果父视图是ListView实例
     *
     * @param isLongClick
     */
    private fun sendClickEvent(isLongClick: Boolean) {
        if (parent is AdapterView<*>) {
            val adapterView = parent as AdapterView<*>
            val position = adapterView.getPositionForView(this)
            val id = adapterView.getItemIdAtPosition(position)
            if (isLongClick) {
                if (adapterView.onItemLongClickListener != null) {
                    adapterView.onItemLongClickListener.onItemLongClick(
                        adapterView,
                        this,
                        position,
                        id
                    )
                }
            } else {
                if (adapterView.onItemClickListener != null) {
                    adapterView.onItemClickListener!!
                        .onItemClick(adapterView, this, position, id)
                }
            }
        }
    }


    /**
     * 设置水波纹的颜色
     *
     * @param rippleColor
     */
    fun setRippleColor(rippleColor: Int) {
        this.rippleColor = resources.getColor(rippleColor)
    }

    fun getRippleColor(): Int {
        return rippleColor
    }

    fun isCentered(): Boolean {
        return isCentered!!
    }

    /**
     * 设置水波纹动画是否开始从父视图中心开始，默认为false
     *
     * @param isCentered
     */
    fun setCentered(isCentered: Boolean?) {
        this.isCentered = isCentered
    }

    fun getRipplePadding(): Int {
        return ripplePadding
    }

    /**
     * 设置水波纹内边距，默认为0dip
     *
     * @param ripplePadding
     */
    fun setRipplePadding(ripplePadding: Int) {
        this.ripplePadding = ripplePadding
    }

    fun isZooming(): Boolean {
        return hasToZoom!!
    }

    /**
     * 在水波纹结束后，是否有放大动画，默认为false
     *
     * @param hasToZoom
     */
    fun setZooming(hasToZoom: Boolean?) {
        this.hasToZoom = hasToZoom
    }

    fun getZoomScale(): Float {
        return zoomScale
    }

    /**
     * 设置放大动画比例
     *
     * @param zoomScale
     */
    fun setZoomScale(zoomScale: Float) {
        this.zoomScale = zoomScale
    }

    fun getZoomDuration(): Int {
        return zoomDuration
    }

    /**
     * 设置放大动画持续时间，默认为200ms
     *
     * @param zoomDuration
     */
    fun setZoomDuration(zoomDuration: Int) {
        this.zoomDuration = zoomDuration
    }

    fun getRippleDuration(): Int {
        return rippleDuration
    }

    /**
     * 设置水波纹动画持续时间，默认为400ms
     *
     * @param rippleDuration
     */
    fun setRippleDuration(rippleDuration: Int) {
        this.rippleDuration = rippleDuration
    }

    fun getFrameRate(): Int {
        return frameRate
    }

    /**
     * 设置水波纹动画的帧速率，默认为10
     *
     * @param frameRate
     */
    fun setFrameRate(frameRate: Int) {
        this.frameRate = frameRate
    }

    fun getRippleAlpha(): Int {
        return rippleAlpha
    }

    /**
     * 设置水波纹动画的透明度，默认为90，取值为0到255之间
     *
     * @param rippleAlpha
     */
    fun setRippleAlpha(rippleAlpha: Int) {
        this.rippleAlpha = rippleAlpha
    }


    /**
     * 绘制扩散背景范围视图bitmap
     *
     * @param radius
     * @return
     */
    private fun getCircleBitmap(radius: Int): Bitmap {
        val output =
            Bitmap.createBitmap(
                originBitmap!!.width,
                originBitmap!!.height,
                Bitmap.Config.ARGB_8888
            )
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(
            (x - radius).toInt(),
            (y - radius).toInt(),
            (x + radius).toInt(),
            (y + radius).toInt()
        )

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(x, y, radius.toFloat(), paint)
        //出来两图交叉情况
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(originBitmap!!, rect, rect, paint)
        return output
    }
}