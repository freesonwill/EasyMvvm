package arch.cayenne.module.hall.ui.adapter

import android.view.View
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.hall.data.GameSupplierListItem
import com.walisport.module.hall.databinding.ItemSupplierHeaderBinding
import com.walisport.module.hall.R
class GameSupplierHeaderViewHolder(
    private val mBinding: ItemSupplierHeaderBinding
) : BaseViewHolder(mBinding) {
    fun bind(item: GameSupplierListItem.Header) {
        with(mBinding) {
            if (item.letter == '*') {
                ivHeaderHot.visibility = View.VISIBLE
                tvHeaderName.text = getString(R.string.supplier_section_title_hot)
            } else {
                ivHeaderHot.visibility = View.GONE
                tvHeaderName.text = item.letter.toString()
            }
        }
    }
}