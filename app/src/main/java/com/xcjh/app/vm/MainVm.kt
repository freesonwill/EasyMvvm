package com.xcjh.app.vm

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.appViewModel
import com.xcjh.app.bean.AppUpdateBean
import com.xcjh.app.bean.ListReq
import com.xcjh.app.bean.NewsBean
import com.xcjh.app.bean.UserInfo
import com.xcjh.app.event.AppViewModel
import com.xcjh.app.net.apiService
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.callback.livedata.BooleanLiveData
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request


class MainVm : BaseViewModel() {


    var update= UnPeekLiveData<AppUpdateBean>()
    var newsBeanValue=UnPeekLiveData<NewsBean>()
    /**
     * 反馈
     */
    fun feedback(content: String) {
       /* request(
            { apiService.feedback(FeedbackDTO(content, Constants.APP_ID)) },
            {
                myToast(appContext.getString(R.string.feedback_ok))
                feedbackOk.value = true
            }, {
                //请求失败
                myToast(it.errorMsg)
            }, true
        )*/
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo() {
        request(
            { apiService.getUserBaseInfo() },
            {

                CacheUtil.setUser(it)
                appViewModel.userInfo.value=it
            }, {

            }
        )
    }
    /**
     * 获取app是否更新
     */
    fun appUpdate() {
        request(
            { apiService.getLatestVersion() },
            {
                update.value=it

            }, {

            }
        )
    }

    fun  getNewsInfo(id :String){
        request(
            { apiService.getNewsInfo(id) },
            {
                newsBeanValue.value=it
            }, {
                //请求失败
                //请求失败
                myToast(it.errorMsg)
            }
        )
    }
}