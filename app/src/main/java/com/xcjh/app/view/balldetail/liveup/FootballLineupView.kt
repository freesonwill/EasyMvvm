package com.xcjh.app.view.balldetail.liveup

import android.widget.LinearLayout
import android.widget.TextView
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.xcjh.app.R
import com.xcjh.app.bean.FootballLineupBean
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.utils.myDivide
import com.xcjh.base_lib.utils.view.visibleOrInvisible

class FootballLineupView : LinearLayout {
    private var tv_home_lineup: TextView? = null
    private var tv_home_value: TextView? = null
    private var tv_away_lineup: TextView? = null
    private var tv_away_value: TextView? = null
    private var lineUpMiddleView: FootballLiveUpMiddleView? = null
    private var lltShow: LinearLayout? = null
    private var lltHide: LinearLayout? = null
    private var firstTable: FootballLineupList? = null

    constructor(context: Context?) : super(context) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
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
        lltHide = v.findViewById(R.id.lltHide)
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
            lltShow?.visibleOrInvisible(true)
            lltHide?.visibleOrInvisible(false)
            tv_home_lineup?.text = "阵型：${it.homeFormation}" //阵型
            tv_home_value?.text =
                "${myDivide(it.awayMarketValue,10000).toInt() }万" + if (it.homeMarketValueCurrency.isNullOrEmpty()) "欧" else {
                    it.homeMarketValueCurrency
                } //身价
            tv_away_lineup?.text = "阵型：${it.homeFormation}" //阵型
            tv_away_value?.text =
                "${myDivide(it.awayMarketValue,10000).toInt() }万"+ if (it.awayMarketValueCurrency.isNullOrEmpty()) "欧" else {
                    it.homeMarketValueCurrency
                }//身价
            lineUpMiddleView?.setData(it)
        } else {
            lltShow?.visibleOrInvisible(false)
            lltHide?.visibleOrInvisible(true)
            firstTable?.setData(it, match, 1)
        }
    }

}