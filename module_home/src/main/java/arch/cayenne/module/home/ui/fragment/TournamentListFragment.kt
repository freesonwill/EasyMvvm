package arch.cayenne.module.home.ui.fragment

import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.databinding.FragmentTournamentListBinding
import arch.cayenne.module.home.ui.adapter.TournamentSectionAdapter
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
    private val letterViewMap = mutableMapOf<Char, View>()
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


    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {

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
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        // 如果是點字母觸發的 scroll，直接選中
                        if (isJumpingByIndex) {
                            pendingJumpIndex?.let {
                                mViewModel.setActiveHeaderIndex(it)
                                adapter.updateActiveHeaderIndex(it)
                            }
                            isJumpingByIndex = false
                            pendingJumpIndex = null
                        } else {
                            mViewModel.getActiveHeaderIndex()
                                ?.let { adapter.updateActiveHeaderIndex(it) }
                        }
                    }
                }
            })
        }
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
            displayList.addAll(hotList.map { TournamentListItem.TournamentItem(it) })
        }

        groupedMap.toSortedMap().forEach { (letter, list) ->
            letterPositionMap[letter] = displayList.size
            displayList.add(TournamentListItem.Header(letter))
            displayList.addAll(list.map { TournamentListItem.TournamentItem(it) })
        }

        if (otherList.isNotEmpty()) {
            letterPositionMap['#'] = displayList.size
            displayList.add(TournamentListItem.Header('#'))
            displayList.addAll(otherList.map { TournamentListItem.TournamentItem(it) })
        }
        adapter.submitList(displayList)
        mViewModel.setLetterPositionMap(letterPositionMap)
        setupAZIndex()
    }

    private fun setupAZIndex() {
        mBinding.llIndexContainer.removeAllViews()
        letterViewMap.clear()

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        mViewModel.getAvailableIndexLetters().forEach { letter ->
            val view = createLetterView(letter)
            letterViewMap[letter] = view
            container.addView(view)
        }

        mBinding.llIndexContainer.addView(container)
    }

    private fun createLetterView(letter: Char): View {
        val isSelected = mViewModel.getHeaderIndex(letter) == mViewModel.getActiveHeaderIndex()
        return if (letter == '*') {
            ImageView(context).apply {
                setImageResource(if (isSelected) R.drawable.ic_hot_league_index else R.drawable.ic_hot_league_index_unselect)
                layoutParams = LinearLayout.LayoutParams(24.dp2px, 18.dp2px)
                setOnClickListener { scrollToSection('*') }
            }
        } else {
            TextView(context).apply {
                text = letter.toString()
                textSize = 11f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(24.dp2px, 18.dp2px)
                setTextColor(
                    SkinnableResourceManager.getColor(
                        context,
                        if (isSelected) arch.cayenne.lib.common.R.color.brand_color else R.color.brand_color_index_unselect
                    )
                )
                setOnClickListener {
                    mViewModel.selectLetter(letter)
                    scrollToSection(letter)
                }
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
        val lastSelectedLetter = mViewModel.getLastSelectedLetter()
        if (currentLetter != lastSelectedLetter) {
            // 還原舊樣式
            lastSelectedLetter?.let { last ->
                when (val oldView = letterViewMap[last]) {
                    is TextView -> oldView.setTextColor(
                        SkinnableResourceManager.getColor(
                            requireContext(),
                            R.color.brand_color_index_unselect
                        )
                    )

                    is ImageView -> oldView.setImageResource(R.drawable.ic_hot_league_index_unselect)
                }
            }

            // 套用新選中樣式
            when (val newView = letterViewMap[currentLetter]) {
                is TextView -> newView.setTextColor(
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        arch.cayenne.lib.common.R.color.brand_color
                    )
                )

                is ImageView -> newView.setImageResource(R.drawable.ic_hot_league_index)
            }

            mViewModel.setLastSelectedLetter(currentLetter)
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