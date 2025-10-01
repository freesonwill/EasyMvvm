package com.walisport.module.live.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.ViewExt.applyInsetsForFitsSystemWindows
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.common.utils.CustomTabIndicatorUtils
import arch.cayenne.lib.common.utils.ImmersionBarUtils.immersionBarSkinTypeExt
import arch.cayenne.lib.common.utils.ext.DimensionExt.px2sp
import arch.cayenne.lib.common.utils.ext.setDrawerInterpolator
import arch.cayenne.lib.common.utils.ext.NavResultExt.observeResult
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.skin.widget.SkinnableTextView
import arch.cayenne.module.betslip.ui.fragment.BetSlipFragment
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.live.R
import com.walisport.module.live.data.BetOnMenuStatus
import com.walisport.module.live.databinding.FragmentLiveMainBinding
import com.walisport.module.live.databinding.TitleBarLiveBinding
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlin.reflect.KClass
import arch.cayenne.lib.common.utils.ext.setupViewPagerScroll
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import arch.cayenne.module.bet.ui.fragment.BetSheetFragment
import arch.cayenne.module.chat.ui.LiveChatFragment

/**
 * 直播详情页
 */

class LiveMainFragment : BaseFragment<LiveMainViewModel, FragmentLiveMainBinding>() {
    companion object {
        const val CHANGE_MATCH = "CHANGE_MATCH"
    }

    override val vbClass: KClass<FragmentLiveMainBinding> = FragmentLiveMainBinding::class
    override val vmClass: KClass<LiveMainViewModel> = LiveMainViewModel::class
    private lateinit var args: LiveMainFragmentArgs
    private var drawerContentFragment: LiveBetOnMenuFragment? = null
    private val titleBarBinding: TitleBarLiveBinding by lazy {
        TitleBarLiveBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }
    private val fixedSkin:String? = null //SkinType.getLogicSkinType(SkinType.SKIN_BLACK_RED.value)

