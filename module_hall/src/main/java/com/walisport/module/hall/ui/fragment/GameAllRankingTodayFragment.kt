package com.walisport.module.hall.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.data.constants.BizUrl
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.utils.DateUtils
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.ccyToSymbol
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.hall.data.addDashItem
import com.walisport.module.hall.databinding.FragmentGameAllRankingTodayBinding
import com.walisport.module.hall.ui.adapter.GameAllRankingListTodayAdapter
import com.walisport.module.hall.ui.viewmodel.CompetitionViewModel
import kotlin.reflect.KClass
import arch.cayenne.lib.common.utils.ext.DimensionExt.px2dp
class GameAllRankingTodayFragment :
    BaseFragment<CompetitionViewModel , FragmentGameAllRankingTodayBinding>() {
    companion object {
        fun newInstance() = GameAllRankingTodayFragment()
    }

    override val vbClass: KClass<FragmentGameAllRankingTodayBinding> =
        FragmentGameAllRankingTodayBinding::class
    override val vmClass: KClass<CompetitionViewModel> = CompetitionViewModel::class

    private val adapter by lazy {
        GameAllRankingListTodayAdapter()
    }

    private var timer: CountDownTimer? = null
    fun getContentHeight(): Int {
        return 2246
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            rvTodayRank.layoutManager = LinearLayoutManager(requireContext())
            rvTodayRank.itemAnimator = null
            rvTodayRank.adapter = adapter
        }
    }

    override fun initListener() {
        mBinding.clBanner.clickNoRepeat {
            navigate(arch.cayenne.lib.res.R.string.nav_module_competition_fragment.deeplink())
        }

        mBinding.ivHelp.addScaleOnTouchAnimation()
        mBinding.ivHelp.clickNoRepeat {
            //到帮助页
            navigate(arch.cayenne.lib.res.R.string.nav_module_web_fragment.deeplink("url" to BizUrl.HELP.url))
        }
    }

    override suspend fun createObserver() {
        mViewModel.apiStateListener.observe(viewLifecycleOwner) { state ->
            when (state) {
                DataState.LoadSuccess -> {
                    mBinding.rvTodayRank.visibility = View.VISIBLE
                    mBinding.clDynamics.visibility = View.GONE
                }

                DataState.DataEmpty -> {
                    mBinding.rvTodayRank.visibility = View.GONE
                    mBinding.clDynamics.visibility = View.VISIBLE
                    mBinding.clDynamics.setState(
                        States.DATA_EMPTY ,
                        arch.cayenne.lib.common.R.string.data_empty.getString()
                    )

                }

                DataState.NoMoreData -> {
                    mBinding.rvTodayRank.visibility = View.VISIBLE
                    mBinding.clDynamics.visibility = View.GONE
                }

                DataState.NetworkUnavailable -> {
                    mBinding.rvTodayRank.visibility = View.GONE
                    mBinding.clDynamics.visibility = View.VISIBLE
                    mBinding.clDynamics.setState(
                        States.NETWORK_ANOMALY() ,
                        arch.cayenne.lib.common.R.string.error_net.getString()
                    )
                }

                else -> {
                }
            }
        }

        mViewModel.rankingListLiveData.observe(viewLifecycleOwner) { gameList ->
            adapter.submitList(gameList.addDashItem())
        }

        mViewModel.dailyMatchLiveData.observe(viewLifecycleOwner) {
            mBinding.tvTimer.text = DateUtils.formatMillisToHMS(it.remainingTime)
            //启动定时器，每秒对剩余时间进行减一，并更新UI
            var remainingTime = it.remainingTime
            timer?.cancel()
            timer = object : CountDownTimer(remainingTime * 1000 , 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    remainingTime--
                    mBinding.tvTimer.text = DateUtils.formatMillisToHMS(remainingTime)
                }

                override fun onFinish() {
                    mBinding.tvTimer.text = DateUtils.formatMillisToHMS(0)
                }
            }
            timer?.start()

            mBinding.tvCurrencySymbol.text = it.ccy.ccyToSymbol()
            mBinding.tvBonus.text = String.format("%,d" , it.betScore)
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getDayMatchDetail()
        mViewModel.queryDailyMatchList()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }


}