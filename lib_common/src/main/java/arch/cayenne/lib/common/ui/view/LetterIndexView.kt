package arch.cayenne.lib.common.ui.view

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.MotionEventCompat
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.DimensionExt.sp2px
import arch.cayenne.lib.skin.widget.SportView

/**
 * @author: caomei
 * @date: 2025/4/29 14:18
 * @description: 字母选择指示器
 */
class LetterIndexView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : SportView(mContext, attrs, defStyleAttr) {
    private val letterList: MutableList<String> = ArrayList()

    private var paint: Paint? = null

    private var itemHeight = 0f // 字母每一项的高度
    private var fontSize = 0f

    private var currentPosition = -1

    private var circleRadius = 0f
    private var circleRadiusExtra = 0f // 默认的选中时圆形的边距，如果不设置，圆形无法完成包含字母（无需自定义）
    private var circlePadding = 0 // 选中时圆形的边距，可自定义
    private var circleColor = 0
    private var drawCircleActionUp = false

    private var itemPadding = 0

    private var textColor = 0
    private var textSelectedColor = 0

    private var stateChangeListener: OnStateChangeListener? = null
    private var eventAction = 0

    private var pop: PopupWindow? = null
    private var popTextView: TextView? = null
    private var popImageView: ImageView? = null
    private var popView: View? = null
    private var showLetterPop = true

    private var activityRootView: View? = null
    private var mDensity = 0f

    init {
        init(attrs, mContext)
    }

    private fun init(attrs: AttributeSet?, context: Context) {
        mDensity = getContext().resources.displayMetrics.density
        loadDefaultSetting()
        if (attrs != null) {
            val array = getContext().obtainStyledAttributes(attrs, R.styleable.LetterIndex)
            val textSize = array.getInteger(R.styleable.LetterIndex_text_size, 0)
            if (textSize != 0) fontSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                textSize.toFloat(),
                resources.displayMetrics
            )
            val textColor = array.getColor(R.styleable.LetterIndex_text_color, 0)
            if (textColor != 0) this.textColor = textColor
            val textSelectedColor = array.getColor(R.styleable.LetterIndex_text_selected_color, 0)
            if (textSelectedColor != 0) this.textSelectedColor = textSelectedColor
            val circlePadding =
                array.getDimension(R.styleable.LetterIndex_circle_padding, 0f).toInt()
            if (circlePadding != 0) this.circlePadding = circlePadding
            val circleColor = array.getColor(R.styleable.LetterIndex_circle_color, 0)
            if (circleColor != 0) this.circleColor = circleColor
            val itemSpace = array.getDimension(R.styleable.LetterIndex_item_space, 0f).toInt()
            if (itemSpace != 0) this.itemPadding = itemSpace / 2
            val drawCircleActionUp =
                array.getBoolean(R.styleable.LetterIndex_draw_circle_action_up, true)
            this.drawCircleActionUp = drawCircleActionUp
            showLetterPop = array.getBoolean(R.styleable.LetterIndex_show_pop, true)
            array.recycle()
        }
        paint = Paint()
        paint!!.color = Color.YELLOW
        paint!!.textSize = fontSize.toFloat()
        paint!!.textAlign = Paint.Align.CENTER
        paint!!.isAntiAlias = true



