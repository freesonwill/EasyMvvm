package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentOrderDetailBinding
import com.walisport.module.topup.databinding.TitleOrderDetailBinding
import com.walisport.module.topup.ui.viewmodel.OrderDetailViewModel
import kotlin.reflect.KClass

/**
 * 微信、支付宝等充值详情页
 */

class OrderDetailFragment : BaseFragment<OrderDetailViewModel, FragmentOrderDetailBinding>() {

    override val vbClass: KClass<FragmentOrderDetailBinding> = FragmentOrderDetailBinding::class
    override val vmClass: KClass<OrderDetailViewModel> = OrderDetailViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            val bind = TitleOrderDetailBinding.inflate(LayoutInflater.from(context), root, false)
            titleBar.loadDynamicsTitleBar(bind.root, null)
            bind.apply {
                tvTitle.text = R.string.pay_wechat.getString()
                ivRight.clickNoRepeat {
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }
}