package arch.cayenne.module.chat.ui.adapter

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.module.chat.data.compare.ChatCompare
import arch.cayenne.module.chat.databinding.ItemLiveChatBinding
import kotlinx.coroutines.flow.combine

class ChatAdapter :
    BaseAdapter<ChatMsg, ChatAdapter.LiveChatViewHolder, ItemLiveChatBinding>(ChatCompare()) {
    private var itemClick: RecyclerItemListener<ChatMsg>? = null

    fun setOnItemListener(listener: RecyclerItemListener<ChatMsg>) {
        this.itemClick = listener
    }

    inner class LiveChatViewHolder(binding: ItemLiveChatBinding) : BaseViewHolder(binding) {
        val nBinding = binding

        fun initListener() {
            nBinding.main.apply {

                setOnClickListener {
                    val position = it.tag as Int
                    itemClick?.onItemClick(getItem(position), position)
                }
            }
//           nBinding.tv.setOnClickListener {
//               "tv".logd("aaa")
//           }
        }

        fun setText(bean: ChatMsg, position: Int) {
            val first = "${bean.userName}:"
            val second = bean.content

            val builder = SpannableStringBuilder()
            builder.append("$first  ")
            builder.setSpan(
                ForegroundColorSpan(
                    SkinnableResourceManager.getColor(
                        binding.root.context, arch.cayenne.lib.common.R.color.color_00A7C0
                    )
                ), 0, first.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.append(second)
            builder.setSpan(
                ForegroundColorSpan(
                    SkinnableResourceManager.getColor(
                        binding.root.context, arch.cayenne.lib.common.R.color.color_FFFFFF
                    )
                ), first.length, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            nBinding.tv.text = builder
            nBinding.main.tag = position
        }
    }

    override fun convertPlus(
        holder: LiveChatViewHolder,
        binding: ItemLiveChatBinding,
        position: Int
    ) {
        holder.setText(getItem(position), position)
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ItemLiveChatBinding {
        val binding = ItemLiveChatBinding.inflate(inflater, parent, false)
        return binding
    }

    override fun createViewHolder(binding: ItemLiveChatBinding, viewType: Int): LiveChatViewHolder {
        val holder = LiveChatViewHolder(binding)
        holder.initListener()
        return holder
    }

}