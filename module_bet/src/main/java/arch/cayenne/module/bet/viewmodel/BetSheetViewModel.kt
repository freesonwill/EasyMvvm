package arch.cayenne.module.bet.viewmodel

import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.module.bet.repo.BetSheetRepository

class BetSheetViewModel(private val repo: BetSheetRepository) : BaseViewModel() {

    suspend fun getBetType() = repo.getBetType()
}