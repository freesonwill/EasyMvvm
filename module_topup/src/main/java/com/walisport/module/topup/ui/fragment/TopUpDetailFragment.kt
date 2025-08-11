package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.DateUtils
import arch.cayenne.lib.common.utils.copyToClipboard
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.topup.R
import com.walisport.module.topup.data.constants.LoadingState
import com.walisport.module.topup.databinding.FragmentTopupDetailBinding
import com.walisport.module.topup.ui.viewmodel.TopUpDetailViewModel
import kotlin.reflect.KClass

/**
 * 充值记录详情页
 */

class TopUpDetailFragment : BaseFragment<TopUpDetailViewModel, FragmentTopupDetailBinding>() {

    override val vbClass: KClass<FragmentTopupDetailBinding> = FragmentTopupDetailBinding::class
    override val vmClass: KClass<TopUpDetailViewModel> = TopUpDetailViewModel::class

    private var defaultImmColor: Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        defaultImmColor = getStatusBarColor()
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.recharge_detail.getString(), {
                findNavController().navigateUp()
            })
        }

        val transactionId = arguments?.getString("transactionId") ?: ""
        mViewModel.setTransactionId(transactionId)
    }

    override fun onStart() {
        super.onStart()
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
    }

    override fun initListener() {
        mBinding.root.touchBackPressed()
        mBinding.ivCopy.clickNoRepeat {
            copyToClipboard(mBinding.tvOrderNumber.text as String?) {
                showToast(R.string.copied_to_clipboard.getString())
            }
        }
    }

    override suspend fun createObserver() {

        mViewModel.dataBean.observe(viewLifecycleOwner) { bean ->
            with(mBinding) {
                tvMoneySymbol.text = "¥"
                tvAmount.text = bean.amount

                tvOrderNumberTitle.text = R.string.order_number.getString()
                tvOrderNumber.text = bean.transactionId

                tvPayMethodTitle.text = R.string.payment_method.getString()
                tvPayMethod.text = "银行卡"

                tvPayStatusTitle.text = R.string.payment_status.getString()
                when (bean.status) {
                    0 -> {
                        tvPayStatus.text = R.string.pay_success.getString()
                        tvPayStatus.setTextColor(R.color.pay_success.getColor())
                    }

                    1 -> {
                        tvPayStatus.text = R.string.pay_failure.getString()
                        tvPayStatus.setTextColor(R.color.pay_failure.getColor())
                    }

                    else -> {
                        tvPayStatus.text = R.string.pay_un_confirm.getString()
                        tvPayStatus.setTextColor(R.color.pay_un_confirm.getColor())
                    }
                }

                tvTimestampTitle.text = R.string.transaction_create_time.getString()
                tvTimestamp.text = DateUtils.getDisplayStr(bean.timestamp, "yyyy.MM.dd HH:mm")
            }

        }

        mViewModel.apiStateListener.observe(viewLifecycleOwner) { state ->
            with(mBinding) {
                when (state) {
                    DataState.NetworkUnavailable -> {
                        loadingView.visibility = View.GONE
                        clDynamics.visibility = View.VISIBLE
                        clDynamics.setState(
                            DynamicStateLayout.States.NETWORK_ANOMALY,
                            arch.cayenne.lib.common.R.string.error_net.getString()
                        )
                    }

                    LoadingState.DataEmpty -> {
                        loadingView.visibility = View.GONE
                        clDynamics.visibility = View.VISIBLE
                        clDynamics.setState(
                            DynamicStateLayout.States.DATA_EMPTY,
                            R.string.recharge_info_empty.getString()
                        )
                    }

                    LoadingState.Loading -> {
                        loadingView.visibility = View.VISIBLE
                        clDynamics.visibility = View.GONE

                    }

                    DataState.LoadSuccess -> {
                        loadingView.visibility = View.GONE
                        clDynamics.visibility = View.GONE
                        llContent.visibility = View.VISIBLE
                    }
                }
            }
        }

    }


    override fun initData() {
        super.initData()
        mViewModel.queryData()
    }
}