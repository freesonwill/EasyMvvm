package com.walisport.module.me.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.me.data.MeRepository
import com.walisport.module.me.data.MeTabBean
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
@KoinViewModel
class MeViewModel : BaseViewModel() {
    private val repository: MeRepository by inject { parametersOf(viewModelScope) }
    val bottomIndexFlow:MutableSharedFlow<Int> = MutableSharedFlow(replay = 1)
    private val _count = MutableLiveData<List<MeTabBean>>()
    val count: LiveData<List<MeTabBean>> = _count

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
        viewModelScope.launch {
            _count.value = listOf(
                MeTabBean(
                    id = 0,
                    name =arch.cayenne.lib.common.R.string.drawer_recently_played.getString(),
                    const = 999
                ),
                MeTabBean(
                    id = 1,
                    name = arch.cayenne.lib.common.R.string.drawer_game_collections.getString(),
                    const = 2
                ),
                MeTabBean(
                    id = 2,
                    name = arch.cayenne.lib.common.R.string.drawer_match_collections.getString(),
                    const = 19
                ))
        }
    }
}