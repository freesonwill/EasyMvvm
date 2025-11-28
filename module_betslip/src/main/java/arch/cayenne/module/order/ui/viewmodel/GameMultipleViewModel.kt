package arch.cayenne.module.order.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.order.data.model.RecordsBean
import arch.cayenne.module.order.data.repo.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject

class GameMultipleViewModel : BaseViewModel() {

    private val gameRepository: GameRepository by inject()

    private val _recordData = MutableLiveData<List<RecordsBean>>()
    val recordData: LiveData<List<RecordsBean>> = _recordData

    fun getGameList() {
        val tmp1 = RecordsBean(
            "抢庄牛牛",
            "https://mock.ja700.com/mock/tn1/public/imgs/games/1.avif",
            "¥10.00",
            "¥5840.00",
            "WL",
            "2025/04/17 15:52:00"
        )
        val tmp2 = RecordsBean(
            "麻将胡了",
            "https://mock.ja700.com/mock/tn1/public/imgs/games/2.avif",
            "¥2500.00",
            "¥5040.00",
            "PG",
            "2025/04/17 15:52:00"
        )
        val tmp3 = RecordsBean(
            "百家乐",
            "https://mock.ja700.com/mock/tn1/public/imgs/games/3.avif",
            "¥2500.00",
            "¥6140.00",
            "WL",
            "2025/04/17 15:52:00"
        )
        _recordData.value = listOf(tmp1, tmp2, tmp3)
    }

    //获取最大倍数列表
    fun getMaxMultipleList() {
        viewModelScope.launch(Dispatchers.IO) {
            val bonus = gameRepository.getMaxMultipleList()
            withContext(Dispatchers.Main) {
                //matchWithMarketsChange.value = matchWithMarkets
            }
        }
    }
}