package arch.cayenne.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.SportDataModel
import arch.cayenne.module.home.databinding.ItemSportsBinding
import arch.cayenne.module.home.enums.SportType

class SportsListAdapter(
    private val onItemClick: (SportDataModel) -> Unit
) : RecyclerView.Adapter<SportsListAdapter.SportViewHolder>() {
    private var selectedPosition = 0
    private var sports: List<SportDataModel>? = null
    class SportViewHolder(val binding: ItemSportsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportViewHolder {
        val binding = ItemSportsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SportViewHolder(binding)
    }

    fun setData(list: List<SportDataModel>) {
        sports = list
    }

    override fun onBindViewHolder(holder: SportViewHolder, position: Int) {
        if (sports.isNullOrEmpty()) return
        val sport = sports!![position]
        val sportType = SportType.fromId(sport.id)!!
        val context = holder.itemView.context
        holder.binding.apply {
            tvSportTitle.text = context.getString(sportType.titleResId)
            tvSportIcon.isEnabled = sport.matchCount > 0
            tvSportIcon.setImageResource(if (tvSportIcon.isEnabled) sportType.iconResActive else sportType.iconResInactive)

            tvSportTitle.setTextColor(ContextCompat.getColorStateList(context, R.color.selector_league_tab_tint))

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

                onItemClick(sport)
            }
        }
    }

    override fun getItemCount(): Int = sports?.size ?: 0
}
