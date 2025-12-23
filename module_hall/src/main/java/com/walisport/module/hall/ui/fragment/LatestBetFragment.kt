package com.walisport.module.hall.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameAllRankingListData
import com.walisport.module.hall.databinding.FragmentGameAllRankingListBinding
import com.walisport.module.hall.ui.adapter.GameAllRankingListAdapter
import com.walisport.module.hall.ui.viewmodel.LatestBetViewModel
import kotlin.reflect.KClass
import arch.cayenne.lib.common.utils.ext.DimensionExt.px2dp
/**
 * 最新投注fragment
 */
class LatestBetFragment : BaseFragment<LatestBetViewModel , FragmentGameAllRankingListBinding>() {
    companion object {
        fun newInstance() = LatestBetFragment()
    }

    override val vbClass: KClass<FragmentGameAllRankingListBinding> =
        FragmentGameAllRankingListBinding::class
    override val vmClass: KClass<LatestBetViewModel> = LatestBetViewModel::class


    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            rvCurrentRank.layoutManager = LinearLayoutManager(requireContext())
            rvCurrentRank.itemAnimator = null
            rvCurrentRank.adapter = GameAllRankingListAdapter(this@LatestBetFragment)
        }
    }
    fun getContentHeight(): Int {
         return 1469
    }
    override fun onResume() {
        LogUtils.e("GameAllRankingTodayFragment-----LatestBetFragment-----${mBinding.root.height}")

        super.onResume()
    }
    override fun initListener() {
    }

    override suspend fun createObserver() {
        mViewModel.apiStateListener.observe(this) { state ->
            when (state) {
                DataState.LoadSuccess -> {
                    mBinding.rvCurrentRank.visibility = View.VISIBLE
                    mBinding.clDynamics.visibility = View.GONE
                }

                DataState.DataEmpty -> {
                    mBinding.rvCurrentRank.visibility = View.GONE
                    mBinding.clDynamics.visibility = View.VISIBLE
                    mBinding.clDynamics.setState(
                        States.DATA_EMPTY,
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
                        States.NETWORK_ANOMALY(),
                        arch.cayenne.lib.common.R.string.error_net.getString()
                    )
                }

                else -> {
                }
            }
        }

        mViewModel.gameListLiveData.observe(this) { list ->
            childFragmentManager.findFragmentByTag(AllInfoDialogFragment.TAG)?.let {
                childFragmentManager.beginTransaction().remove(it).commitAllowingStateLoss()
            }
            (mBinding.rvCurrentRank.adapter as? GameAllRankingListAdapter)?.submitList(
                list.toMutableList()
            )
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.queryLatestBetList()
    }


}