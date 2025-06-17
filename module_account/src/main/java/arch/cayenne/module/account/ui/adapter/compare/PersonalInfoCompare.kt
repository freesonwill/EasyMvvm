package arch.cayenne.module.account.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.account.data.model.PersonalInfoData

/**
 * @author: ricky.chang
 * @date: 2025/6/13 下午4:08
 * @description:
 */
class PersonalInfoCompare : DiffUtil.ItemCallback<PersonalInfoData>() {
    override fun areItemsTheSame(oldItem: PersonalInfoData, newItem: PersonalInfoData): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: PersonalInfoData, newItem: PersonalInfoData): Boolean = oldItem == newItem
}