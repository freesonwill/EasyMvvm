package com.walisport.module.gamedetail.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigateUp
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.gamedetail.R
import com.walisport.module.gamedetail.databinding.FragmentGameDetailBinding
import com.walisport.module.gamedetail.ui.viewmodel.GameDetailPageViewModel
import kotlin.reflect.KClass

class GameDetailFragment : BaseFragment<GameDetailPageViewModel, FragmentGameDetailBinding>() {

    override val vbClass: KClass<FragmentGameDetailBinding> = FragmentGameDetailBinding::class
    override val vmClass: KClass<GameDetailPageViewModel> = GameDetailPageViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        val adapter = GameDetailPagerAdapter(this)
        with (mBinding) {
            // todo 效果待確認
//            ivFavorite.isSelected = mockData.collect
            mBinding.gameDetailPager.adapter = adapter
            mBinding.gameDetailPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        }
        // Disable overscroll effect if desired, or keep it
    }

    //    override fun onStart() {
//        mBinding.root.fitsSystemWindows = false
//        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
//        setStatusBar(StatusBarConfig,mBinding.root)
//        super.onStart()
//    }
    override fun initListener() {
        with (mBinding) {
            viewBalance.init(childFragmentManager)
            ivBack.clickNoRepeat {
                navigateUp()
            }
            ivFavorite.clickNoRepeat {
                it.isSelected = !it.isSelected
            }
        }
    }

    override suspend fun createObserver() {
    }

    private class GameDetailPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        // todo: Define the number of pages based on actual data
        override fun getItemCount(): Int = 10

        override fun createFragment(position: Int): Fragment {
            return GameDetailPageFragment()
        }
    }
}
