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
        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = linearAdapter
        }
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
        with(mBinding) {
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
}