    override fun initView(savedInstanceState: Bundle?) {
        args = LiveMainFragmentArgs.fromBundle(requireArguments())
        mBinding.titleBar.loadDynamicsTitleBar(titleBarBinding.root)
        mViewModel.setMatchId(args.matchId)
        mViewModel.setSportId(args.sportId)
        setVideoView()
        loadFragment()
        mViewModel.observeMatchInfoNotify()
        mBinding.drawerLayout.setDrawerLockMode(
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
            GravityCompat.END
        )
        mBinding.root.applyInsetsForFitsSystemWindows()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        SkinnableResourceManager.setFixedSkin(fixedSkin)
        lifecycle.addObserver(object :DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                if(fixedSkin != null){
                    SkinnableResourceManager.setFixedSkin(null)
                    StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
                    StatusBarConfig.statusBarDarkFont = immersionBarSkinTypeExt(mViewModel.getSkinType())
                    setStatusBar(StatusBarConfig,mBinding.root)
                }
            }

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                mBinding.root.fitsSystemWindows = fixedSkin == null
                if(fixedSkin != null){
                    SkinnableResourceManager.setFixedSkin(fixedSkin)
                    StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
                    StatusBarConfig.statusBarDarkFont = false
                    setStatusBar(StatusBarConfig,mBinding.root)
                    updateBetSheetSkin() //refresh skin to fixed skin
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                lifecycle.removeObserver(this)
                if(fixedSkin != null) updateBetSheetSkin() //restore skin to system skin
            }
        })
    }

    //init DrawerLayout Content
    private fun drawerContent() {
        mBinding.drawerLayout.setIsAnimationRunning(false)
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
        mBinding.liveMain.touchBackPressed()
        with(titleBarBinding) {
            ivBack.clickNoRepeat {
                //软件盘开启后直接关闭软件盘，不返回
                if (isSoftKeyBoardVisible()) {
                    return@clickNoRepeat
                }
                findNavController().navigateUp()
            }
            llcLeagueNameLogo.clickNoRepeat {
                val nav = findNavController()
                val dest = R.id.leagueFragment
                if (nav.currentDestination?.id != dest) {
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
                            },
                        enterAnim = AnimationController[AnimType.routeEnterTB],
                        exitAnim = AnimationController[AnimType.routeExitTB],
                        popEnterAnim = AnimationController[AnimType.routePopEnterTB],
                        popExitAnim = AnimationController[AnimType.routePopExitTB],
                    )
                }
            }

            tvMoney.clickNoRepeat {
                navigate(Uri.parse("walisport://module_topup/topUpFragment"))
            }
        }
        mBinding.tabLayout.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
            override fun onTabSelected(tab: TabLayout.Tab,isTabClick: Boolean) {
                tab.let {
                    if (isTabClick) {
                        CustomTabIndicatorUtils.animateIndicatorToPosition(mBinding.customIndicator,tab.position)
                        val vp = mBinding.vpPage
                        vp.startFadeAnim {
                            vp.setCurrentItem(tab.position, false)
                            it.invoke()
                        }
                    }
                }
                tab.view.findViewById<SkinnableTextView>(R.id.tabText)?.let { textView ->
                    textView.setTextColor(
                        SkinnableResourceManager.getColor(
                            textView.context,
                            R.color.tab_selected_text_color
                        )
                    )
                    textView.textSize = 15f.px2sp
                    textView.typeface = Typeface.DEFAULT_BOLD
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab,isTabClick: Boolean) {
                tab.view.findViewById<SkinnableTextView>(R.id.tabText)?.let { textView ->
                    textView.setTextColor(
                        SkinnableResourceManager.getColor(
                            textView.context,
                            R.color.video_tab_text_color
                        )
                    )
                    textView.textSize = 15f.px2sp
                    textView.typeface = Typeface.DEFAULT
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab,isTabClick: Boolean) {
                // Handle reselect if needed
            }
        })
        mBinding.vpPage.setupViewPagerScroll(mBinding.tabLayout,mBinding.customIndicator,0.45f)
    }

    override fun createObserverAtState(): Lifecycle.State {
        return Lifecycle.State.RESUMED
    }

    @SuppressLint("SetTextI18n")
    override suspend fun createObserver() {
        launch {
            AnimationController.getFlow(AnimType.drawerEnter).collect {
                if(it == null) return@collect
                mBinding.drawerLayout.setDrawerInterpolator(it.duration, it.interpolator.toInterpolator())
            }
        }
        observeResult<Bundle>(CHANGE_MATCH) {
            val newArgs: LiveMainFragmentArgs = LiveMainFragmentArgs.fromBundle(it)
            "observeResult-->newArgs--->$newArgs,args:${args},extras:${it},${this.args.equal(newArgs)}".logd(TAG)
            if (this.args.equal(newArgs)) return@observeResult
            this.args = newArgs
            updateMatchId(newArgs.matchId)
        }
        mViewModel.observeMatchInfoNotify()
        launch(Lifecycle.State.RESUMED) {
            //网络异常登陆成功后才获取数据
            mViewModel.observeLoginChange()
                .filter { it && mViewModel.apiStateListener.value == DataState.NetworkUnavailable }
                .collect {
                    if (it) {
                        mViewModel.matchId.value?.let { matchId ->
                            mViewModel.registerMatchInfoNotify(matchId)
                            mViewModel.registerStatisticsNotify(matchId)
                            if (mViewModel.mainMatch.value == null) {
                                mViewModel.getMainMatch(matchId)
                            }
                        }
                    }
                }
        }
        mViewModel.liveBetOnMenu.observe(viewLifecycleOwner) {
            when (it!!) {
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
                "${CurrencySymbols.getSymbol(it?.currency ?: "")}${(it?.balance ?: 0L).getFormalMoney()}"
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
        launch(Lifecycle.State.RESUMED) {
            mViewModel.matchIdSportIdObserver.collect {
                refreshBetSlip()
            }
        }
    }

    //比赛ID发生变化,取消订阅,数据请空
    private fun updateMatchId(matchId: Long) {
        mBinding.vpPage.setCurrentItem(1,false)
        mBinding.tabLayout.getTabAt(1)?.select()
        CustomTabIndicatorUtils.animateIndicatorToPosition(mBinding.customIndicator,1,false)
        mViewModel.matchId.value?.let {
            deleteDataAndSubscriptions(matchId)
            mViewModel.setMatchId(matchId)
        }
    }

    private fun deleteDataAndSubscriptions(matchId: Long) {
        mViewModel.unregisterMatchInfoNotify(matchId)
        mViewModel.clearAllMatch()
    }

    private fun setVideoView() {
        childFragmentManager.findFragmentByTag(LiveMatchMediaFragment.TAG) as? LiveMatchMediaFragment
            ?: LiveMatchMediaFragment().also {
                it.arguments = Bundle().apply {
                    mViewModel.matchId.value?.let { value ->
                        putLong("matchId", value)
                    }
                }
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentVideo.id, it, LiveMatchMediaFragment.TAG)
                    .commitNow()
            }
    }

    private fun loadFragment() {
        val tabSelectPosition = 1
        with(mBinding) {
            val list = listOf(
                PagerBean(arch.cayenne.lib.res.R.string.bet_title.getString()) { BetSlipFragment() },
                PagerBean(R.string.live_bet_on.getString()) { LiveBetOnFragment() },
                PagerBean(R.string.live_chat.getString()) { createChatFragment() },
                PagerBean(R.string.live_outs.getString()) { LiveOutsFragment() },
                PagerBean(R.string.live_lineup.getString()) { LiveLineupFragment() },
                PagerBean(R.string.live_standings.getString()) { LiveStandingsFragment() }
            )

            vpPage.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
            launch {
                delay(500)
                vpPage.offscreenPageLimit = list.size
            }

            TabLayoutMediator(tabLayout, vpPage,false) { tab, position ->
                tab.text = list[position].title
                tab.setCustomView(R.layout.custom_tab)
                tab.customView?.findViewById<SkinnableTextView>(R.id.tabText)?.apply {
                    text = list[position].title
                    setTextColor(
                        SkinnableResourceManager.getColor(
                            context,
                            if (position == tabSelectPosition) R.color.tab_selected_text_color else R.color.video_tab_text_color
                        )
                    )
                    textSize = 15f.px2sp
                    typeface =
                        if (position == tabSelectPosition) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

                }
                tab.view.setOnClickListener { /* Handle click */ }
            }.attach()
            tabLayout.clearOnTabSelectedListeners()
            tabLayout.post{
                CustomTabIndicatorUtils.animateIndicatorToPosition(mBinding.customIndicator,1,false)
                mBinding.vpPage.setCurrentItem(1,false)
            }
            tabLayout.removeAllTips()
        }
    }
    private fun refreshBetSlip() {
        val adapter = mBinding.vpPage.adapter?.let { it as PagerAdapter }
        val tag = "f${adapter?.getItemId(0)}"
        val fragment = childFragmentManager.findFragmentByTag(tag)?.let { it as BetSlipFragment }
        fragment?.refreshBetSlip(mViewModel.matchId.value ?: -1, mViewModel.sportId.value ?: -1)
    }

    private fun isSoftKeyBoardVisible(): Boolean {
        val flag = getChatFragment()?.isSoftKeyboardVisible() ?: false
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

    override fun onStop() {
        getChatFragment()?.closeChatWebsocket()
        super.onStop()
    }

    override fun onDestroyView() {
        mViewModel.matchId.value?.let {
            deleteDataAndSubscriptions(it)
        }
        CustomTabIndicatorUtils.clear()
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

    private fun updateBetSheetSkin() {
        val type = mViewModel.getSkinType()
        if (fixedSkin != null && type != fixedSkin) {
            BetSheetFragment.find(requireActivity())?.forceUpdateSkin()
        }
    }

    private fun getChatFragment():LiveChatFragment?{
        val adapter = mBinding.vpPage.adapter?.let { it as PagerAdapter }
        val index = adapter!!.pages.indexOfFirst { it.title == R.string.live_chat.getString() }
        val tag = "f${adapter.getItemId(index)}"
        val fragment = childFragmentManager.findFragmentByTag(tag)?.let { it as LiveChatFragment }
       return fragment
    }

    private fun createChatFragment():LiveChatFragment{
        val fragment = LiveChatFragment()
        fragment.setMatchLiveData(mViewModel.matchId,mViewModel.mainMatch)
        return fragment
    }
}