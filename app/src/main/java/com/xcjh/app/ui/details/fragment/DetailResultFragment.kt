package com.xcjh.app.ui.details.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.xcjh.app.adapter.ImportEventAdapter
import com.xcjh.app.adapter.TextLiveAdapter
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.bean.IncidentsBean
import com.xcjh.app.bean.LiveTextBean
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.bean.PostSchMatchListBean
import com.xcjh.app.databinding.FragmentDetailTabResultBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.vertical
import com.xcjh.base_lib.utils.view.visibleOrGone

/**
 * 赛况
 */

class DetailResultFragment(var match: MatchDetailBean) :
    BaseVpFragment<DetailVm, FragmentDetailTabResultBinding>() {

    private val textAdapter by lazy { TextLiveAdapter() }
    private val eventAdapter by lazy { ImportEventAdapter() }

    override val typeId: Long
        get() = 3

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.layoutFootball.visibleOrGone("1" == match.matchType)
        mDatabind.layoutBasketball.visibleOrGone("2" == match.matchType)
        if ("1" == match.matchType) {//1：足球；2：篮球
            mDatabind.viewFootballStatus.setTeamInfo(
                match.homeLogo,
                match.homeName,
                match.awayLogo,
                match.awayName
            )
            //文字直播和重要事件列表适配器
            mDatabind.rcvTextLive.run {
                vertical()
                adapter = textAdapter
                distance(0, 0, 0, 0)
            }
            textAdapter.setLogo(match.homeLogo, match.awayLogo)
            mDatabind.rcvImportEvent.run {
                vertical()
                adapter = eventAdapter
                distance(0, 0, 0, 0)
            }
            //文字直播按钮
            mDatabind.btnTextLive.isSelected = true
            mDatabind.btnTextLive.setOnClickListener(View.OnClickListener {
                mDatabind.btnTextLive.isSelected = true
                mDatabind.btnImportEvent.isSelected = false
                mDatabind.rcvTextLive.visibility = View.VISIBLE
                mDatabind.rcvImportEvent.visibility = View.GONE
            })
            //重要事件按钮
            mDatabind.btnImportEvent.setOnClickListener(View.OnClickListener {
                mDatabind.btnTextLive.isSelected = false
                mDatabind.btnImportEvent.isSelected = true
                mDatabind.rcvTextLive.visibility = View.GONE
                mDatabind.rcvImportEvent.visibility = View.VISIBLE
            })

        } else {

            //设置篮球赛况第一个表格里主客队头像和名称
            mDatabind.viewBasketballTable.setTeamInfo(
                match.homeLogo,
                match.homeName,
                match.awayLogo,
                match.awayName
            )
            //设置篮球赛况第二个表格里主客队头像和名称
            mDatabind.viewBasketballData.setTitleBar(
                match.homeLogo,
                match.homeName,
                match.awayLogo,
                match.awayName
            )
        }
    }

    override fun lazyLoadData() {
        if ("1" == match.matchType) {//1：足球；2：篮球
            //获取足球赛况技术统计表格的数据
            mViewModel.getFootballStatus(match.matchId)
            //获取足球赛况文字直播和重要事件
            mViewModel.getLiveEvent(match.matchId)
            mViewModel.getIncidents(match.matchId)

        } else {
            //获取篮球赛况 得分数据
            mViewModel.getBasketballScore(match.matchId)
            //获取篮球赛况 统计数据
            mViewModel.getBasketballStatus(match.matchId)
        }
    }

    override fun createObserver() {
        //篮球比赛赛况技术统计上表格数据接口返回处理
        mViewModel.basketScore.observe(this) {
            if (it != null) {
                mDatabind.viewBasketballTable.setTeamData(it)
            }
        }
        //篮球比赛赛况技术统计下图表数据接口返回处理
        mViewModel.basketStatus.observe(this) {
            if (it != null) {
                //主队客队2分球、3分球、罚球数据统计
                Log.e("TAG", "createObserver: ==="+Gson().toJson(it) )
                mDatabind.viewBasketballData.setData(it)
            }
        }

        ///==========足球比赛赛况技术统计图表数据接口返回处理
        mViewModel.footStatus.observe(this) {
            if (it != null) {
                //足球比赛赛况技术统计，需要依据接口进行调整
                mDatabind.viewFootballStatus.setData(it)
            }
        }
        //文字直播
        mViewModel.text.observe(this) {
            if (it != null) {
                textAdapter.submitList(it)
            }
        }
        //重要事件
        mViewModel.incidents.observe(this) {
            if (it != null) {
                val event = ArrayList<IncidentsBean>()
                for (item: IncidentsBean in it) {
                    //重要事件  红黄牌 进球 换人
                    when(item.type){
                        1,3,4,9,15->{
                            event.add(item)
                        }
                    }
                }
                eventAdapter.submitList(event)
            }
        }

        appViewModel.appPolling.observe(activity as MatchDetailActivity) {
            if (isAdded && !isFirst) {
                lazyLoadData()
            }
        }

    }
}
