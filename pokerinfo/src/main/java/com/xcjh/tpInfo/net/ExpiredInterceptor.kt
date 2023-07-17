package com.xcjh.tpInfo.net

import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.myToast
import com.xcjh.tpInfo.R
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 *
 * 描述：判断响应是否有效的处理
 * 继承后扩展各种无效响应处理：包括token过期、账号异地登录、时间戳过期、签名sign错误等<br></br>
 * @author zhouyou
 */
class ExpiredInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val responseBody = response.body()
        val source = responseBody!!.source()
        source.request(Long.MAX_VALUE)
        val buffer = source.buffer()
        var charset = StandardCharsets.UTF_8
        val contentType = responseBody.contentType()
        if (contentType != null) {
            charset = contentType.charset(StandardCharsets.UTF_8)
        }
        val bodyString = buffer.clone().readString(charset)
        val isText = isText(contentType)
        if (!isText) {
            return response
        }
        //判断响应是否过期（无效）
        if (isResponseExpired(response, bodyString)) {
            myToast(appContext.getString(R.string.login_expired))
           // startNewActivity<LoginActivity> {  }
        }
        return response
    }
    var lastClickTime = 0L
    /**
     * 登录过期
     * @param response
     * @param bodyString
     * @return
     */
    private fun isResponseExpired(response: Response?, bodyString: String?): Boolean {
        try {
            val jsonObject = bodyString?.let { JSONObject(it) }
            if (jsonObject?.getInt("code") == Constants.EXPIRED_CODE) {
                val currentTime = System.currentTimeMillis()
                return if (lastClickTime != 0L && (currentTime - lastClickTime < 1000)) {
                    false
                }else{
                    if (response?.request()?.url().toString().contains("center/apis")){
                        false
                    }else{
                        lastClickTime = currentTime
                        true
                    }
                }
            }
        } catch (_: Exception) { }
        return false
    }

    private fun isText(mediaType: MediaType?): Boolean {
        if (mediaType == null) {
            return false
        }
        if (mediaType.type() != null && mediaType.type() == "text") {
            return true
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype() == "json") {
                return true
            }
        }
        return false
    }
}