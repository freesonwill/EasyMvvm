package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.module.bet.data.ComboRateBean
import arch.cayenne.module.bet.repo.ComboBetRepository
import kotlinx.coroutines.launch
import kotlin.math.pow

class ComboBetViewModel(private val repo: ComboBetRepository) : BaseViewModel() {

    private val _onBetListListener = MutableLiveData<List<BetBean>>()
    val onBetListListener: LiveData<List<BetBean>> get() = _onBetListListener

    private val _onComboRateListener = MutableLiveData<List<ComboRateBean>>()
    val onComboRateListener: LiveData<List<ComboRateBean>> get() = _onComboRateListener

    init {
        viewModelScope.launch {
            repo.observeComboBet().collect {
                _onBetListListener.value = it
                if (it.size <= 1) {
                    if (it.isNotEmpty()) {
                        repo.saveToSingleBet(it.first().matchId)
                    }
                } else {
                    _onComboRateListener.value = calculateMultiRateSums(it)
                }
            }
        }
    }

    private fun calculateMultiRateSums(data: List<BetBean>): List<ComboRateBean> {
        val result = mutableListOf<ComboRateBean>()
        val n = data.size
        for (k in n downTo 1) {
            val combinations = data.combinations(k)

            val totalRate = combinations.fold(0L) { acc, combo ->
                acc + combo.fold(1L) { prod, bet -> prod * bet.selection.odds }
            }

            // 將 totalRate 無條件捨去為倍率的前兩位，例如：39204 -> 392
            val scale = 10.0.pow((k * 2)).toLong() // 每個 odds 是 x100，所以總共乘了 100^k = 10^(2k)
            val oddsInt = (totalRate / (scale / 100)).toInt() // 例如：39204 / 100 = 392

            result.add(ComboRateBean(combo = k, odds = oddsInt))
        }
        return result
    }

    private fun <T> List<T>.combinations(k: Int): List<List<T>> {
        if (k == 0) return listOf(emptyList())
        if (this.isEmpty()) return emptyList()

        val head = first()
        val tail = drop(1)

        val withHead = tail.combinations(k - 1).map { listOf(head) + it }
        val withoutHead = tail.combinations(k)

        return withHead + withoutHead
    }

    fun removeBet(id: Int) {
        viewModelScope.launch {
            repo.removeBet(id)
        }
    }

    fun removeAll() {
        repo.removeAll()
    }

    fun updateRateMoney(id: Int, money: String) {
        _onComboRateListener.value?.let {
            val updatedList = it.map { rate ->
                if (rate.combo == id) {
                    rate.copy(money = money)
                } else {
                    rate
                }
            }
            _onComboRateListener.value = updatedList
        }
    }

    fun sendBet() {
        val betList = _onBetListListener.value ?: return
        repo.sendBet(betList.map { it.matchId })
    }
}