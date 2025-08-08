package com.walisport.module.topup.data

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoneyForScale
import arch.cayenne.lib.database.entity.RechargeRecordBean
import com.walisport.module.topup.TopUpRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlin.random.Random

class TopUpRecordsRepository(
    private val remoteManager: TopUpRemoteManager
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    private val collectMatchChange by lazy { MutableStateFlow<List<RechargeRecordBean>>(listOf()) }  //CollectMatchCrossRef

    fun clearCurrentMatch() {
        collectMatchChange.value = emptyList<RechargeRecordBean>()
    }

    suspend fun getCollectData(page: Int): ApiResponseState = withContext(scope.coroutineContext) {
        delay(1000)
        val last = collectMatchChange.value.maxByOrNull { it.id }?.id

        val dataList = mutableListOf<RechargeRecordBean>()
        //每页10条测试数据
        for (i in 0 until DEFAULT_LIST_SIZE) {
            dataList.add(RechargeRecordBean((page - 1) * 10 + i, Random.nextLong(0, 10000).getMoneyForScale()))
        }

        collectMatchChange.value += dataList
        return@withContext ApiResponseState.Succeeded(dataList)

    }

    companion object {
        const val DEFAULT_LIST_SIZE = 10

    }

}

