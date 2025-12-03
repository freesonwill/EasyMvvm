package arch.cayenne.module.home.ui.fragment

import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewOutlineProvider
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.fragment.app.viewModels
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.animation.CustomCurveTransformer
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.data.constants.DrawerAction.ACTION_INIT
import arch.cayenne.lib.common.data.constants.DrawerAction.ACTION_OPEN
import arch.cayenne.lib.common.data.constants.DrawerAction.KEY_ACTION
import arch.cayenne.lib.common.data.constants.DrawerAction.REQUEST_KEY_DRAWER
import arch.cayenne.lib.common.ui.adapter.BannerImageAdapter
import arch.cayenne.lib.common.ui.view.CustomTabIndicator
import arch.cayenne.lib.common.ui.viewmodel.UnReadMessageViewModel
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.DensityInfo
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.clickNoRepeatSingle
import arch.cayenne.lib.common.utils.ext.removeAllTips
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
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.tabs.TabLayout
import kotlin.reflect.KClass

/**
 * 体育页
 */
class NewHomeFragment : BaseFragment<HomeViewModel, FragmentNewHomeBinding>() {
    override val vbClass: KClass<FragmentNewHomeBinding> = FragmentNewHomeBinding::class
    override val vmClass: KClass<HomeViewModel> = HomeViewModel::class

    private val unreadMessageViewModel: UnReadMessageViewModel by viewModels()
    private var homeMediator: HomeTabMediator? = null
    private var promoTabs: List<PromoTab> = emptyList()
    private var playTypes: List<PlayType> = emptyList()
    private var indicatorDrawable: android.graphics.drawable.Drawable? = null
    private var customIndicator: CustomTabIndicator? = null

