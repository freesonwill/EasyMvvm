package arch.cayenne.module.account.data.repo

import arch.cayenne.lib.http._interface.IApi
import arch.cayenne.lib.http.data.HttpApiResponse
import arch.cayenne.module.account.data.model.LoginResponseVo
import arch.cayenne.module.account.data.model.LoginVo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 *
 * @date: 2026/1/9 11:26
 * @description:
 */
interface IAccountLoginApi : IApi {
    //用户登录
    @POST("api/account/login")
    suspend fun accountLogin(@Body loginVo: LoginVo): Response<HttpApiResponse<LoginResponseVo>>
}