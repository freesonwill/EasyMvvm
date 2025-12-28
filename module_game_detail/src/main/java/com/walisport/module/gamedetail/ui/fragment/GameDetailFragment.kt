package com.walisport.module.gamedetail.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.viewmodel.BalanceViewModel
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigateUp
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.gamedetail.databinding.FragmentGameDetailBinding
import com.walisport.module.gamedetail.ui.viewmodel.GameDetailPageViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameDetailFragment : BaseFragment<GameDetailPageViewModel, FragmentGameDetailBinding>() {

    override val vbClass: KClass<FragmentGameDetailBinding> = FragmentGameDetailBinding::class
    override val vmClass: KClass<GameDetailPageViewModel> = GameDetailPageViewModel::class

    private val balanceViewModel: BalanceViewModel by viewModel()

    private val args by navArgs<GameDetailFragmentArgs>()


    override fun initView(savedInstanceState: Bundle?) {
        val gameId = args.gameId
        val adapter = GameDetailPagerAdapter(this)
        with (mBinding) {
//            val statusBarHeight = ImmersionBar.getStatusBarHeight(this@GameDetailFragment)
//            val lp = titleTop.layoutParams as ConstraintLayout.LayoutParams
//            lp.guideBegin += statusBarHeight
//            val bottomLp = titleBottom.layoutParams as ConstraintLayout.LayoutParams
//            lp.guideEnd += statusBarHeight
//            titleTop.layoutParams = lp
//            titleBottom.layoutParams = bottomLp

            // todo 效果待確認
//            ivFavorite.isSelected = mockData.collect
            gameDetailPager.adapter = adapter
            gameDetailPager.orientation = ViewPager2.ORIENTATION_VERTICAL
            // Set offscreen page limit to reduce memory usage during fast scrolling
            // Only keep 1 page on each side, allowing faster recycling
            gameDetailPager.offscreenPageLimit = 1
            viewBalance.setBalanceViewModel(balanceViewModel, viewLifecycleOwner)
        }
        // Disable overscroll effect if desired, or keep it
    }

    override fun onStart() {
        super.onStart()
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(
            autoPadding = false
        )
        setStatusBar(StatusBarConfig, mBinding.root)
    }
    override fun initListener() {
        with (mBinding) {
            viewBalance.init(childFragmentManager)
            ivBack.clickNoRepeat {
                navigateUp()
            }
            ivFavorite.clickNoRepeat {
                if (mViewModel.isCollected.value != true) {
                    showToast("收藏成功")
                }
                mViewModel.toggleCollectStatus(args.gameId)

                lifecycleScope.launch {
                    mViewModel.notifyFavouriteChanged()
                }
            }
        }
    }

    override suspend fun createObserver() {
        mViewModel.isCollected.observe(viewLifecycleOwner) { isCollected ->
            mBinding.ivFavorite.isSelected = isCollected
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.queryGameDetail(args.gameId)
    }

    override fun onDestroyView() {
        // Clear ViewPager2 adapter to prevent memory leaks
        mBinding.gameDetailPager.adapter = null
        super.onDestroyView()
    }

    private class GameDetailPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        // todo: Define the number of pages based on actual data
        override fun getItemCount(): Int = 10

        override fun createFragment(position: Int): Fragment {
            return GameDetailPageFragment()
        }
    }
}
