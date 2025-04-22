package arch.cayenne.module.bet.viewmodel

import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
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
                (it.toOdds() + minOdds)
            }
        } ?: minOdds
        setEditNumber(odds.toLong())
    }

    fun reserve(id: Long) {
        val odds = onEditNumber.value?.toOdds() ?: 0
        repository.setSingleToReserve(id, odds)
    }
}