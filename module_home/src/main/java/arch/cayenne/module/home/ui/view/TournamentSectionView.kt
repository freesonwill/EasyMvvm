package arch.cayenne.module.home.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.res.R
import arch.cayenne.lib.skin.widget.SportImageView
import arch.cayenne.lib.skin.widget.SportTextView
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

    var isExpanded: Boolean = true
        private set

    init {
        binding.rvTournamentList.layoutManager = LinearLayoutManager(context)
    }

    fun toggleVisibility(expand: Boolean) {
        isExpanded = expand
        binding.rvTournamentList.visibility = if (expand) View.VISIBLE else View.GONE
        binding.llIndexContainer.visibility = if (expand) View.VISIBLE else View.GONE
    }

    fun setTournamentList(tournaments: List<TournamentDataModel>) {
        "joseph sectionView setTournamentList: ${tournaments.size}".logd()
        adapter = TournamentSectionAdapter()
        binding.rvTournamentList.adapter = adapter

        val grouped = tournaments.groupBy {
            it.name.firstOrNull()?.uppercaseChar()?.takeIf { ch -> ch in 'A'..'Z' } ?: '#'
        }.toSortedMap()

        val displayList = mutableListOf<TournamentListItem>()
        grouped.forEach { (letter, list) ->
            letterPositionMap[letter] = displayList.size
            displayList.add(TournamentListItem.Header(letter))
            displayList.addAll(list.map { TournamentListItem.TournamentItem(it) })
        }

        adapter.submitList(displayList)

        setupAZIndex()
    }

    private fun setupAZIndex() {
        binding.llIndexContainer.removeAllViews()

        // 熱門聯賽索引（ImageView）
        val hotIcon = SportImageView(context).apply {
            setImageResource(arch.cayenne.module.home.R.drawable.ic_hot_league_index)
            layoutParams = ViewGroup.LayoutParams(20.dp2px, 18.dp2px)
            setOnClickListener {
                letterPositionMap['#']?.let {
                    (binding.rvTournamentList.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                        it,
                        0
                    )
                }
            }
        }
        binding.llIndexContainer.addView(hotIcon)

        // A-Z 快捷索引
        ('A'..'Z').forEach { letter ->
            val tv = SportTextView(context).apply {
                text = letter.toString()
                textSize = 11f
                setTextColor(
                    ContextCompat.getColorStateList(
                        context,
                        R.color.brand_color
                    )
                )
                gravity = Gravity.CENTER
                layoutParams = ViewGroup.LayoutParams(20.dp2px, 18.dp2px)
                setOnClickListener {
                    letterPositionMap[letter]?.let {
                        (binding.rvTournamentList.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                            it,
                            0
                        )
                    }
                }
                setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        arch.cayenne.module.home.R.color.bg_card
                    )
                )
            }
            binding.llIndexContainer.addView(tv)
        }
    }

}