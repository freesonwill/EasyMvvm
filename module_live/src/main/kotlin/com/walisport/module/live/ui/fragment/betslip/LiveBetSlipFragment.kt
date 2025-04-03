package com.walisport.module.live.ui.fragment.betslip

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveBetSlipLayoutBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LiveBetSlipFragment : BaseFragment<LiveBetSlipViewModel, FragmentLiveBetSlipLayoutBinding>() {
    override val mBinding: FragmentLiveBetSlipLayoutBinding by viewBind()
    override val mViewModel: LiveBetSlipViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        initMenu()
    }

    private fun initMenu() {
        val array = resources.getStringArray(R.array.bet_slip_menus)
        val adapter = LiveBetSlipAdapter(object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
            }
        })
        adapter.submitList(array.toList())
        mBinding.betslipRecycler.layoutManager = LinearLayoutManager(context)
        mBinding.betslipRecycler.adapter = adapter
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}