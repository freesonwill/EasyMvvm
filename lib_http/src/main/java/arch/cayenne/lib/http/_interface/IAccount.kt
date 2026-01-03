package arch.cayenne.lib.http._interface

import arch.cayenne.lib.http.data.AccountInfo
import arch.cayenne.lib.http.data.ApiNickname
import arch.cayenne.lib.http.data.ApiUpAvatar
import arch.cayenne.lib.http.data.AvatarUrl
import arch.cayenne.lib.http.data.HttpApiResponse
import arch.cayenne.lib.http.data.ProfileInfo
import arch.cayenne.lib.http.data.RecommendNicknameVo
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @Multipart
    @POST("api/upload/3n1/uploadImg")
    suspend fun upAvatar(
        @Part files: MultipartBody.Part,
        @Part("uid") uid: okhttp3.RequestBody,
        @Part("token") token: okhttp3.RequestBody
    ): Response<HttpApiResponse<AvatarUrl>>

    //更新用户头像account/avatar
    @POST("api/account/avatar")
    suspend fun updateAvatar(@Body avatarUrl: ApiUpAvatar) : Response<HttpApiResponse<Any>>
}