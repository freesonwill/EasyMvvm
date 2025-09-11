package arch.cayenne.module.home.ui.fragment

import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.NavResultExt.observeResult
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.setDrawerInterpolator
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.lib.common.utils.ext.startZoomInAnim
import arch.cayenne.lib.common.utils.helper.doSmartAnim
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.databinding.FragmentNewHomeBinding
import arch.cayenne.module.home.ui.adapter.SubHomePagerAdapter
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.KClass

class NewHomeFragment : BaseFragment<HomeViewModel, FragmentNewHomeBinding>() {
    override val vbClass: KClass<FragmentNewHomeBinding> = FragmentNewHomeBinding::class
    override val vmClass: KClass<HomeViewModel> = HomeViewModel::class
    private var drawerContentFragment: DrawerContentFragment? = null

    //    private val tournamentListFragment  = TournamentListFragment.newInstance()
    private var isExpanded = false

    override fun initView(savedInstanceState: Bundle?) {
        initPlayTypeLayout()
        setReceiveHorizontalScrollResult()
    }

    override fun onStart() {
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig,mBinding.clMain)
        super.onStart()
    }

    //init 一級導航欄位
    private fun initPlayTypeLayout() {
        with(mBinding) {
            ivHomeSidebar.clickNoRepeat {
                initDrawerContent()
                drawerLayout.openDrawer(GravityCompat.START)
            }
            val tabResList = mutableListOf<Int>()

            tlHome.setTabResArray(tabResList.toIntArray())
            tlHome.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
                override fun onTabSelected(tab: TabLayout.Tab, isTabClick:Boolean) {
                    tab.position.apply {
                        mViewModel.setCurrentPlayType(PlayType.entries[this].id)
                        val vp = mBinding.vpSub
                        if(isTabClick) {
                            vp.setCurrentItem(this, false)
                            vp.startZoomInAnim()
                        } else {
                            vp.doSmartAnim(this)
                        }
                        (childFragmentManager.findFragmentByTag("f$this") as? SubHomeFragment)?.onFragmentSelected()
                    }
                    tab.let {
                        // 设置选中Tab为粗体
                        (it.view.getChildAt(1) as? TextView)?.typeface = Typeface.DEFAULT_BOLD
                    }

                }

                override fun onTabUnselected(tab: TabLayout.Tab, isTabClick:Boolean) {
                    tab.position.apply {
                        (childFragmentManager.findFragmentByTag("f$this") as? SubHomeFragment)?.onFragmentUnSelected()
                    }
                    tab.let {
                        // 设置默认
                        (it.view.getChildAt(1) as? TextView)?.typeface = Typeface.DEFAULT
                    }
                }
                override fun onTabReselected(tab: TabLayout.Tab, isTabClick:Boolean) {}
            })

            PlayType.entries.forEachIndexed { index, playType ->
                tabResList.add(playType.titleRes)
                tlHome.addTab(
                    tab = tlHome.newTab().apply {
                        setText(playType.titleRes)
                    },
                    setSelected = index == 0,
                )
            }
            vpSub.adapter = SubHomePagerAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = viewLifecycleOwner.lifecycle,
                playTypes = listOf(PlayType.TODAY, PlayType.EARLY, PlayType.CHAMPION)
            )

            /*CustomTabLayoutMediator(
                tabLayout = tlHome,
                viewPager = vpSub,
            ) { tab, position ->
                tab.setText(PlayType.entries[position].titleRes)
            }.also { it.attach () }*/

            TabLayoutMediator(tlHome, vpSub) { tab, position ->
                tab.setText(PlayType.entries[position].titleRes)
            }.attach()

            vpSub.offscreenPageLimit = 1
            vpSub.setupHorizontalScrollDegree()
        }
    }

    private fun setReceiveHorizontalScrollResult() {
        childFragmentManager.setFragmentResultListener(getString(R.string.new_home_vp_sub_key), this) { requestKey, bundle ->
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
            llWalletEntry.apply {
                addScaleOnTouchAnimation(ivWalletAdd)
            }.setOnClickListener {
                //navigate(Uri.parse("walisport://module_home/homeFragment"))
                navigate(Uri.parse("walisport://module_topup/topUpFragment"))
            }


            mBinding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
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
            mBinding.tvWalletBalance.text =
                getString(
                    R.string.balance_format,
                    CurrencySymbols.getSymbol(it?.currency?:""),
                    (it?.balance?:0L).getFormalMoney()
                )
        }


        mViewModel.playTypeIndexChange.observeEvent(viewLifecycleOwner, this) {
            mBinding.tlHome.getTabAt(it)?.select()
            mBinding.tlHome.removeAllTips()
        }


        mViewModel.apiStateListener.observe(viewLifecycleOwner) {
            when(it) {
                is HomeState.Tournament.LoadSuccess, HomeState.Tournament.LoadFailure, HomeState.Sport.LoadFailure -> {

                }
                is DataState.NetworkUnavailable, DataState.NoMoreData, HomeState.Match.LoadSuccess, HomeState.Match.DataEmpty -> {
                    initDrawerContent()
                }
                else -> Unit
            }
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

}