package com.walisport.module.live.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.skin.widget.SkinnableLinearLayout
import com.bumptech.glide.Glide
import com.walisport.module.live.data.model.MatchEventBean
import com.walisport.module.live.databinding.ViewMatchEventBinding
import com.walisport.module.live.ui.adapter.MatchEventAdapter

/**
 * 赛况页赛况布局控件
 */

class MatchEventView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SkinnableLinearLayout(context, attrs, defStyleAttr) {

    private val mBinding: ViewMatchEventBinding =
        ViewMatchEventBinding.inflate(LayoutInflater.from(context), this, true)
    private var matchAdapter: MatchEventAdapter = MatchEventAdapter(context)

    init {
        mBinding.recyclerMatchEvent.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = matchAdapter
            //加入针对RecyclerView的触摸事件拦截，解决RecyclerView嵌套在ScrollView中上滑不够丝滑的问题
            addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    // 返回true以拦截触摸事件，防止滑动
                    return true
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            })
        }
    }

    //设置比赛双方名称和LOGO
    fun setTeamInfo(homeName: String, awayName: String, homeLogo: String, awayLogo: String) {
        mBinding.tvHomeCountry.text = homeName
        mBinding.tvAwayCountry.text = awayName
        Glide.with(context).load(homeLogo).into(mBinding.ivHomeCountry)
        Glide.with(context).load(awayLogo).into(mBinding.ivAwayCountry)
    }

    fun setData(array: ArrayList<MatchEventBean>) {
        matchAdapter.submitList(array)
    }
}