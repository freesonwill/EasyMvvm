package com.walisport.module.hall.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.ui.view.CustomFilterSideBarView
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.enableRecyclerViewBounce
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.hall.data.GameSupplierListItem
import arch.cayenne.module.hall.ui.adapter.GameSupplierSectionAdapter
import arch.cayenne.module.hall.ui.view.decoration.SupplierStickyHeaderItemDecoration
import com.walisport.module.hall.databinding.FragmentGameContentBottomSheetBinding
import com.walisport.module.hall.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.walisport.module.hall.ui.fragment.GameContentFragment
import com.walisport.module.hall.ui.viewmodel.GameContentViewModel
import kotlin.reflect.KClass
import arch.cayenne.lib.common.R as CommonR
import com.google.android.material.R as MaterialR
import com.walisport.module.hall.databinding.ItemSupplierHeaderBinding
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.hall.ui.viewmodel.GameContentListViewModel
import kotlinx.coroutines.delay
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
class GameContentListBottomSheetFragment :
    BaseBottomSheetFragment<GameContentListViewModel, FragmentGameContentBottomSheetBinding>() {

    override val vbClass: KClass<FragmentGameContentBottomSheetBinding> =
        FragmentGameContentBottomSheetBinding::class
    override val vmClass: KClass<GameContentListViewModel> = GameContentListViewModel::class

//    private val subHomeViewModel: SubHomeViewModel by lazy {
//        if (arguments?.getInt(ARG_PLAY_TYPE_ID) == PlayType.EARLY.id) {
//            viewModels<EarlyViewModel>({ requireParentFragment() }).value
//        } else {
//            viewModels<SubHomeViewModel>({ requireParentFragment() }).value
//        }
//    }

    private lateinit var adapter: GameSupplierSectionAdapter
    private var pendingJumpIndex: Int? = null
    private var stickyHeaderDecoration: SupplierStickyHeaderItemDecoration? = null
    private val mGameContentVm: GameContentViewModel by sharedViewModel<GameContentViewModel, GameContentFragment>(fragmentFilter = { f->
        f.category == requireArguments().getInt(GAME_TYPE_ID)
    })
    private val gameTypeId: Int by lazy {
        arguments?.getInt(GAME_TYPE_ID) ?: -1
    }


    override fun initData() {
        mViewModel.setSportId(gameTypeId)
        mViewModel.getTournaments(gameTypeId)
        LogUtils.e("mGameContentVm-----$mGameContentVm,category:${mGameContentVm.getCategory()}, gameTypeId:$gameTypeId")
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            // 設置最大高度為螢幕的 81%
            val screenHeight = resources.displayMetrics.heightPixels
            val maxFragmentHeight = (screenHeight * 0.81).toInt()
            root.maxHeight = maxFragmentHeight

            isHorizontalGestureEnable = false
            isVerticalGestureEnable = false
            ceSearchSupplier.hint = getString(R.string.supplier_search_hint)
            ceSearchSupplier.imeOptions = EditorInfo.IME_ACTION_SEARCH

            adapter = GameSupplierSectionAdapter(
                onClick = { ids ->
                   // mViewModel.setSelectIds(adapter.getSelectedTournamentIds())
                    //  mGameContentVm.onTournamentListSelected(tournament)
                },
                onSelectionChanged = {
                    updateConfirmButtonState()
                }
            )
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
                    if (!ceSearchSupplier.hasFocus()) {
                        ceSearchSupplier.hideKeyboardAndClearFocus(requireContext())
                    }

                    v.performClick()
                }
                false
            }
            clDynamics.clickNoRepeat{}
            with(ceSearchSupplier) {
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

            ivHomeLeagueCollapse.clickNoRepeat {
                dismiss()
            }

            // 重置按鈕：恢復為彈窗打開時的選中狀態
            tvReset.clickNoRepeat {
                adapter.resetToInitialState()
                // 重置後更新按鈕狀態
                updateConfirmButtonState()
            }

            // 確認按鈕：根據按鈕狀態執行不同操作
            tvConfirm.clickNoRepeat {
                val isChanged = adapter.isSelectionChanged()
                val isInitialValid = adapter.isInitialSelectionStillValid()

                if (isChanged || !isInitialValid) {
                    // 按鈕為"查看最新結果"狀態：執行網絡請求
                    val selectedIds = adapter.getSelectedTournamentIds()
                    val selectedTournaments = adapter.getSelectedTournaments()

                    // 保存選中狀態到 ViewModel（跨彈窗生命週期）
                     mGameContentVm.saveTournamentSelections(selectedIds)

                    // 若聯賽有選中，則清除 tlLeagueList 的選中狀態
                    if (selectedIds.isNotEmpty()) {
                        if(selectedIds.size!=1){
                            mGameContentVm.requestClearLeagueListSelection()
                        }
                        //保存本地记录,记录选中
                        mViewModel.setSelectIds(selectedIds)
                    }

                    // 重置外部tab切換標記（因為用戶已確認篩選）
                    // mGameContentVm.resetTournamentTabSwitched()
                    dismiss()
                } else {
                    // 按鈕為"確定"狀態：關閉彈窗，保持結果不變
                    // 即使沒有改變，也要保存當前狀態（可能是第一次打開）
                    val selectedTournamentIds = adapter.getSelectedTournamentIds()
                   mGameContentVm.saveTournamentSelections(selectedTournamentIds)

                    // 若聯賽有選中，則清除 tlLeagueList 的選中狀態
                    if (selectedTournamentIds.isNotEmpty()) {
                        if(selectedTournamentIds.size!=1){
                            mGameContentVm.requestClearLeagueListSelection()
                        }
                    }
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

    private fun updateConfirmButtonState() {

        val isChanged = adapter.isSelectionChanged()
        val isInitialValid = adapter.isInitialSelectionStillValid()
        if (isChanged || !isInitialValid) {
            mBinding.tvConfirm.text = getString(R.string.view_latest_results)
        } else {
            mBinding.tvConfirm.text = getString(R.string.btn_confirm)
        }

        if (adapter.getSelectedTournamentIds().isEmpty()){
            mBinding.tvConfirm.text = getString(R.string.btn_confirm)
        }
        mBinding.tvReset.isSelected = isChanged
        if (adapter.getSelectedTournamentIds().isEmpty()){
            mBinding.tvReset.isSelected = false
        }
    }
    override suspend fun createObserver() {
        mViewModel.tournamentsChange.observe(viewLifecycleOwner) {data->
            if (data.isEmpty()){
                if(mViewModel.isSearchMode){
                    if (mBinding.ceSearchSupplier.text.toString().isNotEmpty()) {
                        mBinding.clDynamics.setState(
                            States.DATA_EMPTY,
                            arch.cayenne.lib.common.R.string.data_empty.getString()
                        )
                    }else{
                        return@observe
                    }
                }else{
                    mBinding.clDynamics.setState(
                        States.DATA_EMPTY ,
                        arch.cayenne.lib.common.R.string.data_empty.getString()
                    )
                }
                mBinding.llIndexContainer.visibility = View.GONE
                mBinding.clDynamics.visibility = View.VISIBLE
                return@observe
            }else{
                if(mViewModel.isSearchMode){
                    mBinding.llIndexContainer.visibility = View.GONE
                    mBinding.clDynamics.visibility = View.GONE
                } else{
                    mBinding.llIndexContainer.visibility = View.VISIBLE
                    mBinding.clDynamics.visibility = View.GONE
                }
            }
            adapter.submitList(data)
            val selectedIds = data
                .asSequence()
                .filterIsInstance<GameSupplierListItem.GameSupplierItem>()
                .filter { it.tournament.isSelected==1 }
                .map { it.tournament.id }
                .toList()
            adapter.setSelectedIds(selectedIds)
            adapter.saveCurrentAsInitialState(selectedIds)
            mBinding.groupTop.visibility = View.VISIBLE
            setupAZIndex()
            mBinding.rvTournamentList.post{
                launch{
                    val selectedIndex = data
                        .asSequence()
                        .mapIndexedNotNull { index, item ->
                            if (item is GameSupplierListItem.GameSupplierItem && item.tournament.isSelected == 1) index else null
                        }
                        .firstOrNull() ?: 0
                    val layoutManager = mBinding.rvTournamentList.layoutManager as LinearLayoutManager
                    layoutManager.scrollToPosition(selectedIndex)
                }

            }
        }

        mViewModel.activeHeaderIndex.observe(viewLifecycleOwner) { _ ->
            updateAZIndexHighlight()
            mBinding.rvTournamentList.invalidateItemDecorations()
        }

    }

    //右边字母和左边数据联动
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
        stickyHeaderDecoration = SupplierStickyHeaderItemDecoration(
            isHeader = { position ->
                adapter.currentList.getOrNull(position) is GameSupplierListItem.Header
            },
            createHeaderView = { context, parent ->
                ItemSupplierHeaderBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                ).root
            },
            bindHeaderView = { view, position ->
                val item = adapter.currentList.getOrNull(position) as? GameSupplierListItem.Header
                    ?: return@SupplierStickyHeaderItemDecoration
                val binding = ItemSupplierHeaderBinding.bind(view)
                if (item.letter == '*') {
                    binding.ivHeaderHot.visibility = View.VISIBLE
                    binding.tvHeaderName.text = getString(R.string.supplier_section_title_hot)
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
            is GameSupplierListItem.Header -> firstVisible
            else -> (firstVisible downTo 0).firstOrNull {
                adapter.currentList[it] is GameSupplierListItem.Header
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


    companion object {
        private const val GAME_TYPE_ID = "gameTypeId"
        fun newInstance(
            gameTypeId: Int,
        ): GameContentListBottomSheetFragment {
            return GameContentListBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putInt(GAME_TYPE_ID, gameTypeId)
                }
            }
        }
    }
}


