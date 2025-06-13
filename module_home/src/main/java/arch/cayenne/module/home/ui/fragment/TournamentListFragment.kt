package arch.cayenne.module.home.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.databinding.FragmentTournamentListBinding
import arch.cayenne.module.home.databinding.ItemTournamentHeaderBinding
import arch.cayenne.module.home.ui.adapter.TournamentSectionAdapter
import arch.cayenne.module.home.ui.view.decoration.StickyHeaderItemDecoration
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import arch.cayenne.module.home.ui.viewmodel.TournamentListViewModel
import com.ibm.icu.text.Transliterator
import org.koin.android.ext.android.inject
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

    private val transliterator: Transliterator by inject()

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


    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            llRoot.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    if (ceSearch.hasFocus()) {
                        // 清除focus, 隱藏鍵盤
                        ceSearch.clearFocus()
                        val imm =
                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(mBinding.ceSearch.windowToken, 0)
                    }

                    v.performClick()
                }
                false
            }
            ceSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH
            ceSearch.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    // 離開搜尋框 → 回到列表頂端
                    rvTournamentList.smoothScrollToPosition(0)
                    llIndexContainer.visibility = View.VISIBLE
                }
            }

            ceSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val keyword = s?.toString()?.trim().orEmpty()
                    if (keyword.isNotEmpty()) {
                        mViewModel.searchTournament(keyword)
                    } else {
                        mViewModel.clearSearch()
                        mBinding.llIndexContainer.visibility = View.VISIBLE
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    mBinding.llIndexContainer.visibility = View.GONE
                }
            })

            ivHomeLeagueCollapse.setOnClickListener {
                homeViewModel.requestCollapseTournamentDropdown()
            }

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
            rvTournamentList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    // 點字母時忽略以下頂部item判斷, 避免排序最底的字母分類, 因為底部空間不足無法吸頂時, 無法被選中
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
                    // 偵測是否最後一個 header 正在吸頂
                    val lastHeaderIndex =
                        adapter.currentList.indexOfLast { it is TournamentListItem.Header }
                    if (adapter.isLastHeaderSticking(lastHeaderIndex, layoutManager)) {
                        recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                    } else {
                        recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        // 如果是點字母觸發的 scroll，直接選中並更新 ViewModel 狀態
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
        setupStickyHeader()
    }

    override fun initListener() {
    }

    override fun createObserver() {
        mViewModel.tournaments.observe(viewLifecycleOwner) { list ->
            if (!list.isNullOrEmpty()) {
                setTournamentList(list)
            } else {
                mBinding.clDynamics.visibility = View.VISIBLE
                mBinding.clDynamics.setState(
                    DynamicStateLayout.States.DATA_EMPTY,
                    R.string.lineup_empty.getString()
                )
            }
            homeViewModel.changeState(HomeState.LOADING_TOURNAMENT_LIST_SUCCESS)
        }

        mViewModel.activeHeaderIndex.observe(viewLifecycleOwner) { index ->
            updateAZIndexHighlight()
            mBinding.rvTournamentList.invalidateItemDecorations()
        }

        mViewModel.searchDisplayList.observe(viewLifecycleOwner) { result ->
            if (result == null) {
                mViewModel.tournaments.value?.let { setTournamentList(it) }
            } else {
                //TODO 整個searchDisplayList都要和tournaments一起處理發送，adapter只會被一個live data觸發
                val displayList = result.map { TournamentListItem.TournamentItem(it.third, it.first, it.second) }
                adapter.submitList(displayList)
            }
        }

    }

    private fun setSearchHint(list: List<TournamentListItem>) {
        val firstItemName = list.firstOrNull {
            it is TournamentListItem.TournamentItem
        }?.let {
            (it as TournamentListItem.TournamentItem).tournament.name
        }?.takeIf { it.isNotBlank() }

        firstItemName?.let {
            mBinding.ceSearch.hint = it
        }
    }


    private fun setTournamentList(tournaments: List<BaseTournamentData>) {
        val groupedMap = mutableMapOf<Char, MutableList<BaseTournamentData>>()
        val hotList = mutableListOf<BaseTournamentData>()
        val otherList = mutableListOf<BaseTournamentData>()
        val displayList = mutableListOf<TournamentListItem>()
        val letterPositionMap = mutableMapOf<Char, Int>()

        tournaments.forEach { tournament ->
            val pinyin = transliterator.transliterate(tournament.name).trim()
            val firstChar = pinyin.firstOrNull()?.uppercaseChar()
            when {
                tournament.hot -> hotList.add(tournament)
                firstChar != null && firstChar in 'A'..'Z' -> {
                    groupedMap.getOrPut(firstChar) { mutableListOf() }.add(tournament)
                }

                else -> otherList.add(tournament)
            }
        }

        if (hotList.isNotEmpty()) {
            displayList.add(TournamentListItem.Header('*'))
            letterPositionMap['*'] = displayList.size - 1
            displayList.addAll(hotList.map { TournamentListItem.TournamentItem(it, null, null) })
        }

        groupedMap.toSortedMap().forEach { (letter, list) ->
            letterPositionMap[letter] = displayList.size
            displayList.add(TournamentListItem.Header(letter))
            displayList.addAll(list.map { TournamentListItem.TournamentItem(it, null, null) })
        }

        if (otherList.isNotEmpty()) {
            letterPositionMap['#'] = displayList.size
            displayList.add(TournamentListItem.Header('#'))
            displayList.addAll(otherList.map { TournamentListItem.TournamentItem(it, null, null) })
        }
        setSearchHint(displayList)
        displayList.add(TournamentListItem.FooterView)
        adapter.submitList(displayList)
        mViewModel.setLetterPositionMap(letterPositionMap)
        setupAZIndex()
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

        val scroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return layoutManager.computeScrollVectorForPosition(targetPosition)
            }
        }
        scroller.targetPosition = index
        layoutManager.startSmoothScroll(scroller)
    }

    private fun updateAZIndexHighlight() {
        val currentIndex = mViewModel.getActiveHeaderIndex()
        val currentLetter = mViewModel.getAvailableIndexLetters().firstOrNull {
            mViewModel.getHeaderIndex(it) == currentIndex
        } ?: return
        mViewModel.setLastSelectedLetter(currentLetter)
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

                binding.root.setBackgroundColor(
                    SkinnableResourceManager.getColor(
                        view.context,
                        R.color.home_card_odds_background
                    )
                )
            }
        )

        mBinding.rvTournamentList.addItemDecoration(decoration)
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