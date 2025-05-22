package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.betslip.data.model.BetSlipFilterBean

class BetSlipFilterViewModel: BaseViewModel() {

    private val _onFilterChangeListener = MutableLiveData<BetSlipFilterBean>()
    val onFilterChangeListener: LiveData<BetSlipFilterBean> get() = _onFilterChangeListener

    private var startTime: Long? = null
    private var endTime: Long? = null
    private var sportId: Int = -1
    private var matchId: Long = -1

    fun init() {
        updateFilter()
    }

    fun setDateTime(startTime: Long?, endTime: Long?) {
        this.startTime = startTime
        this.endTime = endTime
        updateFilter()
    }

    fun setIds(matchId: Long, sportId: Int) {
        this.matchId = matchId
        this.sportId = sportId
        updateFilter()
    }

    private fun updateFilter() {
        _onFilterChangeListener.value = BetSlipFilterBean(
            sportId = sportId,
            matchId = matchId,
            startTime = startTime,
            endTime = endTime
        )
    }
}