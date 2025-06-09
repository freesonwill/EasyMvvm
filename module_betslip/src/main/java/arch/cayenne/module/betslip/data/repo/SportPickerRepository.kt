package arch.cayenne.module.betslip.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.SportDao
import arch.cayenne.module.betslip.BetSlipRemoteManager
import arch.cayenne.module.betslip.data.model.SportFilterBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class SportPickerRepository(
    override val scope: CoroutineScope,
    private val remoteManager: BetSlipRemoteManager,
    private val sportDao: SportDao
) : BaseRepository() {

    suspend fun getAllSports() = withContext(scope.coroutineContext) {
        mutableListOf<SportFilterBean>().apply {
            add(SportFilterBean.getAllTypeBean())
            addAll(sportDao.getAllSports().map {
                SportFilterBean(
                    sportId = it.sportId,
                    sportName = it.sportName
                )
            })
        }
    }

    suspend fun getSportList() = withContext(scope.coroutineContext) {
        remoteManager.getSportList().map {
            SportFilterBean(
                sportId = it.sportId,
                sportName = it.sportName
            )
        }
    }

}