package arch.cayenne.module.order.ui.fragment

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.FragmentGameDropMenuBinding
import arch.cayenne.module.betslip.databinding.FragmentOrderGameBinding
import arch.cayenne.module.order.data.constants.GamePageEnum
import arch.cayenne.module.order.data.constants.OrderSortType
import arch.cayenne.module.order.ui.viewmodel.OrderGameViewModel
import kotlin.reflect.KClass

/**
 * 投注记录-游戏面板
 */

class OrderGameFragment : BaseFragment<OrderGameViewModel, FragmentOrderGameBinding>() {

    override val vbClass: KClass<FragmentOrderGameBinding> = FragmentOrderGameBinding::class
    override val vmClass: KClass<OrderGameViewModel> = OrderGameViewModel::class
    private var menuBinding: FragmentGameDropMenuBinding? = null
    private var isExpanded: Boolean = false

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            val page = GamePageEnum.entries.toTypedArray()
            viewPager.adapter = PagerAdapter(childFragmentManager, lifecycle, page.map { it.page })
            ivAllArrow.isSelected = true
            tvAll.isSelected = true
            viewPager.setupHorizontalScrollDegree()
        }
    }

    override fun initListener() {
        mBinding.clAll.clickNoRepeat {
            mViewModel.setTabType(GamePageEnum.ALL)
        }
        mBinding.tvMultiple.clickNoRepeat {
            mViewModel.setTabType(GamePageEnum.MULTIPLE)
        }
        mBinding.tvBonus.clickNoRepeat {
            mViewModel.setTabType(GamePageEnum.BONUS)
        }
        mBinding.clFilter.clickNoRepeat {
            GameFilterDialogFragment.newInstance().show(childFragmentManager)
        }
    }

    override suspend fun createObserver() {
        mViewModel.tabType.observe(viewLifecycleOwner) {
            changeTabType(it)
        }
        mViewModel.sortType.observe(viewLifecycleOwner) {
            changeSortType(it)
        }
    }

    private fun initMenuClickListener() {
        menuBinding?.apply {
            tvSortByAll.clickNoRepeat {
                mViewModel.setSortType(OrderSortType.SORT_ALL)
            }
            tvSortByWin.clickNoRepeat {
                mViewModel.setSortType(OrderSortType.SORT_WIN)
            }
            tvSortByOne.clickNoRepeat {
                mViewModel.setSortType(OrderSortType.SORT_ONE)
            }
            tvSortByTen.clickNoRepeat {
                mViewModel.setSortType(OrderSortType.SORT_TEN)
            }
        }
    }

    private fun changeTabType(type: GamePageEnum) {
        when (type) {
            GamePageEnum.ALL -> {
                mBinding.tvMultiple.isSelected = false
                mBinding.tvBonus.isSelected = false
                mBinding.viewPager.setCurrentItem(0, true)
                changeDropMenuState(!isExpanded)
            }

            GamePageEnum.MULTIPLE -> {
                changeDropMenuState(false)
                mBinding.ivAllArrow.isSelected = false
                mBinding.tvAll.isSelected = false
                mBinding.tvBonus.isSelected = false
                mBinding.tvMultiple.isSelected = true
                mBinding.viewPager.setCurrentItem(1, true)
            }

            GamePageEnum.BONUS -> {
                changeDropMenuState(false)
                mBinding.ivAllArrow.isSelected = false
                mBinding.tvAll.isSelected = false
                mBinding.tvBonus.isSelected = true
                mBinding.tvMultiple.isSelected = false
                mBinding.viewPager.setCurrentItem(2, true)
            }
        }
    }

    private fun changeSortType(type: OrderSortType) {
        when (type) {
            OrderSortType.SORT_WIN -> {
                mBinding.tvAll.text = R.string.menu_win.getString()
                menuBinding?.let { binding ->
                    binding.tvSortByAll.isSelected = false
                    binding.tvSortByWin.isSelected = true
                    binding.tvSortByOne.isSelected = false
                    binding.tvSortByTen.isSelected = false
                }
            }

            OrderSortType.SORT_ONE -> {
                mBinding.tvAll.text = R.string.menu_one.getString()
                menuBinding?.let { binding ->
                    binding.tvSortByAll.isSelected = false
                    binding.tvSortByWin.isSelected = false
                    binding.tvSortByOne.isSelected = true
                    binding.tvSortByTen.isSelected = false
                }
            }

            OrderSortType.SORT_TEN -> {
                mBinding.tvAll.text = R.string.menu_ten.getString()
                menuBinding?.let { binding ->
                    binding.tvSortByAll.isSelected = false
                    binding.tvSortByWin.isSelected = false
                    binding.tvSortByOne.isSelected = false
                    binding.tvSortByTen.isSelected = true
                }
            }

            else -> {
                mBinding.tvAll.text = R.string.menu_all.getString()
                menuBinding?.let { binding ->
                    binding.tvSortByAll.isSelected = true
                    binding.tvSortByWin.isSelected = false
                    binding.tvSortByOne.isSelected = false
                    binding.tvSortByTen.isSelected = false
                }
            }
        }
        changeDropMenuState(false, tabColor = false)
    }

    private fun changeDropMenuState(expanded: Boolean, tabColor: Boolean = true) {
        val container = mBinding.llDropDown
        isExpanded = expanded
        if (expanded) {
            mBinding.llDropMask.apply {
                visibility = View.VISIBLE
                alpha = 1f
                clickNoRepeat {
                    changeDropMenuState(false, tabColor = false)
                }
            }
            if (menuBinding == null) {
                menuBinding = FragmentGameDropMenuBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    mBinding.llDropDown,
                    false
                )
                initMenuClickListener()
            }
            container.removeAllViews()
            container.addView(menuBinding?.root)
            container.visibility = View.VISIBLE
            val inAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_from_top)
            menuBinding?.root?.startAnimation(inAnim)
            mBinding.ivAllArrow.isSelected = true
            mBinding.tvAll.isSelected = true
        } else {
            val outAnim = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.slide_out_to_top
            )
            outAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    menuBinding = null
                    container.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            menuBinding?.root?.startAnimation(outAnim)
            mBinding.llDropMask.animate()
                .alpha(0f)
                .setDuration(300L)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {}
                    override fun onAnimationEnd(p0: Animator) {
                        mBinding.llDropMask.visibility = View.GONE
                    }
                    override fun onAnimationCancel(p0: Animator) {}
                    override fun onAnimationRepeat(p0: Animator) {}
                })
                .start()
            if (tabColor) {
                mBinding.ivAllArrow.isSelected = false
                mBinding.tvAll.isSelected = false
            }
        }
    }
}