        loadDefaultLetters()
        calculateCircleRadius(true)
        initPopupwindow()
        if (getContext() is Activity) activityRootView =
            getActivityRootView(getContext() as Activity)
        invalidate()
    }



    /**
     * 默认数据
     */
    private fun loadDefaultSetting() {
        fontSize =10.sp2px
        circleRadiusExtra = 2f.dp2px.toFloat()
        circlePadding =2f.dp2px
        itemPadding =5f.dp2px
        textColor = Color.BLACK
        textSelectedColor = Color.WHITE
        circleColor = Color.RED
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        var num=0
        for (i in 0 until letterList.size) {
            if(letterList[i]!=DATA_TYPE){
                num=i
                break
            }
        }
        if (heightMode == MeasureSpec.EXACTLY) { // 固定高度
            itemHeight = height / letterList.size
        } else { // 自适应高度
            itemHeight = (paint!!.measureText(letterList[num]).toInt() + itemPadding * 2).toFloat()
            height = itemHeight * letterList.size
        }
        height += (paddingTop + paddingBottom).toFloat()
        val width = (paint!!.measureText(letterList[num]) + paddingLeft + paddingRight).toInt()
        setMeasuredDimension(width, height.toInt())
    }

    /**
     * 计算背景的大小
     */
    private fun calculateCircleRadius(force: Boolean) {
        if (force || circleRadius == 0f) {
            var num=0
            for (i in 0 until letterList.size) {
                if(letterList[i]!=DATA_TYPE){
                    num=i
                    break
                }
            }
            circleRadius =
                paint!!.measureText(letterList[num]) / 2 + circleRadiusExtra + circlePadding
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in letterList.indices) {
            val itemCenterY = i * itemHeight + itemHeight / 2 + paddingTop
            val metrics = paint!!.fontMetrics
            val baseLineY =
                (itemCenterY + (metrics.bottom - metrics.top) / 2 - metrics.bottom).toInt()

            //            boolean showCircle = (eventAction != MotionEvent.ACTION_UP || (eventAction == MotionEvent.ACTION_UP && drawCircleActionUp));
            val showCircle =
                ((eventAction != MotionEvent.ACTION_UP && eventAction != MotionEvent.ACTION_CANCEL) || (eventAction == MotionEvent.ACTION_UP && drawCircleActionUp))
            if (currentPosition == i && showCircle) {
                paint!!.color = circleColor
                canvas.drawCircle((width / 2).toFloat(), itemCenterY, circleRadius, paint!!)
            }

            paint!!.color = if (i == currentPosition && showCircle) textSelectedColor else textColor
            if (letterList[i] == DATA_TYPE) {
                val bitmap = drawableToBitmap(mContext, R.drawable.host_icon)
                canvas.drawBitmap(
                    bitmap,
                    (width / 2).toFloat() -6.dp2px,
                    (baseLineY - 10.dp2px).toFloat(),
                    paint
                )
            } else {
                canvas.drawText(
                    letterList[i], (width / 2).toFloat(), baseLineY.toFloat(),
                    paint!!
                )
            }
        }
    }


    private fun getMotionEventY(ev: MotionEvent, activePointerId: Int): Float {
        val index = MotionEventCompat.findPointerIndex(ev, activePointerId)
        if (index < 0) {
            return (-1).toFloat()
        }
        return MotionEventCompat.getY(ev, index)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eventAction = event.action
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (showLetterPop && activityRootView != null) {
                if (pop == null) initPopupwindow()
                val popHeight = pop!!.contentView.height // 获取 PopupWindow 的高度
                val yOffset = event.rawY.toInt() - (popHeight / 2) // 中心对齐
                pop!!.showAtLocation(activityRootView, Gravity.TOP or Gravity.RIGHT, 100, yOffset)
            }

            MotionEvent.ACTION_MOVE -> {
                val popHeight = pop!!.contentView.height // 获取 PopupWindow 的高度
                val yOffset = event.rawY.toInt() - (popHeight / 2) // 中心对齐
                pop!!.update(100, yOffset, -1, -1)
            }

            MotionEvent.ACTION_UP -> if (pop != null && pop!!.isShowing) pop!!.dismiss()
            MotionEvent.ACTION_CANCEL -> if (pop != null && pop!!.isShowing) pop!!.dismiss()
        }

        val position = getCurrentPosition(event.rawY)
        if (currentPosition == position && this.eventAction == eventAction) return true
        currentPosition = position
        this.eventAction = eventAction
        if (letterList[currentPosition] == DATA_TYPE) {
            popImageView!!.visibility = VISIBLE
            popTextView!!.visibility = GONE
            popImageView!!.setImageDrawable(
                ContextCompat.getDrawable(
                    mContext,
                    R.drawable.host_icon
                )
            )
        } else {
            popImageView!!.visibility = GONE
            popTextView!!.visibility = VISIBLE
            popTextView!!.text = letterList[currentPosition]
        }

        invalidate()
        if (stateChangeListener != null) stateChangeListener!!.onStateChange(
            eventAction,
            currentPosition,
            letterList[currentPosition],
            (itemHeight * currentPosition + itemHeight / 2).toInt() + paddingTop
        )
        return true
    }


    private fun getCurrentPosition(rawY: Float): Int {
        // 获取视图在屏幕上的位置
        val location = IntArray(2)
        getLocationOnScreen(location) // 获取当前视图左上角在屏幕上的坐标
        val viewTopOnScreen = location[1] // 当前视图顶部的 Y 坐标

        // 将全局 Y 坐标转换为相对于视图的 Y 坐标
        var y = rawY - viewTopOnScreen
        // 开始计算位置
        if (y >= height) return letterList.size - 1 // 超出底部时返回最后一个位置

        if (y <= 0) return 0 // 超出顶部时返回第一个位置


        y -= paddingTop.toFloat() // 去除顶部内边距的影响
        val pos = (y / itemHeight).toInt() // 计算位置索引
        if (pos > letterList.size - 1) return letterList.size - 1 // 防止越界


        return pos
    }

    /**
     * 加载默认的26个字母
     */
    private fun loadDefaultLetters() {
        letterList.add(DATA_TYPE)
        for (i in 0..25) {
            letterList.add((65 + i).toChar().uppercaseChar().toString())
        }
    }

    private fun initPopupwindow() {
        popTextView = createPopTextView()
        popView = createPopView()
        pop = PopupWindow(popView,90f.dp2px, 90f.dp2px)

        pop!!.setBackgroundDrawable(shapeDrawable)
    }


    private fun getActivityRootView(activity: Activity): View? {
        var rootView = activity.window.decorView.findViewById<View>(android.R.id.content)
        if (rootView == null) rootView =
            (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
        return rootView
    }

    private fun createPopTextView(): TextView {
        val textView = TextView(context)
        val params: ViewGroup.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        textView.layoutParams = params
        textView.gravity = Gravity.CENTER
        textView.setTextColor(Color.WHITE)
        textView.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            25f,
            resources.displayMetrics
        )
        return textView
    }

    private fun createPopView(): View {
        val viewRelative = RelativeLayout(context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val textView = TextView(context)
        val paramsText: ViewGroup.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        textView.layoutParams = params
        textView.gravity = Gravity.CENTER
        textView.setTextColor(Color.WHITE)
        textView.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            25f,
            resources.displayMetrics
        )
        viewRelative.addView(textView)
        popTextView = textView

        val imageView = ImageView(context)
        val paramsImage: ViewGroup.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.layoutParams = paramsImage
        viewRelative.addView(imageView)
        popImageView = imageView


        viewRelative.layoutParams = params
        return viewRelative
    }

    private fun createPopImageView(): ImageView {
        val imageView = ImageView(context)
        val params: ViewGroup.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.layoutParams = params

        return imageView
    }

    private val shapeDrawable: Drawable
        /**
         * 选择放大的背景 创建默认的Shape背景
         */
        get() {
            val raduis = 20
            val innerRadii = floatArrayOf(
                raduis.toFloat(),
                raduis.toFloat(),
                raduis.toFloat(),
                raduis.toFloat(),
                raduis.toFloat(),
                raduis.toFloat(),
                raduis.toFloat(),
                raduis.toFloat()
            )
            val roundRectShape = RoundRectShape(innerRadii, RectF(), innerRadii)
            val drawable = ShapeDrawable(roundRectShape)
            drawable.paint.color = Color.argb(78, 128, 125, 120)
            drawable.paint.style = Paint.Style.FILL
            return drawable
        }

    interface OnStateChangeListener {
        fun onStateChange(eventAction: Int, position: Int, letter: String?, itemCenterY: Int)
    }

    fun setOnStateChangeListener(listener: OnStateChangeListener?): LetterIndexView {
        this.stateChangeListener = listener
        return this
    }

    val letterCount: Int
        /**
         * 当前字母总数
         */
        get() = letterList.size

    /**
     * 可在字母先后添加额外的字母
     */
    fun addLetter(position: Int, vararg letter: String): LetterIndexView {
        if (letter != null && letter.size > 0) {
            for (l in letter) letterList.add(position, l)
            invalidate()
        }
        return this
    }

    /**
     * 设置字体大小
     *
     * @param size 单位 SP
     */
    fun setFontSize(size: Int): LetterIndexView {
        fontSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            size.toFloat(),
            resources.displayMetrics
        )
        invalidate()
        return this
    }

    /**
     * 设置字体颜色
     */
    fun setTextColor(@ColorInt color: Int): LetterIndexView {
        textColor = color
        invalidate()
        return this
    }

    /**
     * 设置字体选中时的颜色
     */
    fun setTextSelectedColor(@ColorInt color: Int): LetterIndexView {
        textSelectedColor = color
        invalidate()
        return this
    }

    /**
     * 设置选中时的圆形背景颜色，如果不需要圆形背景，可设置完全透明颜色即可
     */
    fun setCircleColor(@ColorInt color: Int): LetterIndexView {
        circleColor = color
        invalidate()
        return this
    }

    /**
     * 设置选中时的圆形的内边距
     */
    fun setCirclePadding(padding: Int): LetterIndexView {
        circlePadding =padding.dp2px
        invalidate()
        return this
    }

    /**
     * 设置两个字母Item之间的距离
     *
     * @param space 单位 DP
     * @return
     */
    fun setItemSpace(space: Int): LetterIndexView {
        itemPadding = space.dp2px/ 2
        invalidate()
        return this
    }

    /**
     * 设置 手势离开屏幕后选中字母是否显示圆形背景，默认 false
     *
     * @param draw
     * @return
     */
    fun setDrawCircleActionUp(draw: Boolean): LetterIndexView {
        drawCircleActionUp = draw
        invalidate()
        return this
    }

    /**
     * 设置 滑动时，是否显示默认的 提示 泡泡窗口，默认 true，不支持自定义，如需自定义 可通过setOnStateChangeListener监听状态做相应的处理
     */
    fun setShowLetterPop(showLetterPop: Boolean): LetterIndexView {
        this.showLetterPop = showLetterPop
        invalidate()
        return this
    }

   private fun drawableToBitmap(context: Context, drawableResId: Int): Bitmap {
        // 获取 Drawable
        val drawable = ContextCompat.getDrawable(context, drawableResId)
        requireNotNull(drawable) { "Drawable resource not found!" }

        // 如果 Drawable 已经是 BitmapDrawable，直接返回 Bitmap
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        // 创建一个 Bitmap
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // 创建 Canvas 并将 Drawable 转换为 Bitmap
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    companion object {
        // icon 类型的数据，可以显示图片
        private const val DATA_TYPE = "icon"

    }
}
