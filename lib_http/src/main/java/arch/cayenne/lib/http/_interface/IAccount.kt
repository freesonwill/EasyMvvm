package arch.cayenne.lib.http._interface

import arch.cayenne.lib.http.data.AccountInfo
import arch.cayenne.lib.http.data.HttpApiResponse
import arch.cayenne.lib.http.data.ProfileInfo
import retrofit2.Response
import retrofit2.http.GET

interface IAccount: IApi {
    @GET("api/account/info")
    suspend fun profileInfo() : Response<HttpApiResponse<AccountInfo>>
}