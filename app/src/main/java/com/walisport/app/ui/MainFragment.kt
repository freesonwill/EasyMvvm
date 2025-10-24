package com.walisport.app.ui

import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.DrawerAction.ACTION_CLOSE
import arch.cayenne.lib.common.data.constants.DrawerAction.ACTION_INIT
import arch.cayenne.lib.common.data.constants.DrawerAction.ACTION_OPEN
import arch.cayenne.lib.common.data.constants.DrawerAction.KEY_ACTION
import arch.cayenne.lib.common.data.constants.DrawerAction.REQUEST_KEY_DRAWER
import arch.cayenne.lib.common.ui.fragment.EmptyFragment
import arch.cayenne.lib.common.utils.ext.NavResultExt.observeResult
import arch.cayenne.lib.common.utils.ext.setDrawerInterpolator
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.chat.ui.fragment.MainChatFragment
import arch.cayenne.module.home.ui.fragment.DrawerContentFragment
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
    private val fragments = SparseArray<Fragment>()
    private val titleRes = arrayOf("体育","注单","聊天","我")
    private var drawerContentFragment: DrawerContentFragment? = null

    override fun initView(savedInstanceState: Bundle?) {
        setCurrentFragment(selectedIndex)
        initDrawerContent()
        setDrawerLayoutListener()
    }

    private fun initDrawerContent() {
        //TODO 優化initDrawerContent及setCurrentFragment在replace及add問題
        drawerContentFragment = DrawerContentFragment()
        drawerContentFragment.also {
            it?.setOnFunctionClickListener {
//                    mBinding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        //蒙層顏色依照版型作變化
        mBinding.drawerLayout.setScrimColor(
            SkinnableResourceManager.getColor(
                requireContext(),
                arch.cayenne.module.home.R.color.drawer_scrim_color
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
                    ACTION_INIT -> initDrawerContent()
                }
            }
            bottomNavigation.setOnItemSelectedListener { container, view, position ->
                container.setSelected(position)
                //"bottomNavigation1----$position".logd(TAG)
                setCurrentFragment(position)
                initDrawerContent()
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

    private fun getFragment(position: Int): Fragment {
        var f = fragments[position]
        if (f == null) {
            f = when (position) {
                0 -> HallFragment()
                1 -> NewHomeFragment()
                2 -> HomeOrderFragment()
                3 -> MainChatFragment()
                4 -> MeFragment()
                else -> EmptyFragment()
            }
            fragments[position] = f
        }
        if(position != 0 && f is EmptyFragment){
            f.setTitle(titleRes[position-1])
        }
        return f
    }

    private fun setCurrentFragment(index: Int) {
        val fragment = getFragment(index)
        childFragmentManager.beginTransaction().apply {
            childFragmentManager.fragments.find { it.isVisible }?.let {
                hide(it)
            }
            if (!fragment.isAdded) {
                add(R.id.fragment_container, fragment)
            } else {
                show(fragment)
            }
        }.commitNow()
        /*//java.lang.IllegalStateException: Fragment no longer exists for key f#0: unique id ba2286df-4545-4383-b414-da475c5d5aac
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()*/
    }
}