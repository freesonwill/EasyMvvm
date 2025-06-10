package arch.cayenne.module.home.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportStringExt.getAwayScore
import arch.cayenne.lib.common.utils.ext.SportStringExt.getHomeScore
import arch.cayenne.lib.common.utils.ext.SportStringExt.limitTitleLength
import arch.cayenne.lib.common.utils.ext.toLocalDateTimeString
import arch.cayenne.lib.common.utils.ext.toMinuteSecondFormat
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.ItemMatchCardBinding
import com.bumptech.glide.Glide

class MatchItemViewHolder(
    private val mBinding: ItemMatchCardBinding,
    private val onMatchItemClickListener: OnMatchItemClickListener?
) : BaseViewHolder(mBinding) {
    private var oddsColumnAdapter: OddsColumnAdapter = OddsColumnAdapter(onMatchItemClickListener)
    private val viewPool = RecyclerView.RecycledViewPool()
    init {
        //右半盤口
        val defaultTitleList = listOf(
            R.string.match_title_win,
            R.string.match_title_handicap,
            R.string.match_title_over_under
        )
        with(mBinding) {
            layoutOddsTitle.columnCount = defaultTitleList.size
            defaultTitleList.forEachIndexed { index, title ->
                val titleView = TextView(binding.root.context).apply {
                    text = getString(title)
                    setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            arch.cayenne.lib.common.R.color.secondary_title
                        )
                    )
                    textSize = 13f
                    setPadding(5, 4, 5, 4)
                }
                val lp = GridLayout.LayoutParams().apply {
                    width = 0
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(index, 1f)
                    setMargins(3.dp2px, 0, 0, 0)
                }

                layoutOddsTitle.addView(titleView, lp)
            }

            rvOddsGrid.apply {
                layoutManager = GridLayoutManager(root.context, 3)
                adapter = oddsColumnAdapter

                val spacing = 2.dp2px
                if (itemDecorationCount > 0) {
                    removeItemDecorationAt(0)
                }
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        if (position == RecyclerView.NO_POSITION) return

                        val column = position % 3
                        outRect.left = spacing / 2
                        outRect.right = spacing / 2
                    }
                })
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun init(data: MatchWithMarkets) {
//        oddsColumnAdapter.onOddsClick = { selection, b ->
//            onMatchItemClickListener?.onOddsCellClick(data, selection)
//        }
        with(mBinding) {
            val basicInfo = data.match.basicInfo
            val liveInfo = data.match.liveInfo

            //賽事資訊
            setIconWithDefault(
                basicInfo.tournamentIcon,
                R.drawable.ic_default_tournament,
                ivTournamentIcon
            )
            tvTournamentName.text = basicInfo.tournamentName

            if (basicInfo.status == 5) {  //開賽中
                tvRoll.visibility = View.VISIBLE
                tvGameStatus.visibility = View.VISIBLE
                tvGameStatus.text = liveInfo.period
                tvGameTime.text = liveClock(liveInfo.clock, liveInfo.clockModified)
            } else {
                tvRoll.visibility = View.GONE
                tvGameStatus.visibility = View.GONE
                tvGameTime.text = basicInfo.startTime.toLocalDateTimeString()
            }

            //客隊
            setIconWithDefault(basicInfo.awayTeamIcon, R.drawable.ic_default_team, ivAwayIcon)
            tvAwayName.text = basicInfo.awayTeam.limitTitleLength()
            tvAwayScore.text = liveInfo.score.getAwayScore()

            //主隊
            setIconWithDefault(basicInfo.homeTeamIcon, R.drawable.ic_default_team, ivHomeIcon)
            tvHomeName.text = basicInfo.homeTeam.limitTitleLength()
            tvHomeScore.text = liveInfo.score.getHomeScore()
            tvWatchCount.text = liveInfo.viewerCount.toString()
            ivFavorite.isSelected = data.match.collect

            rvOddsGrid.setRecycledViewPool(viewPool)

            val selectionsGrouped = data.markets.map { it.market to it.selections }
            oddsColumnAdapter.submitList(selectionsGrouped)
        }
    }

    private fun setIconWithDefault(tournamentIcon: String, defaultIcon: Int, view: ImageView) {
        Glide.with(binding.root)
            .load(tournamentIcon.ifEmpty { defaultIcon })
            .placeholder(defaultIcon) // 載入中預設圖
            .error(defaultIcon)       // 載入失敗預設圖
            .into(view)
    }

    @SuppressLint("SetTextI18n")
    fun bindPayload(item: MatchWithMarkets, payloads: List<Any>) {
        val changes = payloads.firstOrNull() as? Set<*> ?: return
        with(mBinding) {
            val basicInfo = item.match.basicInfo
            val liveInfo = item.match.liveInfo

//            oddsColumnAdapter.onOddsClick = { selection, _ ->
//                onMatchItemClickListener?.onOddsCellClick(item, selection)
//            }

            if ("status" in changes) {
                if (basicInfo.status == 5) {  //開賽中
                    tvGameStatus.visibility = View.VISIBLE
                    tvGameStatus.text = liveInfo.period
                } else {
                    tvGameStatus.visibility = View.GONE
                    tvGameTime.text = basicInfo.startTime.toLocalDateTimeString()
                }
            }

            if ("clock" in changes) {
                tvGameTime.text = liveClock(liveInfo.clock, liveInfo.clockModified)
            }
            if ("score" in changes) {
                tvAwayScore.text = liveInfo.score.getAwayScore()
                tvHomeScore.text = liveInfo.score.getHomeScore()
            }
            if ("viewerCount" in changes) {
                tvWatchCount.text = liveInfo.viewerCount.toString()
            }
            if ("odds" in changes) {
                val selectionsGrouped = item.markets.map { it.market to it.selections }
                oddsColumnAdapter.submitList(selectionsGrouped)
            }
            if ("collect" in changes) {
                ivFavorite.isSelected = item.match.collect
            }
        }
    }

    private fun liveClock(clock: Int, modified: Long) : String =
        (clock).toMinuteSecondFormat()
}