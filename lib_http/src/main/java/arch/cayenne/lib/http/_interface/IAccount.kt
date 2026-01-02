package arch.cayenne.lib.http._interface

import arch.cayenne.lib.http.data.AccountInfo
import arch.cayenne.lib.http.data.ApiNickname
import arch.cayenne.lib.http.data.HttpApiResponse
import arch.cayenne.lib.http.data.ProfileInfo
import arch.cayenne.lib.http.data.RecommendNicknameVo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface IAccount: IApi {
    //获取用户信息 get
    @GET("api/account/info")
    suspend fun profileInfo() : Response<HttpApiResponse<AccountInfo>>

    //推荐昵称 post
    @GET("api/account/nickname/recommend")
    suspend fun recommendNickname() : Response<HttpApiResponse<RecommendNicknameVo>>

    //更改昵称 post/account/nickname
    @POST("api/account/nickname")
    suspend fun changeNickname(@Body nickname: ApiNickname) : Response<HttpApiResponse<Any>>
}