package arch.cayenne.module.home.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Rect
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportStringExt.limitTitleLength
import arch.cayenne.lib.common.utils.ext.toLocalDateTimeString
import arch.cayenne.lib.common.utils.ext.toMinuteSecondFormat
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.skin.widget.SkinnableTextView
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.ItemMatchCardBinding
import com.bumptech.glide.Glide

class MatchItemViewHolder(
    private val mBinding: ItemMatchCardBinding,
    private val onMatchItemClickListener: OnMatchItemClickListener?,
    private val viewPool: RecycledViewPool
) : BaseViewHolder(mBinding) {
    private var oddsColumnAdapter: OddsColumnAdapter = OddsColumnAdapter(onMatchItemClickListener)
    init {
        //右半盤口
        val defaultTitleList = listOf(
            R.string.match_title_win,
            R.string.match_title_handicap,
            R.string.match_title_over_under
        )
        with(mBinding) {
            defaultTitleList.forEach { title ->
                val titleView = SkinnableTextView(root.context).apply {
                    text = getString(title)
                    setTextColorRes(R.color.home_secondary_text)
                    textSize = 13f
                    setPadding(0, 0, 2.dp2px, 0)
                    maxLines = 1
                    ellipsize = TextUtils.TruncateAt.END
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
                }
                layoutOddsTitle.addView(titleView)
            }

            rvOddsGrid.itemAnimator = null
            rvOddsGrid.apply {
                setRecycledViewPool(viewPool)
                setHasFixedSize(true)
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

            //主隊
            setIconWithDefault(basicInfo.homeTeamIcon, R.drawable.ic_default_team, ivHomeIcon)
            tvHomeName.text = basicInfo.homeTeam.limitTitleLength()
            tvWatchCount.text = liveInfo.viewerCount.toString()
            ivFavorite.isSelected = data.match.collect

            if (basicInfo.status == 5) {
                tvAwayScore.text = liveInfo.homeScore.toString()
                tvHomeScore.text = liveInfo.awayScore.toString()
            } else {
                tvAwayScore.text = ""
                tvHomeScore.text = ""
            }

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
                if (basicInfo.status == 5) {
                    tvAwayScore.text = liveInfo.homeScore.toString()
                    tvHomeScore.text = liveInfo.awayScore.toString()
                } else {
                    tvAwayScore.text = ""
                    tvHomeScore.text = ""
                }
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