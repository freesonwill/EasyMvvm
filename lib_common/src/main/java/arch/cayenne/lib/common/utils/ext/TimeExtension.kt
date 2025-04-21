package arch.cayenne.lib.common.utils.ext

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
    val sdf = SimpleDateFormat("MMdd HHmmss", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(date)
}
