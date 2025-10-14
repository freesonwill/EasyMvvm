package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.utils.copyToClipboard
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.topup.R
import com.walisport.module.topup.data.entity.CoinBean
import com.walisport.module.topup.databinding.FragmentCryptoBinding
import com.walisport.module.topup.databinding.ItemCoinBinding
import com.walisport.module.topup.ui.viewmodel.CryptoViewModel
import kotlinx.coroutines.Job
import kotlin.reflect.KClass

/**
 * 充值-加密货币页面
 */

class CryptoFragment : BaseFragment<CryptoViewModel, FragmentCryptoBinding>() {

    override val vbClass: KClass<FragmentCryptoBinding> = FragmentCryptoBinding::class
    override val vmClass: KClass<CryptoViewModel> = CryptoViewModel::class
    private var drawTournamentTabJob: Job? = null

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData() {
        super.initData()
        mViewModel.getCoinList()
    }

    override fun initListener() {
        mBinding.layCopy.clickNoRepeat {
            copyToClipboard(mBinding.tvAddress.text as String?) {
                showToast(R.string.tip_copy_suc.getString())
            }
        }
        mBinding.layLesson.clickNoRepeat {
            showToast(R.string.recharge_lesson.getString())
        }
        mBinding.layCustomer.clickNoRepeat {
            showToast(R.string.cus_service.getString())
        }
    }

    override suspend fun createObserver() {
        mViewModel.coinData.observe(viewLifecycleOwner) {
            val data = it as List<CoinBean>
            List(data.size) { index ->
                mBinding.tlCoinList.addTab(mBinding.tlCoinList.newTab().setTag(data[index]))
            }
            drawTournamentTabJob?.cancel()
            drawTournamentTabJob = launch {
                val tabLayout = mBinding.tlCoinList
                for (i in 0 until tabLayout.tabCount) {
                    val tab = tabLayout.getTabAt(i)
                    val data = tab?.tag as? CoinBean
                    if (tab != null && data != null && tab.customView == null) {
                        tab.customView = createTabView(data, i)
                        tab.view.setPadding(6.dp2px, 0, 0, 0)
                    }
                }
            }
        }
    }

    private fun createTabView(
        bean: CoinBean,
        position: Int
    ): View {
        val tabBinding = ItemCoinBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        )
        tabBinding.apply {
            tvNameCoin.text = bean.coinName
            root.setOnClickListener {
                selectTab(position)
            }
        }
        return tabBinding.root
    }

    private fun selectTab(position: Int) {
        val tabLayout = mBinding.tlCoinList
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            tab?.view?.isSelected = i == position
        }
    }
}