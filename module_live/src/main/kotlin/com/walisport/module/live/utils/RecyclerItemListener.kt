package com.walisport.module.live.utils

interface RecyclerItemListener<T> {
    fun  onItemClick(item:T,position:Int)
}