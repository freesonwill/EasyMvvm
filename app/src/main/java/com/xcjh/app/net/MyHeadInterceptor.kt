package com.xcjh.app.net

import com.hjq.language.MultiLanguages
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.getUUID
import com.xcjh.app.utils.CacheUtil
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*

/**
 * 自定义头部参数拦截器，传入heads
 */
class MyHeadInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.addHeader("device", "Android")
        builder.addHeader("appId", Constants.APP_ID)
        if (CacheUtil.isLogin()){
            //登录用户
            builder.addHeader("sportstoken", CacheUtil.getToken())
        }else{
            //游客
            builder.addHeader("tourist", getUUID().toString())
            //builder.addHeader("tourist", "aaaabbbbbccccdddeee")
        }
        when (MultiLanguages.getAppLanguage().language){
            "zh"->{
                builder.addHeader("content-language", "zh_CN ")
            }
            "en"->{
                builder.addHeader("content-language", "en_US")
            }
            else ->{
                builder.addHeader("content-language", "en_US")
            }
        }
        return chain.proceed(builder.build())
    }

}