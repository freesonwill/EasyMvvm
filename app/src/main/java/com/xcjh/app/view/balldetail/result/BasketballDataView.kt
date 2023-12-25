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
import com.xcjh.app.utils.setProgressValue
import com.xcjh.app.view.ProgressBarView
import kotlin.math.roundToInt

class BasketballDataView : RelativeLayout {
  /*  private lateinit var iv_icon_home: ImageView
    private lateinit var iv_icon_away: ImageView
    private lateinit var iv_name_home: TextView
    private lateinit var iv_name_away: TextView*/
    private lateinit var tvHit2Home: TextView
    private lateinit var tvHit3Home: TextView
    private lateinit var tvPenaltyHome: TextView
    private lateinit var tvHit2Away: TextView
    private lateinit var tvHit3Away: TextView
    private lateinit var tvPenaltyAway: TextView
    private lateinit var pro_two_point: ProgressBar
    private lateinit var pro_three_point: ProgressBar
    private lateinit var pro_penalty: ProgressBar

    private lateinit var pgTwoPercent: ProgressBarView
    private lateinit var tv2Home: TextView
    private lateinit var tv2Away: TextView

    private lateinit var pgThreePercent: ProgressBarView
    private lateinit var tv3Home: TextView
    private lateinit var tv3Away: TextView

    private lateinit var pgFqPercent: ProgressBarView
    private lateinit var tvFqHome: TextView
    private lateinit var tvFqAway: TextView

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
       /* iv_icon_home = v.findViewById(R.id.iv_icon_home)
        iv_icon_away = v.findViewById(R.id.iv_icon_away)
        iv_name_home = v.findViewById(R.id.iv_name_home)
        iv_name_away = v.findViewById(R.id.iv_name_away)*/

        //2f
        pgTwoPercent = v.findViewById(R.id.pgTwoPercent)
        tv2Home = v.findViewById(R.id.tv2Home)
        tv2Away = v.findViewById(R.id.tv2Away)

        //3f
        pgThreePercent = v.findViewById(R.id.pgThreePercent)
        tv3Home = v.findViewById(R.id.tv3Home)
        tv3Away = v.findViewById(R.id.tv3Away)

        //罚球
        pgFqPercent = v.findViewById(R.id.pgFqPercent)
        tvFqHome = v.findViewById(R.id.tvFqHome)
        tvFqAway = v.findViewById(R.id.tvFqAway)

        //2分球
        tvHit2Home = v.findViewById(R.id.tvHit2Home)
        tvHit2Away = v.findViewById(R.id.tvHit2Away)
        //3分球
        tvHit3Home = v.findViewById(R.id.tvHit3Home)
        tvHit3Away = v.findViewById(R.id.tvHit3Away)
        //罚球
        tvPenaltyHome = v.findViewById(R.id.tvPenaltyHome)
        tvPenaltyAway = v.findViewById(R.id.tvPenaltyAway)
        //主客占比
        pro_two_point = v.findViewById(R.id.pro_two_point)
        pro_three_point = v.findViewById(R.id.pro_three_point)
        pro_penalty = v.findViewById(R.id.pro_penalty)
    }

    fun setTitleBar(homeIcon: String?, homeName: String?, awayIcon: String?, awayName: String?) {
       /* Glide.with(this).load(homeIcon).placeholder(com.xcjh.base_lib.R.drawable.ic_default_bg).into(iv_icon_home)
        Glide.with(this).load(awayIcon).placeholder(com.xcjh.base_lib.R.drawable.ic_default_bg).into(iv_icon_away)
        iv_name_home.text = homeName?:""
        iv_name_away.text = awayName?:""*/
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
            homeV3 =
                if (shot3 == 0) 0 else myDivide(hit3 * 100, shot3).roundToInt()//hit3 * 100 / shot3
            homeP = if (penalty == 0) 0 else myDivide(penaltyHit * 100, penalty).roundToInt()

            tv2Home.text = "$homeV2"
            tv3Home.text = "$homeV3"
            tvFqHome.text = "$homeP"

            tvHit2Home.text = (2 * hit2).toString()
            tvHit3Home.text = (3 * hit3).toString()
            tvPenaltyHome.text = (1 * penaltyHit).toString()
        }
        bean.away.apply {
            awayV2 = if (shot2 == 0) 0 else myDivide(hit2 * 100, shot2).roundToInt()
            awayV3 = if (shot3 == 0) 0 else myDivide(hit3 * 100, shot3).roundToInt()
            awayP = if (penalty == 0) 0 else myDivide(penaltyHit * 100, penalty).roundToInt()

            tv2Away.text = "$awayV2"
            tv3Away.text = "$awayV3"
            tvFqAway.text = "$awayP"

            tvHit2Away.text = (2 * hit2).toString()
            tvHit3Away.text = (3 * hit3).toString()
            tvPenaltyAway.text = (1 * penaltyHit).toString()
        }
        //===============命中率===============
        pgTwoPercent.progress= getProgress(homeV2,awayV2)
        pgThreePercent.progress= getProgress(homeV3,awayV3)
        pgFqPercent.progress= getProgress(homeP,awayP)

        ////=========比分========
        pro_two_point.progress= getProgress(bean.home.hit2,bean.away.hit2)
        pro_three_point.progress= getProgress(bean.home.hit3,bean.away.hit3)
        pro_penalty.progress= getProgress(bean.home.penaltyHit,bean.away.penaltyHit)

    }

    /**
     * 客队分母
     */
    private fun getProgress(home:Int,away:Int):Int {
       return setProgressValue( if (home == away && home == 0) 50 else away * 100 / (home + away))
    }

}