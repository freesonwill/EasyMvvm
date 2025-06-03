package com.walisport.module.search.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.bumptech.glide.Glide
import com.walisport.module.search.R
import com.walisport.module.search.data.model.SearchResultBaseBean
import com.walisport.module.search.data.model.SearchResultPlayerBean
import com.walisport.module.search.data.model.SearchResultTeamBean
import com.walisport.module.search.data.model.SearchResultTournamentBean
import com.walisport.module.search.databinding.FragmentSearchResultDirectMatchBinding
import com.walisport.module.search.ui.adapter.SearchResultRaceAdapter
import com.walisport.module.search.ui.viewmodel.SearchResultViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.reflect.KClass

class SearchResultDirectMatchFragment :
    BaseFragment<SearchResultViewModel, FragmentSearchResultDirectMatchBinding>() {
    override val vbClass: KClass<FragmentSearchResultDirectMatchBinding>
        get() = FragmentSearchResultDirectMatchBinding::class
    override val vmClass: KClass<SearchResultViewModel>
        get() = SearchResultViewModel::class

    private val linearAdapter by lazy {
        SearchResultRaceAdapter().apply {
            onBetClick = { match ->
                navigate(Uri.parse("walisport://module_live/liveFragment?matchId=${match.matchId}&sportId=${match.basicInfo.sportId}"))
            }
        }
    }

    override fun createVM(): SearchResultViewModel {
        return activityViewModel<SearchResultViewModel>().value
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = linearAdapter
            }
            clDate.clickNoRepeat {
                setCalendarState(clCalendar.visibility != View.VISIBLE)
            }
            calendarView.apply {
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
                Calendar.getInstance().apply {
                    timeInMillis = mViewModel.selectedDateFlow.value.time
                }.apply {
                    calendarView.scrollToCalendar(
                        get(Calendar.YEAR),
                        get(Calendar.MONTH) + 1,
                        get(Calendar.DAY_OF_MONTH),
                        true
                    )
                }
            }
            tvConfirm.clickNoRepeat {
                mViewModel.setSelectedDate(Date(calendarView.selectedCalendar.timeInMillis))
                setCalendarState(clCalendar.visibility != View.VISIBLE)
            }
        }
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

    @SuppressLint("SetTextI18n")
    private suspend fun updateDirectInfo(data: SearchResultBaseBean) {
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
                            data.tournamentShortName,
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

    override fun initListener() {
    }

    override fun createObserver() {
        with(mViewModel) {
            lifecycleScope.launch {
                directData.collect { data ->
                    data?.let { updateDirectInfo(it) }
                }
            }

            lifecycleScope.launch {
                combineResult.collect { combineResult ->
                    linearAdapter.submitList(combineResult)
                }
            }
        }
    }
}