package com.walisport.module.business.common.data.api

import arch.cayenne.lib.http._interface.IApi
import arch.cayenne.lib.http.data.HttpApiResponse
import com.walisport.module.business.common.data.BannerActiveBean
import retrofit2.Response
import retrofit2.http.GET

/**
 * @date: 2026/1/8 17:00
 * @description:
 */
interface IHomeCommonApi:IApi {

    @GET("api/banner/active")
    suspend fun getBannerActive(): Response<HttpApiResponse<List<BannerActiveBean>>>
}