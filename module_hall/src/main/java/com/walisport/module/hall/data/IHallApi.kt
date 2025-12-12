package com.walisport.module.hall.data

import arch.cayenne.lib.http._interface.IApi
import arch.cayenne.lib.http.data.HttpApiResponse
import com.walisport.module.hall.data.constants.GameSortType
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 * @date: 2025/12/11 11:24
 * @description:
 */
interface IHallApi : IApi {

    @GET("/game")
    suspend fun queryGameList(
        @Query("page") page: Int ,//页码
        @Query("pageSize") pageSize: Int ,//页大小
        @Query("supplier") supplier: Int = 0 ,//供应商
        @Query("sort") sort: String = GameSortType.HOT.desc ,//排序方式（0: 热门, 1: 最新上线, 2: 火热返奖, 3: 冰冷返奖）
        @Query("platform") platform: String = "0" ,//游戏平台（0：热门，1：原创，x：其他平台）
    ): Response<HttpApiResponse<GamePageVo>>
}