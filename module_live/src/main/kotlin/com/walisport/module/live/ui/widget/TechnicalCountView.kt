package com.walisport.module.live.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.data.model.MatchTrendData
import com.walisport.module.live.databinding.ViewTechnicalStatisticsBinding

/**
 * 赛况页技术统计布局控件
 */

class TechnicalCountView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var clicklistener: OnClickListener? = null

    private var mBinding: ViewTechnicalStatisticsBinding =
        ViewTechnicalStatisticsBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        mBinding.viewGoalTrend.setOnClickListener{
            clicklistener?.onClick()
        }
    }

    //在全屏直播模式下，只需要展示技术统计的部分数据
    fun setFullScreenMode() {
        mBinding.layData.visibility = View.GONE
        mBinding.tvStatisticsTitle.visibility = View.GONE
        mBinding.root.background = null
    }

    fun setTeamInfo(homeName: String, awayName: String, homeLogo: String, awayLogo: String) {
        mBinding.tvHomeName.text = homeName
        mBinding.tvAwayName.text = awayName
        Glide.with(context).load(homeLogo).into(mBinding.ivHomeLogo)
        Glide.with(context).load(awayLogo).into(mBinding.ivAwayLogo)
    }


    fun setScore(sore: String) {
        mBinding.tvScore.text = sore
    }

    fun setTrendData(data: MatchTrendData) {
        mBinding.viewGoalTrend.setData(data)
    }

    //设置进攻数据
    fun setAttackData(home: Int, away: Int) {
        mBinding.tvAttackHome.text = home.toString()
        mBinding.tvAttackAway.text = away.toString()
        if (home > away) {
            mBinding.ivAttack.setBackgroundResource(R.drawable.bg_shape_stands_red_blue)
            mBinding.ivAttackArrow.setBackgroundResource(R.drawable.bg_shape_attack_right)
        } else if (home == away) {
            mBinding.ivAttack.setBackgroundResource(R.drawable.bg_shape_stands)
            mBinding.ivAttackArrow.setBackgroundDrawable(null)
        } else {
            mBinding.ivAttack.setBackgroundResource(R.drawable.bg_shape_stands_blue_red)
            mBinding.ivAttackArrow.setBackgroundResource(R.drawable.bg_shape_attack_left)
        }
    }

    //危险进攻数据
    fun setDangerAttackData(home: Int, away: Int) {
        mBinding.tvDangerAttackHome.text = home.toString()
        mBinding.tvDangerAttackAway.text = away.toString()
        if (home > away) {
            mBinding.ivDangerAttack.setBackgroundResource(R.drawable.bg_shape_stands_red_blue)
            mBinding.ivDangerAttackArrow.setBackgroundResource(R.drawable.bg_shape_danger_attack_left)
        } else if (home == away) {
            mBinding.ivDangerAttack.setBackgroundResource(R.drawable.bg_shape_stands)
            mBinding.ivDangerAttackArrow.setBackgroundDrawable(null)
        } else {
            mBinding.ivDangerAttack.setBackgroundResource(R.drawable.bg_shape_stands_blue_red)
            mBinding.ivDangerAttackArrow.setBackgroundResource(R.drawable.bg_shape_danger_attack_left)
        }
    }

    //红蓝双方角球、红牌、黄牌数据
    fun setHomeAwayData(
        homeSm: Int,
        homeRedCard: Int,
        homeYellowCard: Int,
        awaySm: Int,
        awayRedCard: Int,
        awayYellowCard: Int
    ) {
        mBinding.tvTechAttackHome.text = homeSm.toString()
        mBinding.tvTechRedCardHome.text = homeRedCard.toString()
        mBinding.tvTechYellowCardHome.text = homeYellowCard.toString()
        mBinding.tvTechAttackAway.text = awaySm.toString()
        mBinding.tvTechRedCardAway.text = awayRedCard.toString()
        mBinding.tvTechYellowCardAway.text = awayYellowCard.toString()
    }

    //控球率数据
    fun setBallControlData(home: Int, away: Int) {
        mBinding.tvBallControlHome.text = home.toString()
        mBinding.tvBallControlAway.text = away.toString()
        if (home > away) {
            mBinding.ivBallControlRate.setBackgroundResource(R.drawable.bg_shape_stands_red_blue)
            mBinding.ivDangerAttackArrow.setBackgroundResource(R.drawable.bg_shape_danger_attack_left)
        } else if (home == away) {
            mBinding.ivBallControlRate.setBackgroundResource(R.drawable.bg_shape_stands)
            mBinding.ivDangerAttackArrow.setBackgroundDrawable(null)
        } else {
            mBinding.ivBallControlRate.setBackgroundResource(R.drawable.bg_shape_stands_blue_red)
            mBinding.ivDangerAttackArrow.setBackgroundResource(R.drawable.bg_shape_danger_attack_left)
        }
    }

    fun setProgressData() {
        mBinding.techProKql.setData("控球率", 35, 20)
        mBinding.techProFiveKql.setData("五分钟控球率", 35, 20)
        mBinding.techProSm.setData("射门数", 35, 20)
        mBinding.techProSzs.setData("射正数", 12, 13)
        mBinding.techProCqs.setData("传球数", 11, 13)
        mBinding.techProCgl.setData("传球成功率", 10, 15)
        mBinding.techProRyq.setData("任意球", 24, 23)
        mBinding.techProJiao.setData("角球", 22, 13)
        mBinding.techProYw.setData("越位", 14, 12)
        mBinding.techProFg.setData("犯规", 15, 22)
    }

    fun setOnItemClickListener(listener: OnClickListener) {
        this.clicklistener = listener
    }

    interface OnClickListener {
        fun onClick()
    }
}