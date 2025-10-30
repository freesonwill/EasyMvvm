package arch.cayenne.module.chat.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.module.chat.databinding.ItemLanguageLayoutBinding

/**
 * @author: wenxi
 * @date: 30/10/25 11:42
 * @description:
 */
class LanguageAdapter() :
    BaseAdapter<String, LanguageAdapter.LanguageViewHolder, ItemLanguageLayoutBinding>(object :
        DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }) {
    private var selectPosition: Int = -1


    inner class LanguageViewHolder(private val nBinding: ItemLanguageLayoutBinding) :
        BaseViewHolder(nBinding) {

        init {
            initListener()
        }

        fun initListener() {
            nBinding.main.setOnClickListener {
                val position = it.tag as Int
                val lastPosition = selectPosition
                selectPosition = position
                if(lastPosition != -1){
                    notifyItemChanged(lastPosition)
                }
                if(selectPosition != -1){
                    notifyItemChanged(selectPosition)
                }
            }
        }

    }

    override fun convertPlus(
        holder: LanguageViewHolder,
        binding: ItemLanguageLayoutBinding,
        position: Int
    ) {
        binding.tv.text = getItem(position)
        binding.main.tag = position

        binding.main.isSelected = selectPosition == position
        binding.tv.isSelected = selectPosition == position
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemLanguageLayoutBinding {
        return ItemLanguageLayoutBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemLanguageLayoutBinding,
        viewType: Int
    ): LanguageViewHolder {
        return LanguageViewHolder(binding)
    }
}