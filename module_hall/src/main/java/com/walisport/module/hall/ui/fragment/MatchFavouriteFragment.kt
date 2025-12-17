package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.hall.R
import com.walisport.module.hall.databinding.FragmentGameFavouriteBinding
import com.walisport.module.hall.databinding.FragmentMatchFavouriteBinding
import com.walisport.module.hall.databinding.FragmentRecentlyPlayedBinding
import com.walisport.module.hall.databinding.TitleBarSimpleBinding
import com.walisport.module.hall.ui.viewmodel.GameFavouriteViewModel
import com.walisport.module.hall.ui.viewmodel.MatchFavouriteViewModel
import com.walisport.module.hall.ui.viewmodel.RecentlyPlayedViewModel
import kotlin.reflect.KClass

class MatchFavouriteFragment : BaseFragment<MatchFavouriteViewModel, FragmentMatchFavouriteBinding>() {
    override val vbClass: KClass<FragmentMatchFavouriteBinding> = FragmentMatchFavouriteBinding::class
    override val vmClass: KClass<MatchFavouriteViewModel> = MatchFavouriteViewModel::class

    private val titleBarBinding: TitleBarSimpleBinding by lazy {
        TitleBarSimpleBinding.inflate(
            LayoutInflater.from(context),
            mBinding.titleBar,
            false
        )
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            titleBar.loadDynamicsTitleBar(titleBarBinding.root, null)
            titleBarBinding.tvTitleName.text = getString(R.string.title_match_favourite)
        }
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {
        with(titleBarBinding) {
            ivBack.addScaleOnTouchAnimation()
            ivBack.clickNoRepeat {
                findNavController().navigateUp()
            }
        }
    }

    override suspend fun createObserver() {
        mViewModel.apiStateListener.observe(viewLifecycleOwner) { state ->
            when (state) {
                DataState.LoadSuccess, DataState.NoMoreData -> {
                    mBinding.rvGame.visibility = View.VISIBLE
                    mBinding.clDynamics.visibility = View.GONE
                }
                DataState.DataEmpty -> {
                    mBinding.rvGame.visibility = View.GONE
                    mBinding.clDynamics.visibility = View.VISIBLE
                    mBinding.clDynamics.setState(
                        States.DATA_EMPTY,
                        arch.cayenne.lib.common.R.string.data_empty.getString()
                    )
                }
                DataState.NetworkUnavailable -> {
                    mBinding.rvGame.visibility = View.GONE
                    mBinding.clDynamics.visibility = View.VISIBLE
                    mBinding.clDynamics.setState(
                        States.NETWORK_ANOMALY(),
                        arch.cayenne.lib.common.R.string.error_net.getString()
                    )
                }
                else -> {}
            }
        }
    }

    override fun initData() {
        super.initData()
    }
}
