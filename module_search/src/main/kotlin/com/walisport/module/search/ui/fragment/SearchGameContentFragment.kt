package com.walisport.module.search.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.adapter.GridSpacingItemDecoration
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.ui.view.SimpleTabDataModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import arch.cayenne.lib.common.utils.helper.BackToTopHelper
import arch.cayenne.lib.database.entity.GameSupplierDataModel
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.search.R
import arch.cayenne.lib.common.data.constants.GameSortType
import com.walisport.module.search.databinding.FragmentSearchGameContentBinding
import com.walisport.module.search.databinding.LayoutGameSortingMenuBinding
import com.walisport.module.search.ui.adapter.SearchGameCardAdapter
import com.walisport.module.search.ui.controller.SearchGameTabController
import com.walisport.module.search.ui.viewmodel.SearchGameContentViewModel
import kotlinx.coroutines.delay
import kotlin.reflect.KClass
import arch.cayenne.lib.common.R as RC

/**
 * Search 模組專用的 GameContentFragment，完全複製 hall 模組的 GameContentFragment 邏輯和 UI。
 * 
 * 用途：當用戶輸入匹配關鍵字（例如「電子」）時，顯示遊戲分類關聯頁面。
 * 與 GameContentFragment 功能完全一致，包括 Tab 切換、排序選單、遊戲卡片列表等。
 * 
 * 目前使用 mock 資料，未來可切換為真實後端 API。
 */
