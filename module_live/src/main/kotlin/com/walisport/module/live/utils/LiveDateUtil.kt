package com.walisport.module.live.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

object LiveDateUtil {

    @SuppressLint("SimpleDateFormat")
    fun getMDHm(time: Long): String {
        var date: String = ""
        try {
            val sdf = SimpleDateFormat("MM月dd日 HH:mm")
            val dat = Date(time)
            date = sdf.format(dat)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return date
    }

}