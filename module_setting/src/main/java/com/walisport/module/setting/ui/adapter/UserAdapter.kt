package com.walisport.module.setting.ui.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import com.walisport.module.setting.R
import com.walisport.module.setting.data.UserBean
import com.walisport.module.setting.databinding.ItemUserBinding
import com.walisport.module.setting.ui.adapter.compare.UserCompare

class UserAdapter : BaseAdapter<UserBean, BaseViewHolder, ItemUserBinding>(
    UserCompare()
) {
    private var isEdit: Boolean = false
    private var clicklistener: OnItemClickListener? = null

    @SuppressLint("DefaultLocale")
    override fun convertPlus(
        holder: BaseViewHolder, binding: ItemUserBinding, position: Int
    ) {
        val item = getItem(position)
        binding.tvUserNick.text = item.nickname
        binding.ivUserAvatar.background = getUserAvatar(item.id)
        if (position == itemCount - 1) {
            binding.viewLine.visibility = View.GONE
        } else {
            binding.viewLine.visibility = View.VISIBLE
        }
        if (isEdit) {
            binding.radioDel.visibility = View.VISIBLE
            binding.radioUser.visibility = View.GONE
        } else {
            binding.radioDel.visibility = View.GONE
            binding.radioUser.visibility = View.VISIBLE
        }
        binding.radioUser.isSelected = item.isSelected
        if (item.id == -1) {
            binding.radioDel.visibility = View.GONE
            binding.radioUser.visibility = View.GONE
        }
        binding.radioDel.setOnClickListener {
            clicklistener?.onItemDelete(item.id, item.nickname)
        }
        binding.root.setOnClickListener {
            clicklistener?.onItemClick(item.id)
        }
    }

    private fun getUserAvatar(id: Int): Drawable {
        return when (id) {
            -1 -> R.drawable.ic_head_login.getDrawable()
            0 -> R.drawable.ic_head_info_1.getDrawable()
            1 -> R.drawable.ic_head_info_2.getDrawable()
            2 -> R.drawable.ic_head_info_3.getDrawable()
            else -> R.drawable.ic_head_info_1.getDrawable()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setEditStatus(status: Boolean) {
        isEdit = status
        notifyDataSetChanged()
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ItemUserBinding {
        return ItemUserBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemUserBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        clicklistener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(id: Int)
        fun onItemDelete(id: Int, nick: String)
    }
}