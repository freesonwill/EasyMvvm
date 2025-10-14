package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import arch.cayenne.lib.skin.widget.SkinnableImageView
import arch.cayenne.lib.skin.widget.SkinnableTextView
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.databinding.FragmentMainChatLayoutBinding
import arch.cayenne.module.chat.databinding.ItemChatTablayoutLayoutBinding
import arch.cayenne.module.chat.ui.viewmodel.MainChatViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 6/10/25 17:46
 * @description: 首页聊天室
 */
class MainChatFragment : BaseFragment<MainChatViewModel, FragmentMainChatLayoutBinding>() {
    override val vbClass: KClass<FragmentMainChatLayoutBinding>
        get() = FragmentMainChatLayoutBinding::class
    override val vmClass: KClass<MainChatViewModel>
        get() = MainChatViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        initViewPager2()
//        initTabLayout()
    }

    override fun initListener() {
//        mBinding.tabLayout.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
//            override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
//
//                if (isTabClick) {
//                    mBinding.viewpager2.startFadeAnim {
//                        mBinding.viewpager2.setCurrentItem(tab.position, false)
//                        it.invoke()
//                    }
//                }
//                val tabIcon = tab.view.findViewById<SkinnableImageView>(R.id.tab_icon)
//                val tabTv = tab.view.findViewById<SkinnableTextView>(R.id.tab_tv)
//                updateTabBack(tab.position, true, tabIcon, tabTv)
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab, isTabClick: Boolean) {
//                val tabIcon = tab.view.findViewById<SkinnableImageView>(R.id.tab_icon)
//                val tabTv = tab.view.findViewById<SkinnableTextView>(R.id.tab_tv)
//                updateTabBack(tab.position, false, tabIcon, tabTv)
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) {
//            }
//        })
        mBinding.viewpager2.setCurrentItem(0, false)

    }

    override suspend fun createObserver() {
    }

    private fun initViewPager2() {
        val titles = arrayOf(
            R.string.chat_room.getString(),
            R.string.living_events.getString(),
            R.string.chat_customer.getString()
        )
        val pages = listOf(
            PagerBean(titles[0]) {
                ChatHomeFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean("chat", true)
                    }
                }
            },
            PagerBean(titles[0]) { LivingEventsFragment() },
            PagerBean(titles[2]) { CustomerFragment() })
        val adapter = PagerAdapter(childFragmentManager, lifecycle, pages)
        mBinding.viewpager2.adapter = adapter
    }

//    private fun initTabLayout() {
//        mBinding.apply {
//            TabLayoutMediator(tabLayout, viewpager2) { tab, position ->
//                val itemBinding = ItemChatTablayoutLayoutBinding.inflate(
//                    LayoutInflater.from(context),
//                    null,
//                    false
//                )
//                val tabIcon = itemBinding.tabIcon
//                val tabTv = itemBinding.tabTv
//                updateTabBack(position, position == 0, tabIcon, tabTv, isInit = true)
//                tab.setCustomView(itemBinding.root)
//            }.attach()
//            tabLayout.clearOnTabSelectedListeners()
//            tabLayout.removeAllTips()
//            reflexPadding(tabLayout)
//        }
//    }

    private fun updateTabBack(
        position: Int,
        isSelected: Boolean,
        tabIcon: SkinnableImageView,
        tabTv: SkinnableTextView,
        isInit: Boolean = false,
    ) {
        val textColorId = if (isSelected) arch.cayenne.lib.common.R.color.color_FFFFFF else arch.cayenne.lib.common.R.color.color_999999
        tabTv.setTextColor(ContextCompat.getColor(requireContext(), textColorId))

        val iconBackId = if (isSelected) arch.cayenne.lib.common.R.color.color_E03C64 else arch.cayenne.lib.common.R.color.color_0FFFFFFF
        tabIcon.backgroundTintList = ContextCompat.getColorStateList(requireContext(), iconBackId)

        when (position) {
            0 -> {
                tabIcon.setImageResource(if (isSelected) R.drawable.icon_main_chat_room_active else R.drawable.icon_main_chat_room_inactive)
                if (isInit) {
                    tabTv.text = R.string.chat_room.getString()
                }
            }

            1 -> {
                tabIcon.setImageResource(if (isSelected) R.drawable.icon_main_chat_living_active else R.drawable.icon_main_chat_living_inactive)
                if (isInit) {
                    tabTv.text = R.string.living_events.getString()
                }
            }

            2 -> {
                tabIcon.setImageResource(if (isSelected) R.drawable.icon_main_chat_customer_active else R.drawable.icon_main_chat_customer_inactive)
                if (isInit) tabTv.text = R.string.chat_customer.getString()
            }
        }
    }

    private fun reflexPadding(tabLayout: TabLayout) {
        tabLayout.post {
            try {
                //拿到tabLayout的mTabStrip属性
                val mTabStrip = tabLayout.getChildAt(0) as LinearLayout
                for (i in 0 until mTabStrip.childCount) {
                    val tabView = mTabStrip.getChildAt(i)
                    val params = tabView.layoutParams as LinearLayout.LayoutParams
                    when (i) {
                        0 -> {
                            params.leftMargin = 48.dp2px
                        }

                        1, 2 -> {
                            params.leftMargin = 74.dp2px
                        }
                    }
                    tabView.layoutParams = params
                    tabView.invalidate()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onStart() {
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(autoIsNavigation = true)
        setStatusBar(StatusBarConfig, mBinding.topBg)
        super.onStart()
    }

}