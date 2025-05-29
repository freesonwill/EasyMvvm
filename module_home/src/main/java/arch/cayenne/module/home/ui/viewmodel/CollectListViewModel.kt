package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.data.repo.CollectListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

/**
 * @author:
 * @date: 2025/5/23 上午11:32
 * @description:
 */
@KoinViewModel
class CollectListViewModel : BaseViewModel() {
    private val balanceRepository: BalanceRepository by inject()
    private val collectListRepository : CollectListRepository by inject()
    val currentBalanceChange by lazy { MutableLiveData<Long>() }
    val matchListChange by lazy { MutableLiveData<List<MatchWithMarkets>>() }
    private var page = 1

    override fun initViewModel() {
        super.initViewModel()
        viewModelScope.launch(Dispatchers.IO) {
            balanceRepository.observeBalance().collect {
                withContext(Dispatchers.Main) {
                    currentBalanceChange.value = it
                }
            }
        }
    }

    fun getCollect() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = collectListRepository.getCollectData(page)
            withContext(Dispatchers.Main) {
                matchListChange.value = list
            }
        }
    }
}
