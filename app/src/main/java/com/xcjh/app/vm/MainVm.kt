package com.xcjh.app.vm

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.appViewModel
import com.xcjh.app.bean.*
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
    //新闻详情
    var newsBeanValue=UnPeekLiveData<NewsBean>()

    //活动中心详情
    var events = UnPeekLiveData<EventsBean>()
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

    /**
     * 新闻详情
     */
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

    /**
     * 活动中心详情
     */
    fun getActivityInfo(id:String){
        request(
            { apiService.getActivityInfo(id) },
            {
                events.value=it
            }, {
                //请求失败
                events.value= EventsBean()

            },isShowDialog=true
        )
    }

}