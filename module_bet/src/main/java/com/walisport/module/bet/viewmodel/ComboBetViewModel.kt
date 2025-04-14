package com.walisport.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.walisport.lib.base.data.viewmodel.BaseViewModel
import com.walisport.lib.database.entity.BetBean
import com.walisport.module.bet.data.ComboRateBean
import com.walisport.module.bet.repo.ComboBetRepository
import kotlinx.coroutines.launch

class ComboBetViewModel(private val repo: ComboBetRepository): BaseViewModel() {

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
                        repo.saveToSingleBet(it.first().gameId)
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
            val totalRate = combinations.fold(0f) { acc, combo ->
                acc + combo.fold(1f) { prod, bet -> prod * bet.odds }
            }
            // 四捨五入到小數點後兩位
            val rounded = String.format("%.2f", totalRate).toFloat()
            result.add(ComboRateBean(combo = k, rate = rounded))
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
}