package com.xcjh.app.view

import android.content.Context
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.github.gzuliyujiang.wheelview.annotation.CurtainCorner
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.xcjh.app.R
import com.xcjh.app.listener.OnChooseDateListener
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.TimeUtil

/**
 * Description: 自定义带有ViewPager的Bottom弹窗
 * Create by dance, at 2019/5/5
 */
class XPBottomPopu(context: Context) : BottomPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.dialog_select_date
    }

    var dateTimePickerView: MyDateWheelLayout? = null
    var ivclose: ImageView? = null
    var tvcz: TextView? = null
    var tvsure: TextView? = null
    var type = ""
    var maxTime = ""
    var minTime = ""
    var calendarTime = ""
    var listre: OnChooseDateListener? = null
    val calendar = java.util.Calendar.getInstance()
    val currentYear = calendar[java.util.Calendar.YEAR]
    val currentMonth = calendar[java.util.Calendar.MONTH] + 1
    val currentDay = calendar[java.util.Calendar.DAY_OF_MONTH]
    val currentTime = "$currentYear-$currentMonth-$currentDay"


    @RequiresApi(Build.VERSION_CODES.N)
    fun setOnLister(mcalendarTime: String, type: String, mlistre: OnChooseDateListener) {
        listre = mlistre
        calendarTime=mcalendarTime
        if (type != "3") {
            minTime = currentTime
            maxTime = TimeUtil.getMyCurrentDay(currentTime, 30)!!

        } else {
            maxTime = currentTime
            minTime = TimeUtil.getMyCurrentDay(currentTime, -30)!!

        }

        println("你得到的日期是maxTime=$maxTime  minTime=$minTime")
    }

    override fun onCreate() {
        super.onCreate()

        dateTimePickerView = findViewById<MyDateWheelLayout>(R.id.datewheel)
        ivclose = findViewById<ImageView>(R.id.ivNext)
        tvcz = findViewById<TextView>(R.id.tvcz)
        tvsure = findViewById<TextView>(R.id.tvsure)
        val activity = context as FragmentActivity

        val startValue = DateEntity.target(currentYear - 5, 1, 1)
        val endValue = DateEntity.target(currentYear + 5, 12, 31)
        var defaultValue = DateEntity.target(currentYear, currentMonth, currentDay)
        if (calendarTime!=null&&calendarTime.isNotEmpty()){
            defaultValue=DateEntity.target(calendarTime.substring(0,4).toInt(),
                calendarTime.substring(5,7).toInt(), calendarTime.substring(8,calendarTime.length).toInt())
        }
        dateTimePickerView?.yearWheelView?.curtainCorner = CurtainCorner.LEFT
        dateTimePickerView?.monthWheelView?.curtainCorner = CurtainCorner.NONE
        dateTimePickerView?.dayWheelView?.curtainCorner = CurtainCorner.RIGHT


        dateTimePickerView?.setRange(startValue, endValue, defaultValue)
        dateTimePickerView?.setDateFormatter(com.xcjh.app.view.BirthdayFormatter())
        dateTimePickerView?.setResetWhenLinkage(false)
        dateTimePickerView?.setOnDateSelectedListener { year, month, day ->
            LogUtils.d("当前选择的时间是==$year-$month-$day")
            var chooseTime="$year-$month-$day"
            if (type!="3") {//赛程
                if (TimeUtil.compareDates(chooseTime, maxTime) > 0) {//超过了最大日期,需要选择最大值
                    val default = DateEntity.target(maxTime.substring(0,4).toInt(),
                        maxTime.substring(5,7).toInt(), maxTime.substring(8).toInt())
                    dateTimePickerView?.setDefaultValue(default)
                }
                if (TimeUtil.compareDates(chooseTime, minTime) < 0) {//小于了最小日期,需要选择当日
                    val default = DateEntity.target(minTime.substring(0,4).toInt(),
                        minTime.substring(5,7).toInt(), minTime.substring(8).toInt())
                    dateTimePickerView?.setDefaultValue(default)
                }
            }else{//赛果
                if (TimeUtil.compareDates(chooseTime, maxTime) > 0) {//超过了最大日期,需要选择最大值
                    val default = DateEntity.target(maxTime.substring(0,4).toInt(),
                        maxTime.substring(5,7).toInt(), maxTime.substring(8).toInt())
                    dateTimePickerView?.setDefaultValue(default)
                }
                if (TimeUtil.compareDates(chooseTime, minTime) < 0) {//小于了最小日期,需要选择当日
                    val default = DateEntity.target(minTime.substring(0,4).toInt(),
                        minTime.substring(5,7).toInt(), minTime.substring(8).toInt())
                    dateTimePickerView?.setDefaultValue(default)
                }
            }



        }
        ivclose?.setOnClickListener { listre!!.onDismiss() }
        tvcz?.setOnClickListener {
            dateTimePickerView?.setDefaultValue(defaultValue)

        }

        tvsure?.setOnClickListener {
            listre?.onSure(
                dateTimePickerView?.selectedYear.toString() + "-" +
                        TimeUtil.checkTimeSingle(dateTimePickerView?.selectedMonth!!) + "-" + TimeUtil.checkTimeSingle(
                    dateTimePickerView?.selectedDay!!
                )
            )

        }

    }


    override fun onShow() {
        super.onShow()
    }

    override fun onDismiss() {
        super.onDismiss()
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context) * .85f).toInt()
    }
}