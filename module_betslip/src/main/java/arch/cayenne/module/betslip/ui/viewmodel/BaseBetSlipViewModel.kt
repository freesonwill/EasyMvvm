package arch.cayenne.module.betslip.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.betslip.data.constants.BetSlipEnum


abstract class BaseBetSlipViewModel : BaseViewModel() {

    companion object {
        const val SIZE = 10
    }

    private var ids: Pair<Long, List<Int>> = Pair(-1, listOf(-1))
    protected val matchId: Long get() = ids.first
    protected val sportIds: List<Int> get() = ids.second

    private var times: Pair<Long?, Long?> = Pair(null, null)
    protected val startTime: Long? get() = times.first
    protected val endTime: Long? get() = times.second

    fun setIds(matchId: Long, sportId: Int) {
        this.ids = Pair(matchId, listOf(sportId))
    }

    fun setIds(matchId: Long, sportIds: List<Int>) {
        this.ids = Pair(matchId, sportIds)
    }

    fun setTime(startTime: Long?, endTime: Long?) {
        this.times = Pair(startTime, endTime)
    }

    abstract fun refreshData(status: BetSlipEnum)
    abstract fun loadMoreData(status: BetSlipEnum)
    abstract fun canLoadMore(): Boolean
    abstract fun deleteAll()
}