class SearchGameContentFragment : BaseFragment<SearchGameContentViewModel, FragmentSearchGameContentBinding>() {
    companion object {
        private const val ARG_GAME_TYPE_ID = "arg_game_type_id"
        private const val ARG_KEYWORD = "arg_keyword"

        /**
         * @param gameTypeId 遊戲分類 ID（例如：電子 = 4）
         * @param keyword 搜索關鍵字（例如：「電子」）
         */
        fun newInstance(
            gameTypeId: Int,
            keyword: String? = null
        ) = SearchGameContentFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_GAME_TYPE_ID, gameTypeId)
                keyword?.let { putString(ARG_KEYWORD, it) }
            }
        }
    }

    override val vbClass: KClass<FragmentSearchGameContentBinding> = FragmentSearchGameContentBinding::class
    override val vmClass: KClass<SearchGameContentViewModel> = SearchGameContentViewModel::class

    private lateinit var adapter: SearchGameCardAdapter
    private var isExpanded = false
    private var sortingMenuBinding: LayoutGameSortingMenuBinding? = null
    private var currentSortType = GameSortType.HOT
    private var sortMenuClicked: Boolean = false
    private val defaultAnimDuration = 210L
    private var helper: BackToTopHelper? = null

    private val gameTypeId: Int
        get() = requireArguments().getInt(ARG_GAME_TYPE_ID, 4)

    private val keyword: String?
        get() = requireArguments().getString(ARG_KEYWORD)

    private val itemDecoration by lazy {
        GridSpacingItemDecoration(
            spanCount = 3,
            horizontalSpacing = 9.dp2px,
            verticalSpacing = 13.dp2px,
            includeEdge = false
        )
    }

    private lateinit var gameTabController: SearchGameTabController

    /**
     * 將供應商列表轉換成 Tab 列表（與 GameContentFragment 一致）
     */
    fun supplierTabList(list: List<GameSupplierDataModel>): List<SimpleTabDataModel> {
        val l = ArrayList<SimpleTabDataModel>()
        l.add(
            SimpleTabDataModel(
                id = 0,
                simpleName = "",
                icon = "",
            )
        )
        list.take(10).forEach { item ->
            l.add(
                SimpleTabDataModel(
                    id = item.id,
                    simpleName = item.name,
                    icon = item.icon,
                )
            )
        }
        return l
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            // 初始化 Tab Controller
            gameTabController = SearchGameTabController(
                tabGroup = customTabGroup,
                rewardTipsView = tvRewardTips,
                gridItemDecoration = itemDecoration,
                initialSortType = currentSortType,
                onSupplierChanged = { supplierId ->
                    // 點擊全部 取消全部选中
                    if (supplierId == null) {
                        mViewModel.clearSupplierSelected()
                    } else {
                        mViewModel.selectSupplierId(supplierId)
                    }
                    rvGame.startFadeAnim { onComplete ->
                        mViewModel.setSupplier(if (supplierId == null) emptyList() else listOf(supplierId))
                        toggleGameSorting(false)
                        helper?.reset()
                        mViewModel.reload()
                        onComplete.invoke()
                    }
                },
                onSortTypeChanged = { sortType ->
                    currentSortType = sortType
                    mViewModel.setSortType(sortType)
                    helper?.reset()
                    mViewModel.reload()
                },
                onShowAllSupplierClick = {
                    // TODO: 之後可打開供應商 BottomSheet
                }
            )
            gameTabController.attach()

            rvGame.layoutManager = GridLayoutManager(requireContext(), 3)
            rvGame.addItemDecoration(itemDecoration)
            adapter = SearchGameCardAdapter(onItemClick = {
                // TODO: 點擊遊戲卡片，跳轉到遊戲詳情
                // navigate(arch.cayenne.lib.res.R.string.nav_module_gamedetail.deeplink())
            })
            rvGame.adapter = adapter
            rvGame.itemAnimator = null

            helper = BackToTopHelper(rvGame, ivBackToTop, true) {
                // 點擊回到頂部按鈕的額外操作
                //如果排序方式是热返和冷返
                if (currentSortType == GameSortType.HOT_REWARD || currentSortType == GameSortType.COLD_REWARD) {
                    aplHomeBanner.setExpanded(true, false)
                }
            }
        }
    }

    override fun initListener() {
        mBinding.customTabGroup.setOnSortBtnClick {
            toggleGameSorting(!isExpanded)
        }
        mBinding.customTabGroup.setOnShowAllCategoryClick({}, {
            // 点击更多供应商按钮时，需要判断排序菜单是否展开，若展开则先收起
            if (isExpanded) {
                toggleGameSorting(false)
            }
            // TODO: 顯示供應商 BottomSheet
        })
        
        // 加載更多監聽（與 GameContentFragment 一致）
        mBinding.rvGame.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            private var isLoading = false
            override fun onScrolled(rv: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val layoutManager = rv.layoutManager as? GridLayoutManager ?: return
                val totalItemCount = rv.adapter?.itemCount ?: 0
                if (totalItemCount == 0) return
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (!isLoading && (totalItemCount - lastVisibleItemPosition) <= 6) {
                    isLoading = true
                    rv.post {
                        if (mViewModel.apiStateListener.value == DataState.LoadSuccess) {
                            mViewModel.loadNextPage()
                        }
                        isLoading = false
                    }
                }
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
                        RC.string.data_empty.getString()
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
                        RC.string.error_net.getString()
                    )
                }

                else -> {
                }
            }
        }

        // 拿到供应商列表
        mViewModel.gameSupplierList.observe(viewLifecycleOwner) {
            gameTabController.submitTabs(supplierTabList(it))
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.setGameTypeId(gameTypeId)
        mViewModel.setSupplier(emptyList())
        mViewModel.setSortType(currentSortType)
        mViewModel.getSuppliers(gameTypeId)
    }

    /**
     * 排序選單的展開收起切換（與 GameContentFragment 一致）
     */
    private fun toggleGameSorting(expanded: Boolean) {
        isExpanded = expanded
        val container = mBinding.llGameDropdown

        if (expanded) {
            container.visibility = View.VISIBLE

            if (sortingMenuBinding == null) {
                sortingMenuBinding = LayoutGameSortingMenuBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    container,
                    false
                )
                container.addView(sortingMenuBinding?.root)
                setupSortingMenuViews()
            }

            mBinding.vGameListMask.apply {
                visibility = View.VISIBLE
                alpha = 1f
                clickNoRepeat {
                    toggleGameSorting(false)
                }
            }

            val slideInAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_from_top)
            sortingMenuBinding?.root?.startAnimation(slideInAnim)
            slideInAnim.duration = AnimationController[AnimType.popupEnter]?.duration ?: defaultAnimDuration
            slideInAnim.interpolator = AnimationController[AnimType.popupEnter]?.interpolator?.toInterpolator()
                ?: LinearInterpolator()

            mBinding.customTabGroup.setSortBtnSrc(RC.drawable.ic_sort_collapse)
            mBinding.customTabGroup.setSortBtnTextColor(
                SkinnableResourceManager.getColor(
                    requireContext(),
                    RC.color.color_00E0E5
                )
            )

        } else {
            val slideOutAnim = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.slide_out_to_top
            )
            slideOutAnim.duration = AnimationController[AnimType.popupExit]?.duration ?: defaultAnimDuration
            slideOutAnim.interpolator = AnimationController[AnimType.popupExit]?.interpolator?.toInterpolator()
                ?: LinearInterpolator()

            slideOutAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    container.visibility = View.GONE
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
            sortingMenuBinding?.root?.startAnimation(slideOutAnim)

            mBinding.vGameListMask.animate()
                .alpha(0f)
                .setDuration(AnimationController[AnimType.popupExit]?.duration ?: defaultAnimDuration)
                .setListener(object : android.animation.Animator.AnimatorListener {
                    override fun onAnimationStart(p0: android.animation.Animator) {}
                    override fun onAnimationEnd(p0: android.animation.Animator) {
                        mBinding.vGameListMask.visibility = View.GONE
                    }
                    override fun onAnimationCancel(p0: android.animation.Animator) {}
                    override fun onAnimationRepeat(p0: android.animation.Animator) {}
                })
                .start()

            if (!sortMenuClicked) {
                mBinding.customTabGroup.setSortBtnSrc(RC.drawable.ic_sort_expand)
                mBinding.customTabGroup.setSortBtnTextColor(
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        RC.color.color_C0C0C0
                    )
                )
            } else {
                mBinding.customTabGroup.setSortBtnSrc(RC.drawable.ic_sort_expand_blue)
                mBinding.customTabGroup.setSortBtnTextColor(
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        RC.color.color_00E0E5
                    )
                )
            }
        }
    }

    private fun setupSortingMenuViews() {
        sortingMenuBinding?.let { binding ->
            updateSortingMenuSelection()

            binding.tvSortByHot.clickNoRepeat {
                sortMenuClicked = true
                if (currentSortType != GameSortType.HOT) {
                    currentSortType = GameSortType.HOT
                    itemDecoration.verticalSpacing = 12.dp2px
                    mBinding.tvRewardTips.visibility = View.GONE
                    updateSortingMenuSelection()
                    setSortBtnText()
                    gameTabController.applySortType(currentSortType, notify = true)
                    helper?.reset()
                    mViewModel.reload()
                }
                toggleGameSorting(false)
            }

            binding.tvSortByNew.clickNoRepeat {
                sortMenuClicked = true
                if (currentSortType != GameSortType.NEW) {
                    itemDecoration.verticalSpacing = 12.dp2px
                    currentSortType = GameSortType.NEW
                    mBinding.tvRewardTips.visibility = View.GONE
                    updateSortingMenuSelection()
                    setSortBtnText()
                    gameTabController.applySortType(currentSortType, notify = true)
                    helper?.reset()
                    mViewModel.reload()
                }
                toggleGameSorting(false)
            }

            binding.tvSortByHotReward.clickNoRepeat {
                sortMenuClicked = true
                if (currentSortType != GameSortType.HOT_REWARD) {
                    currentSortType = GameSortType.HOT_REWARD
                    itemDecoration.verticalSpacing = 14.dp2px
                    mBinding.tvRewardTips.visibility = View.VISIBLE
                    mBinding.aplHomeBanner.setExpanded(true, true)
                    updateSortingMenuSelection()
                    setSortBtnText()
                    gameTabController.applySortType(currentSortType, notify = true)
                    helper?.reset()
                    mViewModel.reload()
                }
                toggleGameSorting(false)
            }

            binding.tvSortByColdReward.clickNoRepeat {
                sortMenuClicked = true
                if (currentSortType != GameSortType.COLD_REWARD) {
                    currentSortType = GameSortType.COLD_REWARD
                    itemDecoration.verticalSpacing = 14.dp2px
                    mBinding.tvRewardTips.visibility = View.VISIBLE
                    mBinding.aplHomeBanner.setExpanded(true, true)
                    updateSortingMenuSelection()
                    setSortBtnText()
                    gameTabController.applySortType(currentSortType, notify = true)
                    helper?.reset()
                    mViewModel.reload()
                }
                toggleGameSorting(false)
            }
        }
    }

    private fun updateSortingMenuSelection() {
        sortingMenuBinding?.let { binding ->
            val selectedColor = SkinnableResourceManager.getColor(
                requireContext(),
                RC.color.color_00E0E5
            )
            val unselectedColor = SkinnableResourceManager.getColor(
                requireContext(),
                RC.color.color_999999
            )

            when (currentSortType) {
                GameSortType.HOT -> {
                    binding.tvSortByHot.setTextColor(selectedColor)
                    binding.tvSortByNew.setTextColor(unselectedColor)
                    binding.tvSortByHotReward.setTextColor(unselectedColor)
                    binding.tvSortByColdReward.setTextColor(unselectedColor)
                }
                GameSortType.NEW -> {
                    binding.tvSortByHot.setTextColor(unselectedColor)
                    binding.tvSortByNew.setTextColor(selectedColor)
                    binding.tvSortByHotReward.setTextColor(unselectedColor)
                    binding.tvSortByColdReward.setTextColor(unselectedColor)
                }
                GameSortType.HOT_REWARD -> {
                    binding.tvSortByHot.setTextColor(unselectedColor)
                    binding.tvSortByNew.setTextColor(unselectedColor)
                    binding.tvSortByHotReward.setTextColor(selectedColor)
                    binding.tvSortByColdReward.setTextColor(unselectedColor)
                }
                GameSortType.COLD_REWARD -> {
                    binding.tvSortByHot.setTextColor(unselectedColor)
                    binding.tvSortByNew.setTextColor(unselectedColor)
                    binding.tvSortByHotReward.setTextColor(unselectedColor)
                    binding.tvSortByColdReward.setTextColor(selectedColor)
                }
            }
        }
    }

    private fun setSortBtnText() {
        gameTabController.applySortType(currentSortType, notify = false)
    }

    override fun onDestroyView() {
        gameTabController.detach()
        helper = null
        super.onDestroyView()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden && isExpanded) {
            toggleGameSorting(false)
        }
    }

    override fun onPause() {
        super.onPause()
        if (isExpanded) {
            toggleGameSorting(false)
        }
    }
}

