package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.getFormatDate
import com.walisport.module.topup.R
import com.walisport.module.topup.data.DateFilterBean
import com.walisport.module.topup.data.DateFilterEnum
import java.util.Calendar

class DatePickerViewModel : BaseViewModel() {

    private val _dateTitleListener = MutableLiveData<List<DateFilterBean>>()
    val dateTitleListener: LiveData<List<DateFilterBean>> get() = _dateTitleListener

    private val _customTimeListener = MutableLiveData<Long?>()

    val getCustomTime: Long
        get() {
            return _customTimeListener.value?.let {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                calendar.timeInMillis
            } ?: System.currentTimeMillis()
        }

    init {
        val data = DateFilterEnum.entries.map {
            DateFilterBean(
                it.title,
                it
            )
        }
        _dateTitleListener.value = data
    }

    fun setCustomTime(time: Long?) {
        _customTimeListener.value = time
    }

    fun setSelected(position: Int) {
        _dateTitleListener.value?.let {
            if (position != it.lastIndex) {
                setCustomTime(null)
            }
            val newList = it.mapIndexed { index, datePickerBean ->
                when (index) {
                    it.lastIndex -> datePickerBean.copy(title = getFormatDate(), isSelected = index == position)
                    position -> datePickerBean.copy(isSelected = true)
                    else -> datePickerBean.copy(isSelected = false)
                }
            }
            _dateTitleListener.value = newList
        }
    }

    private fun getFormatDate(): String {
        return _customTimeListener.value?.let {
            val date = it.getFormatDate()
            R.string.date_picker_date_before.getString(date)
        } ?: DateFilterEnum.entries.last().title
    }

    fun cancel() {
        setCustomTime(null)
        _dateTitleListener.value?.let {
            val newList = it.mapIndexed { index, datePickerBean ->
                when (index) {
                    0 -> datePickerBean.copy(isSelected = true)
                    it.lastIndex -> datePickerBean.copy(title = getFormatDate(), isSelected = false)
                    else -> datePickerBean.copy(isSelected = false)
                }
            }
            _dateTitleListener.value = newList
        }
    }

    fun getSelectedDate(): DateFilterEnum {
        _dateTitleListener.value?.let {
            val selected = it.firstOrNull { datePickerBean -> datePickerBean.isSelected }
            if (selected != null) {
                val index = it.indexOf(selected)
                if (index != -1) {
                    return DateFilterEnum.entries[index]
                }
            }
        }
        return DateFilterEnum.ALL
    }
}