package com.xcjh.app.view.balldetail.liveup

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.xcjh.app.R
import com.xcjh.app.bean.FootballLineupBean
import com.xcjh.app.bean.FootballPlayer
import com.xcjh.app.databinding.ViewFootballPlayerBinding
import com.xcjh.app.databinding.ViewMatchRefereeBinding
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.loge

/**
 * 足球阵容 阵型排版
 */
class FootballLiveUpMiddleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr) {
    private var mHight: Int = 0//布局高
    private var mWidth: Int = 0//布局宽
    private val marginB = dp2px(10)//距离底部距离
    private val marginT = dp2px(20)//距离中心距离
    private val itemH = dp2px(50)//子项高度

    init {
        this.background = ContextCompat.getDrawable(context, R.drawable.bg_live_up)
        // postDelayed({setData("")},200)
        this.post {
            mWidth = measuredWidth
            mHight = measuredWidth * 630 / 351
            layoutParams.height = mHight
            // setData("")
        }
    }

    /**
     * 设置动态数据
     */
    fun setData(bean: FootballLineupBean) {
        /*  var str = data
          if (str.isEmpty()) {
              str = OpenAssets.openAsFile(context, "football.json")
          }
          val bean = jsonToObject<FootballLineupBean>(str)*/
        removeAllViews()
        val homeList = bean.home.filter {
            it.first == 1
        }
        val awayList = bean.away.filter {
            it.first == 1
        }
        val homeSet = homeList.map {
            it.y
        }.toSet()//.size
        val awaySet = awayList.map {
            it.y
        }.toSet()
        val maxHomeGroup = findMaxGroup(homeList)
        val maxAwayGroup = findMaxGroup(awayList)
        "${mWidth} ===${maxHomeGroup}".loge("===setData:")
        "${mWidth} ===${maxAwayGroup}".loge("===setData:")

        val lineHomeH =  mHight / 2 - itemH * homeSet.size - marginB - marginT
        val lineAwayH =  mHight / 2 - itemH * awaySet.size - marginB - marginT
        for ((pos, player) in homeList.withIndex()) {
            val child =
                ViewFootballPlayerBinding.inflate(LayoutInflater.from(context), null, false)
            child.ivPlayer.setImageResource(R.drawable.icon_team_red)
            child.tvPlayerNum.text = player.shirtNumber.toString()
            child.tvPlayerName.text = player.name
            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            lp.width = mWidth / maxHomeGroup
            lp.leftMargin = mWidth * player.x / 100 - lp.width / 2
            for ((i, item) in homeSet.withIndex()) {
                if (player.y == item && homeSet.size > 1) {
                    // lp.topMargin = mHight * (12 + 75 * i / (homeSet.size - 1)) / 200 - dp2px(25)
                    lp.topMargin = marginB + i * lineHomeH / (homeSet.size - 1) + itemH * i
                }
            }
            //  child.root.layoutParams = lp
            // addView(child.root)
            addViewInLayout(child.root, pos, lp)
        }
        for ((pos, player) in awayList.withIndex()) {
            val child =
                ViewFootballPlayerBinding.inflate(LayoutInflater.from(context), null, false)
            child.ivPlayer.setImageResource(R.drawable.icon_team_blue)
            child.tvPlayerNum.text = player.shirtNumber.toString()
            child.tvPlayerName.text = player.name
            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            lp.addRule(ALIGN_PARENT_BOTTOM)
            lp.width = mWidth / maxAwayGroup
            lp.leftMargin = mWidth * (100 - player.x) / 100 - lp.width / 2
            for ((i, item) in awaySet.withIndex()) {
                if (player.y == item && awaySet.size > 1) {
                    // lp.bottomMargin = mHight * (12 + 72 * i / (awaySet.size - 1)) / 200 - dp2px(25)
                    lp.bottomMargin = marginB + i * lineAwayH / (awaySet.size - 1) + itemH * i
                    /*if (player.position == "G") {
                        //守门员
                        lp.bottomMargin = mHight * player.y / 200 - dp2px(25)
                    }*/
                }
            }
            /* child.root.layoutParams = lp
             addView(child.root)*/
            addViewInLayout(child.root, pos, lp)
        }
        val middle = ViewMatchRefereeBinding.inflate(LayoutInflater.from(context), null, false)
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.addRule(CENTER_IN_PARENT)
        // lp.topMargin = mHight / 2 - dp2px(17)
        middle.root.layoutParams = lp
        if (!bean.refereeName.isNullOrEmpty()) {
            middle.tvRefereeName.text = bean.refereeName
        } else {
            middle.tvRefereeName.text = context.getString(R.string.no_referee_name)//"暂无裁判信息"
        }
        // addView(middle.root)
        addViewInLayout(middle.root, -1, lp)
        requestLayout()
    }

    /**
     * 找到位置相同最大行数的数值
     */
    fun findMaxGroup(players: List<FootballPlayer>): Int {
        //保存位置在同一水平的人数
        val heightCountMap = players.groupingBy { it.y }.eachCount()
        return heightCountMap.values.maxOrNull() ?: 0
    }
}