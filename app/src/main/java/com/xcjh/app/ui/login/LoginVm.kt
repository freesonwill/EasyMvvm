package com.xcjh.app.ui.login

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.CountryListBean
import com.xcjh.app.bean.LoginInfo
import com.xcjh.app.bean.PostGetMsgBean
import com.xcjh.app.bean.PostLoaginBean
import com.xcjh.app.net.apiService
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request


class LoginVm : BaseViewModel() {

    var logain = UnPeekLiveData<String>()
    var codeData = UnPeekLiveData<String>()
    var countrys = UnPeekLiveData<ArrayList<CountryListBean>>()

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
}