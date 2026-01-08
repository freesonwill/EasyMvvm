package com.walisport.module.me.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.database.entity.UserDataBean
import com.walisport.module.business.common.data.repo.BalanceRepository
import com.walisport.module.me.data.MeRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class MeVIPInfoViewModel : BaseViewModel() {

    private val repository: MeRepository by inject { parametersOf(viewModelScope) }
    private val balanceRepo: BalanceRepository by inject { parametersOf(viewModelScope) }

    private val _onVipListener = MutableLiveData<UserDataBean>()
    val onVipListener: LiveData<UserDataBean> get() = _onVipListener

    val balanceFlow = balanceRepo.observeInfo().map {
        if(it == null)
            return@map ""
        CurrencySymbols.getSymbol(it.currency) + it.balance.getFormalMoney()
    }

    init {
        viewModelScope.launch {
            repository.observeUserInfo().collect {
                _onVipListener.value = it
            }
        }
    }

    //获取账户信息
    fun getAccountInfo() {
        viewModelScope.launch {
            repository.getAccountInfo()
        }
    }
}