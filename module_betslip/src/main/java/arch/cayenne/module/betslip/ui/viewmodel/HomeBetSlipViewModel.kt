package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.getFormatDate
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipDateFilterEnum
import arch.cayenne.module.betslip.data.model.DateFilterBean

class HomeBetSlipViewModel: BaseViewModel() {

    private val _onDateFilter = MutableLiveData<DateFilterBean>()
    val onDateFilter: LiveData<DateFilterBean> get() = _onDateFilter

    var customTime: Long? = null
        set(value) {
            field = value
            if (value != null && value > 0L) {
                setDateFilter(value)
            }
        }

    init {
        _onDateFilter.value = DateFilterBean(
            title = BetSlipDateFilterEnum.ALL.title,
            date = BetSlipDateFilterEnum.ALL
        )
    }

    fun setDateFilter(dateFilter: BetSlipDateFilterEnum) {
        _onDateFilter.value = DateFilterBean(
            title = dateFilter.title,
            date = dateFilter
        )
    }

    private fun setDateFilter(millisecond: Long) {
        val date = millisecond.getFormatDate()
        val title = R.string.date_picker_date_before.getString(date)
        _onDateFilter.value = DateFilterBean(
            title = title,
            date = BetSlipDateFilterEnum.CUSTOM
        )
    }
}