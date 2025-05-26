package arch.cayenne.lib.http.interceptor

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author: zhangsan
 * @date: 2025/5/21 11:12
 * @description: 动态baseUrl,根据 Header 中的 baseUrl 切换
 */
class DynamicBaseUrlInterceptor : Interceptor {
    companion object {
        const val baseUrl = "baseUrl"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val baseUrlHeader = originalRequest.header(baseUrl)

        if (baseUrlHeader.isNullOrBlank()) {
            return chain.proceed(originalRequest) // 没有 header，走默认
        }

        val oldUrl = originalRequest.url
        val newBaseUrl = baseUrlHeader.toHttpUrlOrNull() ?: return chain.proceed(originalRequest) // 解析失败，忽略

        // 重新构造 URL
        val newUrl = newBaseUrl.newBuilder()
            .encodedPath(oldUrl.encodedPath)
            .query(oldUrl.query)
            .build()

        val newRequest = originalRequest.newBuilder()
            .removeHeader(baseUrl)
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}
