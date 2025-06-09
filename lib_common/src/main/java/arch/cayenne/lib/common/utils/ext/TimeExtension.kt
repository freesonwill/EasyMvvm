package arch.cayenne.lib.common.utils.ext

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun Int.toMinuteSecondFormat(): String {
    val minutes = this / 60
    val seconds = this % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

fun Long.toLocalDateTimeString(): String {
    val curTime = Calendar.getInstance()
    val time = Calendar.getInstance().apply { timeInMillis = this@toLocalDateTimeString }
    val isSameDay = time.get(Calendar.YEAR) == curTime.get(Calendar.YEAR) &&
            time.get(Calendar.DAY_OF_YEAR) == curTime.get(Calendar.DAY_OF_YEAR)
    val isSameYear = time.get(Calendar.YEAR) == curTime.get(Calendar.YEAR)
    val sdf : SimpleDateFormat = if (!isSameYear) {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    } else if (!isSameDay) {
        SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault())
    } else {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    }
    return sdf.format(time.time)
}
