package com.walisport.module.me.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.me.data.MeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class MeViewModel : BaseViewModel() {
    private val repository: MeRepository by inject { parametersOf(viewModelScope) }
    val bottomIndexFlow:MutableSharedFlow<Int> = MutableSharedFlow(replay = 1)
    private val _recentlyCount = MutableLiveData<Long>(0)
    val recentlyCount: LiveData<Long> = _recentlyCount

    private val _gameFavouriteCount = MutableLiveData<Long>(0)
    val gameFavourite: LiveData<Long> = _gameFavouriteCount

    private val _matchCount = MutableLiveData<Long>(0)
    val matchCount: LiveData<Long> = _matchCount

    override fun initViewModel() {
        super.initViewModel()
    }
    //子类判断是否滑动到顶部
    private val _sonVerticalScrollIsTop = MutableLiveData<Boolean?>(true)
    val sonVerticalScrollIsTop: LiveData<Boolean?> = _sonVerticalScrollIsTop


    private val _scrollTop = MutableLiveData<Boolean?>()
    val scrollTop: LiveData<Boolean?> = _scrollTop

    fun setSonVerticalScrollIsTop(boo:Boolean){
        if (boo!=sonVerticalScrollIsTop.value){
            _sonVerticalScrollIsTop.value = boo
        }
    }

    fun setScrollTop(boo:Boolean){
        _scrollTop.value = boo
    }


    fun getSonVerticalScrollIsTop():Boolean?{
        return sonVerticalScrollIsTop.value
    }
    fun createObserver() {
//        viewModelScope.launch {
//            delay(1500)
//            _recentlyCount.value = 1000
//            _gameCount.value = 1
//            _matchCount.value = 1
//        }
    }

    fun setRecentlyCount(count:Long){
        _recentlyCount.value = count
    }

    fun setGameFavouriteCount(count:Long){
        _gameFavouriteCount.value = count
    }
}