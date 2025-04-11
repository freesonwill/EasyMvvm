package com.walisport.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import com.walisport.lib.database.entity.BetBean
import com.walisport.module.bet.repo.ComboBetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ComboBetViewModel(private val repo: ComboBetRepository): BaseViewModel() {

    private val _onBetListListener = MutableLiveData<List<BetBean>>()
    val onBetListListener: LiveData<List<BetBean>> get() = _onBetListListener

    init {
        viewModelScope.launch {
            repo.observeComboBet().collect {
                _onBetListListener.value = it
                if (it.size <= 1) {
                    if (it.isNotEmpty()) {
                        repo.saveToSingleBet(it.first().gameId)
                    }
                }
            }
        }
    }

    fun removeBet(id: Int) {
        viewModelScope.launch {
            repo.removeBet(id)
        }
    }

    fun removeAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeAll()
        }
    }
}