package com.walisport.app.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.module.chat.ui.fragment.MainChatFragment
import arch.cayenne.lib.common.data.constants.DrawerAction.ACTION_CLOSE
import arch.cayenne.lib.common.data.constants.DrawerAction.ACTION_OPEN
import arch.cayenne.lib.common.data.constants.DrawerAction.KEY_ACTION
import arch.cayenne.lib.common.data.constants.DrawerAction.REQUEST_KEY_DRAWER
import arch.cayenne.lib.common.data.constants.FragmentResultEnum
import arch.cayenne.lib.common.utils.ext.NavResultExt.observeResult
import arch.cayenne.lib.common.utils.ext.setDrawerInterpolator
import arch.cayenne.module.home.ui.fragment.NewHomeFragment
import arch.cayenne.module.home.ui.view.Style
import arch.cayenne.module.order.ui.fragment.HomeOrderFragment
import com.walisport.app.R
import com.walisport.app.databinding.FragmentMainBinding
import com.walisport.app.ui.viewmodel.BetSlot
import com.walisport.app.ui.viewmodel.MainFragmentViewModel
import com.walisport.module.hall.ui.fragment.HallFragment
import com.walisport.module.me.ui.fragment.MeFragment
import kotlinx.coroutines.flow.filterNotNull
import kotlin.reflect.KClass


class MainFragment : BaseFragment<MainFragmentViewModel, FragmentMainBinding>() {
    override val vbClass: KClass<FragmentMainBinding>
        get() = FragmentMainBinding::class
    override val vmClass: KClass<MainFragmentViewModel>
        get() = MainFragmentViewModel::class
    // 1. 使用 lazy 延遲初始化並持有所有 Fragment 實例
    private val fragments by lazy {
        arrayOfNulls<Fragment>(BottomNavType.entries.size)
    }

    enum class BottomNavType(@DrawableRes val icon:Int,@StringRes val title:Int) {
        HOME(arch.cayenne.module.home.R.drawable.ic_home, arch.cayenne.module.home.R.string.title_home),
        SPORT(arch.cayenne.module.home.R.drawable.ic_sport, arch.cayenne.module.home.R.string.title_sport),
        BET_SLOT(arch.cayenne.module.home.R.drawable.ic_betslip, arch.cayenne.module.home.R.string.title_betslip),
        CHAT(arch.cayenne.module.home.R.drawable.ic_chat, arch.cayenne.module.home.R.string.title_chat),
        ME(arch.cayenne.module.home.R.drawable.ic_me, arch.cayenne.module.home.R.string.title_me),
    }


    private fun getFragment(position: Int): Fragment {
        return fragments[position] ?: when (position) {
            0 -> HallFragment()
            1 -> NewHomeFragment()
            2 -> HomeOrderFragment()
            3 -> MainChatFragment()
            else -> MeFragment()
        }.also { fragments[position] = it}
    }

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.selectedIndexFlow.value.let {
            setCurrentFragment(it)
            mBinding.bottomNavigation.selectedIndex = it
        }
        setDrawerLayoutListener()
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
    override fun initListener() {

        mBinding.apply {
            setDrawerLayoutListener()
            // 設定監聽器，使用 parentFragmentManager
            requireActivity().supportFragmentManager.setFragmentResultListener(REQUEST_KEY_DRAWER, this@MainFragment) { requestKey, bundle ->
                when (bundle.getString(KEY_ACTION)) {
                    ACTION_OPEN -> drawerLayout.openDrawer(GravityCompat.START)
                    ACTION_CLOSE -> drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            bottomNavigation.setOnItemSelectedListener { container, view, position ->
                container.selectedIndex = position
                //"bottomNavigation1----$position".logd(TAG)
                val arguments:Bundle? = if(position != BottomNavType.BET_SLOT.ordinal) null else Bundle().apply {
                    putInt(HomeOrderFragment.BET_MODE,mViewModel.betSlotFlow.value.ordinal)
                }
                setCurrentFragment(position,arguments)
                mViewModel.selectedIndexFlow.value = position
            }
        }

        childFragmentManager.setFragmentResultListener(FragmentResultEnum.KEY_PAGE.k,viewLifecycleOwner){ key, bundle->
            bundle.getInt(key,-1).let {
                if(it == - 1) return@let
                mBinding.bottomNavigation.selectedIndex = it
                setCurrentFragment(it,bundle.apply { remove(FragmentResultEnum.KEY_PAGE.k) })
            }
        }
    }

    override suspend fun createObserver() {

        launch {
            AnimationController.getFlow(AnimType.drawerEnter).collect {
                if(it == null) return@collect
                mBinding.drawerLayout.setDrawerInterpolator(it.duration, it.interpolator.toInterpolator())
            }
        }
        launch {
            mViewModel.betSlotFlow.filterNotNull().collect {
                mBinding.navBetSlip.setBarStyle(Style.IconText(it.icon,it.title))
            }
        }
        launch {
            mViewModel.selectedIndexFlow.collect {
                if(it == BottomNavType.HOME.ordinal) mViewModel.betSlotFlow.value = BetSlot.BET_RECORD
                else if(it == BottomNavType.SPORT.ordinal) mViewModel.betSlotFlow.value = BetSlot.BET_SLIP
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            //設定底部欄位margin
            val lp = mBinding.bottomNavigation.layoutParams as ViewGroup.MarginLayoutParams
            lp.bottomMargin = systemBars.bottom
            mBinding.bottomNavigation.layoutParams = lp
            // 给布局设置 padding，不避开状态栏，避开导航栏
            view.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
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

    private fun setCurrentFragment(index: Int, arguments: Bundle? = null) {
        if (index !in fragments.indices) return // 防呆
        val fragment = getFragment(index)
        fragment.arguments = arguments
        childFragmentManager.beginTransaction().apply {
            for(i in BottomNavType.entries.indices) {
                childFragmentManager.findFragmentByTag("$TAG$i")?.let {
                    //"hide fragment ---> $it".logd(TAG)
                    hide(it)
                }
            }
            if (!fragment.isAdded) {
                add(R.id.fragment_container,fragment,"$TAG$index")
                fragment.launch(Lifecycle.State.RESUMED, fragment.lifecycleScope){
                    fragment.onHiddenChanged(false)
                }
            } else {
                show(fragment)
            }
        }.commit()
        //java.lang.IllegalStateException: Fragment no longer exists for key f#0: unique id ba2286df-4545-4383-b414-da475c5d5aac
        /*childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment,"hello")
            .commit()*/
    }

}