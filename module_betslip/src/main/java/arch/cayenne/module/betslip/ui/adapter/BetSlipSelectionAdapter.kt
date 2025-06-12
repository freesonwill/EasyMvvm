package arch.cayenne.module.betslip.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipConfirmBinding
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipInvalidBinding
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipReserveBinding
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipSettledBinding
import arch.cayenne.module.betslip.databinding.ItemLiveBetSlipUnsettleBinding
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.ui.compare.BetSlipSelectionCompare
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.module.betslip.ui.viewholder.item.BaseBetSlipItemViewHolder
import arch.cayenne.module.betslip.ui.viewholder.item.BetSlipConfirmItemViewHolder
import arch.cayenne.module.betslip.ui.viewholder.item.BetSlipInvalidItemViewHolder
import arch.cayenne.module.betslip.ui.viewholder.item.BetSlipReserveItemViewHolder
import arch.cayenne.module.betslip.ui.viewholder.item.BetSlipSettledItemViewHolder
import arch.cayenne.module.betslip.ui.viewholder.item.BetSlipUnsettledItemViewHolder

class BetSlipSelectionAdapter(
    betSlipType: BetSlipEnum
) : BaseAdapter<BetSlipSelectionData, BaseBetSlipItemViewHolder<*>, ViewBinding>(
    BetSlipSelectionCompare()
) {
    private val betType = betSlipType
    private var expandEnum: BetSlipExpandedEnum = BetSlipExpandedEnum.Hide
    private var parentPosition: Int = -1

    private var expandListener: RecyclerItemListener<BetSlipExpandedEnum>? = null
    private var liveListener: RecyclerItemListener<BetSlipSelectionData>? = null

    fun updateBasicData(flag: BetSlipExpandedEnum, parentPosition: Int) {
        this.expandEnum = flag
        this.parentPosition = parentPosition
    }

    override fun convertPlus(
        holder: BaseBetSlipItemViewHolder<*>,
        binding: ViewBinding,
        position: Int
    ) {
        holder.covertPlus(itemCount, expandEnum, getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ViewBinding {
        return when (betType) {
            BetSlipEnum.UnSettled -> ItemLiveBetSlipUnsettleBinding.inflate(inflater, parent, false)
            BetSlipEnum.Confirming -> ItemLiveBetSlipConfirmBinding.inflate(inflater, parent, false)
            BetSlipEnum.Settled -> ItemLiveBetSlipSettledBinding.inflate(inflater, parent, false)
            BetSlipEnum.Reserve -> ItemLiveBetSlipReserveBinding.inflate(inflater, parent, false)
            BetSlipEnum.Invalid -> ItemLiveBetSlipInvalidBinding.inflate(inflater, parent, false)
        }
    }

    override fun createViewHolder(
        binding: ViewBinding, viewType: Int
    ): BaseBetSlipItemViewHolder<*> {
        val holder = when (betType) {
            BetSlipEnum.UnSettled -> BetSlipUnsettledItemViewHolder(binding)
            BetSlipEnum.Confirming -> BetSlipConfirmItemViewHolder(binding)
            BetSlipEnum.Settled -> BetSlipSettledItemViewHolder(binding)
            BetSlipEnum.Reserve -> BetSlipReserveItemViewHolder(binding)
            BetSlipEnum.Invalid -> BetSlipInvalidItemViewHolder(binding)
        }
        holder.createViewHolder()
        holder.setExpandedListener(expandListener)
        holder.setLiveListener(liveListener)
        return holder
    }

    fun setLiveListener(listener: RecyclerItemListener<BetSlipSelectionData>?) {
        this.liveListener = listener
    }

    fun setExpandListener(listener: RecyclerItemListener<BetSlipExpandedEnum>?) {
        this.expandListener = listener
    }
}