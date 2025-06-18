package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.getFormatDate
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipDateFilterEnum
import arch.cayenne.module.betslip.data.model.DateFilterBean

class DatePickerViewModel : BaseViewModel() {

    private val _dateTitleListener = MutableLiveData<List<DateFilterBean>>()
    val dateTitleListener: LiveData<List<DateFilterBean>> get() = _dateTitleListener

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
        val data = BetSlipDateFilterEnum.entries.map {
            DateFilterBean(
                it.title,
                it
            )
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
                        datePickerBean.copy(title = getFormatDate(), isSelected = index == position)
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
            val date = it.getFormatDate()
            R.string.date_picker_date_before.getString(date)
        } ?: BetSlipDateFilterEnum.entries.last().title
    }

    fun cancel() {
        customTime = null
        _dateTitleListener.value?.let {
            val newList = it.mapIndexed { index, datePickerBean ->
                if (index == 0) {
                    datePickerBean.copy(isSelected = true)
                } else if (index == it.lastIndex) {
                    datePickerBean.copy(title = getFormatDate(), isSelected = false)
                } else {
                    datePickerBean.copy(isSelected = false)
                }
            }
            _dateTitleListener.value = newList
        }
    }

    fun getSelectedDate(): BetSlipDateFilterEnum {
        _dateTitleListener.value?.let {
            val selected = it.firstOrNull { datePickerBean -> datePickerBean.isSelected }
            if (selected != null) {
                val index = it.indexOf(selected)
                if (index != -1) {
                    return BetSlipDateFilterEnum.entries[index]
                }
            }
        }
        return BetSlipDateFilterEnum.ALL
    }
}