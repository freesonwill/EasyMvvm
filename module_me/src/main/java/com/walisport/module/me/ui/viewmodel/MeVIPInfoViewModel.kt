package com.walisport.module.me.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.VIPDataExt
import com.walisport.module.me.data.MeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class MeVIPInfoViewModel : BaseViewModel() {

    private val repository: MeRepository by inject { parametersOf(viewModelScope) }
    private val balanceRepo: BalanceRepository by inject { parametersOf(viewModelScope) }

    private val _vipLevelLiveData = MutableLiveData<Long>(75)
    val vipLevelLiveData: LiveData<Long> = _vipLevelLiveData
    val balanceFlow = balanceRepo.observeInfo().map {
        if(it == null) return@map ""
        CurrencySymbols.getSymbol(it.currency) + it.balance.getFormalMoney()
    }

    override fun initViewModel() {
        super.initViewModel()
    }

    fun createObserver() {
        viewModelScope.launch {
            delay(1000)
            val vipLevel = 75L
            _vipLevelLiveData.value = vipLevel
            // 將 VIP 等級同步到 UserDataManager，讓其他模組也能獲取
            VIPDataExt.setVIPLevel(vipLevel)
        }
    }
}