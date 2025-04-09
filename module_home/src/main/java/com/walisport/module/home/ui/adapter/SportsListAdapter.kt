package com.walisport.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.walisport.module.home.databinding.ItemSportsBinding
import com.walisport.module.home.enums.SportType

class SportsListAdapter(
    private val sports: List<SportType>,
    private val onItemClick: (SportType) -> Unit
) : RecyclerView.Adapter<SportsListAdapter.SportViewHolder>() {

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
            if (root.isSelected) {
                tvSportTitle.setTextColor(context.getColor(com.walisport.lib.common.R.color.white))
                tvSportIcon.isSelected = true
            } else {
                tvSportTitle.setTextColor(context.getColor(com.walisport.lib.common.R.color.secondary_text))
                tvSportIcon.isSelected = false
            }
            root.setOnClickListener { onItemClick(sport) }
        }
    }

    override fun getItemCount(): Int = sports.size
}
