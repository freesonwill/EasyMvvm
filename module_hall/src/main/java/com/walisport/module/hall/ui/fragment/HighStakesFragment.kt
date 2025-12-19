package com.walisport.module.hall.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.hall.databinding.FragmentGameAllRankingListBinding
import com.walisport.module.hall.ui.adapter.GameAllRankingListAdapter
import com.walisport.module.hall.ui.viewmodel.HighStakesViewModel
import kotlin.reflect.KClass

/**
 * 大额fragment
 */
class HighStakesFragment : BaseFragment<HighStakesViewModel , FragmentGameAllRankingListBinding>() {
    companion object {
        fun newInstance() = HighStakesFragment()
    }

    override val vbClass: KClass<FragmentGameAllRankingListBinding> =
        FragmentGameAllRankingListBinding::class
    override val vmClass: KClass<HighStakesViewModel> = HighStakesViewModel::class

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            rvCurrentRank.layoutManager = LinearLayoutManager(requireContext())
            rvCurrentRank.adapter = GameAllRankingListAdapter()

        }
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
        mViewModel.apiStateListener.observe(viewLifecycleOwner) { state ->
            when (state) {
                DataState.LoadSuccess -> {
                    mBinding.rvCurrentRank.visibility = View.VISIBLE
                    mBinding.clDynamics.visibility = View.GONE
                }

                DataState.DataEmpty -> {
                    mBinding.rvCurrentRank.visibility = View.GONE
                    mBinding.clDynamics.visibility = View.VISIBLE
                    mBinding.clDynamics.setState(
                        States.DATA_EMPTY ,
                        arch.cayenne.lib.common.R.string.data_empty.getString()
                    )

                }

                DataState.NoMoreData -> {
                    mBinding.rvCurrentRank.visibility = View.VISIBLE
                    mBinding.clDynamics.visibility = View.GONE
                }

                DataState.NetworkUnavailable -> {
                    mBinding.rvCurrentRank.visibility = View.GONE
                    mBinding.clDynamics.visibility = View.VISIBLE
                    mBinding.clDynamics.setState(
                        States.NETWORK_ANOMALY() ,
                        arch.cayenne.lib.common.R.string.error_net.getString()
                    )
                }

                else -> {
                }
            }
        }

        mViewModel.gameListLiveData.observe(viewLifecycleOwner) { list ->
            (mBinding.rvCurrentRank.adapter as? GameAllRankingListAdapter)?.submitList(list)
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.queryRecordBig()
    }


}