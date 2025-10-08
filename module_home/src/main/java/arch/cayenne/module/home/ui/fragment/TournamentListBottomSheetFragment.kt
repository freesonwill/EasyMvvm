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
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.enableRecyclerViewBounce
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.databinding.FragmentTournamentBottomSheetBinding
import arch.cayenne.module.home.databinding.ItemTournamentHeaderBinding
import arch.cayenne.module.home.ui.adapter.TournamentSectionAdapter
import arch.cayenne.module.home.ui.view.CustomFilterSideBarView
import arch.cayenne.module.home.ui.view.decoration.StickyHeaderItemDecoration
import arch.cayenne.module.home.ui.viewmodel.SubHomeViewModel
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

    private val subHomeViewModel: SubHomeViewModel by viewModels({ requireParentFragment() })
    private lateinit var adapter: TournamentSectionAdapter
    private var pendingJumpIndex: Int? = null
    private var stickyHeaderDecoration: StickyHeaderItemDecoration? = null

    override fun initData() {
        arguments?.apply {
            val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getSerializable(ARG_TOURNAMENT_TYPE, TournamentListType::class.java)
            } else {
                getSerializable(ARG_TOURNAMENT_TYPE) as? TournamentListType
            }
            mViewModel.setSportId(getInt(ARG_SPORT_ID))
            mViewModel.setPlayTypeId(getInt(ARG_PLAY_TYPE_ID))
            type?.apply {
                mViewModel.setType(this)
                mBinding.ivHomeLeagueCollapse.isVisible = this == TournamentListType.MORE
                mViewModel.getTournaments()
            }
        }
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
                onTournamentClick = { tournament ->
                    //新版改為多選方式
                    subHomeViewModel.onTournamentListSelected(tournament)
                    showToast("Select: ${tournament.simpleName}")
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

            // 重置按鈕：清除所有選中狀態
            tvReset.setOnClickListener {
                showToast("Reset")
                adapter.clearAllSelections()
            }

            // 確認按鈕：將選中的聯賽傳給viewmodel
            tvConfirm.setOnClickListener {
                showToast("Confirm")
                val selectedTournaments = adapter.getSelectedTournaments()
                // TODO: 將selectedTournaments傳給viewmodel做相應處理
                dismiss()
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


