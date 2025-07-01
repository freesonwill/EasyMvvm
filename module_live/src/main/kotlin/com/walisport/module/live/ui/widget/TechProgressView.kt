package com.walisport.module.live.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.walisport.module.live.R
import com.walisport.module.live.data.model.MatchHalfTeamStats
import com.walisport.module.live.databinding.ViewProgressStatisticsBinding

/**
 * 赛况页技术统计布局控件中的红蓝双方数据对比控件
 */

class TechProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val mBinding: ViewProgressStatisticsBinding =
        ViewProgressStatisticsBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TechProgressView)
        val title = typedArray.getString(R.styleable.TechProgressView_titleBar)
        typedArray.recycle()
        mBinding.tvProgressName.text = title
    }

    @SuppressLint("SetTextI18n")
    fun setData(item: MatchHalfTeamStats, isRate: Boolean) {
        if (isRate) {
            val total = (item.homeNum + item.awayNum).toFloat()
            val home = ((item.homeNum / total) * 100).toInt()
            val away = ((item.awayNum / total) * 100).toInt()
            mBinding.tvProgressLeft.text = "$home%"
            mBinding.tvProgressRight.text = "$away%"
            mBinding.proLeft.progress = home
            mBinding.proRight.progress = away
        } else {
            mBinding.tvProgressLeft.text = String.format("%s", item.homeNum)
            mBinding.tvProgressRight.text = String.format("%s", item.awayNum)
            mBinding.proLeft.progress = item.homeNum
            mBinding.proRight.progress = item.awayNum
        }
    }
}