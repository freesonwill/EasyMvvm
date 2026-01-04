package arch.cayenne.module.account.ui.adapter

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.SystemAvatarBean
import arch.cayenne.module.account.data.model.PersonalInfoData
import arch.cayenne.module.account.databinding.ItemPersonalInfoBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

/**
 * @author: ricky.chang
 * @date: 2025/6/10 下午2:44
 * @description:
 */
class PersonalInfoItemViewHolder (private val mBinding: ItemPersonalInfoBinding
) : BaseViewHolder(mBinding) {
    fun bind(item: SystemAvatarBean, onClick: (SystemAvatarBean) -> Unit) {
        with(mBinding) {
            Glide.with(this.root.context)
                .load(item.host+item.url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivPersonalHead)
            ivPersonalHead.setOnClickListener {
                onClick(item)
            }
        }
    }
}