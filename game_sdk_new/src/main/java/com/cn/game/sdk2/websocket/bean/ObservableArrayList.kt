package com.cn.game.sdk2.websocket.bean

class ObservableArrayList<T> : ArrayList<T>() {
    private var callbacks: OnListChangedCallback? = null

    interface OnListChangedCallback {
        fun change()
    }

    fun addOnListChangedCallback(changedCallback: OnListChangedCallback) {
        callbacks = changedCallback
    }

    override fun clear() {
        super.clear()
        callbacks?.change()
    }

    override fun remove(element: T): Boolean {
        val remove = super.remove(element)
        callbacks?.change()
        return remove
    }

    override fun add(element: T): Boolean {
        val result = super.add(element)
        callbacks?.change()
        return result
    }

    fun modify(){
        callbacks?.change()
    }

}