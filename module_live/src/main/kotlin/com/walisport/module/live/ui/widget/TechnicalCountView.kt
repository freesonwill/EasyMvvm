package com.walisport.module.live.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import arch.cayenne.lib.skin.res.SportSkinResourceManager
import arch.cayenne.lib.skin.widget.SportLinearLayout
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.data.EventEnum
import com.walisport.module.live.data.model.MatchHalfTeamStats
import com.walisport.module.live.data.model.MatchTrendData
import com.walisport.module.live.databinding.ViewTechnicalStatisticsBinding

/**
 * 赛况页技术统计布局控件
 */

class TechnicalCountView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SportLinearLayout(context, attrs, defStyleAttr) {

    private var clicklistener: OnClickListener? = null

    private var mBinding: ViewTechnicalStatisticsBinding =
        ViewTechnicalStatisticsBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        mBinding.viewGoalTrend.setOnClickListener {
            clicklistener?.onClick()
        }
    }

    //全屏直播模式下的技术统计控件
    fun setFullScreenMode() {
        mBinding.tvStatisticsTitle.visibility = View.GONE
        mBinding.root.background = null
    }

    //设置比赛双方名称和LOGO
    fun setTeamInfo(homeName: String, awayName: String, homeLogo: String, awayLogo: String) {
        mBinding.tvHomeName.text = homeName
        mBinding.tvAwayName.text = awayName
        Glide.with(context).load(homeLogo).into(mBinding.ivHomeLogo)
        Glide.with(context).load(awayLogo).into(mBinding.ivAwayLogo)
    }

    //设置比赛双方进球比分
    fun setScore(sore: String) {
        mBinding.tvScore.text = sore
    }

    //设置比赛趋势蜡烛图数据
    fun setTrendData(data: MatchTrendData) {
        mBinding.viewGoalTrend.setData(data)
    }

    //设置进攻数据
    fun setAttackData(home: Int, away: Int) {
        mBinding.tvAttackHome.text = home.toString()
        mBinding.tvAttackAway.text = away.toString()
        mBinding.ivAttack.background = getAttackBackground(home, away)
    }

    //设置危险进攻数据
    fun setDangerAttackData(home: Int, away: Int) {
        mBinding.tvDangerAttackHome.text = home.toString()
        mBinding.tvDangerAttackAway.text = away.toString()
        mBinding.ivDangerAttack.background = getDangerAttackBackground(home, away)
    }

    //设置控球率数据
    fun setBallControlData(home: Int, away: Int) {
        mBinding.tvBallControlHome.text = home.toString()
        mBinding.tvBallControlAway.text = away.toString()
        mBinding.ivBallControlRate.background = getBallControlBackground(home, away)
    }

    //设置比赛双方角球数据
    fun setCornerBallData(home: Int, away: Int) {
        mBinding.tvCornerHome.text = home.toString()
        mBinding.tvCornerAway.text = away.toString()
    }

    //设置比赛双方红牌数据
    fun setRedCardData(home: Int, away: Int) {
        mBinding.tvHomeRedCard.text = home.toString()
        mBinding.tvAwayRedCard.text = away.toString()
    }

    //设置比赛双方黄牌数据
    fun setYellowCardData(home: Int, away: Int) {
        mBinding.tvHomeYellowCard.text = home.toString()
        mBinding.tvAwayYellowCard.text = away.toString()
    }

    //设置比赛双方各项技术统计数据
    fun setMatchData(data: List<MatchHalfTeamStats>) {
        mBinding.layData.removeAllViews()
        for (i in data.indices) {
            val item = data[i]
            val progress = TechProgressView(context, null, 0)
            when (item.type) {
                EventEnum.EVENT_BALL_CONTROL.type -> {
                    progress.setData(
                        context.getString(R.string.statistics_kql),
                        item.homeNum,
                        item.awayNum,
                        true
                    )
                }

                EventEnum.EVENT_PASS_SUC.type -> {
                    val total = (item.homeNum + item.awayNum).toFloat()
                    val left = (item.homeNum / total) * 100f
                    val right = (item.awayNum / total) * 100f
                    progress.setData(
                        context.getString(R.string.statistics_cqcgl),
                        left.toInt(),
                        right.toInt(),
                        true
                    )
                }

                EventEnum.EVENT_SHOOT.type -> {
                    progress.setData(
                        context.getString(R.string.statistics_sms),
                        item.homeNum,
                        item.awayNum,
                        false
                    )
                }

                EventEnum.EVENT_SHOOT_SUC.type -> {
                    progress.setData(
                        context.getString(R.string.statistics_szs),
                        item.homeNum,
                        item.awayNum,
                        false
                    )
                }

                EventEnum.EVENT_PASS.type -> {
                    progress.setData(
                        context.getString(R.string.statistics_cqs),
                        item.homeNum,
                        item.awayNum,
                        false
                    )
                }

                EventEnum.EVENT_FREE.type -> {
                    progress.setData(
                        context.getString(R.string.statistics_ryq),
                        item.homeNum,
                        item.awayNum,
                        false
                    )
                }

                EventEnum.EVENT_CORNER.type -> {
                    progress.setData(
                        context.getString(R.string.statistics_jq),
                        item.homeNum,
                        item.awayNum,
                        false
                    )
                }

                EventEnum.EVENT_OFFSIDE.type -> {
                    progress.setData(
                        context.getString(R.string.statistics_yw),
                        item.homeNum,
                        item.awayNum,
                        false
                    )
                }
            }
            mBinding.layData.addView(progress)
        }
    }

    private fun getAttackBackground(home: Int, away: Int): Drawable? {
        return if (home < away) {
            if (home == 0) {
                SportSkinResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_attack_zero
                )
            } else {
                SportSkinResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_attack_left
                )
            }
        } else if (home > away) {
            if (away == 0) {
                SportSkinResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_attack_hundred
                )
            } else {
                SportSkinResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_attack_right
                )
            }
        } else {
            SportSkinResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_attack
            )
        }
    }

    private fun getDangerAttackBackground(home: Int, away: Int): Drawable? {
        return if (home < away) {
            if (home == 0) {
                SportSkinResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_danger_attack_zero
                )
            } else {
                SportSkinResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_danger_attack_left
                )
            }
        } else if (home > away) {
            if (away == 0) {
                SportSkinResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_danger_attack_hundred
                )
            } else {
                SportSkinResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_danger_attack_right
                )
            }
        } else {
            SportSkinResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_danger_attack
            )
        }
    }

    private fun getBallControlBackground(home: Int, away: Int): Drawable? {
        return if (home < away) {
            if (home == 0) {
                SportSkinResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_control_rate_zero
                )
            } else {
                SportSkinResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_control_rate_left
                )
            }
        } else if (home > away) {
            if (away == 0) {
                SportSkinResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_control_rate_hundred
                )
            } else {
                SportSkinResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_control_rate_right
                )
            }
        } else {
            SportSkinResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_control_rate
            )
        }
    }

    fun setOnItemClickListener(listener: OnClickListener) {
        this.clicklistener = listener
    }

    interface OnClickListener {
        fun onClick()
    }
}