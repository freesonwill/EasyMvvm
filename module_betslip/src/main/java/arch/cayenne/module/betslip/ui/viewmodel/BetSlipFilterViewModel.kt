package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel

class BetSlipFilterViewModel: BaseViewModel() {

    private val _onDateTimeFilter = MutableLiveData<Pair<Long?, Long?>>(Pair(null, null))
    val onDateTimeFilter: LiveData<Pair<Long?, Long?>> get() = _onDateTimeFilter

    private val _onMatchSportIdFilter = MutableLiveData<Pair<Long, Int>>(Pair(-1, -1))
    val onMatchSportIdFilter: LiveData<Pair<Long, Int>> get() = _onMatchSportIdFilter

    fun setDateTime(startTime: Long?, endTime: Long?) {
        _onDateTimeFilter.value = Pair(startTime, endTime)
    }

    fun setIds(matchId: Long, sportId: Int) {
        _onMatchSportIdFilter.value = Pair(matchId, sportId)
    }
}