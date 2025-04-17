package arch.cayenne.module.bet.viewmodel

import arch.cayenne.lib.common.utils.ext.StringExt.toValue
import arch.cayenne.module.bet.repo.ReserveRepository

class ReserveDialogViewModel(private val repository: ReserveRepository) : NumberCalculatorViewModel() {

    val mixRate = 0.01f

    fun addMixRate() {
        _onEditNumber.value = _onEditNumber.value?.let {
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

    fun reserve(id: Int) {
        val odds = _onEditNumber.value?.toValue() ?: 0
        repository.setSingleToReserve(id, odds)
    }
}