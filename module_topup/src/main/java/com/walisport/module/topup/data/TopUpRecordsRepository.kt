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
        val dataList = mutableListOf<RechargeRecordBean>()
        dataList.add(RechargeRecordBean("wL20220611", 1, 1, 1758706619000L, "","010 2930 2039 1220","9,100.00"))
        dataList.add(RechargeRecordBean("wL20220612", 2, 2, 1758706619000L, "流水不足","010 2930 2039 1220","9,100.00"))
        dataList.add(RechargeRecordBean("wL20220613", 3, 3, 1758706619000L, "","010 2930 2039 1220","9,100.00"))
        dataList.add(RechargeRecordBean("wL20220614", 4, 1, 1758706619000L, "","010 2930 2039 1220","9,100.00"))
        return@withContext ApiResponseState.Succeeded(dataList)
    }

    companion object {
        const val DEFAULT_LIST_SIZE = 10

    }

}

