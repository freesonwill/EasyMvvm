package com.xcjh.app.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.kongzue.dialogx.dialogs.BottomDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.xcjh.app.R


/**
 * 日历选择
 */
fun selectTime(context: Context, block: (start: Calendar, end: Calendar) -> Unit) {
    //对于未实例化的布局：
    //DialogX.globalStyle = MaterialYouStyle.style()
    BottomDialog.build()
        .setCustomView(object : OnBindView<BottomDialog?>(R.layout.dialog_select_calendar) {
            override fun onBind(dialog: BottomDialog?, v: View) {
                val tvMonth = v.findViewById<TextView>(R.id.tvMonth)
                val ivPre = v.findViewById<ImageView>(R.id.ivPre)
                val ivNext = v.findViewById<ImageView>(R.id.ivNext)
                var n = 0
                val mCalendarView = v.findViewById<CalendarView>(R.id.calendarView)
                var calendarStart: Calendar? = null
                var calendarEnd: Calendar? = null
                mCalendarView.setOnCalendarRangeSelectListener(object :
                    CalendarView.OnCalendarRangeSelectListener {
                    override fun onCalendarSelectOutOfRange(calendar: Calendar?) {
                        Log.e("====", "onCalendarSelectOutOfRange: =====" + calendar.toString())
                    }

                    override fun onSelectOutOfRange(
                        calendar: Calendar?,
                        isOutOfMinRange: Boolean
                    ) {
                        Log.e("====", "onSelectOutOfRange: =====" + calendar?.timeInMillis)
                    }

                    override fun onCalendarRangeSelect(calendar: Calendar, isEnd: Boolean) {
                        //  Log.e("====", "onCalendarRangeSelect: =====" +calendar.timeInMillis)
                        if (isEnd) {
                            n++
                        } else {
                            if (calendarStart != null) {
                                Log.e(
                                    "====",
                                    "onCalendarRangeSelect: =====" + calendar.differ(calendarStart)
                                )
                                if (calendar.differ(calendarStart) == 0) {
                                    n++
                                } else {
                                    n = 0
                                }
                            } else {
                                n = 0
                            }
                        }

                        if (n == 0) {
                            calendarStart = calendar
                        } else if (n == 1) {
                            calendarEnd = calendar
                            block.invoke(calendarStart!!, calendarEnd!!)
                            mCalendarView.postDelayed(
                                {
                                    dialog?.dismiss()
                                }, 500
                            )
                        }

                        // Log.e("====", "onCalendarRangeSelect: =====" +calendar.toString())
                    }
                })
                //  mCalendarHeight = dipToPx(this, 46);
                mCalendarView.setRange(
                    mCalendarView.curYear,
                    mCalendarView.curMonth,
                    mCalendarView.curDay,
                    2030,
                    1,
                    1,
                )
                mCalendarView.post(Runnable {
                    mCalendarView.scrollToCurrent()
                    tvMonth.text = "${mCalendarView.curMonth} ${mCalendarView.curYear}"
                })

                mCalendarView.setOnMonthChangeListener { year, month ->
                    tvMonth.text = "$month $year"

                }
                tvMonth.setOnClickListener {
                    dialog?.dismiss()
                }
                ivPre.setOnClickListener {
                    mCalendarView.scrollToPre(true)
                }
                ivNext.setOnClickListener {
                    mCalendarView.scrollToNext(true)
                }
            }
        })
        .setMaskColor(
            ContextCompat.getColor(context, com.xcjh.base_lib.R.color.blacks_tr)
        )
        //.setCancelable(true)
        .show()
}