package arch.cayenne.module.betslip.ui.viewmodel

import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.module.betslip.data.repo.ConfirmingSlipRepository

class ConfirmingSlipViewModel(private val repo: ConfirmingSlipRepository): OrderSlipViewModel(repo) {

    override fun setData(data: List<BetSlipOrderBean>) {
        super.setData(data)
        if (data.isEmpty()) {
            repo.unregisterOrderStatus()
        } else {
            repo.registerOrderStatus()
        }
    }
}