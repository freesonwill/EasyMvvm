package com.coolone.lib_base.http

import kotlinx.coroutines.CancellationException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 用来封装业务错误信息
 *
 * @author zs
 * @date 2020-05-09
 */
data class ApiException(val errorMessage: String, val errorCode: Int) : Throwable(){

    companion object {
        /**
         * 捕获异常信息
         */
        fun getApiException(e: Throwable): ApiException {
            return when (e) {
                is UnknownHostException -> {
                    ApiException("网络异常", -100)
                }
                is JSONException -> {//|| e is JsonParseException
                    ApiException("数据异常", -100)
                }
                is SocketTimeoutException -> {
                    ApiException("连接超时", -100)
                }
                is ConnectException -> {
                    ApiException("连接错误", -100)
                }
                is HttpException -> {
                    ApiException("http code ${e.code()}", -100)
                }
                is ApiException -> {
                    e
                }
                /**
                 * 如果协程还在运行，个别机型退出当前界面时，viewModel会通过抛出CancellationException，
                 * 强行结束协程，与java中InterruptException类似，所以不必理会,只需将toast隐藏即可
                 */
                is CancellationException -> {
                    ApiException("", -10)
                }
                else -> {
                    ApiException("未知错误", -100)
                }
            }
        }
    }

}