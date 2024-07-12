package com.xcjh.app.net

import android.util.Log
import com.google.gson.Gson
import com.xcjh.app.bean.AppHost
import com.xcjh.app.bean.HostBean
import com.xcjh.app.utils.getUUID
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.LogUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * @Author pang
 * @Date 2024年07月01日   时间：16:36
 * 域名切换类
 */
class ChangeHostUtil {
    /**
     * 获取域名地址
     */
    private val HOST_URL = "https://d3ty9kq80i16x4.cloudfront.net/cnddomain/domainList.txt";

    /**
     * 超时时间
     */
    private val TIME_OUT = 4L


    /**
     * 获取host数组
     */
    fun getHostList(requestCallback: (host: AppHost?) -> Unit) {
        val client = OkHttpClient().newBuilder().readTimeout(TIME_OUT, TimeUnit.SECONDS).build()
        val request = Request.Builder().url(HOST_URL).build()
        var response = client.newCall(request)
        response.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                getHostListError(requestCallback)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code() == 200) {
                    val hostString = response.body()?.string()
                    LogUtils.d("host切换 获取域名字符串", hostString)
                    if (hostString.isNullOrEmpty()) {
                        getHostListError(requestCallback)
                    } else {
                        try {
                            var gson = Gson()
                            val hostBean: HostBean? =
                                gson.fromJson(hostString, HostBean::class.java)
                            if (hostBean?.app != null && hostBean.app.isNotEmpty()) {
//                                val one = AppHost("thisdomaindoesnotexist12345.com", "")
//                                val two = AppHost("wthisdomaindoesnotexist123456.com", "")
//                                val three = AppHost("192.168.101.15:6003", "")
//                                hostBean.app.clear()
//                                hostBean.app.add(one)
//                                hostBean.app.add(two)
//                                hostBean.app.add(three)
                                LogUtils.d("host切换 成功返回的连接", hostBean.app.toString())

                                checkHostConnection(0, hostBean.app.toMutableList()) {
                                    if (it == null) {
                                        getHostList(requestCallback)
//                                            requestCallback.invoke(null)
                                    } else {
                                        requestCallback.invoke(it)
                                        LogUtils.d("host切换 返回的连接", it)
                                    }
                                }
                            } else {
                                getHostListError(requestCallback)
                            }

                        } catch (e: Exception) {
                            getHostListError(requestCallback)
                        }
                    }

                } else {
                    getHostListError(requestCallback)
                }
            }
        })

    }

    /**
     * 重新获取
     */
    fun getHostListError(requestCallback: (host: AppHost?) -> Unit) {
        GlobalScope.launch {
            delay(3000)
            getHostList(requestCallback)
        }
    }

    /**
     * 检查host的联通性
     */
//    private fun checkHostConnection(
//        index: Int, hosts: MutableList<AppHost>, callback: (enableHost: AppHost?) -> Unit
//    ) {
//        if (index >= hosts.size || hosts[index].domainUrl == null) {
//            callback.invoke(null)
//            return
//        }else if(!isValidHttpUrl(hosts[index].domainUrl!!)){
//            checkHostConnection(index + 1, hosts, callback)
//
//        }else{
//            val client = OkHttpClient().newBuilder().readTimeout(TIME_OUT, TimeUnit.SECONDS).build()
//            val request = Request.Builder().url( hosts[index].domainUrl!!).build()
//            var response = client.newCall(request)
//            response.enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    LogUtils.d("host切换 检查连接失败  ${e.message}", hosts[index])
//                    if (e is java.net.UnknownHostException) {
//                        checkHostConnection(index + 1, hosts, callback)
//                    } else {
//                        callback.invoke(null)
//                    }
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    callback.invoke(hosts[index])
//                    LogUtils.d("host切换 检查连接成功", hosts[index])
//                    response.close()
//                }
//            })
//        }
//
//
//    }



    /**
     * 检查host的联通性
     */
    private fun checkHostConnection(
        index: Int, hosts: MutableList<AppHost>, callback: (enableHost: AppHost?) -> Unit
    ) {
        if (index >= hosts.size || hosts[index].domainUrl == null) {
            callback.invoke(null)
            return
        }else if(!isValidHttpUrl(hosts[index].domainUrl!!)){
            checkHostConnection(index + 1, hosts, callback)

        }else{
            //测试接口，获取首页banner轮播图
//            var http=hosts[index].domainUrl!!+"/apis/app/advertising/banner/list"
            var http=hosts[index].domainUrl!!+"/apis/app/advertising/personal/center"
            val client = OkHttpClient().newBuilder().readTimeout(TIME_OUT, TimeUnit.SECONDS).build()
            var sports_language:String=""
            //语言 0是中文  1是繁体  2是英文
            if( Constants.languageType==0){
                sports_language="zh_CN"
            }else  if( Constants.languageType==1){
                sports_language="zh_TW"
            }else{
                sports_language="en_US"
            }
            val request = Request.Builder().url(http)
                .addHeader("device", "Android")
                .addHeader("appId", Constants.APP_ID)
                .addHeader("Content-Type", "application/json")
                .addHeader("tourist",  getUUID().toString())
                .addHeader("sports_language", sports_language)
                .build()
            var response = client.newCall(request)
            response.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    LogUtils.d("host切换 检查连接失败  ${e.message}", hosts[index])
                    if (e is java.net.UnknownHostException) {
                        checkHostConnection(index + 1, hosts, callback)
                    } else {
                        callback.invoke(null)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body()?.string()
                    if(response.isSuccessful&&response.code()==200){
                        callback.invoke(hosts[index])
                        LogUtils.d("host切换 检查连接成功", hosts[index])
                        response.close()
                    }else{
                        checkHostConnection(index + 1, hosts, callback)
                    }

                }
            })
        }


    }

    fun isValidHttpUrl(url: String): Boolean {
        // 检查是否以http://或https://开头，忽略大小写
        return url.startsWith("http://", ignoreCase = true) || url.startsWith("https://", ignoreCase = true)
    }


}