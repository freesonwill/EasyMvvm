package com.walisport.module.hall.data

import arch.cayenne.lib.http._interface.IApi
import arch.cayenne.lib.http.data.HttpApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 * @date: 2025/12/11 11:24
 * @description:
 */
interface IGameFavouriteApi : IApi {

    @GET("api/game/collect")
    suspend fun getGameCollectList(
        @Query("page") page: Int ,
        @Query("pageSize") pageSize: Int
    ): Response<HttpApiResponse<ProfilePlayedPageVo>>
}