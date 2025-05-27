package com.walisport.module.live.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.data.StatusBarEnum
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.module.betslip.ui.fragment.BetSlipFragment
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveMainBinding
import com.walisport.module.live.databinding.TitleBarLiveBinding
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import kotlin.reflect.KClass

/**
 * 直播详情页
 */

class LiveMainFragment : BaseFragment<LiveMainViewModel, FragmentLiveMainBinding>() {

    override val vbClass: KClass<FragmentLiveMainBinding> = FragmentLiveMainBinding::class
    override val vmClass: KClass<LiveMainViewModel> = LiveMainViewModel::class
    private val args: LiveMainFragmentArgs by navArgs()

    private val titleBarBinding: TitleBarLiveBinding by lazy {
        TitleBarLiveBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadDynamicsTitleBar(titleBarBinding.root)
        setVideoView()
        loadFragment()
        mViewModel.matchId = args.matchId
        mViewModel.sportId = args.sportId
        StatusBarConfig.statusBarType = StatusBarEnum.DEFAULT
        setStatusBar(StatusBarConfig,mBinding.root)
    }

    override fun initListener() {
        with(titleBarBinding) {
            ivBack.clickNoRepeat { findNavController().navigateUp() }
            tvCompetitionName.clickNoRepeat {
                navigate(
                    LiveMainFragmentDirections.actionLiveMainFragmentToLeagueFragment()
                        .apply {
                            arguments.putLong("matchID", mViewModel.matchId)
                            arguments.putInt("leagueID", mViewModel.leagueID)
                        })
            }
            ivLandscapeLeagueIcon.clickNoRepeat {
                navigate(
                    LiveMainFragmentDirections.actionLiveMainFragmentToLeagueFragment()
                        .apply {
                            arguments.putLong("matchID", mViewModel.matchId)
                            arguments.putInt("leagueID", mViewModel.leagueID)
                        }
                )
            }
        }
        mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    @SuppressLint("SetTextI18n")
    override fun createObserver() {
        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            titleBarBinding.tvMoney.text = "¥ ${it.getFormalMoney()}"
        }
        mViewModel.mainMatch.observe(viewLifecycleOwner) {
            it?.let {
                mViewModel.leagueID = it.basicInfo.tournamentId //联赛ID
                Glide.with(this).load(it.basicInfo.tournamentIcon)
                    .error(R.drawable.title_league_icon).into(titleBarBinding.ivLandscapeLeagueIcon)
                titleBarBinding.tvCompetitionName.text = it.basicInfo.matchName
            }
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getMainMatch(mViewModel.matchId)
        mViewModel.observeMatchBean(mViewModel.matchId)
        mViewModel.registerMatchInfoNotify(mViewModel.matchId)
        mViewModel.registerStatisticsNotify(123456L)
        mViewModel.observeMatchStaticsNotify()
    }

    private fun setVideoView() {
        childFragmentManager.findFragmentByTag(LiveVideoFragment.TAG) as? LiveVideoFragment
            ?: LiveVideoFragment().also {
                it.arguments = Bundle().apply { putLong("matchId", args.matchId) }
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentVideo.id, it, LiveVideoFragment.TAG).commitNow()
            }
    }

    private fun loadFragment() {
        with(mBinding) {
            mBinding.tabLayout.removeAllTabs()
            val list =
                listOf(
                    PagerBean(R.string.live_note_order.getString()) {
                        BetSlipFragment().apply {
                            arguments = Bundle().apply {
                                putLong(BetSlipFragment.matchKey, args.matchId)
                                putInt(BetSlipFragment.sportKey, args.sportId)
                            }
                        }
                    },
                    PagerBean(R.string.live_bet_on.getString()) { LiveBetOnFragment() },
                    PagerBean(R.string.live_chat.getString()) { LiveChatFragment() },
                    PagerBean(R.string.live_outs.getString()) { LiveOutsFragment() },
                    PagerBean(R.string.live_lineup.getString()) { LiveLineupFragment() },
                    PagerBean(R.string.live_standings.getString()) { LiveStandingsFragment() })
            vpPage.adapter = null
            vpPage.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
            vpPage.offscreenPageLimit = list.size
            TabLayoutMediator(tabLayout, vpPage) { tab, position ->
                val tabView = tab.view
                tab.text = list[position].title
                tabView.setOnClickListener {}
            }.attach()
            mBinding.tabLayout.getTabAt(1)?.select()
            mBinding.vpPage.setCurrentItem(1,false)
            tabLayout.removeAllTips()
        }
    }

    override fun onDestroyView() {
        mViewModel.unregisterStatisticsNotify()
        mViewModel.unregisterMatchInfoNotify(mViewModel.matchId)
        mViewModel.clearAllMatch()
        super.onDestroyView()
    }

    override fun onResume() {
        StatusBarConfig.statusBarType = StatusBarEnum.DEFAULT
        setStatusBar(StatusBarConfig,mBinding.root)
        super.onResume()
    }
}