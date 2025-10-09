package arch.cayenne.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.module.home.data.model.CommonFeaturesBean
import arch.cayenne.module.home.databinding.ItemDrawerFeaturesBinding
import arch.cayenne.module.home.ui.adapter.compare.CommonFuncCompare
import arch.cayenne.module.home.ui.viewholder.CommonFeaturesViewHolder

class DrawerFeaturesAdapter(): BaseAdapter<CommonFeaturesBean, CommonFeaturesViewHolder, ItemDrawerFeaturesBinding>(
    CommonFuncCompare()
) {
    override fun convertPlus(holder: CommonFeaturesViewHolder, binding: ItemDrawerFeaturesBinding, position: Int) {
        val bean = getItem(position)
        holder.init(bean)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemDrawerFeaturesBinding {
        return ItemDrawerFeaturesBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemDrawerFeaturesBinding, viewType: Int): CommonFeaturesViewHolder {
        return CommonFeaturesViewHolder(binding)
    }

}