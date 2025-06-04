package arch.cayenne.module.betslip.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.module.betslip.BetSlipRemoteManager
import kotlinx.coroutines.CoroutineScope

abstract class BaseBetSlipRepository(
    override val scope: CoroutineScope,
    protected val remoteManager: BetSlipRemoteManager
) : BaseRepository()