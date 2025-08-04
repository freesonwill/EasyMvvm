package arch.cayenne.module.home.ui.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnLayout
import androidx.fragment.app.FragmentManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.R as RC
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.extractDate
import arch.cayenne.lib.common.utils.ext.toChineseMonth
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.HomeTourPopupCalendarViewBinding
import arch.cayenne.module.home.ui.viewmodel.HomeCalendarViewModel
import arch.cayenne.module.home.utils.DateUtils
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import galaxy.common.proto.Common
import kotlin.reflect.KClass

class HomeCalendarFragment private constructor() : BaseFragment<HomeCalendarViewModel, HomeTourPopupCalendarViewBinding>() {
    override val vbClass: KClass<HomeTourPopupCalendarViewBinding>
        get() = HomeTourPopupCalendarViewBinding::class
    override val vmClass: KClass<HomeCalendarViewModel>
        get() = HomeCalendarViewModel::class

    enum class AnimState {
        EXPANDING, EXPAND, COLLAPSING, COLLAPSE
    }

    private val defaultAnimDuration = 300L

    private var onDataSelectedListener: ((String) -> Unit)? = null
    private var onCalendarDismissListener: (() -> Unit)? = null
    private var onResetDateListener: (()-> Unit)? = null
    private var onDismissListener: (()-> Unit)? = null
    private var range: List<Common.DailyMatchCount>? = null
    private var marginTop: Int = 0

