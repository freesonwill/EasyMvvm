package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.BizUrl
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.showToast
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
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {
        mBinding.layCustomer.clickNoRepeat {
            showToast(R.string.cus_service.getString())
        }
        mBinding.layLesson.clickNoRepeat {
            navigate(
                arch.cayenne.lib.res.R.string.nav_module_web_fragment
                    .deeplink("url" to BizUrl.TOP_LESSON.url)
            )
        }
    }

    override suspend fun createObserver() {

    }
}