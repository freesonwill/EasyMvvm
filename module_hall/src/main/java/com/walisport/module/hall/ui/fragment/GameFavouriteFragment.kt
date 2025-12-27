package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.databinding.TitleBarSimpleBinding
import arch.cayenne.lib.common.ui.adapter.GridSpacingItemDecoration
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.BackToTopHelper
import com.walisport.module.business.common.data.UniversalLoadMoreScrollListener
import com.walisport.module.business.common.ui.adapter.GameContentAdapter
import com.walisport.module.hall.R
import com.walisport.module.hall.databinding.FragmentGameFavouriteBinding
import com.walisport.module.hall.ui.viewmodel.GameFavouriteViewModel
import kotlinx.coroutines.delay
import kotlin.reflect.KClass

class GameFavouriteFragment : BaseFragment<GameFavouriteViewModel, FragmentGameFavouriteBinding>() {
    override val vbClass: KClass<FragmentGameFavouriteBinding> = FragmentGameFavouriteBinding::class
    override val vmClass: KClass<GameFavouriteViewModel> = GameFavouriteViewModel::class

    private lateinit var adapter: GameContentAdapter

    private val titleBarBinding: TitleBarSimpleBinding by lazy {
        TitleBarSimpleBinding.inflate(
            LayoutInflater.from(context),
            mBinding.titleBar,
            false
        )
    }

    private val itemDecoration by lazy {
        GridSpacingItemDecoration(
            spanCount = 3,
            horizontalSpacing = 9.dp2px,
            verticalSpacing = 12.dp2px,
            includeEdge = false // 確保邊緣沒有空隙
        )
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            titleBar.loadDynamicsTitleBar(titleBarBinding.root, null)
            titleBarBinding.tvTitleName.text = getString(R.string.title_game_favourite)
            mBinding.root.touchBackPressed()
            rvGame.layoutManager = GridLayoutManager(requireContext(), 3)
            rvGame.addItemDecoration(itemDecoration)
            adapter = GameContentAdapter(onItemClick = {
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

    override fun initListener() {
        with(titleBarBinding) {
            ivBack.addScaleOnTouchAnimation()
            ivBack.clickNoRepeat {
                findNavController().navigateUp()
            }
        }
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
                        arch.cayenne.lib.common.R.string.data_empty.getString()
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

    override fun initData() {
        mViewModel.reload()
        super.initData()
    }

    override fun onStart() {
        super.onStart()
    }
}
