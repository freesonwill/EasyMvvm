package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.fragment.CoinDialogFragment
import arch.cayenne.lib.common.utils.QRCodeUtils
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.copyToClipboard
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.CoinBean
import com.bumptech.glide.Glide
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentCryptoBinding
import com.walisport.module.topup.databinding.TabCoinBinding
import com.walisport.module.topup.ui.viewmodel.TopUpCryptoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass

/**
 * 充值-加密货币页面
 */

class TopUpCryptoFragment : BaseFragment<TopUpCryptoViewModel, FragmentCryptoBinding>() {

    override val vbClass: KClass<FragmentCryptoBinding> = FragmentCryptoBinding::class
    override val vmClass: KClass<TopUpCryptoViewModel> = TopUpCryptoViewModel::class
    private var drawTournamentTabJob: Job? = null

    override fun initView(savedInstanceState: Bundle?) {
        generateQRCode()
    }

    override fun initListener() {
        mBinding.layCopy.clickNoRepeat {
            copyToClipboard(mBinding.tvCryptoAddress.text as String?) {
                showToast(R.string.tip_copy_suc.getString())
            }
        }
        mBinding.layCoin.clickNoRepeat {
            val location = IntArray(2)
            mBinding.layCoin.getLocationOnScreen(location)
            val offset = location[1] + 25.dp2px
            CoinDialogFragment.newInstance(offset).apply {
                setDismissListener(object : CoinDialogFragment.DialogDismissListener {
                    override fun onDismiss() {
                        ViewUtils.expandView(mBinding.ivArrow, false)
                    }

                    override fun onShow() {
                        ViewUtils.expandView(mBinding.ivArrow, true)
                    }
                })
            }.show(childFragmentManager)
        }
        mBinding.layLesson.clickNoRepeat {
            navigate(TopUpFragmentDirections.actionTopUpFragmentToFundDetailsFragment().apply {
                arguments.putString("type", "recharge")
            })
        }
        mBinding.layCustomer.clickNoRepeat {
            showToast(R.string.cus_service.getString())
        }
        mBinding.btnMoreCoin.clickNoRepeat {
            showSelectCoinDialog()
        }
    }

    override suspend fun createObserver() {
        mViewModel.coinData.observe(viewLifecycleOwner) {
            val data = it as List<CoinBean>
            mBinding.tlCoinList.removeAllTabs()
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
        val tabBinding = TabCoinBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        )
        tabBinding.apply {
            Glide.with(this@TopUpCryptoFragment)
                .load(bean.icon)
                .placeholder(R.drawable.icon_pay_usdt)
                .into(ivLogoCoin)
            tvNameCoin.text = bean.name
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

    private fun generateQRCode() {
        lifecycleScope.launch(Dispatchers.IO) {
            val size: Int = 122.dp2px
            val content = "TGPs2ZF7nr1cjtdsMQTHmfpgXqqDnAYE6i"
            val bitmap = QRCodeUtils.generateQRCode(content, size, size)
            withContext(Dispatchers.Main) {
                mBinding.ivQrcode.setImageBitmap(bitmap)
            }
        }
    }

    private fun showSelectCoinDialog() {
        val tag = "sel_coin_bottom_fragment"
        if (childFragmentManager.findFragmentByTag(tag) != null) return
        SelCoinBottomFragment.newInstance().show(childFragmentManager, tag)
    }
}