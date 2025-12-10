package arch.cayenne.module.order.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import java.util.Calendar
import java.util.Locale

class OrderDateCustomViewModel: BaseViewModel() {

    private val _onStartTimeListener = MutableLiveData<String>()
    val onStartTimeListener: LiveData<String> get() = _onStartTimeListener

    private val _onEndTimeListener = MutableLiveData<String>()
    val onEndTimeListener: LiveData<String> get() = _onEndTimeListener

    private val _customTimeListener = MutableLiveData<Long?>()
    val customTimeListener: LiveData<Long?> get() = _customTimeListener

    var calendar: Calendar
        private set

    init {
        initDefaultTime()
        calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            _customTimeListener.value = timeInMillis
        }
    }

    private fun initDefaultTime() {
        val c = Calendar.getInstance()
        c.timeInMillis = System.currentTimeMillis()
        
        val year = c.get(Calendar.YEAR)
        val month = String.format(Locale.getDefault(), "%02d", c.get(Calendar.MONTH) + 1)
        val day = String.format(Locale.getDefault(), "%02d", c.get(Calendar.DAY_OF_MONTH))
        
        val currentDate = "$year-$month-$day"
        _onStartTimeListener.value = currentDate
        _onEndTimeListener.value = currentDate
    }

    /**
     * 設置日期為當天開始時間 (0:0:0)
     */
    private fun Calendar.setToDayStart() {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    /**
     * 設置日期為當天結束時間 (23:59:59)
     */
    private fun Calendar.setToDayEnd() {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }

    /**
     * 獲取昨天的時間區間
     * @return LongArray [開始時間戳(0:0:0), 結束時間戳(23:59:59)]
     */
    fun getYesterdayTimeRange(): LongArray {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        
        calendar.setToDayStart()
        val startTime = calendar.timeInMillis
        
        calendar.setToDayEnd()
        val endTime = calendar.timeInMillis
        
        return longArrayOf(startTime, endTime)
    }

    /**
     * 獲取上週的時間區間（週一到週日）
     * @return LongArray [週一 0:0:0, 週日 23:59:59]
     */
    fun getLastWeekTimeRange(): LongArray {
        val calendar = Calendar.getInstance()
        
        // 獲取當前是星期幾（1=週日, 2=週一, ..., 7=週六）
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        
        // 計算到上週一的天數差
        val daysToLastMonday = if (dayOfWeek == Calendar.SUNDAY) {
            8 // 如果今天是週日，上週一是8天前
        } else {
            dayOfWeek - Calendar.MONDAY + 7 // 否則計算到上週一
        }
        
        // 設置為上週一 0:0:0
        calendar.add(Calendar.DAY_OF_MONTH, -daysToLastMonday)
        calendar.setToDayStart()
        val startTime = calendar.timeInMillis
        
        // 設置為上週日 23:59:59
        calendar.add(Calendar.DAY_OF_MONTH, 6)
        calendar.setToDayEnd()
        val endTime = calendar.timeInMillis
        
        return longArrayOf(startTime, endTime)
    }

    /**
     * 獲取上月的時間區間
     * @return LongArray [上月1號 0:0:0, 上月最後一天 23:59:59]
     */
    fun getLastMonthTimeRange(): LongArray {
        val calendar = Calendar.getInstance()
        
        // 設置為上月
        calendar.add(Calendar.MONTH, -1)
        
        // 設置為上月1號 0:0:0
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.setToDayStart()
        val startTime = calendar.timeInMillis
        
        // 設置為上月最後一天 23:59:59
        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, lastDay)
        calendar.setToDayEnd()
        val endTime = calendar.timeInMillis
        
        return longArrayOf(startTime, endTime)
    }

    /**
     * 獲取自定義時間區間
     * @return LongArray [開始日期 0:0:0, 結束日期 23:59:59]
     */
    fun getCustomTimeRange(): LongArray {
        val startDateStr = _onStartTimeListener.value ?: return longArrayOf(0, 0)
        val endDateStr = _onEndTimeListener.value ?: return longArrayOf(0, 0)
        
        val startCalendar = Calendar.getInstance()
        val endCalendar = Calendar.getInstance()
        
        // 解析開始日期
        val startParts = startDateStr.split("-")
        startCalendar.set(Calendar.YEAR, startParts[0].toInt())
        startCalendar.set(Calendar.MONTH, startParts[1].toInt() - 1)
        startCalendar.set(Calendar.DAY_OF_MONTH, startParts[2].toInt())
        startCalendar.setToDayStart()
        val startTime = startCalendar.timeInMillis
        
        // 解析結束日期
        val endParts = endDateStr.split("-")
        endCalendar.set(Calendar.YEAR, endParts[0].toInt())
        endCalendar.set(Calendar.MONTH, endParts[1].toInt() - 1)
        endCalendar.set(Calendar.DAY_OF_MONTH, endParts[2].toInt())
        endCalendar.setToDayEnd()
        val endTime = endCalendar.timeInMillis
        
        return longArrayOf(startTime, endTime)
    }

    fun setStartTime(date: String) {
        _onStartTimeListener.value = date
        
        // 解析日期並設置為當天 0:0:0
        val parts = date.split("-")
        val calendar = calendar
        calendar.set(Calendar.YEAR, parts[0].toInt())
        calendar.set(Calendar.MONTH, parts[1].toInt() - 1)
        calendar.set(Calendar.DAY_OF_MONTH, parts[2].toInt())
        calendar.setToDayStart()
        
        _customTimeListener.value = calendar.timeInMillis
    }

    fun setEndTime(date: String) {
        _onEndTimeListener.value = date
        
        // 解析日期並設置為當天 23:59:59
        val parts = date.split("-")
        val calendar = calendar
        calendar.set(Calendar.YEAR, parts[0].toInt())
        calendar.set(Calendar.MONTH, parts[1].toInt() - 1)
        calendar.set(Calendar.DAY_OF_MONTH, parts[2].toInt())
        calendar.setToDayEnd()
        
        _customTimeListener.value = calendar.timeInMillis
    }
}