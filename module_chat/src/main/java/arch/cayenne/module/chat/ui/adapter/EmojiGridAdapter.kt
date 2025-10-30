package arch.cayenne.module.chat.ui.adapter

import android.content.ClipData.Item
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.chat.data.compare.EmojiChildCompare
import arch.cayenne.module.chat.data.constants.EmojiTypeEnum
import arch.cayenne.module.chat.data.model.EmojiData
import arch.cayenne.module.chat.databinding.ItemEmojiGridLayoutBinding
import arch.cayenne.module.chat.utils.EmojiDeleteAnimHelper

/**
 * @author: wenxi
 * @date: 23/10/25 10:28
 * @description:
 */
class EmojiGridAdapter :
    BaseAdapter<List<EmojiData>, EmojiGridAdapter.EmojiGridViewHolder, ItemEmojiGridLayoutBinding>(
        EmojiChildCompare()
    ) {

    private var emojiType: EmojiTypeEnum = EmojiTypeEnum.NORMAL

    private var emojiAnim:EmojiDeleteAnimHelper? = null

    fun updateEmojiType(type: EmojiTypeEnum) {
        this.emojiType = type
    }

    inner class EmojiGridViewHolder(binding: ItemEmojiGridLayoutBinding) : BaseViewHolder(binding) {
        fun initAdapter() {
            val nBinding = binding as ItemEmojiGridLayoutBinding
            val manager = object :GridLayoutManager(nBinding.root.context,8){
                override fun canScrollVertically(): Boolean {
                    return true
                }
            }
            val adapter = EmojiItemAdapter()
            nBinding.gridRecycler.also {
                it.layoutManager = manager
                it.adapter = adapter
            }
        }
    }

    override fun convertPlus(
        holder: EmojiGridViewHolder,
        binding: ItemEmojiGridLayoutBinding,
        position: Int
    ) {
        val list = getItem(position)
        binding.tvTitle.text = if (position == 0) "最近使用" else "全部表情"
        val lp = binding.tvTitle.layoutParams as ConstraintLayout.LayoutParams
        lp.leftMargin = if (position == 0) 6 else 8.dp2px

        binding.tvTitle.layoutParams = lp
        binding.gridRecycler.also {
            val manager = it.layoutManager as GridLayoutManager
            manager.spanCount = if (emojiType == EmojiTypeEnum.NORMAL) 8 else 4
            val adapter = it.adapter?.let { it as EmojiItemAdapter }
            adapter?.submitList(list)
//          if(emojiAnim == null && emojiType == EmojiTypeEnum.NORMAL){
//              emojiAnim = EmojiDeleteAnimHelper(binding.gridRecycler)
//          }
        }

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
    ): ItemEmojiGridLayoutBinding {
        return ItemEmojiGridLayoutBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemEmojiGridLayoutBinding,
        viewType: Int
    ): EmojiGridViewHolder {
        val holder = EmojiGridViewHolder(binding)
        holder.initAdapter()
        return holder
    }
}