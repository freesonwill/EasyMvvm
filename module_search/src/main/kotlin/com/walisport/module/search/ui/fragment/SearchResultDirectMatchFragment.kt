package com.walisport.module.search.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.walisport.module.search.R
import com.walisport.module.search.data.model.SearchResultBaseBean
import com.walisport.module.search.databinding.FragmentSearchResultDirectMatchBinding
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

    override fun createVM(): SearchResultViewModel {
        return activityViewModel<SearchResultViewModel>().value
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

    @SuppressLint("SetTextI18n")
    private suspend fun updateDirectInfo(data: SearchResultBaseBean) {
        with(mBinding) {
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
                        //TODO
                    }
                }
            }
        }
    }
}