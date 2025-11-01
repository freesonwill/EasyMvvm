package com.walisport.app.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.fragment.EmptyFragment
import arch.cayenne.module.chat.ui.fragment.MainChatFragment
import arch.cayenne.lib.common.data.constants.DrawerAction.ACTION_CLOSE
import arch.cayenne.lib.common.data.constants.DrawerAction.ACTION_OPEN
import arch.cayenne.lib.common.data.constants.DrawerAction.KEY_ACTION
import arch.cayenne.lib.common.data.constants.DrawerAction.REQUEST_KEY_DRAWER
import arch.cayenne.lib.common.utils.ext.setDrawerInterpolator
import arch.cayenne.module.home.ui.fragment.NewHomeFragment
import arch.cayenne.module.order.ui.fragment.HomeOrderFragment
import com.walisport.app.R
import com.walisport.app.databinding.FragmentMainBinding
import com.walisport.module.hall.ui.fragment.HallFragment
import com.walisport.module.me.ui.fragment.MeFragment
import kotlin.reflect.KClass


class MainFragment : BaseFragment<EmptyViewModel, FragmentMainBinding>() {
    override val vbClass: KClass<FragmentMainBinding>
        get() = FragmentMainBinding::class
    override val vmClass: KClass<EmptyViewModel>
        get() = EmptyViewModel::class
    private val selectedIndex get() = mBinding.bottomNavigation.selectedIndex
    private val titleRes = arrayOf("体育","注单","聊天","我")
    // 1. 使用 lazy 延遲初始化並持有所有 Fragment 實例
    private val fragments by lazy {
        listOf(
            HallFragment(),
            NewHomeFragment(),
            HomeOrderFragment(),
            MainChatFragment(),
            MeFragment()
        )
    }

    override fun initView(savedInstanceState: Bundle?) {
        setCurrentFragment(selectedIndex)
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
                container.setSelected(position)
                //"bottomNavigation1----$position".logd(TAG)
                setCurrentFragment(position)
//                when (position) {
//                    0 -> {
//                        val w = container.getWeight(1)
//                        if (w == 1f) {
//                            container.setWeight(1, 80f / 75f)
//                            container.setBarStyle(
//                                1,
//                                Style.IconBadge(
//                                    R.mipmap.ic_fifa.getDrawable(),
//                                    "世界杯",
//                                    (-18f).dp2px
//                                )
//                            )
//                        } else {
//                            container.setBarStyle(
//                                1,
//                                Style.IconTextBadge(
//                                    R.drawable.ic_chat.getDrawable(),
//                                    R.string.title_sport.getString(), "9"
//                                )
//                            )
//                            container.setWeight(1, 1f)
//                        }
//                    }
//
//                    1 -> {
//                        val w = container.getWeight(2)
//                        if (w == 1f) {
//                            container.setWeight(2, 121f / 75f)
//                            container.setBarStyle(
//                                2,
//                                Style.Icon(R.mipmap.ic_sport_banner.getDrawable())
//                            )
//                        } else {
//                            container.setBarStyle(
//                                2,
//                                Style.IconTextBadge(
//                                    R.drawable.ic_chat.getDrawable(),
//                                    R.string.title_sport.getString(), "9"
//                                )
//                            )
//                            container.setWeight(2, 1f)
//                        }
//                    }
//
//                    2 -> {
//                        if (container.getBarStyle(3) == Style.Icon::class.java) {
//                            container.setBarStyle(
//                                3,
//                                Style.IconText(
//                                    R.drawable.ic_chat.getDrawable(),
//                                    R.string.title_sport.getString()
//                                )
//                            )
//                        } else {
//                            container.setBarStyle(3, Style.Icon(R.mipmap.ic_home2.getDrawable()))
//                        }
//                    }
//
//                    3 -> {
//
//                    }
//                }
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

    private fun setCurrentFragment(index: Int) {
        if (index !in fragments.indices) return // 防呆

        val transaction = childFragmentManager.beginTransaction()
        fragments.forEachIndexed { position, fragment ->
            if (position == index) {
                if (fragment.isAdded) {
                    transaction.show(fragment)
                } else {
                    transaction.add(R.id.fragment_container, fragment)
                }
            } else {
                if (fragment.isAdded) {
                    transaction.hide(fragment)
                }
            }
            if(position != 0 && fragment is EmptyFragment){
                fragment.setTitle(titleRes[position-1])
            }
        }
        transaction.commit()
        /*//java.lang.IllegalStateException: Fragment no longer exists for key f#0: unique id ba2286df-4545-4383-b414-da475c5d5aac
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()*/
    }
}