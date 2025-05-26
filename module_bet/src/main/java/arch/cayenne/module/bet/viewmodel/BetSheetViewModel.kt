package arch.cayenne.module.bet.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.bet.repo.BetSheetRepository

class BetSheetViewModel(private val repo: BetSheetRepository) : BaseViewModel() {

    init {
        repo.register()
    }

    suspend fun getBetType() = repo.getBetType()

    fun unregister() {
        repo.unregister()
    }
}