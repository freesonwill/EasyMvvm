package com.luxury.lib_base.base.interface_

interface IModel {
    fun onInit() { }

    fun showLoading(title: String? = "加载中")

    fun dismissLoading()
}