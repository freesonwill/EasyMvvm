package com.xcjh.tpInfo.net

import com.xcjh.base_lib.bean.ApiArticleRes
import com.xcjh.base_lib.bean.ApiResponse
import com.xcjh.tpInfo.bean.*
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.http.*

/**
 *
 */
interface ApiComService {

    companion object {
        //test
        const val SERVER_URL_CENTER = "http://192.168.101.3:9488/center/apis/"//中台测试
        const val SERVER_URL = "http://192.168.101.7:6001/apis/"//app通用 开发

        // const val SERVER_URL = "https://test.holdem.news/apis/"//app通用 测试
        //正式
        /* const val SERVER_URL_CENTER = "https://www.2web3.net/user-center/center/apis/"//中台正式
         const val SERVER_URL = "https://holdem.news/apis/"//app通用 正式*/
        const val SERVER_URL_EMAIL = "https://www.2web3.net/user-user/"//邮箱验证
        //const val SERVER_URL = "http://192.168.102.39:50000/chatgenius/"
    }

    /**
     *  =====================登录===========================
     * 1 ->id
     */
    @POST(SERVER_URL_CENTER + "user/user-login/login")
    suspend fun login(@Body req: LoginReq): ApiResponse<LoginInfo>

    //上传图片到oss
    @Multipart
    @POST("xcjh/admin/admin-base/upload-picture")
    suspend fun uploadOss(@Part file: MultipartBody.Part): ApiResponse<Any>

    //上传视频到oss
    @Multipart
    @POST("xcjh/admin/admin-base/upload-video")
    suspend fun uploadVideoOss(@Part file: MultipartBody.Part): ApiResponse<Any>



}