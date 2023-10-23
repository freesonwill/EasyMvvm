package com.xcjh.app.utils

import android.content.Context
import android.content.res.Resources
import com.xcjh.app.R
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.TimeConstant
import java.util.Locale

object ChatTimeUtile {
    fun getRecentTimeSpanByNow(contenx:Context, millis: Long): String? {
        val now = System.currentTimeMillis()
        val span = now - millis
        LogUtils.d(now.toString()+"--"+millis+"间隔时间=$span")
        return if (span < 1000) {
            contenx.resources.getString(R.string.txt_time_gg)
        } else if (span < TimeConstant.MIN) {
            java.lang.String.format(Locale.getDefault(),  contenx.resources.getString(R.string.txttime_sencend), span / TimeConstant.SEC)
        } else if (span < TimeConstant.HOUR) {
            java.lang.String.format(Locale.getDefault(), contenx.resources.getString(R.string.txttime_minits), span / TimeConstant.MIN)
        } else if (span < TimeConstant.DAY) {
            java.lang.String.format(Locale.getDefault(), contenx.resources.getString(R.string.txttime_hours), span / TimeConstant.HOUR)
        } else if (span < TimeConstant.MONTH) {
            java.lang.String.format(Locale.getDefault(), contenx.resources.getString(R.string.txttime_days), span / TimeConstant.DAY)
        } else if (span < TimeConstant.YEAR) {
            java.lang.String.format(Locale.getDefault(), contenx.resources.getString(R.string.txttime_mouth), span / TimeConstant.MONTH)
        } else if (span > TimeConstant.YEAR) {
            java.lang.String.format(Locale.getDefault(), contenx.resources.getString(R.string.txttime_years), span / TimeConstant.YEAR)
        } else {
            String.format("%tF", millis)
        }
    }
}