    private var maskView: View? = null
    private var heightAnimator: ValueAnimator? = null
    private var currentAnimState: AnimState? = null
    private var tabSelectedDate: String = "0"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(mBinding.clCalendarPopupRoot) {
            visibility = View.INVISIBLE
            layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                topMargin = this@HomeCalendarFragment.marginTop
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            clCalendarPopupRoot.apply {
                bringToFront()
                setBackgroundResource(
                    R.drawable.shape_home_calendar_background.getSkinnableResourceId()
                )
            }

            //update weekview color
            calendarView.apply {
                setWeeColor(
                    R.color.home_calendar_background.getSkinnableColor(),
                    RC.color.secondary_text.getSkinnableColor()
                )
                //update calendarView textColor
                setTextColor(
                    RC.color.main_text.getSkinnableColor(),
                    RC.color.explanation_text.getSkinnableColor(),
                    RC.color.explanation_text.getSkinnableColor(),
                    RC.color.main_text.getSkinnableColor(),
                    RC.color.main_text.getSkinnableColor(),
                )
                setSelectedColor(
                    resources.getColor(R.color.home_calendar_selected_theme_color, null),
                    resources.getColor(RC.color.white, null),
                    resources.getColor(RC.color.white, null)
                )
            }

            //update current month title text color
            tvCurrentMonth.setTextColor(
                RC.color.secondary_text.getSkinnableColor()
            )

            //update previous and next month button drawable
            ivLeftClick.setImageResource(
                R.drawable.ic_calendar_arrow_left.getSkinnableResourceId()
            )
            ivRightClick.setImageResource(
                R.drawable.ic_calendar_arrow_right.getSkinnableResourceId()
            )

            //update calendarView button
            calendarBtnCancel.apply {
                setBackgroundResource(
                    R.drawable.shape_home_calendar_cancel.getSkinnableResourceId()
                )
                setTextColor(
                    RC.color.title_bar.getSkinnableColor()
                )
            }
            calendarBtnOk.setBackgroundResource(
                R.drawable.shape_home_calendar_ok.getSkinnableResourceId()
            )

            maskView?.background = createMaskGradient()
            setSchemeDate()
            expandView()
        }
    }

    @SuppressLint("DefaultLocale")
    override fun initListener() {
        with(mBinding) {
            // 獲取當前日期
            val year = "${calendarView.selectedCalendar.year}"
            val month = String.format("%02d", calendarView.selectedCalendar.month)
            val day = String.format("%02d", calendarView.selectedCalendar.day)
            var selectedDate =
                if (tabSelectedDate == "0") "$year$month$day"
                else tabSelectedDate

            // 透過 binding 操作 Popup 內部的 View
            ivRightClick.clickNoRepeat {
                calendarView.scrollToNext(true)
            }
            ivLeftClick.clickNoRepeat {
                calendarView.scrollToPre(true)
            }

            calendarBtnCancel.clickNoRepeat {
                calendarView.scrollToCurrent()
                val minRangeDate = calendarView.minRangeCalendar
                calendarView.scrollToCalendar(
                    minRangeDate.year,
                    minRangeDate.month,
                    minRangeDate.day
                )
                onResetDateListener?.invoke()
                collapseView()
            }
            calendarBtnOk.clickNoRepeat {
                setSelectedDateTab(selectedDate)
                collapseView()
            }

            setCurrentDate()
            calendarView.setOnCalendarSelectListener(object :
                CalendarView.OnCalendarSelectListener {
                override fun onCalendarOutOfRange(calendar: Calendar?) = Unit
                override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
                    if (calendar == null) return
                    selectedDate = "$calendar"
                    tvCurrentMonth.text =
                        resources.getString(
                            R.string.format_month_year,
                            calendar.month.toChineseMonth(),
                            calendar.year.toString()
                        )

                    //控制左右按鈕的enabled
                    if (calendar.month > calendarView.curMonth) {
                        ivRightClick.isEnabled = false
                        ivLeftClick.isEnabled = true
                    } else {
                        ivRightClick.isEnabled = true
                        ivLeftClick.isEnabled = false
                    }
                }
            })

            maskView?.setOnClickListener {
                when(currentAnimState) {
                    AnimState.EXPANDING,
                    AnimState.EXPAND -> collapseView()
                    else -> expandView()
                }
            }
        }
    }

    override suspend fun createObserver() = Unit

    private fun Int.getSkinnableColor(): Int{
        return SkinnableResourceManager.getColor(requireContext(), this)
    }

    private fun Int.getSkinnableResourceId(): Int {
        return SkinnableResourceManager.getTargetResourceId(requireContext(), this)
    }

    private fun getFullyHeight(): Int {
        with(mBinding.clCalendarPopupRoot) {
            measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            return measuredHeight
        }
    }

    private fun expandView() {
        with(mBinding.clCalendarPopupRoot) {
            layoutParams = layoutParams.apply { height = 1 }
            doOnLayout {
                val currentHeight = (heightAnimator?.animatedValue as? Int) ?: height
                val fullyHeight = getFullyHeight()
                val startHeight =
                    if(fullyHeight == currentHeight) 1 else currentHeight
                heightAnimator?.cancel()

                heightAnimator = ValueAnimator.ofInt(startHeight, fullyHeight).apply {
                    addUpdateListener {
                        updateHeight(it.animatedValue as Int)
                        requireView()
                    }
                    duration = defaultAnimDuration
                    interpolator = DecelerateInterpolator()
                    doOnStart {
                        currentAnimState = AnimState.EXPANDING
                        layoutParams =
                            layoutParams.apply {
                                height = startHeight
                            }
                        visibility = View.VISIBLE
                        setMaskViewAlpha(true)
                    }
                    doOnEnd { currentAnimState = AnimState.EXPAND }
                    start()
                }
            }
        }
    }

    private fun collapseView() {
        with(mBinding.clCalendarPopupRoot) {
            val currentHeight = (heightAnimator?.animatedValue as? Int) ?: height
            heightAnimator?.cancel()

            heightAnimator = ValueAnimator.ofInt(currentHeight, 1).apply {
                addUpdateListener {
                    updateHeight(it.animatedValue as Int)
                    requireView()
                }
                duration = defaultAnimDuration
                interpolator = DecelerateInterpolator()
                doOnStart {
                    currentAnimState = AnimState.COLLAPSING
                    setMaskViewAlpha(false)
                }
                doOnEnd {
                    currentAnimState = AnimState.COLLAPSE
                    visibility = View.INVISIBLE
                    mBinding.clCalendarPopupRoot.postDelayed({
                        if(currentAnimState == AnimState.COLLAPSE) {
                            dismiss()
                            onDismissListener?.invoke()
                        }
                    }, 100L)
                }
                start()
            }
        }
    }

    private fun updateHeight(height: Int) {
        mBinding.clCalendarPopupRoot.apply {
            layoutParams = layoutParams.apply { this.height = height }
            requireView()
        }
    }

    private fun createMaskGradient(): Drawable {
        val defaultColor = 0x80000000
        val defaultStartAt = 0.2f
        val defaultStopAt = 0.5f
        return object : Drawable() {
            private val paint = Paint()
            private lateinit var shader: LinearGradient

            override fun onBoundsChange(bounds: Rect) {
                super.onBoundsChange(bounds)
                shader = LinearGradient(
                    0f, bounds.bottom.toFloat(),
                    0f, bounds.top.toFloat(),
                    intArrayOf(defaultColor.toInt(), defaultColor.toInt(), Color.TRANSPARENT),
                    floatArrayOf(0f, defaultStartAt, defaultStopAt),
                    Shader.TileMode.CLAMP
                )
                paint.shader = shader
            }

            override fun draw(canvas: Canvas) {
                canvas.drawRect(bounds, paint)
            }

            override fun setAlpha(alpha: Int) {
                paint.alpha = alpha
            }

            @Deprecated(
                message = "Deprecated in Java",
                replaceWith = ReplaceWith("PixelFormat.OPAQUE", "android.graphics.PixelFormat")
            )
            override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

            override fun setColorFilter(colorFilter: ColorFilter?) {
                paint.colorFilter = colorFilter
            }
        }
    }

    private fun setMaskViewAlpha(visible: Boolean) {
        maskView?.let {
            it.post {
                it.animate()
                    .alpha(if (visible) 1f else 0f)
                    .setDuration(defaultAnimDuration)
                    .setListener(object: Animator.AnimatorListener{
                        override fun onAnimationStart(p0: Animator) {
                            if(visible) it.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(p0: Animator) {
                            if(!visible) it.visibility = View.GONE
                        }

                        override fun onAnimationCancel(p0: Animator) = Unit
                        override fun onAnimationRepeat(p0: Animator) = Unit
                    })
                    .start()
            }
        }
    }

    fun show(manager: FragmentManager, containerId: Int, tabSelectedDate: String) {
        this.tabSelectedDate = tabSelectedDate
        val lastFragment = manager.findFragmentByTag(TAG)
        if (lastFragment == null || !lastFragment.isAdded) {
            manager.beginTransaction()
                .setReorderingAllowed(true)
                .add(containerId, this, this.javaClass.simpleName)
                .commit()
        }
    }

    private fun dismiss() {
        if (parentFragment != null) {
            mBinding.clCalendarPopupRoot.post {
                parentFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .remove(this)
                    .commitAllowingStateLoss()
            }
        }
    }

    private fun setCurrentDate() {
        val currentYear = mBinding.calendarView.curYear
        val currentMonth = mBinding.calendarView.curMonth
        //日期tab為全部時標記為今日
        if (tabSelectedDate == "0") {
            mBinding.calendarView.scrollToCurrent(true)
            mBinding.tvCurrentMonth.text = resources.getString(
                R.string.format_month_year,
                currentMonth.toChineseMonth(),
                currentYear.toString()
            )
        } else {
            val result = tabSelectedDate.extractDate()
            result?.let {
                val (year, month, day) = it
                with(mBinding) {
                    calendarView.scrollToCalendar(year, month, day)
                    tvCurrentMonth.text = resources.getString(
                        R.string.format_month_year,
                        month.toChineseMonth(),
                        year.toString()
                    )
                }
            } ?: run {
                val curYear = mBinding.calendarView.curYear
                val curMonth = mBinding.calendarView.curMonth
                mBinding.calendarView.scrollToCurrent(true)
                mBinding.tvCurrentMonth.text = resources.getString(
                    R.string.format_month_year,
                    curMonth.toChineseMonth(),
                    curYear.toString()
                )
            }
        }
    }

    //選取日期後按確定時連動至早盤日期tab,選取對應的日期
    private fun setSelectedDateTab(selectedDate: String) {
        onDataSelectedListener?.invoke(DateUtils.getMonthDay(selectedDate))
        // 通常選擇完資料後，會自動關閉 popup
        collapseView()
    }

    //設定標記紅色日期及可選取日期範圍
    private fun setSchemeDate() {
        range?.let { list ->
            val map: MutableMap<String, Calendar> = HashMap()
            for (date in list) {
                //API回傳資料，有比賽的日期才需要標記紅字
                val dateArray = date.day.split("-")
                if (date.count > 0) {
                    val schemeCalendar = getSchemeCalendar(
                        dateArray[0].toInt(),
                        dateArray[1].toInt(),
                        dateArray[2].toInt()
                    )
                    map[schemeCalendar.toString()] = schemeCalendar
                }
            }
            //可選取日期區間為未來7天
            val startDateTriple = list.first()
            val startDateArray = startDateTriple.day.split("-")
            val endDateTriple = list.last()
            val endDateArray = endDateTriple.day.split("-")
            //設定可以選取的日期區間，目前設定為31天
            with(mBinding) {
                calendarView.setRange(
                    startDateArray[0].toInt(),
                    startDateArray[1].toInt(),
                    startDateArray[2].toInt(),
                    endDateArray[0].toInt(),
                    endDateArray[1].toInt(),
                    endDateArray[2].toInt()
                )
                calendarView.setSchemeDate(map)
                calendarView.scrollToCalendar(
                    startDateArray[0].toInt(),
                    startDateArray[1].toInt(),
                    startDateArray[2].toInt()
                )
            }
        }
    }

    private fun getSchemeCalendar(
        year: Int,
        month: Int,
        day: Int
    ): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        calendar.drawIndex = 0
        return calendar
    }

    fun updateRange(range: List<Common.DailyMatchCount>) {
        this.range = range
        setSchemeDate()
    }

    /**
     * Builder 類別用於構建 HomeCalendarPopupWindow
     */
    class Builder {
        private var onDateSelectedListener: ((String) -> Unit)? = null
        private var onCalendarDismissListener: (()-> Unit)? = null
        private var onResetDateListener: (()-> Unit)? = null
        private var onDismissListener: (()-> Unit)? = null
        private var range: List<Common.DailyMatchCount>? = null
        private var marginTop: Int = 0
        private var maskView: View? = null

        /**
         * 提供一個公開的方法讓外部設定監聽器
         */
        fun setOnDateSelectedListener(listener: (String) -> Unit) = apply {
            this.onDateSelectedListener = listener
        }
        fun setOnCalendarDismissListener(listener: () -> Unit) = apply {
            this.onCalendarDismissListener = listener
        }
        fun setOnResetDateListener(listener: () -> Unit) = apply {
            this.onResetDateListener = listener
        }
        fun setOnDismissListener(listener: () -> Unit) = apply {
            this.onDismissListener = listener
        }
        fun setRange(range: List<Common.DailyMatchCount>) = apply {
            this.range = range
        }
        fun setMarginTop(value: Int) {
            this.marginTop = value
        }
        fun setMaskView(maskView: View) = apply {
            this.maskView = maskView
        }
        fun build(): HomeCalendarFragment {
            return HomeCalendarFragment().apply {
                this.onCalendarDismissListener = this@Builder.onCalendarDismissListener
                this.onResetDateListener = this@Builder.onResetDateListener
                this.onDataSelectedListener = this@Builder.onDateSelectedListener
                this.onDismissListener = this@Builder.onDismissListener
                this.range = this@Builder.range
                this.marginTop = this@Builder.marginTop
                this.maskView = this@Builder.maskView
            }
        }
    }
}
