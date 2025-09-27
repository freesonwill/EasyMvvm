package com.walisport.module.topup.data

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoneyForScale
import com.walisport.module.topup.TopUpRemoteManager
import com.walisport.module.topup.data.entity.RechargeRecordBean
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

    val recordListChange by lazy { MutableStateFlow<List<RechargeRecordBean>>(listOf()) }  //CollectMatchCrossRef

    fun clearCurrentList() {
        recordListChange.value = emptyList<RechargeRecordBean>()
    }

    suspend fun getListData(page: Int): ApiResponseState = withContext(scope.coroutineContext) {
        delay(1000)
        //取最后一条的id做分页标记
        val last = recordListChange.value.maxByOrNull { it.iid }?.iid
        val dataList = mutableListOf<RechargeRecordBean>()
        //每页10条测试数据
        for (i in 0 until DEFAULT_LIST_SIZE) {
            dataList.add(RechargeRecordBean("1", 1, 1, 1758706619000L, "","010 2930 2039 120","999.00"))
        }
        recordListChange.value += dataList
        return@withContext ApiResponseState.Succeeded(dataList)

    }

    companion object {
        const val DEFAULT_LIST_SIZE = 10

    }

}

