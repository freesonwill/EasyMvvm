package arch.cayenne.module.picker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.ResourceExt.getStringArray
import arch.cayenne.module.bet.R

class DatePickerViewModel: BaseViewModel() {

    private val _dateTitleListener = MutableLiveData<List<DatePickerBean>>()
    val dateTitleListener: LiveData<List<DatePickerBean>> get() = _dateTitleListener

    init {
        val data = R.array.date_picker.getStringArray().map {
            DatePickerBean(it)
        }
        _dateTitleListener.value = data
    }

    fun setSelected(position: Int) {
        _dateTitleListener.value?.let {
            val newList = it.mapIndexed { index, datePickerBean ->
                if (index == position) {
                    datePickerBean.copy(isSelected = true)
                } else {
                    datePickerBean.copy(isSelected = false)
                }
            }
            _dateTitleListener.value = newList
        }
    }

    fun cancel() {
        _dateTitleListener.value?.let {
            val newList = it.mapIndexed { _, datePickerBean ->
                datePickerBean.copy(isSelected = false)
            }
            _dateTitleListener.value = newList
        }
    }
}