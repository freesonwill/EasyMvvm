package com.walisport.module.bet.viewmodel

class ReserveDialogViewModel : NumberCalculatorViewModel() {

    val mixRate = 0.01f

    fun addMixRate() {
        _onEditMoney.value = _onEditMoney.value?.let {
            if (it.isEmpty()) {
                mixRate.toString()
            } else {
                val rate = if (it.last() == '.') {
                    it.substring(0, it.length - 1)
                } else {
                    it
                }
                val doubledValue = rate.toFloat()
                val value = doubledValue + mixRate
                "%.2f".format(value)
            }
        } ?: mixRate.toString()
    }

    fun reserve() {
        //TODO REPO AND DB
    }
}