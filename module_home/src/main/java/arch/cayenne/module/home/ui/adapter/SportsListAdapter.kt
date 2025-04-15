package arch.cayenne.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.ItemSportsBinding
import arch.cayenne.module.home.enums.SportType

class SportsListAdapter(
    private val sports: List<SportType>,
    private val onItemClick: (SportType) -> Unit
) : RecyclerView.Adapter<SportsListAdapter.SportViewHolder>() {
    private var selectedPosition = 0

    class SportViewHolder(val binding: ItemSportsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportViewHolder {
        val binding = ItemSportsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SportViewHolder, position: Int) {
        val sport = sports[position]
        val context = holder.itemView.context
        holder.binding.apply {
            tvSportTitle.text = context.getString(sport.titleResId)
            tvSportIcon.setImageResource(if (tvSportIcon.isEnabled) sport.iconResActive else sport.iconResInactive)
//            if (root.isSelected) {
//                tvSportTitle.setTextColor(context.getColor(R.color.main_text))
//                tvSportIcon.isSelected = true
//            } else {
//                tvSportTitle.setTextColor(context.getColor(R.color.secondary_text))
//                tvSportIcon.isSelected = false
//            }
            tvSportTitle.setTextColor(ContextCompat.getColorStateList(context, R.color.selector_league_tab_tint))

            // 依據選中狀態設定 UI
            root.isSelected = (holder.adapterPosition == selectedPosition)
            tvSportIcon.isSelected = root.isSelected

            // 設定點擊事件
            root.setOnClickListener {
                val oldPosition = selectedPosition
                selectedPosition = holder.adapterPosition

                // 更新舊選中項目（避免 UI 異常）
                if (oldPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(oldPosition)
                }
                notifyItemChanged(selectedPosition)

                onItemClick(sport)
            }
        }
    }

    override fun getItemCount(): Int = sports.size
}
