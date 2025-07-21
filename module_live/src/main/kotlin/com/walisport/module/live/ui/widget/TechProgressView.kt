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
        val total = (item.homeNum + item.awayNum).toFloat()
        var home = 0
        var away = 0
        if (total > 0f) {//防止除零错误
            home = ((item.homeNum / total) * 100).toInt()
            away = ((item.awayNum / total) * 100).toInt()
        }
        if (isRate) {
            mBinding.tvProgressLeft.text = "$home%"
            mBinding.tvProgressRight.text = "$away%"
        } else {
            mBinding.tvProgressLeft.text = "${item.homeNum}"
            mBinding.tvProgressRight.text = "${item.awayNum}"
        }
        mBinding.proLeft.progress = home
        mBinding.proRight.progress = away
    }
}