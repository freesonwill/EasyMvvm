package arch.cayenne.module.order.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.betslip.databinding.FragmentOrderDateDialogBinding
import arch.cayenne.module.order.data.constants.OrderDatePageEnum
import arch.cayenne.module.order.ui.viewmodel.OrderDateDialogViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.KClass

class OrderDateDialogFragment: BaseBottomSheetFragment<OrderDateDialogViewModel, FragmentOrderDateDialogBinding>() {

    override val vbClass: KClass<FragmentOrderDateDialogBinding> = FragmentOrderDateDialogBinding::class
    override val vmClass: KClass<OrderDateDialogViewModel> = OrderDateDialogViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        initTabLayout()
        mBinding.viewPager.isHorizontalScrollBarEnabled = false
        mBinding.viewPager.setupHorizontalScrollDegree()
    }

    override fun initListener() {
        mBinding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override suspend fun createObserver() {
    }


    private fun initTabLayout() {
        val page = OrderDatePageEnum.entries.toTypedArray()
        mBinding.viewPager.adapter =
            PagerAdapter(childFragmentManager, lifecycle, page.map { it.page })
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager, false) { tab, position ->
            val textView = TextView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                maxLines = 1
                isSingleLine = true
                ellipsize = null
                text = page[position].page.title
                gravity = Gravity.CENTER
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                if (position == 0) {
                    setTextColor(
                        SkinnableResourceManager.getColor(
                            requireContext(),
                            arch.cayenne.lib.common.R.color.color_00E0E5
                        )
                    )
                } else {
                    setTextColor(
                        SkinnableResourceManager.getColorStateList(
                            requireContext(),
                            arch.cayenne.lib.common.R.color.color_C0C0C0
                        )
                    )
                }
                typeface = Typeface.DEFAULT
            }
            tab.customView = textView
        }.attach()

        mBinding.tabLayout.addOnTabSelectedListener2(object :
            TabLayoutExt.OnTabSelectedListener2 {
            override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
                (tab.customView as? TextView)?.apply {
                    setTextColor(
                        SkinnableResourceManager.getColor(
                            requireContext(),
                            arch.cayenne.lib.common.R.color.color_00E0E5
                        )
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                (tab.customView as? TextView)?.apply {
                    setTextColor(
                        SkinnableResourceManager.getColorStateList(
                            requireContext(),
                            arch.cayenne.lib.common.R.color.color_C0C0C0
                        )
                    )
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) {
            }
        })
    }
}