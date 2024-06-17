package com.cn.game.sdk2.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.bean.FlowState
import com.cn.game.sdk2.data.bean.HistoryResultBean
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.utils.getString
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * Description: 快3 ViewModel
 * author       : zhangsan
 * createTime   : 2024/6/17 11:02
 **/
class Fast3ViewModel : BaseViewModel() {
    private val TAG = "Fast3ViewModel"
    var betOkClick: LiveData<Boolean> = UnPeekLiveData()
    var betDeleteClick: LiveData<Boolean> = UnPeekLiveData()
    val timeLiveData: LiveData<Int> by lazy { UnPeekLiveData() }
    val historyResultBeans: LiveData<List<HistoryResultBean>> by lazy { UnPeekLiveData() }
    val isClickOperation:LiveData<Boolean> by lazy { UnPeekLiveData(true) }
    private var mCounterDownJob: Job? = null
    val moneyTotal:LiveData<Float> by lazy { UnPeekLiveData(10000f) }

    //====================================== Method ========================================//
    init {
        val list = ArrayList<HistoryResultBean>()
        for (c in 0 until 20) {
            list.add(HistoryResultBean(result = listOf(c % 6+1, (c + 1) % 6+1, (c + 2) % 6+1)))
        }
        (historyResultBeans as UnPeekLiveData).value = list
    }

    fun betOkClick(){
        (betOkClick as UnPeekLiveData).value = true
    }

    fun betDeleteClick(){
        (betDeleteClick as UnPeekLiveData).value = true
    }

    fun startCounterDown(times: Int) {
        mCounterDownJob?.cancel()
        mCounterDownJob = viewModelScope.launch {
            loadingChange.showDialog.value = getString(R.string.g_home_betting_begin)
            delay(1000)
            startCountDown(times).collect { state ->
                Log.d(TAG, "startCounterDown-->${state}")
                when (state) {
                    is FlowState.Start -> {
                        (isClickOperation as UnPeekLiveData).value = false
                    }

                    is FlowState.Progress -> {
                        loadingChange.dismissDialog.value = true
                        (timeLiveData as UnPeekLiveData).value = state.data!!
                    }

                    is FlowState.Success -> {
                        (timeLiveData as UnPeekLiveData).value = 0
                        loadingChange.showDialog.value = getString(R.string.g_home_betting_end)
                        delay(1000)
                        loadingChange.dismissDialog.value = true
                        (isClickOperation as UnPeekLiveData).value = true
                    }

                    else -> {
                    }
                }

            }
        }
    }

    private suspend fun startCountDown(times: Int): Flow<FlowState<Int>> {
        return flow {
            emit(FlowState.Start())
            repeat(times) { time ->
                emit(FlowState.Progress(time, times, (times - time)))
                delay(1_000)
            }
            delay(200)
            emit(FlowState.Success())
        }
    }

}