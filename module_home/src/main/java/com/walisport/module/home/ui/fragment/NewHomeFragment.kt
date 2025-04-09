package com.walisport.module.home.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseFragment
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.bet.ui.fragment.FloatingButtonFragment
import com.walisport.module.home.databinding.FragmentNewHomeBinding
import com.walisport.module.home.enums.HomeTab
import com.walisport.module.home.enums.SportType
import com.walisport.module.home.ui.adapter.HomePagerAdapter
import com.walisport.module.home.ui.adapter.SportsListAdapter
import kotlin.reflect.KClass

class NewHomeFragment : BaseFragment<EmptyViewModel, FragmentNewHomeBinding>() {
    override val vbClass: KClass<FragmentNewHomeBinding> = FragmentNewHomeBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        childFragmentManager.beginTransaction()
            .replace(mBinding.floatingContainer.id, FloatingButtonFragment())
            .commit()
        with(mBinding) {
            vpHome.apply {
                adapter = HomePagerAdapter(childFragmentManager, lifecycle, HomeTab.entries)
                isUserInputEnabled = false
                currentItem = 0
            }

            TabLayoutMediator(tlHome, vpHome) { tab, position ->
                val tabView = tab.view
                tab.text = HomeTab.entries[position].getTitle(this@NewHomeFragment.requireContext())
                tabView.setPadding(11.dp2px, 0, 11.dp2px, 0)
            }.attach()


            val apiSportIds = listOf(1, 2, 3, 4, 5) // mock API 回傳的運動 ID
            val sportsList = apiSportIds.mapNotNull { SportType.fromId(it) }

            rvSportsList.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = SportsListAdapter(sportsList) { sport ->
                    // 點擊事件處理
                    Toast.makeText(
                        requireContext(),
                        "選擇：${getString(sport.titleResId)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun initListener() {

    }

    override fun createObserver() {

    }
}