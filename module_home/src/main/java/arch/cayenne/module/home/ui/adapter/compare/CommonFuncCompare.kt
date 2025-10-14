package arch.cayenne.module.home.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.home.data.model.CommonFeaturesBean

class CommonFuncCompare : DiffUtil.ItemCallback<CommonFeaturesBean>() {
    override fun areItemsTheSame(oldItem: CommonFeaturesBean, newItem: CommonFeaturesBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CommonFeaturesBean, newItem: CommonFeaturesBean): Boolean {
        return oldItem == newItem
    }
}