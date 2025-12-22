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
interface IRankingApi : IApi {

    /**
     * 最新投注
     */
    @GET("api/record/betting")
    suspend fun recordBetting(
        @Query("page") page: Int ,//页码
        @Query("pageSize") pageSize: Int ,//页大小
    ): Response<HttpApiResponse<BettingPageVo>>

    /**
     * 大额赢家
     */
    @GET("api/record/big")
    suspend fun recordBig(
        @Query("page") page: Int ,//页码
        @Query("pageSize") pageSize: Int ,//页大小
    ): Response<HttpApiResponse<BigPageVo>>

    /**
     * 每日投注比赛信息
     */
    @GET("api/record/day/match/detail")
    suspend fun dayMatchDetail(
    ): Response<HttpApiResponse<DailyBetMatchVo>>


    /**
     * 每日比赛
     */
    @GET("api/record/day")
    suspend fun getDailyMatch(
        @Query("page") page: Int ,//页码
        @Query("pageSize") pageSize: Int ,//页大小
    ): Response<HttpApiResponse<DayPageVo>>

}