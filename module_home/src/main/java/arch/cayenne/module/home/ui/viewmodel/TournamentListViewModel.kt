package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.module.home.data.repo.TournamentListRepository
import arch.cayenne.module.home.ui.fragment.TournamentListType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

@KoinViewModel
class TournamentListViewModel : BaseViewModel() {
    private var type : TournamentListType = TournamentListType.MORE
    private var sportId = -1
    private val repo : TournamentListRepository by inject()

    private val _isLoading = MutableLiveData<Boolean>()

    val tournaments by lazy { MutableLiveData<List<BaseTournamentData>>() }

    fun setType(type: TournamentListType) {
        this.type = type
    }

    fun getType() = type

    fun setSportId(sportId: Int) {
        this.sportId = sportId
    }

    fun getTournaments() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val list = repo.getAllTournaments(type, sportId)
            withContext(Dispatchers.Main) {
                tournaments.value = list
            }
        }

    }


}