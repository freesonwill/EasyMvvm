package arch.cayenne.module.home.extension

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Int.toMinuteSecondFormat(): String {
    val minutes = this / 60
    val seconds = this % 60
    return String.format(Locale.getDefault(), "%d'%02d'", minutes, seconds)
}

fun Long.toLocalDateTimeString(): String {
    val date = Date(this) // this = timestamp in milliseconds
    val sdf = SimpleDateFormat("MM月dd日 HH时mm分ss秒", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(date)
}
