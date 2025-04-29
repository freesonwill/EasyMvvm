package com.walisport.module.search.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.walisport.module.search.databinding.FragmentSearchMainBinding
import com.walisport.module.search.viewmodel.SearchMainViewModel
import kotlin.reflect.KClass

/**
 * 搜索首页
 */
class SearchMainFragment : BaseFragment<SearchMainViewModel, FragmentSearchMainBinding>() {

    override val vbClass: KClass<FragmentSearchMainBinding> = FragmentSearchMainBinding::class
    override val vmClass: KClass<SearchMainViewModel> = SearchMainViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadSearchTitleBar("请输入内容", {},{})
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }


}