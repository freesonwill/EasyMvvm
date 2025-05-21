package com.walisport.module.search.ui.view;

import android.view.View
import android.view.ViewGroup

/**
 * @author: caomei
 * @date: 2025/4/22 14:18
 * @description: 流布局适配器
 */
abstract class FlowAdapter<T> {
    private var onDataChangedListener: OnDataChangedListener? = null

    private var data: MutableList<T>? = null

    /**
     * 子View创建
     *
     * @param parent
     * @param item
     * @param position
     * @return
     */
    abstract fun getView(parent: ViewGroup?, item: T, position: Int): View?

    /**
     * 初始化View
     *
     * @param view
     * @param item
     * @param position
     * @return
     */
    abstract fun initView(view: View?, item: T, position: Int)


    /**
     * 折叠View 默认不设置
     *
     * @return
     */
    fun foldView(): View? {
        return null
    }


    val count: Int
        /**
         * 数据的数量
         *
         * @return
         */
        get() = if (this.data == null) 0 else data!!.size

    /**
     * 获取数据
     *
     * @return
     */
    fun getData(): List<T>? {
        return data
    }


    /**
     * 设置新数据
     *
     * @param data
     */
    fun setNewData(data: MutableList<T>) {
        this.data = data
        notifyDataChanged()
    }


    /**
     * 添加数据
     *
     * @param data
     */
    fun addData(data: List<T>) {
        if (this.data == null) {
            this.data = ArrayList()
        }
        this.data!!.addAll(data)
        notifyDataChanged()
    }

    /**
     * 删除某个item
     *
     * @param position
     */
    fun deleteData(position: Int) {
        this.data!!.remove(this.data!![position])
        notifyDataChanged()
    }

    /**
     * 删除所有item
     */
    fun deleteAllData() {
        this.data!!.clear()
        notifyDataChanged()
    }

    /**
     * 添加数据
     *
     * @param index
     * @param data
     */
    fun addData(index: Int, data: List<T>) {
        if (this.data == null) {
            this.data = ArrayList()
        }
        this.data!!.addAll(index, data)
        notifyDataChanged()
    }


    /**
     * 添加数据
     *
     * @param data
     */
    fun addData(temp: T) {
        if (this.data == null) {
            this.data = ArrayList()
        }
        if (this.data!!.contains(temp)) {
            return
        }
        this.data!!.add(temp)
        if (this.data!!.size > 20) {//超过20条就截取最新20条
            val list = this.data!!.takeLast(20)
            this.data!!.clear()
            this.data!!.addAll(list)
        }
        notifyDataChanged()
    }


    /**
     * 获取指定位置的数据
     *
     * @param position
     * @return
     */
    fun getItem(position: Int): T? {
        if (this.data != null && position >= 0 && position < data!!.size) {
            return data!![position]
        }
        return null
    }


    /**
     * 刷新数据
     */
    fun notifyDataChanged() {
        if (this.onDataChangedListener != null) {
            onDataChangedListener!!.onChanged()
        }
    }

    fun setOnDataChangedListener(listener: OnDataChangedListener?) {
        this.onDataChangedListener = listener
    }

    interface OnDataChangedListener {
        fun onChanged()
    }
}
