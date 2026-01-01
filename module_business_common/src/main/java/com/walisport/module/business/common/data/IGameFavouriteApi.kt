package com.walisport.module.business.common.data

import arch.cayenne.lib.http._interface.IApi
import arch.cayenne.lib.http.data.HttpApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 *
 * @date: 2025/12/11 11:24
 * @description:
 */
interface IGameFavouriteApi : IApi {

    @GET("api/game/collect")
    suspend fun getGameCollectList(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<HttpApiResponse<ProfilePlayedPageVo>>


    // 更新游戏收藏状态
    //important: 和拉取收藏列表是同一个url, 但是请求方式是POST, f**k
    @POST("api/game/collect")
    suspend fun updateGameCollect(
        @Body body: ProfileCollectEditVo
    ): Response<HttpApiResponse<String>>
}