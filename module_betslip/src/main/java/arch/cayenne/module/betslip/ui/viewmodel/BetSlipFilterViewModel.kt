package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel

abstract class BetSlipFilterViewModel: BaseViewModel() {

    private val _onDateTimeFilter = MutableLiveData<Pair<Long?, Long?>>()
    val onDateTimeFilter: LiveData<Pair<Long?, Long?>> get() = _onDateTimeFilter

    private val _onSportIdFilter = MutableLiveData<Int>()
    val onSportIdFilter: LiveData<Int> get() = _onSportIdFilter

    fun setDateTimeFilter(startTime: Long?, endTime: Long?) {
        _onDateTimeFilter.value = Pair(startTime, endTime)
    }

    fun setSportIdFilter(sportId: Int) {
        _onSportIdFilter.value = sportId
    }
}