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
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.ui.adapter.livebetslip.BetSlipBaseAdapterManager
import arch.cayenne.module.betslip.ui.compare.BetSlipCompare
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.module.betslip.data.model.BetSlipOrder
import arch.cayenne.module.betslip.data.model.BetSlipReserve

class BetSlipAdapter(type: BetSlipEnum) :
    BaseAdapter<BetSlipData, BetSlipAdapter.LiveBetSlipViewHolder, ViewBinding>(BetSlipCompare()) {
    private val betSlipType = type
    private var earlySettleListener: RecyclerItemListener<BetSlipOrder>? = null
    private var cancelReserveListener: RecyclerItemListener<BetSlipReserve>? = null
    private var modifyReserveListener: RecyclerItemListener<BetSlipReserve>? = null
    private var liveListener: RecyclerItemListener<BetSlipSelectionData>? = null

    fun setEarlySettleListener(listener: RecyclerItemListener<BetSlipOrder>) {
        this.earlySettleListener = listener
    }

    fun setReserveListener(
        cancelListener: RecyclerItemListener<BetSlipReserve>,
        modifyListener: RecyclerItemListener<BetSlipReserve>
    ) {
        this.cancelReserveListener = cancelListener
        this.modifyReserveListener = modifyListener
    }

    fun setLiveListener(liveListener: RecyclerItemListener<BetSlipSelectionData>) {
        this.liveListener = liveListener
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
        val data = getItem(position)
        holder.manager?.covertPlus(position, data)
    }

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
                    earlySettleListener?.onItemClick(getItem(position) as BetSlipOrder, position)
                }
            }
            manager?.cancelReserveSubmitListener = object : RecyclerItemListener<String> {
                override fun onItemClick(item: String?, position: Int) {
                    cancelReserveListener?.onItemClick(getItem(position) as BetSlipReserve, position)
                }
            }
            manager?.reserveModifySubmitListener = object : RecyclerItemListener<String> {
                override fun onItemClick(item: String?, position: Int) {
                    modifyReserveListener?.onItemClick(getItem(position) as BetSlipReserve, position)
                }
            }
        }
    }

}