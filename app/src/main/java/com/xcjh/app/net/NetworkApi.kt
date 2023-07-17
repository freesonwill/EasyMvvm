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
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 *
 */


//双重校验锁式-单例 封装NetApiService 方便直接快速调用简单的接口
val apiService: ApiComService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    NetworkApi.INSTANCE.getApi(ApiComService::class.java, ApiComService.SERVER_URL)
}

class NetworkApi : BaseNetworkApi() {

    companion object {
        val INSTANCE: NetworkApi by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkApi()
        }
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
                    .request( "Request===") // request的Tag
                    .response( "Response===") // Response的Tag
                    //.addHeader("log-header", "I am the log request header.") // 添加打印头, 注意 key 和 value 都不能是中文
                    .build())
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



