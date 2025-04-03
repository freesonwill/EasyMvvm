package com.walisport.lib.common.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

/***
 * @param T: Item Object
 * @param VH: ViewHolder, you can call function of getBaseViewHolder if your adapter is sincerely simple.
 * @param VB: ItemBinding
 *
 * @author Link Hsieh
 */
abstract class BaseAdapter<T, VH : BaseViewHolder, VB : ViewBinding>(
    compare: DiffUtil.ItemCallback<T>
) :
    ListAdapter<T, VH>(compare) {

    abstract fun convertPlus(holder: VH, binding: VB, position: Int)

    abstract fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): VB

    abstract fun createViewHolder(binding: VB, viewType: Int): VH
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = createViewBinding(LayoutInflater.from(parent.context), parent, viewType)
        val holder = createViewHolder(binding, viewType)
        return holder
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        convertPlus(holder, holder.binding as VB, position)
    }

    protected fun getBaseViewHolder(binding: VB): BaseViewHolder {
        return BaseViewHolder(binding)
    }

}