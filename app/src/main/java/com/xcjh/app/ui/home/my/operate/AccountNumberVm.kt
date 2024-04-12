package com.xcjh.app.ui.home.my.operate

import android.content.Context
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.R
import com.xcjh.app.bean.BindSend
import com.xcjh.app.bean.CaptchaCheckIt
import com.xcjh.app.bean.CaptchaVOReq
import com.xcjh.app.bean.Input
import com.xcjh.app.bean.LoginSend
import com.xcjh.app.bean.WordCaptchaGetIt
import com.xcjh.app.net.apiService
import com.xcjh.app.view.slider.CaptchaCheckOt
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.utils.getUUID
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request
import com.xcjh.base_lib.utils.requestNoCheck

class AccountNumberVm : BaseViewModel() {
    //获取到文字
    var codeText= UnPeekLiveData<Input<WordCaptchaGetIt>>()

    //验证成功文字
    var codeVerify= UnPeekLiveData<Input<CaptchaCheckIt>>()

    //验证了以后执行调用短信或者邮箱
    var sendingMessage=UnPeekLiveData<String>()


    //发送短信成功
    var sendingMessageSuccess = UnPeekLiveData<Boolean>()

    //发送邮箱成功
    var sendingEmailSuccess = UnPeekLiveData<Boolean>()
    //绑定成功
    var bindSucceed= UnPeekLiveData<Boolean>()
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
     * 验证文字的偏移
     */
    fun setText(captchaCheckOt: CaptchaCheckOt){
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




    /**
     * 绑定邮箱
     */
    fun bindEmail(email: String,code:String,context:Context) {
        var send= BindSend()
        send.email=email
        send.code=code
        request(
            { apiService.bindEmail(send) },

            {
                myToast(context.getString(R.string.binding_success))
                bindSucceed.value=true
            }, {
                //请求失败
                myToast(it.errorMsg)
            }, true
        )
    }



    /**
     * 绑定手机号
     */
    fun bindPhone(tel: String,code:String,areaCode:String,context:Context) {
        var send= BindSend()
        send.tel=tel
        send.areaCode=areaCode
        send.code=code
        request(
            { apiService.bindPhone(send) },

            {
                myToast(context.getString(R.string.binding_success))
                bindSucceed.value=true
            }, {
                //请求失败
                myToast(it.errorMsg)
            }, true
        )
    }

}