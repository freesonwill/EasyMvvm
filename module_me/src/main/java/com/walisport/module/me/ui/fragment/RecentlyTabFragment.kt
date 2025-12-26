package com.walisport.module.me.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.adapter.GridSpacingItemDecoration
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.business.common.data.Category
import com.walisport.module.business.common.data.UniversalLoadMoreScrollListener
import com.walisport.module.business.common.ui.adapter.GameContentAdapter
import com.walisport.module.live.data.EventClick
import com.walisport.module.me.databinding.FragmentRecentlyTabBinding
import com.walisport.module.me.ui.viewmodel.RecentlyTabViewModel
import kotlinx.coroutines.delay
import kotlin.reflect.KClass

/**
 * 我的页面底部的最近tab
 * @date: 2025/10/17 16:52
 * @description:
 */
class RecentlyTabFragment : BaseFragment<RecentlyTabViewModel, FragmentRecentlyTabBinding>() {

    override val vbClass: KClass<FragmentRecentlyTabBinding> = FragmentRecentlyTabBinding::class
    override val vmClass: KClass<RecentlyTabViewModel> = RecentlyTabViewModel::class

    private lateinit var adapter: GameContentAdapter

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            rvRecently.layoutManager = GridLayoutManager(requireContext(), 3)
            val itemDecoration = GridSpacingItemDecoration(
                spanCount = 3,
                horizontalSpacing = 9.dp2px,
                verticalSpacing = 17.dp2px,
                includeEdge = false // 確保邊緣沒有空隙
            )
            rvRecently.addItemDecoration(itemDecoration)
            adapter = GameContentAdapter(onItemClick = {
                mViewModel.setIsClickGame(EventClick.EVENT_CLICK_ACK_TRUE.type)
                navigate(arch.cayenne.lib.res.R.string.nav_module_gamedetail.deeplink("gameId" to it.id))
                launch {
                    delay(AnimationController[AnimType.popupExit]!!.duration)
                    adapter.submitList(emptyList())
                }

            })
            rvRecently.adapter = adapter
//            BackToTopHelper(rvRecently, ivBackToTop, true)
        }
    }

    override fun initListener() {

        mBinding.rvRecently.addOnScrollListener(UniversalLoadMoreScrollListener(6) {
            if (mViewModel.apiStateListener.value == DataState.LoadSuccess) {
                mViewModel.loadNextPage()
            }
        })
    }

    override suspend fun createObserver() {
        mViewModel.gameListLiveData.observe(viewLifecycleOwner) {
            it.let { list ->
                adapter.submitList(list)

                // 自動加載下一頁數據（如果當前數據量較少）
                if (list.size <= 10) {
                    mViewModel.loadNextPage()
                }
            }
        }

        mViewModel.gameClickData.observe(viewLifecycleOwner) {
            it?.let {
                if (it.clickFlag== EventClick.EVENT_CLICK_ACK_TRUE.type){
                    mViewModel.reload()
                    mViewModel.setIsClickGame(EventClick.EVENT_CLICK_ACK_FALSE.type)
                }
            }
        }

        mViewModel.apiStateListener.observe(viewLifecycleOwner) { state ->
            when (state) {
                DataState.LoadSuccess -> {
                    mBinding.rvRecently.visibility = View.VISIBLE
                    mBinding.clDynamics.visibility = View.GONE
                }

                DataState.DataEmpty -> {
                    mBinding.rvRecently.visibility = View.GONE
                    mBinding.clDynamics.visibility = View.VISIBLE
                    mBinding.clDynamics.setState(
                        DynamicStateLayout.States.DATA_EMPTY,
                        com.walisport.module.business.common.R.string.game_data_empty.getString()
                    )

                }

                DataState.NoMoreData -> {
                    mBinding.rvRecently.visibility = View.VISIBLE
                    mBinding.clDynamics.visibility = View.GONE
                }

                DataState.NetworkUnavailable -> {
                    mBinding.rvRecently.visibility = View.GONE
                    mBinding.clDynamics.visibility = View.VISIBLE
                    mBinding.clDynamics.setState(
                        DynamicStateLayout.States.NETWORK_ANOMALY(),
                        arch.cayenne.lib.common.R.string.error_net.getString()
                    )
                }

                else -> {
                }
            }
        }


    }

    override fun initData() {
        arguments?.apply {
            mViewModel.setCategory(Category.RECENT.type)
        }
        mViewModel.reload()
        super.initData()
    }
}