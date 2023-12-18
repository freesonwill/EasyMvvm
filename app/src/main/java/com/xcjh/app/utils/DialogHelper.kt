package com.xcjh.app.utils

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.utils.setup
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.github.gzuliyujiang.wheelpicker.widget.DateWheelLayout
import com.github.gzuliyujiang.wheelpicker.widget.LinkageWheelLayout
import com.github.gzuliyujiang.wheelpicker.widget.OptionWheelLayout
import com.github.gzuliyujiang.wheelview.annotation.CurtainCorner
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.kongzue.dialogx.dialogs.BottomDialog
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.xcjh.app.R
import com.xcjh.app.bean.AnchorListBean
import com.xcjh.app.view.MyDateWheelLayout
import com.xcjh.base_lib.App
import com.xcjh.base_lib.utils.TimeUtil


/**
 * 日历选择
 */
fun selectTime(context: Context, timeOld: String, block: (start: Calendar, end: Calendar) -> Unit) {
    //对于未实例化的布局：
    //DialogX.globalStyle = MaterialYouStyle.style()


    BottomDialog.build()
        .setCustomView(object : OnBindView<BottomDialog?>(R.layout.dialog_select_calendar) {
            override fun onBind(dialog: BottomDialog?, v: View) {
                if (dialog!!.dialogImpl.imgTab != null) {
                    dialog!!.dialogImpl.imgTab.setBackgroundResource(R.drawable.dilogx_white)
                }
                val tvMonth = v.findViewById<TextView>(R.id.tvMonth)

                val ivPre = v.findViewById<ImageView>(R.id.ivPre)
                val ivNext = v.findViewById<ImageView>(R.id.ivNext)
                var n = 0
                val mCalendarView = v.findViewById<CalendarView>(R.id.calendarView)
                var calendarStart: Calendar? = null
                var calendarEnd: Calendar? = null
                val year: Int = mCalendarView.curYear
                val month: Int = mCalendarView.curMonth
                val day: Int = mCalendarView.curDay

                val map: MutableMap<String, Calendar?> = HashMap()
                if (timeOld.isNotEmpty()) {
                    var year1: Int = timeOld.substring(0, 4).toInt()
                    var month1: Int = timeOld.substring(5, 7).toInt()
                    var day1: Int = timeOld.substring(8, 10).toInt()
                    map[getSchemeCalendar(year1, month1, day1, -0xffffff, "").toString()] =
                        getSchemeCalendar(year1, month1, day1, -0xffffff, "")
                }

                map[getSchemeCalendar(
                    year,
                    month,
                    day,
                    Color.parseColor("#F7DA73"),
                    ""
                ).toString()] =
                    getSchemeCalendar(year, month, day, Color.parseColor("#F7DA73"), "")
                //此方法在巨大的数据量上不影响遍历性能，推荐使用
                mCalendarView.setSchemeDate(map)


                mCalendarView.setOnCalendarRangeSelectListener(object :
                    CalendarView.OnCalendarRangeSelectListener {
                    override fun onCalendarSelectOutOfRange(calendar: Calendar?) {

                    }

                    override fun onSelectOutOfRange(
                        calendar: Calendar?,
                        isOutOfMinRange: Boolean,
                    ) {
                        // Log.e("====", "onSelectOutOfRange: =====" + calendar?.timeInMillis)
                    }

                    override fun onCalendarRangeSelect(calendar: Calendar, isEnd: Boolean) {
                        //  Log.e("====", "onCalendarRangeSelect: =====" +calendar.timeInMillis)
                        block.invoke(calendar!!, calendar!!)
//                        if (isEnd) {
//                            n++
//                        } else {
//                            if (calendarStart != null) {
//                                Log.e(
//                                    "====",
//                                    "onCalendarRangeSelect: =====" + calendar.differ(calendarStart)
//                                )
//                                if (calendar.differ(calendarStart) == 0) {
//                                    n++
//                                } else {
//                                    n = 0
//                                }
//                            } else {
//                                n = 0
//                            }
//                        }
//
//                        if (n == 0) {
//                            calendarStart = calendar
//                        } else if (n == 1) {
//                            calendarEnd = calendar
//                            block.invoke(calendarStart!!, calendarEnd!!)
                        mCalendarView.postDelayed(
                            {
                                dialog?.dismiss()
                            }, 500
                        )
//                        }

                        // Log.e("====", "onCalendarRangeSelect: =====" +calendar.toString())
                    }
                })
                //  mCalendarHeight = dipToPx(this, 46);


                mCalendarView.setRange(
                    2023,
                    1,
                    1,
                    2026,
                    12,
                    31,
                )
                mCalendarView.post(Runnable {
                    mCalendarView.scrollToCurrent()
                    tvMonth.text =
                        " ${mCalendarView.curYear} ${"年"} ${mCalendarView.curMonth} ${"月"}"
                })

                mCalendarView.setOnMonthChangeListener { year, month ->
                    tvMonth.text = " $year ${"年"} $month ${"月"}"

                }
                ivPre.setOnClickListener {
                    mCalendarView.scrollToPre(true)
                }
                ivNext.setOnClickListener {
                    mCalendarView.scrollToNext(true)
                }
            }
        }).setBackgroundColor(Color.parseColor("#2b2156"))

        .setMaskColor(//背景遮罩
            ContextCompat.getColor(context, com.xcjh.base_lib.R.color.blacks_tr)
        )

        .show()
}

