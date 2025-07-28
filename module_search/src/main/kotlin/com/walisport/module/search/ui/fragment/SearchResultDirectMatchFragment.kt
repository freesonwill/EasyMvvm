package com.walisport.module.search.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.bumptech.glide.Glide
import com.walisport.module.search.R
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
import com.walisport.module.search.ui.fragment.SearchResultBaseFragment.Companion.GO_BACK_TO_MAIN
import com.walisport.module.search.ui.viewmodel.SearchResultDirectMatchViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.reflect.KClass
import arch.cayenne.lib.common.R as RC

class SearchResultDirectMatchFragment :
    SearchBaseFragment<SearchResultDirectMatchViewModel, FragmentSearchResultDirectMatchBinding>() {
    override val vmClass: KClass<SearchResultDirectMatchViewModel>
        get() = SearchResultDirectMatchViewModel::class
    override val contentVbClass: KClass<FragmentSearchResultDirectMatchBinding>
        get() = FragmentSearchResultDirectMatchBinding::class

    private val args: SearchResultDirectMatchFragmentArgs by navArgs()

    private val dateHintStr: String
        get() = R.string.search_date_hint.toTranslatedStr()
    private val noDataStr: String
        get() = R.string.search_result_no_data.toTranslatedStr()

    private val linearAdapter by lazy {
        SearchResultRaceAdapter().apply {
            onBetClick = { match ->
                findNavController().navigate("walisport://module_live/liveFragment?matchId=${match.matchId}&sportId=${match.basicInfo.sportId}".toUri())
            }
            onFavoriteClick = { match ->
                lifecycleScope.launch {
                    fun apiHandle(state: ApiResponseState) {
                        when(state) {
                            is ApiResponseState.Succeeded<*> -> {
                                this@apply.updateFavoriteStatus(
                                    match.matchId, !match.collect
                                )
                            }
                            is ApiResponseState.Failed -> {
                                state.error?.let { showToast(it.msg) }
                            }
                            else -> Unit
                        }
                    }
                    if (match.collect) {
                        mViewModel.removeCollect(match.matchId) { apiHandle(it) }
                    } else {
                        mViewModel.addCollect(match.matchId) { apiHandle(it) }
                    }
                }
            }
        }
    }

    private var datePicker: SearchDatePickerFragment? = null

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setRaceView()

        with(contentBinding) {
            clDate.clickNoRepeat {
                openDatePicker()
            }
            tvDate.text = dateHintStr
        }
    }

    override fun initData() {
        super.initData()
        doSearch()
        args.keyword?.let { keyword ->
            updateSearchText(keyword)
            mViewModel.setCurrentTitle(keyword)
        }
    }

    override fun createObserver() {
        super.createObserver()
        with(mViewModel) {
            launch(Lifecycle.State.STARTED) {
                // 搜尋結果
                launch {
                    directData.collect { data ->
                        data?.let { updateDirectInfo(it) } ?: run {
                            with(contentBinding) {
                                tvTitle.text = currentTitle
                                tvSubTitle.text = noDataStr
                            }
                            updateBackgroundColor()
                        }
                    }
                }

                launch {
                    apiStateListener.observe(viewLifecycleOwner) { state ->
                        switchUI(state)
                    }
                }

                launch {
                    combineResult.collect { combineResult ->
                        linearAdapter.submitList(combineResult) {
                            contentBinding.recyclerView.apply {
                                smoothScrollToPosition(0)
                            }
                        }
                    }
                }

                launch {
                    selectedDateFlow.collect { date ->
                        contentBinding.tvDate.apply {
                            text =
                                if (date == null) dateHintStr
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
                        contentBinding.ivDateArrow.imageTintList =
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
            }
        }
    }

    override fun onLanguageChanged(locale: Locale) {
        super.onLanguageChanged(locale)
        linearAdapter.updateLanguage(locale)
    }

    override fun closeDatePicker() {
        super.closeDatePicker()
        if (datePicker?.isVisible == true) {
            datePicker?.close()
        }
    }

    override fun onResume() {
        super.onResume()
        requireView().post {
            updateStatusSearchBar()
        }
    }

    override fun onDestroyView() {
        contentBinding.recyclerView.adapter = null
        datePicker = null
        updateStatusSearchBar()
        super.onDestroyView()
    }

    override fun onBackPressed(): Boolean {
        findNavController().also { nav ->
            nav.backQueue.getOrNull(nav.backQueue.size - 2)?.destination?.id?.let { fromId ->
                if(fromId == R.id.searchResultBaseFragment) {
                    setTempScreenShot()
                    parentFragmentManager.setFragmentResult(GO_BACK_TO_MAIN, bundleOf(GO_BACK_TO_MAIN to true))
                }
            }
        }
        return super.onBackPressed()
    }

    private fun doSearch() {
        args.data?.let { data ->
            mViewModel.getSearchResult(data)
        }
        args.id?.let { id ->
            args.type.let { type ->
                mViewModel.getSearchResult(id, type)
            }
        }
    }

    private fun setEmptyView(state: DataState) {
        val layoutState =
            if(state == DataState.NetworkUnavailable) DynamicStateLayout.States.NETWORK_ANOMALY
            else DynamicStateLayout.States.DATA_EMPTY
        val errorStr =
            if(state == DataState.NetworkUnavailable) {
                RC.string.error_net.toTranslatedStr()
            } else {
                R.string.no_search_result.toTranslatedStr()
            }
        val onRefresh: (() -> Unit)? =
            if(state == DataState.NetworkUnavailable) { ::doSearch }
            else null

        contentBinding.dynamicState.setState(layoutState, errorStr, onRefresh)
    }

    private fun setRaceView() {
        with(contentBinding) {
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

                                    SearchResultRaceAdapter.VIEW_TYPE_ITEM -> {
                                        val prevType = linearAdapter.getItemViewType(position - 1)
                                        outRect.set(
                                            0,
                                            if (prevType == SearchResultRaceAdapter.VIEW_TYPE_HEADER) 0 else 12.dp2px,
                                            0, 0
                                        )
                                    }

                                    else -> Unit
                                }
                            }
                        })
                    }
                }
                itemAnimator = null
            }
        }
    }

    private fun openDatePicker() {
        val oldDate = mViewModel.getSelectedDate()

        childFragmentManager.setFragmentResultListener(DATE_PICKER_RESULT_KEY, viewLifecycleOwner) { _, bundle ->
            childFragmentManager.clearFragmentResultListener(DATE_PICKER_RESULT_KEY)

            setDateBarStatus(false)
            datePicker = null

            val newDate =
                bundle.getLong(DATE_PICKER_RESULT_TIME_IN_MILLIS)
                    .takeIf { bundle.containsKey(DATE_PICKER_RESULT_TIME_IN_MILLIS) }
                    ?.let { Date(it) }
            mViewModel.setSelectedDate(newDate)
            contentBinding.clDate.isSelected = newDate != null

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

        datePicker =
            SearchDatePickerFragment.Builder().apply {
                val statusBarHeight =
                    ViewCompat.getRootWindowInsets(requireView())
                        ?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
                val clDateBottom = run {
                    IntArray(2).apply {
                        contentBinding.clDate.getLocationOnScreen(this)
                    }[1] + contentBinding.clDate.height
                }
                setMarginTop(clDateBottom - statusBarHeight)
                setMarginStart(8.dp2px)
                setMarginEnd(8.dp2px)
                setSchemeDates(mViewModel.racedDateMap)
                mViewModel.getSelectedDate()?.time?.let { setSelectedDate(it) }
            }.build()

        datePicker?.show(childFragmentManager, contentBinding.clRoot.id)
        setDateBarStatus(true)
    }

    private fun setDateBarStatus(isOpen: Boolean) {
        with(contentBinding) {
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

    private fun switchUI(state: DataState) {
        with(contentBinding) {
            loadingView.visibility = if (state is DataState.Loading) View.VISIBLE else View.GONE
            recyclerView.visibility = if (state is DataState.LoadSuccess) View.VISIBLE else View.GONE

            when (state) {
                is DataState.NetworkUnavailable,
                is DataState.DataEmpty,
                is DataState.None -> {
                    setEmptyView(state)
                    dynamicState.visibility = View.VISIBLE
                }
                else -> {
                    dynamicState.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateDirectInfo(data: SearchResultBaseBean) {
        with(contentBinding) {
            val isPlayer = data is SearchResultPlayerBean
            when (data) {
                is SearchResultTournamentBean -> {
                    tvTitle.text = data.name
                    tvSubTitle.text =
                        data.season.takeIf { it.isNotEmpty() }
                            ?.let {
                                String.format(
                                    R.string.search_result_sub_title_tournament.toTranslatedStr(),
                                    data.season
                                )
                            } ?: noDataStr
                }

                is SearchResultTeamBean -> {
                    tvTitle.text = data.name
                    tvSubTitle.text =
                        listOf(
                            data.tournamentShortName,
                            data.rank.toString(),
                            data.win.toString(),
                            data.lose.toString()
                        ).takeIf { it.all { item -> item.isNotEmpty() } }?.let {
                            String.format(
                                R.string.search_result_sub_title_team.toTranslatedStr(),
                                data.tournamentShortName,
                                data.rank,
                                data.win,
                                data.lose
                            )
                        } ?: noDataStr
                }

                is SearchResultPlayerBean -> {
                    tvTitle.text = data.name
                    tvSubTitle.text =
                        listOf(
                            data.tournamentShortName,
                            data.teamName,
                            data.number.toString(),
                            data.position.name
                        ).takeIf { it.all { item -> item.isNotEmpty() } }?.let {
                            String.format(
                                R.string.search_result_sub_title_player.toTranslatedStr(),
                                data.tournamentShortName,
                                data.teamName,
                                data.number,
                                data.position.name
                            )
                        } ?: noDataStr
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

            updateBackgroundColor(data.color)
        }
    }

    private fun updateBackgroundColor(color: String? = null) {
        run {
            if (color?.isNotEmpty() == true) color.toColorInt()
            else ContextCompat.getColor(
                requireContext(),
                R.color.search_result_default_gradient_start
            )
        }.let {
            mBinding.clRoot.apply {
                val duration = 100
                GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(it, Color.BLACK)
                ).let { newDrawable ->
                    background = TransitionDrawable(
                        arrayOf(background, newDrawable)
                    ).apply {
                        startTransition(duration)
                    }
                }
            }
        }
    }
}