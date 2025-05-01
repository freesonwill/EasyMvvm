package com.walisport.module.live.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.database.entity.MatchBean
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.live.R
import com.walisport.module.live.data.MatchPeriodEnum
import com.walisport.module.live.databinding.FragmentLiveMainBinding
import com.walisport.module.live.databinding.TittleBarLiveBinding
import com.walisport.module.live.utils.Timer
import com.walisport.module.live.viewmodel.LiveMainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.reflect.KClass

/**
 * 直播详情页
 */
class LiveMainFragment : BaseFragment<LiveMainViewModel, FragmentLiveMainBinding>() {

    override val vbClass: KClass<FragmentLiveMainBinding> = FragmentLiveMainBinding::class
    override val vmClass: KClass<LiveMainViewModel> = LiveMainViewModel::class
    private val args: LiveMainFragmentArgs by navArgs()
    private var isGone: Boolean = true
    private var binding: TittleBarLiveBinding? = null
    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        binding = TittleBarLiveBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
        mBinding.titleBar.loadDynamicsTitleBar(binding!!.root)
        binding!!.ivBack.clickNoRepeat { findNavController().navigateUp() }
        binding!!.apply {
            tvMoney.text = "¥ 10000.00"
            tvCompetitionName.clickNoRepeat {
            }
            ivLandscapeLeagueIcon.clickNoRepeat {
                navigate(LiveMainFragmentDirections.actionLiveMainFragmentToLeagueFragment())
            }
        }
        setVideoView()
        loadFragment()
        val matchId = args.matchId
        val sportId = args.sportId
        mViewModel.matchId = matchId
        mViewModel.sportId = sportId
    }

    override fun initData() {
        super.initData()
        mViewModel.geMatchMainMatch(mViewModel.matchId)
    }

    override fun initListener() {

    }

    override fun createObserver() {
        mViewModel.matchMainMatch.observe(viewLifecycleOwner) {
            it?.let {
                LogUtils.dTag(TAG, "matchMainMatch----->${it}")
                Glide.with(this).load(it.basicInfo.tournamentIcon)
                    .error(R.drawable.title_league_icon)
                    .into(binding!!.ivLandscapeLeagueIcon)
                binding!!.tvCompetitionName.text =
                    "${it.basicInfo.homeTeam} vs ${it.basicInfo.awayTeam}"
            }
        }
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