/**
 * 日期选择
 */
fun selectDate(context: Context, timeOld: String, block: (time: String) -> Unit) {
    //对于未实例化的布局：
    //DialogX.globalStyle = MaterialYouStyle.style()


    BottomDialog.build()
        .setCustomView(object : OnBindView<BottomDialog?>(R.layout.dialog_select_date) {
            override fun onBind(dialog: BottomDialog?, v: View) {
                if (dialog!!.dialogImpl.imgTab != null) {
                    dialog!!.dialogImpl.imgTab.setBackgroundResource(R.drawable.dilogx_eff1f5)
                }
                val dateTimePickerView: MyDateWheelLayout =
                    v.findViewById<MyDateWheelLayout>(R.id.datewheel)
                val ivclose = v.findViewById<ImageView>(R.id.ivNext)
                val tvcz = v.findViewById<TextView>(R.id.tvcz)
                val tvsure = v.findViewById<TextView>(R.id.tvsure)
                val calendar = java.util.Calendar.getInstance()
                val currentYear = calendar[java.util.Calendar.YEAR]
                val currentMonth = calendar[java.util.Calendar.MONTH] + 1
                val currentDay = calendar[java.util.Calendar.DAY_OF_MONTH]
                val startValue = DateEntity.target(currentYear - 1, 1, 1)
                val endValue = DateEntity.target(currentYear+1, 12, 31)
                val defaultValue = DateEntity.target(currentYear, currentMonth, currentDay)

                dateTimePickerView.yearWheelView.curtainCorner = CurtainCorner.LEFT
                dateTimePickerView.monthWheelView.curtainCorner = CurtainCorner.NONE
                dateTimePickerView.dayWheelView.curtainCorner = CurtainCorner.RIGHT


                dateTimePickerView.setRange(startValue, endValue, defaultValue)
                dateTimePickerView.setDateFormatter(com.xcjh.app.view.BirthdayFormatter())
                dateTimePickerView.setResetWhenLinkage(false)
                ivclose.setOnClickListener { dialog?.dismiss() }
                tvcz.setOnClickListener {
                    dateTimePickerView.setDefaultValue(defaultValue)

                }
                tvsure.setOnClickListener {
                    block.invoke(
                        dateTimePickerView.selectedYear.toString() + "-" +
                                TimeUtil.checkTimeSingle(dateTimePickerView.selectedMonth) + "-" + TimeUtil.checkTimeSingle(
                            dateTimePickerView.selectedDay
                        )
                    )
                    dialog?.dismiss()
                }


            }
        }).setBackgroundColor(Color.parseColor("#FFFFFF"))

        .setMaskColor(//背景遮罩
            ContextCompat.getColor(context, com.xcjh.base_lib.R.color.blacks_tr)
        )

        .show().isAllowInterceptTouch = false
}

/**
 * 日历选择
 */
fun selectCountry(context: Context, list: List<String>, block: (bean: String) -> Unit) {
    //对于未实例化的布局：
    //DialogX.globalStyle = MaterialYouStyle.style()


    BottomDialog.build()
        .setCustomView(object : OnBindView<BottomDialog?>(R.layout.dialog_select_country) {
            override fun onBind(dialog: BottomDialog?, v: View) {
                if (dialog!!.dialogImpl.imgTab != null) {
                    dialog!!.dialogImpl.imgTab.setBackgroundResource(R.drawable.dilogx_white)
                }
                val tvcancle = v.findViewById<TextView>(R.id.tvcancle)

                val tvsure = v.findViewById<TextView>(R.id.tvsure)
                val wheel_linkage = v.findViewById<OptionWheelLayout>(R.id.wheel_linkage)
                wheel_linkage.setData(list)

                tvcancle.setOnClickListener {
                    dialog.dismiss()

                }
                tvsure.setOnClickListener {
                    block.invoke(wheel_linkage.wheelView.getCurrentItem())
                    dialog.dismiss()

                }


            }
        }).setBackgroundColor(Color.parseColor("#ffffff"))

        .setMaskColor(//背景遮罩
            ContextCompat.getColor(context, com.xcjh.base_lib.R.color.blacks_tr)
        )

        .show().isAllowInterceptTouch = false
}

