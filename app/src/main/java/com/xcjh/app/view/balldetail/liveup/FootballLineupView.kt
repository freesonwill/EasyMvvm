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
import com.xcjh.app.utils.myDivide
import com.xcjh.base_lib.utils.view.visibleOrInvisible

/**
 * 首发阵容
 */
class FootballLineupView : LinearLayout {
    private var tv_home_lineup: TextView? = null
    private var tv_home_value: TextView? = null
    private var tv_away_lineup: TextView? = null
    private var tv_away_value: TextView? = null
    private var lineUpMiddleView: FootballLiveUpMiddleView? = null
    private var lltShow: RelativeLayout? = null
    private var firstTable: FootballLineupList? = null

    constructor(context: Context?) : super(context) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context)
    }

    @SuppressLint("MissingInflatedId")
    fun initView(context: Context?) {
        val v = LayoutInflater.from(getContext()).inflate(R.layout.view_detail_game_liveup, this)
        lltShow = v.findViewById(R.id.lltShow)
        firstTable = v.findViewById(R.id.firstTable)
        tv_home_lineup = v.findViewById(R.id.tv_match_home_lineup)
        tv_home_value = v.findViewById(R.id.tv_match_home_value)
        tv_away_lineup = v.findViewById(R.id.tv_match_away_lineup)
        tv_away_value = v.findViewById(R.id.tv_match_away_value)
        lineUpMiddleView = v.findViewById(R.id.lineUpMiddleView)
    }

    @SuppressLint("SetTextI18n")
    fun setData(it: FootballLineupBean, match: MatchDetailBean) {
        if (!it.homeFormation.isNullOrEmpty()) {
            //有阵型排版
            lltShow?.visibleOrInvisible(true)
            firstTable?.visibleOrInvisible(false)
            tv_home_lineup?.text = "阵型 ${it.homeFormation}" //阵型
            tv_home_value?.text = getMarketValue(it.homeMarketValue)

            tv_away_lineup?.text = "阵型 ${it.awayFormation}" //阵型
            tv_away_value?.text = getMarketValue(it.awayMarketValue)
            lineUpMiddleView?.setData(it)
        } else {
            //首发无阵型 直接展示列表
            lltShow?.visibleOrInvisible(false)
            firstTable?.visibleOrInvisible(true)
            firstTable?.setData(it, 1)
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
            "${myDivide(v, 100000000, "0.000")}".dropLastWhile { it =='0' }.dropLastWhile { it =='.' }+"亿欧"
        } else if (v > 9999) {
            // "${myDivide(v, 10000, "0.0")}万欧"
            "${myDivide(v, 10000, "0.0")}".dropLastWhile { it =='0' }.dropLastWhile { it =='.' }+"万欧"
        } else {
            "${v}欧"
        }
        return p
    }



}