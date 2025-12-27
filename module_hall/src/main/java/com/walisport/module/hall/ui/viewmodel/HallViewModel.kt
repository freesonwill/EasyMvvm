package com.walisport.module.hall.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi

import com.walisport.module.hall.data.HallRepository
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class HallViewModel : BaseViewModel() {

    private val repository: HallRepository by inject { parametersOf(viewModelScope) }

    //分类列表触发广告位收起动画  true 为收起 false 为展开
    private val _scorll = MutableLiveData<Boolean>()
    val scorll: LiveData<Boolean> = _scorll

    //滚动状态变更通知
    private val _scrollStateChanged = UnPeekLiveData<Int>()
    val scrollStateChanged: LiveData<Int> = _scrollStateChanged

    val gameCategory = repository.gameCategoryListLiveData //分类列表
    fun setScorll(bool:Boolean){
        if (bool!=scorll.value) {
            _scorll.value = bool
        }
    }

    fun setScrollState(state:Int){
        if (_scrollStateChanged.value != state) {
            _scrollStateChanged.value = state
        }
    }


    fun queryGameCommon() {
        repository.queryGameCommonList()
    }




}