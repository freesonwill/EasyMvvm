package com.xcjh.app.ui.login

import android.util.Log
import com.google.gson.Gson
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.R
import com.xcjh.app.bean.CaptchaCheckIt
import com.xcjh.app.bean.CaptchaGetIt
import com.xcjh.app.bean.CaptchaVOReq
import com.xcjh.app.bean.CountryListBean
import com.xcjh.app.bean.Input
import com.xcjh.app.bean.LoginInfo
import com.xcjh.app.bean.LoginSend
import com.xcjh.app.bean.Point
import com.xcjh.app.bean.PostGetMsgBean
import com.xcjh.app.bean.PostLoaginBean
import com.xcjh.app.bean.WordCaptchaGetIt
import com.xcjh.app.net.apiService
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.view.slider.AESUtil
import com.xcjh.app.view.slider.CaptchaCheckOt
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.utils.getUUID
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request
import com.xcjh.base_lib.utils.requestNoCheck


class LoginVm : BaseViewModel() {

    var logain = UnPeekLiveData<String>()
    var codeData = UnPeekLiveData<String>()
    var countrys = UnPeekLiveData<ArrayList<CountryListBean>>()
    //获取到图片
    var codeImage= UnPeekLiveData<Input<CaptchaGetIt>>()
    //验证成功
    var codeVerify= UnPeekLiveData<Input<CaptchaCheckIt>>()
    //获取到文字
    var codeText= UnPeekLiveData<Input<WordCaptchaGetIt>>()
    //验证了以后执行调用短信或者邮箱
    var sendingMessage=UnPeekLiveData<String>()
    //发送短信成功
    var sendingMessageSuccess = UnPeekLiveData<Boolean>()

    //发送邮箱成功
    var sendingEmailSuccess = UnPeekLiveData<Boolean>()
    /**
     * 登录
     */
    fun getLogin(bean: PostLoaginBean) {
        request(
            { apiService.getLogin(bean) },

            {
                CacheUtil.setIsLogin(true, LoginInfo("", "", it))
                logain.value = it
            }, {
                //请求失败
                myToast(it.errorMsg)
            }, true
        )
    }

    /**
     * 极光推送绑定用户
     */
    fun jPUSHbIND(id: String) {
        request(
            { apiService.jPushBind(id) },

            {


            }, {
                //请求失败

            }, false
        )
    }

    /**
     * 获取国家
     */
    fun getCountrys() {

        request(
            {
                apiService.getCountrys()
            },

            {
                countrys.value = it
            }, {
                //请求失败
                countrys.value = arrayListOf()
            }, false
        )
    }


    /**
     第一个接口获取图片
     */
    fun getImage(type:String) {
        ////滑动拼图 blockPuzzle,文字点选 clickWord
        var code= CaptchaVOReq()
        code.captchaType=type
        code.clientUid= getUUID().toString()
        requestNoCheck(
            { apiService.getImageCode(code) },

            {
                codeImage.value=it
            }, {
                //请求失败
                myToast(it.errorMsg)
            }, false
        )
    }

    /**
     * 获取文字的图片
     */
    fun getText(type:String) {
        ////滑动拼图 blockPuzzle,文字点选 clickWord
        var code= CaptchaVOReq()
        code.captchaType=type
        code.clientUid= getUUID().toString()
        requestNoCheck(
            { apiService.getWordCaptchaAsync(code) },

            {
                codeText.value=it
            }, {
                //请求失败
                myToast(it.errorMsg)
            }, false
        )
    }


    /**
     *验证偏移
     */
    fun getCaptcha(sliderXMoved: Double,key:String,token:String) {
        val point = Point(sliderXMoved, 5.0)
        var pointStr = Gson().toJson(point).toString()
        val o = CaptchaCheckOt(
            captchaType = "blockPuzzle",
            pointJson = AESUtil.encode(pointStr, key),
            token = token
        )

        requestNoCheck(
            { apiService.getCaptcha(o) },

            {
//                codeVerify.value=it
            }, {
                //请求失败
                myToast(it.errorMsg)
            }, false
        )
    }


    /**
     * 验证文字的偏移
     */
    fun setText(captchaCheckOt:CaptchaCheckOt){
        requestNoCheck(
            { apiService.getCaptcha(captchaCheckOt) },

            {
                codeVerify.value=it
            }, {
                //请求失败
                myToast(it.errorMsg)
            }, false
        )
    }


    /**
     * 发送短信
     */
    fun getMessage(account: String,areaCode:String) {
        var send= LoginSend()
        send.account=account
        send.areaCode=areaCode
        request(
            { apiService.getMessage(send) },

            {
                sendingMessageSuccess.value=true
            }, {
                //请求失败
                myToast(it.errorMsg)
            }, true
        )
    }


    /**
     * 发送邮箱
     */
    fun getEmail(email: String) {
        var send= LoginSend()
        send.email=email
        request(
            { apiService.getMailbox(send) },

            {
                sendingEmailSuccess.value=true
            }, {
                //请求失败
                myToast(it.errorMsg)
            }, true
        )
    }
}