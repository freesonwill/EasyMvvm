package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.adapter.GridSpacingItemDecoration
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.checkCurrentScrollState
import arch.cayenne.lib.common.utils.ext.onScrolledOver
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.BackToTopHelper
import com.walisport.module.business.common.data.UniversalLoadMoreScrollListener
import com.walisport.module.business.common.ui.adapter.GameContentAdapter
import com.walisport.module.hall.R
import com.walisport.module.hall.databinding.FragmentGameRecentBinding
import com.walisport.module.hall.ui.viewmodel.GameRecentViewModel
import com.walisport.module.hall.ui.viewmodel.HallViewModel
import com.walisport.module.live.data.EventClick
import kotlinx.coroutines.delay
import kotlin.reflect.KClass

class GameRecentFragment : BaseFragment<GameRecentViewModel, FragmentGameRecentBinding>() {

    companion object {
        private const val ARG_CATEGORY_TYPE = "arg_category_type"
        fun newInstance(
            categoryType: Int,
        ) = GameRecentFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_CATEGORY_TYPE, categoryType)

            }
        }
    }

    override val vbClass: KClass<FragmentGameRecentBinding> = FragmentGameRecentBinding::class
    override val vmClass: KClass<GameRecentViewModel> = GameRecentViewModel::class
    private val hallViewModel: HallViewModel by sharedViewModel<HallViewModel, HallFragment>()
    private lateinit var adapter: GameContentAdapter
    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            rvGame.layoutManager = GridLayoutManager(requireContext(), 3)
            val itemDecoration = GridSpacingItemDecoration(
                spanCount = 3,
                horizontalSpacing = 9.dp2px,
                verticalSpacing = 12.dp2px,
                includeEdge = false // 確保邊緣沒有空隙
            )
            rvGame.addItemDecoration(itemDecoration)
            adapter = GameContentAdapter(onItemClick = {
                mViewModel.setIsClickGame(EventClick.EVENT_CLICK_ACK_TRUE.type)
                navigate(arch.cayenne.lib.res.R.string.nav_module_gamedetail.deeplink("gameId" to it.id))
                launch{
                    delay(AnimationController[AnimType.popupExit]!!.duration)
                    adapter.submitList(emptyList())
                }

            })
            rvGame.adapter = adapter
            BackToTopHelper(rvGame, ivBackToTop, true)
        }
    }

    override fun initData() {
        super.initData()
        arguments?.apply {
            mViewModel.setCategory(this.getInt(ARG_CATEGORY_TYPE))
        }
        mViewModel.reload()
    }

    override fun initListener() {
        mBinding.rvGame.onScrolledOver(100f, 80f, {
            hallViewModel.setScorll(true)
        }, {
            hallViewModel.setScorll(false)
        })
        mBinding.rvGame.addOnScrollListener(UniversalLoadMoreScrollListener(6) {
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
                    mBinding.rvGame.visibility = View.VISIBLE
                    mBinding.clDynamics.visibility = View.GONE
                }

                DataState.DataEmpty -> {
                    mBinding.rvGame.visibility = View.GONE
                    mBinding.clDynamics.visibility = View.VISIBLE
                    mBinding.clDynamics.setState(
                        States.DATA_EMPTY,
                        R.string.game_data_empty.getString()
                    )

                }

                DataState.NoMoreData -> {
                    mBinding.rvGame.visibility = View.VISIBLE
                    mBinding.clDynamics.visibility = View.GONE
                }

                DataState.NetworkUnavailable -> {
                    mBinding.rvGame.visibility = View.GONE
                    mBinding.clDynamics.visibility = View.VISIBLE
                    mBinding.clDynamics.setState(
                        States.NETWORK_ANOMALY(),
                        arch.cayenne.lib.common.R.string.error_net.getString()
                    )
                }

                else -> {
                }
            }
        }
    }

    override fun onStart() {
        mViewModel.getIsClickGame()
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        mBinding.rvGame.post {
            mBinding.rvGame.checkCurrentScrollState(100f, 80f, {
                hallViewModel.setScorll(true)
            }, {
                hallViewModel.setScorll(false)
            })
        }
    }
}