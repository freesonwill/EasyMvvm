package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.compare.LiveBetSlipCompare
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.data.model.LiveBetSlipData
import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import com.walisport.module.live.databinding.AdapterLiveBetSlipConfirmBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipInvalidBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipReserveBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipSettledBinding
import com.walisport.module.live.databinding.AdapterLiveBetSlipUnsettleBinding
import com.walisport.module.live.ui.adapter.livebetslip.LiveBetSlipBaseAdapterManager
import com.walisport.module.live.utils.LiveBetSlipAdapterMangerInterface
import com.walisport.module.live.utils.RecyclerItemListener
import galaxy.common.proto.Common
import galaxy.common.proto.Common.Order

class LiveBetSlipAdapter(type: LiveBetSlipEnum) :
    BaseAdapter<LiveBetSlipData, LiveBetSlipAdapter.LiveBetSlipViewHolder, ViewBinding>(
        LiveBetSlipCompare()
    ) {
    private val betSlipType = type
    private var earlySettleListener: RecyclerItemListener<LiveBetSlipData>? = null
    private var cancelReserveListener: RecyclerItemListener<LiveBetSlipData>? = null
    private var modifyReserveListener: RecyclerItemListener<LiveBetSlipData>? = null

    fun setEarlySettleListener(listener: RecyclerItemListener<LiveBetSlipData>) {
        this.earlySettleListener = listener
    }

    fun setReserveListener(
        cancelListener: RecyclerItemListener<LiveBetSlipData>,
        modifyListener: RecyclerItemListener<LiveBetSlipData>
    ) {
        this.cancelReserveListener = cancelListener
        this.modifyReserveListener = modifyListener
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (betSlipType) {
            LiveBetSlipEnum.UnSettled -> AdapterLiveBetSlipUnsettleBinding.inflate(
                inflater,
                parent,
                false
            )

            LiveBetSlipEnum.Confirming -> AdapterLiveBetSlipConfirmBinding.inflate(
                inflater,
                parent,
                false
            )

            LiveBetSlipEnum.Settled -> AdapterLiveBetSlipSettledBinding.inflate(
                inflater,
                parent,
                false
            )

            LiveBetSlipEnum.Reserve -> AdapterLiveBetSlipReserveBinding.inflate(
                inflater,
                parent,
                false
            )

            LiveBetSlipEnum.Invalid -> AdapterLiveBetSlipInvalidBinding.inflate(
                inflater,
                parent,
                false
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
        val manager = LiveBetSlipBaseAdapterManager.initManager(binding,betSlipType)

        fun initListener(){
            manager?.expandedListener = object : RecyclerItemListener<LiveBetSlipExpandedEnum> {
                override fun onItemClick(item: LiveBetSlipExpandedEnum?, position: Int) {
                    val status =
                        if (getItem(position).expandedEnum == LiveBetSlipExpandedEnum.Fold) LiveBetSlipExpandedEnum.Expanded else LiveBetSlipExpandedEnum.Fold
                    currentList[position].expandedEnum = status
                    notifyItemChanged(position)
                }
            }
            manager?.earlySettleSubmitListener = object :RecyclerItemListener<String>{
                override fun onItemClick(item: String?, position: Int) {
                    if (getItem(position).order?.earlySupport == false) {
                        return
                    }
                    earlySettleListener?.onItemClick(getItem(position), position)
                }
            }
            manager?.cancelReserveSubmitListener = object :RecyclerItemListener<String>{
                override fun onItemClick(item: String?, position: Int) {
                    cancelReserveListener?.onItemClick(getItem(position), position)
                }
            }
            manager?.reserveModifySubmitListener = object :RecyclerItemListener<String>{
                override fun onItemClick(item: String?, position: Int) {
                    modifyReserveListener?.onItemClick(getItem(position), position)
                }
            }
        }
    }

}