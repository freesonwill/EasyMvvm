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
import arch.cayenne.module.betslip.data.constants.LiveBetSlipEnum
import arch.cayenne.module.betslip.data.constants.LiveBetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.LiveBetSlipSelectionData
import arch.cayenne.module.betslip.ui.compare.LiveBetSlipSelectionCompare
import arch.cayenne.module.betslip.utisl.RecyclerItemListener

class LiveBetSlipSelectionAdapter(
    betSlipType: LiveBetSlipEnum,
    val expandListener: RecyclerItemListener<LiveBetSlipExpandedEnum>? = null
) :
    BaseAdapter<LiveBetSlipSelectionData, LiveBetSlipSelectionAdapter.LiveBetSlipSelectionViewHolder, ViewBinding>(
        LiveBetSlipSelectionCompare()
    ) {
    private val betType = betSlipType
    private var expandEnum: LiveBetSlipExpandedEnum = LiveBetSlipExpandedEnum.Hide
    private var parentPosition: Int = -1

    fun updateBasicData(flag: LiveBetSlipExpandedEnum, parentPosition: Int) {
        this.expandEnum = flag
        this.parentPosition = parentPosition
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ViewBinding {
        return when (betType) {
           LiveBetSlipEnum.UnSettled -> ItemLiveBetSlipUnsettleBinding.inflate(
                inflater, parent, false
            )

           LiveBetSlipEnum.Confirming -> ItemLiveBetSlipConfirmBinding.inflate(
                inflater, parent, false
            )

           LiveBetSlipEnum.Settled -> ItemLiveBetSlipSettledBinding.inflate(
                inflater, parent, false
            )

           LiveBetSlipEnum.Reserve -> ItemLiveBetSlipReserveBinding.inflate(
                inflater, parent, false
            )

           LiveBetSlipEnum.Invalid -> ItemLiveBetSlipInvalidBinding.inflate(
                inflater, parent, false
            )
        }

    }

    override fun createViewHolder(
        binding: ViewBinding, viewType: Int
    ): LiveBetSlipSelectionViewHolder {
        val holder = LiveBetSlipSelectionViewHolder(binding)
        holder.manager?.createViewHolder()
        holder.manager?.expandedListener = object : RecyclerItemListener<LiveBetSlipExpandedEnum> {
            override fun onItemClick(item: LiveBetSlipExpandedEnum?, position: Int) {
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
            arch.cayenne.module.betslip.ui.adapter.livebetslip.item.LiveBetSlipBaseItemManager.initManager(
                binding,
                betType
            )
    }


}