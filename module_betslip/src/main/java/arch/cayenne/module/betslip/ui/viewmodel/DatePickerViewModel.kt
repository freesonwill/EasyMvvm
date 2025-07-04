package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.getFormatDate
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipDateFilterEnum
import arch.cayenne.module.betslip.data.model.DateFilterBean
import java.util.Calendar

class DatePickerViewModel : BaseViewModel() {

    enum class Page {
        TIME, DATE
    }

    private val _dateTitleListener = MutableLiveData<List<DateFilterBean>>()
    val dateTitleListener: LiveData<List<DateFilterBean>> get() = _dateTitleListener

    private val _customTimeListener = MutableLiveData<Long?>()
    val customTimeListener: LiveData<Long?> get() = _customTimeListener

    private val _pageListener = MutableLiveData<Page>()
    val pageListener: LiveData<Page> get() = _pageListener

    val calendar: Calendar = Calendar.getInstance()

    val getCustomTime: Long
        get() {
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            return calendar.timeInMillis
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

    fun setCustomTime(time: Long?) {
        calendar.timeInMillis = time ?: System.currentTimeMillis()
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
        } ?: BetSlipDateFilterEnum.entries.last().title
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

    fun turnToDatePicker() {
        if (_customTimeListener.value == null) {
            setCustomTime(System.currentTimeMillis())
        }
        _pageListener.value = Page.DATE
    }

    fun backToTimePicker() {
        _pageListener.value = Page.TIME
    }
}