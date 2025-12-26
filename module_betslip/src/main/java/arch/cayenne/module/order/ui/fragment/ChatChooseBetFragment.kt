package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.ChatMsgType
import arch.cayenne.lib.common.ui.viewmodel.UnReadMessageViewModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavResultExt.sendResult
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.FragmentChooseBetLayoutBinding
import arch.cayenne.module.order.data.constants.OrderPageEnum
import arch.cayenne.module.order.ui.viewmodel.ChatChooseViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 11/12/25 19:21
 * @description: 聊天选择注单页面
 */
class ChatChooseBetFragment : BaseFragment<ChatChooseViewModel, FragmentChooseBetLayoutBinding>() {
    override val vbClass: KClass<FragmentChooseBetLayoutBinding>
        get() = FragmentChooseBetLayoutBinding::class
    override val vmClass: KClass<ChatChooseViewModel>
        get() = ChatChooseViewModel::class
    private val unreadMessageViewModel: UnReadMessageViewModel by viewModels()
    private var tabLayoutMediator: TabLayoutMediator? = null

    companion object {
        const val CHOOSE_BET_MODE = "CHOOSE_BET_MODE"
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.tabLayout.post {
            mBinding.tabLayout.removeAllTips()
        }
        mBinding.viewPager.setupHorizontalScrollDegree()
        initViewPager()
    }

    private fun initViewPager() {
        val page = OrderPageEnum.entries.toTypedArray()
        mBinding.viewPager.adapter =
            PagerAdapter(childFragmentManager, lifecycle, page.map { it.page })

        tabLayoutMediator?.detach()
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager, false) { tab, position ->
            tab.text = page[position].page.title
        }.apply {
            tabLayoutMediator = this
        }.attach()
        mBinding.tabLayout.setSelectedTabIndicator(R.drawable.bg_order_indicator)
        reflexPadding(mBinding.tabLayout)
    }


    override fun initListener() {
        with(mBinding) {
            ivClose.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override suspend fun createObserver() {
        with(unreadMessageViewModel) {
            //未读消息监听
            unreadMsg.observe(viewLifecycleOwner) { flag ->

            }
        }
        unreadMessageViewModel.createObserver()
        mViewModel.betClickLiveData.observe(viewLifecycleOwner) {
            val bundle = Bundle().apply {
                putInt("key", if (it == ChatMsgType.BET_GAME) 0 else 1)
            }
            sendResult("choose_bet", bundle)
            findNavController().navigateUp()
        }
    }

    private fun reflexPadding(tabLayout: TabLayout) {
        tabLayout.post {
            try {
                val mTabStrip = tabLayout.getChildAt(0) as LinearLayout
                for (i in 0 until mTabStrip.childCount) {
                    val tabView = mTabStrip.getChildAt(i)
                    val params = tabView.layoutParams as LinearLayout.LayoutParams
                    params.width = 70.dp2px
                    params.marginStart = if (i == 0) 65.dp2px else 105.dp2px
                    tabView.layoutParams = params
                    tabView.invalidate()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(autoIsNavigation = true)
        setStatusBar(StatusBarConfig, mBinding.root)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }
}