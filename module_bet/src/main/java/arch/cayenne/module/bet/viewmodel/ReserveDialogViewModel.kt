package arch.cayenne.module.bet.viewmodel

import arch.cayenne.lib.common.utils.ext.StringExt.toValue
import arch.cayenne.module.bet.repo.ReserveRepository

class ReserveDialogViewModel(private val repository: ReserveRepository) : NumberCalculatorViewModel() {

    val minOdds = 1

    fun addMixRate() {
        val odds = onEditNumber.value?.let {
            if (it.isEmpty()) {
                minOdds
            } else {
                val rate = if (it.last() == '.') {
                    it.substring(0, it.length - 1)
                } else {
                    it
                }
                (it.toValue() + minOdds)
            }
        } ?: minOdds
        setEditNumber(odds)
    }

    fun reserve(id: Int) {
        val odds = onEditNumber.value?.toValue() ?: 0
        repository.setSingleToReserve(id, odds)
    }
}