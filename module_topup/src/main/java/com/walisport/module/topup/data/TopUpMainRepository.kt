package com.walisport.module.topup.data

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import com.walisport.module.topup.TopUpRemoteManager
import com.walisport.module.topup.data.entity.RechargeRecordBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class TopUpMainRepository(
    private val remoteManager: TopUpRemoteManager
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun getListData(page: Int): ApiResponseState = withContext(scope.coroutineContext) {
        delay(1000)
        val dataList = mutableListOf<RechargeRecordBean>()
        dataList.add(RechargeRecordBean("wL20220611", 1, 1, 1758706619000L, "","010 2930 2039 1220","9,100.00"))
        dataList.add(RechargeRecordBean("wL20220612", 2, 2, 1758706619000L, "流水不足","010 2930 2039 1220","9,100.00"))
        dataList.add(RechargeRecordBean("wL20220613", 2, 3, 1758706619000L, "","010 2930 2039 1220","9,100.00"))
        dataList.add(RechargeRecordBean("wL20220614", 3, 1, 1758706619000L, "","010 2930 2039 1220","9,100.00"))
        return@withContext ApiResponseState.Succeeded(dataList)
    }
}

