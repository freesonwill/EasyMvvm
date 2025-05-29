package arch.cayenne.lib.common.utils.ext

import android.net.Uri
import androidx.annotation.StringRes
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString

/**
 * @author: zhangsan
 * @date: 2025/4/23 16:55
 * @description: DeepLink 构建扩展工具，支持字符串与资源 ID 构造 URI。
 */
object DeeplinkExt {


    /**
     * 字符串转deeplink的uri
     * @param params 替换模板占位符，如 ("path" to "go")
     * @return uri 替换后的合法 URI
     * @example
     * input: "http://badiu.com?path={path}".deeplink("path" to "go")
     * output: "http://badiu.com?path=go"
     */
    fun String.deeplink(vararg params: Pair<String, Any>): Uri {
        val sb = StringBuffer(this.split("?")[0])
        params.forEachIndexed { index, (key, value) ->
            if (index == 0) sb.append("?") else sb.append("&")
            sb.append(key).append("=").append(value)
        }
        return Uri.parse(sb.toString())
    }

    /**
     * R.string中字符串转uri
     *
     * @param params
     * @return uri
     * @example
     * input: R.string.deeplink_baidu.deeplink("path" to "go")
     * output: "http://badiu.com?path=go"
     */
    fun @receiver:StringRes Int.deeplink(vararg params: Pair<String, Any>): Uri {
        return this.getString().deeplink(*params)
    }

    /**
     * 字符串转deeplink的uri
     * @param params
     * @return uri
     * @example
     * input: "http://badiu.com?path={path}".deeplink("path=go")
     * output: "http://badiu.com?path=go"
     */
    fun String.deeplink(vararg params: String): Uri {
        val sb = StringBuffer(this.split("?")[0])
        params.forEachIndexed { index, value ->
            if (index == 0) sb.append("?")
            if (index > 0) sb.append("&")
            value.split("&").forEachIndexed { index2, value2 ->
                if (index2 > 0) sb.append("&")
                /*val value3 = value2.split("=")
                sb.append(value3[0]).append("=").append(Uri.encode(value3[1]))*/
                sb.append(value2)
            }
        }
        return Uri.parse(sb.toString())
    }


    /**
     * R.string中字符串转uri
     * @param params
     * @return uri
     * @example
     * input: R.string.deeplink_baidu.deeplink("path=go&url=http://baidu.com","fruit=apple")
     * output: "http://badiu.com?path=go&url=http://baidu.com&&fruit=apple"
     */
    fun @receiver:StringRes Int.deeplink(vararg params: String): Uri {
        return this.getString().deeplink(*params)
    }

    /**
     * Int.deeplink(vararg params: String)和Int.deeplink(vararg params: Pair<String, String>)
     * 不传参时候会有二义性，补充一个
     * @return
     */
    fun @receiver:StringRes Int.deeplink(): Uri {
        return Uri.parse(this.getString())
    }
}