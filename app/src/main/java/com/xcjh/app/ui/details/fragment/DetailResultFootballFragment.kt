package com.xcjh.app.ui.details.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.databinding.FragmentDetailTabResultFootballBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.ui.details.fragment.result.FootballFragment
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.base_lib.App
import com.xcjh.base_lib.utils.bindBgViewPager2
import com.xcjh.base_lib.utils.initFragment
import com.xcjh.base_lib.utils.view.visibleOrGone

/**
 * 足球赛况
 */
class DetailResultFootballFragment(private var match: MatchDetailBean) :
    BaseVpFragment<DetailVm, FragmentDetailTabResultFootballBinding>() {
    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }
    override val typeId: Long
        get() = 3

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewFootballStatus.visibleOrGone("1" == match.matchType)
        mDatabind.lltTab.visibleOrGone("1" == match.matchType)
        mDatabind.viewPager.visibleOrGone("1" == match.matchType)
        if ("1" == match.matchType) {//1：足球；2：篮球
            mDatabind.viewFootballStatus.setTeamInfo(
                match.homeLogo,
                match.homeName,
                match.awayLogo,
                match.awayName
            )
            mDatabind.viewPager.initFragment(this, arrayListOf(FootballFragment(0,match), FootballFragment(1,match)),isUserInputEnabled=false)
            mDatabind.magicIndicator.setBackgroundResource(R.drawable.round_indicator_bg)
            mDatabind.magicIndicator.bindBgViewPager2(
                mDatabind.viewPager,
                arrayListOf(getString(R.string.text_live), getString(R.string.import_event)),
                selectSize = 13f,
                unSelectSize = 13f,
                selectColor = com.xcjh.base_lib.R.color.white,
                normalColor = R.color.c_94999f,
                typefaceBold = true,
                scrollEnable = false,
                paddingH = 19.0,
                lineIndicatorColor = R.color.c_323235,
            )
            mDatabind.viewPager.offscreenPageLimit = 2
        }
        loadData()
    }

    override fun lazyLoadData() {
        //loadData()
    }
    private fun loadData() {
        if ("1" == match.matchType) {//1：足球；2：篮球
            //获取足球赛况技术统计表格的数据
            vm.getFootballStatus(match.matchId)
            //获取足球赛况文字直播和重要事件
            vm.getLiveEvent(match.matchId)
            vm.getIncidents(match.matchId)
        }
    }

    override fun onDestroy() {
        MyWsManager.getInstance(App.app)?.removeOtherPushListener(this.toString())
        super.onDestroy()
    }

    override fun createObserver() {
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
                }
            }
        }

    }
}
