package arch.cayenne.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.SportDataModel
import arch.cayenne.module.home.data.constants.SportType
import arch.cayenne.module.home.databinding.ItemSportsBinding
import arch.cayenne.module.home.ui.adapter.compare.SportDataModelCompare

class SportsListAdapter(
    private val onItemClick: (Int) -> Unit
) : BaseAdapter<SportDataModel, BaseViewHolder, ItemSportsBinding>(
    SportDataModelCompare()
) {

    private var selectedPosition = 0
    override fun convertPlus(holder: BaseViewHolder, binding: ItemSportsBinding, position: Int) {
        val sport = getItem(position)
        val sportType = SportType.fromId(sport.id) ?: SportType.Init
        val context = holder.itemView.context
        binding.apply {
            tvSportTitle.text = context.getString(sportType.titleResId)
            tvSportIcon.isEnabled = sport.matchCount > 0
            tvSportIcon.setImageResource(if (tvSportIcon.isEnabled) sportType.iconResActive else sportType.iconResInactive)

            // 依據選中狀態設定 UI
            root.isSelected = (holder.adapterPosition == selectedPosition)
            tvSportIcon.isSelected = root.isSelected

            // 設定點擊事件
            root.setOnClickListener {
                if (!tvSportIcon.isEnabled) return@setOnClickListener
                val oldPosition = selectedPosition
                selectedPosition = holder.adapterPosition

                // 更新舊選中項目（避免 UI 異常）
                if (oldPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(oldPosition)
                }
                notifyItemChanged(selectedPosition)

                onItemClick(sport.id)
            }
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemSportsBinding {
        return ItemSportsBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemSportsBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }
}
