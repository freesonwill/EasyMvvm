package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import java.math.BigDecimal


class LiveEarlySettledKeyboardViewModel : BaseViewModel() {
    private val _editNumber: MutableLiveData<String> = MutableLiveData()
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


}