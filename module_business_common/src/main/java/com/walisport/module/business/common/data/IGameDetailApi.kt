package com.walisport.module.business.common.data

import arch.cayenne.lib.http._interface.IApi
import arch.cayenne.lib.http.data.HttpApiResponse
import com.walisport.module.gamedetail.data.model.GameDetailVo
import com.walisport.module.gamedetail.data.model.ProfileCollectEditVo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 *
 * @date: 2025/12/11 11:24
 * @description:
 */
interface IGameDetailApi : IApi {
    // 获取游戏详情
    @GET("api/game/{id}")
    suspend fun getGameDetail(
        @Path("id") id: Long
    ): Response<HttpApiResponse<GameDetailVo>>

    // 更新游戏收藏状态
    @POST("api/game/collect")
    suspend fun updateGameCollect(
        @Body body: ProfileCollectEditVo
    ): Response<HttpApiResponse<String>>

}