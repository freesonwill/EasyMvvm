package arch.cayenne.module.account.ui.adapter

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.account.data.model.PersonalInfoData
import arch.cayenne.module.account.databinding.ItemPersonalInfoBinding

/**
 * @author: ricky.chang
 * @date: 2025/6/10 下午2:44
 * @description:
 */
class PersonalInfoItemViewHolder (private val mBinding: ItemPersonalInfoBinding
) : BaseViewHolder(mBinding) {
    fun bind(item: PersonalInfoData, onClick: (PersonalInfoData) -> Unit) {
        with(mBinding) {
            ivPersonalHead.setImageResource(item.resId)
            ivPersonalHead.setOnClickListener {
                onClick(item)
            }
        }
    }
}