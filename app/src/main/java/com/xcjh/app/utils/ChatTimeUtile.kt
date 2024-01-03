package com.xcjh.app.utils

import android.content.Context
import android.content.res.Resources
import com.xcjh.app.R
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.TimeConstant
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object ChatTimeUtile {
    fun formatTimestamp(contenx: Context, timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val diff = currentTime - timestamp

        val seconds = diff / 1000
        if (seconds < 60) {
            return contenx.resources.getString(R.string.txt_time_gg)
        }

        val minutes = seconds / 60
        if (minutes < 60) {
            return "$minutes" + contenx.resources.getString(R.string.txttime_minits)
        }

        val hours = minutes / 60
        if (isSameDay(timestamp, currentTime)) {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            return format.format(Date(timestamp))
        }
        if (hours < 24) {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            return contenx.resources.getString(R.string.txttime_yesterday) + format.format(
                Date(
                    timestamp
                )
            )
        }


        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date(timestamp))
    }

    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val calendar1 = Calendar.getInstance()
        calendar1.timeInMillis = timestamp1

        val calendar2 = Calendar.getInstance()
        calendar2.timeInMillis = timestamp2

        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)
    }

}