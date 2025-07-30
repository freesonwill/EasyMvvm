package arch.cayenne.module.home.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.home.databinding.ItemMatchCardBinding
import arch.cayenne.module.home.ui.adapter.compare.MatchItemCompare

class MatchItemAdapter(private val onMatchItemClickListener: OnMatchItemClickListener? = null) :
    BaseAdapter<MatchWithMarkets, MatchItemViewHolder, ItemMatchCardBinding>(MatchItemCompare()) {

    private val viewPool = RecyclerView.RecycledViewPool()
    private var showNoMoreData: Boolean = false

    override fun convertPlus(
        holder: MatchItemViewHolder,
        binding: ItemMatchCardBinding,
        position: Int
    ) {
        val item = getItem(position)
        holder.init(item)
        binding.root.setOnClickListener {
            onMatchItemClickListener?.onLiveEntryClick(getItem(holder.adapterPosition))
        }
        binding.ivFavorite.apply { addScaleOnTouchAnimation() }.setOnClickListener {
            onMatchItemClickListener?.onFavoriteClick(getItem(holder.adapterPosition))
        }
        if (position == itemCount - 1 && showNoMoreData) {
            binding.tvNoMoreData.visibility = View.VISIBLE
        } else {
            binding.tvNoMoreData.visibility = View.GONE
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemMatchCardBinding {
        return ItemMatchCardBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemMatchCardBinding,
        viewType: Int
    ): MatchItemViewHolder {
        return MatchItemViewHolder(binding, onMatchItemClickListener, viewPool)
    }

    override fun onBindViewHolder(
        holder: MatchItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val item = getItem(holder.adapterPosition)
            holder.bindPayload(item, payloads)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showNoMoreData(hasNoMore: Boolean) {
        showNoMoreData = hasNoMore
        notifyDataSetChanged()
    }
}

interface OnMatchItemClickListener {
    fun onLiveEntryClick(item: MatchWithMarkets)
    fun onFavoriteClick(item: MatchWithMarkets)
    fun onOddsCellClick(selection: SelectionBeanLite, x: Float, y: Float)
}