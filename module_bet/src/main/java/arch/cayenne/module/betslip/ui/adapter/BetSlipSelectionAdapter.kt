package arch.cayenne.module.betslip.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.bet.databinding.ItemLiveBetSlipConfirmBinding
import arch.cayenne.module.bet.databinding.ItemLiveBetSlipInvalidBinding
import arch.cayenne.module.bet.databinding.ItemLiveBetSlipReserveBinding
import arch.cayenne.module.bet.databinding.ItemLiveBetSlipSettledBinding
import arch.cayenne.module.bet.databinding.ItemLiveBetSlipUnsettleBinding
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.LiveBetSlipSelectionData
import arch.cayenne.module.betslip.ui.compare.BetSlipSelectionCompare
import arch.cayenne.module.betslip.utisl.RecyclerItemListener

class BetSlipSelectionAdapter(
    betSlipType: BetSlipEnum,
    val expandListener: RecyclerItemListener<BetSlipExpandedEnum>? = null
) :
    BaseAdapter<LiveBetSlipSelectionData, BetSlipSelectionAdapter.LiveBetSlipSelectionViewHolder, ViewBinding>(
        BetSlipSelectionCompare()
    ) {
    private val betType = betSlipType
    private var expandEnum: BetSlipExpandedEnum = BetSlipExpandedEnum.Hide
    private var parentPosition: Int = -1

    fun updateBasicData(flag: BetSlipExpandedEnum, parentPosition: Int) {
        this.expandEnum = flag
        this.parentPosition = parentPosition
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ViewBinding {
        return when (betType) {
           BetSlipEnum.UnSettled -> ItemLiveBetSlipUnsettleBinding.inflate(
                inflater, parent, false
            )

           BetSlipEnum.Confirming -> ItemLiveBetSlipConfirmBinding.inflate(
                inflater, parent, false
            )

           BetSlipEnum.Settled -> ItemLiveBetSlipSettledBinding.inflate(
                inflater, parent, false
            )

           BetSlipEnum.Reserve -> ItemLiveBetSlipReserveBinding.inflate(
                inflater, parent, false
            )

           BetSlipEnum.Invalid -> ItemLiveBetSlipInvalidBinding.inflate(
                inflater, parent, false
            )
        }

    }

    override fun createViewHolder(
        binding: ViewBinding, viewType: Int
    ): LiveBetSlipSelectionViewHolder {
        val holder = LiveBetSlipSelectionViewHolder(binding)
        holder.manager?.createViewHolder()
        holder.manager?.expandedListener = object : RecyclerItemListener<BetSlipExpandedEnum> {
            override fun onItemClick(item: BetSlipExpandedEnum?, position: Int) {
                expandListener?.onItemClick(expandEnum, parentPosition)
            }
        }
        return holder
    }

    override fun convertPlus(
        holder: LiveBetSlipSelectionViewHolder, binding: ViewBinding, position: Int
    ) {
        holder.manager?.covertPlus(position, itemCount, expandEnum, getItem(position))
    }


    inner class LiveBetSlipSelectionViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        val manager =
            arch.cayenne.module.betslip.ui.adapter.livebetslip.item.BetSlipBaseItemManager.initManager(
                binding,
                betType
            )
    }


}