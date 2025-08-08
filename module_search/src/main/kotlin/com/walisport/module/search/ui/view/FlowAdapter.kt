package com.walisport.module.search.ui.view;

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * 通用 FlowLayout Adapter，負責資料與 ViewBinding 的橋接邏輯
 * @param T 資料型別
 * @param VH ViewHolder 類型，需繼承 FlowViewHolder
 * @param VB ViewBinding 類型
 */
abstract class FlowAdapter<T, VH : FlowViewHolder, VB : ViewBinding> {

    // 資料集合
    private var data: MutableList<T> = mutableListOf()

    // 資料更新監聽器，通常由 FlowLayout 實作
    private var onDataChangedListener: OnDataChangedListener? = null

    /**
     * 綁定資料與視圖邏輯，供子類實作
     */
    abstract fun convertPlus(holder: VH, binding: VB, position: Int)

    /**
     * 建立 ViewBinding，供子類自訂 inflate 邏輯
     */
    abstract fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): VB

    /**
     * 建立 ViewHolder，供子類自訂實作
     */
    abstract fun createViewHolder(binding: VB, viewType: Int): VH

    /**
     * 對外建立 ViewHolder 的統一入口
     */
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = createViewBinding(LayoutInflater.from(parent.context), parent, viewType)
        return createViewHolder(binding, viewType)
    }

    /**
     * 對外綁定資料與 ViewHolder
     */
    @Suppress("UNCHECKED_CAST")
    fun onBindViewHolder(holder: VH, position: Int) {
        convertPlus(holder, holder.binding as VB, position)
    }

    /**
     * 回傳資料筆數
     */
    fun getItemCount(): Int = data.size

    /**
     * 取得指定位置的資料
     */
    fun getItem(position: Int): T = data[position]

    /**
     * 取得整份資料清單（唯讀）
     */
    fun getData(): List<T> = data

    /**
     * 設定新資料，會取代原有內容並觸發更新
     */
    fun setNewData(newData: List<T>) {
        data.clear()
        data.addAll(newData)
        notifyDataChanged()
    }

    /**
     * 刪除指定位置的資料，並觸發更新
     */
    fun deleteData(position: Int) {
        data.removeAt(position)
        notifyDataChanged()
    }

    /**
     * 刪除所有資料，並觸發更新
     */
    fun deleteAllData() {
        data.clear()
        notifyDataChanged()
    }

    /**
     * 通知資料更新，觸發 listener 回調
     */
    fun notifyDataChanged() {
        onDataChangedListener?.onChanged()
    }

    /**
     * 設定資料更新監聽器
     */
    fun setOnDataChangedListener(listener: OnDataChangedListener?) {
        this.onDataChangedListener = listener
    }

    /**
     * 資料更新回調介面，由 FlowLayout 實作接收
     */
    interface OnDataChangedListener {
        fun onChanged()
    }
}