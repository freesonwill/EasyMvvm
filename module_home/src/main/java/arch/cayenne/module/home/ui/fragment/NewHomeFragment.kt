package arch.cayenne.module.home.ui.fragment

import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.ui.view.CustomTabIndicator
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.DensityInfo
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavResultExt.observeResult
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.clickNoRepeatSingle
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.setDrawerInterpolator
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.lib.common.utils.ext.setupViewPagerScroll
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.databinding.FragmentNewHomeBinding
import arch.cayenne.module.home.ui.adapter.SubHomePagerAdapter
import arch.cayenne.module.home.ui.view.HomeTabMediator
import arch.cayenne.module.home.ui.view.PromoTab
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayout
import kotlin.reflect.KClass

class NewHomeFragment : BaseFragment<HomeViewModel, FragmentNewHomeBinding>() {
    override val vbClass: KClass<FragmentNewHomeBinding> = FragmentNewHomeBinding::class
    override val vmClass: KClass<HomeViewModel> = HomeViewModel::class
    private var drawerContentFragment: DrawerContentFragment? = null
    private var homeMediator: HomeTabMediator? = null
    private var promoTabs: List<PromoTab> = emptyList()

    // 移除原生指示器 drawable 控制，統一使用自訂指示器
    private var customIndicator: CustomTabIndicator? = null

    //    private val tournamentListFragment  = TournamentListFragment.newInstance()

    override fun initView(savedInstanceState: Bundle?) {
        initPlayTypeLayout()
        setReceiveHorizontalScrollResult()
        setDrawerLayoutListener()

    }

