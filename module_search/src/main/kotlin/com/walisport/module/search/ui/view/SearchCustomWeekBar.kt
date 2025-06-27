package com.walisport.module.search.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.haibin.calendarview.WeekBar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SearchCustomWeekBar(context: Context?) : WeekBar(context) {
    private var locale: Locale = Locale.getDefault()

    init {
        LayoutInflater.from(context).inflate(com.haibin.calendarview.R.layout.cv_week_bar, this, true)
    }

    override fun onWeekStartChange(weekStart: Int) {
        for (i in 0 until childCount) {
            (getChildAt(i) as TextView).text = getWeekString(i, weekStart)
        }
    }

    private fun getWeekString(index: Int, weekStart: Int): String {
        return run {
            when (weekStart) {
                Calendar.SUNDAY -> (index % 7) + 1
                Calendar.MONDAY -> if (index == 6) Calendar.SUNDAY else Calendar.MONDAY + index
                else -> if (index == 0) Calendar.SATURDAY else weekStart + index - 1
            }
        }.run {
            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, this@run)
            }.time.let { date ->
                SimpleDateFormat("EEE", locale).format(date)
            }
        }
    }

    fun setLocale(locale: Locale) {
        this.locale = locale
    }
}
