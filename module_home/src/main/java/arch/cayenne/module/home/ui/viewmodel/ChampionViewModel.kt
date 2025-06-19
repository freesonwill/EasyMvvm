package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.bet.repo.BetRepository
import arch.cayenne.module.home.data.repo.ChampionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

@KoinViewModel
class ChampionViewModel : BaseViewModel() {
    private val championRepository: ChampionRepository  by inject()
    private val betRepository: BetRepository by inject()
    private val balanceRepository: BalanceRepository by inject()

    private var matchId: Long = 0
    val currentBalanceChange by lazy { MutableLiveData<InfoBean>() }
    val matchWithMarketsChange by lazy { MutableLiveData<MatchWithMarkets?>() }
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    override fun initViewModel() {
        super.initViewModel()
        // 觀察賽事訂閱後，後端主動送出的變化
        viewModelScope.launch(Dispatchers.IO) {
            championRepository.observeMatchNotify().collect { matchWithMarket ->
                if (matchWithMarketsChange.value == null) return@collect
                if (matchWithMarketsChange.value!!.match.matchId != matchWithMarket.match.matchId) return@collect
                withContext(Dispatchers.Main) {
                    matchWithMarketsChange.value = matchWithMarket
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            balanceRepository.observeBalance().collect {
                withContext(Dispatchers.Main) {
                    currentBalanceChange.value = it
                }
            }
        }
        //觀察投注單的變化，主要用來做selection變更
        viewModelScope.launch(Dispatchers.IO) {
            betRepository.observerAllBet.distinctUntilChanged().collect { betSelectionBeans ->
                if (matchWithMarketsChange.value == null) return@collect
                val matchWithMarkets = championRepository.getOnCurrentMatch(matchId, betSelectionBeans.map { it.selectionId })
                withContext(Dispatchers.Main) {
                    matchWithMarketsChange.value = matchWithMarkets
                }
            }
        }
    }

    fun setMatchId(matchId: Long) {
        this.matchId = matchId
    }
    fun getChampionDetail() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val matchWithMarkets = championRepository.getChampionDetail(matchId)
            withContext(Dispatchers.Main) {
                matchWithMarketsChange.value = matchWithMarkets
                _isLoading.value = false
            }
        }
    }

    fun subscribeMatch() {
        viewModelScope.launch(Dispatchers.IO) {
            championRepository.subscribeMatch(arrayListOf(matchId))
        }
    }
    fun cancelSubscribeMatch() {
        viewModelScope.launch(Dispatchers.IO) {
            "取消訂閱比賽  $matchId".logi(this::class.java.name)
            championRepository.cancelSubscribeMatch(arrayListOf(matchId))
        }
    }

    suspend fun setSelection(selectionId: Long) : AddSelectionStatus {
        val bean = championRepository.getSelectionInsertBean(matchId, selectionId)
        return if (bean == null) {
            AddSelectionStatus.FAIL
        } else {
            betRepository.setSelection(bean)
        }
    }

}