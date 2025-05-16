package arch.cayenne.module.betslip.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipConfirmBinding
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipInvalidBinding
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipReserveBinding
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipSettledBinding
import arch.cayenne.module.betslip.databinding.AdapterLiveBetSlipUnsettleBinding
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.ui.adapter.livebetslip.BetSlipBaseAdapterManager
import arch.cayenne.module.betslip.ui.compare.BetSlipCompare
import arch.cayenne.module.betslip.utisl.RecyclerItemListener
import galaxy.common.proto.Common
import galaxy.common.proto.Common.Order

class BetSlipAdapter(type: BetSlipEnum) :
    BaseAdapter<BetSlipData, BetSlipAdapter.LiveBetSlipViewHolder, ViewBinding>(BetSlipCompare()) {
    private val betSlipType = type
    private var earlySettleListener: RecyclerItemListener<BetSlipData>? = null
    private var cancelReserveListener: RecyclerItemListener<BetSlipData>? = null
    private var modifyReserveListener: RecyclerItemListener<BetSlipData>? = null

    fun setEarlySettleListener(listener: RecyclerItemListener<BetSlipData>) {
        this.earlySettleListener = listener
    }

    fun setReserveListener(
        cancelListener: RecyclerItemListener<BetSlipData>,
        modifyListener: RecyclerItemListener<BetSlipData>
    ) {
        this.cancelReserveListener = cancelListener
        this.modifyReserveListener = modifyListener
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ViewBinding {
        return when (betSlipType) {
            BetSlipEnum.UnSettled -> AdapterLiveBetSlipUnsettleBinding.inflate(
                inflater, parent, false
            )

            BetSlipEnum.Confirming -> AdapterLiveBetSlipConfirmBinding.inflate(
                inflater, parent, false
            )

            BetSlipEnum.Settled -> AdapterLiveBetSlipSettledBinding.inflate(
                inflater, parent, false
            )

            BetSlipEnum.Reserve -> AdapterLiveBetSlipReserveBinding.inflate(
                inflater, parent, false
            )

            BetSlipEnum.Invalid -> AdapterLiveBetSlipInvalidBinding.inflate(
                inflater, parent, false
            )
        }
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): LiveBetSlipViewHolder {
        val holder = LiveBetSlipViewHolder(binding)
        //监听投注项是否展开
        holder.manager?.createViewHolder()
        holder.initListener()
        return holder
    }


    override fun convertPlus(holder: LiveBetSlipViewHolder, binding: ViewBinding, position: Int) {
        holder.manager?.covertPlus(position, getItem(position))
    }

    private fun getSelections(order: Common.Order) = order.selectionsList

    private fun getMatch(selection: Common.OrderSelection) = selection.matchBasic

    private fun getEarlySettlePrice(order: Order) = order.earlySettlePrice

    inner class LiveBetSlipViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        val manager = BetSlipBaseAdapterManager.initManager(binding, betSlipType)

        fun initListener() {
            manager?.expandedListener = object : RecyclerItemListener<BetSlipExpandedEnum> {
                override fun onItemClick(item: BetSlipExpandedEnum?, position: Int) {
                    val status =
                        if (getItem(position).expandedEnum == BetSlipExpandedEnum.Fold) BetSlipExpandedEnum.Expanded else BetSlipExpandedEnum.Fold
                    currentList[position].expandedEnum = status
                    notifyItemChanged(position)
                }
            }
            manager?.earlySettleSubmitListener = object : RecyclerItemListener<String> {
                override fun onItemClick(item: String?, position: Int) {
                    earlySettleListener?.onItemClick(getItem(position), position)
                }
            }
            manager?.cancelReserveSubmitListener = object : RecyclerItemListener<String> {
                override fun onItemClick(item: String?, position: Int) {
                    cancelReserveListener?.onItemClick(getItem(position), position)
                }
            }
            manager?.reserveModifySubmitListener = object : RecyclerItemListener<String> {
                override fun onItemClick(item: String?, position: Int) {
                    modifyReserveListener?.onItemClick(getItem(position), position)
                }
            }
        }
    }

}