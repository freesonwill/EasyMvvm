package com.xcjh.app.ui.details.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.bean.BasketballScoreBean
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.databinding.FragmentDetailTabResultBasketBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.ReceiveChangeMsg
import com.xcjh.app.websocket.listener.OtherPushListener
import com.xcjh.base_lib.App
import com.xcjh.base_lib.utils.loge
import java.math.BigDecimal

/**
 * 赛况
 */

class DetailResultBasketFragment(private var match: MatchDetailBean) :
    BaseVpFragment<DetailVm, FragmentDetailTabResultBasketBinding>() {
    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }
    override val typeId: Long
        get() = 3

    override fun initView(savedInstanceState: Bundle?) {
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

        MyWsManager.getInstance(App.app)?.setOtherPushListener(this@DetailResultBasketFragment.toString(),
            object : OtherPushListener {
                override fun onChangeMatchData(matchList: ArrayList<ReceiveChangeMsg>) {
                    try {
                        //防止数据未初始化的情况
                        if (isAdded && match.status in 0.. 9) {
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
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.message?.loge("e====e")
                    }
                }
            })
        loadData()
    }

    override fun lazyLoadData() {
        //loadData()
    }
    private fun loadData() {
        //获取篮球赛况 得分数据
        vm.getBasketballScore(match.matchId)
        //获取篮球赛况 统计数据
        vm.getBasketballStatus(match.matchId)
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

        appViewModel.appPolling.observe(activity as MatchDetailActivity) {
            if (isAdded && !isFirst) {
                if ("2" == match.matchType) {//1：足球；2：篮球
                    //获取篮球赛况 得分数据
                    // vm.getBasketballScore(match.matchId)
                    //获取篮球赛况 统计数据
                    vm.getBasketballStatus(match.matchId)
                }
            }
        }

    }
}
