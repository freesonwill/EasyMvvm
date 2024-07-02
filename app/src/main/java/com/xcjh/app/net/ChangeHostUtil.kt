package com.xcjh.app.net

import com.google.gson.Gson
import com.xcjh.app.bean.HostBean
import com.xcjh.base_lib.utils.LogUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
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
    private val HOST_URL = "http://192.168.101.239:8080/tt/host.txt";

    /**
     * 超时时间
     */
    private val TIME_OUT = 2L

    /**
     * 获取host数组
     */


    fun getHostList(requestCallback: (host: String?) -> Unit) {
        val client = OkHttpClient().newBuilder().readTimeout(TIME_OUT, TimeUnit.SECONDS).build()
        val request = Request.Builder().url(HOST_URL).build()
        var response = client.newCall(request)
        response.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requestCallback.invoke(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code() == 200) {
                    val hostString = response.body()?.string()
                    LogUtils.d("host切换 获取域名字符串", hostString)
                    if (hostString.isNullOrEmpty()) {
                        requestCallback.invoke(null)
                    } else {
                        try {
                            var gson = Gson()
                            val hostBean: HostBean? =
                                gson.fromJson(hostString, HostBean::class.java)
                            var hosts = mutableListOf<String>()
                            if (hostBean?.app != null && hostBean.app.isNotEmpty()) {
                                hostBean.app.forEach { host ->
                                    host.apiDomainUrl?.let {
                                        hosts.add(it)
                                    }

                                }
                                if (hosts.size > 0) {
                                    checkHostConnection(0, hosts) {
                                        if (it == null) {
                                            requestCallback.invoke(null)
                                        } else {
                                            requestCallback.invoke(it)
                                            LogUtils.d("host切换 返回的连接", it)
                                        }
                                    }
                                } else {
                                    requestCallback.invoke(null)

                                }

                            } else {
                                requestCallback.invoke(null)

                            }

                        } catch (e: Exception) {
                            requestCallback.invoke(null)

                        }


                    }

                } else {
                    requestCallback.invoke(null)
                }
            }
        })

    }

    /**
     * 检查host的联通性
     */
    private fun checkHostConnection(
        index: Int, hosts: MutableList<String>, callback: (enableHost: String?) -> Unit
    ) {
        if (index >= hosts.size) {
            callback.invoke(null)
            return
        }
        val client = OkHttpClient().newBuilder().readTimeout(TIME_OUT, TimeUnit.SECONDS).build()
        val request = Request.Builder().url(hosts[index]).build()
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
                callback.invoke(hosts[index])
                LogUtils.d("host切换 检查连接成功", hosts[index])
                response.close()
            }
        })
    }

}