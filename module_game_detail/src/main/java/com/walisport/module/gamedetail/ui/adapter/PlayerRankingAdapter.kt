package com.walisport.module.gamedetail.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.gamedetail.R
import com.walisport.module.gamedetail.data.model.PlayerRankingBean
import com.walisport.module.gamedetail.databinding.ItemPlayerRankingBinding
import com.walisport.module.gamedetail.ui.compare.PlayerRankingCompare
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class PlayerRankingAdapter: BaseAdapter<PlayerRankingBean, BaseViewHolder, ViewBinding>(PlayerRankingCompare()) {

    private var locale: Locale = Locale.getDefault()

    @SuppressLint("SetTextI18n")
    override fun convertPlus(holder: BaseViewHolder, binding: ViewBinding, position: Int) {
        val mBinding = binding as ItemPlayerRankingBinding
        val mData = getItem(position)

        with(mBinding) {
            ivRankingIcon.apply {
                visibility = if(position < 3) ViewGroup.VISIBLE else ViewGroup.GONE
                when(position) {
                    0 -> setImageResource(R.drawable.ic_ranking_1)
                    1 -> setImageResource(R.drawable.ic_ranking_2)
                    2 -> setImageResource(R.drawable.ic_ranking_3)
                }
            }
            tvRanking.apply {
                visibility = if(position < 3) ViewGroup.GONE else ViewGroup.VISIBLE
                text = (position + 1).toString()
            }
            tvPlayerName.text = mData.name
            tvTimestamp.text = formatTimestamp(root.context, mData.timestamp)
            //todo currency enum 待確認
            ivBetSymbol.visibility = View.GONE
            ivWinningSymbol.visibility = View.GONE
            tvBetValue.text = String.format(Locale.getDefault(), "%.2f", mData.bet.toDouble())
            tvMultiplierValue.text = "${mData.multiple}x"
            tvWinningsValue.text = String.format(Locale.getDefault(), "%.2f", mData.bonus.toDouble())
            viewDivider.visibility = if (position == itemCount - 1) ViewGroup.GONE else ViewGroup.VISIBLE
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return ItemPlayerRankingBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    private fun formatTimestamp(context: Context, timestamp: Long): String {
        val now = LocalDate.now()
        val yesterday = now.minusDays(1)
        val inputDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        val inputDate = inputDateTime.toLocalDate()

        return when {
            // 規則 1: 今天 -> xx时xx分xx秒 (例如 12:59:36)
            inputDate.isEqual(now) -> {
                inputDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            }
            // 規則 2: 昨天 -> 昨天 xx时xx分xx秒 (例如 昨天 12:59:36)
            inputDate.isEqual(yesterday) -> {
                val str = SkinnableResourceManager.getString(context, R.string.yesterday, locale)
                "$str " + inputDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            }
            // 規則 4: 去年或更早 -> xxxx年xx月xx日 (例如 2014-07-15)
            inputDate.year < now.year -> {
                inputDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }
            // 規則 3: 昨天之前 (但仍在今年) -> xx月xx日xx时xx分xx秒 (例如 07-15 12:59:36)
            else -> {
                inputDateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss"))
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateLanguage(locale: Locale) {
        this.locale = locale
        notifyDataSetChanged()
    }
}