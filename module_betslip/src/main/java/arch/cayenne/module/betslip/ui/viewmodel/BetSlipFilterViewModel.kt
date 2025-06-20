package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.module.betslip.data.model.BetSlipFilterBean

class BetSlipFilterViewModel: BaseViewModel() {

    private val _onFilterChangeListener = MutableLiveData<BetSlipFilterBean>()
    val onFilterChangeListener: LiveData<BetSlipFilterBean> get() = _onFilterChangeListener

    private var startTime: Long? = null
    private var endTime: Long? = null
    private var sportId: List<Int> = listOf(-1)
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
        this.sportId = listOf(sportId)
    }

    fun setIds(matchId: Long, sportIds: List<Int>) {
        this.matchId = matchId
        this.sportId = sportIds
        updateFilter()
    }

    /**
     * 在Fragment OnResume时，更新matchId，
     * 检查matchId 和 sportId,不做重复查询
     * */
    fun checkUpdate(){
        val filter = onFilterChangeListener.value
        if(filter?.matchId != matchId){
            updateFilter()
        }
    }

    private fun updateFilter() {
        _onFilterChangeListener.value = BetSlipFilterBean(
            sportIds = sportId,
            matchId = matchId,
            startTime = startTime,
            endTime = endTime
        )
    }
}