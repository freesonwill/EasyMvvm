package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.hall.R
import com.walisport.module.hall.data.HallGamePage
import com.walisport.module.hall.data.HallGameTabDefault
import com.walisport.module.hall.databinding.FragmentHallBinding
import com.walisport.module.hall.databinding.ItemHallGameTabBinding
import com.walisport.module.hall.ui.viewmodel.HallViewModel
import kotlin.reflect.KClass

/**
 * 游戏大厅界面
 */

class HallFragment : BaseFragment<HallViewModel, FragmentHallBinding>() {

    override val vbClass: KClass<FragmentHallBinding> = FragmentHallBinding::class
    override val vmClass: KClass<HallViewModel> = HallViewModel::class

    private val mockTabList = arrayListOf(
        HallGameTabDefault(
            res = R.drawable.ic_tab_hall_recent,
            _title = R.string.tab_recent.getString(),
            _page = { GameContentFragment.newInstance() }
        ),
        HallGameTabDefault(
            res = R.drawable.ic_tab_hall_all,
            _title = R.string.tab_all.getString(),
            _page = { GameContentFragment.newInstance() }
        ),
        HallGameTabDefault(
            res = R.drawable.ic_tab_hall_table,
            _title = R.string.tab_table.getString(),
            _page = { GameContentFragment.newInstance() }
        ),
        HallGameTabDefault(
            res = R.drawable.ic_tab_hall_slot,
            _title = R.string.tab_slot.getString(),
            _page = { GameContentFragment.newInstance() }
        ),
        HallGameTabDefault(
            res = R.drawable.ic_tab_hall_slot,
            _title = R.string.tab_slot.getString(),
            _page = { GameContentFragment.newInstance() }
        ),
        HallGameTabDefault(
            res = R.drawable.ic_tab_hall_slot,
            _title = R.string.tab_slot.getString(),
            _page = { GameContentFragment.newInstance() }
        ),
        HallGameTabDefault(
            res = R.drawable.ic_tab_hall_slot,
            _title = R.string.tab_slot.getString(),
            _page = { GameContentFragment.newInstance() }
        ),
        HallGameTabDefault(
            res = R.drawable.ic_tab_hall_slot,
            _title = R.string.tab_slot.getString(),
            _page = { GameContentFragment.newInstance() }
        )
    )

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            root.touchBackPressed()

            balanceView.init(childFragmentManager)

            var barHeight = ViewUtils.getStatusBarHeight(requireContext())
            val params = guideline.layoutParams as ConstraintLayout.LayoutParams
            params.guideBegin = 43.dp2px
            guideline.layoutParams = params

            vpGame.adapter = PagerAdapter(childFragmentManager, lifecycle, mockTabList)

            TabLayoutMediator(tlGame, vpGame) { tab, position ->
                tab.customView = createGameTabView(position, mockTabList[position])
            }.attach()
        }
    }

    private fun createGameTabView(position: Int, item: HallGamePage) : View {
        val tabBinding = ItemHallGameTabBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        tabBinding.tvTitle.text = item.title
        if (item is HallGameTabDefault) {
            tabBinding.ivIcon.setBackgroundResource(item.res)
        } else {
            //TODO 從api來
        }

        return tabBinding.root
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }

    override fun onStart() {
//        mBinding.ivLogo.post {
//            var barHeight = ViewUtils.getStatusBarHeight(requireContext())
//            var toBarHeight = mBinding.ivLogo.height
//
//            val paramsLin = mBinding.homeBarIcon.layoutParams as LayoutParams
//            paramsLin.height = barHeight+toBarHeight
//            mBinding.homeBarIcon.layoutParams = paramsLin
//        }
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(autoPadding = false, autoIsNavigation = true)
        setStatusBar(StatusBarConfig, mBinding.clMain)
        super.onStart()
    }



}