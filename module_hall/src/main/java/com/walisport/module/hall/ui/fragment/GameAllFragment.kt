package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import com.walisport.module.hall.databinding.FragmentGameAllBinding
import com.walisport.module.hall.ui.adapter.GameAllHeaderAdapter
import com.walisport.module.hall.ui.adapter.GameAllListAdapter
import com.walisport.module.hall.ui.adapter.GameAllRankingAdapter
import com.walisport.module.hall.ui.helper.BackToTopHelper
import kotlin.reflect.KClass

class GameAllFragment: BaseFragment<EmptyViewModel, FragmentGameAllBinding>() {
    companion object {
        fun newInstance() = GameAllFragment()
    }

    private val headerAdapter by lazy { GameAllHeaderAdapter() }
    private val listAdapter by lazy {
        GameAllListAdapter {
//            (parentFragment as BaseFragment<*,*>).navigate(HallFragmentDirections.actionHallFragmentToHallCategoryFragment())
            (parentFragment as BaseFragment<*,*>).navigate(arch.cayenne.lib.res.R.string.nav_module_hall_category.deeplink())
//            navigate(arch.cayenne.lib.res.R.string.nav_module_setting_fragment.deeplink())
        }
    }
    private val rankingAdapter by lazy {
        GameAllRankingAdapter(
            parentFragmentManager,
            childFragmentManager,
            lifecycle
        )
    }



    override val vbClass: KClass<FragmentGameAllBinding> = FragmentGameAllBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            val concatAdapter = ConcatAdapter(
                headerAdapter,
                listAdapter,
                rankingAdapter
            )
            rvContent.layoutManager = LinearLayoutManager(requireContext())
            rvContent.adapter = concatAdapter
            BackToTopHelper(rvContent, ivBackToTop)
        }
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }

    override fun onStart() {
        super.onStart()
        headerAdapter.restProBannerJob(mBinding.rvContent)
    }

    override fun onStop() {
        super.onStop()
        headerAdapter.stopProBannerJob(mBinding.rvContent)
    }
}