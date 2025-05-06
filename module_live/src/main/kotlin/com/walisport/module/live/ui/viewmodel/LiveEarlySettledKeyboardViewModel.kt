package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.live.data.repository.LiveBetRepository
import galaxy.common.proto.Common.EarlySettlePrice
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal


class LiveEarlySettledKeyboardViewModel : BaseViewModel() {
    private val _editNumber: MutableLiveData<String> = MutableLiveData()
    private val repository: LiveBetRepository by inject { parametersOf(viewModelScope) }
    var prices: MutableLiveData<EarlySettlePrice> = MutableLiveData()
    val editNumber: LiveData<String> = _editNumber.distinctUntilChanged()


    fun addNumber(number: Int) {
        val current = _editNumber.value ?: ""
        if (current.contains(".")) {
            val lastValue = current.substringAfter(".")
            if (lastValue.length > 2) return
        }
        _editNumber.value = current + number
    }

    fun setNumber(number: Int) {
        _editNumber.value = number.toString()
    }

    fun setDot() {
        val current = _editNumber.value ?: ""
        if (current.isEmpty()) {
            _editNumber.value = "0."
            return
        } else if (current.contains(".")) {
            return
        }
        _editNumber.value = "$current."
    }

    fun clearNumber() {
        _editNumber.value = ""
    }

    fun doubleNumber() {
        _editNumber.value?.let {
            if (it.isEmpty()) {
                ""
            } else {
                _editNumber.value = BigDecimal(it).multiply(BigDecimal(2)).toString()
            }
        } ?: ""
    }

    fun backNumber() {
        _editNumber.value = _editNumber.value?.let {
            if (it.length > 1) {
                it.substring(0, it.length - 1)
            } else {
                ""
            }
        }
    }

    fun earlySettledPrice(betId: String) {
        viewModelScope.launch {
            val result = repository.earlySettledPrice(betId)
            if (!result.isNullOrEmpty()) {
                prices.value = result.first()
            }
        }
    }


}