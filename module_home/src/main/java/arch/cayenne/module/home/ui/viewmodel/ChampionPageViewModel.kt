package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.bet.data.AddSelectionStatus
import arch.cayenne.module.bet.data.BetInsertBean
import arch.cayenne.module.bet.repo.BetRepository
import arch.cayenne.module.home.data.repo.ChampionRepository
import com.walisport.module.business.common.repo.BalanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

@KoinViewModel
class ChampionPageViewModel : BaseViewModel() {
    private val championRepository: ChampionRepository  by inject()
    private val betRepository: BetRepository by inject()
    private val balanceRepository: BalanceRepository by inject()

    private var matchId: Long = 0
    val currentBalanceChange by lazy { MutableLiveData<InfoBean?>() }
    val matchWithMarketsChange by lazy { MutableLiveData<MatchWithMarkets?>() }

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
            balanceRepository.observeInfo().collect {
                withContext(Dispatchers.Main) {
                    currentBalanceChange.value = it
                }
            }
        }
        //觀察投注單的變化，主要用來做selection變更
        viewModelScope.launch(Dispatchers.IO) {
            betRepository.observerAllBet
                .distinctUntilChanged()
                .collect { betSelectionBeans ->
                if (matchWithMarketsChange.value == null) return@collect
                val matchWithMarkets = championRepository.getOnCurrentMatch(matchId, betSelectionBeans.map { it.selectionId })
                withContext(Dispatchers.Main) {
                    matchWithMarketsChange.value = matchWithMarkets
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            championRepository.observeLoginChange()
                .filter { it }
                .collect {
                    launch(Dispatchers.Main) {
                        subscribeMatch()
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

    suspend fun setSelection(selection: SelectionBeanLite) : AddSelectionStatus {
        if (!betRepository.isConnected) {
            return AddSelectionStatus.Failure.NetworkDisconnected
        }
        var bean: BetInsertBean? = null
        matchWithMarketsChange.value?.also { matchWithMarkets ->
            matchWithMarkets.markets.forEach { marketWithSelections ->
                    val selectionLiteBean = marketWithSelections.selections.find { it.selectionId == selection.selectionId }
                    if (selectionLiteBean  != null) {
                        bean = championRepository.matchSelectionInsertBean(
                            match = matchWithMarkets.match,
                            market = marketWithSelections.market,
                            selectionBean = selectionLiteBean
                        )
                    }
                }
        }
        return if (bean == null) {
            AddSelectionStatus.Failure.Fail
        } else {
            betRepository.setSelection(bean!!)
        }
    }

    fun getCurrentSelectionCount(): Int = betRepository.count
}