package com.xcjh.app.ui.details.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.xcjh.app.R
import com.xcjh.app.adapter.ImportEventAdapter
import com.xcjh.app.adapter.TextLiveAdapter
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.bean.BasketballScoreBean
import com.xcjh.app.bean.IncidentsBean
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.databinding.FragmentDetailTabResultBinding
import com.xcjh.app.databinding.LayoutEmptyBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.ui.details.fragment.index.Index1Fragment
import com.xcjh.app.ui.details.fragment.index.Index2Fragment
import com.xcjh.app.ui.details.fragment.index.Index3Fragment
import com.xcjh.app.ui.details.fragment.result.FootballFragment
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.ReceiveChangeMsg
import com.xcjh.app.websocket.listener.OtherPushListener
import com.xcjh.base_lib.App
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.initFragment
import com.xcjh.base_lib.utils.loge
import com.xcjh.base_lib.utils.vertical
import com.xcjh.base_lib.utils.view.visibleOrGone
import java.math.BigDecimal

/**
 * 赛况
 */

class DetailResultFragment(private var match: MatchDetailBean) :
    BaseVpFragment<DetailVm, FragmentDetailTabResultBinding>() {
    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }
    override val typeId: Long
        get() = 3

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewFootballStatus.visibleOrGone("1" == match.matchType)
        mDatabind.lltFootball.visibleOrGone("1" == match.matchType)
        mDatabind.viewPager.visibleOrGone("1" == match.matchType)
        mDatabind.lltBasketball.visibleOrGone("2" == match.matchType)
        if ("1" == match.matchType) {//1：足球；2：篮球
            mDatabind.viewFootballStatus.setTeamInfo(
                match.homeLogo,
                match.homeName,
                match.awayLogo,
                match.awayName
            )
           
            //文字直播按钮
            mDatabind.btnTextLive.isSelected = true
            mDatabind.btnTextLive.setOnClickListener {
                changeUI(0)
            }
            //重要事件按钮
            mDatabind.btnImportEvent.setOnClickListener {
                changeUI(1)
            }
            mDatabind.viewPager.initFragment(
                this,
                arrayListOf(FootballFragment(0,match), FootballFragment(1,match))
            )
            mDatabind.viewPager.offscreenPageLimit = 2
            mDatabind.viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    changeUI(position)
                }
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
        loadData()
        MyWsManager.getInstance(App.app)?.setOtherPushListener(this@DetailResultFragment.toString(),
            object : OtherPushListener {
                override fun onChangeMatchData(matchList: ArrayList<ReceiveChangeMsg>) {
                    try {
                        //防止数据未初始化的情况
                        if (match.status in 0..if (match.matchType == "1") 7 else 9) {
                            matchList.forEach {
                                if (match.matchId == it.matchId.toString() && match.matchType == it.matchType.toString()) {
                                    if (match.matchType == "2") {
                                        BasketballScoreBean().apply {
                                            status = BigDecimal(it.status).toInt()
                                            homeScoreList = it.scoresDetail?.get(0) ?: arrayListOf()
                                            awayScoreList = it.scoresDetail?.get(1) ?: arrayListOf()
                                            homeOverTimeScoresList =
                                                it.scoresDetail?.get(0)?.filterIndexed { index, v ->
                                                    index > 3
                                                }
                                            awayOverTimeScoresList =
                                                it.scoresDetail?.get(1)?.filterIndexed { index, v ->
                                                    index > 3
                                                }
                                        }.apply {
                                            mDatabind.viewBasketballTable.setTeamData(this)
                                        }
                                    } else {

                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.message?.loge("e====e")
                    }
                }
            })
    }
    private fun changeUI(pos: Int) {
        mDatabind.viewPager.currentItem = pos
        mDatabind.btnTextLive.isSelected = pos == 0
        mDatabind.btnImportEvent.isSelected = pos == 1
    }
    private fun loadData() {
        if ("1" == match.matchType) {//1：足球；2：篮球
            //获取足球赛况技术统计表格的数据
            vm.getFootballStatus(match.matchId)
            //获取足球赛况文字直播和重要事件
            vm.getLiveEvent(match.matchId)
            vm.getIncidents(match.matchId)

        } else {
            //获取篮球赛况 得分数据
            vm.getBasketballScore(match.matchId)
            //获取篮球赛况 统计数据
            vm.getBasketballStatus(match.matchId)
        }
    }

    override fun onDestroy() {
        MyWsManager.getInstance(App.app)?.removeOtherPushListener(this.toString())
        super.onDestroy()
    }

    override fun createObserver() {
        //篮球比赛赛况技术统计上表格数据接口返回处理
        vm.basketScore.observe(this) {
            if (it != null) {
                mDatabind.viewBasketballTable.setTeamData(it)
            }
        }

        //篮球比赛赛况技术统计下图表数据接口返回处理
        vm.basketStatus.observe(this) {
            if (it != null) {
                //主队客队2分球、3分球、罚球数据统计
                // Gson().toJson(it).loge()
                mDatabind.viewBasketballData.setData(it)
            }
        }

        ///==========足球比赛赛况技术统计图表数据接口返回处理
        vm.footStatus.observe(this) {
            if (it != null) {
                //足球比赛赛况技术统计，需要依据接口进行调整
                mDatabind.viewFootballStatus.setData(it)
            }
        }

        appViewModel.appPolling.observe(activity as MatchDetailActivity) {
            if (isAdded && !isFirst) {
                if ("1" == match.matchType) {//1：足球；2：篮球
                    //获取足球赛况技术统计表格的数据
                    vm.getFootballStatus(match.matchId)
                    //获取足球赛况文字直播和重要事件
                    vm.getLiveEvent(match.matchId)
                    vm.getIncidents(match.matchId)

                } else {
                    //获取篮球赛况 得分数据
                    // vm.getBasketballScore(match.matchId)
                    //获取篮球赛况 统计数据
                    vm.getBasketballStatus(match.matchId)
                }
            }
        }

    }
}
