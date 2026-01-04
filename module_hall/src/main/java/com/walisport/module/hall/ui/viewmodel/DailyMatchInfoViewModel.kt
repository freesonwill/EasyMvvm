package com.walisport.module.hall.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.dao.DailyBetMatchDataDao
import com.walisport.module.hall.data.DailyBetMatchVo
import com.walisport.module.hall.data.HallRepository.Companion.INITIAL_PAGE
import com.walisport.module.hall.data.RankingRepository
import com.walisport.module.hall.data.toDailyBetMatchData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class DailyMatchInfoViewModel(
    private val dailyBetMatchDataDao: DailyBetMatchDataDao,
) : BaseViewModel() {

    private val repository: RankingRepository by inject { parametersOf(viewModelScope) }

    val dailyBetMatchDataBeanFlow = dailyBetMatchDataDao.observeDailyBetMatchDataBean()

    /**
     * 每日投注比赛信息
     */
    fun getDayMatchDetail() {
        viewModelScope.launch {
            callApi(
                {
                    repository.getDayMatchDetail()
                },
                {
                    when (it) {
                        is ApiResponseState.Failed -> {
                        }

                        is ApiResponseState.Succeeded<*> -> {
                            val dailyBetMatchVo = it.dataAs<DailyBetMatchVo>()

                            dailyBetMatchVo?.toDailyBetMatchData().let { dailyBean ->
                                dailyBean?.let {
                                    viewModelScope.launch(Dispatchers.IO) {
                                        dailyBetMatchDataDao.insert(dailyBean)
                                    }
                                }
                            }
                        }

                        else -> {}
                    }
                }, autoUpdateState = false
            )
        }
    }



}