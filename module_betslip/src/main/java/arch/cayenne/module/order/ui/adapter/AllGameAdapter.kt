package arch.cayenne.module.order.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemOrderGameBinding
import arch.cayenne.module.betslip.databinding.ItemOrderHeaderBinding
import arch.cayenne.module.order.data.model.OrderAllBean
import arch.cayenne.module.order.ui.compare.OrderAllCompare
import com.bumptech.glide.Glide

class AllGameAdapter : BaseAdapter<OrderAllBean, BaseViewHolder, ViewBinding>(
    OrderAllCompare()
) {
    private var listener: OnItemClickListener? = null

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_BODY = 1
    }

    override fun convertPlus(
        holder: BaseViewHolder, binding: ViewBinding, position: Int
    ) {
        val item = getItem(position)
        if (binding is ItemOrderGameBinding) {
            binding.tvGameName.text = item.gameName
            binding.tvGameSupport.text = item.supplier
            binding.tvGameTime.text = item.time
            binding.tvGameWin.text = item.win
            binding.tvGameBelong.text = "牛牛"
            binding.tvGameMultiple.text = "584 x"
            binding.tvGameBet.text = R.string.title_bet.getString() + "  " + item.bet
            Glide.with(binding.root.context).load(item.img).into(binding.ivGameLogo)
            binding.viewLine.isVisible = position != itemCount - 1
        } else if (binding is ItemOrderHeaderBinding) {

        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ViewBinding {
        if (viewType == TYPE_HEADER) {
            return ItemOrderHeaderBinding.inflate(inflater, parent, false)
        }
        return ItemOrderGameBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isHeader(position)) TYPE_HEADER else TYPE_BODY
    }

    private fun isHeader(position: Int): Boolean {
        val item = getItem(position)
        return item.isHeader
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.listener = onItemClickListener
    }

    fun interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}