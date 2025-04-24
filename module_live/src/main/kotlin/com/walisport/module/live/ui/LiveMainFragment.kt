package com.walisport.module.live.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import arch.cayenne.lib.base.adapter.PagerAdapter
import arch.cayenne.lib.base.data.PagerBean
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.removeAllTips
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveMainBinding
import com.walisport.module.live.databinding.TittleBarLiveBinding
import com.walisport.module.live.viewmodel.LiveMainViewModel
import galaxy.common.proto.Common
import kotlin.reflect.KClass
import androidx.core.view.isGone
import com.walisport.module.live.data.MatchPeriodEnum
import com.walisport.module.live.utils.Timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * 直播详情页
 */
class LiveMainFragment : BaseFragment<LiveMainViewModel, FragmentLiveMainBinding>() {

    override val vbClass: KClass<FragmentLiveMainBinding> = FragmentLiveMainBinding::class
    override val vmClass: KClass<LiveMainViewModel> = LiveMainViewModel::class
    private val args: LiveMainFragmentArgs by navArgs()
    private var isGone : Boolean = true


    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        val binding = TittleBarLiveBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
        mBinding.titleBar.loadDynamicsTitleBar(binding.root)
        binding.ivBack.clickNoRepeat {findNavController().navigateUp() }
        binding.apply {
            tvMoney.text = "¥ 10000.00"
            tvCompetitionName.clickNoRepeat {
                mBinding.llcOuts.visibility = View.VISIBLE
                mBinding.llcOuts.animate()
                    .alpha(if(isGone)0.95f else 0f) // 透明度从当前值渐变到 1（完全可见）
                    .setDuration(200) // 动画持续时间 500 毫秒
                    .start()
                isGone = !isGone
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
        mViewModel.geMatchMainMatch(mViewModel.matchId)
        mViewModel.matchMainMatch.observe(viewLifecycleOwner){
           it?.let {
               LogUtils.dTag(TAG, "matchMainMatch----->${it}")
                           Glide.with(this).load(it.basicInfo.tournamentIcon)
                .error(R.drawable.title_league_icon)
                .into(binding.ivLandscapeLeagueIcon)
            binding.tvCompetitionName.text = "${it.basicInfo.homeTeam} vs ${it.basicInfo.awayTeam}"
               upData(it)
           }
       }

    }

   private fun upData(data:Common.Match){
       //主队
       Glide.with(this).load(data.basicInfo.homeTeamIcon)
           .into(mBinding.outsHomeLogo)
       mBinding.outsHomeName.text = data.basicInfo.homeTeam
       //客队
       Glide.with(this).load(data.basicInfo.awayTeamIcon)
           .into(mBinding.outsAwayLogo)
       mBinding.outsAwayName.text = data.basicInfo.awayTeam
       // bool roll_clock = 2;    //是否走表
       //clock走表时间，以秒为单位
       // int64 clock_modified = 8;  //走表修改时间就是网络延迟时间段。 本地时间戳减去+网络延迟时间段
       //本地时间-clock_modified +clock
       if (data.basicInfo.liveInfo.rollClock){
           val timer = Timer(data.basicInfo.liveInfo.clock.toLong())
           val scope = CoroutineScope(Dispatchers.Default)
           timer.start(scope) { time ->
               mBinding.tvTime.text = time
           }
       }else{
           mBinding.tvScore.text = data.basicInfo.liveInfo.score.ifEmpty { "0 - 0" }
           mBinding.tvPeriod.text =
               MatchPeriodEnum.fromCode(data.basicInfo.liveInfo.period)?.description ?: ""
       }
       mBinding.tvTime.visibility =  if (data.basicInfo.liveInfo.rollClock)View.VISIBLE else View.GONE
       mBinding.tvToday.visibility =  if (data.basicInfo.liveInfo.rollClock)View.VISIBLE else View.GONE
       mBinding.tvScore.visibility = if (!data.basicInfo.liveInfo.rollClock)View.VISIBLE else View.GONE
       mBinding.tvPeriod.visibility =  if (!data.basicInfo.liveInfo.rollClock)View.VISIBLE else View.GONE
    }



    override fun initListener() {

    }

    override fun createObserver() {

    }

    private fun setVideoView() {
        childFragmentManager.findFragmentByTag(LiveVideoFragment.TAG)
                as? LiveVideoFragment ?: LiveVideoFragment().also {
                    arguments?.putLong("matchId", args.matchId)
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