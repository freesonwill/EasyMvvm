package com.walisport.module.popup.slot.data

import arch.cayenne.lib.http._interface.IApi
import arch.cayenne.lib.http.data.HttpApiResponse
import retrofit2.Response
import retrofit2.http.GET


interface IPopupApi : IApi {
    @GET("api/popup/list")
    suspend fun getPopupList(): Response<HttpApiResponse<List<PopupVo>>>

}