package arch.cayenne.module.home.ui.view

import android.content.Context
import android.graphics.PointF
import android.icu.text.Transliterator
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.databinding.ViewTournamentSectionBinding
import arch.cayenne.module.home.ui.adapter.TournamentSectionAdapter

class TournamentSectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding =
        ViewTournamentSectionBinding.inflate(LayoutInflater.from(context), this, true)
    private lateinit var adapter: TournamentSectionAdapter
    private val letterPositionMap = mutableMapOf<Char, Int>()

    var onCollapseClick: (() -> Unit)? = null
    var onTournamentClick: ((Int) -> Unit)? = null

    init {
        binding.rvTournamentList.layoutManager = LinearLayoutManager(context)
        binding.ivHomeLeagueCollapse.setOnClickListener {
            onCollapseClick?.invoke()
        }
    }

    private fun setTournamentList(tournaments: List<TournamentDataModel>) {
        if (!::adapter.isInitialized) {
            adapter = TournamentSectionAdapter { tournamentId ->
                onTournamentClick?.invoke(tournamentId)
            }
            binding.rvTournamentList.adapter = adapter
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
        binding.llIndexContainer.removeAllViews()

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

        binding.llIndexContainer.addView(container)
    }

    private fun scrollToSection(letter: Char) {
        val position = letterPositionMap[letter] ?: return
        val layoutManager = binding.rvTournamentList.layoutManager as? LinearLayoutManager ?: return

        val scroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return layoutManager.computeScrollVectorForPosition(targetPosition)
            }
        }
        scroller.targetPosition = position
        layoutManager.startSmoothScroll(scroller)
    }

    fun postSetTournamentList(tournaments: List<TournamentDataModel>) {
        binding.rvTournamentList.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.rvTournamentList.viewTreeObserver.removeOnGlobalLayoutListener(this)
                setTournamentList(tournaments)
            }
        })
    }

    interface OnChampionDropdownListener {
        fun onRequestCollapseChampion()
    }
}