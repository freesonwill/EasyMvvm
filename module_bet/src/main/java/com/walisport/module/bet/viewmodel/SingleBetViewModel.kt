package com.walisport.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.walisport.lib.database.entity.BetBean
import com.walisport.module.bet.repo.BetSheetRepository
import kotlinx.coroutines.launch

class SingleBetViewModel(private val betRepo: BetSheetRepository) : NumberCalculatorViewModel() {

    private val _onBetSheetListener = MutableLiveData<List<BetBean>>()
    val onBetSheetListener: LiveData<List<BetBean>> get() =  _onBetSheetListener

    private val _onBetWinMoney = MediatorLiveData<String>().apply {
        var odds = 1.0f
        addSource(_onBetSheetListener) { data ->
            data.forEach {
                odds *= it.odds
            }
        }
        addSource(_onEditMoney) {
            val money = if (it.isEmpty()) {
                "0"
            } else if (it.last() == '.') {
                it.substring(0, it.length - 1)
            } else {
                it
            }
            value = if (money.isEmpty()) {
                "0.00"
            } else {
                val betMoney = money.toFloatOrNull() ?: 0f
                String.format("%.2f", betMoney * odds)
            }
        }
    }
    val onBetWinMoney: LiveData<String> get() = _onBetWinMoney

    init {
        viewModelScope.launch {
            betRepo.observeBetSheet().collect {
                _onBetSheetListener.value = it
            }
        }
    }

    fun sendBet() {

    }
}