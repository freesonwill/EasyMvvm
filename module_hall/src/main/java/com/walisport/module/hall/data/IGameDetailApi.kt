package com.walisport.module.hall.data

import arch.cayenne.lib.http._interface.IApi
import arch.cayenne.lib.http.data.HttpApiResponse
import com.walisport.module.hall.data.constants.GameSortType
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 *
 * @date: 2025/12/11 11:24
 * @description:
 */
interface IGameDetailApi : IApi {

//    @GET("/game/{id}")
//        suspend fun getGameDetail(
//            @retrofit2.http.Path("id") id: Int
//        ): Response<HttpApiResponse<Game>>
}