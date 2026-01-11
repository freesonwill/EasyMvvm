package com.walisport.module.hall.ui.fragment

import android.content.ComponentName
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.animation.CustomCurveTransformer
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.data.constants.DrawerAction.ACTION_OPEN
import arch.cayenne.lib.common.data.constants.DrawerAction.KEY_ACTION
import arch.cayenne.lib.common.data.constants.DrawerAction.REQUEST_KEY_DRAWER
import arch.cayenne.lib.common.ui.adapter.BannerImageAdapter
import arch.cayenne.lib.common.ui.viewmodel.UnReadMessageViewModel
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.clickNoRepeatSingle
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.setRoundedBackground
import arch.cayenne.lib.common.utils.ext.setScaleAnim
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.skin.widget.SkinnableTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.business.common.data.Category
import com.walisport.module.business.common.ui.viewmodel.BalanceViewModel
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameCategoryVo
import com.walisport.module.hall.data.HallGamePage
import com.walisport.module.hall.data.HallGameTabDefault
import com.walisport.module.hall.databinding.FragmentHallBinding
import com.walisport.module.hall.databinding.ItemHallGameTabBinding
import com.walisport.module.hall.ui.view.ScrollableTabIndicatorHelper
import com.walisport.module.hall.ui.viewmodel.HallViewModel
import com.walisport.module.popup.slot.ui.fragment.PopupSlotFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

/**
 * 游戏大厅界面
 */

class HallFragment : BaseFragment<HallViewModel , FragmentHallBinding>() {

    override val vbClass: KClass<FragmentHallBinding> = FragmentHallBinding::class
    override val vmClass: KClass<HallViewModel> = HallViewModel::class

    private val popupSlotFragment: PopupSlotFragment by lazy {
        PopupSlotFragment()
    }

    private val balanceViewModel: BalanceViewModel by viewModel()
    private val mMinHeight = 38.dp2px

    private val mMaxHeight = 42.dp2px

