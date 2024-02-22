package com.xcjh.app.ui.home.home.tab

import androidx.lifecycle.lifecycleScope
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.bean.LiveReq
import com.xcjh.app.bean.NewsBean
import com.xcjh.app.bean.NewsReq
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainNewsListVm : BaseViewModel() {
    //进行中的比赛
    var liveList = UnPeekLiveData<ListDataUiState<NewsBean>>()
    var newsBeanValue=UnPeekLiveData<NewsBean>()

    private var pageNo = 1






    /**
     * 获取首页新闻列表
     */
    fun getNewsList(isRefresh: Boolean){
        if (isRefresh) {
            pageNo = 1
        }
        request(
            { apiService.getNewsList(NewsReq(current=pageNo)) },
            {
                pageNo++
                liveList.value = ListDataUiState(
                    isSuccess = true,
                    isRefresh = isRefresh,
                    isEmpty = it.records.isEmpty(),
                    isFirstEmpty = isRefresh && it.records.isEmpty(),
                    listData = it.records
                )
            }, {
                //请求失败
                //请求失败
                liveList.value = ListDataUiState(
                    isSuccess = false,
                    isRefresh = isRefresh,
                    errMessage = it.errorMsg,
                    listData = arrayListOf()
                )


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