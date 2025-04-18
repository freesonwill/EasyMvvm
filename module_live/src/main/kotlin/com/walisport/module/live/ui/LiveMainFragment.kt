package com.walisport.module.live.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import arch.cayenne.lib.base.adapter.PagerAdapter
import arch.cayenne.lib.base.data.PagerBean
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.utils.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.removeAllTips
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

    override fun initView(savedInstanceState: Bundle?) {
        val binding =
            TittleBarLiveBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
        mBinding.titleBar.loadDynamicsTitleBar(binding.root) {
            findNavController().navigateUp()
        }
        Glide.with(this).load("").override(96.dp2px, 22.dp2px)
            .error(R.drawable.title_league_icon)           // 加载失败时的占位符
            .into(binding.ivLandscapeLeagueIcon)
        binding.apply {
            tvCompetitionName.text = "中国VS日本"
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

        "matchId:$matchId".logd("LiveMainFragment")
        mViewModel.liveStream(matchId)
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

    private fun setVideoView() {
        childFragmentManager.findFragmentByTag(LiveVideoFragment.TAG)
                as? LiveVideoFragment ?: LiveVideoFragment().also {
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
                PagerBean(R.string.live_outs.getString()) { LiveOutsFragment() },
                PagerBean(R.string.live_lineup.getString()) { LiveLineupFragment() },
                PagerBean(R.string.live_standings.getString()) { LiveStandingsFragment() })
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