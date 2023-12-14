package com.xcjh.app.utils

import android.content.Context
import android.content.res.Resources
import com.xcjh.app.R
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.TimeConstant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ChatTimeUtile {
    fun formatTimestamp(contenx:Context, timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val diff = currentTime - timestamp

        val seconds = diff / 1000
        if (seconds < 60) {
            return contenx.resources.getString(R.string.txt_time_gg)
        }

        val minutes = seconds / 60
        if (minutes < 60) {
            return "$minutes"+ contenx.resources.getString(R.string.txttime_minits)
        }

        val hours = minutes / 60
        if (hours < 24) {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            return format.format(Date(timestamp))
        }

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date(timestamp))
    }

}