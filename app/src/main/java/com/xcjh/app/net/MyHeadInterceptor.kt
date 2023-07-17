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
        //  builder.addHeader("token", "token123456").build()
        builder.addHeader("device", "Android").build()
        builder.addHeader("appId", Constants.APP_ID).build()
        if (CacheUtil.isLogin()){
            //登录用户
            builder.addHeader("token", CacheUtil.getToken()).build()
        }else{
            //游客
            builder.addHeader("tourist", getUUID().toString()).build()
            //builder.addHeader("tourist", "aaaabbbbbccccdddeee").build()
        }
        when (MultiLanguages.getAppLanguage().language){
            "zh"->{
                builder.addHeader("content-language", "zh_CN ").build()
            }
            "en"->{
                builder.addHeader("content-language", "en_US").build()
            }
            else ->{
                builder.addHeader("content-language", "en_US").build()
            }
        }
        return chain.proceed(builder.build())
    }

}