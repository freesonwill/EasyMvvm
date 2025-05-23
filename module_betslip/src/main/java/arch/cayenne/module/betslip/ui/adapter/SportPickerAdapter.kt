package arch.cayenne.module.betslip.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.betslip.data.model.SportFilterBean
import arch.cayenne.module.betslip.databinding.ItemSportBinding
import arch.cayenne.module.betslip.ui.compare.SportPickerCompare

class SportPickerAdapter(private val listener: SportPickerListener) : BaseAdapter<SportFilterBean, BaseViewHolder, ItemSportBinding>(
    SportPickerCompare()
) {
    override fun convertPlus(holder: BaseViewHolder, binding: ItemSportBinding, position: Int) {
        val bean = getItem(position)
        binding.root.isSelected = bean.isSelected
        binding.groupArrow.isVisible = bean.isSelected
        binding.tvTitle.text = bean.sportName
        val titleColor = if (bean.isSelected) {
            Color.parseColor("#00D271")
        } else {
            ContextCompat.getColor(
                holder.itemView.context,
                arch.cayenne.lib.common.R.color.secondary_text
            )
        }
        binding.tvTitle.setTextColor(titleColor)
        binding.root.setOnClickListener {
            listener.onSportSelected(bean.sportId)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemSportBinding {
        return ItemSportBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemSportBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    interface SportPickerListener {
        fun onSportSelected(id: Int)
    }
}