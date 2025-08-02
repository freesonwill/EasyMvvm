package arch.cayenne.module.home.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.extractDate
import arch.cayenne.lib.common.utils.ext.toChineseMonth
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.HomeTourPopupCalendarViewBinding
import arch.cayenne.module.home.utils.DateUtils
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import galaxy.common.proto.Common

/**
 * 一個與生命週期感知的 PopupWindow 基底類別，特別為 Fragment 進行了優化。
 * 它會監聽傳入的 LifecycleOwner，並在 onDestroy 事件觸發時自動 dismiss，以防止記憶體洩漏。
 * @param context Context
 * @param lifecycleOwner LifecycleOwner
 * @param bindingInflater ViewBinding 的泛型類型
 * @param builder Builder
 */
class HomeCalendarPopupWindow<VB : ViewBinding>(
    val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    bindingInflater: (LayoutInflater) -> VB,
    builder: Builder<VB>
) : DefaultLifecycleObserver { //實作 DefaultLifecycleObserver

    val binding: VB = bindingInflater(LayoutInflater.from(context))

    private val onDataSelectedListener: ((String) -> Unit)? = builder.onDateSelectedListener
    private val onCalendarDismissListener: (() -> Unit)? = builder.onCalendarDismissListener
    private val onResetDateListener: (()-> Unit)? = builder.onResetDateListener
    private val popupWindow: PopupWindow = PopupWindow(
        binding.root,
        builder.width,
        builder.height,
        builder.isFocusable
    )

    init {
        //將自己註冊為生命週期觀察者
        lifecycleOwner.lifecycle.addObserver(this)

        popupWindow.isOutsideTouchable = builder.isOutsideTouchable
        if (!builder.isOutsideTouchable && !builder.isFocusable) {
            if (popupWindow.background == null) {
                popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
        builder.animationStyle?.let { popupWindow.animationStyle = it }
        builder.onDismissListener?.let { popupWindow.setOnDismissListener(it) }
        builder.elevation?.let { popupWindow.elevation = it }

        popupWindow.inputMethodMode = PopupWindow.INPUT_METHOD_NOT_NEEDED
        popupWindow.setOnDismissListener {
            onCalendarDismissListener?.invoke()
        }
    }

    /**
     * 覆寫 onDestroy 方法
     * 當 LifecycleOwner (例如 Fragment 的 View) 被銷毀時，這個方法會被自動呼叫。
     */
    override fun onDestroy(owner: LifecycleOwner) {
        dismiss() // 自動關閉 popup，防止洩漏
    }

    /**
     * 顯示 PopupWindow，作為錨點 View 的下拉。
     * @param anchor 錨點 View
     * @param xOff X 軸偏移量
     * @param yOff Y 軸偏移量
     * @param gravity 對齊方式 (相對於錨點)
     */
    fun showAsDropDown(
        anchor: View,
        xOff: Int = 0,
        yOff: Int = 0,
        gravity: Int = Gravity.NO_GRAVITY
    ) {
        if (!popupWindow.isShowing) {
            popupWindow.showAsDropDown(anchor, xOff, yOff, gravity)
        }
    }

    private fun dismiss() {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
        //解除觀察，避免不必要的呼叫
        lifecycleOwner.lifecycle.removeObserver(this)
    }

    private fun setCurrentDate(vb: HomeTourPopupCalendarViewBinding, tabSelectedDate: String) {
        val currentYear = vb.calendarView.curYear
        val currentMonth = vb.calendarView.curMonth
        //日期tab為全部時標記為今日
        if (tabSelectedDate == "0") {
            val resources = vb.root.resources
            vb.calendarView.scrollToCurrent(true)
            vb.tvCurrentMonth.text = resources.getString(
                R.string.format_month_year,
                currentMonth.toChineseMonth(),
                currentYear.toString()
            )
        } else {
            val result = tabSelectedDate.extractDate()
            result?.let {
                val (year, month, day) = it
                with(vb) {
                    val resources = vb.root.resources
                    calendarView.scrollToCalendar(year, month, day)
                    tvCurrentMonth.text = resources.getString(
                        R.string.format_month_year,
                        month.toChineseMonth(),
                        year.toString()
                    )
                }
            } ?: run {
                val resources = vb.root.resources
                val curYear = vb.calendarView.curYear
                val curMonth = vb.calendarView.curMonth
                vb.calendarView.scrollToCurrent(true)
                vb.tvCurrentMonth.text = resources.getString(
                    R.string.format_month_year,
                    curMonth.toChineseMonth(),
                    curYear.toString()
                )
            }
        }

    }

    fun setUIListener(tabSelectedDate: String) {
        with(binding as HomeTourPopupCalendarViewBinding) {
            // 獲取當前日期
            var selectedDate = if (tabSelectedDate == "0") {
                "${this.calendarView.selectedCalendar}"
            } else tabSelectedDate
            // 透過 binding 操作 Popup 內部的 View
            this.ivRightClick.clickNoRepeat {
                this.calendarView.scrollToNext(true)
            }
            this.ivLeftClick.clickNoRepeat {
                this.calendarView.scrollToPre(true)
            }
            this.calendarBtnCancel.clickNoRepeat {
                this.calendarView.scrollToCurrent()
                val minRangeDate = calendarView.minRangeCalendar
                calendarView.scrollToCalendar(minRangeDate.year,minRangeDate.month,minRangeDate.day)
                onResetDateListener?.invoke()
                dismiss() // 關閉 Popup
            }
            this.calendarBtnOk.clickNoRepeat {
                setSelectedDateTab(selectedDate)
                dismiss()
            }
            setCurrentDate(binding, tabSelectedDate)
            this.calendarView.setOnCalendarSelectListener(object :
                CalendarView.OnCalendarSelectListener {
                override fun onCalendarOutOfRange(calendar: Calendar?) {

                }

                override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
                    if (calendar == null) return
                    selectedDate = "$calendar"
                    val resources = binding.root.resources
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
        }
    }

    //選取日期後按確定時連動至早盤日期tab,選取對應的日期
    private fun setSelectedDateTab(selectedDate: String) {
        onDataSelectedListener?.invoke(DateUtils.getMonthDay(selectedDate))
        // 通常選擇完資料後，會自動關閉 popup
        dismiss()
    }

    //設定標記紅色日期及可選取日期範圍
    fun setSchemeDate(list: List<Common.DailyMatchCount>) {
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
        with(binding as HomeTourPopupCalendarViewBinding) {
            calendarView.setRange(
                startDateArray[0].toInt(),
                startDateArray[1].toInt(),
                startDateArray[2].toInt(),
                endDateArray[0].toInt(),
                endDateArray[1].toInt(),
                endDateArray[2].toInt()
            )
            calendarView.setSchemeDate(map)
            calendarView.scrollToCalendar(startDateArray[0].toInt(),  startDateArray[1].toInt(),  startDateArray[2].toInt())
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

    fun updateCalendarSkin() {
        with(binding as HomeTourPopupCalendarViewBinding) {
            val rootContext = root.context
            //update calendarView skin root
            clCalendarPopupRoot.setBackgroundResource(
                SkinnableResourceManager.getTargetResourceId(
                    rootContext,
                    R.drawable.shape_home_calendar_background
                )
            )
            //update weekview color
            val backgroundColor = SkinnableResourceManager.getColor(
                rootContext,
                R.color.home_calendar_background
            )
            val textColor = SkinnableResourceManager.getColor(
                rootContext,
                arch.cayenne.lib.common.R.color.secondary_text
            )
            calendarView.setWeeColor(backgroundColor, textColor)
            //update current month title text color
            tvCurrentMonth.setTextColor(
                SkinnableResourceManager.getColor(
                    rootContext,
                    arch.cayenne.lib.common.R.color.secondary_text
                )
            )
            //update previous and next month button drawable
            ivLeftClick.setImageResource(
                SkinnableResourceManager.getTargetResourceId(
                    rootContext,
                    R.drawable.ic_calendar_arrow_left
                )
            )
            ivRightClick.setImageResource(
                SkinnableResourceManager.getTargetResourceId(
                    rootContext,
                    R.drawable.ic_calendar_arrow_right
                )
            )
            //update calendarView textColor
            calendarView.setTextColor(
                SkinnableResourceManager.getColor(
                    rootContext,
                    arch.cayenne.lib.common.R.color.main_text
                ),
                SkinnableResourceManager.getColor(
                    rootContext,
                    arch.cayenne.lib.common.R.color.explanation_text
                ),
                SkinnableResourceManager.getColor(
                    rootContext,
                    arch.cayenne.lib.common.R.color.explanation_text
                ),
                SkinnableResourceManager.getColor(
                    rootContext,
                    arch.cayenne.lib.common.R.color.main_text
                ),
                SkinnableResourceManager.getColor(
                    rootContext,
                    arch.cayenne.lib.common.R.color.main_text
                )
            )
            calendarView.setSelectedColor(
                rootContext.resources.getColor(R.color.home_calendar_selected_theme_color, null),
                rootContext.resources.getColor(arch.cayenne.lib.common.R.color.white, null),
                rootContext.resources.getColor(arch.cayenne.lib.common.R.color.white, null)
            )
            //update calendarView button
            calendarBtnCancel.setBackgroundResource(
                SkinnableResourceManager.getTargetResourceId(
                    rootContext,
                    R.drawable.shape_home_calendar_cancel
                )
            )
            calendarBtnCancel.setTextColor(
                SkinnableResourceManager.getColor(
                    rootContext,
                    arch.cayenne.lib.common.R.color.title_bar
                )
            )
            calendarBtnOk.setBackgroundResource(
                SkinnableResourceManager.getTargetResourceId(
                    rootContext,
                    R.drawable.shape_home_calendar_ok
                )
            )
        }
    }

    /**
     * Builder 類別用於構建 HomeCalendarPopupWindow
     */
    open class Builder<VB : ViewBinding> {
        //Builder 的建構函數增加 LifecycleOwner
        private val context: Context
        private val lifecycleOwner: LifecycleOwner
        private val bindingInflater: (LayoutInflater) -> VB

        /**
         * 主要建構函數
         */
        constructor(
            context: Context,
            lifecycleOwner: LifecycleOwner,
            bindingInflater: (LayoutInflater) -> VB
        ) {
            this.context = context
            this.lifecycleOwner = lifecycleOwner
            this.bindingInflater = bindingInflater
        }

        /**
         * 為 Fragment 設計的便利建構函數
         * 只需要傳入 Fragment 實例，即可自動取得 Context 和 ViewLifecycleOwner
         */
        constructor(fragment: Fragment, bindingInflater: (LayoutInflater) -> VB) : this(
            fragment.requireContext(),
            fragment.viewLifecycleOwner, // 使用 viewLifecycleOwner 是關鍵！
            bindingInflater
        )

        internal var width: Int = WindowManager.LayoutParams.MATCH_PARENT
        internal var height: Int = WindowManager.LayoutParams.WRAP_CONTENT
        internal var isFocusable: Boolean = true
        internal var isOutsideTouchable: Boolean = true

        @StyleRes
        internal var animationStyle: Int? = null
        internal var onDismissListener: PopupWindow.OnDismissListener? = null
        internal var elevation: Float? = null
        internal var onDateSelectedListener: ((String) -> Unit)? = null
        internal var onCalendarDismissListener: (()-> Unit)? = null
        internal var onResetDateListener: (()-> Unit)? = null

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
        open fun build(): HomeCalendarPopupWindow<VB> {
            return HomeCalendarPopupWindow(context, lifecycleOwner, bindingInflater, this)
        }
    }
}
