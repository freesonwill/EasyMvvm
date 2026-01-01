package com.walisport.module.me.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
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
import com.walisport.module.business.common.data.UniversalLoadMoreScrollListener
import com.walisport.module.business.common.ui.adapter.GameContentSimpleAdapter
import com.walisport.module.live.data.EventClick
import com.walisport.module.me.databinding.FragmentGameCollectionsBinding
import com.walisport.module.me.ui.viewmodel.GameCollectionTabViewModel
import com.walisport.module.me.ui.viewmodel.MeViewModel
import kotlinx.coroutines.delay
import kotlin.reflect.KClass

/**
 *
 * @date: 2025/10/17 16:52
 * @description:
 */
class GameCollectionsTabFragment :
    BaseFragment<GameCollectionTabViewModel, FragmentGameCollectionsBinding>() {

    override val vbClass: KClass<FragmentGameCollectionsBinding> =
        FragmentGameCollectionsBinding::class
    override val vmClass: KClass<GameCollectionTabViewModel> = GameCollectionTabViewModel::class

    private val parentViewModel: MeViewModel by viewModels({ requireParentFragment() })

    private lateinit var adapter: GameContentSimpleAdapter


    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            rvRecently.layoutManager = GridLayoutManager(requireContext(), 3)
            val itemDecoration = GridSpacingItemDecoration(
                spanCount = 3,
                horizontalSpacing = 12.dp2px,
                verticalSpacing = 12.dp2px,
                includeEdge = false // 確保邊緣沒有空隙
            )
            rvRecently.addItemDecoration(itemDecoration)
            adapter = GameContentSimpleAdapter(onItemClick = {
                mViewModel.setIsClickGame(EventClick.EVENT_CLICK_ACK_TRUE.type)
                navigate(arch.cayenne.lib.res.R.string.nav_module_gamedetail.deeplink("gameId" to it.gameID))
            })
            rvRecently.itemAnimator = null
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
                mBinding.rvRecently.post {
                    setHeight()
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
                    setHeight()
                }

                DataState.NoMoreData -> {
                    mBinding.rvRecently.visibility = View.VISIBLE
                    mBinding.clDynamics.visibility = View.GONE
                    setHeight()
                }

                DataState.NetworkUnavailable -> {
                    mBinding.rvRecently.visibility = View.GONE
                    mBinding.clDynamics.visibility = View.VISIBLE
                    mBinding.clDynamics.setState(
                        DynamicStateLayout.States.NETWORK_ANOMALY(),
                        arch.cayenne.lib.common.R.string.error_net.getString()
                    )
                    setHeight()
                }

                else -> {
                }
            }
        }

        mViewModel.totalCountLiveData.observe(viewLifecycleOwner) {
            it?.let { count ->
                parentViewModel.setGameFavouriteCount(count)
            }
        }

        // 監聽收藏變化，若有變化則重新加載數據
        mViewModel.favouriteChangedLiveData.observe(viewLifecycleOwner) { isChanged ->
            launch(Lifecycle.State.RESUMED) {
                if (isChanged) {
                    mViewModel.reload()
                }
            }
        }

        mViewModel.reload()
    }

    fun setHeight() {
        launch(Lifecycle.State.RESUMED) {
            if (mBinding.clDynamics.visibility == View.VISIBLE)
                parentViewModel.setOnHeight(mBinding.clDynamics.height)
            else
                if (mBinding.rvRecently.height == 0)
                    parentViewModel.setOnHeight(mBinding.clDynamics.height)
                else
                    parentViewModel.setOnHeight(mBinding.rvRecently.height)
        }
    }

    override fun onResume() {
        setHeight()
        super.onResume()
    }
}