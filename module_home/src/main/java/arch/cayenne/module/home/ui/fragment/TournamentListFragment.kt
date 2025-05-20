package arch.cayenne.module.home.ui.fragment

import android.graphics.PointF
import android.icu.text.Transliterator
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.databinding.FragmentTournamentListBinding
import arch.cayenne.module.home.ui.adapter.TournamentSectionAdapter
import arch.cayenne.module.home.ui.viewmodel.HomeViewModel
import kotlin.reflect.KClass

class TournamentListFragment : BaseFragment<HomeViewModel, FragmentTournamentListBinding>() {

    override val vbClass: KClass<FragmentTournamentListBinding> =
        FragmentTournamentListBinding::class
    override val vmClass: KClass<HomeViewModel> = HomeViewModel::class

    //    private var dropdownListener: TournamentSectionView.OnChampionDropdownListener? = null
    private val homeViewModel: HomeViewModel by sharedViewModel<HomeViewModel, NewHomeFragment>()
    private lateinit var adapter: TournamentSectionAdapter
    private val letterPositionMap = mutableMapOf<Char, Int>()

    private var onReadyCallback: (() -> Unit)? = null

    fun setOnReadyCallback(callback: () -> Unit) {
        onReadyCallback = callback
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            rvTournamentList.layoutManager = LinearLayoutManager(context)
            root.doOnPreDraw {
                onReadyCallback?.invoke()
            }
            ivHomeLeagueCollapse.setOnClickListener {
                homeViewModel.requestCollapseTournamentDropdown()
            }
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
        homeViewModel.allTournaments.observe(viewLifecycleOwner) { list ->
            "observe tournaments:$list".logd()
            if (!list.isNullOrEmpty()) {
                setTournamentList(list)
            }
        }
    }

    private fun setTournamentList(tournaments: List<TournamentDataModel>) {
        if (!::adapter.isInitialized) {
            adapter = TournamentSectionAdapter { tournamentId ->
//                onTournamentClick?.invoke(tournamentId)
                mViewModel.selectTournament(tournamentId)
                homeViewModel.setShowAllTournaments(false)
                homeViewModel.requestCollapseTournamentDropdown()
            }
            mBinding.rvTournamentList.adapter = adapter
        }

        val transliterator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Transliterator.getInstance("Han-Latin/Names; Latin-ASCII")
        } else {
            TODO("VERSION.SDK_INT < Q")
        }
        val groupedMap = mutableMapOf<Char, MutableList<TournamentDataModel>>()
        val hotList = mutableListOf<TournamentDataModel>()

        tournaments.forEach { tournament ->
            val pinyin = transliterator.transliterate(tournament.name).trim()
            val firstChar = pinyin.firstOrNull()?.uppercaseChar()
            val groupKey = if (firstChar != null && firstChar in 'A'..'Z') firstChar else '#'

            // 歸類進字母列表
            groupedMap.getOrPut(groupKey) { mutableListOf() }.add(tournament)
            // 歸類進熱門列表
            if (tournament.hot) {
                hotList.add(tournament)
            }
        }

        // 將熱門歸類進 '#' 區塊
        if (hotList.isNotEmpty()) {
            groupedMap['#'] = hotList
        }

        val displayList = mutableListOf<TournamentListItem>()
        letterPositionMap.clear()

        groupedMap.toSortedMap().forEach { (letter, list) ->
            letterPositionMap[letter] = displayList.size
            displayList.add(TournamentListItem.Header(letter))
            displayList.addAll(list.map { TournamentListItem.TournamentItem(it) })
        }

        adapter.submitList(displayList)
        setupAZIndex()
    }

    private fun setupAZIndex() {
        mBinding.llIndexContainer.removeAllViews()

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        val hotIcon = ImageView(context).apply {
            setImageResource(R.drawable.ic_hot_league_index)
            layoutParams = LinearLayout.LayoutParams(20.dp2px, 18.dp2px)
            setOnClickListener { scrollToSection('#') }
        }
        container.addView(hotIcon)

        ('A'..'Z').forEach { letter ->
            if (letterPositionMap.containsKey(letter)) {
                val tv = TextView(context).apply {
                    text = letter.toString()
                    textSize = 11f
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(20.dp2px, 18.dp2px)
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.brand_color
                        )
                    ) // 非 stateList
                    setOnClickListener { scrollToSection(letter) }
                }
                container.addView(tv)
            }
        }
        container.isClickable = true
        container.isFocusable = true

        mBinding.llIndexContainer.addView(container)
    }

    private fun scrollToSection(letter: Char) {
        val position = letterPositionMap[letter] ?: return
        val layoutManager =
            mBinding.rvTournamentList.layoutManager as? LinearLayoutManager ?: return

        val scroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return layoutManager.computeScrollVectorForPosition(targetPosition)
            }
        }
        scroller.targetPosition = position
        layoutManager.startSmoothScroll(scroller)
    }

    companion object {
        fun newInstance(): TournamentListFragment {
            return TournamentListFragment()
        }
    }
}