package com.walisport.module.search.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.data.constants.GameSortType
import arch.cayenne.lib.common.data.constants.GameSortType.*
import arch.cayenne.lib.common.ui.adapter.GridSpacingItemDecoration
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.view.SimpleTabDataModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.bumptech.glide.Glide
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchTypeEnum
import com.walisport.module.search.data.model.SearchResultBaseBean
import com.walisport.module.search.data.model.SearchResultPlayerBean
import com.walisport.module.search.data.model.SearchResultTeamBean
import com.walisport.module.search.data.model.SearchResultTournamentBean
import com.walisport.module.search.databinding.FragmentSearchResultDirectMatchBinding
import com.walisport.module.search.ui.adapter.SearchGameCardAdapter
import com.walisport.module.search.ui.adapter.SearchResultRaceAdapter
import com.walisport.module.search.ui.controller.SearchGameTabController
import com.walisport.module.search.ui.viewmodel.DirectPageMode
import com.walisport.module.search.ui.viewmodel.SearchResultDirectMatchViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.reflect.KClass
import arch.cayenne.lib.common.R as RC
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import kotlinx.coroutines.flow.filter

class SearchResultDirectMatchFragment :
    SearchBaseFragment<SearchResultDirectMatchViewModel, FragmentSearchResultDirectMatchBinding>() {
    override val vmClass: KClass<SearchResultDirectMatchViewModel>
        get() = SearchResultDirectMatchViewModel::class
    override val contentVbClass: KClass<FragmentSearchResultDirectMatchBinding>
        get() = FragmentSearchResultDirectMatchBinding::class

    private val args: SearchResultDirectMatchFragmentArgs by navArgs()

    private val dateHintStr: String
        get() = R.string.search_date_hint.toTranslatedStr()
    private val noDataStr: String
        get() = R.string.search_result_no_data.toTranslatedStr()

    private val linearAdapter by lazy {
        SearchResultRaceAdapter().apply {
            onBetClick = { match ->
                navigate("walisport://module_live/liveFragment?matchId=${match.matchId}&sportId=${match.basicInfo.sportId}".toUri())
                requireView().postDelayed({ updateStatusSearchBar(false) }, 200L)
            }
            onFavoriteClick = { match ->
                lifecycleScope.launch {
                    fun apiHandle(state: ApiResponseState) {
                        when(state) {
                            is ApiResponseState.Succeeded<*> -> {
                                this@apply.updateFavoriteStatus(
                                    match.matchId, !match.collect
                                )
                            }
                            is ApiResponseState.Failed -> {
                                state.error?.let { showToast(it.msg) }
                            }
                            else -> Unit
                        }
                    }
                    if (match.collect) {
                        mViewModel.removeCollect(match.matchId) { apiHandle(it) }
                    } else {
                        mViewModel.addCollect(match.matchId) { apiHandle(it) }
                    }
                }
            }
        }
    }

    private var datePicker: SearchDatePickerFragment? = null

    // 遊戲卡片列表（供應商 / 類型直配使用）
    private val vendorAdapter by lazy {
        SearchGameCardAdapter { game ->
            showToast(game.name) // TODO: 點擊遊戲卡片要做的事（開遊戲、跳詳情...）
        }
    }

    // ItemDecoration（用於遊戲卡片 Grid 布局）
    private val itemDecoration by lazy {
        GridSpacingItemDecoration(
            spanCount = 3,
            horizontalSpacing = 9.dp2px,
            verticalSpacing = 13.dp2px,
            includeEdge = false
        )
    }

    // ItemDecoration（用於體育模式 Linear 布局）
    private var sportsItemDecoration: ItemDecoration? = null

    // Search 模組專用：封裝 customTabGroup 相關行為，使其與 GameContentFragment 一致
    private lateinit var gameTabController: SearchGameTabController

    /**
     * 將供應商列表轉換成 Tab 列表（與 GameContentFragment 一致）
     */
    fun supplierTabList(list: List<arch.cayenne.lib.database.entity.GameSupplierDataModel>): List<SimpleTabDataModel> {
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
        super.initView(savedInstanceState)
        setRaceView()

        with(contentBinding) {
            clDate.clickNoRepeat {
                openDatePicker()
            }
            tvDate.text = dateHintStr

            // 初始化遊戲 Tab 控制器（供 DirectPageMode.VENDOR 使用）
            gameTabController = SearchGameTabController(
                tabGroup = customTabGroup,
                rewardTipsView = tvRewardTips,
                gridItemDecoration = itemDecoration,
                initialSortType = mViewModel.selectedSortType.value,
                onSupplierChanged = { supplierId ->
                    // id = 0 代表「全部」，其它為具體供應商
                    mViewModel.switchSortType(
                        sortType = mViewModel.selectedSortType.value,
                        supplierId = supplierId ?: mViewModel.supplierData.value?.id
                    )
                },
                onSortTypeChanged = { sortType ->
                    val supplierId = mViewModel.supplierData.value?.id
                    mViewModel.switchSortType(sortType, supplierId)
                },
                onShowAllSupplierClick = {
                    // TODO: 之後可打開供應商 BottomSheet，暫時不實作
                }
            )
            gameTabController.attach()
        }
        setTouchBackPressed(true)
    }


    private fun setTouchBackPressed(bool: Boolean){
        contentBinding.root.touchBackPressed(bool)
        contentBinding.recyclerView.touchBackPressed(bool)
    }
    override fun initData() {
        super.initData()
        doSearch()
        args.keyword?.let { keyword ->
            updateSearchText(keyword)
            mViewModel.setCurrentTitle(keyword)
        }
    }

    override suspend fun createObserver() {
        super.createObserver()
        with(mViewModel) {
            launch(Lifecycle.State.STARTED) {
                // 觀察頁面模式，決定顯示體育或供應商 UI
                launch {
                    pageMode.collect { mode ->
                        when (mode) {
                            DirectPageMode.VENDOR -> {
                                setupVendorMode()
                                // 如果沒有供應商數據（分類模式），立即設置標題
                                if (mViewModel.supplierData.value == null) {
                                    updateCategoryHeader()
                                }
                            }
                            DirectPageMode.SPORTS -> setupSportsMode()
                            null -> Unit
                        }
                    }
                }

                // 體育模式：直接使用 directData / combineResult / selectedDateFlow
                launch {
                    directData.collect { data ->
                        if (pageMode.value == DirectPageMode.SPORTS) {
                            data?.let { updateDirectInfo(it) } ?: run {
                                with(contentBinding) {
                                    tvTitle.text = currentTitle
                                    tvSubTitle.text = noDataStr
                                }
                                updateBackgroundColor()
                            }
                        }
                    }
                }

                launch {
                    apiStateListener.observe(viewLifecycleOwner) { state ->
                        when (pageMode.value) {
                            DirectPageMode.VENDOR -> switchVendorUI(state)
                            DirectPageMode.SPORTS -> switchUI(state)
                            null -> switchUI(state)
                        }
                    }
                }

                launch {
                    combineResult.collect { combineResult ->
                        if (pageMode.value == DirectPageMode.SPORTS) {
                            linearAdapter.submitList(combineResult) {
                                contentBinding.recyclerView.apply {
                                    smoothScrollToPosition(0)
                                }
                            }
                        }
                    }
                }

                launch {
                    selectedDateFlow.collect { date ->
                        if (pageMode.value == DirectPageMode.SPORTS) {
                            contentBinding.tvDate.apply {
                                text =
                                    if (date == null) dateHintStr
                                    else SimpleDateFormat("MM-dd", Locale.getDefault()).format(date)
                                setTextColor(
                                    if (date == null)
                                        SkinnableResourceManager.getColor(
                                            requireContext(),
                                            R.color.search_result_date
                                        )
                                    else
                                        SkinnableResourceManager.getColor(
                                            requireContext(),
                                            R.color.search_result_date_selected
                                        )
                                )
                            }
                            contentBinding.ivDateArrow.imageTintList =
                                if (date == null)
                                    SkinnableResourceManager.getColorStateList(
                                        requireContext(),
                                        R.color.search_result_date
                                    )
                                else
                                    SkinnableResourceManager.getColorStateList(
                                        requireContext(),
                                        R.color.search_result_date_selected
                                    )
                        }
                    }
                }

                // 供應商模式：觀察供應商資訊
                launch {
                    supplierData.collect { supplier ->
                        if (pageMode.value == DirectPageMode.VENDOR) {
                            if (supplier != null) {
                                // 供應商模式：顯示供應商資訊
                                updateVendorHeader(supplier)
                            } else {
                                // 分類模式：顯示搜索關鍵字
                                updateCategoryHeader()
                            }
                        }
                    }
                }

                // 供應商模式：觀察供應商列表，轉換成 Tab（與 GameContentFragment 一致）
                launch {
                    gameSupplierList.collect { suppliers ->
                        if (pageMode.value == DirectPageMode.VENDOR && suppliers.isNotEmpty()) {
                            if (::gameTabController.isInitialized) {
                                gameTabController.submitTabs(supplierTabList(suppliers))
                            } else {
                                contentBinding.customTabGroup.submitTabList(supplierTabList(suppliers))
                                contentBinding.customTabGroup.select(0) // 默認選中「全部」
                            }
                        }
                    }
                }

                // 供應商模式：觀察遊戲卡片列表（目前為 mock）
                launch {
                    vendorGames.collect { games ->
                        if (pageMode.value == DirectPageMode.VENDOR) {
                            vendorAdapter.submitList(games) {
                                contentBinding.recyclerView.smoothScrollToPosition(0)
                            }
                        }
                    }
                }

                launch {
                    observeLoginChange()
                        .filter { it && apiStateListener.value == DataState.NetworkUnavailable }
                        .collect { doSearch() }
                }
            }
        }
    }

    override fun onLanguageChanged(locale: Locale) {
        super.onLanguageChanged(locale)
        linearAdapter.updateLanguage(locale)
    }

    override fun closeDatePicker() {
        super.closeDatePicker()
        if (datePicker?.isVisible == true) {
            datePicker?.close()
        }
    }

    override fun onResume() {
        super.onResume()
        requireView().post {
            if (isAdded && view != null) {
                updateStatusSearchBar()
            }
        }
    }

    override fun onDestroyView() {
        contentBinding.recyclerView.adapter = null
        datePicker = null
        if (::gameTabController.isInitialized) {
            gameTabController.detach()
        }
        updateStatusSearchBar()
        super.onDestroyView()
    }

    override fun onBackPressed(): Boolean {
        findNavController().also { nav ->
            nav.backQueue.getOrNull(nav.backQueue.size - 2)?.destination?.id?.let { fromId ->
                if(fromId == R.id.searchResultBaseFragment) {
                    setTempScreenShot()
                    parentFragmentManager.setFragmentResult(GO_BACK_TO_MAIN, bundleOf(GO_BACK_TO_MAIN to true))
                }
            }
        }
        return super.onBackPressed()
    }

    private fun doSearch() {
        with(mViewModel) {
            // 判斷是否為遊戲供應商模式（type == UNKNOWN && id != null）
            if (args.type == SearchTypeEnum.UNKNOWN && args.id != null) {
                args.id?.toIntOrNull()?.let { supplierId ->
                    loadSupplierInfo(supplierId)
                }
                return
            }

            // 判斷是否為「遊戲分類匹配」（例如電子、老虎機等）
            if (args.type == SearchTypeEnum.NORMAL_WORD && args.id != null) {
                args.id?.toIntOrNull()?.let { gameTypeId ->
                    loadGameCategory(gameTypeId)
                }
                return
            }

            // 斷網重連（體育模式）
            if(directMatchId != null && directMatchType != null) {
                getSearchResult(
                    directMatchId.toString(),
                    directMatchType!!,
                    startTime,
                    endTime
                )
                return
            }

            // SearchResultBaseFragment 來的（體育模式）
            args.data?.let { data ->
                mViewModel.getSearchResult(data)
                return
            }

            // SearchListFragment 來的（體育模式）
            args.id?.let { id ->
                args.type.let { type ->
                    mViewModel.getSearchResult(id, type)
                    return
                }
            }
        }
    }

    private fun setEmptyView(state: DataState) {
        val layoutState =
            if(state == DataState.NetworkUnavailable) DynamicStateLayout.States.NETWORK_ANOMALY()
            else DynamicStateLayout.States.DATA_EMPTY
        val errorStr =
            if(state == DataState.NetworkUnavailable) {
                RC.string.error_net.toTranslatedStr()
            } else {
                R.string.no_search_result.toTranslatedStr()
            }

        contentBinding.dynamicState.setState(layoutState, errorStr)
    }

    private fun setRaceView() {
        with(contentBinding) {
            recyclerView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = linearAdapter
                itemAnimator = null
            }
        }
    }

    /**
     * 設置體育模式的 ItemDecoration
     */
    private fun setupSportsItemDecoration() {
        with(contentBinding.recyclerView) {
            // 清除所有現有的 decorations
            while (itemDecorationCount > 0) {
                removeItemDecorationAt(0)
            }
            // 添加體育模式的 decoration
            if (sportsItemDecoration == null) {
                sportsItemDecoration = object : ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: android.graphics.Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        if (position == RecyclerView.NO_POSITION) return

                        // 確保 adapter 是 linearAdapter 且有數據
                        val adapter = parent.adapter as? SearchResultRaceAdapter ?: return
                        if (adapter.itemCount == 0) return

                        try {
                            val currentType = adapter.getItemViewType(position)
                            when (currentType) {
                                SearchResultRaceAdapter.VIEW_TYPE_HEADER -> {
                                    outRect.set(0, 0, 0, 0)
                                }

                                SearchResultRaceAdapter.VIEW_TYPE_ITEM -> {
                                    if (position > 0) {
                                        val prevType = adapter.getItemViewType(position - 1)
                                        outRect.set(
                                            0,
                                            if (prevType == SearchResultRaceAdapter.VIEW_TYPE_HEADER) 0 else 12.dp2px,
                                            0, 0
                                        )
                                    } else {
                                        outRect.set(0, 0, 0, 0)
                                    }
                                }

                                else -> Unit
                            }
                        } catch (e: Exception) {
                            // 如果出錯，不設置任何 offset
                            outRect.set(0, 0, 0, 0)
                        }
                    }
                }
            }
            sportsItemDecoration?.let { addItemDecoration(it) }
        }
    }

    private fun openDatePicker() {
        with(mViewModel) {
            val oldDate = getSelectedDate()
            datePicker =
                SearchDatePickerFragment.Builder().apply {
                    val statusBarHeight =
                        ViewCompat.getRootWindowInsets(requireView())
                            ?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
                    val clDateBottom = run {
                        IntArray(2).apply {
                            contentBinding.clDate.getLocationOnScreen(this)
                        }[1] + contentBinding.clDate.height
                    }
                    setMarginTop(clDateBottom - statusBarHeight)
                    setMarginStart(8.dp2px)
                    setMarginEnd(8.dp2px)
                    setSchemeDates(racedDateMap)
                    setOnAfterDismissAnimListener { startTime: Long?, endTime: Long?, timeInMills: Long? ->
                        datePicker = null

                        val newDate = timeInMills?.let { Date(it) }
                        setSelectedDate(newDate)
                        contentBinding.clDate.isSelected = newDate != null

                        if (oldDate != newDate) {
                            directMatchType?.let { type ->
                                getSearchResult(
                                    directMatchId.toString(),
                                    type,
                                    startTime,
                                    endTime
                                )
                                setFilterTime(startTime, endTime)
                            }
                        }
                    }
                    setOnBeforeDismissAnimListener { setDateBarStatus(false) }
                    setOnBeforeExpandAnimListener { setDateBarStatus(true) }
                    getSelectedDate()?.time?.let { setSelectedDate(it) }
                }.build()

            setDateBarStatus(true)
            datePicker?.show(childFragmentManager, contentBinding.clRoot.id)
        }
    }

    private fun setDateBarStatus(isOpen: Boolean) {
        setTouchBackPressed(!isOpen)
        with(contentBinding) {
            ivDateArrow.rotation =
                if (isOpen) 180f else 0f
            clDate.background =
                SkinnableResourceManager.getDrawable(
                    requireContext(),
                    if (isOpen) R.drawable.shape_search_result_date_btn_bg_opened
                    else R.drawable.shape_search_result_direct_item_bg
                )
        }
    }

    private fun switchUI(state: DataState) {
        with(contentBinding) {
            recyclerView.visibility = if (state is DataState.LoadSuccess) View.VISIBLE else View.GONE
            when (state) {
                is DataState.NetworkUnavailable,
                is DataState.DataEmpty,
                is DataState.None -> {
                    setEmptyView(state)
                    dynamicState.visibility = View.VISIBLE
                }
                else -> {
                    dynamicState.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateDirectInfo(data: SearchResultBaseBean) {
        with(contentBinding) {
            val isPlayer = data is SearchResultPlayerBean
            when (data) {
                is SearchResultTournamentBean -> {
                    tvTitle.text = data.name
                    tvSubTitle.text =
                        data.season.takeIf { it.isNotEmpty() }
                            ?.let {
                                String.format(
                                    R.string.search_result_sub_title_tournament.toTranslatedStr(),
                                    data.season
                                )
                            } ?: noDataStr
                }

                is SearchResultTeamBean -> {
                    tvTitle.text = data.name
                    tvSubTitle.text =
                        listOf(
                            data.tournamentShortName,
                            data.rank.toString(),
                            data.win.toString(),
                            data.lose.toString()
                        ).takeIf { it.all { item -> item.isNotEmpty() } }?.let {
                            String.format(
                                R.string.search_result_sub_title_team.toTranslatedStr(),
                                data.tournamentShortName,
                                data.rank,
                                data.win,
                                data.lose
                            )
                        } ?: noDataStr
                }

                is SearchResultPlayerBean -> {
                    tvTitle.text = data.name
                    tvSubTitle.text =
                        listOf(
                            data.tournamentShortName,
                            data.teamName,
                            data.number.toString(),
                            data.position.name
                        ).takeIf { it.all { item -> item.isNotEmpty() } }?.let {
                            String.format(
                                R.string.search_result_sub_title_player.toTranslatedStr(),
                                data.tournamentShortName,
                                data.teamName,
                                data.number,
                                data.position.name
                            )
                        } ?: noDataStr
                }
            }

            Glide.with(requireContext())
                .load(data.icon)
                .placeholder(
                    if (isPlayer) R.drawable.ic_search_result_player_placeholder
                    else R.drawable.ic_search_result_placeholder
                )
                .into(
                    if (isPlayer) ivPlayer
                    else ivIcon
                )

            ivPlayer.visibility = if (isPlayer) View.VISIBLE else View.GONE
            ivIcon.visibility = if (!isPlayer) View.VISIBLE else View.GONE

            updateBackgroundColor(data.color)
        }
    }

    private fun updateBackgroundColor(color: String? = null) {
        run {
            if (color?.isNotEmpty() == true) color.toColorInt()
            else ContextCompat.getColor(
                requireContext(),
                R.color.search_result_default_gradient_start
            )
        }.let {
            mBinding.clRoot.apply {
                val duration = 100
                GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(it, Color.BLACK)
                ).let { newDrawable ->
                    background = TransitionDrawable(
                        arrayOf(background, newDrawable)
                    ).apply {
                        startTransition(duration)
                    }
                }
            }
        }
    }

    /**
     * 設置供應商模式 UI
     */
    private fun setupVendorMode() {
        with(contentBinding) {
            // 隱藏日期選擇器
            clDate.visibility = View.GONE
            customTabGroup.visibility = View.VISIBLE
            tvRewardTips.visibility = View.GONE

            recyclerView.apply {
                // 清除所有現有的 decorations（包括體育模式的）
                while (itemDecorationCount > 0) {
                    removeItemDecorationAt(0)
                }
                
                // 設置 Grid 布局
                if (layoutManager !is GridLayoutManager) {
                    layoutManager = GridLayoutManager(requireContext(), 3)
                }
                
                // 添加供應商模式的 decoration
                addItemDecoration(itemDecoration)
                
                // 設置 adapter
                adapter = vendorAdapter
                itemAnimator = null
            }
        }
    }

    /**
     * 設置體育模式 UI（恢復原樣）
     */
    private fun setupSportsMode() {
        with(contentBinding) {
            // 顯示日期選擇器
            clDate.visibility = View.VISIBLE
            customTabGroup.visibility = View.GONE
            tvRewardTips.visibility = View.GONE

            // 恢復為 Linear 布局
            recyclerView.apply {
                // 清除所有現有的 decorations（包括供應商模式的）
                while (itemDecorationCount > 0) {
                    removeItemDecorationAt(0)
                }
                
                // 設置 Linear 布局
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                
                // 設置 adapter
                adapter = linearAdapter
                
                // 添加體育模式的 decoration
                setupSportsItemDecoration()
            }
        }
    }

    /**
     * 更新供應商標題和 ICON
     */
    private fun updateVendorHeader(supplier: arch.cayenne.lib.database.entity.GameSupplierDataModel) {
        with(contentBinding) {
            tvTitle.text = supplier.name
            tvSubTitle.text = args.keyword ?: ""
            // 載入供應商 ICON
            if (supplier.icon.isNotEmpty()) {
                Glide.with(requireContext())
                    .load(supplier.icon)
                    .placeholder(R.drawable.ic_search_result_placeholder)
                    .into(ivIcon)
                ivIcon.visibility = View.VISIBLE
                ivPlayer.visibility = View.GONE
            } else {
                ivIcon.visibility = View.GONE
            }
            // 供應商模式背景色固定（可根據設計調整）
            updateBackgroundColor()
        }
    }

    /**
     * 更新分類模式標題（例如：電子、老虎機）
     */
    private fun updateCategoryHeader() {
        with(contentBinding) {
            tvTitle.text = args.keyword ?: ""
            tvSubTitle.text = ""
            ivIcon.visibility = View.GONE
            ivPlayer.visibility = View.GONE
            // 分類模式背景色固定（可根據設計調整）
            updateBackgroundColor()
        }
    }

    /**
     * 供應商模式的 UI 狀態切換
     */
    private fun switchVendorUI(state: DataState) {
        with(contentBinding) {
            recyclerView.visibility = if (state is DataState.LoadSuccess) View.VISIBLE else View.GONE
            when (state) {
                is DataState.NetworkUnavailable,
                is DataState.DataEmpty,
                is DataState.None -> {
                    setEmptyView(state)
                    dynamicState.visibility = View.VISIBLE
                }
                else -> {
                    dynamicState.visibility = View.GONE
                }
            }
        }
    }
}