package arch.cayenne.module.home.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.enableBottomBounce
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.TournamentListState
import arch.cayenne.module.home.databinding.FragmentTournamentListBinding
import arch.cayenne.module.home.databinding.ItemTournamentHeaderBinding
import arch.cayenne.module.home.ui.adapter.TournamentSectionAdapter
import arch.cayenne.module.home.ui.view.decoration.StickyHeaderItemDecoration
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import arch.cayenne.module.home.ui.viewmodel.TournamentListViewModel
import kotlin.reflect.KClass

class TournamentListFragment :
    BaseFragment<TournamentListViewModel, FragmentTournamentListBinding>() {

    override val vbClass: KClass<FragmentTournamentListBinding> =
        FragmentTournamentListBinding::class
    override val vmClass: KClass<TournamentListViewModel> = TournamentListViewModel::class

    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()
    private lateinit var adapter: TournamentSectionAdapter
    private var isJumpingByIndex = false
    private var pendingJumpIndex: Int? = null

    override fun initData() {
        arguments?.apply {
            val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getSerializable(ARG_TOURNAMENT_TYPE, TournamentListType::class.java)
            } else {
                getSerializable(ARG_TOURNAMENT_TYPE) as? TournamentListType
            }
            mViewModel.setSportId(getInt(ARG_SPORT_ID))
            type?.apply {
                mViewModel.setType(this)
                mBinding.ivHomeLeagueCollapse.isVisible = this == TournamentListType.MORE
                mViewModel.getTournaments()
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            clDynamics.setState(
                DynamicStateLayout.States.DATA_EMPTY,
                R.string.lineup_empty.getString()
            )

            ceSearch.hint = getString(R.string.tournament_section_title)
            ceSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH
            adapter = TournamentSectionAdapter(
                onTournamentClick = { tournament ->
                    homeViewModel.onTournamentListSelected(tournament)
                    if (mViewModel.getType() == TournamentListType.MORE) {
                        homeViewModel.requestCollapseTournamentDropdown()
                    }
                }
            )
            rvTournamentList.layoutManager = LinearLayoutManager(context)
            rvTournamentList.adapter = adapter
            rvTournamentList.enableBottomBounce()
        }
        setupStickyHeader()
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
                        // 進入搜尋模式顯示空列表
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
                        val keyword = text?.toString()?.trim().orEmpty()
                        if (hasInput()) {
                            mViewModel.searchTournament(keyword)
                        }
                        true
                    } else {
                        false
                    }
                }

                addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        val keyword = s?.toString()?.trim().orEmpty()
                        if (keyword.isEmpty() && !mBinding.ceSearch.hasFocus()) {
                            mViewModel.setSearchMode(false)
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
                homeViewModel.requestCollapseTournamentDropdown()
            }

            rvTournamentList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (isJumpingByIndex) return

                    val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                    val firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (firstVisible == RecyclerView.NO_POSITION) return

                    val newIndex = when (adapter.currentList.getOrNull(firstVisible)) {
                        is TournamentListItem.Header -> firstVisible
                        else -> (firstVisible downTo 0).firstOrNull {
                            adapter.currentList[it] is TournamentListItem.Header
                        }
                    }
                    if (newIndex != null) {
                        mViewModel.setActiveHeaderIndex(newIndex)
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (isJumpingByIndex) {
                            pendingJumpIndex?.let {
                                mViewModel.setActiveHeaderIndex(it)
                            }
                            isJumpingByIndex = false
                            pendingJumpIndex = null
                        }
                    }
                }
            })
        }
    }

    override fun createObserver() {
        mViewModel.uiState.observe(viewLifecycleOwner) { model ->
            adapter.submitList(model.displayList)
            if (!mViewModel.isSearchMode) homeViewModel.changeState(HomeState.Tournament.LoadListSuccess)
            with(mBinding) {
                when (model.state) {
                    TournamentListState.INIT_LIST -> {
                        setupAZIndex()
                    }

                    TournamentListState.RESTORE_LIST -> {
                        clDynamics.visibility = View.GONE
                        groupTop.visibility = View.VISIBLE
                        llIndexContainer.visibility = View.VISIBLE
                    }

                    TournamentListState.LIST_DATA_EMPTY -> {
                        clDynamics.visibility = View.VISIBLE
                        groupTop.visibility = View.GONE
                        llIndexContainer.visibility = View.GONE
                    }

                    TournamentListState.SEARCH_MATCH -> {
                        clDynamics.visibility = View.GONE
                    }

                    TournamentListState.SEARCH_INIT -> {
                        clDynamics.visibility = View.GONE
                        llIndexContainer.visibility = View.GONE
                    }

                    TournamentListState.SEARCH_DATA_EMPTY -> {
                        clDynamics.visibility = View.VISIBLE
                    }
                }
            }
        }

        mViewModel.activeHeaderIndex.observe(viewLifecycleOwner) { index ->
            updateAZIndexHighlight()
            mBinding.rvTournamentList.invalidateItemDecorations()
        }
    }

    private fun setupAZIndex() {
        with(mBinding.llIndexContainer) {
            setLetters(mViewModel.getAvailableIndexLetters())
            onLetterTouch = { letter ->
                mViewModel.selectLetter(letter)
                scrollToSection(letter)
            }
        }
    }

    private fun scrollToSection(letter: Char) {
        val index = mViewModel.getHeaderIndex(letter) ?: return
        val layoutManager =
            mBinding.rvTournamentList.layoutManager as? LinearLayoutManager ?: return
        isJumpingByIndex = true
        pendingJumpIndex = index

        updateAZIndexHighlight()

        val scroller = createFastScroller(context, layoutManager, index)
        layoutManager.startSmoothScroll(scroller)
    }

    private fun updateAZIndexHighlight() {
        val currentIndex = mViewModel.getActiveHeaderIndex()
        val currentLetter = mViewModel.getAvailableIndexLetters().firstOrNull {
            mViewModel.getHeaderIndex(it) == currentIndex
        } ?: return
        mBinding.llIndexContainer.setSelectedLetter(currentLetter)
    }

    private fun setupStickyHeader() {
        val decoration = StickyHeaderItemDecoration(
            isHeader = { position ->
                adapter.currentList.getOrNull(position) is TournamentListItem.Header
            },
            createHeaderView = {
                ItemTournamentHeaderBinding.inflate(layoutInflater).root
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

        mBinding.rvTournamentList.addItemDecoration(decoration)
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
        speedPerPixel: Float = 0.08f
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
        fun newInstance(sportId: Int, type: TournamentListType): TournamentListFragment {
            return TournamentListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SPORT_ID, sportId)
                    putSerializable(ARG_TOURNAMENT_TYPE, type)
                }
            }
        }
    }
}

enum class TournamentListType {
    MORE, CHAMPION, NONE
}