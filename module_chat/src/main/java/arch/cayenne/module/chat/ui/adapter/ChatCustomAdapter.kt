package arch.cayenne.module.chat.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.databinding.ItemChatCustomerLayoutBinding

/**
 * @author: wenxi
 * @date: 31/10/25 15:17
 * @description:
 */
class ChatCustomAdapter :
    BaseAdapter<Int, ChatCustomAdapter.ChatCustomerViewHolder, ItemChatCustomerLayoutBinding>(
        object : ItemCallback<Int>() {
            override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    inner class ChatCustomerViewHolder(binding: ItemChatCustomerLayoutBinding) :
        BaseViewHolder(binding) {

        fun getAvatar(position: Int): Int {
            return when (position) {
                0 -> R.drawable.icon_chat_item_union_pay
                1 -> R.drawable.icon_chat_item_customer
                else -> R.drawable.test_item_customer
            }
        }

        fun getTitle(position: Int): String {
            return when (position) {
                0 -> "官方2联银商"
                1 -> "福利客服"
                else -> "南京红姐秒冲"
            }
        }

        fun getContent(position: Int): String {
            return when (position) {
                0 -> "您好，当前闪付不再线上，请稍后"
                1 -> "您好，我是您的福利客服，您目前享有首有首有首有首"
                else -> "您好，支付宝、微信、银行卡、您好，支付宝、微信、银行卡、数字人民"
            }
        }
    }

    override fun convertPlus(
        holder: ChatCustomerViewHolder,
        binding: ItemChatCustomerLayoutBinding,
        position: Int
    ) {
        binding.ivAvatar.setImageResource(holder.getAvatar(position))
        binding.name.text = holder.getTitle(position)
        binding.content.text = holder.getContent(position)
        binding.flCircle.isInvisible = position == 0
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemChatCustomerLayoutBinding {
        return ItemChatCustomerLayoutBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemChatCustomerLayoutBinding,
        viewType: Int
    ): ChatCustomerViewHolder {
        return ChatCustomerViewHolder(binding)
    }
}