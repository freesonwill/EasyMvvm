package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.topup.R
import com.walisport.module.topup.data.constants.LoadingState
import com.walisport.module.topup.databinding.FragmentWithdrawRecordsBinding
import com.walisport.module.topup.ui.adapter.WithdrawAdapter
import com.walisport.module.topup.ui.viewmodel.WithdrawRecordsViewModel
import kotlin.reflect.KClass

/**
 * 充值记录列表页
 */

class WithdrawRecordsFragment : BaseFragment<WithdrawRecordsViewModel, FragmentWithdrawRecordsBinding>() {

    override val vbClass: KClass<FragmentWithdrawRecordsBinding> = FragmentWithdrawRecordsBinding::class
    override val vmClass: KClass<WithdrawRecordsViewModel> = WithdrawRecordsViewModel::class
    private val mAdapter by lazy { WithdrawAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.withdrawal_record.getString(), {
                findNavController().navigateUp()
            })
            refreshLayout.setEnableLoadMore(true)
            refreshLayout.setEnableScrollContentWhenLoaded(true)
            refreshLayout.setOnRefreshListener {
                mViewModel.reload()
            }
            refreshLayout.setOnLoadMoreListener {
                mViewModel.loadNextPage()
            }
        }
        mBinding.withdrawList.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.reload()
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
        mViewModel.onDateFilter.observe(viewLifecycleOwner) {
            mBinding.tvDateFilter.text = it.title
        }
        mViewModel.onWithdrawFilter.observe(viewLifecycleOwner) { list ->
            val selectedNames = list.map { it.txName }
            val displayText = when (selectedNames.size) {
                1 -> selectedNames.first()
                else -> selectedNames.joinToString("/")
            }
            mBinding.tvSportFilter.text = displayText
        }
        mViewModel.recordListChange.observe(viewLifecycleOwner) { list ->
            mAdapter.submitList(list)
        }
        mViewModel.apiStateListener.observe(viewLifecycleOwner) { state ->
            with(mBinding) {
                when (state) {
                    DataState.NetworkUnavailable -> {
                        refreshLayout.finishRefresh()
                        refreshLayout.finishLoadMore()
                        refreshLayout.setEnableLoadMore(false)
                    }

                    DataState.NoMoreData -> {
                        refreshLayout.finishLoadMore()
                        refreshLayout.setEnableLoadMore(false)
                    }

                    LoadingState.DataEmpty -> {
                        refreshLayout.finishRefresh()
                    }

                    LoadingState.Loading -> {
                        refreshLayout.setEnableLoadMore(true)
                    }

                    LoadingState.Refreshing -> {
                        refreshLayout.setEnableLoadMore(true)
                    }

                    LoadingState.LoadingNext -> {
                    }

                    DataState.LoadSuccess -> {
                        if (refreshLayout.isRefreshing) refreshLayout.finishRefresh()
                        refreshLayout.finishLoadMore()
                    }
                }
            }
        }
    }
}