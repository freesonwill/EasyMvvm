package com.walisport.module.business.common.utils.biz

import arch.cayenne.lib.common.utils.biz.IBiz
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.http.data.HttpApiResponse
import arch.cayenne.lib.http.data.Result
import com.walisport.module.business.common.data.BannerActiveBean
import com.walisport.module.business.common.data.BannerListBean
import com.walisport.module.business.common.data.api.IBannerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

/**
 * @date: 2026/1/8 17:23
 * @description:
 */
object BannerBiz : IBiz, KoinComponent {
    private val httpClient by inject<HttpClient>(named("3n1_http"))

    suspend fun getBannerList():Result<HttpApiResponse<List<BannerListBean>>> = withContext(Dispatchers.IO) {
        val api = httpClient.create(IBannerApi::class.java)
        httpClient.safeRequest(request = { api.getBannerList() })
    }

    suspend fun getBannerActive():Result<HttpApiResponse<List<BannerActiveBean>>> = withContext(Dispatchers.IO) {
        val api = httpClient.create(IBannerApi::class.java)
        httpClient.safeRequest(request = { api.getBannerActive() })
    }

}