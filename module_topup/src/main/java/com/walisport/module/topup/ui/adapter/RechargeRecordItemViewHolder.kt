package com.walisport.module.topup.ui.adapter

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.RechargeRecordBean
import com.bumptech.glide.Glide
import com.walisport.module.topup.databinding.ItemRechargeRecordBinding

class RechargeRecordItemViewHolder(
    private val mBinding: ItemRechargeRecordBinding,
    private val onMatchItemClickListener: OnMatchItemClickListener?,
    private val viewPool: RecycledViewPool
) : BaseViewHolder(mBinding) {


    @SuppressLint("SetTextI18n")
    fun init(data: RechargeRecordBean) {
//        oddsColumnAdapter.onOddsClick = { selection, b ->
//            onMatchItemClickListener?.onOddsCellClick(data, selection)
//        }
        with(mBinding) {
            tvAmount.text = data.amount
        }
    }

    private fun setIconWithDefault(tournamentIcon: String, defaultIcon: Int, view: ImageView) {
        Glide.with(binding.root)
            .load(tournamentIcon.ifEmpty { defaultIcon })
            .placeholder(defaultIcon) // 載入中預設圖
            .error(defaultIcon)       // 載入失敗預設圖
            .into(view)
    }


}