package com.walisport.module.business.common.data.api

import arch.cayenne.lib.http._interface.IApi
import arch.cayenne.lib.http.data.HttpApiResponse
import com.walisport.module.business.common.data.BannerActiveBean
import com.walisport.module.business.common.data.BannerListBean
import retrofit2.Response
import retrofit2.http.GET

/**
 * @date: 2026/1/8 17:00
 * @description:
 */
interface IBannerApi:IApi {

    @GET("api/banner/list")
    suspend fun getBannerList(): Response<HttpApiResponse<List<BannerListBean>>>

    @GET("api/banner/active")
    suspend fun getBannerActive(): Response<HttpApiResponse<List<BannerActiveBean>>>
}