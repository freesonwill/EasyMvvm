package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.getFormatDate
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipDateFilterEnum
import arch.cayenne.module.betslip.data.model.DateFilterBean
import arch.cayenne.module.betslip.data.model.SportFilterBean
import arch.cayenne.module.betslip.data.repo.HomeBetSlipRepository
import kotlinx.coroutines.launch

class HomeBetSlipViewModel(private val repo: HomeBetSlipRepository) : BaseViewModel() {

    private val _onDateFilter = MutableLiveData<DateFilterBean>()
    val onDateFilter: LiveData<DateFilterBean> get() = _onDateFilter

    private val _onSportFilter = MutableLiveData<List<SportFilterBean>>()
    val onSportFilter: LiveData<List<SportFilterBean>> get() = _onSportFilter

    var customTime: Long? = null
        private set

    init {
        _onDateFilter.value = DateFilterBean(
            title = BetSlipDateFilterEnum.ALL.title,
            date = BetSlipDateFilterEnum.ALL
        )
        _onSportFilter.value = listOf(SportFilterBean.getAllTypeBean())
        repo.setDetail()
    }

    fun setDateFilter(dateFilter: BetSlipDateFilterEnum) {
        _onDateFilter.value = DateFilterBean(
            title = dateFilter.title,
            date = dateFilter
        )
    }

    fun setDateFilter(millisecond: Long) {
        customTime = millisecond
        val date = millisecond.getFormatDate()
        val title = R.string.date_picker_date_before.getString(date)
        _onDateFilter.value = DateFilterBean(
            title = title,
            date = BetSlipDateFilterEnum.CUSTOM
        )
    }

    fun setSportFilter(ids: List<Int>) {
        if (ids.size == 1 && ids.first() == SportFilterBean.ALL_TYPE_ID) {
            _onSportFilter.value = listOf(SportFilterBean.getAllTypeBean())
        } else {
            viewModelScope.launch {
                _onSportFilter.value = repo.getSportByIds(ids)
            }
        }
    }
}