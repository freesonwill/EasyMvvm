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
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.chat.data.compare.ChatCompare
import arch.cayenne.lib.common.data.constants.ChatMsgType
import arch.cayenne.module.chat.data.model.ChatMsgPageBean
import arch.cayenne.module.chat.data.model.ColorSpan
import arch.cayenne.module.chat.data.model.MentionSpan
import arch.cayenne.module.chat.databinding.ItemLiveChatBinding
import arch.cayenne.module.chat.manager.SoftKeyBoardAnim
import arch.cayenne.module.chat.utils.ChatMsgUtils

class ChatPageAdapter(
    private val specialClick: (bean: ChatMsgPageBean, clickSpan: String, clickType: ChatMsgType) -> Unit,
    private val longClick: (bean: ChatMsgPageBean) -> Unit
) :
    BaseAdapter<ChatMsgPageBean, ChatPageAdapter.LiveChatViewHolder, ItemLiveChatBinding>(
        ChatCompare()
    ) {
    private val longPressTimeout = 700L
    private val longPressHandler = android.os.Handler()
    private val longPressRunnable = object : LongClickListener() {
        override fun run() {
            bean?.let {
                longClick.invoke(it)
            }
        }
    }

    private abstract inner class LongClickListener : Runnable {
        open var bean: ChatMsgPageBean? = null

        fun updateBean(newBean: ChatMsgPageBean) {
            this.bean = newBean
        }
    }

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
                        if (event == null || widget == null || buffer == null) {
                            return super.onTouchEvent(widget, buffer, event)
                        }
                        val msgId = widget.tag as String
                        val position = currentList.indexOfFirst { it.msgId == msgId }
                        if (getItem(position).msgType == ChatMsgType.SYSTEM) {
                            return true
                        }
                        // 获取点击位置
                        val x = event.x.toInt() - widget.totalPaddingLeft + widget.scrollX
                        val y = event.y.toInt() - widget.totalPaddingTop + widget.scrollY

                        val layout = widget.layout
                        val line = layout.getLineForVertical(y)
                        val off = layout.getOffsetForHorizontal(line, x.toFloat())
                        val nameSpans =
                            buffer.getSpans(off - 1, off + 1, ColorSpan::class.java)
                        if (event.action == MotionEvent.ACTION_DOWN) {

                            if (nameSpans.isNotEmpty()) {
                                longPressRunnable.updateBean(getItem(position))
                                longPressHandler.postDelayed(longPressRunnable, longPressTimeout)
                                return true
                            }

                        } else if (event.action == MotionEvent.ACTION_UP) {
                            longPressHandler.removeCallbacks(longPressRunnable)
                            val spans = buffer.getSpans(off - 1, off + 1, MentionSpan::class.java)
                            if (nameSpans.isNotEmpty()) {
                                specialClick.invoke(getItem(position), "", ChatMsgType.NAME)
                                return true
                            }
                            if (spans.isNotEmpty()) {
                                spans.first().also {
                                    specialClick.invoke(getItem(position), it.tv, it.msgType)
                                }
                                return true
                            } else {
                                specialClick.invoke(getItem(position), "", ChatMsgType.TEXT)
                            }
                        }
                        return true
                    }
                }
            }
        }

        fun setText(bean: ChatMsgPageBean, position: Int) {
            val first = "${bean.userName}:"
            val second = ChatMsgUtils.recoveryContent(bean)

            val builder = SpannableStringBuilder()
            builder.append("$first  \u2060")
            builder.setSpan(
                ColorSpan(
                    SkinnableResourceManager.getColor(
                        binding.root.context, arch.cayenne.lib.common.R.color.color_00A7C0
                    )
                ), 0, first.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.append(second)

            nBinding.tv.text = builder
            nBinding.tv.tag = bean.msgId
        }

        fun startFlash(position: Int) {
            currentList[position].flashFlag = false
            SoftKeyBoardAnim.atFlashNotifyAnim(nBinding.tv, currentList[position].msgType)
        }
    }


    override fun convertPlus(
        holder: LiveChatViewHolder,
        binding: ItemLiveChatBinding,
        position: Int
    ) {
        holder.setText(getItem(position), position)
        val item = getItem(position)

        holder.nBinding.tv.backgroundTintList = ContextCompat.getColorStateList(
            binding.tv.context,
            if (item.msgType == ChatMsgType.BET_SPORT || item.msgType == ChatMsgType.BET_GAME) {
                arch.cayenne.lib.common.R.color.color_632433
            } else {
                arch.cayenne.lib.common.R.color.color_0FFFFFFF
            }
        )
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
