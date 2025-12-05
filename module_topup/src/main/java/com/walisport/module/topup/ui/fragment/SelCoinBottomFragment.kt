package com.walisport.module.topup.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.walisport.module.topup.databinding.FragmentCoinSelectBinding
import com.walisport.module.topup.ui.adapter.CoinAdapter
import com.walisport.module.topup.ui.viewmodel.TopUpCryptoViewModel
import com.walisport.module.topup.ui.viewmodel.TopUpViewModel
import kotlin.reflect.KClass

/**
 * 币种选择底部弹窗
 */

class SelCoinBottomFragment :
    BaseBottomSheetFragment<TopUpCryptoViewModel, FragmentCoinSelectBinding>() {

    override val vbClass: KClass<FragmentCoinSelectBinding> = FragmentCoinSelectBinding::class
    override val vmClass: KClass<TopUpCryptoViewModel> = TopUpCryptoViewModel::class
    private val coinAdapter by lazy { CoinAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.recyclerCoin.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = coinAdapter
        }
    }

    override fun initListener() {
        mBinding.ivClose.clickNoRepeat {
            this@SelCoinBottomFragment.dismiss()
            this@SelCoinBottomFragment.dialog?.dismiss()
        }
        coinAdapter.setOnItemClickListener(object : CoinAdapter.OnItemClickListener {
            override fun onItemClick(id: Int) {
                mViewModel.selectCoin(id)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    override suspend fun createObserver() {
        mViewModel.coinData.observe(viewLifecycleOwner) {
            if (it != null) {
                coinAdapter.submitList(it)
                coinAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initBottomSheetStyle()
    }

    private fun initBottomSheetStyle() {
        val bottomSheet = dialog?.findViewById<FrameLayout>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return
        val screenHeight = resources.displayMetrics.heightPixels
        val targetHeight = (screenHeight * 0.88).toInt()
        val topOffset = screenHeight - targetHeight
        bottomSheet.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        BottomSheetBehavior.from(bottomSheet).apply {
            isFitToContents = false
            expandedOffset = topOffset
            state = BottomSheetBehavior.STATE_EXPANDED
            isDraggable = false
            isCancelable = true
            skipCollapsed = false
            isHideable = true
        }
    }

    companion object {
        fun newInstance(): SelCoinBottomFragment {
            return SelCoinBottomFragment()
        }
    }
}