    override fun onStart() {
        mBinding.homeTopBar.post{
            //动态设置沉浸式状态栏背景高度 状态栏高度+bar控件高度
            var barHeight = ViewUtils.getStatusBarHeight(requireContext())
            var toBarHeight = mBinding.homeTopBar.height

            val paramsLin = mBinding.homeBarIcon.layoutParams as LayoutParams
            paramsLin.height = barHeight+toBarHeight
            mBinding.homeBarIcon.layoutParams = paramsLin
        }
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(autoIsNavigation = true)
        setStatusBar(StatusBarConfig,mBinding.llMain)
        super.onStart()
    }
    private fun setDrawerLayoutListener() {
        with (mBinding) {
            drawerLayout.setLayerType(View.LAYER_TYPE_NONE,null)
            drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    // 動畫滑動中...
                    "onDrawerSlide slideOffset: $slideOffset".logd()
                    if (slideOffset in 0.1f .. 0.99f && drawerLayout.layerType != View.LAYER_TYPE_NONE) {
                        // 抽屜打開一半之前，使用軟體層
                        drawerLayout.setLayerType(View.LAYER_TYPE_NONE, null)
                    }
                }

                override fun onDrawerOpened(drawerView: View) {
                    // 抽屜打開後
                    drawerLayout.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                }

                override fun onDrawerClosed(drawerView: View) {
                    // 抽屜關閉後
                    drawerLayout.setLayerType(View.LAYER_TYPE_NONE, null)
                }

                override fun onDrawerStateChanged(newState: Int) {
                }
            })
        }
    }
    //init 一級導航欄位
    private fun initPlayTypeLayout() {
        with(mBinding) {
            setupSidebar()
            setupViewPager()
            setupTabLayout()
            setupTabMediator()
        }
    }

    private fun setupSidebar() {
        mBinding.ivHomeSidebar.clickNoRepeat {
            initDrawerContent()
            mBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setupViewPager() {
        mBinding.vpSub.apply {
            promoTabs = buildPromoTabs()
            val promoCount = promoTabs.size
            adapter = SubHomePagerAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = viewLifecycleOwner.lifecycle,
                promoCount = promoCount,
                playTypes = listOf(
                    PlayType.TODAY,
                    PlayType.EARLY,
                    PlayType.CHAMPION,
                    PlayType.FAVORITE
                )
            )
            offscreenPageLimit = 3
            setupHorizontalScrollDegree()
            // 預設選中第一個可見tab（若有promo則為index 0 的promo頁）
            setCurrentItem(0, false)
        }
    }

    private fun setupTabLayout() {
        with(mBinding.tlHome) {
            // 僅提供資源給 TabLayout，實際 tabs 由 Mediator 建立
            val tabResList = PlayType.entries.map { it.titleRes }
            setTabResArray(tabResList.toIntArray())
            post {
                setupTabsStyle()
                updateTabTextStyle(selectedTabPosition.coerceAtLeast(0))
                // 綁定自定義指示器（速度交由 HomeTabIndicatorUtils + AnimationController 控制）
                customIndicator = mBinding.homeIndicator
                mBinding.vpSub.setupViewPagerScroll(
                    this,
                    customIndicator!!,
                    tabIndicatorWidth = 0.45f
                )
            }
        }
    }

    private fun setupTabMediator() {
        homeMediator = HomeTabMediator(
            tabLayout = mBinding.tlHome,
            viewPager = mBinding.vpSub,
            tabConfiguration = { tab, position ->
                if (position < promoTabs.size) {
                    tab.customView = promoTabs[position].createView(requireContext())
                    tab.tag = "PROMO"
                } else {
                    tab.setText(PlayType.entries[position - promoTabs.size].titleRes)
                }
            },
            onPreselectChanged = { pos ->
                updateTabTextStyle(pos)
            },
            
        )
        homeMediator?.attach { pos ->
            // 由 Mediator 回調的最終選中頁，用於控制指示器顯示/隱藏
            val isPromo = pos < promoTabs.size
            mBinding.homeIndicator.alpha = if (isPromo) 0f else 1f
        }
        // 預設選中第一個tab
        mBinding.tlHome.post {
            mBinding.tlHome.getTabAt(0)?.select()
            // 如果第一個是 promo，啟動即隱藏指示器
            if (promoTabs.isNotEmpty()) {
                mBinding.homeIndicator.alpha = 0f
            }
        }
        // Mediator 建立完後，新增自定義監聽
        mBinding.tlHome.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
            override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
                val position = tab.position
                if (position < promoTabs.size) {
                    // promo：隱藏指示器、無文字選中樣式；頁面切換交由 Mediator 處理
                    mBinding.homeIndicator.alpha = 0f
                } else {
                    val playIndex = position - promoTabs.size
                    val playType = PlayType.entries[playIndex]
                    mViewModel.setCurrentPlayType(playType.id)

                    if (playType != PlayType.FAVORITE) {
                        (childFragmentManager.findFragmentByTag("f$position") as? SubHomeFragment)?.onFragmentSelected()
                    }

                    // 樣式：設為粗體，並更新顏色
                    (tab.view.getChildAt(1) as? TextView)?.typeface = Typeface.DEFAULT_BOLD
                    updateTabTextStyle(position)

                    // 指示器顯示
                    mBinding.homeIndicator.alpha = 1f

                    // 頁面切換動畫全部交由 Mediator
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                val position = tab.position
                if (position >= promoTabs.size) {
                    val playIndex = position - promoTabs.size
                    val playType = PlayType.entries[playIndex]
                    if (playType != PlayType.FAVORITE) {
                        (childFragmentManager.findFragmentByTag("f$position") as? SubHomeFragment)?.onFragmentUnSelected()
                    }
                    // 設為預設字重並更新顏色
                    (tab.view.getChildAt(1) as? TextView)?.typeface = Typeface.DEFAULT
                    updateTabStyle(position, -1)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) = Unit
        })
    }

    private fun buildPromoTabs(): List<PromoTab> {
        // 先以本地資源占位兩個圖片 tab
        val resId = R.drawable.ic_supertab_sample
        return listOf(
            PromoTab(imageResId = resId) {},
            PromoTab(imageResId = resId) {}
        )
    }

    private fun setupTabsStyle() {
        with(mBinding.tlHome) {
            val tabStrip = (getChildAt(0) as? ViewGroup) ?: return
            val tabWidthPx = 56.dp2px
            for (i in 0 until tabStrip.childCount) {
                val tabView = tabStrip.getChildAt(i)
                val lp = tabView.layoutParams as ViewGroup.MarginLayoutParams
                val isPromo = (getTabAt(i)?.tag == "PROMO")
                lp.width = if (isPromo) 104.dp2px else tabWidthPx
                tabView.layoutParams = lp

                val tv = (getTabAt(i)?.view?.getChildAt(1) as? TextView)
                tv?.apply {
                    textSize = 17f
                    maxLines = 1
                    isSingleLine = true
                    ellipsize = android.text.TextUtils.TruncateAt.END
                }
            }
            tabStrip.requestLayout()

            // 原生指示器關閉，改用自訂指示器
            setSelectedTabIndicator(null)
            setSelectedTabIndicatorColor(android.graphics.Color.TRANSPARENT)
        }
    }

    // 更新所有 Tab 文字樣式（用於預選中和初始化）
    private fun updateTabTextStyle(selectedPosition: Int) {
        with(mBinding) {
            for (i in 0 until tlHome.tabCount) {
                updateTabStyle(i, selectedPosition)
            }
        }
    }

    // 統一的 Tab 樣式更新方法
    private fun updateTabStyle(tabPosition: Int, selectedPosition: Int) {
        val tv = mBinding.tlHome.getTabAt(tabPosition)?.view?.getChildAt(1) as? TextView ?: return

        val isSelected = selectedPosition >= 0 && tabPosition == selectedPosition
        tv.apply {
            setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
            setTextColor(
                SkinnableResourceManager.getColor(
                    requireContext(),
                    if (isSelected) R.color.sport_item_text_select else R.color.home_secondary_text
                )
            )
        }
    }

    private fun setReceiveHorizontalScrollResult() {
        childFragmentManager.setFragmentResultListener(
            getString(R.string.new_home_vp_sub_key),
            this
        ) { _, bundle ->
            val isEnabled = bundle.getBoolean(getString(R.string.key_enable_horizontal_scroll))
            setIsUserInputEnabled(isEnabled)
        }
    }

    //init DrawerLayout Content
    private fun initDrawerContent() {
         //避免重複創建
        if (drawerContentFragment != null) {
            return
        }
        drawerContentFragment = DrawerContentFragment()
        drawerContentFragment?.also {
            it.setOnFunctionClickListener {
//                    mBinding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        //蒙層顏色依照版型作變化
        mBinding.drawerLayout.setScrimColor(
            SkinnableResourceManager.getColor(
                requireContext(),
                R.color.drawer_scrim_color
            )
        )
        // 使用 view.post 將 commitNow 操作延遲到下一個訊息迴圈
        mBinding.root.post {
            childFragmentManager.beginTransaction()
                .replace(
                    mBinding.fragmentDrawerContent.id,
                    drawerContentFragment!!,
                    DrawerContentFragment.TAG
                )
                .commitNow()
        }
        //如果由模拟投注页面跳转到首页需要关闭左侧菜单栏
        observeResult<String>("Drawer") {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START,false)
        }
    }



    override fun initData() {
        super.initData()
//        mViewModel.setCurrentPlayType(PlayType.TODAY.id)
    }

    override fun initListener() {
        with(mBinding) {
            includedLayout.llWalletEntry.apply {
                addScaleOnTouchAnimation(includedLayout.addMoney)
            }.setOnClickListener {
                //navigate(Uri.parse("walisport://module_home/homeFragment"))
                navigate(Uri.parse("walisport://module_topup/topUpFragment"))
            }

            ivSearchEntry.apply {
                clickNoRepeatSingle { navigate(arch.cayenne.lib.res.R.string.nav_module_search_fragment.deeplink()) }
                addScaleOnTouchAnimation()
            }

            drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
                override fun onDrawerOpened(drawerView: View) {
                    initDrawerContent()
                }
                override fun onDrawerClosed(drawerView: View) {}
                override fun onDrawerStateChanged(newState: Int) {}

            })
        }
    }


    override suspend fun createObserver() {
        launch {
            AnimationController.getFlow(AnimType.drawerEnter).collect {
                if(it == null) return@collect
                mBinding.drawerLayout.setDrawerInterpolator(it.duration, it.interpolator.toInterpolator())
            }
        }
        mViewModel.notifyToChampion.observeEvent(viewLifecycleOwner, this) {
//            toggleTournamentMoreSection(true, TournamentListType.CHAMPION)
        }

        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            mBinding.includedLayout.tvMoney.text =
                getString(
                    R.string.balance_format,
                    CurrencySymbols.getSymbol(it?.currency?:""),
                    (it?.balance?:0L).getFormalMoney()
                )
        }


        mViewModel.playTypeIndexChange.observeEvent(viewLifecycleOwner, this) {
            // PlayType 索引需補上 promo 偏移，避免選到 promo 位置
            val indexWithPromo = promoTabs.size + it
            mBinding.tlHome.getTabAt(indexWithPromo)?.select()
            mBinding.tlHome.removeAllTips()
        }


        mViewModel.apiStateListener.observe(viewLifecycleOwner) {
            when(it) {
                is HomeState.Tournament.LoadSuccess, HomeState.Tournament.LoadFailure, HomeState.Sport.LoadFailure -> {

                }
                is HomeState.FirstMatchListComplete -> {
                    initDrawerContent()
                }
                else -> Unit
            }
        }
        mViewModel.notifySubHomeRefresh.observeEvent(viewLifecycleOwner, this) {
//            mBinding.vpSub.adapter?.getItemId(0)?.also { itemId ->
//                (childFragmentManager.findFragmentByTag("f$itemId") as? SubHomeFragment)?.also { fragment ->
//                    fragment.reloadCurrentMatchListPagerFragment()
//                }
//            }
            mViewModel.resetPageSelectedTimestamp(PlayType.entries[0].id)
            mBinding.vpSub.setCurrentItem(0, false)
        }
    }
    //設置是否允許水平滑動ViewPager，預設是可以滑動
    private fun setIsUserInputEnabled(isUserInputEnabled: Boolean) {
        mBinding.vpSub.isUserInputEnabled = isUserInputEnabled
    }

    override fun onBackPressed(): Boolean {
        //如果抽屉打开，截获此次返回事件，关闭抽屉
        if(mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
        return super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        val metrics = resources.displayMetrics
        //density和scaledDensity被篡改，尝试恢复
        if (metrics.density != DensityInfo.density && DensityInfo.density > 0) {
            metrics.density = DensityInfo.density
        }
        if (metrics.scaledDensity != DensityInfo.scaledDensity && DensityInfo.scaledDensity > 0) {
            metrics.scaledDensity = DensityInfo.scaledDensity
        }
    }

    override fun onDestroyView() {
        homeMediator?.detach()
        homeMediator = null
        super.onDestroyView()
    }

}