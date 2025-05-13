package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.databinding.ViewBallControlDetailCommonLayoutBinding
import arch.cayenne.lib.skin.widget.SportImageView
import arch.cayenne.lib.skin.widget.SportTextView
import kotlin.properties.Delegates


/**
 * @author banli
 * @date 2025/4/28
 * @description :
 */
class BallControlDetailView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding: ViewBallControlDetailCommonLayoutBinding by lazy {
        ViewBallControlDetailCommonLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    }

    //比赛时长,单位分钟
    private var maxTimeM: Int = 0

    //默认一场比赛90分钟
    private val defaultMaxTime: Int = 90

    //一分钟
    private val timeUnitM = 1000L * 60

    //控件宽高
    private var mWith by Delegates.notNull<Int>()
    private var mHeight by Delegates.notNull<Int>()

    //一分钟占的宽度
    private var widthByM: Float = 0f

    //事件集合
    private val listEvent = mutableListOf<BallControlDetailEvent>()

    //红方抢断等事件
    private val redEvent = mutableListOf<BallControlDetailEvent>()

    //蓝方抢断等事件
    private val blueEvent = mutableListOf<BallControlDetailEvent>()

    //红蓝双方控球进攻意图事件
    private val ballControlEvent = mutableListOf<BallControlView.BallControlEvent>()

    init {
        maxTimeM = 90
    }

    fun createEventViews(parent: FrameLayout, isRed: Boolean) {
        parent.removeAllViews()
        val list = if (isRed) redEvent else blueEvent
        list.forEach { item ->
            val imageView = SportImageView(context).apply {
                setImageResource(R.drawable.search_icon)
                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                )
            }
            parent.addView(imageView)

            // 延迟计算边距
            imageView.post {
                val params = imageView.layoutParams as LayoutParams
                val timeM = item.timestamp / timeUnitM * 1f
                val marginLeft = (timeM * widthByM - imageView.width / 2f).toInt()
                params.setMargins(marginLeft, 0, 0, 0)
                params.gravity = Gravity.CENTER_VERTICAL
                imageView.layoutParams = params
            }
        }

    }

    fun updateEventList(backgroundColorList: List<Int>,listEvent: List<BallControlDetailEvent>) {
        this.post {
            handleEvent(listEvent)
            val lastItem = this.listEvent.last()
            checkMaxTime(lastItem.timestamp)
            createTimeViews()
            createEventViews(binding.flRedEvent, true)
            createEventViews(binding.flBlueEvent, false)
            binding.ballControlView.updateEventList(backgroundColorList,ballControlEvent,lastItem.timestamp)
        }
    }

    private fun handleEvent(listEvent: List<BallControlDetailEvent>) {
        this.listEvent.clear()
        this.redEvent.clear()
        this.blueEvent.clear()
        this.ballControlEvent.clear()
        this.listEvent.addAll(listEvent)
        this.listEvent.forEach { item ->
            when (item.eventType.value) {
                BallControlDetailEventType.BALL_CONTROL.value -> {
                    val event = BallControlView.BallControlEvent(
                        timestamp = item.timestamp,
                        isRed = item.isRed,
                        heightPercent = item.heightPercent ?: 0f
                    )
                    this.ballControlEvent.add(event)
                }

                else -> {
                    if (item.isRed) {
                        this.redEvent.add(item)
                    } else {
                        this.blueEvent.add(item)
                    }
                }
            }
        }

    }

    fun updateEvent(backgroundColorList: List<Int>,event: BallControlDetailEvent) {
        this.listEvent.add(event)
        updateEventList(backgroundColorList,listEvent)
    }

    private fun checkMaxTime(time: Long) {
        val timeM: Int = (time / timeUnitM).toInt()
        if (timeM > maxTimeM) {
            maxTimeM = timeM
        }
        if (maxTimeM < defaultMaxTime) {
            maxTimeM = defaultMaxTime
        }

        //一分钟占的宽度
        widthByM = mWith * 1f / maxTimeM

    }

    fun createTimeViews() {
        binding.flTime.removeAllViews()
        for (index in 0..6) {
            val currentIndex = index // 保存循环变量避免闭包捕获问题
            val textStr = when (currentIndex) {
                0 -> "0'"
                1 -> "15'"
                2 -> "30'"
                3 -> "45'"
                4 -> "60'"
                5 -> "75'"
                6 -> "$maxTimeM'"
                else -> {
                    "un"
                }
            }

            val textView = SportTextView(context).apply {
                text = textStr
                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                )
            }

            binding.flTime.addView(textView)

            // 延迟计算边距
            textView.post {
                val params = textView.layoutParams as LayoutParams
                when (currentIndex) {
                    0 -> params.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                    6 -> params.gravity = Gravity.END or Gravity.CENTER_VERTICAL
                    else -> {
                        val marginLeft =
                            (15 * currentIndex * widthByM - textView.width / 2f).toInt()
                        params.setMargins(marginLeft, 0, 0, 0)
                        params.gravity = Gravity.CENTER_VERTICAL
                    }
                }
                textView.layoutParams = params

            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWith = MeasureSpec.getSize(widthMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
    }


    data class BallControlDetailEvent(
        val timestamp: Long,
        val isRed: Boolean,
        val eventType: BallControlDetailEventType,
        val heightPercent: Float? = null,
        val pic: String? = null
    )

    enum class BallControlDetailEventType(val value: Int) {
        BALL_CONTROL(0),//控球
        STEAL(1),//抢断
        RED_CARD(2),//红牌
        YELLOW_CARD(3)//黄牌
    }

}