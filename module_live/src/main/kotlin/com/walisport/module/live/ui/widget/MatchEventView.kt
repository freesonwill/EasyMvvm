package com.walisport.module.live.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.skin.widget.SkinnableLinearLayout
import com.bumptech.glide.Glide
import com.walisport.module.live.data.EventEnum
import com.walisport.module.live.data.model.Incident
import com.walisport.module.live.data.model.MatchEventBean
import com.walisport.module.live.databinding.ViewMatchEventBinding
import com.walisport.module.live.ui.adapter.MatchEventAdapter

/**
 * 赛况页文字直播控件
 */

class MatchEventView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SkinnableLinearLayout(context, attrs, defStyleAttr) {

    private val mBinding: ViewMatchEventBinding =
        ViewMatchEventBinding.inflate(LayoutInflater.from(context), this, true)
    private var matchAdapter: MatchEventAdapter = MatchEventAdapter(context)
    private val hashMap = HashMap<Int, MatchEventBean>()

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

    //全屏直播模式下的文字直播控件，不要圆角背景
    fun setFullScreenMode() {
        mBinding.tvEventTitle.background = null
        mBinding.root.background = null
    }

    //设置比赛双方名称和LOGO
    fun setTeamInfo(homeName: String, awayName: String, homeLogo: String, awayLogo: String) {
        mBinding.tvHomeCountry.text = homeName
        mBinding.tvAwayCountry.text = awayName
        Glide.with(context).load(homeLogo).into(mBinding.ivHomeCountry)
        Glide.with(context).load(awayLogo).into(mBinding.ivAwayCountry)
    }

    //设置文字直播内容
    fun setData(incidents: List<Incident>) {
        hashMap.clear()
        //存在同一分钟内的不同事件，需归集处理
        for (item in incidents) {
            val time = item.time
            val pos = item.position
            val type = item.type
            if (hashMap.containsKey(time)) {
                if (pos == 1) {
                    hashMap[time]?.homeType = type
                    if (type == 9) { //换人事件特殊处理
                        hashMap[time]?.homeType = EventEnum.EVENT_UP.type
                        hashMap[time]?.homeTwoType = EventEnum.EVENT_DW.type
                        hashMap[time]?.homePlayer = item.in_player_name_zh
                        hashMap[time]?.homeTwoPlayer = item.out_player_name_zh
                    }
                } else {
                    hashMap[time]?.awayType = type
                    if (type == 9) {
                        hashMap[time]?.awayType = EventEnum.EVENT_UP.type
                        hashMap[time]?.awayTwoType = EventEnum.EVENT_DW.type
                        hashMap[time]?.awayPlayer = item.in_player_name_zh
                        hashMap[time]?.awayTwoPlayer = item.out_player_name_zh
                    }
                }
            } else {
                val temp = MatchEventBean(time)
                if (pos == 1) {
                    temp.homeType = type
                    if (type == 9) {
                        hashMap[time]?.homeType = EventEnum.EVENT_UP.type
                        hashMap[time]?.homeTwoType = EventEnum.EVENT_DW.type
                        hashMap[time]?.homePlayer = item.in_player_name_zh
                        hashMap[time]?.homeTwoPlayer = item.out_player_name_zh
                    }
                } else {
                    temp.awayType = type
                    if (type == 9) {
                        hashMap[time]?.awayType = EventEnum.EVENT_UP.type
                        hashMap[time]?.awayTwoType = EventEnum.EVENT_DW.type
                        hashMap[time]?.awayPlayer = item.in_player_name_zh
                        hashMap[time]?.awayTwoPlayer = item.out_player_name_zh
                    }
                }
                hashMap[time] = temp
            }
        }
        if (hashMap.size > 0) {
            val list = ArrayList<MatchEventBean>()
            list.add(MatchEventBean(0))
            hashMap.forEach { (_, temp) ->
                list.add(temp)
            }
            list.sortBy { event -> event.time }
            list.add(MatchEventBean(0))
            matchAdapter.submitList(list)
        }
    }
}