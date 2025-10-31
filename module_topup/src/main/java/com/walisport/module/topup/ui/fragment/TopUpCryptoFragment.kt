package com.walisport.module.topup.ui.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.utils.copyToClipboard
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.walisport.module.topup.R
import com.walisport.module.topup.data.entity.CoinBean
import com.walisport.module.topup.databinding.FragmentCryptoBinding
import com.walisport.module.topup.databinding.TabCoinBinding
import com.walisport.module.topup.ui.viewmodel.CryptoViewModel
import kotlinx.coroutines.Job
import kotlin.reflect.KClass


/**
 * 充值-加密货币页面
 */

class TopUpCryptoFragment : BaseFragment<CryptoViewModel, FragmentCryptoBinding>() {

    override val vbClass: KClass<FragmentCryptoBinding> = FragmentCryptoBinding::class
    override val vmClass: KClass<CryptoViewModel> = CryptoViewModel::class
    private var drawTournamentTabJob: Job? = null
    private val size: Int = 128.dp2px

    override fun initView(savedInstanceState: Bundle?) {
        val content = "TGPs2ZF7nr1cjtdsMQTHmfpgXqqDnAYE6i"
        mBinding.tvCryptoAddress.text = content
        val bitmap = generateQRCode(content, size, size)
        mBinding.ivQrcode.setImageBitmap(bitmap)
    }

    override fun initData() {
        super.initData()
        mViewModel.getCoinList()
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
            CoinDialogFragment.newInstance(offset).show(childFragmentManager)
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
            ivLogoCoin.background = bean.coinLogo.getDrawable()
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

    private fun generateQRCode(content: String, width: Int, height: Int): Bitmap? {
        val writer = QRCodeWriter()
        try {
            val bitMatrix: BitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height)
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    pixels[y * width + x] = if (bitMatrix.get(x, y)) -0x1000000 else -0x1
                }
            }
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }
    }

    private fun showSelectCoinDialog() {
        val tag = "sel_coin_bottom_fragment"
        if (childFragmentManager.findFragmentByTag(tag) != null) return
        SelCoinBottomFragment.newInstance().show(childFragmentManager, tag)
    }
}