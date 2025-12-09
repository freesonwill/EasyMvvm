package arch.cayenne.module.order.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import java.util.Calendar

class OrderDateSelectorViewModel: BaseViewModel() {

    private val _customTimeListener = MutableLiveData<Long?>()
    val customTimeListener: LiveData<Long?> get() = _customTimeListener

    var calendar: Calendar
        private set

    init {
        calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            _customTimeListener.value = timeInMillis
        }
    }

    fun getTimeBetweenOneDay(): LongArray {
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endTime = calendar.timeInMillis
        return longArrayOf(endTime)
    }
}