package arch.cayenne.module.chat.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.module.chat.data.compare.AtBeanCompare
import arch.cayenne.module.chat.data.model.AtBean
import arch.cayenne.module.chat.databinding.ItemAtLayoutBinding

/**
 * @author: wenxi
 * @date: 19/11/25 15:57
 * @description:
 */
class AtAdapter() : BaseAdapter<AtBean, AtAdapter.AtViewHolder, ItemAtLayoutBinding>(AtBeanCompare()) {
    private var itemListener:RecyclerItemListener<AtBean>? = null
   var selectedSet:MutableSet<Int> = mutableSetOf()

    fun setItemClickListener(itemListener:RecyclerItemListener<AtBean>){
        this.itemListener = itemListener
    }

    inner class AtViewHolder(binding: ItemAtLayoutBinding) : BaseViewHolder(binding){
        init {
            binding.main.setOnClickListener {
                val position = it.tag as Int


                if(selectedSet.contains(position)){
                    selectedSet.remove(position)
                }else{
                    selectedSet.add(position)
                }
                itemListener?.onItemClick(getItem(position).builder(select = selectedSet.contains(position)),position)
                notifyItemChanged(position)
            }
        }
    }

    override fun convertPlus(holder: AtViewHolder, binding: ItemAtLayoutBinding, position: Int) {
        binding.main.tag = position
        binding.main.isSelected = selectedSet.contains(position)
        binding.ivSelected.isVisible = binding.main.isSelected
        binding.tv.text = getItem(position).name
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemAtLayoutBinding {

        return ItemAtLayoutBinding.inflate(inflater,parent,false)
    }

    fun clear(){
        selectedSet.forEach {
            notifyItemChanged(it)
        }
        selectedSet.clear()
    }

    override fun createViewHolder(binding: ItemAtLayoutBinding, viewType: Int): AtViewHolder {

        return AtViewHolder(binding)
    }
}