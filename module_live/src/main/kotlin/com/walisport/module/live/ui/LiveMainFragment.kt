package com.walisport.module.live.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.skin.widget.SkinnableTextView
import arch.cayenne.module.betslip.ui.fragment.BetSlipFragment
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveMainBinding
import com.walisport.module.live.databinding.TitleBarLiveBinding
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import com.walisport.module.live.utils.TextViewExt.setBottomDrawable
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.helper.ViewPagerAnimHelper
import arch.cayenne.lib.websocket.data.ConnectState
import com.walisport.module.live.data.BetOnMenuStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

/**
 * 直播详情页
 */

class LiveMainFragment : BaseFragment<LiveMainViewModel, FragmentLiveMainBinding>() {

    override val vbClass: KClass<FragmentLiveMainBinding> = FragmentLiveMainBinding::class
    override val vmClass: KClass<LiveMainViewModel> = LiveMainViewModel::class
    private lateinit var args: LiveMainFragmentArgs
    private var drawerContentFragment: LiveBetOnMenuFragment? = null
    private val titleBarBinding: TitleBarLiveBinding by lazy {
        TitleBarLiveBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }
    private var switchTabAnimJob: Job? = null
    private val viewPagerAnimHelper by lazy {
        ViewPagerAnimHelper()
    }

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        args = LiveMainFragmentArgs.fromBundle(requireArguments())
        mBinding.titleBar.loadDynamicsTitleBar(titleBarBinding.root)
        mViewModel.setMatchId(args.matchId)
        mViewModel.setSportId(args.sportId)
        setVideoView()
        loadFragment()
    }

    //init DrawerLayout Content
    private fun drawerContent() {
        //蒙層顏色依照版型作變化
        mBinding.drawerLayout.setScrimColor(
            SkinnableResourceManager.getColor(
                requireContext(),
                R.color.drawer_scrim_color
            )
        )
        if (drawerContentFragment == null) {
            drawerContentFragment = LiveBetOnMenuFragment()
        }
        childFragmentManager.beginTransaction()
            .replace(
                mBinding.fragmentDrawerContent.id,
                drawerContentFragment!!,
                LiveBetOnMenuFragment.TAG
            )
            .commitNow()
        mBinding.drawerLayout.openDrawer(GravityCompat.END)
    }

    override fun initListener() {
        with(titleBarBinding) {
            ivBack.clickNoRepeat {
                //软件盘开启后直接关闭软件盘，不返回
                if (isSoftKeyBoardVisible()) {
                    return@clickNoRepeat
                }
                findNavController().navigateUp()
            }
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
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    switchTabAnimJob?.cancel()
                    switchTabAnimJob = viewPagerAnimHelper.doViewPagerAnim(
                        targetPosition = tab.position,
                        viewPager = mBinding.vpPage,
                        fakeViewPager = mBinding.fragmentFakeViewPager
                    )
                    mBinding.vpPage.setCurrentItem(it.position, false)
                }
                tab?.view?.findViewById<SkinnableTextView>(R.id.tabText)?.let { textView ->
                    textView.setTextColor(
                        SkinnableResourceManager.getColor(
                            textView.context,
                            R.color.tab_selected_text_color
                        )
                    )
                    textView.typeface = Typeface.DEFAULT_BOLD
                    textView.setBottomDrawable(context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.live_tab_indicator
                        )
                    }, 4.dp2px)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.findViewById<SkinnableTextView>(R.id.tabText)?.let { textView ->
                    textView.setTextColor(
                        SkinnableResourceManager.getColor(
                            textView.context,
                            R.color.video_tab_text_color
                        )
                    )
                    textView.typeface = Typeface.DEFAULT
                    textView.setBottomDrawable(context?.let {
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.live_tab_indicatort_tan
                        )
                    }, 3.dp2px)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle reselect if needed
            }
        })
    }

    @SuppressLint("SetTextI18n")
    override fun createObserver() {
        mViewModel.liveBetOnMenu.observe(viewLifecycleOwner) {
            when (it) {
                BetOnMenuStatus.OPEN -> {
                    drawerContent()
                }

                BetOnMenuStatus.CLOSE -> {
                    mBinding.drawerLayout.closeDrawer(GravityCompat.END)
                }
            }
        }
        //根据matchId变动进行数据刷新
        mViewModel.matchId.observe(viewLifecycleOwner) {
            mViewModel.clearAllMatch()
            mViewModel.getMainMatch(it)
            mViewModel.observeMatchBean(it)
            mViewModel.registerMatchInfoNotify(it)
        }
        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            titleBarBinding.tvMoney.text =
                "${CurrencySymbols.getSymbol(it.currency)} ${it.balance.getFormalMoney()}"
        }
        mViewModel.mainMatch.observe(viewLifecycleOwner) {
            it?.let {
                mViewModel.setLeagueID(it.basicInfo.tournamentId)     //联赛ID
                mViewModel.setLeagueName(it.basicInfo.tournamentName) //联赛名称
                val logo = it.basicInfo.tournamentIcon                //联赛LOGO
                mViewModel.setLeagueLogo(logo)
                if (TextUtils.isEmpty(logo)) {
                    titleBarBinding.ivLandscapeLeagueIcon.visibility = View.GONE
                } else {
                    titleBarBinding.ivLandscapeLeagueIcon.visibility = View.VISIBLE
                    Glide.with(this).load(logo).into(titleBarBinding.ivLandscapeLeagueIcon)
                }
                titleBarBinding.tvCompetitionName.text = it.basicInfo.matchName
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            mViewModel.matchIdSportIdObserver.collect {
                refreshBetSlip()
            }
        }
        launch {
            mViewModel.observeConnectStateFlow().collect {
                //监听连接变化
                when (it) {
                    //网络异常
                    ConnectState.NetworkUnavailable -> {
                        mBinding.liveMain.visibility = View.GONE
                        mBinding.clDynamics.setState(
                            States.NETWORK_ANOMALY,
                            arch.cayenne.lib.common.R.string.error_net.getString()
                        ) {
                            mViewModel.reconnect()
                        }
                    }
                    //连接成功
                    ConnectState.ConnectSuccess -> {
                        mBinding.liveMain.visibility = View.VISIBLE
                        mBinding.clDynamics.setVisibilityGone()
                        mViewModel.matchId.value?.let { matchId ->
                            mViewModel.registerMatchInfoNotify(matchId)
                            mViewModel.registerStatisticsNotify(matchId)
                            mViewModel.observeMatchStaticsNotify()
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    //比赛ID发生变化,取消订阅,数据请空
    private fun updateMatchId(matchId: Long) {
        mBinding.tabLayout.getTabAt(1)?.select()
        mBinding.vpPage.setCurrentItem(1, false)
        mViewModel.matchId.value?.let {
            deleteDataAndSubscriptions(matchId)
            mViewModel.setMatchId(matchId)
        }
    }

    private fun deleteDataAndSubscriptions(matchId: Long) {
        mViewModel.unregisterMatchInfoNotify(matchId)
        mViewModel.clearAllMatch()
    }

    override fun initData() {
        super.initData()
        launch(Lifecycle.State.RESUMED) {
            mViewModel.startChatServer()
        }
    }

    private fun setVideoView() {
        childFragmentManager.findFragmentByTag(VideoMainFragment.TAG) as? VideoMainFragment
            ?: VideoMainFragment().also {
                it.arguments = Bundle().apply {
                    mViewModel.matchId.value?.let { value ->
                        putLong(
                            "matchId",
                            value
                        )
                    }
                }
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentVideo.id, it, VideoMainFragment.TAG).commitNow()
            }
    }

    private fun loadFragment() {
        var tabSelectPosition = 1
        with(mBinding) {
            mBinding.tabLayout.removeAllTabs()
            val list =
                listOf(
                    PagerBean(R.string.live_note_order.getString()) { BetSlipFragment() },
                    PagerBean(R.string.live_bet_on.getString()) { LiveBetOnFragment() },
                    PagerBean(R.string.live_chat.getString()) { LiveChatFragment() },
                    PagerBean(R.string.live_outs.getString()) { LiveOutsFragment() },
                    PagerBean(R.string.live_lineup.getString()) { LiveLineupFragment() },
                    PagerBean(R.string.live_standings.getString()) { LiveStandingsFragment() })
            vpPage.adapter = null
            vpPage.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
            launch(Lifecycle.State.RESUMED) {
                delay(500)
                vpPage.offscreenPageLimit = list.size
            }
            TabLayoutMediator(tabLayout, vpPage, false) { tab, position ->
                tab.text = list[position].title
                tab.setCustomView(R.layout.custom_tab)
                tab.customView?.findViewById<SkinnableTextView>(R.id.tabText)?.apply {
                    setBottomDrawable(context?.let {
                        ContextCompat.getDrawable(
                            it,
                            if (position == tabSelectPosition) R.drawable.live_tab_indicator else R.drawable.live_tab_indicatort_tan
                        )
                    }, 4.dp2px)
                    text = list[position].title
                    setTextColor(
                        SkinnableResourceManager.getColor(
                            context,
                            if (position == tabSelectPosition) R.color.tab_selected_text_color else R.color.video_tab_text_color
                        )
                    )
                    typeface =
                        if (position == tabSelectPosition) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

                }
                tab.view.setOnClickListener { /* Handle click */ }
            }.attach()
            mBinding.tabLayout.getTabAt(1)?.select()
            mBinding.vpPage.setCurrentItem(1, false)
            tabLayout.removeAllTips()
        }
    }

    fun refreshBetSlip() {
        val adapter = mBinding.vpPage.adapter?.let { it as PagerAdapter }
        val tag = "f${adapter?.getItemId(0)}"
        val fragment = childFragmentManager.findFragmentByTag(tag)?.let { it as BetSlipFragment }
        fragment?.refreshBetSlip(mViewModel.matchId.value ?: -1, mViewModel.sportId.value ?: -1)
    }

    private fun isSoftKeyBoardVisible(): Boolean {
        val adapter = mBinding.vpPage.adapter?.let { it as PagerAdapter }
        val index = adapter!!.pages.indexOfFirst { it.title == R.string.live_chat.getString() }
        val tag = "f${adapter.getItemId(index)}"
        val fragment = childFragmentManager.findFragmentByTag(tag)?.let { it as LiveChatFragment }
        val flag = fragment?.isSoftKeyboardVisible() ?: false
        return flag
    }


    //离开界面取消订阅
    override fun onPause() {
        super.onPause()
        mViewModel.matchId.value?.let {
            mViewModel.unregisterMatchInfoNotify(it)
        }
    }

    //重新进入界面发起订阅
    override fun onResume() {
        super.onResume()
        mViewModel.matchId.value?.let {
            mViewModel.registerMatchInfoNotify(it)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val newArgs: LiveMainFragmentArgs = LiveMainFragmentArgs.fromBundle(intent.extras!!)
        "onNewIntent-->newArgs--->$newArgs,args:${args},extras:${intent.extras},${
            this.args.equal(
                newArgs
            )
        }".logd(TAG)
        if (this.args.equal(newArgs)) return
        this.args = newArgs
        updateMatchId(newArgs.matchId)
    }

    override fun onStop() {
        super.onStop()
        mViewModel.disConnectChatServer()
    }

    override fun onDestroyView() {
        switchTabAnimJob = null
        mViewModel.matchId.value?.let {
            deleteDataAndSubscriptions(it)
        }
        super.onDestroyView()
    }

    private fun LiveMainFragmentArgs.equal(other: Any?): Boolean {
        if (other !is LiveMainFragmentArgs) return false
        return this.sportId == other.sportId && this.matchId == other.matchId
    }

    override fun onBackPressed(): Boolean {
        //如果抽屉打开，截获此次返回事件，关闭抽屉
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.END)
            return true
        }
        return super.onBackPressed()
    }
}