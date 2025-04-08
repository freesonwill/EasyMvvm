package com.walisport.module.live.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.walisport.module.live.data.model.GoalTrendBean
import com.walisport.module.live.databinding.ViewTechnicalStatisticsBinding

/**
 * 赛况页技术统计布局控件
 */

class TechnicalCountView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val mBinding: ViewTechnicalStatisticsBinding =
        ViewTechnicalStatisticsBinding.inflate(LayoutInflater.from(context), this, true)

    fun setTeamName(homeName: String, awayName: String) {
        mBinding.tvHomeCountry.text = homeName
        mBinding.tvAwayCountry.text = awayName
    }

    fun setScore(sore: String) {
        mBinding.tvScore.text = sore
    }

    fun setData(array: ArrayList<GoalTrendBean>) {
        mBinding.viewGoalTrend.setData(array)
    }
}