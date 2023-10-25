package com.xcjh.app.view.balldetail.result

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.xcjh.app.R
import com.xcjh.app.bean.BasketballSBean
import com.xcjh.app.utils.myDivide
import com.xcjh.app.view.ProgressBarView
import kotlin.math.roundToInt

class BasketballDataView : RelativeLayout {
    private lateinit var iv_icon_home: ImageView
    private lateinit var iv_icon_away: ImageView
    private lateinit var iv_name_home: TextView
    private lateinit var iv_name_away: TextView
    private lateinit var tv_home_two_point: TextView
    private lateinit var tv_home_three_point: TextView
    private lateinit var tv_home_penalty: TextView
    private lateinit var tv_away_two_point: TextView
    private lateinit var tv_away_three_point: TextView
    private lateinit var tv_away_penalty: TextView
    private lateinit var pro_two_point: ProgressBar
    private lateinit var pro_three_point: ProgressBar
    private lateinit var pro_penalty: ProgressBar

    private lateinit var pgTwoPercent: ProgressBarView
    private lateinit var view_game_status_left: TextView
    private lateinit var view_game_status_right: TextView

    private lateinit var pgThreePercent: ProgressBarView
    private lateinit var view_game_status_wx_left: TextView
    private lateinit var view_game_status_wx_right: TextView

    private lateinit var pgFq: ProgressBarView
    private lateinit var view_game_status_kql_left: TextView
    private lateinit var view_game_status_kql_right: TextView

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

    @SuppressLint("MissingInflatedId")
    fun initView(context: Context?) {
        val v = LayoutInflater.from(context).inflate(R.layout.view_basketball_data, this)
        iv_icon_home = v.findViewById(R.id.iv_icon_home)
        iv_icon_away = v.findViewById(R.id.iv_icon_away)
        iv_name_home = v.findViewById(R.id.iv_name_home)
        iv_name_away = v.findViewById(R.id.iv_name_away)

        //2f
        pgTwoPercent = v.findViewById(R.id.pgTwoPercent)
        view_game_status_left = v.findViewById(R.id.view_game_status_left)
        view_game_status_right = v.findViewById(R.id.view_game_status_right)

        //3f
        pgThreePercent = v.findViewById(R.id.pgThreePercent)
        view_game_status_wx_left = v.findViewById(R.id.view_game_status_wx_left)
        view_game_status_wx_right = v.findViewById(R.id.view_game_status_wx_right)

        //罚球
        pgFq = v.findViewById(R.id.pgFq)
        view_game_status_kql_left = v.findViewById(R.id.view_game_status_kql_left)
        view_game_status_kql_right = v.findViewById(R.id.view_game_status_kql_right)

        //2分球
        tv_home_two_point = v.findViewById(R.id.tv_home_two_point)
        tv_away_two_point = v.findViewById(R.id.tv_away_two_point)
        //3分球
        tv_home_three_point = v.findViewById(R.id.tv_home_three_point)
        tv_away_three_point = v.findViewById(R.id.tv_away_three_point)
        //罚球
        tv_home_penalty = v.findViewById(R.id.tv_home_penalty)
        tv_away_penalty = v.findViewById(R.id.tv_away_penalty)
        //主客占比
        pro_two_point = v.findViewById(R.id.pro_two_point)
        pro_three_point = v.findViewById(R.id.pro_three_point)
        pro_penalty = v.findViewById(R.id.pro_penalty)
    }

    fun setTitleBar(homeIcon: String?, homeName: String?, awayIcon: String?, awayName: String?) {
        Glide.with(this).load(homeIcon).placeholder(R.drawable.icon_team).into(iv_icon_home)
        Glide.with(this).load(awayIcon).placeholder(R.drawable.icon_team).into(iv_icon_away)
        iv_name_home.text = homeName
        iv_name_away.text = awayName
    }

    var homeV2 = 0 //2分球命中率 *100
    var awayV2 = 0
    var homeV3 = 0 //3分球命中率
    var awayV3 = 0
    var homeP = 0 //罚球命中率
    var awayP = 0

