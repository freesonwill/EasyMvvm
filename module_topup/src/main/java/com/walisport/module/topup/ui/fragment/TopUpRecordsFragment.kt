package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.topup.R
import com.walisport.module.topup.data.constants.LoadingState
import com.walisport.module.topup.data.entity.RechargeRecordBean
import com.walisport.module.topup.databinding.FragmentTopupRecordsBinding
import com.walisport.module.topup.ui.adapter.OnItemClickListener
import com.walisport.module.topup.ui.adapter.TopupRecordItemAdapter
import com.walisport.module.topup.ui.viewmodel.TopUpRecordsViewModel
import kotlin.reflect.KClass

/**
 * 充值记录列表页
 */

class TopUpRecordsFragment : BaseFragment<TopUpRecordsViewModel, FragmentTopupRecordsBinding>() {

    override val vbClass: KClass<FragmentTopupRecordsBinding> = FragmentTopupRecordsBinding::class
    override val vmClass: KClass<TopUpRecordsViewModel> = TopUpRecordsViewModel::class

    private var defaultImmColor: Int = 0

    private val gameLayoutManager by lazy { LinearLayoutManager(context) }

    private lateinit var listAdapter: TopupRecordItemAdapter


    override fun initView(savedInstanceState: Bundle?) {
        defaultImmColor = getStatusBarColor()
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


            listAdapter = TopupRecordItemAdapter(object : OnItemClickListener {
                override fun onEntryClick(item: RechargeRecordBean) {
                    navigate(
                        R.id.action_topUpRecordsFragment_to_topUpDetailFragment,
                        Bundle().apply {
                            putString("transactionId", item.transactionId)
                        })
                }


            })

            rvCollectList.apply {
                this.layoutManager = gameLayoutManager
                this.adapter = listAdapter
            }

            (rvCollectList.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        }

        mBinding.rvCollectList.touchBackPressed()
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {

        mViewModel.recordListChange.observe(viewLifecycleOwner) { recordBeanList ->
            listAdapter.submitList(recordBeanList)
        }


        mViewModel.apiStateListener.observe(viewLifecycleOwner) { state ->
            with(mBinding) {
                when (state) {
                    DataState.NetworkUnavailable -> {
                        mViewModel.changePageEnd(true)
                        loadingView.visibility = View.GONE
                        refreshLayout.finishRefresh()
                        refreshLayout.finishLoadMore()
                        refreshLayout.setEnableLoadMore(false)
                        clDynamics.visibility = View.VISIBLE
                        clDynamics.setState(
                            DynamicStateLayout.States.NETWORK_ANOMALY,
                            arch.cayenne.lib.common.R.string.error_net.getString()
                        )
                    }

                    DataState.NoMoreData -> {     //這個DataEmpty表示api抓不到任何資料了，有可能是頁面到底，或是從第一頁就抓不到資料
                        mViewModel.changePageEnd(true)
                        refreshLayout.finishLoadMore()
                        refreshLayout.setEnableLoadMore(false)
                        listAdapter.showNoMoreData(true)
                    }

                    LoadingState.DataEmpty -> {  //這個DataEmpty表示確定真的從第一頁就抓不到資料，表示當前的選擇沒有任何賽事
                        loadingView.visibility = View.GONE
                        refreshLayout.finishRefresh()
                        clDynamics.visibility = View.VISIBLE
                        clDynamics.setState(
                            DynamicStateLayout.States.DATA_EMPTY,
                            R.string.recharge_list_empty.getString()
                        )
                    }

                    LoadingState.Loading -> {
                        loadingView.visibility = View.VISIBLE
                        clDynamics.visibility = View.GONE
                        refreshLayout.setEnableLoadMore(true)
                    }

                    LoadingState.Refreshing -> {
                        clDynamics.visibility = View.GONE
                        refreshLayout.setEnableLoadMore(true)
                    }

                    LoadingState.LoadingNext -> {
                        clDynamics.visibility = View.GONE
                    }

                    DataState.LoadSuccess -> {
                        loadingView.visibility = View.GONE
                        if (refreshLayout.isRefreshing) refreshLayout.finishRefresh()
                        refreshLayout.finishLoadMore()
                        clDynamics.visibility = View.GONE
                    }
                }
            }
        }

    }


    override fun initData() {
        super.initData()
        mViewModel.getListData()
    }

    override fun onStart() {
        super.onStart()
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
    }
}