private fun getSchemeCalendar(
    year: Int,
    month: Int,
    day: Int,
    color: Int,
    text: String,
): Calendar? {
    val calendar = Calendar()
    calendar.year = year
    calendar.month = month
    calendar.day = day
    calendar.schemeColor = color //如果单独标记颜色、则会使用这个颜色
    calendar.scheme = text
    return calendar
}

/***
 * 清楚消息弹窗
 */
fun clearMsg(context: Context, block: (isSure: Boolean) -> Unit) {
    CustomDialog.build()
        .setCustomView(object : OnBindView<CustomDialog?>(R.layout.layout_dialogx_clearmsg) {
            override fun onBind(dialog: CustomDialog?, v: View) {
                val tvcancle = v.findViewById<TextView>(R.id.tvcancle)
                val tvsure = v.findViewById<TextView>(R.id.tvsure)
                tvcancle.setOnClickListener {
                    block.invoke(false)
                    dialog?.dismiss()
                }
                tvsure.setOnClickListener {
                    block.invoke(true)
                    dialog?.dismiss()
                }
            }
        }).setAlign(CustomDialog.ALIGN.CENTER).setMaskColor(//背景遮罩
            ContextCompat.getColor(context, com.xcjh.base_lib.R.color.blacks_tr)
        ).show()
}
/***
 * 删除消息弹窗
 */
fun delMsgDilog(context: Context, block: (isSure: Boolean) -> Unit) {
    CustomDialog.build()
        .setCustomView(object : OnBindView<CustomDialog?>(R.layout.layout_dialogx_delmsg) {
            override fun onBind(dialog: CustomDialog?, v: View) {
                val tvcancle = v.findViewById<TextView>(R.id.tvcancle)
                val tvsure = v.findViewById<TextView>(R.id.tvsure)
                tvcancle.setOnClickListener {
                    block.invoke(false)
                    dialog?.dismiss()
                }
                tvsure.setOnClickListener {
                    block.invoke(true)
                    dialog?.dismiss()
                }
            }
        }).setAlign(CustomDialog.ALIGN.CENTER).setMaskColor(//背景遮罩
            ContextCompat.getColor(context, com.xcjh.base_lib.R.color.blacks_tr)
        ).show()
}
/**
 * 信号源选择
 */
fun showSignalDialog(
    anchorList: List<AnchorListBean>?,
    action: (AnchorListBean, Int) -> Unit,
) {
    var pos = 0
    if (anchorList != null) {
        for ((i, item) in anchorList.withIndex()) {
            if (item.isSelect) {
                pos = i
                break
            }
        }
    }

    //模式数据
    BottomDialog.build()
        .setCustomView(object : OnBindView<BottomDialog>(R.layout.dialog_signal_list) {
            override fun onBind(dialog: BottomDialog, v: View) {
                if (dialog.dialogImpl.imgTab != null) {
                    dialog.dialogImpl.imgTab.setBackgroundResource(R.drawable.dilogx_eff1f5)
                }
                val rcvSignal = v.findViewById<RecyclerView>(R.id.rcvSignal)
                val tvCancel = v.findViewById<TextView>(R.id.tvCancel)
                rcvSignal.setup {
                    addType<AnchorListBean> { R.layout.item_signal }

                    onBind {
                        val model = getModel<AnchorListBean>()
                        findView<TextView>(R.id.tvContent).apply {
                            if (model.isSelect) {
                                this.setTextColor(context.getColor(R.color.c_34a853))
                                paint?.isFakeBoldText = true
                            } else {
                                this.setTextColor(context.getColor(R.color.c_37373d))
                                paint?.isFakeBoldText = false
                            }
                            this.text =
                                if (model.pureFlow) model.nickName else model.nickName.ifEmpty {
                                    context.getString(R.string.anchor) + (modelPosition + 1)
                                }
                        }
                    }
                    onClick(R.id.lltItem) {
                        val model = getModel<AnchorListBean>()
                        action.invoke(model, modelPosition)
                        dialog.dismiss()
                    }
                }.models = anchorList

                tvCancel.setOnClickListener {
                    dialog.dismiss()
                }
                rcvSignal.scrollToPosition(pos)
            }
        })
        // .setAlign(CustomDialog.ALIGN.BOTTOM)
        .setMaskColor(ContextCompat.getColor(App.app, com.xcjh.base_lib.R.color.blacks_tr))
        .setCancelable(true)
        .show()
}