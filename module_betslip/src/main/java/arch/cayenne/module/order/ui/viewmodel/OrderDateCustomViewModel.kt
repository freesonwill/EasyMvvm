package arch.cayenne.module.order.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import java.util.Calendar

class OrderDateCustomViewModel: BaseViewModel() {

    private val _onStartTimeListener = MutableLiveData<String>()
    val onStartTimeListener: LiveData<String> get() = _onStartTimeListener

    private val _onEndTimeListener = MutableLiveData<String>()
    val onEndTimeListener: LiveData<String> get() = _onEndTimeListener

    init {
        initDefaultTime()
    }

    private fun initDefaultTime() {
        val c = Calendar.getInstance()
        c.timeInMillis = System.currentTimeMillis()
        
        val year = c.get(Calendar.YEAR)
        val month = String.format("%02d", c.get(Calendar.MONTH) + 1)
        val day = String.format("%02d", c.get(Calendar.DAY_OF_MONTH))
        
        val currentDate = "$year-$month-$day"
        _onStartTimeListener.value = currentDate
        _onEndTimeListener.value = currentDate
    }
}