package arch.cayenne.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.module.home.data.model.CommonFeaturesBean
import arch.cayenne.module.home.databinding.ItemCommonFeaturesBinding
import arch.cayenne.module.home.ui.adapter.compare.CommonFuncCompare
import arch.cayenne.module.home.ui.viewholder.CommonFeaturesViewHolder

class CommonFeaturesAdapter(): BaseAdapter<CommonFeaturesBean, CommonFeaturesViewHolder, ItemCommonFeaturesBinding>(
    CommonFuncCompare()
) {
    override fun convertPlus(holder: CommonFeaturesViewHolder, binding: ItemCommonFeaturesBinding, position: Int) {
        val bean = getItem(position)
        holder.init(bean)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemCommonFeaturesBinding {
        return ItemCommonFeaturesBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemCommonFeaturesBinding, viewType: Int): CommonFeaturesViewHolder {
        return CommonFeaturesViewHolder(binding)
    }

}