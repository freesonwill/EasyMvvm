package com.walisport.module.hall.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.marginStart
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
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
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.skin.widget.SkinnableTextView
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.hall.R
import com.walisport.module.hall.data.HallGamePage
import com.walisport.module.hall.data.HallGameTabDefault
import com.walisport.module.hall.databinding.FragmentHallBinding
import com.walisport.module.hall.databinding.ItemHallGameTabBinding
import com.walisport.module.hall.ui.viewmodel.HallViewModel
import kotlin.reflect.KClass
import arch.cayenne.lib.common.utils.ext.setRoundedBackground
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.google.android.material.tabs.TabLayout
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.removeAllTips
import com.walisport.module.hall.ui.view.ScrollableTabIndicatorHelper
import kotlinx.coroutines.delay
import arch.cayenne.lib.common.utils.ext.setScaleAnim
/**
 * 游戏大厅界面
 */

class HallFragment : BaseFragment<HallViewModel, FragmentHallBinding>() {

    override val vbClass: KClass<FragmentHallBinding> = FragmentHallBinding::class
    override val vmClass: KClass<HallViewModel> = HallViewModel::class


    private val mMinHeight = 34.dp2px

    private val mMaxHeight = 38.dp2px

    private val unreadMessageViewModel: UnReadMessageViewModel by viewModels()
    private var tabIndicatorHelper: ScrollableTabIndicatorHelper? = null
    private val mockTabList = arrayListOf(
        HallGameTabDefault(
            colorRes =  arch.cayenne.lib.common.R.color.color_00E0E5,
            res = R.drawable.ic_tab_hall_recent,
            _title = R.string.tab_recent.getString(),
            _page = { GameRecentFragment.newInstance() }
        ),
        HallGameTabDefault(
            colorRes = arch.cayenne.lib.common.R.color.color_5B32FF,
            res = R.drawable.ic_tab_hall_all,
            _title = R.string.tab_all.getString(),
            _page = { GameAllFragment.newInstance() }
        ),
        HallGameTabDefault(
            colorRes =  arch.cayenne.lib.common.R.color.red_team,
            res = R.drawable.ic_tab_hall_table,
            _title = R.string.tab_table.getString(),
            _page = { GameContentFragment.newInstance() }
        ),
        HallGameTabDefault(
            colorRes =   arch.cayenne.lib.common.R.color.red_team,
            res = R.drawable.ic_tab_hall_slot,
            _title = R.string.tab_slot.getString(),
            _page = { GameContentFragment.newInstance() }
        ),

        HallGameTabDefault(
            colorRes =  arch.cayenne.lib.common.R.color.red_team,
            res = R.drawable.ic_tab_hall_fishing,
            _title = R.string.tab_fishing.getString(),
            _page = { GameContentFragment.newInstance() }
        ),
        HallGameTabDefault(
            colorRes =  arch.cayenne.lib.common.R.color.red_team,
            res = R.drawable.ic_tab_hall_real,
            _title = R.string.tab_real.getString(),
            _page = { GameContentFragment.newInstance() }
        ),
        HallGameTabDefault(
            colorRes =  arch.cayenne.lib.common.R.color.red_team,
            res = R.drawable.ic_tab_hall_original,
            _title = R.string.tab_original.getString(),
            _page = { GameContentFragment.newInstance() }
        ),
        HallGameTabDefault(
            colorRes =  arch.cayenne.lib.common.R.color.red_team,
            res = R.drawable.ic_tab_hall_lottery,
            _title = R.string.tab_lottery.getString(),
            _page = { GameContentFragment.newInstance() }
        ),
        HallGameTabDefault(
            colorRes =  arch.cayenne.lib.common.R.color.red_team,
            res = R.drawable.ic_tab_hall_esports,
            _title = R.string.tab_esprots.getString(),
            _page = { GameContentFragment.newInstance() }
        )
    )

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            root.touchBackPressed()

