package com.xcjh.base_lib.utils

import android.text.TextUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


/**
 * 时间格式转换工具类
 *
 */
object TimeUtil {
    private const val seconds_of_1minute = 60
    private const val seconds_of_1hour = 60 * 60
    private const val seconds_of_2hour = 2 * 60 * 60
    private const val seconds_of_3hour = 3 * 60 * 60

    private const val YMDHMS_FORMAT = "yyyy-MM-dd HH:mm:ss"
    public const val YMDHMS_FORMAT_YEAR = "yyyy-MM-dd"
    private const val search_DateFormat = "MM/dd/yyyy HH:mm:ss"
    private const val TIME_ZERO = "00:00"
    private const val TIME_MAX = "23:59:59"

    fun stringConvertDate(time: String?): Date? {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
        var data: Date? = null
        try {
            data = sdf.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return data
    }

    /***
     * 判断时间差是否大于15分钟  主要用于私聊页面是否显示时间
     */
    fun getTimeCha(start: Long?, endTime: Long?): Boolean? {

        try {

            var xx:Long=0
            if (endTime!! <=0){
                LogUtils.d("true时间差===")
                return true
            }
            val diff = endTime!! - start!! //
            val days = diff / (1000 * 60 * 60 * 24)
            val hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
            val minutes =
                diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) / (1000 * 60)
            val second = diff / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - minutes * 60

            LogUtils.d("时间差===$days--$hours--$minutes")
            if (days > 0||hours > 0||minutes > 300000) {
                LogUtils.d("true时间差===")
                return true
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            LogUtils.d("false时间差===")
            return false
        }
        LogUtils.d("false时间差===")
        return false
    }

    /***
     * 当前日期加几天
     */
    fun addDayEgls(time: String, day: Int): String? {
        var time = time
        if (time == "0") {
            time = gettimenowYear().toString()
        }
        if (time == gettimenowYear() && day == -1) {
            return gettimenowYear()
        }
        var timee = ""
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        try {
            val dt = sdf.parse(time)
            val rightNow = Calendar.getInstance()
            rightNow.time = dt
            rightNow.add(Calendar.DAY_OF_YEAR, day) // 日期加几天
            val dt1 = rightNow.time
            timee = sdf.format(dt1)
            //timee=timee.substring(0,10);
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return timee
    }
    fun getDateStr(day: String?, Num: Int): String? {
        val df = SimpleDateFormat("yyyy-MM-dd")
        var nowDate: Date? = null
        try {
            nowDate = df.parse(day)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        //如果需要向后计算日期 -改为+
        val newDate2 = Date(nowDate!!.time - Num.toLong() * 24 * 60 * 60 * 1000)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        return simpleDateFormat.format(newDate2)
    }

    fun checkTimeSingle(char: Int): String {
        var time = "01"
        if (char < 10) {
            time = "0$char"
        } else {
            time = char.toString()
        }
        return time
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @return
     */
    fun timeStamp2Date(seconds: Long, format: String?): String? {
        var format = format
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss"
        }
        val sdf = SimpleDateFormat(format)
        return sdf.format(Date(seconds))
    }
    fun gettimenowYear(): String? {
        val date = Date()
        val time = date.toLocaleString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(date)
    }
    fun gettimenowSences(): String? {
        val date = Date()
        val time = date.toLocaleString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return dateFormat.format(date)
    }

    fun getStringToDate(dateString: String?, pattern: String?): Long {
        val dateFormat = SimpleDateFormat(pattern)
        var date = Date()
        try {
            date = dateFormat.parse(dateString)
        } catch (e: ParseException) {

// TODO Auto-generated catch block
            e.printStackTrace()
        }
        return date.time
    }

    fun longToString(longNum: Long, dateFormat: String?): String? {
        var dateFormat = dateFormat
        if (TextUtils.isEmpty(dateFormat)) {
            dateFormat = SimpleDateFormat(YMDHMS_FORMAT).toString()
        }
        val format = SimpleDateFormat(dateFormat)
        val date = Date(longNum)
        return format.format(date)
    }


    /**
     * 当地时间 ---> UTC时间
     * @return
     */
    fun local2UTC(time: String, locale: Locale? = Locale.US): String? {

        val sdf = SimpleDateFormat(YMDHMS_FORMAT, locale)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(stringConvertDate(time))
        // return sdf.format(Date())
    }

    //**UTC时间格式样例：2018-09-28T16:00:00.000Z
    //本地时间格式样式：2018-09-28 24:00:00
    /**
     * 当地时间 ---> UTC时间
     * @return
     */
    fun formatLocal2UTC(date: String): String? {
        val utcSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val sdf = SimpleDateFormat(YMDHMS_FORMAT)
        val cal = Calendar.getInstance()
        // 取得时间偏移量：
        val zoneOffset = cal.get(Calendar.ZONE_OFFSET)
        // 取得夏令时差：
        val dstOffset = cal.get(Calendar.DST_OFFSET)
        try {
            var dateValue = sdf.parse(date)
            var longDate = dateValue.time
            longDate = longDate - zoneOffset - dstOffset
            val UTCDate = Date(longDate)
            return utcSdf.format(UTCDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    /**00
     * UTC时间 ---> 当地时间
     * @param utcDate   UTC时间
     * @return
     */
    fun parseUTC2Local(utcDate: String): String? {
        var utcDate = utcDate
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z")
        val sdf = SimpleDateFormat(YMDHMS_FORMAT)
        utcDate = utcDate.replace("Z", " UTC")
        //注意UTC前有空格
        try {
            val date = utcFormat.parse(utcDate)
            return sdf.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * UTC时间 ---> 当地时间
     * @param utcTime   UTC时间
     * @return
     */
    fun utc2Local(utcTime: String?, locale: Locale? = Locale.US): String? {
        try {
            utcTime?.apply {
                val utcFormat = SimpleDateFormat(YMDHMS_FORMAT, locale)
                utcFormat.timeZone = TimeZone.getTimeZone("UTC")
                var gpsUTCDate: Date? = null
                try {
                    gpsUTCDate = utcFormat.parse(this)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val localFormatter = SimpleDateFormat(YMDHMS_FORMAT, locale)
                localFormatter.timeZone = TimeZone.getDefault()
                return localFormatter.format(gpsUTCDate!!.time)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

}

