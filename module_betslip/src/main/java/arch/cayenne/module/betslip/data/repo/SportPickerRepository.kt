package arch.cayenne.module.betslip.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.SportDao
import arch.cayenne.module.betslip.data.model.SportFilterBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class SportPickerRepository(
    override val scope: CoroutineScope,
    private val sportDao: SportDao
) : BaseRepository() {

    suspend fun getAllSports() = withContext(scope.coroutineContext) {
        sportDao.getAllSports().map {
            SportFilterBean(
                sportId = it.sportId,
                sportName = it.sportName
            )
        }
    }

}