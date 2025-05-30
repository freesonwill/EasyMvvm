package com.walisport.module.live.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
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
import com.walisport.module.live.utils.TabMarginExt.reflexMargin
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
        mViewModel.setMatchId(args.matchId)
        mViewModel.setSportId(args.sportId)
        setVideoView()
        loadFragment()
    }

    override fun initListener() {
        with(titleBarBinding) {
            ivBack.clickNoRepeat { findNavController().navigateUp() }
            llcLeagueNameLogo.clickNoRepeat {
                navigate(
                    LiveMainFragmentDirections.actionLiveMainFragmentToLeagueFragment()
                        .apply {
                            mViewModel.matchId.value?.let { value ->
                                arguments.putLong(
                                    "matchID",
                                    value
                                )
                            }
                            mViewModel.leagueID.value?.let { value ->
                                arguments.putInt(
                                    "leagueID",
                                    value
                                )
                            }
                            arguments.putString("leagueName", mViewModel.leagueName.value)
                            arguments.putString("leagueLogo", mViewModel.leagueLogo.value)
                        })
            }

            tvMoney.clickNoRepeat {
                navigate(Uri.parse("walisport://module_topup/topUpFragment"))
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
        //根据matchId变动进行数据刷新
        mViewModel.matchId.observe(viewLifecycleOwner) {
            mViewModel.clearAllMatch()
            mViewModel.getMainMatch(it)
            mViewModel.observeMatchBean(it)
            mViewModel.registerMatchInfoNotify(it)
            mViewModel.registerStatisticsNotify(it)
            mViewModel.observeMatchStaticsNotify()
        }
        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            titleBarBinding.tvMoney.text = "¥ ${it.getFormalMoney()}"
        }
        mViewModel.mainMatch.observe(viewLifecycleOwner) {
            it?.let {
                mViewModel.setLeagueID(it.basicInfo.tournamentId)    //联赛ID
                mViewModel.setLeagueName(it.basicInfo.tournamentName)  //联赛名称
                mViewModel.setLeagueLogo(it.basicInfo.tournamentIcon) //联赛LOGO
                Glide.with(this).load(mViewModel.leagueLogo.value)
                    .error(R.drawable.title_league_icon).into(titleBarBinding.ivLandscapeLeagueIcon)
                titleBarBinding.tvCompetitionName.text = it.basicInfo.matchName
            }
        }
    }


    //比赛ID发生变化,取消订阅,数据请空
    private fun updateMatchId(matchId: Long) {
        mViewModel.matchId.value?.let {
            deleteDataAndSubscriptions(matchId)
            mViewModel.setMatchId(matchId)
        }
    }

    private fun deleteDataAndSubscriptions(matchId: Long) {
        mViewModel.unregisterStatisticsNotify(matchId)
        mViewModel.unregisterMatchInfoNotify(matchId)
        mViewModel.clearAllMatch()
    }

    override fun initData() {
        super.initData()
    }

    private fun setVideoView() {
        childFragmentManager.findFragmentByTag(LiveVideoFragment.TAG) as? LiveVideoFragment
            ?: LiveVideoFragment().also {
                it.arguments = Bundle().apply {
                    mViewModel.matchId.value?.let { value ->
                        putLong(
                            "matchId",
                            value
                        )
                    }
                }
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
                                mViewModel.matchId.value?.let {
                                    putLong(
                                        BetSlipFragment.matchKey,
                                        it
                                    )
                                }
                                mViewModel.sportId.value?.let {
                                    putInt(
                                        BetSlipFragment.sportKey,
                                        it
                                    )
                                }
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
            reflexMargin(mBinding.tabLayout,8.dp2px,8.dp2px,0.dp2px)
            mBinding.tabLayout.getTabAt(1)?.select()
            mBinding.vpPage.setCurrentItem(1, false)
            tabLayout.removeAllTips()
        }
    }

    override fun onDestroyView() {
        mViewModel.matchId.value?.let {
            deleteDataAndSubscriptions(it)
        }
        super.onDestroyView()
    }
}