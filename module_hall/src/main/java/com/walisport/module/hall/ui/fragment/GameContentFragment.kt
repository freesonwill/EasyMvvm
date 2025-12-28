package com.walisport.module.hall.ui.fragment

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
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.checkCurrentScrollState
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.onScrolledOver
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import arch.cayenne.lib.common.utils.helper.BackToTopHelper
import arch.cayenne.lib.database.entity.GameSupplierDataModel
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.business.common.data.UniversalLoadMoreScrollListener
import com.walisport.module.business.common.data.constants.GameSortType
import com.walisport.module.business.common.ui.adapter.GameContentAdapter
import com.walisport.module.hall.R
import com.walisport.module.hall.databinding.FragmentGameContentBinding
import com.walisport.module.hall.databinding.LayoutGameSortingMenuBinding
import com.walisport.module.hall.ui.viewmodel.GameContentViewModel
import com.walisport.module.hall.ui.viewmodel.HallViewModel
import com.walisport.module.live.data.EventClick
import kotlinx.coroutines.delay
import kotlin.reflect.KClass

class GameContentFragment : BaseFragment<GameContentViewModel, FragmentGameContentBinding>() {
    companion object {
        private const val ARG_CATEGORY_TYPE = "arg_category_type"

        fun newInstance(
            categoryType: Int,
        ) = GameContentFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_CATEGORY_TYPE, categoryType)

            }
        }
    }

    override val vbClass: KClass<FragmentGameContentBinding> = FragmentGameContentBinding::class
    override val vmClass: KClass<GameContentViewModel> = GameContentViewModel::class
    private val hallViewModel: HallViewModel by sharedViewModel<HallViewModel, HallFragment>()
    private lateinit var adapter: GameContentAdapter

    private var isExpanded = false
    private var sortingMenuBinding: LayoutGameSortingMenuBinding? = null

    // 當前排序類型，預設為按熱門聯賽排序
    private var currentSortType = GameSortType.HOT

    private var sortMenuClicked: Boolean = false

    private val defaultAnimDuration = 210L
    val category get() = requireArguments().getInt(ARG_CATEGORY_TYPE)

    private var helper: BackToTopHelper? = null

    private val itemDecoration by lazy {
        GridSpacingItemDecoration(
            spanCount = 3,
            horizontalSpacing = 9.dp2px,
            verticalSpacing = 13.dp2px,
            includeEdge = false // 確保邊緣沒有空隙
        )
    }

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

            customTabGroup.setTabClickListener(object :
                arch.cayenne.lib.common.ui.view.CustomGameTabClickListener {
                override fun onTabClicked(id: Int) {
                    //点击全部 取消全部选中
                    if (id == 0) {
                        mViewModel.clearSupplierSelected()
                    } else {
                        mViewModel.selectSupplierId(id)
                    }
                    mBinding.rvGame.startFadeAnim { onComplete ->
                        mViewModel.setSupplier(if (id == 0) emptyList() else listOf(id))
                        toggleGameSorting(false)
                        helper?.reset()
                        mViewModel.reload()
                        onComplete.invoke()
                    }
                }
            })

            rvGame.layoutManager = GridLayoutManager(requireContext(), 3)
            rvGame.addItemDecoration(itemDecoration)
            adapter = GameContentAdapter(onItemClick = {
                mViewModel.setIsClickGame(EventClick.EVENT_CLICK_ACK_TRUE.type)
                navigate(arch.cayenne.lib.res.R.string.nav_module_gamedetail.deeplink("gameId" to it.gameID))
            })
            rvGame.adapter = adapter
            rvGame.itemAnimator = null

            helper = BackToTopHelper(rvGame , ivBackToTop , true) {
                // 點擊回到頂部按鈕的額外操作
                //如果排序方式是热返和冷返
                if (currentSortType == GameSortType.HOT_REWARD || currentSortType == GameSortType.COLD_REWARD) {
                    aplHomeBanner.setExpanded(true , false)
                }
            }
        }

    }

    override fun initListener() {
        mBinding.rvGame.onScrolledOver(100f, 80f, {
            hallViewModel.setScorll(true)
        }, {
            hallViewModel.setScorll(false)
        })

        mBinding.rvGame.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // 这里处理滚动状态变化
                // newState: 0=IDLE, 1=DRAGGING, 2=SETTLING
                hallViewModel.setScrollState(newState)
            }
        })

        mBinding.rvGame.addOnScrollListener(UniversalLoadMoreScrollListener(6) {
            if (mViewModel.apiStateListener.value == DataState.LoadSuccess) {
                mViewModel.loadNextPage()
            }
        })
        mBinding.customTabGroup.setOnSortBtnClick {
            toggleGameSorting(!isExpanded)
        }
        mBinding.customTabGroup.setOnShowAllCategoryClick({}, {
            // 点击更多供应商按钮时，需要判断排序菜单是否展开，若展开则先收起
            if(isExpanded){
                toggleGameSorting(false)
            }
            showSupplierListBottomSheet()
        })

    }

    private fun showSupplierListBottomSheet() {
        val tag = "GameContentFragment_bottom_sheet"
        if (childFragmentManager.findFragmentByTag(tag) != null) return

        GameContentListBottomSheetFragment
            .newInstance(mViewModel.getCategory())
            .show(childFragmentManager, tag)
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


        //拿到供应商列表
        mViewModel.gameSupplierList.observe(viewLifecycleOwner) {
            mBinding.customTabGroup.submitTabList(supplierTabList(it))
        }

        //根据选中的供应商拉取数据
        mViewModel.savedTournamentSelections.observe(viewLifecycleOwner) {
            if (it.size==1){
                launch{
                    delay(200)
                    mBinding.customTabGroup.selectById(it[0])
                }
            }else{
                mViewModel.setSupplier(it)
                helper?.reset()
                mViewModel.reload()
            }
        }

        mViewModel.buttonHasSelection.observe(viewLifecycleOwner) { hasSelection ->
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

    override fun initData() {
        super.initData()
        arguments?.apply {
            mViewModel.setCategory(this.getInt(ARG_CATEGORY_TYPE))
        }
        mViewModel.setSupplier(emptyList())
        mViewModel.setSortType(currentSortType)
        mViewModel.getSuppliers(mViewModel.getCategory())
    }

    /**
     * 排序選單的展開收起切換
     * @param expanded : Boolean 展開、收起
     */
    private fun toggleGameSorting(expanded: Boolean) {
        isExpanded = expanded
        val container = mBinding.llGameDropdown

        if (expanded) {
            container.visibility = View.VISIBLE

            // 展開排序選單
            if (sortingMenuBinding == null) {
                sortingMenuBinding = LayoutGameSortingMenuBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    container,
                    false
                )
                container.addView(sortingMenuBinding?.root)
                setupSortingMenuViews()
            }

            // 先立即顯示遮罩層遮擋底下內容，避免閃爍
            mBinding.vGameListMask.apply {
                visibility = View.VISIBLE
                alpha = 1f
                // 設置點擊事件
                clickNoRepeat {
                    toggleGameSorting(false)
                }
            }

            // 立即開始動畫
            //TODO: 移除xml動畫，改用程式碼設置動畫屬性
            val slideInAnim =
                AnimationUtils.loadAnimation(requireContext() , R.anim.slide_in_from_top)
            sortingMenuBinding?.root?.startAnimation(slideInAnim)
            slideInAnim.duration =
                AnimationController[AnimType.popupEnter]?.duration ?: defaultAnimDuration
            slideInAnim.interpolator =
                AnimationController[AnimType.popupEnter]?.interpolator?.toInterpolator()
                    ?: LinearInterpolator()


            // 切換圖標為收起狀態
            mBinding.customTabGroup.setSortBtnSrc(arch.cayenne.lib.common.R.drawable.ic_sort_collapse)

            //  變色為選中狀態
            mBinding.customTabGroup.setSortBtnTextColor(
                SkinnableResourceManager.getColor(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.color_00E0E5
                )
            )

        } else {
            // 收起排序選單 - 使用動畫
            //TODO: 移除xml動畫，改用程式碼設置動畫屬性
            val slideOutAnim = AnimationUtils.loadAnimation(
                requireContext() ,
                R.anim.slide_out_to_top
            )
            slideOutAnim.duration =
                AnimationController[AnimType.popupExit]?.duration ?: defaultAnimDuration
            slideOutAnim.interpolator =
                AnimationController[AnimType.popupExit]?.interpolator?.toInterpolator()
                    ?: LinearInterpolator()

            slideOutAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    container.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            sortingMenuBinding?.root?.startAnimation(slideOutAnim)

            // 收回時隱藏遮罩層（帶動畫效果）
            mBinding.vGameListMask.animate()
                .alpha(0f)
                .setDuration(AnimationController[AnimType.popupExit]?.duration
                    ?: defaultAnimDuration)
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
                mBinding.customTabGroup.setSortBtnSrc(arch.cayenne.lib.common.R.drawable.ic_sort_expand)
                mBinding.customTabGroup.setSortBtnTextColor(
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        arch.cayenne.lib.common.R.color.color_C0C0C0
                    )
                )
            } else {
                mBinding.customTabGroup.setSortBtnSrc(arch.cayenne.lib.common.R.drawable.ic_sort_expand_blue)
                mBinding.customTabGroup.setSortBtnTextColor(
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        arch.cayenne.lib.common.R.color.color_00E0E5
                    )
                )
            }
        }
    }

    //直接关闭排序菜单， 不要动画
    private fun closeSortingMenu() {
        if (isExpanded) {
            isExpanded = false
            val container = mBinding.llGameDropdown
            container.visibility = View.GONE
            mBinding.vGameListMask.apply {
                visibility = View.GONE
                alpha = 0f
            }
            if (!sortMenuClicked) {
                mBinding.customTabGroup.setSortBtnSrc(arch.cayenne.lib.common.R.drawable.ic_sort_expand)
                mBinding.customTabGroup.setSortBtnTextColor(
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        arch.cayenne.lib.common.R.color.color_C0C0C0
                    )
                )
            } else {
                mBinding.customTabGroup.setSortBtnSrc(arch.cayenne.lib.common.R.drawable.ic_sort_expand_blue)
                mBinding.customTabGroup.setSortBtnTextColor(
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        arch.cayenne.lib.common.R.color.color_00E0E5
                    )
                )
            }
        }
    }

    /**
     * 設置排序選單視圖的點擊事件和初始狀態
     */
    private fun setupSortingMenuViews() {
        sortingMenuBinding?.let { binding ->
            // 設置初始選中狀態
            updateSortingMenuSelection()

            // 點擊按熱門排序
            binding.tvSortByHot.clickNoRepeat {
                sortMenuClicked = true
                if (currentSortType != GameSortType.HOT) {
                    currentSortType = GameSortType.HOT
                    itemDecoration.verticalSpacing = 12.dp2px
                    mBinding.tvRewardTips.visibility = View.GONE
                    updateSortingMenuSelection()
                    setSortBtnText()
                    mViewModel.setSortType(currentSortType)
                    helper?.reset()
                    mViewModel.reload()
                }

                toggleGameSorting(false)
            }

            // 點擊按最新排序
            binding.tvSortByNew.clickNoRepeat {
                sortMenuClicked = true
                if (currentSortType != GameSortType.NEW) {
                    itemDecoration.verticalSpacing = 12.dp2px
                    currentSortType = GameSortType.NEW
                    mBinding.tvRewardTips.visibility = View.GONE
                    updateSortingMenuSelection()
                    setSortBtnText()
                    mViewModel.setSortType(currentSortType)
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
                    mViewModel.setSortType(currentSortType)
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
                    mViewModel.setSortType(currentSortType)
                    helper?.reset()
                    mViewModel.reload()
                }

                toggleGameSorting(false)
            }
        }
    }

    /**
     * 更新排序選單的選中狀態
     */
    private fun updateSortingMenuSelection() {
        sortingMenuBinding?.let { binding ->
            val selectedColor = SkinnableResourceManager.getColor(
                requireContext(),
                arch.cayenne.lib.common.R.color.color_00E0E5
            )
            val unselectedColor = SkinnableResourceManager.getColor(
                requireContext(),
                arch.cayenne.lib.common.R.color.color_999999
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
        when (currentSortType) {
            GameSortType.HOT -> {
                mBinding.customTabGroup.setSortBtnText(arch.cayenne.lib.common.R.string.custom_tab_hot.getString())
            }

            GameSortType.NEW -> {
                mBinding.customTabGroup.setSortBtnText(arch.cayenne.lib.common.R.string.custom_tab_new.getString())
            }

            GameSortType.HOT_REWARD -> {
                mBinding.customTabGroup.setSortBtnText(arch.cayenne.lib.common.R.string.custom_tab_hot_reward.getString())

            }

            GameSortType.COLD_REWARD -> {
                mBinding.customTabGroup.setSortBtnText(arch.cayenne.lib.common.R.string.custom_tab_cold_reward.getString())
            }
        }
    }

    /**
     * 更新按鈕樣式
     * @param hasSelection true: 有選中的供应商，false: 沒有選中的供应商
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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            if (isExpanded) {
                closeSortingMenu()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isExpanded) {
            closeSortingMenu()
        }
    }


}