    //    private val tournamentListFragment  = TournamentListFragment.newInstance()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.balanceView.init(childFragmentManager)
        initPlayTypeLayout()
        setReceiveHorizontalScrollResult()
        initCurveBanner()

    }

    override fun onStart() {
        mBinding.homeTopBar.post {
            //动态设置沉浸式状态栏背景高度 状态栏高度+bar控件高度
            val barHeight = ViewUtils.getStatusBarHeight(requireContext())
            val toBarHeight = mBinding.homeTopBar.height

            val paramsLin = mBinding.homeBarIcon.layoutParams as LayoutParams
            paramsLin.height = barHeight + toBarHeight+20.dp2px
            mBinding.homeBarIcon.layoutParams = paramsLin
        }
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(autoIsNavigation = true)
        setStatusBar(StatusBarConfig, mBinding.llMain)
        super.onStart()
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

    private fun initCurveBanner(){
        val images =  listOf(
            arch.cayenne.lib.common.R.drawable.home_bar_left_icon,
            arch.cayenne.lib.common.R.drawable.home_bar_left_icon,
            arch.cayenne.lib.common.R.drawable.home_bar_left_icon,
            arch.cayenne.lib.common.R.drawable.home_bar_left_icon
                )
        val adapter = BannerImageAdapter(images)
       mBinding.banner.setAdapter(adapter)
        mBinding.banner.setLoopTime(3000)
        // 设置滑动时长丝滑,不影响曲线,
        mBinding. banner.setScrollTime(500)  // 1 秒
        mBinding. banner.setPageTransformer(CustomCurveTransformer())
        // 启动轮播
        mBinding. banner.start()
    }
    private fun setupSidebar() {
        mBinding.ivHomeSidebar.clickNoRepeat {
            requireActivity().supportFragmentManager.setFragmentResult(
                REQUEST_KEY_DRAWER,
                bundleOf(KEY_ACTION to ACTION_OPEN)
            )
        }
    }

    private fun setupViewPager() {
        mBinding.vpSub.apply {
            promoTabs = buildPromoTabs()
            playTypes = listOf(
                PlayType.TODAY,
                PlayType.ROLLING,
                PlayType.EARLY,
                PlayType.CHAMPION,
                PlayType.FAVORITE
            )
            val promoCount = promoTabs.size
            adapter = SubHomePagerAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = viewLifecycleOwner.lifecycle,
                promoCount = promoCount,
                playTypes = playTypes
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
                // 綁定自定義指示器
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
                    tab.setText(playTypes[position - promoTabs.size].titleRes.getString())
                }
            },
            onPreselectChanged = { pos ->
                updateTabTextStyle(pos)
            },

            )
        homeMediator?.attach { pos ->
            // 由 Mediator 回調的最終選中頁：切換指示器與遮罩
            val isPromo = pos < promoTabs.size
            indicatorDrawable?.alpha = if (isPromo) 0 else 255

            if (isPromo) {
                // 停在 promo：立即顯示遮罩
                mBinding.homeIndicatorMask.visibility = View.VISIBLE
            } else {
                // 從 promo 切到一般 tab：確保遮罩先 VISIBLE，延遲後才 GONE
                if (mBinding.homeIndicatorMask.visibility == View.VISIBLE) {
                    mBinding.homeIndicatorMask.postDelayed({
                        mBinding.homeIndicatorMask.visibility = View.GONE
                    }, AnimationController[AnimType.scrollbar]?.duration ?: 200L)
                }
                // 如果遮罩已經是 GONE（一般 tab 之間切換），則不做任何事
            }
        }
        // 預設選中第一個tab
        mBinding.tlHome.post {
            mBinding.tlHome.getTabAt(0)?.select()
            // 如果第一個是 promo，啟動即顯示遮罩
            if (promoTabs.isNotEmpty()) {
                mBinding.homeIndicatorMask.visibility = View.VISIBLE
            }
        }
        // Mediator 建立完後，新增自定義監聽
        mBinding.tlHome.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
            override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
                val position = tab.position
                if (position < promoTabs.size) {
                    // promo：顯示遮罩
                    mBinding.homeIndicatorMask.visibility = View.VISIBLE
                } else {
                    val playIndex = position - promoTabs.size
                    val playType = PlayType.entries[playIndex]
                    mViewModel.setCurrentPlayType(playType.id)

                    if (playType != PlayType.FAVORITE) {
                        (childFragmentManager.findFragmentByTag("f$position") as? ISubFragmentLifecycle)?.onFragmentSelected()
                    }

                    // 樣式：設為粗體，並更新顏色
                    (tab.view.getChildAt(1) as? TextView)?.typeface = Typeface.DEFAULT_BOLD
                    updateTabTextStyle(position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                val position = tab.position
                if (position >= promoTabs.size) {
                    val playIndex = position - promoTabs.size
                    val playType = PlayType.entries[playIndex]
                    if (playType != PlayType.FAVORITE) {
                        (childFragmentManager.findFragmentByTag("f$position") as? ISubFragmentLifecycle)?.onFragmentUnSelected()
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
            tabStrip.clipChildren = false
            tabStrip.clipToPadding = false
            
            val tabWidthPx = 56.dp2px
            for (i in 0 until tabStrip.childCount) {
                val tabView = tabStrip.getChildAt(i)
                val lp = tabView.layoutParams as ViewGroup.MarginLayoutParams
                val isPromo = (getTabAt(i)?.tag == "PROMO")
                if (isPromo) {
                    lp.width = 104.dp2px
                    lp.height = 26.dp2px
                } else {
                    lp.width = tabWidthPx
                    lp.bottomMargin = 3.dp2px
                }
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

            indicatorDrawable =
                ResourcesCompat.getDrawable(resources, R.drawable.shape_home_tab_indicator, null)
            setSelectedTabIndicator(indicatorDrawable)
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

    override fun initData() {
        super.initData()
//        mViewModel.setCurrentPlayType(PlayType.TODAY.id)
    }

    override fun initListener() {
        with(mBinding) {
            balanceView.onAddClickListener = {
                navigate(Uri.parse("walisport://module_topup/topUpFragment"))
            }
            ivSearchEntry.apply {
                clickNoRepeatSingle { navigate(arch.cayenne.lib.res.R.string.nav_module_search_fragment.deeplink()) }
                addScaleOnTouchAnimation()
            }
        }
    }


    override suspend fun createObserver() {
        mViewModel.notifyToChampion.observeEvent(viewLifecycleOwner, this) {
//            toggleTournamentMoreSection(true, TournamentListType.CHAMPION)
        }

        mViewModel.currentBalanceChange.observe(viewLifecycleOwner) {
            mBinding.balanceView.setMoney( getString(
                R.string.balance_format,
                CurrencySymbols.getSymbol(it?.currency ?: ""),
                (it?.balance ?: 0L).getFormalMoney()
            ))
        }


        mViewModel.playTypeIndexChange.observeEvent(viewLifecycleOwner, this) {
            // PlayType 索引需補上 promo 偏移，避免選到 promo 位置
            val indexWithPromo = promoTabs.size + it
            mBinding.tlHome.getTabAt(indexWithPromo)?.select()
            mBinding.tlHome.removeAllTips()
        }


        mViewModel.apiStateListener.observe(viewLifecycleOwner) {
            when (it) {
                is HomeState.Tournament.LoadSuccess, HomeState.Tournament.LoadFailure, HomeState.Sport.LoadFailure -> {

                }

                is HomeState.FirstMatchListComplete -> {
                    requireActivity().supportFragmentManager.setFragmentResult(
                        REQUEST_KEY_DRAWER,
                        bundleOf(KEY_ACTION to ACTION_INIT )
                    )
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


        with(unreadMessageViewModel) {
            //未读消息监听
            unreadMsg.observe(viewLifecycleOwner) { flag ->
                mBinding.ivUnreadDot.visibility = if (flag) View.VISIBLE else View.GONE
            }
        }

        unreadMessageViewModel.createObserver()

    }

    //設置是否允許水平滑動ViewPager，預設是可以滑動
    private fun setIsUserInputEnabled(isUserInputEnabled: Boolean) {
        mBinding.vpSub.isUserInputEnabled = isUserInputEnabled
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