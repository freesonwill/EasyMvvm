package com.luxury.lib_base.base

import androidx.lifecycle.ViewModel
import com.luxury.lib_base.base.interface_.IModel
import com.luxury.lib_base.bean.UnPeekLiveData

abstract class BaseViewModel : ViewModel(), IModel {
    val loadingChange: UiLoadingChange by lazy { UiLoadingChange() }

    override fun onCleared() {
        super.onCleared()
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