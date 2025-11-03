package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.DrawerAction.ACTION_OPEN
import arch.cayenne.lib.common.data.constants.DrawerAction.KEY_ACTION
import arch.cayenne.lib.common.data.constants.DrawerAction.REQUEST_KEY_DRAWER
import arch.cayenne.lib.common.ui.viewmodel.UnReadMessageViewModel
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.FragmentHomeOrderBinding
import arch.cayenne.module.order.data.constants.OrderPageEnum
import arch.cayenne.module.order.ui.viewmodel.BetMode
import arch.cayenne.module.order.ui.viewmodel.HomeOrderViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.filterNotNull
import kotlin.reflect.KClass

/**
 * 投注记录&注单
 */
class HomeOrderFragment: BaseFragment<HomeOrderViewModel, FragmentHomeOrderBinding>() {

    override val vbClass: KClass<FragmentHomeOrderBinding> = FragmentHomeOrderBinding::class
    override val vmClass: KClass<HomeOrderViewModel> = HomeOrderViewModel::class

    private val unreadMessageViewModel: UnReadMessageViewModel by viewModels()
    private var tabLayoutMediator:TabLayoutMediator? = null

    companion object {
        const val BET_MODE = "BET_MODE"
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.tabLayout.post {
            mBinding.tabLayout.removeAllTips()
        }
        mBinding.viewPager.setupHorizontalScrollDegree()
    }


    private fun setMode(m: BetMode){
        //"lifecycle.currentState----${lifecycle.currentState},isAtLeast:${lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)}".logd(TAG)
        if(!lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            launch(Lifecycle.State.STARTED,lifecycleScope){ setMode(m) }
        } else {
            mViewModel.betModelFlow.value = m
        }
    }

    override fun initListener() {
        with (mBinding) {
            ivHam.addScaleOnTouchAnimation()
            ivHam.clickNoRepeat {
                requireActivity().supportFragmentManager.setFragmentResult(
                    REQUEST_KEY_DRAWER,
                    bundleOf(KEY_ACTION to ACTION_OPEN)
                )
            }
        }
    }

    override suspend fun createObserver() {
        with(unreadMessageViewModel) {
            //未读消息监听
            unreadMsg.observe(viewLifecycleOwner) { flag ->
                mBinding.dotHam.visibility = if (flag) android.view.View.VISIBLE else android.view.View.GONE
            }
        }

        unreadMessageViewModel.createObserver()
        launch {
            mViewModel.betModelFlow.filterNotNull().collect {
                val page = when(it){
                    BetMode.BET_RECORD -> {
                        OrderPageEnum.entries.toTypedArray()
                    }
                    else -> {
                        arrayOf(OrderPageEnum.SPORT)
                    }
                }
                mBinding.viewPager.adapter = PagerAdapter(childFragmentManager, lifecycle, page.map { it.page })

                tabLayoutMediator?.detach()
                TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager,false) { tab, position ->
                    tab.text = page[position].page.title
                }.apply {
                    tabLayoutMediator = this
                }.attach()

                if(page.size > 1) {
                    mBinding.tabLayout.setSelectedTabIndicator(R.drawable.bg_order_indicator)
                } else {
                    mBinding.tabLayout.setSelectedTabIndicator(null)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        //"onHiddenChanged--arguments---$arguments--hidden:$hidden".logd(TAG)
        arguments?.getInt(BET_MODE)?.let {
            setMode(BetMode.entries.toTypedArray()[it])
        }
    }
}