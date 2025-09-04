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
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.setDrawerInterpolator
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.databinding.FragmentNewHomeBinding
import arch.cayenne.module.home.ui.adapter.SubHomePagerAdapter
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlin.reflect.KClass

class NewHomeFragment : BaseFragment<HomeViewModel, FragmentNewHomeBinding>() {
    override val vbClass: KClass<FragmentNewHomeBinding> = FragmentNewHomeBinding::class
    override val vmClass: KClass<HomeViewModel> = HomeViewModel::class
    private var drawerContentFragment: DrawerContentFragment? = null

    //    private val tournamentListFragment  = TournamentListFragment.newInstance()
    private var isExpanded = false

    override fun initView(savedInstanceState: Bundle?) {
        initPlayTypeLayout()
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
            tlHome.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.position?.apply {
                        mViewModel.setCurrentPlayType(PlayType.entries[this].id)
                        vpSub.setCurrentItem(this, false)
                        (childFragmentManager.findFragmentByTag("f$this") as? SubHomeFragment)?.onFragmentSelected()
                    }
                    tab?.let {
                        // 设置选中Tab为粗体
                        (it.view.getChildAt(1) as? TextView)?.typeface = Typeface.DEFAULT_BOLD
                    }
//                    layoutContainer.viewContainerRoot.startPageAnimation()
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.position?.apply {
                        (childFragmentManager.findFragmentByTag("f$this") as? SubHomeFragment)?.onFragmentUnSelected()
                    }
                    tab?.let {
                        // 设置默认
                        (it.view.getChildAt(1) as? TextView)?.typeface = Typeface.DEFAULT
                    }
                }
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
            PlayType.entries.forEachIndexed { index, playType ->
                tabResList.add(playType.titleRes)
                tlHome.addTab(
                    tab = tlHome.newTab().apply {
                        setText(playType.titleRes)
                    },
                    setSelected = index == 0
                )
            }

            vpSub.adapter = SubHomePagerAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = viewLifecycleOwner.lifecycle,
                playTypes = listOf(PlayType.TODAY, PlayType.EARLY, PlayType.CHAMPION)
            )
            vpSub.offscreenPageLimit = 1
            vpSub.isUserInputEnabled = false
        }
    }

    //當一級導航改變時，先把底下的view資料清除，等待讀取最新的資料，避免api取得過久，導致UI不協調
    private fun resetHomeView() {
//        toggleTournamentMoreSection(false, TournamentListType.NONE)
//        mBinding.layoutContainer.llDateFilterContainer.visibility = View.GONE
//        mBinding.layoutContainer.llOtherDate.visibility = View.GONE
//        mBinding.ivTournamentMore.visibility = View.GONE
//        mBinding.llHomeTournamentMore.visibility = View.GONE
//
//        if (mViewModel.currentPlayTypeId == PlayType.EARLY.id) {
//            mBinding.layoutContainer.llDateFilterContainer.visibility = View.VISIBLE
//            mBinding.layoutContainer.llOtherDate.visibility = View.VISIBLE
//        }
    }



    //init DrawerLayout Content
    private fun initDrawerContent() {
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
        childFragmentManager.beginTransaction()
            .replace(
                mBinding.fragmentDrawerContent.id,
                drawerContentFragment!!,
                DrawerContentFragment.TAG
            )
            .commitNow()
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
            resetHomeView()
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



    override fun onBackPressed(): Boolean {
        //如果抽屉打开，截获此次返回事件，关闭抽屉
        if(mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
        return super.onBackPressed()
    }

}