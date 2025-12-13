package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.adapter.GridSpacingItemDecoration
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.ui.view.SimpleTabDataModel
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.BackToTopHelper
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.ui.fragment.GameContentListBottomSheetFragment
import com.walisport.module.hall.R
import com.walisport.module.hall.data.UniversalLoadMoreScrollListener
import com.walisport.module.hall.data.constants.GameSortType
import com.walisport.module.hall.data.getCategoryByType
import com.walisport.module.hall.databinding.FragmentHallCategoryBinding
import com.walisport.module.hall.databinding.LayoutGameSortingMenuBinding
import com.walisport.module.hall.databinding.TitleBarGameCategoryBinding
import com.walisport.module.hall.ui.adapter.GameContentAdapter
import com.walisport.module.hall.ui.viewmodel.GameCategoryViewModel
import kotlin.reflect.KClass

class HallCategoryFragment : BaseFragment<GameCategoryViewModel , FragmentHallCategoryBinding>() {
    override val vbClass: KClass<FragmentHallCategoryBinding> = FragmentHallCategoryBinding::class
    override val vmClass: KClass<GameCategoryViewModel> = GameCategoryViewModel::class
    private val titleBarBinding: TitleBarGameCategoryBinding by lazy {
        TitleBarGameCategoryBinding.inflate(
            LayoutInflater.from(context) ,
            mBinding.titleBar ,
            false
        )
    }
    private lateinit var adapter: GameContentAdapter

    private var isExpanded = false
    private var sortingMenuBinding: LayoutGameSortingMenuBinding? = null
    private var sortMenuClicked: Boolean = false


    // 當前排序類型，預設為按熱門聯賽排序
    private var currentSortType = GameSortType.HOT

    private val defaultAnimDuration = 300L

    private var category: Int = 100

    private val mockVendorList by lazy {
        val l = ArrayList<SimpleTabDataModel>()
        for (i in 0..5) {
            l.add(
                SimpleTabDataModel(
                    id = i ,
                    simpleName = getString(R.string.wali) ,
                    icon = "" ,
                )
            )
        }
        l
    }


    override fun initView(savedInstanceState: Bundle?) {
        val requireArguments = requireArguments()
         category = requireArguments.getString("category")?.toInt()?:100

        with(mBinding) {
            titleBar.loadDynamicsTitleBar(titleBarBinding.root , null)
            titleBarBinding.tvTitleName.text = 100.getCategoryByType().desc

            customTabGroup.submitTabList(mockVendorList)

            customTabGroup.setTabClickListener(object :
                arch.cayenne.lib.common.ui.view.CustomGameTabClickListener {
                override fun onTabClicked(id: Int) {
                    mViewModel.setSuppliers(if (id == 0) emptyList() else listOf(id))
                    mViewModel.reload()
                }
            })
            rvGame.itemAnimator = null
            rvGame.layoutManager = GridLayoutManager(requireContext() , 3)
            val itemDecoration = GridSpacingItemDecoration(
                spanCount = 3 ,
                horizontalSpacing = 9.dp2px ,
                verticalSpacing = 18.dp2px ,
                includeEdge = false // 確保邊緣沒有空隙
            )
            rvGame.addItemDecoration(itemDecoration)
            adapter = GameContentAdapter(onItemClick = {
                navigate(arch.cayenne.lib.res.R.string.nav_module_gamedetail.deeplink())
            })
            rvGame.adapter = adapter

            BackToTopHelper(rvGame , ivBackToTop, true)



        }

        mBinding.root.touchBackPressed()
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



        mBinding.customTabGroup.setOnSortBtnClick {
            toggleGameSorting(!isExpanded)
        }

        mBinding.customTabGroup.setOnShowAllCategoryClick({} , {
            showTournamentListBottomSheet()
        })
    }


