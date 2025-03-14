package com.walisport.lib_base.data.viewmodel

import androidx.lifecycle.ViewModel

/**
 * @author: zhangsan
 * @date: 2025/3/14 09:51
 * @description:
 */
abstract class BaseViewModel : ViewModel() {
    //数据回收操作
    private val dataGC by lazy { mutableSetOf<() -> Unit>() }

    //============================ Method ================================//
    open fun onInit() { }

    override fun onCleared() {
        super.onCleared()
        val it = dataGC.iterator()
        while (it.hasNext()) {
            it.next().invoke()
            it.remove()
        }
    }
    //注册自动回收数据
    fun registerAutoGC(onClear: () -> Unit) {
        dataGC.add(onClear)
    }
}