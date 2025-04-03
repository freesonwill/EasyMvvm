package com.walisport.module.live.ui

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.walisport.lib.base.adapter.PagerAdapter
import com.walisport.lib.base.ben.PagerBean
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveBetSlipLayoutBinding
import com.walisport.module.live.ui.adapter.LiveBetSlipAdapter
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 注单
 * */
class LiveBetSlipFragment : BaseFragment<LiveBetSlipViewModel, FragmentLiveBetSlipLayoutBinding>() {
    override val mBinding: FragmentLiveBetSlipLayoutBinding by viewBind()
    override val mViewModel: LiveBetSlipViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        initMenu()
        initViewPager()
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
        val manager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        mBinding.betslipRecycler.layoutManager = manager
        mBinding.betslipRecycler.adapter = adapter
    }

    private fun initViewPager() {
        with(mBinding) {
            val array = resources.getStringArray(R.array.bet_slip_menus)
            val list = listOf(
                PagerBean(array[0]) { LiveBetSlipUnsettledFragment() },
            )
            viewpager.adapter = null
            viewpager.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}