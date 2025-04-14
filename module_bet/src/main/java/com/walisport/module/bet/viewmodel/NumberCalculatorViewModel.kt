package com.walisport.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel

open class NumberCalculatorViewModel : BaseViewModel() {

    companion object {
        const val MAX_MONEY = 10000
        const val MIN_MONEY = 10
    }

    protected val _onEditMoney = MutableLiveData("")
    val onEditMoney: LiveData<String> = _onEditMoney

    fun addNumber(number: Int) {
        val current = _onEditMoney.value.orEmpty()

        val newValue = if (current.contains('.')) {
            val decimalPart = current.substringAfter('.', "")
            if (decimalPart.length >= 2) return  // 最多兩位小數，直接返回不修改
            current + number
        } else {
            current + number
        }

        val formatted = formatMoney(newValue)
        val limited = if (formatted.toDouble() > MAX_MONEY) MAX_MONEY.toString() else formatted

        _onEditMoney.value = limited
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

    fun setNumber(number: Float) {
        if (number > MAX_MONEY) {
            setMaxMoney()
        } else {
            _onEditMoney.value = number.toString()
        }
    }

    fun clearNumber() {
        _onEditMoney.value = ""
    }

    fun doubleNumber() {
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

    fun backNumber() {
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