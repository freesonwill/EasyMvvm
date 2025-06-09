package com.walisport.module.search.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.bumptech.glide.Glide
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchNavigationEvent
import com.walisport.module.search.data.model.SearchResultBaseBean
import com.walisport.module.search.data.model.SearchResultPlayerBean
import com.walisport.module.search.data.model.SearchResultTeamBean
import com.walisport.module.search.data.model.SearchResultTournamentBean
import com.walisport.module.search.databinding.FragmentSearchResultDirectMatchBinding
import com.walisport.module.search.ui.adapter.SearchResultRaceAdapter
import com.walisport.module.search.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.reflect.KClass

class SearchResultDirectMatchFragment :
    BaseFragment<SearchViewModel, FragmentSearchResultDirectMatchBinding>() {
    override val vbClass: KClass<FragmentSearchResultDirectMatchBinding>
        get() = FragmentSearchResultDirectMatchBinding::class
    override val vmClass: KClass<SearchViewModel>
        get() = SearchViewModel::class

    private val linearAdapter by lazy {
        SearchResultRaceAdapter().apply {
            onBetClick = { match ->
                mViewModel.navigateTo(
                    SearchNavigationEvent.ToLiveFragment(
                        "walisport://module_live/liveFragment?matchId=${match.matchId}&sportId=${match.basicInfo.sportId}"
                    )
                )
            }
        }
    }

    enum class RaceViewState {
        Loading, Empty, Success
    }

    override fun createVM(): SearchViewModel {
        return activityViewModel<SearchViewModel>().value
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            dynamicState.setState(
                DynamicStateLayout.States.DATA_EMPTY,
                ContextCompat.getString(requireContext(), R.string.no_search_result)
            )
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = linearAdapter.apply {
                    if (itemDecorationCount == 0) {
                        addItemDecoration(object : ItemDecoration() {
                            override fun getItemOffsets(
                                outRect: android.graphics.Rect,
                                view: View,
                                parent: RecyclerView,
                                state: RecyclerView.State
                            ) {
                                val position = parent.getChildAdapterPosition(view)
                                if (position == RecyclerView.NO_POSITION) return

                                val currentType = linearAdapter.getItemViewType(position)
                                when (currentType) {
                                    SearchResultRaceAdapter.VIEW_TYPE_HEADER -> {
                                        outRect.set(0, 0, 0, 0)
                                    }

                                    else -> {
                                        val prevType = linearAdapter.getItemViewType(position - 1)
                                        outRect.set(
                                            0,
                                            if (prevType == SearchResultRaceAdapter.VIEW_TYPE_HEADER) 0 else 12.dp2px,
                                            0, 0
                                        )
                                    }
                                }
                            }
                        })
                    }
                }
            }
            clDate.clickNoRepeat {
                setCalendarState(clCalendar.visibility != View.VISIBLE)
            }
            tvDate.text = getString(R.string.search_date_hint)
            calendarView.apply {
                clearSingleSelect()
                setAllMode()
                setOnMonthChangeListener { year, month ->
                    setCalendarTitle(year, month)
                }
                setWeeColor(
                    Color.TRANSPARENT,
                    SkinnableResourceManager.getColor(requireContext(), R.color.search_calendar_week_text_color)
                )
                setTextColor(
                    Color.parseColor("#ff0000"),
                    SkinnableResourceManager.getColor(requireContext(), R.color.search_calendar_current_month_text_color),
                    SkinnableResourceManager.getColor(requireContext(), R.color.search_calendar_other_month_text_color),
                    SkinnableResourceManager.getColor(requireContext(), R.color.search_calendar_current_month_text_color),
                    SkinnableResourceManager.getColor(requireContext(), R.color.search_calendar_other_month_text_color)
                )
                setSelectedColor(
                    SkinnableResourceManager.getColor(requireContext(), R.color.search_calendar_selected_theme_color),
                    Color.WHITE,
                    Color.TRANSPARENT
                )
                setCalendarTitle(curYear, curMonth)
            }
            ivPrevMonth.clickNoRepeat {
                calendarView.scrollToPre(true)
            }
            ivNextMonth.clickNoRepeat {
                calendarView.scrollToNext(true)
            }
            tvReset.clickNoRepeat {
                calendarView.clearSingleSelect()
                setCalendarState(clCalendar.visibility != View.VISIBLE)
                mViewModel.setSelectedDate(null)
                mViewModel.directMatchType?.let { type ->
                    mViewModel.getSearchResult(
                        requireContext(),
                        mViewModel.directMatchId.toString(),
                        type
                    )
                }
            }
            tvConfirm.clickNoRepeat {
                setCalendarState(clCalendar.visibility != View.VISIBLE)
                mViewModel.setSelectedDate(Date(calendarView.selectedCalendar.timeInMillis))
                mViewModel.directMatchType?.let { type ->
                    mViewModel.getSearchResult(
                        requireContext(),
                        mViewModel.directMatchId.toString(),
                        type,
                        calendarView.selectedCalendar.timeInMillis.toDateStartTime(),
                        calendarView.selectedCalendar.timeInMillis.toDateEndTime()
                    )
                }
            }
        }
    }

    private fun Long.toDateStartTime(): Long {
        return Calendar.getInstance().apply {
            timeInMillis = this@toDateStartTime
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun Long.toDateEndTime(): Long {
        return Calendar.getInstance().apply {
            timeInMillis = this@toDateEndTime
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    private fun setCalendarState(isOpen: Boolean = true) {
        with(mBinding) {
            clCalendar.visibility =
                if (!isOpen) View.GONE else View.VISIBLE
            ivDateArrow.rotation =
                if(isOpen) 180f else 0f
            clDate.background =
                SkinnableResourceManager.getDrawable(
                    requireContext(),
                    if (isOpen) R.drawable.shape_search_result_date_btn_bg_opened
                    else R.drawable.shape_search_result_direct_item_bg
                )
        }
    }

    private fun setCalendarTitle(year: Int, month: Int) {
        val monthStr =
            SimpleDateFormat("MMMM", Locale.getDefault())
                .format(
                    Calendar.getInstance(Locale.getDefault()).apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month - 1)
                    }.time
                )
        mBinding.tvCalendarTitle.text =
            requireContext().getString(
                R.string.search_result_race_calendar_title,
                monthStr,
                "$year"
            )
    }

    private fun switchUI(state: RaceViewState) {
        with(mBinding) {
            loadingView.visibility = if(state == RaceViewState.Loading) View.VISIBLE else View.GONE
            dynamicState.visibility = if (state ==  RaceViewState.Empty) View.VISIBLE else View.GONE
            recyclerView.visibility = if (state == RaceViewState.Success) View.VISIBLE else View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateDirectInfo(data: SearchResultBaseBean) {
        with(mBinding) {
            val isPlayer = data is SearchResultPlayerBean
            when(data) {
                is SearchResultTournamentBean -> {
                    tvTitle.text = data.name
                    tvSubTitle.text = data.season
                }
                is SearchResultTeamBean -> {
                    tvTitle.text = data.name
                    tvSubTitle.text =
                        requireContext().getString(
                            R.string.search_result_sub_title_tournament,
                            data.tournamentShortName,
                            data.rank,
                            data.win,
                            data.lose
                        )
                }
                is SearchResultPlayerBean -> {
                    tvTitle.text = data.name
                    tvSubTitle.text =
                        requireContext().getString(
                            R.string.search_result_sub_title_player,
                            data.name,
                            data.teamName,
                            data.number,
                            data.position
                        )
                }
            }

            Glide.with(requireContext())
                .load(data.icon)
                .placeholder(
                    if(isPlayer) R.drawable.ic_search_result_player_placeholder
                    else R.drawable.ic_search_result_placeholder
                )
                .into(
                    if(isPlayer) ivPlayer
                    else ivIcon
                )

            ivPlayer.visibility = if(isPlayer) View.VISIBLE else View.GONE
            ivIcon.visibility = if(!isPlayer) View.VISIBLE else View.GONE

            mViewModel.setGradientBgColor(
                if(data.color?.isNotEmpty() == true) Color.parseColor(data.color)
                else ContextCompat.getColor(requireContext(), R.color.search_result_default_gradient_start)
            )
        }
    }

    override fun initListener() = Unit

    override fun createObserver() {
        with(mViewModel) {
            lifecycleScope.launch {
                directData.collect { data ->
                    data?.let { updateDirectInfo(it) }
                }
            }

            lifecycleScope.launch {
                combineResult.collect { combineResult ->
                    when {
                        combineResult == null -> switchUI(RaceViewState.Loading)
                        combineResult.isEmpty() -> switchUI(RaceViewState.Empty)
                        else -> {
                            linearAdapter.submitList(combineResult) {
                                switchUI(RaceViewState.Success)
                                mBinding.recyclerView.smoothScrollToPosition(0)
                            }
                        }
                    }
                }
            }

            lifecycleScope.launch {
                selectedDateFlow.collect { date ->
                    mBinding.tvDate.apply {
                        text =
                            if (date == null) getString(R.string.search_date_hint)
                            else SimpleDateFormat("MM-dd", Locale.getDefault()).format(date)
                        setTextColor(
                            if (date == null)
                                SkinnableResourceManager.getColor(requireContext(), R.color.search_result_date)
                            else
                                SkinnableResourceManager.getColor(requireContext(), R.color.search_result_date_selected)
                        )
                    }
                    mBinding.ivDateArrow.imageTintList =
                        if(date == null)
                            SkinnableResourceManager.getColorStateList(requireContext(), R.color.search_result_date)
                        else
                            SkinnableResourceManager.getColorStateList(requireContext(), R.color.search_result_date_selected)
                }
            }

            viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) {
                    mViewModel.setGradientBgColor(null)
                    super.onStop(owner)
                }
            })
        }
    }
}