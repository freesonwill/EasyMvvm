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
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.enableRecyclerViewBounce
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.databinding.FragmentTournamentListBinding
import arch.cayenne.module.home.databinding.ItemTournamentHeaderBinding
import arch.cayenne.module.home.ui.adapter.TournamentSectionAdapter
import arch.cayenne.module.home.ui.view.decoration.StickyHeaderItemDecoration
import arch.cayenne.module.home.ui.viewmodel.SubHomeViewModel
import arch.cayenne.module.home.ui.viewmodel.TournamentListViewModel
import kotlin.reflect.KClass

class TournamentListFragment :
    BaseFragment<TournamentListViewModel, FragmentTournamentListBinding>() {

    override val vbClass: KClass<FragmentTournamentListBinding> =
        FragmentTournamentListBinding::class
    override val vmClass: KClass<TournamentListViewModel> = TournamentListViewModel::class

    private val subHomeViewModel: SubHomeViewModel by viewModels({ requireParentFragment() })
    private lateinit var adapter: TournamentSectionAdapter
    private var pendingJumpIndex: Int? = null // 用來判斷是否為點擊字母列表來跳選列表分類，null代表非自動跳轉狀態
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

    fun changeSportId(sportId: Int) {
        mViewModel.setSportId(sportId)
        mViewModel.getTournaments()
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            ceSearch.hint = getString(R.string.tournament_section_title)
            ceSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH
            adapter = TournamentSectionAdapter(
                onTournamentClick = { tournament ->
                    subHomeViewModel.onTournamentListSelected(tournament)
                    if (mViewModel.getType() == TournamentListType.MORE) {
                        subHomeViewModel.requestCollapseTournamentDropdown()
                    }
                }
            )
            rvTournamentList.layoutManager = LinearLayoutManager(context)
            rvTournamentList.adapter = adapter
            rvTournamentList.enableRecyclerViewBounce(
                maxOverscroll = 80f
            )
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
                            // 有輸入內容時自動開始搜尋
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

            ivHomeLeagueCollapse.apply{addScaleOnTouchAnimation()}.setOnClickListener {
                subHomeViewModel.requestCollapseTournamentDropdown()
            }

            rvTournamentList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    // 檢查是否有待處理的跳轉
                    pendingJumpIndex?.let { index ->
                        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                        val firstVisible = layoutManager?.findFirstVisibleItemPosition() ?: -1
                        val lastVisible = layoutManager?.findLastVisibleItemPosition() ?: -1

                        // 如果目標位置在可見範圍內，或者已經到達邊界，則完成跳轉
                        if (index >= firstVisible && index <= lastVisible ||
                            (index > lastVisible && lastVisible == adapter.itemCount - 1) ||
                            (index < firstVisible && firstVisible == 0)
                        ) {
                            mViewModel.setActiveHeaderIndex(index)
                            pendingJumpIndex = null
                            return
                        }
                    }

                    // 沒有待處理的跳轉時，正常更新活動標題
                    updateActiveHeaderIndex(recyclerView)
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    when (newState) {
                        RecyclerView.SCROLL_STATE_DRAGGING -> {
                            // 用戶開始拖動時，清理待處理的跳轉
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
                when(it) {
                    is DataState.Loading -> {
                        loadingView.visibility = View.VISIBLE
                    }
                    is DataState.NetworkUnavailable -> {
                        clDynamics.setState(
                            DynamicStateLayout.States.NETWORK_ANOMALY(),
                            arch.cayenne.lib.common.R.string.error_net.getString()
                        )
                        clDynamics.visibility = View.VISIBLE
                        groupTop.visibility = View.GONE
                        llIndexContainer.visibility = View.GONE
                        loadingView.visibility = View.GONE
                    }
                    is DataState.LoadSuccess -> {
                        clDynamics.visibility = View.GONE
                        groupTop.visibility = View.VISIBLE
                        ivHomeLeagueCollapse.isVisible = mViewModel.getType() == TournamentListType.MORE
                        llIndexContainer.visibility = View.VISIBLE
                        loadingView.visibility = View.GONE
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
                        loadingView.visibility = View.GONE
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
        
        pendingJumpIndex = index
        // 直接設置選中的字母
        mBinding.llIndexContainer.setSelectedLetter(letter)

        val scroller = createFastScroller(context, layoutManager, index, 0.06f)
        layoutManager.startSmoothScroll(scroller)
    }

    private fun updateAZIndexHighlight() {
        // 如果有待處理的跳轉，不更新高亮
        if (pendingJumpIndex != null) return

        val currentIndex = mViewModel.getActiveHeaderIndex() ?: return
        val currentLetter = mViewModel.getAvailableIndexLetters().firstOrNull {
            mViewModel.getHeaderIndex(it) == currentIndex
        } ?: return
        mBinding.llIndexContainer.setSelectedLetter(currentLetter)
    }

    private fun setupStickyHeader() {
        stickyHeaderDecoration = StickyHeaderItemDecoration(
            isHeader = { position ->
                adapter.currentList.getOrNull(position) is TournamentListItem.Header
            },
            createHeaderView = { context, parent ->
                ItemTournamentHeaderBinding.inflate(
                    LayoutInflater.from(context),
                    parent, // parent 設為 recyclerView
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
    override fun onDestroyView() {
        super.onDestroyView()
        stickyHeaderDecoration?.let {
            mBinding.rvTournamentList.removeItemDecoration(it)
        }
        stickyHeaderDecoration = null
        pendingJumpIndex = null
        
        // 通知聯賽收回上滑動畫已結束
        if (mViewModel.getType() == TournamentListType.MORE) {
            subHomeViewModel.notifyTournamentSlideOutEnd()
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

    companion object {
        private const val ARG_TOURNAMENT_TYPE = "tournament_type"
        private const val ARG_SPORT_ID = "sport_id"
        private const val ARG_PLAY_TYPE_ID = "play_type_id"
        fun newInstance(playTypeId: Int, sportId: Int, type: TournamentListType): TournamentListFragment {
            return TournamentListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PLAY_TYPE_ID, playTypeId)
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