package arch.cayenne.module.betslip.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.dao.SportDao
import arch.cayenne.module.betslip.data.model.SportFilterBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class HomeBetSlipRepository(
    override val scope: CoroutineScope,
    private val sportDao: SportDao,
    private val userManager: UserDataManager
) : BaseRepository() {

    fun setDetail() {
        userManager.setKeyValue(UserDataKey.KEY_BETSLIP_DETAIL, true)
    }

    suspend fun getSportByIds(ids: List<Int>) = withContext(scope.coroutineContext) {
        sportDao.getSportByIds(ids).map {
            SportFilterBean(
                sportId = it.sportId,
                sportName = it.sportName,
                isSelected = ids.contains(it.sportId)
            )
        }
    }

}