            balanceView.init(childFragmentManager)
            initCurveBanner()
            var barHeight = ViewUtils.getStatusBarHeight(requireContext())

            vpGame.adapter = PagerAdapter(childFragmentManager, lifecycle, mockTabList)
            launch {
                delay(500)
                vpGame.offscreenPageLimit = mockTabList.size
            }
            TabLayoutMediator(tlGame, vpGame,false) { tab, position ->
                tab.customView = createGameTabView(position, mockTabList[position])
                val paddingStart = 5.dp2px
                tab.view.setPadding(0, 0, paddingStart, 0)
                tab.customView?.findViewById<AppCompatImageView>(R.id.iv_Hall_tab_icon)?.apply {
                    if (position == 1) {
                        setScaleAnim(mMinHeight.toInt(), mMaxHeight.toInt())
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
                        setRoundedBackground(backgroundColor = mockTabList[position].colorRes.getColor(), show = true)
                    } else {
                        setTextColor(
                            SkinnableResourceManager.getColor(
                                context,
                                arch.cayenne.lib.common.R.color.color_C0C0C0
                            )
                        )
                        typeface = Typeface.DEFAULT
                        setRoundedBackground(backgroundColor = arch.cayenne.lib.common.R.color.title_bg.getColor(),show = false)
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
            tabBinding.ivHallTabIcon.setBackgroundResource(item.res)
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
        }

        mBinding.tlGame.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
            @SuppressLint("ResourceType")
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
                    launch{
                        if (!mBinding.aciTabBg.isGone){
                            tabIndicatorHelper?.smartAnimateToCurrent()
                        }

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
                    textView.setRoundedBackground(backgroundColor = mockTabList[tab.position].colorRes.getColor(),show = true)
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
                    tab.customView?.findViewById<AppCompatImageView>(R.id.iv_Hall_tab_icon)?.apply {
                        setScaleAnim(mMaxHeight.toInt(), mMinHeight.toInt())
                    }
                    textView.setRoundedBackground(backgroundColor = mockTabList[tab.position].colorRes.getColor(),show = false)
                    textView.typeface = Typeface.DEFAULT
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                // Handle reselect if needed
            }
        })
    }

    override suspend fun createObserver() {


        with(unreadMessageViewModel) {
            //未读消息监听
            unreadMsg.observe(viewLifecycleOwner) { flag ->
                mBinding.ivUnreadDot.visibility = if (flag) View.VISIBLE else View.GONE
            }
        }
        unreadMessageViewModel.createObserver()

        mViewModel. scorll.observe(viewLifecycleOwner){
            if (it){ //收起
                mBinding.homeBarIcon.marginEndAnim()
            }else{ //展开
                mBinding.homeBarIcon.marginStartAnim()
            }
        }
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
        StatusBarConfig.statusBarType =
            StatusBarMode.DRAW_BEHIND( autoIsNavigation = true)
        setStatusBar(StatusBarConfig, mBinding.clMain)
        super.onStart()
    }



    private fun initCurveBanner(){
        val images =  listOf(
            arch.cayenne.lib.common.R.drawable.home_bar_left_icon,
            arch.cayenne.lib.common.R.drawable.home_bar_left_icon,
            arch.cayenne.lib.common.R.drawable.home_bar_left_icon,
            arch.cayenne.lib.common.R.drawable.home_bar_left_icon
        )
        // 自定义适配器
        val adapter = BannerImageAdapter(images)
        mBinding.ivRightLogo.setAdapter(adapter)
        mBinding.ivRightLogo.setLoopTime(3000)
        // 设置滑动时长丝滑,不影响曲线,
        mBinding.ivRightLogo.setScrollTime(500)  // 1 秒
        mBinding.ivRightLogo.setPageTransformer(CustomCurveTransformer())
        // 启动轮播
        mBinding.ivRightLogo.start()
    }





}