package com.walisport.module.topup

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.utils.ext.VIPDataExt.getKoin
import arch.cayenne.lib.database.entity.CoinBean
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.http._interface.IApi
import com.walisport.module.topup.data.entity.CryptoAddressBean
import com.walisport.module.topup.data.entity.CryptoConfigBean
import com.walisport.module.topup.data.entity.CurrentOrderBean
import com.walisport.module.topup.data.entity.FiatConfigBean
import com.walisport.module.topup.data.entity.PaymentMethod
import com.walisport.module.topup.data.entity.PendingOrderBean
import org.koin.core.qualifier.named
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

class TopUpRemoteManager {

    //获取币种列表
    suspend fun getCurrencyTypeList(): List<CoinBean> {
        val client = getKoin().get<HttpClient>(named("wnlApi"))
        val api = client.create(TopUpApi::class.java)
        client.safeRequest(
            request = {
                api.getCurrencyTypeList(currencyType = "0")
            },
            onSuccess = {

            },
            onFailure = { code, msg, throwable ->
                "response------>$code,$msg,$throwable".loge("测试")
            }
        )
        val list = listOf(
            CoinBean(2, true, isSelect = false, name = "USDT", icon = "", unit = ""),
            CoinBean(3, true, isSelect = false, "BTC", "", ""),
            CoinBean(4, true, isSelect = false, "ETH", "", "")
        )
        return list
    }

    //获取处理中的充值订单
    suspend fun getPendingOrder(): PendingOrderBean {
        val client = getKoin().get<HttpClient>()
        val api = client.create(TopUpApi::class.java)
        client.safeRequest(
            request = {
                api.getPendingOrder()
            },
            onSuccess = {

            },
            onFailure = { code, msg, throwable ->
                "response------>$code,$msg,$throwable".loge("测试")
            }
        )
        return PendingOrderBean("ORD1764920336066", 500, "alipay", false, 1800)
    }

    //获取法币充值配置
    suspend fun getFiatConfig(): FiatConfigBean {
        val client = getKoin().get<HttpClient>()
        val api = client.create(TopUpApi::class.java)
        client.safeRequest(
            request = {
                api.getFiatConfig()
            },
            onSuccess = {

            },
            onFailure = { code, msg, throwable ->
                "response------>$code,$msg,$throwable".loge("测试")
            }
        )
        val money = listOf(100, 200, 300, 500, 1000, 2000, 3000)
        val list = listOf(
            PaymentMethod("alipay", "5%", money, 10, 50000),
            PaymentMethod("wechat", "5%", money, 10, 50000)
        )
        return FiatConfigBean(1, "人民币", "¥", list)
    }

    //获取加密货币充值配置
    suspend fun getCryptoConfig(): CryptoConfigBean {
        val client = getKoin().get<HttpClient>()
        val api = client.create(TopUpApi::class.java)
        client.safeRequest(
            request = {
                api.getCryptoConfig()
            },
            onSuccess = {

            },
            onFailure = { code, msg, throwable ->
                "response------>$code,$msg,$throwable".loge("测试")
            }
        )
        val list = listOf(
            CryptoAddressBean("Tron(TRC20)", "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"),
            CryptoAddressBean("Tron(TRC20)", "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa")
        )
        return CryptoConfigBean(2, "USDT", list)
    }

    suspend fun getCurrentOrder(): CurrentOrderBean {
        val client = getKoin().get<HttpClient>()
        val api = client.create(TopUpApi::class.java)
        client.safeRequest(
            request = {
                api.getCurrentOrder()
            },
            onSuccess = {

            },
            onFailure = { code, msg, throwable ->
                "response------>$code,$msg,$throwable".loge("测试")
            }
        )
        return CurrentOrderBean("ORD1764920336066", 200, "alipay")
    }

    suspend fun cancelOrder() {
        val client = getKoin().get<HttpClient>()
        val api = client.create(TopUpApi::class.java)
        client.safeRequest(
            request = {
                api.cancelOrder()
            },
            onSuccess = {

            },
            onFailure = { code, msg, throwable ->
                "response------>$code,$msg,$throwable".loge("测试")
            }
        )
    }

    suspend fun createOrder() {
        val client = getKoin().get<HttpClient>()
        val api = client.create(TopUpApi::class.java)
        client.safeRequest(
            request = {
                api.createOrder()
            },
            onSuccess = {

            },
            onFailure = { code, msg, throwable ->
                "response------>$code,$msg,$throwable".loge("测试")
            }
        )
    }

    interface TopUpApi : IApi {
        @GET("profile/recharge")
        suspend fun getCurrencyTypeList(
            @Query("currencyType") currencyType: String
        ): Response<HttpResponse>

        @GET("profile/recharge/pending")
        suspend fun getPendingOrder(): Response<HttpResponse>

        @GET("profile/recharge/fiat/")
        suspend fun getFiatConfig(): Response<HttpResponse>

        @GET("profile/recharge/crypto/")
        suspend fun getCryptoConfig(): Response<HttpResponse>

        @GET("pay/profile/recharge/check/")
        suspend fun getCurrentOrder(): Response<HttpResponse>

        @PUT("pay/profile/recharge/")
        suspend fun cancelOrder(): Response<HttpResponse>

        @POST("pay/profile/recharge/create")
        suspend fun createOrder(): Response<HttpResponse>
    }

    data class HttpResponse(
        val code: Int,
        val data: Map<String, String>,
        val message: String,
        val timestamp: Long
    )
}