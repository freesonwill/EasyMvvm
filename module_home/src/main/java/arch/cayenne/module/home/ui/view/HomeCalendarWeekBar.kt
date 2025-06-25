package arch.cayenne.module.home.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.HomeCalendarWeekBarBinding
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.WeekBar

/**
 * @author: ricky.chang
 * @date: 2025/6/24 下午3:46
 * @description:
 */
class HomeCalendarWeekBar(context: Context?) : WeekBar(context) {
    private var mPreSelectedIndex = 0

    init {
        HomeCalendarWeekBarBinding.inflate(LayoutInflater.from(context), this)
    }

    override fun onDateSelected(calendar: Calendar, weekStart: Int, isClick: Boolean) {
        getChildAt(mPreSelectedIndex).isSelected = false
        val viewIndex = getViewIndexByCalendar(calendar, weekStart)
        getChildAt(viewIndex).isSelected = true
        mPreSelectedIndex = viewIndex
    }

    /**
     * 当周起始发生变化，使用自定义布局需要重写这个方法，避免出问题
     *
     * @param weekStart 周起始
     */
    override fun onWeekStartChange(weekStart: Int) {
        for (i in 0 until childCount) {
            (getChildAt(i) as TextView).text = getWeekString(i, weekStart)
        }
    }

    /**
     * 或者周文本，这个方法仅供父类使用
     * @param index index
     * @param weekStart weekStart
     * @return 或者周文本
     */
    private fun getWeekString(index: Int, weekStart: Int): String {
        val weeks = context.resources.getStringArray(R.array.chinese_week_string_array)

        if (weekStart == 1) {
            return weeks[index]
        }
        if (weekStart == 2) {
            return weeks[if (index == 6) 0 else index + 1]
        }
        return weeks[if (index == 0) 6 else index - 1]
    }
}
