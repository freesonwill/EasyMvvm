package arch.cayenne.lib.http

import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import arch.cayenne.lib.http.interceptor.DynamicBaseUrlInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import arch.cayenne.lib.http._interface.IApi
import arch.cayenne.lib.http.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Response
import java.io.IOException
import java.net.SocketException
import java.net.UnknownHostException

/**
 * @author: zhangsan
 * @date: 2025/5/14 11:32
 * @description: http客户端
 *
 * @example
 *  step1: HttpClient单例注入koin（HttpModuleInitializer）
 *         loadKoinModules(module {
 *              single {
 *               HttpClient.Builder("http://baidu.com").build()
 *           }
 *         })
 *
 *  step2: 定义api
 *      interface WssServerApi : IApi {
 *         @GET("calendar/vacations")
 *         suspend fun getVacations(
 *             @Query("token") token: String,
 *             @Query("type") type: Int,
 *             @Query("timestamp") timestamp: String,
 *             @Query("client") client: String
 *         ): Response<HolidayResponse>
 *
 *         //修改base url
 *         @Headers("baseUrl:https://httpbin.org/")
 *         @POST("post")
 *         suspend fun postTest(@Body params: Map<String, String>):Response<HttpBinResponse>
 *     }
 *
 *  step3: 发起请求
 *      launch {
 *                val HttpClient = getKoin().get<HttpClient>()
 *                httpClient.safeRequest(
 *                     request = {
 *                         api.postTest(mapOf(
 *                             "name" to "ChatGPT",
 *                             "message" to "Hello World"
 *                         ))
 *                     },
 *                     onSuccess = {
 *                         "response------>$it".logd(TAG)
 *                         showToast(it.toString())
 *                     },
 *                     onFailure = { code, msg, throwable ->
 *                         "response------>$code,$msg,$throwable".loge(TAG)
 *                         showToast(msg)
 *                     }
 *                 )
 *            }
 */
class HttpClient private constructor(private val retrofit: Retrofit) {

    /**
     * 创建api请求
     * @param T
     * @param service
     * @return
     */
    fun <T : IApi> create(service: Class<T>): T {
        return retrofit.create(service)
    }

    /**
     * 请求
     * @param T
     * @param block
     * @return
     */
    // 网络请求封装函数，捕获OkHttp异常，切换线程
    private suspend fun <T> safeRequest(request: suspend () -> Response<T>) = flow {
        emit(Result.Start) // 必须在 flow 的协程上下文中调用 emit
        try {
            // IO 线程执行网络请求
            val response = request()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    emit(Result.Success(body))
                } else {
                    emit(Result.Failure(response.code(), "响应体为空", null))
                }
            } else {
                emit(Result.Failure(response.code(), response.message(), null))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val failure = when (e) {
                is SocketException,
                is UnknownHostException,
                is IOException ->
                    Result.Failure(-1, "网络异常: ${e.localizedMessage}", e)

                else ->
                    Result.Failure(-1, "未知异常: ${e.localizedMessage}", e)
            }
            emit(failure)
        }
    }.flowOn(Dispatchers.IO)


    suspend fun <T> safeRequest(
        @WorkerThread request: suspend () -> Response<T>,
        @UiThread onStart: () -> Unit = {},
        @UiThread onProgress: (progress: Int, total: Int) -> Unit = { _, _ -> },
        @UiThread onSuccess: (T) -> Unit = { },
        @UiThread onFailure: (code: Int, message: String?, throwable: Throwable?) -> Unit = { _, _, _ -> },
    ) {
        safeRequest(request).collect {
            when (it) {
                is Result.Start -> onStart()
                is Result.Progress -> onProgress(it.progress, it.total)
                is Result.Success -> onSuccess(it.data)
                is Result.Failure -> onFailure(it.code, it.message, it.throwable)
            }
        }
    }

    class Builder(private val baseUrl: String, private val timeout: Long = 15L) {
        private var interceptors = mutableListOf<Interceptor>()
        private var callAdapterFactory: CallAdapter.Factory? = null
        private var enableLog: Boolean = false
        private var logLevel: Level = Level.BODY

        /**
         * 添加拦截：注：不需要添加日志拦截打印。
         * @param interceptor Interceptor
         */
        fun addInterceptor(interceptor: Interceptor) = apply {
            interceptors.add(interceptor)
        }

        /**
         * 没有配置就用默认的RxJava2CallAdapterFactory
         * @param factory Factory
         */
        fun addCallAdapterFactory(factory: CallAdapter.Factory) = apply {
            callAdapterFactory = factory
        }

        /**
         * 开启日志
         *
         * @param enable
         * @param level
         */
        fun enableLog(enable: Boolean, level: Level = Level.BODY) = apply {
            this.enableLog = enable
            this.logLevel = level
        }

        fun build(): HttpClient {
            val okHttpBuilder = OkHttpClient.Builder().apply {
                connectTimeout(timeout, TimeUnit.SECONDS)
                readTimeout(timeout, TimeUnit.SECONDS)
                writeTimeout(timeout, TimeUnit.SECONDS)
                interceptors.forEach { addInterceptor(it) }

                // 添加 HTTPS 支持
                val sslParams = arch.cayenne.lib.http.utils.HttpsUtils.getSslSocketFactory()
                sslSocketFactory(sslParams.sslSocketFactory, sslParams.trustManager)
                addInterceptor(DynamicBaseUrlInterceptor())
            }
            if (enableLog) {
                okHttpBuilder.addInterceptor(HttpLoggingInterceptor().apply {
                    level = logLevel
                })
            }
            val builder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())

            callAdapterFactory?.let {
                builder.addCallAdapterFactory(it)
            }

            val retrofit = builder.build()
            return HttpClient(retrofit)
        }
    }
}