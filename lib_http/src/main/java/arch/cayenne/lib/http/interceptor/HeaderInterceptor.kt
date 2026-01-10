package arch.cayenne.lib.http.interceptor


import arch.cayenne.lib.base.BuildConfig
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.http.HttpClient
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 內容先寫死，等到需要時再額外處理，例如token等等的
 * Uid -> 用户UID 目前开发固定用户 100
 * Token -> 用户认证令牌 目前开发固定TOKEN MTAwXzE3NjU0Mzc1NTk1MDk6ZFBoc3dpelQwazRTaUJnbg
 * Lang -> 语言代码 中文/英文 对应 zh-CN/en-US
 * Ccy -> 货币代码 CNY
 * Uuid -> 设备唯一标识 1B3B3ED86CB54E20905AE441BD694A33
 * Base -> 热更版本号参数 0.0.1
 * Version -> 客户端版本号 0.0.1
 * Channelshell -> 渠道标识 ios/android/web appstore-test-5000/android-test-5000/web-test-5000
 * */
class HeaderInterceptor(
    val manager: UserDataManager,
    private val versionCodes: Int
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 構建新的 Request 並加入 Header
        val newRequest = originalRequest.newBuilder()
            .addHeader("Uid", "100")
            .addHeader("Token", "MTAwXzE3NjU0Mzc1NTk1MDk6ZFBoc3dpelQwazRTaUJnbg")
            .addHeader("Lang", "zh-CN")
            .addHeader("Ccy", manager.getValue(UserDataKey.KEY_DEFAULT_FIAT, "USD"))
            .addHeader("Uuid", "1B3B3ED86CB54E20905AE441BD694A33")
            .addHeader("Base", "0.0.1")
            .addHeader("Version", "0.0.1")
            .addHeader("Channelshell", "android-${BuildConfig.BUILD_TYPE}-${versionCodes}")
            .build()

        return chain.proceed(newRequest)
    }

    private fun getSystemLanguage(): String {
        return java.util.Locale.getDefault().toLanguageTag()
    }
}