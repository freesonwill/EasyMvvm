package com.walisport.module.hall.data

import arch.cayenne.lib.http._interface.IApi
import arch.cayenne.lib.http.data.HttpApiResponse
import com.walisport.module.business.common.data.GamePageVo
import com.walisport.module.business.common.data.constants.GameSortType
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 *
 * @date: 2025/12/11 11:24
 * @description:
 */
interface IHallApi : IApi {

    @POST("/api/game")
    suspend fun queryGameList(
        @Query("page") page: Int,//页码
        @Query("pageSize") pageSize: Int,//页大小
        @Query("supplier") supplier: List<Int> = emptyList(),//供应商
        @Query("sort") sort: Int = GameSortType.HOT.type,//排序方式（0: 热门, 1: 最新上线, 2: 火热返奖, 3: 冰冷返奖）
        @Query("category") category: Int = 0,//游戏平台（100/0:全部, 101:最近, 102:热门，103:原创，1:捕鱼,2:真人/视讯,3:棋牌,4:老虎机/电子, 5:体育, 6:彩票, 7:电竞)
    ): Response<HttpApiResponse<GamePageVo>>

    //通用配置
    @GET("api/config/common")
    suspend fun queryGameCommon(): Response<HttpApiResponse<GameCommonVo>>
}