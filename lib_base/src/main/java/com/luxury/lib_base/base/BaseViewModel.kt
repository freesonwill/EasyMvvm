package com.luxury.lib_base.base

import androidx.lifecycle.ViewModel
import com.luxury.lib_base.base.interface_.IModel
import com.luxury.lib_base.bean.UnPeekLiveData

abstract class BaseViewModel : ViewModel(), IModel {
    val loadingChange: UiLoadingChange by lazy { UiLoadingChange() }
    //数据回收操作
    private val dataGC by lazy { mutableSetOf<() -> Unit>() }
    //============================ Method ================================//

    override fun onCleared() {
        super.onCleared()
        val it = dataGC.iterator()
        while (it.hasNext()) {
            it.next().invoke()
            it.remove()
        }
    }

    //注册数据的自动回收
    fun registerDataGC(onClear: () -> Unit) {
        dataGC.add(onClear)
    }

    override fun showLoading(title: String?) {
        this.loadingChange.showDialog.postValue(title)
    }

    override fun dismissLoading() {
        this.loadingChange.dismissDialog
    }

    inner class UiLoadingChange {
        //显示加载框
        val showDialog by lazy { UnPeekLiveData<String>() }

        //隐藏
        val dismissDialog by lazy { UnPeekLiveData<Boolean>() }
    }
}