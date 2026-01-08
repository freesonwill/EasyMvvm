package arch.cayenne.module.home.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.enableRecyclerViewBounce
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.TournamentListType
import arch.cayenne.module.home.databinding.FragmentTournamentBottomSheetBinding
import arch.cayenne.module.home.databinding.ItemTournamentHeaderBinding
import arch.cayenne.module.home.ui.adapter.TournamentSectionAdapter
import arch.cayenne.module.home.ui.view.CustomFilterSideBarView
import arch.cayenne.module.home.ui.view.decoration.StickyHeaderItemDecoration
import arch.cayenne.module.home.ui.viewmodel.EarlyViewModel
import arch.cayenne.module.home.ui.viewmodel.SubHomeViewModelV2
import arch.cayenne.module.home.ui.viewmodel.TournamentListViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.reflect.KClass
import arch.cayenne.lib.common.R as CommonR
import com.google.android.material.R as MaterialR

class TournamentListBottomSheetFragment :
    BaseBottomSheetFragment<TournamentListViewModel, FragmentTournamentBottomSheetBinding>() {

    override val vbClass: KClass<FragmentTournamentBottomSheetBinding> =
        FragmentTournamentBottomSheetBinding::class
    override val vmClass: KClass<TournamentListViewModel> = TournamentListViewModel::class

    private val subHomeViewModelV2: SubHomeViewModelV2 by lazy {
        if (arguments?.getInt(ARG_PLAY_TYPE_ID) == PlayType.EARLY.id) {
            viewModels<EarlyViewModel>({ requireParentFragment() }).value
        } else {
            viewModels<SubHomeViewModelV2>({ requireParentFragment() }).value
        }
    }
    private lateinit var adapter: TournamentSectionAdapter
    private var pendingJumpIndex: Int? = null
    private var stickyHeaderDecoration: StickyHeaderItemDecoration? = null

    // 統一從 arguments 讀取參數
    private val tournamentType: TournamentListType by lazy {
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getSerializable(ARG_TOURNAMENT_TYPE, TournamentListType::class.java)
            } else {
                it.getSerializable(ARG_TOURNAMENT_TYPE) as? TournamentListType
            }
        } ?: TournamentListType.MORE
    }

    private val sportId: Int by lazy {
        arguments?.getInt(ARG_SPORT_ID) ?: -1
    }

    private val playTypeId: Int by lazy {
        arguments?.getInt(ARG_PLAY_TYPE_ID) ?: 2
    }

    override fun initData() {
        mViewModel.setSportId(sportId)
        mViewModel.setPlayTypeId(playTypeId)
        mViewModel.setType(tournamentType)
        mBinding.ivHomeLeagueCollapse.isVisible = tournamentType == TournamentListType.MORE
        mViewModel.getTournaments()
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            // 設置最大高度為螢幕的 81%
            val screenHeight = resources.displayMetrics.heightPixels
            val maxFragmentHeight = (screenHeight * 0.81).toInt()
            root.maxHeight = maxFragmentHeight

            isHorizontalGestureEnable = false
            isVerticalGestureEnable = false
            ceSearch.hint = getString(R.string.tournament_section_title)
            ceSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH

            adapter = TournamentSectionAdapter(
                tournamentListType = tournamentType,
                onTournamentClick = {
                    //do nothing
                },
                onSelectionChanged = {
                    // 選中狀態變更時更新按鈕狀態
                    if (tournamentType == TournamentListType.MORE) {
                        updateConfirmButtonState()
                    }
                }
            )

            // 需求4 & 新需求：初始化選中狀態邏輯
            if (tournamentType == TournamentListType.MORE) {
                // 檢查外部tab是否有切換
                val hasSwitchedTab = subHomeViewModelV2.checkAndResetTournamentTabSwitched()

                if (hasSwitchedTab) {
                    // 情況1：如果外部tab有切換，完全清空選中狀態
                    subHomeViewModelV2.clearSavedTournamentSelections()
                    adapter.clearAllSelections()
                } else {
                    // 情況2：沒有切換外部tab，從 ViewModel 中讀取已保存的選中狀態
                    val savedSelections = subHomeViewModelV2.getCurrentSelectedTournaments()

                    if (savedSelections.isNotEmpty()) {
                        // 有保存的狀態，恢復之前的選中
                        adapter.setSelectedTournamentIds(savedSelections)
                    } else {
                        // 沒有保存的狀態，嘗試從後端加載
                        // TODO: 從後端加載已保存的選中狀態（後端還沒實作）
                        // mViewModel.loadSavedSelectionsFromBackend { selections ->
                        //     if (selections.isNotEmpty()) {
                        //         adapter.setSelectedTournamentIds(selections)
                        //         subHomeViewModel.saveTournamentSelections(selections)
                        //         adapter.saveCurrentAsInitialState()
                        //         updateConfirmButtonState()
                        //     }
                        // }
                        // 
                        // 如果後端也沒有，保持空狀態
                    }
                }

                // 保存當前狀態作為初始狀態（用於重置按鈕）
                adapter.saveCurrentAsInitialState()
                // 設置初始按鈕狀態
                updateConfirmButtonState()
            }
            rvTournamentList.layoutManager = LinearLayoutManager(context)
            rvTournamentList.adapter = adapter
            rvTournamentList.itemAnimator = null
            rvTournamentList.enableRecyclerViewBounce(
                maxOverscroll = 80f
            )
        }
        setupStickyHeader()
    }

    override fun onStart() {
        super.onStart()
        setupBottomSheetBehavior()
    }

    private fun setupBottomSheetBehavior() {
        val bottomSheet = dialog?.findViewById<FrameLayout>(
            MaterialR.id.design_bottom_sheet
        ) ?: return

        val screenHeight = resources.displayMetrics.heightPixels
        val targetHeight = (screenHeight * 0.81).toInt()
        val topOffset = screenHeight - targetHeight  // 頂部偏移 = 19% 螢幕高度

        bottomSheet.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT

        // 設置 Behavior（背景由 Fragment 的根布局提供）
        BottomSheetBehavior.from(bottomSheet).apply {
            isFitToContents = false  // 使用 expandedOffset 控制高度
            expandedOffset = topOffset  // 展開時距離頂部的偏移（19%）
            state = BottomSheetBehavior.STATE_EXPANDED  // 完全展開
            isDraggable = isVerticalGestureEnable  // false，禁止下拉
            skipCollapsed = !isVerticalGestureEnable  // true，跳過折疊
            isHideable = isVerticalGestureEnable  // false，禁止隱藏
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        with(mBinding) {
            llRoot.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    if (!ceSearch.hasFocus()) {
                        ceSearch.hideKeyboardAndClearFocus(requireContext())
                    }

                    v.performClick()
                }
                false
            }

            with(ceSearch) {
                fun hasInput(): Boolean = text?.toString()?.trim()?.isNotEmpty() == true
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus && !hasInput()) {
                        mViewModel.setSearchMode(true)
                    } else if (!hasFocus && !hasInput()) {
                        mViewModel.setSearchMode(false)
                    }
                }
                setOnEditorActionListener { _, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
                    ) {
                        hideKeyboardAndClearFocus(requireContext())
                        true
                    } else {
                        false
                    }
                }

                addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        val keyword = s?.toString()?.trim().orEmpty()
                        if (keyword.isEmpty()) {
                            mViewModel.setSearchMode(false)
                        } else {
                            mViewModel.setSearchMode(true)
                            mViewModel.searchTournament(keyword)
                        }
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                })
            }

            ivHomeLeagueCollapse.setOnClickListener {
                dismiss()
            }

            // 重置按鈕：恢復為彈窗打開時的選中狀態
            tvReset.setOnClickListener {
                if (tournamentType == TournamentListType.MORE) {
                    adapter.resetToInitialState()
                    // 重置後更新按鈕狀態
                    updateConfirmButtonState()
//                    showToast("Reset to initial state")
                } else {
                    adapter.clearAllSelections()
//                    showToast("Reset")
                }
            }

            // 確認按鈕：根據按鈕狀態執行不同操作
            tvConfirm.setOnClickListener {
                if (tournamentType == TournamentListType.MORE) {
                    val isChanged = adapter.isSelectionChanged()
                    val isInitialValid = adapter.isInitialSelectionStillValid()

                    if (isChanged || !isInitialValid) {
                        // 按鈕為"查看最新結果"狀態：執行網絡請求
                        val selectedTournamentIds = adapter.getSelectedTournamentIds()
                        val selectedTournaments = adapter.getSelectedTournaments()

                        // 保存選中狀態到 ViewModel（跨彈窗生命週期）
                        subHomeViewModelV2.saveTournamentSelections(selectedTournamentIds)

                        // TODO: 將selectedTournaments傳給viewmodel做相應處理並執行網絡請求
                        // TODO: 保留接口給後端實作記錄selected（與 saveTournamentSelections 同時進行）

                        // 若聯賽有選中，則清除 tlLeagueList 的選中狀態
                        if (selectedTournamentIds.isNotEmpty()) {
                            subHomeViewModelV2.requestClearLeagueListSelection()
                        }
                        
                        // 重置外部tab切換標記（因為用戶已確認篩選）
                        subHomeViewModelV2.resetTournamentTabSwitched()
                        dismiss()
                    } else {
                        // 按鈕為"確定"狀態：關閉彈窗，保持結果不變
                        // 即使沒有改變，也要保存當前狀態（可能是第一次打開）
                        val selectedTournamentIds = adapter.getSelectedTournamentIds()
                        subHomeViewModelV2.saveTournamentSelections(selectedTournamentIds)

                        // 若聯賽有選中，則清除 tlLeagueList 的選中狀態
                        if (selectedTournamentIds.isNotEmpty()) {
                            subHomeViewModelV2.requestClearLeagueListSelection()
                        }
                        
                        dismiss()
                    }
                } else {
                    val selectedTournaments = adapter.getSelectedTournaments()
                    // TODO: 將selectedTournaments傳給viewmodel做相應處理
                    dismiss()
                }
            }

            rvTournamentList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    pendingJumpIndex?.let { index ->
                        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                        val firstVisible = layoutManager?.findFirstVisibleItemPosition() ?: -1
                        val lastVisible = layoutManager?.findLastVisibleItemPosition() ?: -1

                        if (index >= firstVisible && index <= lastVisible ||
                            (index > lastVisible && lastVisible == adapter.itemCount - 1) ||
                            (index < firstVisible && firstVisible == 0)
                        ) {
                            mViewModel.setActiveHeaderIndex(index)
                            pendingJumpIndex = null
                            return
                        }
                    }
                    updateActiveHeaderIndex(recyclerView)
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    when (newState) {
                        RecyclerView.SCROLL_STATE_DRAGGING -> {
                            pendingJumpIndex = null
                        }
                    }
                }
            })
        }
    }

    override suspend fun createObserver() {
        mViewModel.apiStateListener.observe(viewLifecycleOwner) {
            with(mBinding) {
                when (it) {
                    is DataState.NetworkUnavailable -> {
                        clDynamics.setState(
                            DynamicStateLayout.States.NETWORK_ANOMALY(),
                            CommonR.string.error_net.getString()
                        )
                        clDynamics.visibility = View.VISIBLE
                        groupTop.visibility = View.GONE
                        llIndexContainer.visibility = View.GONE
                    }

                    is DataState.LoadSuccess -> {
                        clDynamics.visibility = View.GONE
                        groupTop.visibility = View.VISIBLE
                        ivHomeLeagueCollapse.isVisible =
                            mViewModel.getType() == TournamentListType.MORE
                        llIndexContainer.visibility = View.VISIBLE
                    }

                    HomeState.TournamentListState.InitList -> {
                        groupTop.visibility = View.VISIBLE
                        setupAZIndex()
                    }

                    HomeState.TournamentListState.RestoreList -> {
                        clDynamics.visibility = View.GONE
                        llIndexContainer.visibility = View.VISIBLE
                    }

                    HomeState.TournamentListState.ListDataEmpty -> {
                        clDynamics.setState(
                            DynamicStateLayout.States.DATA_EMPTY,
                            R.string.lineup_empty.getString()
                        )
                        clDynamics.visibility = View.VISIBLE
                        groupTop.visibility = View.GONE
                        llIndexContainer.visibility = View.GONE
                    }

                    HomeState.TournamentListState.SearchMatch -> {
                        clDynamics.visibility = View.GONE
                    }

                    HomeState.TournamentListState.SearcgInit -> {
                        clDynamics.visibility = View.GONE
                        llIndexContainer.visibility = View.GONE
                    }

                    HomeState.TournamentListState.SearchDataEmpty -> {
                        clDynamics.setState(
                            DynamicStateLayout.States.DATA_EMPTY,
                            R.string.lineup_empty.getString()
                        )
                        clDynamics.visibility = View.VISIBLE
                    }
                }
            }
        }
        mViewModel.tournamentsChange.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            // 數據更新後，檢查按鈕狀態
            if (tournamentType == TournamentListType.MORE) {
                updateConfirmButtonState()
            }
            mBinding.rvTournamentList.post{
                launch{
                    val savedSelections = subHomeViewModelV2.getCurrentSelectedTournaments()
                    if(savedSelections.isNotEmpty()){
                        val selectedIndices = it
                            .asSequence()
                            .mapIndexedNotNull { index, item ->
                                if (item is TournamentListItem.TournamentItem && item.tournament.id in savedSelections) index else null
                            }

                        val selectedIndex = selectedIndices.min()

                        val layoutManager =
                            mBinding.rvTournamentList.layoutManager as LinearLayoutManager
                        val itemHeight = mBinding.rvTournamentList.getChildAt(0)?.height ?: 0
                        val recyclerViewHeight = mBinding.rvTournamentList.height
                        val offset = recyclerViewHeight / 2 - itemHeight / 2
                        layoutManager.scrollToPositionWithOffset(selectedIndex, offset)
                    }

                }
            }
        }

        mViewModel.activeHeaderIndex.observe(viewLifecycleOwner) { _ ->
            updateAZIndexHighlight()
            mBinding.rvTournamentList.invalidateItemDecorations()
        }
    }

    private fun setupAZIndex() {
        with(mBinding.llIndexContainer) {
            indexTitles = mViewModel.getAvailableIndexLetters().map { it.toString() }
            textColorResId =
                SkinnableResourceManager.getTargetResourceId(
                    requireContext(),
                    CommonR.color.color_00A7C0
                )
            onIndexSelectedListener = object : CustomFilterSideBarView.OnIndexSelectedListener {
                override fun onIndexSelected(index: Int, letter: String, isTouching: Boolean) {
                    letter.firstOrNull()?.let {
                        mViewModel.selectLetter(it)
                        scrollToSection(it)
                    }
                }
            }
        }
    }

    private fun scrollToSection(letter: Char) {
        val index = mViewModel.getHeaderIndex(letter) ?: return
        val layoutManager =
            mBinding.rvTournamentList.layoutManager as? LinearLayoutManager ?: return

        pendingJumpIndex = index
        mBinding.llIndexContainer.setSelectedTitle(letter.toString())

        val scroller = createFastScroller(context, layoutManager, index, 0.06f)
        layoutManager.startSmoothScroll(scroller)
    }

    private fun updateAZIndexHighlight() {
        if (pendingJumpIndex != null) return

        val currentIndex = mViewModel.getActiveHeaderIndex() ?: return
        val currentLetter = mViewModel.getAvailableIndexLetters().firstOrNull {
            mViewModel.getHeaderIndex(it) == currentIndex
        } ?: return
        mBinding.llIndexContainer.setSelectedTitle(currentLetter.toString())
    }

    private fun setupStickyHeader() {
        stickyHeaderDecoration = StickyHeaderItemDecoration(
            isHeader = { position ->
                adapter.currentList.getOrNull(position) is TournamentListItem.Header
            },
            createHeaderView = { context, parent ->
                ItemTournamentHeaderBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                ).root
            },
            bindHeaderView = { view, position ->
                val item = adapter.currentList.getOrNull(position) as? TournamentListItem.Header
                    ?: return@StickyHeaderItemDecoration
                val binding = ItemTournamentHeaderBinding.bind(view)
                if (item.letter == '*') {
                    binding.ivHeaderHot.visibility = View.VISIBLE
                    binding.tvHeaderName.text = getString(R.string.tournament_section_title_hot)
                } else {
                    binding.ivHeaderHot.visibility = View.GONE
                    binding.tvHeaderName.text = item.letter.toString()
                }
            }
        )
        stickyHeaderDecoration?.let {
            mBinding.rvTournamentList.addItemDecoration(it)
        }
    }

    private fun updateActiveHeaderIndex(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition()
        if (firstVisible == RecyclerView.NO_POSITION) return

        val newIndex = when (adapter.currentList.getOrNull(firstVisible)) {
            is TournamentListItem.Header -> firstVisible
            else -> (firstVisible downTo 0).firstOrNull {
                adapter.currentList[it] is TournamentListItem.Header
            }
        }

        newIndex?.let { mViewModel.setActiveHeaderIndex(it) }
    }

    private fun View.hideKeyboardAndClearFocus(context: Context) {
        clearFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun createFastScroller(
        context: Context?,
        layoutManager: LinearLayoutManager,
        targetPosition: Int,
        speedPerPixel: Float
    ): LinearSmoothScroller {
        return object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return layoutManager.computeScrollVectorForPosition(targetPosition)
            }

            override fun calculateSpeedPerPixel(displayMetrics: android.util.DisplayMetrics): Float {
                return speedPerPixel / displayMetrics.density
            }
        }.apply {
            this.targetPosition = targetPosition
        }
    }

    /**
     * 更新確認按鈕的狀態和文本
     * - 如果選中結果與打開時相同且原選中聯賽都還存在：顯示"確定"
     * - 如果選中結果不同或原選中聯賽消失：顯示"查看最新結果"
     */
    private fun updateConfirmButtonState() {
        if (tournamentType != TournamentListType.MORE) {
            return
        }

        val isChanged = adapter.isSelectionChanged()
        val isInitialValid = adapter.isInitialSelectionStillValid()

        // 如果選中狀態改變 或 原選中的聯賽消失，顯示"查看最新結果"
        if (isChanged || !isInitialValid) {
            mBinding.tvConfirm.text = getString(R.string.tournament_view_latest_results)
            mBinding.tvReset.isEnabled = true
        } else {
            mBinding.tvConfirm.text = getString(R.string.tournament_confirm)
            mBinding.tvReset.isEnabled = false
        }
    }

    companion object {
        private const val ARG_TOURNAMENT_TYPE = "tournament_type"
        private const val ARG_SPORT_ID = "sport_id"
        private const val ARG_PLAY_TYPE_ID = "play_type_id"
        fun newInstance(
            playTypeId: Int,
            sportId: Int,
            type: TournamentListType
        ): TournamentListBottomSheetFragment {
            return TournamentListBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PLAY_TYPE_ID, playTypeId)
                    putInt(ARG_SPORT_ID, sportId)
                    putSerializable(ARG_TOURNAMENT_TYPE, type)
                }
            }
        }
    }
}


