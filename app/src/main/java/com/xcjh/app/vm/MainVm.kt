package com.xcjh.app.vm

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.appViewModel
import com.xcjh.app.bean.*
import com.xcjh.app.event.AppViewModel
import com.xcjh.app.net.apiService
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.callback.livedata.BooleanLiveData
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainVm : BaseViewModel() {

    private var dataLoaded: Boolean = false

    //首页是否获取到个人信息，如果没有获取到就要在获取到首页数据的时候在获取一次
    public var isGetUserDate:Boolean=false

    //首页是否绑定了极光账号，如果没有获取到就要在获取到首页数据的时候在获绑定一次
    public var isPushDate:Boolean=false

    fun mockDataLoading(): Boolean {
        viewModelScope.launch {
            delay(2500)
            dataLoaded = true
        }
        return dataLoaded
    }
    var update = UnPeekLiveData<AppUpdateBean>()

    //新闻详情
    var newsBeanValue = UnPeekLiveData<NewsBean>()

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
                Constants.ISSTOP_TALK = it.ynForbidden!!
                appViewModel.userInfo.value = it
                isGetUserDate=true
            }, {

            }
        )
    }

    /**
     * 极光推送绑定用户
     */
    fun jPushBind(id: String) {
        request(
            { apiService.jPushBind(id) },
            {
             isPushDate=true
            },
        )
    }

    /**
     * 获取app是否更新
     */
    fun appUpdate() {
        request(
            { apiService.getLatestVersion() },
            {
                update.value = it

            }, {

            }
        )
    }

    /**
     * 新闻详情
     */
    fun getNewsInfo(id: String) {
        request(
            { apiService.getNewsInfo(id) },
            {
                newsBeanValue.value = it
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
    fun getActivityInfo(id: String) {
        request(
            { apiService.getActivityInfo(id) },
            {
                events.value = it
            }, {
                //请求失败
                events.value = EventsBean()

            }, isShowDialog = true
        )
    }


    /**
     * 设置语言
     */
    fun setLanquage() {
        var lanquageRed :String="en_US"
        //语言 0是中文  1是繁体  2是英文
        if( Constants.languageType==0){
            lanquageRed="zh_CN"
        }else  if( Constants.languageType==1){
            lanquageRed="zh_TW"
        }else{
            lanquageRed="en_US"
        }
        request(
            { apiService.setLanguage(lanquageRed) },

            {

            }, {

            }, false
        )
    }

}