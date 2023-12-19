package com.xcjh.app.view

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.github.gzuliyujiang.wheelview.annotation.CurtainCorner
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.xcjh.app.R
import com.xcjh.app.listener.OnChooseDateListener
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

    var listre: OnChooseDateListener? = null

    fun setOnLister(time:String, mlistre: OnChooseDateListener){
        listre= mlistre
    }
    override fun onCreate() {
        super.onCreate()

        dateTimePickerView = findViewById<MyDateWheelLayout>(R.id.datewheel)
        ivclose = findViewById<ImageView>(R.id.ivNext)
        tvcz = findViewById<TextView>(R.id.tvcz)
        tvsure = findViewById<TextView>(R.id.tvsure)
        val activity = context as FragmentActivity
        val calendar = java.util.Calendar.getInstance()
        val currentYear = calendar[java.util.Calendar.YEAR]
        val currentMonth = calendar[java.util.Calendar.MONTH] + 1
        val currentDay = calendar[java.util.Calendar.DAY_OF_MONTH]
        val startValue = DateEntity.target(currentYear - 1, 1, 1)
        val endValue = DateEntity.target(currentYear+1, 12, 31)
        val defaultValue = DateEntity.target(currentYear, currentMonth, currentDay)
        dateTimePickerView?.yearWheelView?.curtainCorner = CurtainCorner.LEFT
        dateTimePickerView?.monthWheelView?.curtainCorner = CurtainCorner.NONE
        dateTimePickerView?.dayWheelView?.curtainCorner = CurtainCorner.RIGHT


        dateTimePickerView?.setRange(startValue, endValue, defaultValue)
        dateTimePickerView?.setDateFormatter(com.xcjh.app.view.BirthdayFormatter())
        dateTimePickerView?.setResetWhenLinkage(false)
        ivclose?.setOnClickListener { listre!!.onDismiss() }
        tvcz?.setOnClickListener {
            dateTimePickerView?.setDefaultValue(defaultValue)

        }

        tvsure?.setOnClickListener {
            listre?.onSure( dateTimePickerView?.selectedYear.toString() + "-" +
                    TimeUtil.checkTimeSingle(dateTimePickerView?.selectedMonth!!) + "-" + TimeUtil.checkTimeSingle(
                dateTimePickerView?.selectedDay!!
            ))

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