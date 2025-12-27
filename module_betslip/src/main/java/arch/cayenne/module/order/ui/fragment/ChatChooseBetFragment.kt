package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.ChatMsgType
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.UnReadMessageViewModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavResultExt.sendResult
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.betslip.BuildConfig
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
        const val SHARE_BET_LISTEN = "share_bet_listen"
        const val SHARE_BET_RESULT = "share_bet_result"
        const val SHARE_BET_TYPE = "share_bet_type"

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
        mViewModel.apiStateListener.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    mBinding.apply {
                        viewPager.isVisible = false
                        dynamicStateLayout.isVisible = true
                        dynamicStateLayout.setState(
                            DynamicStateLayout.States.LOADING,
                            getString(arch.cayenne.lib.common.R.string.loading)
                        )
                    }

                }

                is DataState.NetworkUnavailable -> {
                    mBinding.apply {
                        viewPager.isVisible = true
                        dynamicStateLayout.isVisible = false
                        showToast("获取分享注单失败")
                    }
                }

                is DataState.LoadSuccess -> {
                    mBinding.apply {
                        viewPager.isVisible = true
                        dynamicStateLayout.isVisible = false
                    }
                }

                else -> {}
            }
        }

        mViewModel.betClickLiveData.observe(viewLifecycleOwner) {
            betType = if (it.type == ChatMsgType.BET_GAME) 0 else 1
            val betCode =
                if (it.type == ChatMsgType.BET_GAME) "bet-test202512223-wg0s53" else it.betCode
            val userId = if(it.type == ChatMsgType.BET_GAME) 6660030 else mViewModel.getUid()
            mViewModel.getBetShare(userId, betCode)

//            val bundle = Bundle().apply {
//                putInt(BET_TYPE, if (it.type == ChatMsgType.BET_GAME) 0 else 1)
//                putString(BET_ID,if(it.type == ChatMsgType.BET_GAME) "bet-test202512223-wg0s53" else it.betCode)
//            }
//            sendResult("choose_bet", bundle)
//            findNavController().navigateUp()
        }
        mViewModel.betShareLiveData.observe(viewLifecycleOwner) {
            val bundle = Bundle().apply {
                putParcelable(SHARE_BET_RESULT, it)
                putInt(SHARE_BET_TYPE, betType)
            }
            sendResult(SHARE_BET_LISTEN, bundle)
            findNavController().navigateUp()
        }
    }

    var betType = 0

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