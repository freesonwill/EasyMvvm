package arch.cayenne.module.chat.ui.adapter

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.chat.data.compare.ChatCompare
import arch.cayenne.lib.common.data.constants.MsgType
import arch.cayenne.module.chat.data.model.ChatMsgPageBean
import arch.cayenne.module.chat.data.model.ClickSpan
import arch.cayenne.module.chat.data.model.ColorSpan
import arch.cayenne.module.chat.databinding.ItemLiveChatBinding

class ChatPageAdapter(private val specialClick: (bean: ChatMsgPageBean, clickSpan: String, clickType: MsgType) -> Unit) :
    BaseAdapter<ChatMsgPageBean, ChatPageAdapter.LiveChatViewHolder, ItemLiveChatBinding>(
        ChatCompare()
    ) {

    inner class LiveChatViewHolder(binding: ItemLiveChatBinding) : BaseViewHolder(binding) {
        val nBinding = binding
        fun initListener() {
            nBinding.tv.apply {
                movementMethod = object : LinkMovementMethod() {
                    override fun onTouchEvent(
                        widget: TextView?,
                        buffer: Spannable?,
                        event: MotionEvent?
                    ): Boolean {
                        if (widget != null && buffer != null && event?.action == MotionEvent.ACTION_UP) {
                            // 获取点击位置
                            val x = event.x.toInt() - widget.totalPaddingLeft + widget.scrollX
                            val y = event.y.toInt() - widget.totalPaddingTop + widget.scrollY

                            val layout = widget.layout
                            val line = layout.getLineForVertical(y)
                            val off = layout.getOffsetForHorizontal(line, x.toFloat())
                            val spans = buffer.getSpans(off-1, off + 1, ClickSpan::class.java)
                            val position = widget.tag as Int

                            if (spans.isNotEmpty()) {
                                spans.first().also {
                                    specialClick.invoke(getItem(position), it.tv, it.msgType)
                                }
                                return true
                            } else {
                                specialClick.invoke(getItem(position), "", MsgType.TEXT)
                            }
                        }
                        return true
                    }
                }
            }
        }

        fun setText(bean: ChatMsgPageBean, position: Int) {
            val first = "${bean.userName}:"
            val second = bean.content

            val builder = SpannableStringBuilder()
            builder.append("$first  ")
            builder.setSpan(
                ColorSpan(
                    SkinnableResourceManager.getColor(
                        binding.root.context, arch.cayenne.lib.common.R.color.color_00A7C0
                    )
                ), 0, first.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val msgSpannable = SpannableStringBuilder(second)
//            msgSpannable.setSpan(
//                ColorSpan(
//                    SkinnableResourceManager.getColor(
//                        binding.root.context, arch.cayenne.lib.common.R.color.color_FFFFFF
//                    )
//                ), 0, msgSpannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
            if (bean.msgType in arrayOf(MsgType.AT, MsgType.BET_GAME, MsgType.BET_SPORT)) {
                bean.atRange?.forEach {
                    msgSpannable.setSpan(
                        ClickSpan(bean.msgType, second.substring(it.first, it.last)),
                        it.first,
                        it.last,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            builder.append(msgSpannable)

            nBinding.tv.text = builder
            nBinding.tv.tag = position
        }
    }


    override fun convertPlus(
        holder: LiveChatViewHolder,
        binding: ItemLiveChatBinding,
        position: Int
    ) {
        holder.setText(getItem(position), position)
        val item = getItem(position)

        holder.nBinding.tv.backgroundTintList = ContextCompat.getColorStateList(binding.tv.context,
            if(item.msgType == MsgType.BET_SPORT || item.msgType == MsgType.BET_GAME) {
                arch.cayenne.lib.common.R.color.color_632433
            } else {
                arch.cayenne.lib.common.R.color.color_0FFFFFFF
            })
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
