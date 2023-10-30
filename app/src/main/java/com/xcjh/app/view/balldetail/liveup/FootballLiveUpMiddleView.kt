package com.xcjh.app.view.balldetail.liveup

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.xcjh.app.R
import com.xcjh.app.bean.FootballLineupBean
import com.xcjh.app.databinding.ViewFootballPlayerBinding
import com.xcjh.app.databinding.ViewMatchRefereeBinding
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.loge

/**
 * 足球阵容
 */
class FootballLiveUpMiddleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr) {
    private var mHight: Int = 0
    private var mWidth: Int = 0

    init {
        this.background = ContextCompat.getDrawable(context, R.drawable.bg_live_up)
        // postDelayed({setData("")},200)
        this.post {
            mWidth = measuredWidth
            mHight = measuredWidth * 768 / 343
           // Log.e("===", "setData: ====" +mWidth)
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
        val homeList = bean.home.filter {
            it.first == 1
        }
        val awayList = bean.away.filter {
            it.first == 1
        }
        val homeSet = homeList.map {
            it.y
        }.toSet()//.size
        removeAllViews()
        Log.e("TAG", "${mWidth}===setData: ==="+homeSet.toString() )
        homeList.forEach { player ->
            val child =
                ViewFootballPlayerBinding.inflate(LayoutInflater.from(context), null, false)
            child.tvPlayerNum.text = player.shirtNumber.toString()
            child.tvPlayerName.text = player.name
            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            lp.leftMargin = mWidth * player.x / 100 - dp2px(30)
            when (player.position) {
                "F" -> {
                    //前锋
                    lp.topMargin = mHight * 88 / 200 - dp2px(25)
                }
                "G" -> {
                    //后卫
                    lp.topMargin = mHight * 12 / 200 - dp2px(25)
                }
                else -> {
                    for ((i, item) in homeSet.withIndex()) {
                        if (player.y==item && homeSet.size>1){
                            lp.topMargin = mHight * (12+76*i/(homeSet.size-1)) / 200 - dp2px(25)
                        }
                    }
                }
            }

            child.root.layoutParams = lp
            addView(child.root)
        }
        awayList.forEach { player ->
            val child =
                ViewFootballPlayerBinding.inflate(LayoutInflater.from(context), null, false)
            child.ivPlayer.setBackgroundResource(R.drawable.icon_team_blue)
            child.tvPlayerNum.text = player.shirtNumber.toString()
            child.tvPlayerName.text = player.name
            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            /* lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
             lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
             lp.bottomMargin = hight * player.y / 200 - dp2px(25)*/
            lp.leftMargin = mWidth * player.x / 100 - dp2px(30)
            if (player.position == "F") {
                lp.topMargin = mHight * (200 - player.y) / 200 - dp2px(10)
            } else {
                lp.topMargin = mHight * (200 - player.y) / 200 - dp2px(25)
                if (player.position == "G") {
                    //客队门卫距离底线
                    lp.bottomMargin = mHight * player.y / 200 - dp2px(25)
                }
            }
            // lp.topMargin = hight * (200 - player.y) / 200 - dp2px(25)
            child.root.layoutParams = lp
            addView(child.root)
        }
        val middle = ViewMatchRefereeBinding.inflate(LayoutInflater.from(context), null, false)
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.addRule(CENTER_IN_PARENT)
        lp.topMargin=mHight/2-dp2px(17)
        middle.root.layoutParams = lp
        if (!bean.refereeName.isNullOrEmpty()) {
            middle.tvRefereeName.text = bean.refereeName
        } else {
            middle.tvRefereeName.text = context.getString(R.string.no_referee_name)//"暂无裁判信息"
        }
        addView(middle.root)
    }

}