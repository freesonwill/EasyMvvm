package com.walisport.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.walisport.lib.base.data.viewmodel.BaseViewModel

open class NumberCalculatorViewModel : BaseViewModel() {

    companion object {
        const val MAX_MONEY = 10000
        const val MIN_MONEY = 10
    }

    private val _onEditMoney = MutableLiveData<String>()
    val onEditMoney: LiveData<String> = _onEditMoney

    fun addNumber(number: Int) {
        _onEditMoney.value = _onEditMoney.value?.let {
            val newValue = it + number
            val formattedValue = formatMoney(newValue)
            if (formattedValue.toDouble() > MAX_MONEY) MAX_MONEY.toString() else formattedValue
        } ?: number.toString()
    }

    fun setDot() {
        _onEditMoney.value = _onEditMoney.value?.let {
            if (!it.contains(".")) {
                "$it."
            } else {
                it
            }
        } ?: "0."
    }

    fun setMaxMoney() {
        _onEditMoney.value = MAX_MONEY.toString()
    }

    fun setNumber(number: Int) {
        if (number > MAX_MONEY) {
            setMaxMoney()
        } else {
            _onEditMoney.value = number.toString()
        }
    }

    fun clearMoney() {
        _onEditMoney.value = ""
    }

    fun double() {
        _onEditMoney.value = _onEditMoney.value?.let {
            if (it.isEmpty()) {
                ""
            } else {
                val money = if (it.last() == '.') {
                    it.substring(0, it.length - 1)
                } else {
                    it
                }
                val doubledValue = money.toDouble() * 2
                val formattedValue = formatMoney(doubledValue.toString())
                if (doubledValue > MAX_MONEY) MAX_MONEY.toString() else formattedValue
            }
        } ?: ""
    }

    fun back() {
        _onEditMoney.value = _onEditMoney.value?.let {
            if (it.length > 1) {
                it.substring(0, it.length - 1)
            } else {
                ""
            }
        }
    }

    private fun formatMoney(value: String): String {
        val regex = "^\\d*(\\.\\d{0,2})?$".toRegex()
        return if (regex.matches(value)) {
            value
        } else {
            value.toDoubleOrNull()?.let {
                "%.2f".format(it)
            } ?: ""
        }
    }
}