    override suspend fun createObserver() {

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
                        States.DATA_EMPTY ,
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
                        States.NETWORK_ANOMALY() ,
                        arch.cayenne.lib.common.R.string.error_net.getString()
                    )
                }

                else -> {
                }
            }
        }
        mViewModel.gameListLiveData.observe(viewLifecycleOwner) {
            it.let { list ->
                adapter.submitList(list)

                // 自動加載下一頁數據（如果當前數據量較少）
                if (list.size <= 10) {
                    mViewModel.loadNextPage()
                }
            }
        }

    }

    override fun initData() {
        super.initData()

        mViewModel.setCategory(category ?: 0)
        mViewModel.setSuppliers(emptyList())
        mViewModel.setSortType(currentSortType)
        mViewModel.queryGameList()
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
                    LayoutInflater.from(requireContext()) ,
                    container ,
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
            val slideInAnim =
                AnimationUtils.loadAnimation(requireContext() , R.anim.slide_in_from_top)
            sortingMenuBinding?.root?.startAnimation(slideInAnim)

            // 切換圖標為收起狀態
            mBinding.customTabGroup.setSortBtnSrc(arch.cayenne.lib.common.R.drawable.ic_sort_collapse)

            //  變色為選中狀態
            mBinding.customTabGroup.setSortBtnTextColor(
                SkinnableResourceManager.getColor(
                    requireContext() ,
                    arch.cayenne.lib.common.R.color.color_00E0E5
                )
            )

        } else {
            // 收起排序選單 - 使用動畫
            val slideOutAnim = AnimationUtils.loadAnimation(
                requireContext() ,
                R.anim.slide_out_to_top
            )
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
                .setDuration(defaultAnimDuration)
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
                        requireContext() ,
                        arch.cayenne.lib.common.R.color.color_C0C0C0
                    )
                )
            } else {
                mBinding.customTabGroup.setSortBtnSrc(arch.cayenne.lib.common.R.drawable.ic_sort_expand_blue)
                mBinding.customTabGroup.setSortBtnTextColor(
                    SkinnableResourceManager.getColor(
                        requireContext() ,
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
                    mBinding.tvRewardTips.visibility = View.GONE
                    updateSortingMenuSelection()
                    setSortBtnText()
                    mViewModel.setSortType(currentSortType)
                    mViewModel.reload()
                }

                toggleGameSorting(false)
            }

            // 點擊按最新排序
            binding.tvSortByNew.clickNoRepeat {
                sortMenuClicked = true
                if (currentSortType != GameSortType.NEW) {
                    currentSortType = GameSortType.NEW
                    mBinding.tvRewardTips.visibility = View.GONE
                    updateSortingMenuSelection()
                    setSortBtnText()
                    mViewModel.setSortType(currentSortType)
                    mViewModel.reload()
                }

                toggleGameSorting(false)

            }

            binding.tvSortByHotReward.clickNoRepeat {
                sortMenuClicked = true

                if (currentSortType != GameSortType.HOT_REWARD) {
                    currentSortType = GameSortType.HOT_REWARD
                    mBinding.tvRewardTips.visibility = View.VISIBLE
                    mBinding.aplHomeBanner.setExpanded(true, true)
                    updateSortingMenuSelection()
                    setSortBtnText()
                    mViewModel.setSortType(currentSortType)
                    mViewModel.reload()
                }

                toggleGameSorting(false)
            }

            binding.tvSortByColdReward.clickNoRepeat {
                sortMenuClicked = true

                if (currentSortType != GameSortType.COLD_REWARD) {
                    currentSortType = GameSortType.COLD_REWARD
                    mBinding.tvRewardTips.visibility = View.VISIBLE
                    mBinding.aplHomeBanner.setExpanded(true, true)
                    updateSortingMenuSelection()
                    setSortBtnText()
                    mViewModel.setSortType(currentSortType)
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
                requireContext() ,
                arch.cayenne.lib.common.R.color.color_00E0E5
            )
            val unselectedColor = SkinnableResourceManager.getColor(
                requireContext() ,
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

    private fun showTournamentListBottomSheet() {

    }


}