    @SuppressLint("SetTextI18n")
    fun setData(bean: BasketballSBean) {


        bean.home.apply {
            homeV2 = if (shot2 == 0) 0 else myDivide(hit2 * 100, shot2).roundToInt()
            homeV3 = if (shot3 == 0) 0 else myDivide(hit3 * 100, shot3).roundToInt()//hit3 * 100 / shot3
            homeP = if (penalty == 0) 0 else myDivide(penaltyHit * 100, penalty).roundToInt()

            view_game_status_left.text = "$homeV2"
            view_game_status_wx_left.text = "$homeV3"
            view_game_status_kql_left.text = "$homeP"

            tv_home_two_point.text = hit2.toString()
            tv_home_three_point.text = hit3.toString()
            tv_home_penalty.text = penaltyHit.toString()
        }
        bean.away.apply {
            awayV2 = if (shot2 == 0) 0 else myDivide(hit2 * 100, shot2).roundToInt()
            awayV3 = if (shot3 == 0) 0 else myDivide(hit3 * 100, shot3).roundToInt()
            awayP = if (penalty == 0) 0 else myDivide(penaltyHit * 100, penalty).roundToInt()

            view_game_status_right.text = "$awayV2"
            view_game_status_wx_right.text ="$awayV3"
            view_game_status_kql_right.text = "$awayP"

            tv_away_two_point.text = hit2.toString()
            tv_away_three_point.text = hit3.toString()
            tv_away_penalty.text = penaltyHit.toString()
        }
        //===============命中率===============
        if (homeV2 == 0 && awayV2 == 0) {
            pgTwoPercent.progress = 50
        } else {
            if (homeV2 == 0) {
                pgTwoPercent.progress = 0
            } else if (awayV2 == 0) {
                pgTwoPercent.progress = 100
            } else {
                pgTwoPercent.progress = homeV2* 100 / (homeV2 + awayV2)
            }
        }

        if (homeV3 == 0 && awayV3 == 0) {
            pgThreePercent.progress = 50
        } else {
            if (homeV3 == 0) {
                pgThreePercent.progress = 0
            } else if (awayV3 == 0) {
                pgThreePercent.progress = 100
            } else {
                pgThreePercent.progress = homeV3 * 100/ (homeV3 + awayV3)
            }
        }
        if (homeP == 0 && awayP == 0) {
            pgFq.progress = 50
        } else {
            if (homeP == 0) {
                pgFq.progress = 0
            } else if (awayP == 0) {
                pgFq.progress = 100
            } else {
                pgFq.progress = homeP* 100 / (homeP + awayP)
            }
        }
       ////=========比分========
        if (bean.home.hit2 == 0 && bean.away.hit2 == 0) {
            pro_two_point.progress = 50
        } else {
            if (bean.home.hit2 == 0) {
                pro_two_point.progress = 0
            } else if (bean.away.hit2 == 0) {
                pro_two_point.progress = 100
            } else {
                pro_two_point.progress = bean.home.hit2 * 100 / (bean.home.hit2 + bean.away.hit2)
            }
        }
        if (bean.home.hit3 == 0 && bean.away.hit3 == 0) {
            pro_two_point.progress = 50
        } else {
            if (bean.home.hit3 == 0) {
                pro_two_point.progress = 0
            } else if (bean.away.hit3 == 0) {
                pro_two_point.progress = 100
            } else {
                pro_three_point.progress = bean.home.hit3 * 100 / (bean.home.hit3 + bean.away.hit3)
            }
        }
        if (bean.home.penaltyHit == 0 && bean.away.penaltyHit == 0) {
            pro_two_point.progress = 50
        } else {
            if (bean.home.penaltyHit == 0) {
                pro_two_point.progress = 0
            } else if (bean.away.penaltyHit == 0) {
                pro_two_point.progress = 100
            } else {
                pro_penalty.progress =
                    bean.home.penaltyHit * 100 / (bean.home.penaltyHit + bean.away.penaltyHit)
            }
        }

    }
}