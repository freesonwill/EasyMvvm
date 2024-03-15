package com.xcjh.app.bean

import com.xcjh.base_lib.network.BaseResponse

data class ImageApiResponse <T>(val code: Int, val msg: String, val data: T,val ok: Boolean) : BaseResponse<T>() {
    // 这里是示例，wanandroid 网站返回的 错误码为 0 就代表请求成功，请你根据自己的业务需求来改变
    override fun isSucces() = code == 200
    //override fun isSucces() = ok

    override fun getResponseCode() = code

    override fun getResponseData() = data

    override fun getResponseMsg() = msg



    // override fun getResponseMsg() = massage

}