package arch.cayenne.module.picker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.ResourceExt.getStringArray
import arch.cayenne.module.bet.R
import java.util.Calendar

class DatePickerViewModel: BaseViewModel() {

    private val _dateTitleListener = MutableLiveData<List<DatePickerBean>>()
    val dateTitleListener: LiveData<List<DatePickerBean>> get() = _dateTitleListener

    private val dateArray = R.array.date_picker.getStringArray()

    var customTime: Long? = null
        set(value) {
            field = value
            if (value != null) {
                _dateTitleListener.value?.let {
                    setSelected(it.lastIndex)
                }
            }
        }

    init {
        val data = dateArray.map {
            DatePickerBean(it)
        }
        _dateTitleListener.value = data
    }

    fun setSelected(position: Int) {
        _dateTitleListener.value?.let {
            if (position != it.lastIndex) {
                customTime = null
            }
            val newList = it.mapIndexed { index, datePickerBean ->
                when (index) {
                    it.lastIndex -> {
                        datePickerBean.copy(date = getFormatDate(), isSelected = index == position)
                    }
                    position -> {
                        datePickerBean.copy(isSelected = true)
                    }
                    else -> {
                        datePickerBean.copy(isSelected = false)
                    }
                }
            }
            _dateTitleListener.value = newList
        }
    }

    private fun getFormatDate(): String {
        return customTime?.let {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = customTime!!

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val time = "$year/$month/$day"
            R.string.date_picker_date_before.getString(time)
        } ?: dateArray.last()
    }

    fun cancel() {
        customTime = null
        _dateTitleListener.value?.let {
            val newList = it.mapIndexed { index, datePickerBean ->
                if (index == it.lastIndex) {
                    datePickerBean.copy(date = getFormatDate(), isSelected = false)
                } else {
                    datePickerBean.copy(isSelected = false)
                }
            }
            _dateTitleListener.value = newList
        }
    }
}