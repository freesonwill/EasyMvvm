package arch.cayenne.lib.http._interface

import arch.cayenne.lib.http.data.HttpApiResponse
import arch.cayenne.lib.http.data.ProfileInfo
import retrofit2.Response
import retrofit2.http.GET

interface IAccount: IApi {
    @GET("profile/info")
    suspend fun profileInfo() : Response<HttpApiResponse<ProfileInfo>>
}