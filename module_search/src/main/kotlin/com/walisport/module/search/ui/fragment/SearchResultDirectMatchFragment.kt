package com.walisport.module.search.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
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
import com.walisport.module.search.ui.fragment.SearchDatePickerFragment.Companion.DATE_PICKER_RESULT_END
import com.walisport.module.search.ui.fragment.SearchDatePickerFragment.Companion.DATE_PICKER_RESULT_KEY
import com.walisport.module.search.ui.fragment.SearchDatePickerFragment.Companion.DATE_PICKER_RESULT_START
import com.walisport.module.search.ui.fragment.SearchDatePickerFragment.Companion.DATE_PICKER_RESULT_TIME_IN_MILLIS
import com.walisport.module.search.ui.viewmodel.SearchResultDirectMatchViewModel
import com.walisport.module.search.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.reflect.KClass

class SearchResultDirectMatchFragment :
    BaseFragment<SearchResultDirectMatchViewModel, FragmentSearchResultDirectMatchBinding>() {
    override val vbClass: KClass<FragmentSearchResultDirectMatchBinding>
        get() = FragmentSearchResultDirectMatchBinding::class
    override val vmClass: KClass<SearchResultDirectMatchViewModel>
        get() = SearchResultDirectMatchViewModel::class

    private val sharedViewModel: SearchViewModel by sharedViewModel<SearchViewModel, SearchFragment>()
    private val args: SearchResultDirectMatchFragmentArgs by navArgs()

    private val linearAdapter by lazy {
        SearchResultRaceAdapter().apply {
            onBetClick = { match ->
                navigateTo(
                    SearchNavigationEvent.ToLiveFragment(
                        "walisport://module_live/liveFragment?matchId=${match.matchId}&sportId=${match.basicInfo.sportId}"
                    )
                )
            }
            onFavoriteClick = { match ->
                lifecycleScope.launch {
                    val success = if (match.collect) {
                        mViewModel.removeCollect(match.matchId)
                    } else {
                        mViewModel.addCollect(match.matchId)
                    }

                    if (success) {
                        this@apply.updateFavoriteStatus(
                            match.matchId, !match.collect
                        )
                    }
                }
            }
        }
    }

    enum class RaceViewState {
        Loading, Empty, Success
    }

    override fun initView(savedInstanceState: Bundle?) {
        setEmptyView()
        setRaceView()

        with(mBinding) {
            clDate.clickNoRepeat {
                openDatePicker()
            }
            tvDate.text = getString(R.string.search_date_hint)
        }
    }

    override fun initData() {
        super.initData()
        args.data?.let { data ->
            mViewModel.getSearchResult(data)
        }
        args.id?.let { id ->
            args.type.let { type ->
                mViewModel.getSearchResult(id, type)
            }
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
                                SkinnableResourceManager.getColor(
                                    requireContext(),
                                    R.color.search_result_date
                                )
                            else
                                SkinnableResourceManager.getColor(
                                    requireContext(),
                                    R.color.search_result_date_selected
                                )
                        )
                    }
                    mBinding.ivDateArrow.imageTintList =
                        if (date == null)
                            SkinnableResourceManager.getColorStateList(
                                requireContext(),
                                R.color.search_result_date
                            )
                        else
                            SkinnableResourceManager.getColorStateList(
                                requireContext(),
                                R.color.search_result_date_selected
                            )
                }
            }

            viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    updateResultBackground(mViewModel.tempBackgroundColor)
                }

                override fun onStop(owner: LifecycleOwner) {
                    updateResultBackground(null)
                }
            })
        }
    }

    override fun onDestroyView() {
        mBinding.recyclerView.adapter = null
        super.onDestroyView()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        sharedViewModel.setStatusBarState(hidden)
        super.onHiddenChanged(hidden)
    }

    private fun setEmptyView() {
        with(mBinding) {
            dynamicState.setState(
                DynamicStateLayout.States.DATA_EMPTY,
                ContextCompat.getString(requireContext(), R.string.no_search_result)
            )
        }
    }

    private fun setRaceView() {
        with(mBinding) {
            recyclerView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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
        }
    }

    private fun openDatePicker() {
        val oldDate = mViewModel.getSelectedDate()

        childFragmentManager.setFragmentResultListener(DATE_PICKER_RESULT_KEY, viewLifecycleOwner) { _, bundle ->
            childFragmentManager.clearFragmentResultListener(DATE_PICKER_RESULT_KEY)

            setTitleBarMask(false)
            setDateBarStatus(false)

            val newDate =
                bundle.getLong(DATE_PICKER_RESULT_TIME_IN_MILLIS)
                    .takeIf { bundle.containsKey(DATE_PICKER_RESULT_TIME_IN_MILLIS) }
                    ?.let { Date(it) }
            mViewModel.setSelectedDate(newDate)

            if (oldDate != newDate) {
                mViewModel.directMatchType?.let { type ->
                    mViewModel.getSearchResult(
                        mViewModel.directMatchId.toString(),
                        type,
                        bundle.getLong(DATE_PICKER_RESULT_START),
                        bundle.getLong(DATE_PICKER_RESULT_END)
                    )
                }
            }
        }

        val marginTop = mBinding.clBasicInfo.height + mBinding.clDate.height
        val datePicker = SearchDatePickerFragment.newInstance(
            marginTop, 8.dp2px, 8.dp2px, mViewModel.getSelectedDate()?.time
        )

        datePicker.show(childFragmentManager, mBinding.clRoot.id)
        setTitleBarMask(true) {
            datePicker.close()
        }
        setDateBarStatus(true)
    }

    private fun setDateBarStatus(isOpen: Boolean) {
        with(mBinding) {
            ivDateArrow.rotation =
                if (isOpen) 180f else 0f
            clDate.background =
                SkinnableResourceManager.getDrawable(
                    requireContext(),
                    if (isOpen) R.drawable.shape_search_result_date_btn_bg_opened
                    else R.drawable.shape_search_result_direct_item_bg
                )
        }
    }

    private fun switchUI(state: RaceViewState) {
        with(mBinding) {
            loadingView.visibility = if (state == RaceViewState.Loading) View.VISIBLE else View.GONE
            dynamicState.visibility = if (state == RaceViewState.Empty) View.VISIBLE else View.GONE
            recyclerView.visibility =
                if (state == RaceViewState.Success) View.VISIBLE else View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateDirectInfo(data: SearchResultBaseBean) {
        with(mBinding) {
            val isPlayer = data is SearchResultPlayerBean
            when (data) {
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
                    if (isPlayer) R.drawable.ic_search_result_player_placeholder
                    else R.drawable.ic_search_result_placeholder
                )
                .into(
                    if (isPlayer) ivPlayer
                    else ivIcon
                )

            ivPlayer.visibility = if (isPlayer) View.VISIBLE else View.GONE
            ivIcon.visibility = if (!isPlayer) View.VISIBLE else View.GONE

            updateResultBackground(
                if (data.color?.isNotEmpty() == true) Color.parseColor(data.color)
                else ContextCompat.getColor(
                    requireContext(),
                    R.color.search_result_default_gradient_start
                )
            )
        }
    }

    private fun navigateTo(event: SearchNavigationEvent) {
        sharedViewModel.setNavigationEvent(event)
    }

    private fun updateResultBackground(color: Int? = null) {
        sharedViewModel.setResultBackgroundColor(color)
        if (color != null)
            mViewModel.setTempBackgroundColor(color)
        setStatusBarState(color == null)
    }

    private fun setStatusBarState(isEnabled: Boolean) {
        sharedViewModel.setStatusBarState(isEnabled)
    }

    private fun setTitleBarMask(isEnabled: Boolean, onClick: (() -> Unit)? = null) {
        sharedViewModel.setTitleBarMaskEvent(isEnabled, onClick)
    }
}