package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.repo.BetRepository
import arch.cayenne.module.bet.repo.BetSheetRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class BetSheetViewModel(
    private val repo: BetSheetRepository,
    private val betRepo: BetRepository
) : BaseViewModel() {

    private val _betSheetSizeListener = MutableLiveData<Int>()
    val betSheetSizeListener: LiveData<Int> get() = _betSheetSizeListener

    val count: Int
        get() = _betSheetSizeListener.value ?: 0

    init {
        viewModelScope.launch {
            repo.observerBetCount.distinctUntilChanged().collect { count ->
                _betSheetSizeListener.value = count
            }
        }
    }

    suspend fun getSelectionSize() = repo.getSelectionSize()

    fun unregister() {
        repo.unregister()
        repo.stopObserveLoginStatus()
    }

    fun register() {
        repo.register()
        repo.observeLoginStatus()
    }

    fun removeSingleBet() {
        repo.removeSingleBet()
    }

    fun cancel() {
        betRepo.cancelAdd()
    }

    suspend fun getBetType(): BetTypeEnum? = repo.getBetType()
}