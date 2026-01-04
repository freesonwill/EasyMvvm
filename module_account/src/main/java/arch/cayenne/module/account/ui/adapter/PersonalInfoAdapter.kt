package arch.cayenne.module.account.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.database.entity.SystemAvatarBean
import arch.cayenne.module.account.data.model.PersonalInfoData
import arch.cayenne.module.account.databinding.ItemPersonalInfoBinding
import arch.cayenne.module.account.ui.adapter.compare.PersonalInfoCompare


/**
 * @author: ricky.chang
 * @date: 2025/6/10 下午2:35
 * @description:
 */
class PersonalInfoAdapter: BaseAdapter<SystemAvatarBean, PersonalInfoItemViewHolder, ItemPersonalInfoBinding>(
    PersonalInfoCompare()
) {
    private var selectedPosition : Int = -1
    private var onItemClickListener: ((SystemAvatarBean) -> Unit)? = null
    override fun convertPlus(holder: PersonalInfoItemViewHolder, binding: ItemPersonalInfoBinding, position: Int) {
        if (getItem(position).id == selectedPosition) {
            binding.ivPersonalHeadSelected.visibility = View.VISIBLE
        } else {
            binding.ivPersonalHeadSelected.visibility = View.INVISIBLE
        }
        holder.bind(getItem(position)) { data ->
            // 2. 在 onBindViewHolder 中更新 UI
            // 檢查點擊的項目是否是已選中的項目
            if (selectedPosition == data.id) {
                return@bind
            }
            // 4. 在點擊事件中更新狀態
            val oldPosition = selectedPosition
            selectedPosition = data.id
           notifyDataSetChanged()
            onItemClickListener?.invoke(data)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int,
    ): ItemPersonalInfoBinding {
        return ItemPersonalInfoBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemPersonalInfoBinding, viewType: Int): PersonalInfoItemViewHolder {
        return PersonalInfoItemViewHolder(binding)
    }
    fun getSelectedResId(): Int {
        return if (selectedPosition == -1) {
            0
        } else {
            getItem(selectedPosition).id
        }
    }
    fun getSelectedPosition(): Int {
        return selectedPosition
    }
    fun setSelectedPosition(position: Int) {
        if (position < 0 || position >= itemCount) {
            return
        }
        val oldPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(oldPosition) // 通知舊的項目更新 UI (取消選中狀態)
        notifyItemChanged(selectedPosition) // 通知新的項目更新 UI (變為選中狀態)
    }
    fun setOnItemClickListener(listener: (SystemAvatarBean) -> Unit) {
        onItemClickListener = listener
    }
}