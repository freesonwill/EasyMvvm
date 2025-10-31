package arch.cayenne.module.chat.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.chat.databinding.ItemChatLivingF1LayoutBinding
import arch.cayenne.module.chat.databinding.ItemChatLivingLayoutBinding

/**
 * @author: wenxi
 * @date: 31/10/25 10:38
 * @description:
 */
class ChatLivingAdapter:BaseAdapter<Int,ChatLivingAdapter.ChatLivingHolder,ViewBinding>(
    object :ItemCallback<Int>(){
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }
) {
val normalType = 101
val f1Type = 102

inner class ChatLivingHolder(binding:ViewBinding) :BaseViewHolder(binding){}

    override fun convertPlus(
        holder: ChatLivingHolder,
        binding: ViewBinding,
        position: Int
    ) {

    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding{
    return  if(viewType == normalType)
          ItemChatLivingLayoutBinding.inflate(inflater,parent,false)
      else
          ItemChatLivingF1LayoutBinding.inflate(inflater,parent,false)
    }

    override fun createViewHolder(
        binding: ViewBinding,
        viewType: Int
    ): ChatLivingHolder {
        return ChatLivingHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return if(position > 1) f1Type else normalType
    }

}