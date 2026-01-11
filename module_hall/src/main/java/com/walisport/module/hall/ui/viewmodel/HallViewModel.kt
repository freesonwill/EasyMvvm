package com.walisport.module.hall.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.http.data.Result
import com.walisport.module.business.common.data.BannerActiveBean
import com.walisport.module.business.common.ui.viewmodel.BaseBannerViewModel
import com.walisport.module.business.common.utils.biz.BannerBiz
import com.walisport.module.hall.data.HallRepository
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class HallViewModel : BaseBannerViewModel() {
    private val repository: HallRepository by inject { parametersOf(viewModelScope) }

    private val _bannerActiveLiveData = MutableLiveData<List<BannerActiveBean>>(emptyList())
    val curveBannerLiveData: LiveData<List<BannerActiveBean>> = _bannerActiveLiveData.map {
        it.filter { banner -> banner.activityType == BannerActiveBean.ACTIVITY_TYPE_HOME_FIXED }
    }
    val inviteFriendBannerLiveData: LiveData<List<BannerActiveBean>> = _bannerActiveLiveData.map {
        it.filter { banner -> banner.activityType == BannerActiveBean.ACTIVITY_TYPE_INVITE_FRIEND }
    }
    val gameCategory = repository.gameCategoryListLiveData //分类列表

    fun observeUserLogin() = repository.observeUserLogin()

    fun queryGameCommon() {
        repository.queryGameCommonList()
    }

    fun checkIsLogin(): Boolean {
        return repository.checkIsLogin()
    }

    suspend fun getBannerActive() {
        val result = BannerBiz.getBannerActive()
        if(result is Result.Success){
            _bannerActiveLiveData.value = result.data.data
        } else {
            "getBannerActive failed: $result".loge(TAG)
        }
    }
}