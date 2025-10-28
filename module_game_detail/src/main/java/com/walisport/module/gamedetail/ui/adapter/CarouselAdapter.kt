package com.walisport.module.gamedetail.ui.adapter

import android.view.ViewGroup
import com.walisport.module.gamedetail.ui.view.CarouselScrollView
import com.walisport.module.gamedetail.ui.viewholder.CarouselViewHolder

/**
 * 為 CarouselScrollView 提供數據和視圖的適配器。
 * @param VH ViewHolder 的類型，必須是 CarouselViewHolder 的子類。
 */
abstract class CarouselAdapter<VH: CarouselViewHolder> {
    private var carouselView: CarouselScrollView? = null
    private val observers = mutableListOf<() -> Unit>()

    /**
     * 獲取列表項的數量。
     */
    abstract fun getItemCount(): Int

    /**
     * 當 CarouselScrollView 需要一個新的 ViewHolder 時呼叫此方法。
     *
     * @param parent 新視圖將被添加到的父 ViewGroup。
     * @param viewType 視圖的類型（暫不實現）。
     * @return 一個新的 VH 實例。
     */
    abstract fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH

    /**
     * 當 CarouselScrollView 需要將數據綁定到一個 ViewHolder 時呼叫此方法。
     *
     * @param holder 需要綁定數據的 ViewHolder。
     * @param position 數據在列表中的位置。
     */
    abstract fun onBindViewHolder(holder: VH, position: Int)

    internal fun registerAdapterDataObserver(observer: () -> Unit) {
        observers.add(observer)
    }

    internal fun unregisterAdapterDataObserver(observer: () -> Unit) {
        observers.remove(observer)
    }

    /**
     * 通知所有觀察者數據集已發生變化。
     * 這會觸發 CarouselScrollView 重新佈局。
     */
    fun notifyDataSetChanged() {
        observers.forEach { it.invoke() }
    }

    internal fun attachToCarousel(carousel: CarouselScrollView) {
        this.carouselView = carousel
    }

    internal fun detachFromCarousel() {
        this.carouselView = null
    }
}