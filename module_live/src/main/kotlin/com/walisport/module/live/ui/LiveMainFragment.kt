package com.walisport.module.live.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.removeAllTips
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveMainBinding
import com.walisport.module.live.databinding.TittleBarLiveBinding
import com.walisport.module.live.viewmodel.LiveMainViewModel
import kotlin.reflect.KClass

/**
 * 直播详情页
 */
class LiveMainFragment : BaseFragment<LiveMainViewModel, FragmentLiveMainBinding>() {

    override val vbClass: KClass<FragmentLiveMainBinding> = FragmentLiveMainBinding::class
    override val vmClass: KClass<LiveMainViewModel> = LiveMainViewModel::class
    private val args: LiveMainFragmentArgs by navArgs()
    private var leagueID: Int = 0

    private val titleBarBinding: TittleBarLiveBinding by lazy {
        TittleBarLiveBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadDynamicsTitleBar(titleBarBinding.root)
        setVideoView()
        loadFragment()
        val matchId = args.matchId
        val sportId = args.sportId
        mViewModel.matchId = matchId
        mViewModel.sportId = sportId
    }

    override fun initListener() {
        with(titleBarBinding) {
            ivBack.clickNoRepeat { findNavController().navigateUp() }
            ivLandscapeLeagueIcon.clickNoRepeat {
                navigate(LiveMainFragmentDirections.actionLiveMainFragmentToLeagueFragment(leagueID))
            }
            tvCompetitionName.clickNoRepeat {
                navigate(LiveMainFragmentDirections.actionLiveMainFragmentToLeagueFragment(leagueID))
            }
        }
    }

    override fun createObserver() {
        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            titleBarBinding.tvMoney.text = it.getFormalMoney()
        }

        mViewModel.mainMatch.observe(viewLifecycleOwner) {
            it?.let {
                "matchMainMatch----->${it}".logd(TAG)
                leagueID = it.basicInfo.tournamentId //联赛ID
                Glide.with(this).load(it.basicInfo.tournamentIcon)
                    .error(R.drawable.title_league_icon)
                    .into(titleBarBinding.ivLandscapeLeagueIcon)
                titleBarBinding.tvCompetitionName.text =
                    it.basicInfo.matchName
            }
        }
    }


    override fun initData() {
        super.initData()
        mViewModel.getMainMatch(mViewModel.matchId)

    }


    private fun setVideoView() {
        childFragmentManager.findFragmentByTag(LiveVideoFragment.TAG)
                as? LiveVideoFragment ?: LiveVideoFragment().also {
            it.arguments = Bundle().apply { putLong("matchId", args.matchId) }

            childFragmentManager.beginTransaction()
                .replace(mBinding.fragmentVideo.id, it, LiveVideoFragment.TAG)
                .commitNow()
        }
    }

    private fun loadFragment() {
        with(mBinding) {
            val list = listOf(
                PagerBean(R.string.live_note_order.getString()) { LiveBetSlipFragment() },
                PagerBean(R.string.live_bet_on.getString()) { LiveBetOnFragment() },
                PagerBean(R.string.live_chat.getString()) { LiveChatFragment() },
                PagerBean(R.string.live_outs.getString()) {
                    LiveOutsFragment().also {
                        it.arguments = Bundle().apply { putLong("matchId", args.matchId) }
                    }
                },
                PagerBean(R.string.live_lineup.getString()) { LiveLineupFragment() },
                PagerBean(R.string.live_standings.getString()) {
                    LiveStandingsFragment().also {
                        it.arguments = Bundle().apply { putLong("matchId", args.matchId) }
                    }
                })
            vpPage.adapter = null
            vpPage.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
            vpPage.offscreenPageLimit = list.size
            TabLayoutMediator(tabLayout, vpPage) { tab, position ->
                val tabView = tab.view
                tab.text = list[position].title
                tabView.setOnClickListener {
                }
            }.attach()
            tabLayout.removeAllTips()
        }
    }
}