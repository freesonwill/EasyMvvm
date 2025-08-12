package com.walisport.module.topup.data

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoneyForScale
import com.walisport.module.topup.TopUpRemoteManager
import com.walisport.module.topup.data.entity.RechargeDetailBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

class TopUpDetailRepository(
    private val remoteManager: TopUpRemoteManager
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)


    suspend fun queryData(transactionId: String): ApiResponseState =
        withContext(scope.coroutineContext) {
            delay(1000)

            return@withContext ApiResponseState.Succeeded(
                RechargeDetailBean(
                    transactionId,
                    Random.nextLong(0, 10000).getMoneyForScale(),
                    "银行卡",
                    0,
                    System.currentTimeMillis()
                )
            )
        }


}

