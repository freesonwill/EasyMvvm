package arch.cayenne.module.chat.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.module.chat.data.compare.EmojiCompare
import arch.cayenne.module.chat.data.constants.EmojiTypeEnum
import arch.cayenne.module.chat.data.model.EmojiModel
import arch.cayenne.module.chat.databinding.ItemBidEmojiLayoutBinding
import arch.cayenne.module.chat.databinding.ItemChatEmojiTitleLayoutBinding
import arch.cayenne.module.chat.databinding.ItemEmojiLayoutBinding
import arch.cayenne.module.chat.databinding.ItemRecylerFooterBinding

/**
 * @author: wenxi
 * @date: 23/10/25 10:28
 * @description:
 */
class EmojiGridAdapter :
    BaseAdapter<EmojiModel, EmojiGridAdapter.EmojiGridViewHolder, ViewBinding>(
        EmojiCompare()
    ) {

    private var emojiType: EmojiTypeEnum = EmojiTypeEnum.NORMAL
    private var recentList: List<EmojiModel> = arrayListOf()
    private val topViewType = 1
    private val bottomViewType = 4
    private val normalEmojiViewType = 2
    private val bidEmojiViewType = 3
    private var emojiListener: RecyclerItemListener<EmojiModel>? = null

    fun setEmojiListener(listener: RecyclerItemListener<EmojiModel>) {
        this.emojiListener = listener
    }

    fun setRecentList(list: List<EmojiModel>) {
        this.recentList = list
        notifyItemChanged(0)
    }

    fun updateEmojiType(type: EmojiTypeEnum) {
        this.emojiType = type

    }

    inner class EmojiGridViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        fun initRecentAdapter() {
            val nBinding = binding
            if (nBinding is ItemChatEmojiTitleLayoutBinding) {
                nBinding.recycler.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    val nAdapter = EmojiItemAdapter()
                    nAdapter.setItemListener(emojiListener)
                    nAdapter.submitList(recentList)
                    adapter = nAdapter
                }
            }
        }

        fun initListener() {
            val nBinding = binding
            when (nBinding) {
                is ItemEmojiLayoutBinding -> {
                    nBinding.container.setOnClickListener {
                        val position = it.tag as Int
                        emojiListener?.onItemClick(getItem(position), position)
                    }
                }

                is ItemBidEmojiLayoutBinding -> {
                    nBinding.container.setOnClickListener {
                        val position = it.tag as Int
                        emojiListener?.onItemClick(getItem(position), position)
                    }
                }

                else -> {
                }
            }
        }

        fun updateImg(position: Int) {
            val nBinding = binding
            when (nBinding) {
                is ItemEmojiLayoutBinding -> {
                    nBinding.iv.setImageResource(getItem(position).resId)
                    nBinding.container.tag = position
                }

                is ItemBidEmojiLayoutBinding -> {
                    nBinding.iv.setImageResource(getItem(position).resId)
                    nBinding.container.tag = position
                }

                else -> {

                }
            }
        }

    }

    override fun convertPlus(
        holder: EmojiGridViewHolder,
        binding: ViewBinding,
        position: Int
    ) {
//        mangerUpdateListener?.updateManager(position)
        holder.updateImg(position)

//        val list = getItem(position)
//        binding.tvTitle.text = if (position == 0) "最近使用" else "全部表情"
//        val lp = binding.tvTitle.layoutParams as ConstraintLayout.LayoutParams
//        lp.leftMargin = if (position == 0) 6 else 8.dp2px
//
//        binding.tvTitle.layoutParams = lp
//        binding.gridRecycler.also {
//            val manager = it.layoutManager as GridLayoutManager
//            manager.spanCount = if (emojiType == EmojiTypeEnum.NORMAL) 8 else 4
//            val adapter = it.adapter?.let { it as EmojiItemAdapter }
//            adapter?.submitList(list)
//        }

//        val contentLp = ConstraintLayout.LayoutParams(
//            LayoutParams.MATCH_PARENT,
//            if (position == 0) LayoutParams.WRAP_CONTENT else LayoutParams.MATCH_PARENT
//        )
//        binding.root.layoutParams = contentLp
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (viewType) {
            topViewType -> ItemChatEmojiTitleLayoutBinding.inflate(
                inflater,
                parent,
                false
            )

            bottomViewType -> ItemRecylerFooterBinding.inflate(inflater, parent, false)
            normalEmojiViewType -> ItemEmojiLayoutBinding.inflate(
                inflater,
                parent,
                false
            )

            else -> ItemBidEmojiLayoutBinding.inflate(inflater, parent, false)
        }
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): EmojiGridViewHolder {
        val holder = EmojiGridViewHolder(binding)
        holder.initRecentAdapter()
        holder.initListener()
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            return topViewType
        } else if (getItem(position).resId == -2) {
            return bottomViewType
        } else if (emojiType == EmojiTypeEnum.NORMAL) normalEmojiViewType else bidEmojiViewType

    }
//        return  if (emojiType == EmojiTypeEnum.NORMAL) normalEmojiViewType else bidEmojiViewType


}