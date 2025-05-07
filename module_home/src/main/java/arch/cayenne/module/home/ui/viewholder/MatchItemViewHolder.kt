package arch.cayenne.module.home.ui.viewholder

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
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
import arch.cayenne.module.home.ui.adapter.MatchItemAdapter
import arch.cayenne.module.home.ui.adapter.OddsColumnAdapter
import com.bumptech.glide.Glide

class MatchItemViewHolder(
    private val mBinding: ItemMatchCardBinding,
    private val onMatchItemClickListener: MatchItemAdapter.OnMatchItemClickListener?
) : BaseViewHolder(mBinding) {
    private lateinit var oddsColumnAdapter: OddsColumnAdapter

    fun init(data: MatchWithMarkets) {
        oddsColumnAdapter = OddsColumnAdapter { selection, _ ->
            onMatchItemClickListener?.onOddsCellClick(data, selection)
        }
        with(mBinding) {
            val basicInfo = data.match.basicInfo
            val liveInfo = data.match.liveInfo

            //賽事資訊
            Glide.with(binding.root).load(basicInfo.tournamentIcon).into(ivTournamentIcon)
//            val iconUrl = basicInfo.tournamentIcon
//            Glide.with(binding.root)
//                .load(if (iconUrl.isEmpty()) R.drawable.ic_default_tournament else iconUrl)
//                .placeholder(R.drawable.ic_default_tournament) // 載入中預設圖
//                .error(R.drawable.ic_default_tournament)       // 載入失敗預設圖
//                .into(ivTournamentIcon)
            tvTournamentName.text = basicInfo.tournamentName
            //TODO 階段與時間待確認
            if (basicInfo.status == 4) {
                tvGameStatus.text = basicInfo.startTime.toLocalDateTimeString()
                tvGameTime.visibility = TextView.GONE
            } else {
                tvGameStatus.text = liveInfo.period
                tvGameTime.visibility = TextView.VISIBLE
                tvGameTime.text = liveInfo.clock.toMinuteSecondFormat()
            }

            Glide.with(binding.root).load(basicInfo.awayTeamIcon)
                .error(arch.cayenne.lib.res.R.color.color_333A45).into(ivAwayIcon)
            tvAwayName.text = basicInfo.awayTeam.limitTitleLength()
            tvAwayScore.text = liveInfo.score.getAwayScore()

            Glide.with(binding.root).load(basicInfo.homeTeamIcon)
                .error(arch.cayenne.lib.res.R.color.color_333A45).into(ivHomeIcon)
            tvHomeName.text = basicInfo.homeTeam.limitTitleLength()
            tvHomeScore.text = liveInfo.score.getHomeScore()
            tvWatchCount.text = liveInfo.viewerCount.toString()
            ivFavorite.isSelected = data.match.collect
            val defaultTitleList = listOf(
                R.string.match_title_win,
                R.string.match_title_handicap,
                R.string.match_title_over_under
            )
            layoutOddsTitle.columnCount = defaultTitleList.size
            defaultTitleList.forEachIndexed { index, title ->
                val titleView = TextView(binding.root.context).apply {
                    text = getString(title)
                    setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            R.color.secondary_title
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

            val selectionsGrouped = data.markets.map { it.market to it.selections }
            oddsColumnAdapter.submitList(selectionsGrouped)
        }
    }

    fun bindPayload(item: MatchWithMarkets, payloads: List<Any>) {
        val changes = payloads.firstOrNull() as? Set<*> ?: return
        with(mBinding) {
            val basicInfo = item.match.basicInfo
            val liveInfo = item.match.liveInfo

            if ("status" in changes) {
                if (basicInfo.status == 4) {
                    tvGameStatus.text = basicInfo.startTime.toLocalDateTimeString()
                    tvGameTime.visibility = TextView.GONE
                } else {
                    tvGameStatus.text = liveInfo.period
                    tvGameTime.visibility = TextView.VISIBLE
                    tvGameTime.text = liveInfo.clock.toMinuteSecondFormat()
                }
            }

            if ("clock" in changes) {
                tvGameTime.text = liveInfo.clock.toMinuteSecondFormat()
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
}