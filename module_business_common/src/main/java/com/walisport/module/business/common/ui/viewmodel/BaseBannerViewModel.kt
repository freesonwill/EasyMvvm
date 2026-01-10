package com.walisport.module.business.common.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.http.data.Result
import com.walisport.module.business.common.ui.adapter.BannerImageMatchAdapter
import com.walisport.module.business.common.utils.biz.BannerBiz

/**
 * @date: 2026/1/8 22:33
 * @description:
 */
abstract class BaseBannerViewModel:BaseViewModel() {
    //分类列表触发广告位收起动画  true 为收起 false 为展开
    private val _scroll = MutableLiveData<Boolean>()
    val scroll: LiveData<Boolean> = _scroll
    //滚动状态变更通知
    private val _scrollStateChanged = UnPeekLiveData<Int>()
    val scrollStateChanged: LiveData<Int> = _scrollStateChanged

    suspend fun getBannerList():List<BannerImageMatchAdapter.ImageData>{
        val result =  BannerBiz.getBannerList()
        return if( result is Result.Success){
            result.data.data.map {
                BannerImageMatchAdapter.ImageData(
                    imgUrl = it.imageDomain+it.bottomImagePath,
                    targetUrl = it.buttons[0].operateParams[0]
                )
            }
        }else {
            "getBannerList failed:$result".loge()
            emptyList()
        }
    }

    fun setScroll(b:Boolean){
        if (b!=scroll.value) {
            _scroll.value = b
        }
    }

    fun setScrollState(state:Int){
        if (_scrollStateChanged.value != state) {
            _scrollStateChanged.value = state
        }
    }
}