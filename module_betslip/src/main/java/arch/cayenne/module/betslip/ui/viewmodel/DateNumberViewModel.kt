package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import java.util.Calendar

class DateNumberViewModel: BaseViewModel() {

    private val _customTimeListener = MutableLiveData<Long?>()
    val customTimeListener: LiveData<Long?> get() = _customTimeListener

    var calendar: Calendar
        private set

    val getCustomTime: Long
        get() {
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            return calendar.timeInMillis
        }

    init {
        calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            _customTimeListener.value = timeInMillis
        }
    }

    fun setCustomTime(time: Long?) {
        if (_customTimeListener.value == null && time == null) return
        calendar.timeInMillis = time ?: System.currentTimeMillis()
        _customTimeListener.value = time
    }
}