package arch.cayenne.lib.http._interface

import arch.cayenne.lib.http.data.CurrencyInfo
import arch.cayenne.lib.http.data.HttpApiResponse
import retrofit2.Response
import retrofit2.http.GET

interface IConfig : IApi {
    @GET ("api/config/currency")
    suspend fun currency() : Response<HttpApiResponse<List<CurrencyInfo>>>

}