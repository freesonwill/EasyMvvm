package com.walisport.module.live.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.skin.widget.SkinnableLinearLayout
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
) : SkinnableLinearLayout(context, attrs, defStyleAttr) {

    private var clicklistener: OnClickListener? = null
    private var unitWidth: Float = 0f

    private var mBinding: ViewTechnicalStatisticsBinding =
        ViewTechnicalStatisticsBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        mBinding.viewGoalTrend.setOnClickListener {
            clicklistener?.onClick()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        unitWidth = (width - 24.dp2px) / 90f
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
    @SuppressLint("SetTextI18n")
    fun setTrendData(data: MatchTrendData) {
        mBinding.viewGoalTrend.setData(data)
        val size = data.data.size
        if (size > 90) {
            //当比赛时间超过90分钟时需重新绘制时间栏
            //mBinding.lastTime.text = "$size'"
            refreshTimeLayout(size)
        }
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

    private fun refreshTimeLayout(minute: Int) {
        val color = mBinding.lastTime.currentTextColor
        mBinding.layTime.removeAllViews()
        for (index in 0..6) {
            val textStr = when (index) {
                0 -> "0'"
                1 -> "15'"
                2 -> "30'"
                3 -> "45'"
                4 -> "60'"
                5 -> "75'"
                6 -> "$minute'"
                else -> {
                    ""
                }
            }
            val textView = AppCompatTextView(context).apply {
                text = textStr
                setTextColor(color)
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            }
            mBinding.layTime.addView(textView)
            textView.post {
                var params: LayoutParams
                if (textView.layoutParams is LayoutParams) {
                    params = textView.layoutParams as LayoutParams
                    when (index) {
                        0 -> params.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                        6 -> params.gravity = Gravity.END or Gravity.CENTER_VERTICAL
                        else -> {
                            val marginLeft = (15 * index * unitWidth - textView.width / 2f).toInt()
                            params.setMargins(marginLeft, 0, 0, 0)
                            params.gravity = Gravity.CENTER_VERTICAL
                        }
                    }
                    textView.layoutParams = params
                }
            }
        }
    }

    private fun getAttackBackground(home: Int, away: Int): Drawable? {
        return if (home < away) {
            if (home == 0) {
                SkinnableResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_attack_zero
                )
            } else {
                SkinnableResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_attack_left
                )
            }
        } else if (home > away) {
            if (away == 0) {
                SkinnableResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_attack_hundred
                )
            } else {
                SkinnableResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_attack_right
                )
            }
        } else {
            SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_attack
            )
        }
    }

    private fun getDangerAttackBackground(home: Int, away: Int): Drawable? {
        return if (home < away) {
            if (home == 0) {
                SkinnableResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_danger_attack_zero
                )
            } else {
                SkinnableResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_danger_attack_left
                )
            }
        } else if (home > away) {
            if (away == 0) {
                SkinnableResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_danger_attack_hundred
                )
            } else {
                SkinnableResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_danger_attack_right
                )
            }
        } else {
            SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_danger_attack
            )
        }
    }

    private fun getBallControlBackground(home: Int, away: Int): Drawable? {
        return if (home < away) {
            if (home == 0) {
                SkinnableResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_control_rate_zero
                )
            } else {
                SkinnableResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_control_rate_left
                )
            }
        } else if (home > away) {
            if (away == 0) {
                SkinnableResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_control_rate_hundred
                )
            } else {
                SkinnableResourceManager.getDrawable(
                    mBinding.root.context,
                    R.drawable.icon_control_rate_right
                )
            }
        } else {
            SkinnableResourceManager.getDrawable(
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