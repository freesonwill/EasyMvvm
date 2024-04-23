package com.cn.game.sdk.net


import com.xcjh.base_lib.bean.ApiResponse
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 *
 */
interface ApiComService {

    companion object {
        //dev  http://192.168.101.180:1820/#/home
//        const val SERVER_URL = "http://192.168.101.15:6003/apis/"//app通用 开发
//        const val SHARE_IP = "http://192.168.101.180:1820/"//比赛分享链接
//        const val WEB_SOCKET_URL = "ws://192.168.101.15:6006/ws-sports-chat" ///new dev
        //test 预发布
         var SERVER_URL = "https://app.cbd246.com/apis/"//app通用 测试
        var SHARE_IP = "https://app.cbd246.com/"//比赛分享链接
        var WEB_SOCKET_URL = "ws://35.220.148.132:7641" ///test



        var SERVER_URL_EMAIL = "https://www.2web3.net/user-user/"//邮箱验证
    }



}