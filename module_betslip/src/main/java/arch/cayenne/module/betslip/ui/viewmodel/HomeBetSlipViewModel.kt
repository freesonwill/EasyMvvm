package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.getFormatDate
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipDateFilterEnum
import arch.cayenne.module.betslip.data.model.DateFilterBean
import arch.cayenne.module.betslip.data.model.SportFilterBean
import arch.cayenne.module.betslip.data.repo.HomeBetSlipRepository

class HomeBetSlipViewModel(repo: HomeBetSlipRepository) : BaseViewModel() {

    private val _onDateFilter = MutableLiveData<DateFilterBean>()
    val onDateFilter: LiveData<DateFilterBean> get() = _onDateFilter

    private val _onSportFilter = MutableLiveData<SportFilterBean>()
    val onSportFilter: LiveData<SportFilterBean> get() = _onSportFilter

    var customTime: Long? = null
        private set

    init {
        _onDateFilter.value = DateFilterBean(
            title = BetSlipDateFilterEnum.ALL.title,
            date = BetSlipDateFilterEnum.ALL
        )
        _onSportFilter.value = SportFilterBean.getAllTypeBean()
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

    fun setSportFilter(id: Int, name: String) {
        _onSportFilter.value = SportFilterBean(
            sportId = id,
            sportName = name,
            isSelected = true
        )
    }
}