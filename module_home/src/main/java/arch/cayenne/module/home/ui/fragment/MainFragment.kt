package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import android.util.SparseArray
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.ViewExt.applyInsetsForFitsSystemWindows
import arch.cayenne.lib.common.ui.fragment.EmptyFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentMainBinding
import arch.cayenne.module.home.ui.view.Style
import kotlin.reflect.KClass


class MainFragment : BaseFragment<EmptyViewModel, FragmentMainBinding>() {
    override val vbClass: KClass<FragmentMainBinding>
        get() = FragmentMainBinding::class
    override val vmClass: KClass<EmptyViewModel>
        get() = EmptyViewModel::class
    private val selectedIndex get() = mBinding.bottomNavigation.selectedIndex
    private val fragments = SparseArray<Fragment>()
    private val titleRes = arrayOf("体育","注单","聊天","我")

    override fun initView(savedInstanceState: Bundle?) {
        setCurrentFragment(selectedIndex)
    }

    override fun initListener() {
        mBinding.apply {
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

    }

    override fun onStart() {
        super.onStart()
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // 给布局设置 padding，不避开状态栏，避开导航栏
            view.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig,mBinding.root)
    }

    private fun getFragment(position: Int): Fragment {
        var f = fragments[position]
        if (f == null) {
            f = when (position) {
                0 -> NewHomeFragment()
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
        }.commit()
        /*//java.lang.IllegalStateException: Fragment no longer exists for key f#0: unique id ba2286df-4545-4383-b414-da475c5d5aac
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()*/
    }
}