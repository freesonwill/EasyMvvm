package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.bet.repo.BetRepository
import arch.cayenne.module.home.data.repo.ChampionRepository
import kotlinx.coroutines.Dispatchers
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
    val currentBalanceChange by lazy { MutableLiveData<Long>() }
    val matchWithMarketsChange by lazy { MutableLiveData<MatchWithMarkets?>() }

    override fun initViewModel() {
        super.initViewModel()
        viewModelScope.launch(Dispatchers.IO) {
            balanceRepository.observeBalance().collect {
                withContext(Dispatchers.Main) {
                    currentBalanceChange.value = it
                }
            }
        }
    }

    fun setMatchId(matchId: Long) {
        this.matchId = matchId
    }
    fun getChampionDetail() {
        viewModelScope.launch(Dispatchers.IO) {
            val matchWithMarkets = championRepository.getChampionDetail(matchId)
            withContext(Dispatchers.Main) {
                matchWithMarketsChange.value = matchWithMarkets
            }

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