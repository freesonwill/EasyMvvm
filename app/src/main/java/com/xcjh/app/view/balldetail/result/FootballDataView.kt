package com.xcjh.app.view.balldetail.result

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.xcjh.app.R
import com.xcjh.app.bean.StatusBean
import com.xcjh.app.databinding.ViewDetailFootballStatusBinding
import com.xcjh.app.utils.setProgressValue

/**
 * Describe : 足球直播详情页面赛况布局控件
 */
class FootballDataView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private var binding: ViewDetailFootballStatusBinding
    init {
        binding=ViewDetailFootballStatusBinding.inflate(LayoutInflater.from(context),this,true)
    }

    fun setTeamInfo(homeIcon: String?, homeName: String?, awayIcon: String?, awayName: String?) {
        binding.tvHomeName.text = homeName ?: ""
        binding.tvAwayName.text = awayName ?: ""
        Glide.with(this).load(homeIcon).placeholder(R.drawable.def_football)
            .into(binding.ivHomeIcon)
        Glide.with(this).load(awayIcon).placeholder(R.drawable.def_football)
            .into(binding.ivAwayIcon)
    }

    fun setData(array: ArrayList<StatusBean>) {
        for ((type, home, away) in array) {
            when (type) {
                // 23-进攻 24-危险进攻 25-控球率
                23 -> {
                    binding.viewGameStatusJg.progress = setProgressValue(if (home == away && home == 0) 50 else home * 100 / (home + away))

                    binding.viewGameStatusJgLeft.text = home.toString()
                    binding.viewGameStatusJgRight.text = away.toString()
                }

                24 -> {
                    binding.viewGameStatusWx.progress = setProgressValue( if (home == away && home == 0) 50 else home * 100 / (home + away))
                    binding.tvWxHome.text = home.toString()
                    binding.tvWxAway.text = away.toString()
                }

                25 -> {
                    binding.viewGameStatusKql.progress = setProgressValue( if (home == away && home == 0) 50 else home * 100 / (home + away))
                    binding.tvKqlHome.text = home.toString()
                    binding.tvKqlAway.text = away.toString()
                }

                2 -> {//2-角球
                    binding.tvHomeYw.text = home.toString()
                    binding.tvAwayYw.text = away.toString()
                }

                3 -> {//3-黄牌
                    binding.tvHomeYellow.text = home.toString()
                    binding.tvAwayYellow.text = away.toString()
                }

                4 -> {//4-红牌
                    binding.tvHomeRed.text = home.toString()
                    binding.tvAwayRed.text = away.toString()
                }
                // 21-射正 22-射偏
                21 -> {
                    binding.tvSzHome.text = home.toString()
                    binding.tvSzAway.text = away.toString()
                    binding.pbSz.progress =setProgressValue( if (home == away && home == 0) 50 else home * 100 / (home + away))
                }

                22 -> {
                    binding.tvSpHome.text = home.toString()
                    binding.tvSpAway.text = away.toString()
                    binding.pbSp.progress = setProgressValue( if (home == away && home == 0) 50 else home * 100 / (home + away))
                }

                else -> {}
            }
        }
    }
    /**
     * 客队分母
     */
    private fun getProgress(home:Int,away:Int):Int {
        return setProgressValue( if (home == away && home == 0) 50 else home * 100 / (home + away))
    }

}