package arch.cayenne.lib.common.web

import android.webkit.JavascriptInterface
import androidx.navigation.findNavController
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.helper.showToast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.java.KoinJavaComponent.inject

/**
 *
 * @date: 2025/10/14 16:02
 * @description:
 */
class WLSJsInterface(val webView: WLSWebView) {

    private val manager: UserDataManager by inject(UserDataManager::class.java)

    private val gson = Gson()

    @JavascriptInterface
    fun showToast(string: String) {
        this.webView.post {
            this.webView.showToast(string)
        }
    }


    @JavascriptInterface
    fun getUidToken(input: String , resolve: String , reject: String) {
        val uid = manager.getValue(UserDataKey.KEY_UID , -1)
        val token = manager.getValue(UserDataKey.KEY_TOKEN , "")

        val obj = mapOf("uid" to uid , "token" to token)
        val response = Gson().toJson(obj)
        webView.post {
            // 调用 resolve 回传成功数据
            webView.evaluateJavascript("$resolve('$response')" , null)
            // 如果有错误，可以调用 reject
            // webView.evaluateJavascript("$reject('Error message')", null)
        }
    }

    @JavascriptInterface
    fun back(input: String , resolve: String , reject: String) {
        this.webView.post {
            this.webView.findNavController().popBackStack()
        }
        webView.post {
            // 调用 resolve 回传成功数据
            webView.evaluateJavascript("$resolve('success')" , null)
            // 如果有错误，可以调用 reject
            // webView.evaluateJavascript("$reject('Error message')", null)
        }
    }

    @JavascriptInterface
    fun postMessage(input: String , resolve: String , reject: String) {
        "postMessage called with input: $input".loge("JsInterface")
        webView.post {
            // 调用 resolve 回传成功数据
            webView.evaluateJavascript("$resolve('success')" , null)
            // 如果有错误，可以调用 reject
            // webView.evaluateJavascript("$reject('Error message')", null)
        }

    }

    @JavascriptInterface
    fun postMessage(input: String) {
//        "postMessage called with input: $input".loge("JsInterface")
        val mapType = object : TypeToken<Map<String , Any>>() {}.type
        val data: Map<String , Any> = gson.fromJson(input , mapType)
        when (data["type"]) {
            "back" -> {
                this.webView.findNavController().popBackStack()
            }

            "openPage" -> {
                val params: Map<String , Any> = data["params"] as? Map<String , Any> ?: return
                val page = params["pageName"] as? String ?: return
                // 这里可以根据 page 字段来决定打开哪个页面
                // 例如：
                when (page) {
                    "customer" -> {
                        // 打开客服页面
                        "open customer".loge("JsInterface")
                    }
                    // 添加更多页面处理逻辑
                }
            }
        }

    }


}