package com.xcjh.app.net

import com.hjq.gson.factory.GsonFactory
import com.xcjh.base_lib.BuildConfig
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.network.BaseNetworkApi
import com.xcjh.base_lib.network.HttpsUtils
import com.xcjh.base_lib.network.cookie.CookieManger
import com.xcjh.base_lib.network.logging.Level
import com.xcjh.base_lib.network.logging.LoggingInterceptor
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 *
 */


//双重校验锁式-单例 封装NetApiService 方便直接快速调用简单的接口
val apiService: ApiComService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    NetworkApi.INSTANCE.getApi(ApiComService::class.java, ApiComService.SERVER_URL)
}
//val apiServiceNew: ApiComService = NetworkApi.INSTANCE.getApi(ApiComService::class.java, ApiComService.SERVER_URL)
//  lateinit var apiService: ApiComService
//private val apiServiceLock = Any()
//private var baseUrl: String = ApiComService.SERVER_URL
//fun changeBaseUrl(newBaseUrl: String) {
//    synchronized(apiServiceLock) {
//        baseUrl= newBaseUrl
//        // 在 baseUrl 发生变化时，不需要重新创建实例，因为 apiService 会在下次调用 getApiService 时重新创建
//    }
//}
//@JvmName("fetchApiService")
//fun getApiService(serverUrl: String): ApiComService {
//    synchronized(apiServiceLock) {
//        if (!::apiService.isInitialized ||  baseUrl != NetworkApi.baseUrl) {
//            // 如果 apiService 未初始化或者 baseUrl 发生变化，则重新创建实例
//            apiService = NetworkApi.INSTANCE.getApi(ApiComService::class.java, baseUrl)
//        }
//    }
//    return apiService
//}
class NetworkApi : BaseNetworkApi() {

    //新添加的修改url


    companion object {
        val INSTANCE: NetworkApi by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkApi()
        }

        var baseUrl: String = ApiComService.SERVER_URL
            private set


    }



    /**
     * 实现重写父类的setHttpClientBuilder方法，
     * 在这里可以添加拦截器，可以对 OkHttpClient.Builder 做任意操作
     */
    override fun setHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        val sslParams = HttpsUtils.getSslSocketFactory(null, null, null)
        builder.apply {
            //设置缓存配置 缓存最大10M
            cache(Cache(File(appContext.cacheDir, "tp_cache"), 10 * 1024 * 1024))
            //添加Cookies自动持久化
            cookieJar(CookieManger(appContext))
            //示例：添加公共heads 注意要设置在日志拦截器之前，不然Log中会不显示head信息

            addInterceptor(MyHeadInterceptor())
            addInterceptor(ExpiredInterceptor())

            //添加缓存拦截器 可传入缓存天数，不传默认7天
            // addInterceptor(CacheInterceptor())
            // addInterceptor(TokenOutInterceptor())
            // 日志拦截器
            addInterceptor(
                LoggingInterceptor.Builder() //构建者模式
                    .loggable(BuildConfig.DEBUG) //是否开启日志打印
                    .setLevel(Level.BASIC) //打印的等级
                    .log(Platform.INFO) // 打印类型
                    .request("Request===") // request的Tag
                    .response("Response===") // Response的Tag
                    //.addHeader("log-header", "I am the log request header.") // 添加打印头, 注意 key 和 value 都不能是中文
                    .build()
            )
            //超时时间 连接、读、写
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
        }
        return builder
    }

    /**
     * 实现重写父类的setRetrofitBuilder方法，
     * 在这里可以对Retrofit.Builder做任意操作，比如添加GSON解析器，protobuf等
     */
    override fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder {
        return builder.apply {
            // addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            addConverterFactory(GsonConverterFactory.create(GsonFactory.getSingletonGson()))
        }
    }

    /* val cookieJar: PersistentCookieJar by lazy {
         PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(appContext))
     }*/

}



