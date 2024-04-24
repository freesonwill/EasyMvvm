package com.cn.game.sdk.ui.fast

import android.util.Log
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.base_lib.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameHomeVm : BaseViewModel() {
    var submit= UnPeekLiveData<Boolean>()
    fun getddd(){

        Log.i("VVVVVVVVVVVV","11111111111111")

    }

    override fun onCleared() {
        super.onCleared()

    }

}