package arch.cayenne.module.betslip.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.SportDao
import arch.cayenne.module.betslip.BetSlipRemoteManager
import arch.cayenne.module.betslip.data.model.SportFilterBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class SportPickerRepository(
    override val scope: CoroutineScope,
    private val remoteManager: BetSlipRemoteManager,
    private val sportDao: SportDao
) : BaseRepository() {

    private val _observeSportFlow = MutableSharedFlow<List<SportFilterBean>>(replay = 1, extraBufferCapacity = 1)
    val observeSportFlow: Flow<List<SportFilterBean>> = _observeSportFlow

    init {
        scope.launch {
            sportDao.observeAllSports().collect {
                val list = mutableListOf<SportFilterBean>().apply {
                    add(SportFilterBean.getAllTypeBean())
                    addAll(it.map { sport ->
                        SportFilterBean(
                            sportId = sport.sportId,
                            sportName = sport.sportName
                        )
                    }.sortedBy { it.sportId })
                }
                _observeSportFlow.emit(list)
            }
        }

    }
}