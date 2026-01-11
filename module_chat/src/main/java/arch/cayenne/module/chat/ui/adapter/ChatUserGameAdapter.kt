package arch.cayenne.module.chat.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import java.lang.ref.WeakReference
import arch.cayenne.module.chat.databinding.AdapterGameItemLayoutBinding
import com.walisport.module.live.data.model.GameListBean
import arch.cayenne.module.chat.R
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
class ChatUserGameAdapter() :
    BaseAdapter<GameListBean, ChatUserGameAdapter.ChatUserGameViewHolder, ViewBinding>(
        GameListBeanDiffCallback()
    ) {

    inner class ChatUserGameViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        private val viewBinding: AdapterGameItemLayoutBinding =
            binding as AdapterGameItemLayoutBinding

        fun updateItem(position: Int) {
            val item = getItem(position)
            viewBinding.tvNumber.setTextColor(if (position==0) R.color.adapter_number_text_color.getColor() else arch.cayenne.lib.common.R.color.white.getColor())
//            viewBinding.gameName.text = item.name
//            viewBinding.tvNumber.text = "${position+1}"
//            viewBinding.tvMoney.text = item.money
        }
    }

    override fun convertPlus(holder: ChatUserGameViewHolder, binding: ViewBinding, position: Int) {
        holder.updateItem(position)
    }

    override fun onBindViewHolder(
        holder: ChatUserGameViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            holder.updateItem(position)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ViewBinding {
        val binding = AdapterGameItemLayoutBinding.inflate(inflater, parent, false)
        return binding
    }

    override fun createViewHolder(
        binding: ViewBinding, viewType: Int
    ): ChatUserGameViewHolder {
        val holder = ChatUserGameViewHolder(binding)
        return holder
    }
}


interface LivBetListCallback {
    fun itemListCallback(
        cell: WeakReference<View>, marketI: Long, selectionId: Long, x: Float, y: Float
    )
}

class GameListBeanDiffCallback : DiffUtil.ItemCallback<GameListBean>() {

    override fun areItemsTheSame(oldItem: GameListBean, newItem: GameListBean): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: GameListBean, newItem: GameListBean): Boolean {
        return oldItem == newItem
    }
}