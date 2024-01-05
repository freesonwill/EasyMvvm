package com.xcjh.app.view.balldetail.result

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.bumptech.glide.Glide
import com.xcjh.app.R
import com.xcjh.app.bean.BasketballSBean
import com.xcjh.app.databinding.ViewBasketballDataBinding
import com.xcjh.app.databinding.ViewBasketballTableBinding
import com.xcjh.app.utils.myDivide
import com.xcjh.app.utils.setProgressValue
import com.xcjh.app.view.ProgressBarView
import kotlin.math.roundToInt

class BasketballDataView(context: Context, attributeSet: AttributeSet) : LinearLayoutCompat(context, attributeSet){

    private var binding: ViewBasketballDataBinding
    init {
        binding= ViewBasketballDataBinding.inflate(LayoutInflater.from(context),this,true)
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

        binding.apply {
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
            proTwoPoint.progress= getProgress(bean.home.hit2,bean.away.hit2)
            proThreePoint.progress= getProgress(bean.home.hit3,bean.away.hit3)
            proPenalty.progress= getProgress(bean.home.penaltyHit,bean.away.penaltyHit)
        }
    }

    /**
     * 客队分母
     */
    private fun getProgress(home:Int,away:Int):Int {
       return setProgressValue( if (home == away && home == 0) 50 else away * 100 / (home + away))
    }

}