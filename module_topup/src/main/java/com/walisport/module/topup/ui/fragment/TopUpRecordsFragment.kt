package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.topup.R
import com.walisport.module.topup.data.constants.LoadingState
import com.walisport.module.topup.data.entity.RechargeRecordBean
import com.walisport.module.topup.databinding.FragmentTopupRecordsBinding
import com.walisport.module.topup.ui.adapter.TopUpAdapter
import com.walisport.module.topup.ui.viewmodel.TopUpRecordsViewModel
import kotlin.reflect.KClass

/**
 * 充值记录列表页
 */

class TopUpRecordsFragment : BaseFragment<TopUpRecordsViewModel, FragmentTopupRecordsBinding>() {

    override val vbClass: KClass<FragmentTopupRecordsBinding> = FragmentTopupRecordsBinding::class
    override val vmClass: KClass<TopUpRecordsViewModel> = TopUpRecordsViewModel::class
    private val mAdapter by lazy { TopUpAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.recharge_record.getString(), {
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
        mBinding.topUpList.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }
        mAdapter.setOnItemClick(object : TopUpAdapter.OnTopUpItemClickListener {
            override fun onClick(item: RechargeRecordBean) {
                navigate(TopUpRecordsFragmentDirections.actionTopUpRecordsFragmentToTopUpDetailFragment()
                    .apply {
                        arguments.putString("amount", item.amount)
                        arguments.putString("iid", item.iid)
                        arguments.putInt("type", item.type)
                        arguments.putString("account", item.account)
                        arguments.putInt("status", item.status)
                        arguments.putLong("time", item.time)
                        arguments.putString("reason", item.reason)
                    }
                )
            }
        })
        mBinding.topUpList.touchBackPressed()
        mBinding.root.touchBackPressed()
    }

    override fun initData() {
        super.initData()
        mViewModel.reload()
    }

    override fun onStart() {
        super.onStart()
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
        mViewModel.onDateFilter.observe(viewLifecycleOwner) {
            mBinding.tvDateFilter.text = it.title
        }
        mViewModel.onRechargeFilter.observe(viewLifecycleOwner) { list ->
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