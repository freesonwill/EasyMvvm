package com.walisport.module.me.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.UserDataBean
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.me.data.MeRepository
import com.walisport.module.me.data.MeTabBean
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class MeViewModel : BaseViewModel() {
    private val repository: MeRepository by inject { parametersOf(viewModelScope) }
    val bottomIndexFlow: MutableSharedFlow<Int> = MutableSharedFlow(replay = 1)

    private var _recentlyCount: Long = 0L
    private var _gameFavouriteCount: Long = 0L
    private var _matchCount: Long = 0L

    private val _count = MutableLiveData<List<MeTabBean>>()
    val count: LiveData<List<MeTabBean>> = _count

    private val _onVipListener = MutableLiveData<UserDataBean>()
    val onVipListener: LiveData<UserDataBean> get() = _onVipListener

    private val _onViewpagerHeight = MutableLiveData<Int>()
    val onViewpagerHeight: LiveData<Int> get() = _onViewpagerHeight
     fun setOnHeight(height: Int) {
         _onViewpagerHeight.value = height
    }

    override fun initViewModel() {
        super.initViewModel()
        viewModelScope.launch {
            repository.observeUserInfo().collect {
                _onVipListener.value = it
            }
        }
    }

    //子类判断是否滑动到顶部
    private val _sonVerticalScrollIsTop = MutableLiveData<Boolean?>(true)
    val sonVerticalScrollIsTop: LiveData<Boolean?> = _sonVerticalScrollIsTop


    private val _scrollTop = MutableLiveData<Boolean?>()
    val scrollTop: LiveData<Boolean?> = _scrollTop

    fun setSonVerticalScrollIsTop(boo: Boolean) {
        if (boo != sonVerticalScrollIsTop.value) {
            _sonVerticalScrollIsTop.value = boo
        }
    }

    fun setScrollTop(boo: Boolean) {
        _scrollTop.value = boo
    }


    fun getSonVerticalScrollIsTop(): Boolean? {
        return sonVerticalScrollIsTop.value
    }

    fun createObserver() {

    }

    fun setRecentlyCount(count: Long) {
        _recentlyCount = count
        changeCount()
    }

    fun setGameFavouriteCount(count: Long) {
        _gameFavouriteCount = count
        changeCount()
    }

    private fun changeCount() {
        viewModelScope.launch {
            _count.value = listOf(
                MeTabBean(
                    id = 0,
                    name = arch.cayenne.lib.common.R.string.drawer_recently_played.getString(),
                    const = _recentlyCount.toInt()
                ),
                MeTabBean(
                    id = 1,
                    name = arch.cayenne.lib.common.R.string.drawer_game_collections.getString(),
                    const = _gameFavouriteCount.toInt()
                ),
                MeTabBean(
                    id = 2,
                    name = arch.cayenne.lib.common.R.string.drawer_match_collections.getString(),
                    const = _matchCount.toInt()
                )
            )
        }
    }
}