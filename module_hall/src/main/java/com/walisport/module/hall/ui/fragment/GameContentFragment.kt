package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.ui.adapter.GridSpacingItemDecoration
import arch.cayenne.lib.common.ui.view.SimpleTabDataModel
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.onScrolledOver
import arch.cayenne.lib.common.utils.ext.checkCurrentScrollState
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.scrollToBottomWithLoadMore
import arch.cayenne.lib.common.utils.helper.BackToTopHelper
import arch.cayenne.module.home.ui.fragment.GameContentListBottomSheetFragment
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.data.HotColdType
import com.walisport.module.hall.data.UniversalLoadMoreScrollListener
import com.walisport.module.hall.databinding.FragmentGameContentBinding
import com.walisport.module.hall.ui.adapter.GameContentAdapter
import com.walisport.module.hall.ui.viewmodel.GameContentViewModel
import com.walisport.module.hall.ui.viewmodel.GameRecentViewModel
import com.walisport.module.hall.ui.viewmodel.HallViewModel
import kotlin.random.Random
import kotlin.reflect.KClass

class GameContentFragment : BaseFragment<GameContentViewModel, FragmentGameContentBinding>() {
    companion object {
        fun newInstance() = GameContentFragment()
    }
    override val vbClass: KClass<FragmentGameContentBinding> = FragmentGameContentBinding::class
    override val vmClass: KClass<GameContentViewModel> = GameContentViewModel::class
    private val hallViewModel: HallViewModel by sharedViewModel<HallViewModel, HallFragment>()
    private lateinit var adapter: GameContentAdapter
    private var list : MutableList<GameContentData> = mutableListOf()
    private var page : Long = 0
    private val mockVendorList by lazy {
        val l = ArrayList<SimpleTabDataModel>()
        for (i in 0..5) {
            l.add(
                SimpleTabDataModel(
                    id = i,
                    simpleName = getString(R.string.wali),
                    icon = "",
                )
            )
        }
        l
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            rvGame.layoutManager = GridLayoutManager(requireContext(),  3)
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
            customTabGroup.submitTabList(mockVendorList)
            BackToTopHelper(rvGame, ivBackToTop)
        }
        mViewModel.mockList(page)
        mViewModel.getSuppliers(3)
    }

    override fun initListener() {
        mBinding.rvGame.onScrolledOver(100f, 80f, {
            hallViewModel.setScorll(true)
        }, {
            hallViewModel.setScorll(false)
        })
        mBinding.rvGame.addOnScrollListener(UniversalLoadMoreScrollListener(6) {
            page++
            mViewModel.mockList(page)
        })
        mBinding.customTabGroup.setOnShowAllCategoryClick({},{
            showTournamentListBottomSheet()
        })

    }

    private fun showTournamentListBottomSheet() {
        val tag = "tournament_bottom_sheet"
        if (childFragmentManager.findFragmentByTag(tag) != null) return

        GameContentListBottomSheetFragment
            .newInstance(3
            )
            .show(childFragmentManager, tag)
    }

    override suspend fun createObserver() {
        mViewModel.gameRecentList.observe(viewLifecycleOwner) {
            it.let {
                list.addAll(it)
                adapter.submitList(list)
            }
        }

        mViewModel.gameSupplierList.observe(viewLifecycleOwner){


        }

        //
        mViewModel.savedTournamentSelections.observe(viewLifecycleOwner){


        }

        mViewModel.buttonHasSelection.observe(viewLifecycleOwner){  hasSelection ->
            updateTournamentButtonStyle(hasSelection)
            if (!hasSelection) {//重新获取数据 在供应商列表没有选中情况下,选中全部
                mBinding.customTabGroup.select(0)
            }
        }

        // 清除 tlLeagueList
        mViewModel.shouldClearLeagueListSelection.observeEvent(viewLifecycleOwner, this) {
            clearLeagueListSelection()
        }
    }
    /**
     * 更新聯賽按鈕樣式
     * @param hasSelection true: 有選中的聯賽，false: 沒有選中的聯賽
     */
    private fun updateTournamentButtonStyle(hasSelection: Boolean) {
        mBinding.customTabGroup.updateTournamentButtonStyle(hasSelection)
    }
    /**
     * 清除 tlLeagueList 的選中狀態（需求2）
     */
    private fun clearLeagueListSelection() {
        with(mBinding) {
            // 清除所有 tab 的選中狀態
            customTabGroup.clearLeagueListSelection()
        }
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