package arch.cayenne.module.betslip.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import kotlinx.coroutines.CoroutineScope

class HomeBetSlipRepository(override val scope: CoroutineScope, private val userManager: UserDataManager) : BaseRepository() {

    fun setDetail() {
        userManager.setKeyValue(UserDataKey.KEY_BETSLIP_DETAIL, true)
    }

}