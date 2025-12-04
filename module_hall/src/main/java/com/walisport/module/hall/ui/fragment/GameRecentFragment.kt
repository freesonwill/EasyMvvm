package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.ui.adapter.GridSpacingItemDecoration
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.checkCurrentScrollState
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.helper.BackToTopHelper
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.databinding.FragmentGameRecentBinding
import com.walisport.module.hall.ui.adapter.GameContentAdapter
import com.walisport.module.hall.ui.viewmodel.HallViewModel
import kotlin.reflect.KClass
import arch.cayenne.lib.common.utils.ext.onScrolledOver
import com.walisport.module.hall.data.UniversalLoadMoreScrollListener
import com.walisport.module.hall.ui.viewmodel.GameRecentViewModel

class GameRecentFragment : BaseFragment<GameRecentViewModel, FragmentGameRecentBinding>() {
    companion object {
        fun newInstance() = GameRecentFragment()
    }

    override val vbClass: KClass<FragmentGameRecentBinding> = FragmentGameRecentBinding::class
    override val vmClass: KClass<GameRecentViewModel> = GameRecentViewModel::class
    private val hallViewModel: HallViewModel by sharedViewModel<HallViewModel, HallFragment>()
    private lateinit var adapter: GameContentAdapter
    private var list: MutableList<GameContentData> = mutableListOf()
    private var page: Long = 0
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
                navigate(arch.cayenne.lib.res.R.string.nav_module_gamedetail.deeplink())
            })
            rvGame.adapter = adapter
            BackToTopHelper(rvGame, ivBackToTop)
        }
        mViewModel.mockList(page)
    }

    override fun initListener() {
        mBinding.rvGame.onScrolledOver(100f, 80f, {
            hallViewModel.setScorll(true)
        }, {
            hallViewModel.setScorll(false)
        })
    }

    override suspend fun createObserver() {
        mViewModel.gameRecentList.observe(viewLifecycleOwner) {
            LogUtils.e("gameRecentList--------------->${it}")
            it.let {
                list.addAll(it)
                adapter.submitList(list)
            }
        }

        mBinding.rvGame.addOnScrollListener(UniversalLoadMoreScrollListener(6) {
            page++
            mViewModel.mockList(page)
        })

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