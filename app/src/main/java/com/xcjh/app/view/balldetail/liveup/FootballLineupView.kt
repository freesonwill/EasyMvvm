package com.xcjh.app.view.balldetail.liveup

import android.widget.LinearLayout
import android.widget.TextView
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.xcjh.app.R
import com.xcjh.app.bean.FootballLineupBean
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.databinding.ViewDetailFootballStatusBinding
import com.xcjh.app.databinding.ViewDetailGameLiveupBinding
import com.xcjh.app.utils.myDivide
import com.xcjh.base_lib.utils.view.visibleOrInvisible

/**
 * 首发阵容
 */
class FootballLineupView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs)  {

    private var binding: ViewDetailGameLiveupBinding
    init {
        binding= ViewDetailGameLiveupBinding.inflate(LayoutInflater.from(context),this,true)
    }


    @SuppressLint("SetTextI18n")
    fun setData(it: FootballLineupBean, match: MatchDetailBean) {
        binding.apply {
            if (!it.homeFormation.isNullOrEmpty()) {
                //有阵型排版
                lltShow.visibleOrInvisible(true)
                firstTable.visibleOrInvisible(false)
                tvMatchHomeLineup.text = "${resources.getString(R.string.competition_txt_formation)} ${it.homeFormation}" //阵型
                tvMatchHomeValue.text = getMarketValue(it.homeMarketValue)
                tvMatchAwayLineup.text = "${resources.getString(R.string.competition_txt_formation)} ${it.awayFormation}" //阵型
                tvMatchAwayValue.text = getMarketValue(it.awayMarketValue)
                lineUpMiddleView.setData(it)
            } else {
                //首发无阵型 直接展示列表
                lltShow.visibleOrInvisible(false)
                firstTable.visibleOrInvisible(true)
                firstTable.setData(it, 1)
            }
        }
    }

    /**
     * 获取球员身价
     */
    private fun getMarketValue(v: Int): String {
        val p: String = if (v == 0) {
            "-"
        } else if (v > 99999999) {
            // "${myDivide(v, 100000000, "0.000")}亿欧"
            "${myDivide(v, 100000000, "0.000")}".dropLastWhile { it =='0' }.dropLastWhile { it =='.' }+"${resources.getString(R.string.money_txt_billion)}"
        } else if (v > 9999) {
            // "${myDivide(v, 10000, "0.0")}万欧"
            "${myDivide(v, 10000, "0.0")}".dropLastWhile { it =='0' }.dropLastWhile { it =='.' }+"${resources.getString(R.string.money_txt_myriohm)}"
        } else {
            "${v}欧"
        }
        return p
    }



}