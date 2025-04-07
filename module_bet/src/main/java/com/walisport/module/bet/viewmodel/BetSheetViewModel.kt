package com.walisport.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.walisport.lib.database.entity.BetBean
import com.walisport.module.bet.repo.BetSheetRepository
import kotlinx.coroutines.launch

class BetSheetViewModel(private val betRepo: BetSheetRepository) : NumberCalculatorViewModel() {

    private val _onBetSheetListener = MutableLiveData<List<BetBean>>()
    val onBetSheetListener: LiveData<List<BetBean>> get() =  _onBetSheetListener

    init {
        viewModelScope.launch {
            betRepo.observeBetSheet().collect {
                _onBetSheetListener.value = it
            }
        }
    }
}