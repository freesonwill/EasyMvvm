package arch.cayenne.module.betslip.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import kotlinx.coroutines.CoroutineScope
import org.koin.java.KoinJavaComponent

class BetSlipOtherSettingRepository(override val scope: CoroutineScope): BaseRepository() {

    private val userManager: UserDataManager by KoinJavaComponent.inject(UserDataManager::class.java)

    fun isBetSlipDetail() = userManager.getValue(UserDataKey.KEY_BETSLIP_DETAIL, false)
}