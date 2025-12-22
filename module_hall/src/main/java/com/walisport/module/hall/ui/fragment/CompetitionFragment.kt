package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.BizUrl
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.utils.DateUtils
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.ccyToSymbol
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.hall.R
import com.walisport.module.hall.data.addDashItem
import com.walisport.module.hall.databinding.FragmentCompetitionBinding
import com.walisport.module.hall.databinding.TitleBarCompetitionBinding
import com.walisport.module.hall.ui.adapter.GameAllRankingListTodayAdapter
import com.walisport.module.hall.ui.viewmodel.CompetitionViewModel
import kotlin.reflect.KClass

class CompetitionFragment : BaseFragment<CompetitionViewModel , FragmentCompetitionBinding>() {
    override val vbClass: KClass<FragmentCompetitionBinding> = FragmentCompetitionBinding::class
    override val vmClass: KClass<CompetitionViewModel> = CompetitionViewModel::class
    private val titleBarBinding: TitleBarCompetitionBinding by lazy {
        TitleBarCompetitionBinding.inflate(
            LayoutInflater.from(context) ,
            mBinding.titleBar ,
            false
        )
    }

    private val adapter by lazy {
        GameAllRankingListTodayAdapter()
    }

    private var timer: CountDownTimer? = null


    override fun initView(savedInstanceState: Bundle?) {

        with(mBinding) {
            titleBar.loadDynamicsTitleBar(titleBarBinding.root , null)
            titleBarBinding.tvTitleName.text = getString(R.string.title_daily_match)

            rvGame.layoutManager = LinearLayoutManager(requireContext())
            rvGame.itemAnimator = null
            rvGame.adapter = adapter

        }
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {
        with(titleBarBinding) {
            ivBack.addScaleOnTouchAnimation()
            ivBack.clickNoRepeat {
                findNavController().navigateUp()
            }

            ivQuestion.addScaleOnTouchAnimation()
            ivQuestion.clickNoRepeat {
                //跳转到帮助页面
                navigate(arch.cayenne.lib.res.R.string.nav_module_web_fragment.deeplink("url" to BizUrl.HELP.url))
            }

        }

        with(mBinding) {
            ivHelper.addScaleOnTouchAnimation()
            ivHelper.clickNoRepeat {
                //跳转到帮助页面
                navigate(arch.cayenne.lib.res.R.string.nav_module_web_fragment.deeplink("url" to BizUrl.HELP.url))
            }
        }

    }


    override suspend fun createObserver() {

        mViewModel.apiStateListener.observe(viewLifecycleOwner) { state ->
            when (state) {
                DataState.LoadSuccess -> {
                    mBinding.rvGame.visibility = View.VISIBLE
                    mBinding.clDynamics.visibility = View.GONE
                }

                DataState.DataEmpty -> {
                    mBinding.rvGame.visibility = View.GONE
                    mBinding.clDynamics.visibility = View.VISIBLE
                    mBinding.clDynamics.setState(
                        States.DATA_EMPTY ,
                        arch.cayenne.lib.common.R.string.data_empty.getString()
                    )

                }

                DataState.NoMoreData -> {
                    mBinding.rvGame.visibility = View.VISIBLE
                    mBinding.clDynamics.visibility = View.GONE
                }

                DataState.NetworkUnavailable -> {
                    mBinding.rvGame.visibility = View.GONE
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