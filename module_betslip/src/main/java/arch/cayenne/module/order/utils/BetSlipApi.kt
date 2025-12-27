package arch.cayenne.module.order.utils

import arch.cayenne.lib.http._interface.IApi
import arch.cayenne.lib.http.data.HttpApiResponse
import arch.cayenne.module.order.data.model.BetShareBean
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author: wenxi
 * @date: 26/12/25 20:41
 * @description:
 */
interface BetSlipApi : IApi {

    // 获取游戏详情
    @GET("api/record/share")
    suspend fun getBetShareResult(
       @Query("userId") userId: Long,
       @Query("settleId") settleId: String): Response<HttpApiResponse<BetShareBean>>
}