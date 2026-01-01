package arch.cayenne.module.home.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Rect
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.SportStringExt.limitTitleLength
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.toLocalDateTimeString
import arch.cayenne.lib.common.utils.ext.toMinuteSecondFormat
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.skin.widget.SkinnableTextView
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.SportType
import arch.cayenne.module.home.databinding.ItemMatchCardBinding
import arch.cayenne.module.home.utils.setFavoriteIcon
import com.bumptech.glide.Glide

class MatchItemViewHolder(
    private val mBinding: ItemMatchCardBinding,
    private val onMatchItemClickListener: OnMatchItemClickListener?,
    private val playType: Int,
    private val viewPool: RecycledViewPool
) : BaseViewHolder(mBinding) {
    private val oddsColumnAdapter: OddsColumnAdapter  by lazy { OddsColumnAdapter(onMatchItemClickListener) }

    init {
        with(mBinding) {
            rvOddsGrid.itemAnimator = null
            rvOddsGrid.apply {
                setRecycledViewPool(viewPool)
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
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

            val drawable =
                ContextCompat.getDrawable(root.context, R.drawable.layer_favorite_crossfade)
                    ?.mutate()
            ivFavorite.setImageDrawable(drawable)
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(data: MatchWithMarkets) {
        with(mBinding) {
            val basicInfo = data.match.basicInfo
            val liveInfo = data.match.liveInfo

            val selectionsGrouped = data.markets.map { it.market to it.selections }
            oddsColumnAdapter.submitList(selectionsGrouped)

            if (playType == PlayType.FAVORITE.id) {
                //收藏界面里的联赛图标换为球类图标
                ivTournamentIcon.setImageDrawable(
                    SportType.fromId(
                        data.match.basicInfo.sportId
                    )?.iconResActive?.getDrawable()
                )
            } else {
                //賽事資訊
                setIconWithDefault(
                    basicInfo.tournamentIcon,
                    R.drawable.ic_default_tournament,
                    ivTournamentIcon
                )
            }
            tvTournamentName.text = basicInfo.tournamentName

            if (basicInfo.status == 5) {  //開賽中
                tvRoll.visibility = View.GONE
                tvGameStatus.visibility = View.VISIBLE
                tvGameStatus.text = liveInfo.period
                tvGameTime.text = liveClock(liveInfo.clock, liveInfo.clockModified)
                tvGameTime.visibility = if (liveInfo.rollClock) View.VISIBLE else View.GONE
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
            ivLiveAnimation.setImageResource(
                if (liveInfo.liveAnimation.isBlank()) R.drawable.ic_live_animation_disabled else R.drawable.ic_live_animation
            )
            ivLiveVideo.setImageResource(
                if (liveInfo.liveVideo) R.drawable.ic_live_video else R.drawable.ic_live_video_disabled
            )

            mBinding.ivFavorite.setFavoriteIcon(data.match.collect, true)

            if (basicInfo.status == 5) {
                tvAwayScore.text = liveInfo.awayScore.toString()
                tvHomeScore.text = liveInfo.homeScore.toString()
            } else {
                tvAwayScore.text = ""
                tvHomeScore.text = ""
            }

            root.setOnClickListener {
                onMatchItemClickListener?.onLiveEntryClick(data)
            }

            ivFavorite.apply { addScaleOnTouchAnimation() }.setOnClickListener {
                onMatchItemClickListener?.onFavoriteClick(ivFavorite, data)
            }
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

            ivFavorite.apply { addScaleOnTouchAnimation() }.setOnClickListener {
                onMatchItemClickListener?.onFavoriteClick(ivFavorite, item)
            }

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
            if ("rollClock" in changes) {
                tvGameTime.visibility = if (liveInfo.rollClock) View.VISIBLE else View.GONE
            }
            if ("score" in changes) {
                if (basicInfo.status == 5) {
                    tvAwayScore.text = liveInfo.awayScore.toString()
                    tvHomeScore.text = liveInfo.homeScore.toString()
                } else {
                    tvAwayScore.text = ""
                    tvHomeScore.text = ""
                }
            }

            if ("viewerCount" in changes) {
                tvWatchCount.text = liveInfo.viewerCount.toString()
            }

            if ("liveAnimation" in changes) {
                ivLiveAnimation.setImageResource(
                    if (liveInfo.liveAnimation.isBlank()) R.drawable.ic_live_animation_disabled else R.drawable.ic_live_animation
                )
            }

            if ("liveVideo" in changes) {
                ivLiveVideo.setImageResource(
                    if (liveInfo.liveVideo) R.drawable.ic_live_video else R.drawable.ic_live_video_disabled
                )
            }
            if ("odds" in changes) {
                val selectionsGrouped = item.markets.map { it.market to it.selections }
                oddsColumnAdapter.submitList(selectionsGrouped)
            }
            if ("collect" in changes) {
                if (mBinding.ivFavorite.isSelected == item.match.collect) return
                mBinding.ivFavorite.setFavoriteIcon(item.match.collect, false)
            }
        }
    }

    private fun liveClock(clock: Int, modified: Long): String =
        (clock).toMinuteSecondFormat()
}