    private val unreadMessageViewModel: UnReadMessageViewModel by viewModels()
    private var tabIndicatorHelper: ScrollableTabIndicatorHelper? = null

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            root.touchBackPressed()
            balanceView.init(childFragmentManager)
            balanceView.setBalanceViewModel(balanceViewModel, viewLifecycleOwner)
            initCurveBanner()
            btnLogin.clickNoRepeat {
//                navigate(
//                    arch.cayenne.lib.res.R.string.nav_module_login_fragment.deeplink(),
//                    enterAnim = AnimationController[AnimType.routeEnterBT],
//                    exitAnim = AnimationController[AnimType.routeExitTB],
//                    popEnterAnim = AnimationController[AnimType.routeExitTB],
//                    popExitAnim = AnimationController[AnimType.routeExitBT]
//                )

                //到LoginActivity
                val intent = Intent()
                intent.component = ComponentName(
                    requireActivity().packageName,
                    "arch.cayenne.module.account.ui.activity.LoginActivity"
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                requireActivity().navigate(intent)
            }
        }
        initPopupSlot()
    }


    override fun initData() {
        mViewModel.checkIsLogin()
        mViewModel.queryGameCommon()

        launch { mViewModel.getBannerActive() }
    }

    override fun onDestroyView() {
        tabIndicatorHelper?.release()
        tabIndicatorHelper = null
        super.onDestroyView()
    }

    private fun createGameTabView(position: Int, item: HallGamePage): View {
        val tabBinding =
            ItemHallGameTabBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        tabBinding.tvTitle.text = item.title
        if (item is HallGameTabDefault) {
            Glide.with(mBinding.root)
                .load(item.icon)
                .transition(DrawableTransitionOptions.withCrossFade()) // 淡入动画
                .into(tabBinding.ivHallTabIcon)
        } else {
            //TODO 從api來
        }
        return tabBinding.root
    }

    override fun initListener() {
        with(mBinding) {
            ivHomeSidebar.addScaleOnTouchAnimation()
            ivHomeSidebar.clickNoRepeat {
                requireActivity().supportFragmentManager.setFragmentResult(
                    REQUEST_KEY_DRAWER,
                    bundleOf(KEY_ACTION to ACTION_OPEN)
                )
            }

            balanceView.onAddClickListener = {
                navigate(Uri.parse("walisport://module_topup/topUpFragment"))
            }

//            ivRightLogo.apply {
//                clickNoRepeatSingle { navigate(arch.cayenne.lib.res.R.string.nav_module_promotion_fragment.deeplink()) }
//                addScaleOnTouchAnimation()
//            }

            ivRightLogo.setOnBannerListener { Int, position ->
                val url = mViewModel.curveBannerLiveData.value?.get(position)?.targetUrl ?: ""
                navigate(arch.cayenne.lib.res.R.string.nav_module_web_fragment.deeplink("url" to url))
            }

            llSearchBar.apply {
                clickNoRepeatSingle {
                    navigate(arch.cayenne.lib.res.R.string.nav_module_search_fragment.deeplink())
                }
            }
        }

    }


    fun initTab(tabCategoryList: List<GameCategoryVo>) {
        mBinding.aciTabBg.visibility = View.VISIBLE
        //循环把tabCategoryList装到HallGameTabDefault里面
        val tabList = tabCategoryList.map { vo ->
            var colorRes = vo.color
            if (colorRes.isEmpty()){
                colorRes = String.format("#%06X", Category.entries.find { it.type == vo.category }?.color)
            }
            if(vo.category==Category.ALL.type){
                HallGameTabDefault(
                    colorRes = colorRes,
                    icon = vo.icon,
                    thumbhash = vo.thumbhash,
                    _title = vo.name,
                    _page = { GameAllFragment.newInstance() }
                )
            } else if (vo.category==Category.RECENT.type){
                HallGameTabDefault(
                    colorRes = vo.color,
                    icon = vo.icon,
                    thumbhash = vo.thumbhash,
                    _title = vo.name,
                    _page = { GameRecentFragment.newInstance(vo.category) }
                )
            }else{
                HallGameTabDefault(
                    colorRes = vo.color,
                    icon = vo.icon,
                    thumbhash = vo.thumbhash,
                    _title = vo.name,
                    _page = { GameContentFragment.newInstance(vo.category) }
                )
            }

        }


        with(mBinding) {
            vpGame.adapter = PagerAdapter(childFragmentManager, lifecycle, tabList)
            launch {
                delay(500)
                vpGame.offscreenPageLimit = tabCategoryList.size
            }
            TabLayoutMediator(tlGame, vpGame, false) { tab, position ->
                tab.customView = createGameTabView(position, tabList[position])
                val paddingStart = 5.dp2px
                tab.view.setPadding(0, 0, paddingStart, 0)
                tab.customView?.findViewById<AppCompatImageView>(R.id.iv_Hall_tab_icon)?.apply {
                    if (position == 1) {
                        setScaleAnim(mMinHeight.toInt(), mMaxHeight.toInt())
                    }
                }
                tab.customView?.findViewById<SkinnableTextView>(R.id.tv_title)?.apply {
                    if (position == 1) {
                        (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                            marginStart = marginStart
                            topMargin = 4.dp2px
                            marginEnd = marginEnd
                            bottomMargin = bottomMargin
                            layoutParams = this
                        }
                    }
                }
                tab.customView?.findViewById<SkinnableTextView>(R.id.tv_title)?.apply {
                    if (position == 1) {
                        typeface = Typeface.DEFAULT_BOLD
                        setTextColor(
                            SkinnableResourceManager.getColor(
                                context,
                                arch.cayenne.lib.common.R.color.white
                            )
                        )
                        setRoundedBackground(
                            backgroundColor = tabList[position].colorRes,
                            show = true
                        )
                    } else {
                        setTextColor(
                            SkinnableResourceManager.getColor(
                                context,
                                arch.cayenne.lib.common.R.color.color_C0C0C0
                            )
                        )
                        typeface = Typeface.DEFAULT
                        setRoundedBackground(
                            backgroundColor =String.format("#%06X", 0xFFFFFF and arch.cayenne.lib.common.R.color.title_bg) ,
                            show = false
                        )
                    }
                }

            }.attach()
            tlGame.clearOnTabSelectedListeners()
            tlGame.removeAllTips()
            //  tlGame.getTabAt(1)?.select()
            tabIndicatorHelper = ScrollableTabIndicatorHelper(mBinding.tlGame, mBinding.aciTabBg)
            tabIndicatorHelper?.setup()
            vpGame.setCurrentItem(1, false)
        }

        mBinding.tlGame.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {

            override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
                LogUtils.e("checkAndAnimateIfSettled-------->onTabSelected------->")
                tab.let {
                    if (isTabClick) {
                        val vp = mBinding.vpGame
                        vp.startFadeAnim {
                            vp.setCurrentItem(tab.position, false)
                            it.invoke()
                        }
                    }
                    launch {
                        if (!mBinding.aciTabBg.isGone) {
                            tabIndicatorHelper?.smartAnimateToCurrent()
                        }

                    }

                }
                tab.customView?.findViewById<SkinnableTextView>(R.id.tv_title)?.apply {
                    (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                        marginStart = marginStart
                        topMargin = 4.dp2px
                        marginEnd = marginEnd
                        bottomMargin = bottomMargin
                        layoutParams = this
                    }
                }
                tab.customView?.findViewById<AppCompatImageView>(R.id.iv_Hall_tab_icon)?.apply {
                    setScaleAnim(mMinHeight.toInt(), mMaxHeight.toInt())
                }
                tab.view.findViewById<SkinnableTextView>(R.id.tv_title)?.let { textView ->
                    textView.setTextColor(
                        SkinnableResourceManager.getColor(
                            textView.context,
                            arch.cayenne.lib.common.R.color.white
                        )
                    )
                    textView.setRoundedBackground(
                        backgroundColor = tabList[tab.position].colorRes,
                        show = true
                    )
                    textView.typeface = Typeface.DEFAULT_BOLD
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                tab.view.findViewById<SkinnableTextView>(R.id.tv_title)?.let { textView ->
                    textView.setTextColor(
                        SkinnableResourceManager.getColor(
                            textView.context,
                            arch.cayenne.lib.common.R.color.color_C0C0C0
                        )
                    )
                    tab.customView?.findViewById<SkinnableTextView>(R.id.tv_title)?.apply {
                        (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                            marginStart = marginStart
                            topMargin = 5.dp2px
                            marginEnd = marginEnd
                            bottomMargin = bottomMargin
                            layoutParams = this
                        }
                    }
                    tab.customView?.findViewById<AppCompatImageView>(R.id.iv_Hall_tab_icon)?.apply {
                        setScaleAnim(mMaxHeight.toInt(), mMinHeight.toInt())
                    }
                    textView.setRoundedBackground(
                        backgroundColor = tabList[tab.position].colorRes,
                        show = false
                    )
                    textView.typeface = Typeface.DEFAULT
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                // Handle reselect if needed
            }
        })
    }

    override suspend fun createObserver() {
        mViewModel.curveBannerLiveData.observe(viewLifecycleOwner) { bannerList ->
            val adapter = mBinding.ivRightLogo.adapter as BannerImageAdapter
            val images = bannerList.map { it.imagePath }
            adapter.setDatas(images)
            mBinding.ivRightLogo.isVisible = adapter.itemCount != 0
        }

        launch {
            mViewModel.observeUserLogin().collect {
                mBinding.btnLogin.isVisible = !it
                mBinding.balanceView.isVisible = it
            }
        }

        mViewModel.gameCategory.observe(viewLifecycleOwner) { categoryList ->
            LogUtils.e("HallFragment--->gameCategory--->$categoryList")
            //分类列表数据更新后处理
            //根据category大到小排序
            val sortedList = categoryList.sortedWith(
                compareBy<GameCategoryVo> {
                    when (it.category) {
                        Category.RECENT.type -> 0
                        Category.ALL.type -> 1
                        else -> 2
                    }
                }.thenByDescending { it.category }
            )
            //剔除category为102的项
            val filteredList = sortedList.filter { it.category != Category.HOT.type }
            //更新tab列表
            initTab(filteredList)
        }

        with(unreadMessageViewModel) {
            //未读消息监听
            unreadMsg.observe(viewLifecycleOwner) { flag ->
                mBinding.ivUnreadDot.visibility = if (flag) View.VISIBLE else View.GONE
            }
        }
        unreadMessageViewModel.createObserver()

        mViewModel.scroll.observe(viewLifecycleOwner) {
            if (it) { //收起
                mBinding.homeBarIcon.marginEndAnim()
            } else { //展开
                mBinding.homeBarIcon.marginStartAnim()
            }
        }

        mViewModel.scrollStateChanged.observe(viewLifecycleOwner) {
            if (it == RecyclerView.SCROLL_STATE_IDLE) {
                launch {
                    delay(200)
                    popupSlotFragment.fadeAndIn()
                }
            } else {
                popupSlotFragment.fadeAndOut()
            }
        }
    }

    override fun onStart() {
        mBinding.homeTopBar.post {
            //动态设置沉浸式状态栏背景高度 状态栏高度+bar控件高度
            val barHeight = ViewUtils.getStatusBarHeight(requireContext())
            val toBarHeight = mBinding.homeTopBar.height

            val paramsLin = mBinding.homeBarIcon.layoutParams as LayoutParams
            paramsLin.height = barHeight + toBarHeight + 20.dp2px
            mBinding.homeBarIcon.layoutParams = paramsLin
        }
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType =
            StatusBarMode.DRAW_BEHIND(autoIsNavigation = true)
        setStatusBar(StatusBarConfig, mBinding.clMain)
        super.onStart()
    }


    private fun initCurveBanner(images:List<String> = emptyList()) {
        // 自定义适配器
        val adapter = BannerImageAdapter(images)
        mBinding.ivRightLogo.setAdapter(adapter)
        mBinding.ivRightLogo.setLoopTime(5000)
        // 设置滑动时长丝滑,不影响曲线,
        mBinding.ivRightLogo.setScrollTime(500)
        mBinding.ivRightLogo.setPageTransformer(CustomCurveTransformer())
        // 启动轮播
        mBinding.ivRightLogo.start()
    }

    private fun initPopupSlot() {
        childFragmentManager.beginTransaction()
            .add(R.id.fragment_hall_container_view , popupSlotFragment , PopupSlotFragment.TAG)
            .commit()
    }

}