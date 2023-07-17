package com.xcjh.base_lib.utils

import android.text.TextUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


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

    fun longToString(longNum: Long, dateFormat: String?): String? {
        var dateFormat = dateFormat
        if (TextUtils.isEmpty(dateFormat)) {
            dateFormat = YMDHMS_FORMAT
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
    /**
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

