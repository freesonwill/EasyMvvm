package arch.cayenne.module.order.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemOrderGameBinding
import arch.cayenne.module.betslip.databinding.ItemOrderHeaderBinding
import arch.cayenne.module.order.data.model.OrderAllBean
import arch.cayenne.module.order.data.model.RecordsBean
import arch.cayenne.module.order.ui.compare.OrderAllCompare
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class AllGameAdapter : BaseAdapter<OrderAllBean, BaseViewHolder, ViewBinding>(
    OrderAllCompare()
) {
    private var itemListener: RecyclerItemListener<OrderAllBean>? = null

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_BODY = 1
    }

    override fun convertPlus(
        holder: BaseViewHolder, binding: ViewBinding, position: Int
    ) {
        val item = getItem(position)
        if (binding is ItemOrderGameBinding) {
            binding.main.tag = position
            binding.tvGameName.text = item.gameName
            binding.tvGameSupport.text = item.supplier
            binding.tvGameTime.text = item.time
            binding.tvGameWin.text = item.win
            binding.tvGameBelong.text = "牛牛"
            binding.tvGameMultiple.text = "584 x"
            binding.tvGameBet.text = R.string.title_bet.getString() + "  " + item.bet
            loadImage(binding.root.context, item.img, binding.ivGameLogo)
            binding.viewLine.isVisible = position != itemCount - 1
        } else if (binding is ItemOrderHeaderBinding) {
            binding.tvDate.text = item.gameName
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
        if(binding is ItemOrderGameBinding){
            binding.main.setOnClickListener {
                val position = it.tag as Int
                itemListener?.onItemClick(currentList[position],position)
            }
        }

        return BaseViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isHeader(position)) TYPE_HEADER else TYPE_BODY
    }

    private fun isHeader(position: Int): Boolean {
        val item = getItem(position)
        return item.isHeader
    }

    fun setOnItemClickListener(onItemClickListener: RecyclerItemListener<OrderAllBean>) {
        this.itemListener = onItemClickListener
    }

    private fun loadImage(context: Context, url: String, image: ImageView) {
        val options = RequestOptions().transforms(RoundedCorners(30.dp2px))
        Glide.with(context).load(url).apply(options).into(image)
    }

    fun interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}