package com.walisport.module.live.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.walisport.module.live.data.model.MatchEventBean
import com.walisport.module.live.databinding.ViewMatchEventBinding
import com.walisport.module.live.ui.adapter.MatchEventAdapter

/**
 * 赛况页赛况布局控件
 */

class MatchEventView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val mBinding: ViewMatchEventBinding =
        ViewMatchEventBinding.inflate(LayoutInflater.from(context), this, true)
    private var matchAdapter: MatchEventAdapter = MatchEventAdapter(context)

    init {
        mBinding.recyclerMatchEvent.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = matchAdapter
        }
    }

    fun setTeamName(homeName: String, awayName: String) {
        mBinding.tvHomeCountry.text = homeName
        mBinding.tvAwayCountry.text = awayName
    }

    fun setData(array: ArrayList<MatchEventBean>) {
        matchAdapter.submitList(array)
    }
}