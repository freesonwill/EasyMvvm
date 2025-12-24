package com.walisport.module.gamedetail.data

import arch.cayenne.lib.http._interface.IApi
import arch.cayenne.lib.http.data.HttpApiResponse
import com.walisport.module.gamedetail.data.model.GameDetailVo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 *
 * @date: 2025/12/11 11:24
 * @description:
 */
interface IGameDetailApi : IApi {

    @GET("api/game/{id}")
    suspend fun getGameDetail(
        @Path("id") id: Long
    ): Response<HttpApiResponse<GameDetailVo>>
}