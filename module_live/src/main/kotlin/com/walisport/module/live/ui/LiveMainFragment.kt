package com.walisport.module.live.ui

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.lib.base.adapter.PagerAdapter
import com.walisport.lib.base.ben.PagerBean
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.common.utils.ext.ResourceExt.getString
import com.walisport.lib.common.utils.ext.removeAllTips
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveMainBinding
import com.walisport.module.live.viewmodel.LiveMainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LiveMainFragment : BaseFragment<LiveMainViewModel, FragmentLiveMainBinding>() {
    override val mBinding: FragmentLiveMainBinding by viewBind()
    override val mViewModel: LiveMainViewModel by viewModel()
    override fun initView(savedInstanceState: Bundle?) {
        setCountView()
        loadFragment()
    }

    override fun initListener() {
        mBinding.titleBar.loadLiveTitleBar("","中国VS日本",false,"1000.00",{
            //返回
            findNavController().navigateUp()
        },{b: Boolean ->  //展开收起回调

        })
    }

    override fun createObserver() {
    }

    private fun setCountView() {
        childFragmentManager.findFragmentByTag(LiveVideoFragment.TAG)
                as? LiveVideoFragment ?: LiveVideoFragment().also {
            childFragmentManager.beginTransaction()
                .replace(mBinding.fragmentVideo.id, it, LiveVideoFragment.TAG)
                .commitNow()
        }
    }

    private fun loadFragment() {
        with(mBinding) {
            val list = listOf(
                PagerBean(R.string.live_note_order.getString()) { LiveNoteOrderFragment() },
                PagerBean(R.string.live_bet_on.getString()) { LiveBetOnFragment()},
                PagerBean(R.string.live_chat.getString()) { LiveChatFragment() },
                PagerBean(R.string.live_outs.getString()) { LiveOutsFragment() },
                PagerBean(R.string.live_lineup.getString()) { LiveLineupFragment() },
                PagerBean(R.string.live_standings.getString()) { LiveStandingsFragment() })
            vpPage.adapter = null
            vpPage.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
            TabLayoutMediator(tabLayout, vpPage) { tab, position ->
                val tabView = tab.view
                tab.text = list[position].title
                tabView.setOnClickListener {
                }
            }.attach()
            tabLayout.removeAllTips()
        }
    }

}