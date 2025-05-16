package arch.cayenne.module.betslip.utisl

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object BetSlipDateUtil {

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