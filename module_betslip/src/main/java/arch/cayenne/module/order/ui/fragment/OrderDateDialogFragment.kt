package arch.cayenne.module.order.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.FragmentOrderDateDialogBinding
import arch.cayenne.module.order.data.constants.OrderDatePageEnum
import arch.cayenne.module.order.ui.viewmodel.OrderDateDialogViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.KClass

class OrderDateDialogFragment: BaseBottomSheetFragment<OrderDateDialogViewModel, FragmentOrderDateDialogBinding>(), UpdateCustomViewInterface {

    override val vbClass: KClass<FragmentOrderDateDialogBinding> = FragmentOrderDateDialogBinding::class
    override val vmClass: KClass<OrderDateDialogViewModel> = OrderDateDialogViewModel::class

    private var resultListener: ((Long?, Long?) -> Unit)? = null

    override fun initView(savedInstanceState: Bundle?) {
        initTabLayout()
        mBinding.viewPager.isHorizontalScrollBarEnabled = false
        mBinding.viewPager.setupHorizontalScrollDegree()
        setupViewPagerHeightAdjustment()
    }
    
    private fun setupViewPagerHeightAdjustment() {
        mBinding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // 延遲以確保內容已經渲染
                mBinding.viewPager.post {
                    updateViewPagerHeight()
                }
            }
        })
    }
    
    override fun updateViewPagerHeight() {
        // 獲取當前頁面的 Fragment
        val currentItem = mBinding.viewPager.currentItem
        val fragment = childFragmentManager.findFragmentByTag("f$currentItem")
        
        fragment?.view?.let { fragmentView ->
            // 測量當前 Fragment 的高度
            fragmentView.measure(
                View.MeasureSpec.makeMeasureSpec(mBinding.viewPager.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val height = fragmentView.measuredHeight
            
            // 更新 ViewPager 的高度
            val layoutParams = mBinding.viewPager.layoutParams
            layoutParams.height = height
            mBinding.viewPager.layoutParams = layoutParams
        }
    }

    override fun initListener() {
        mBinding.btnCancel.setOnClickListener {
            dismiss()
        }
        mBinding.btnConfirm.setOnClickListener {
            resultListener?.let {
                val curIndex = mBinding.viewPager.currentItem
                val f = childFragmentManager.findFragmentByTag("f$curIndex")
                if (f is OrderDataPage) {
                    val result = f.getResult()
                    when (result.size) {
                        1 -> {
                            it.invoke(result.first(), null)
                            dismiss()
                        }
                        2 -> {
                            val startTime = result.first()
                            val endTime = result.last()
                            if (startTime > endTime) {
                                showToast(getString(R.string.title_order_betting_return_error))
                            } else {
                                it.invoke(result.first(), result.last())
                                dismiss()
                            }
                        }
                        else -> {
                            it.invoke(null, null)
                            dismiss()
                        }
                    }
                }
            } ?: run {
                dismiss()
            }
        }
    }

    override suspend fun createObserver() {
    }

    fun setListener(listener: (Long?, Long?) -> Unit) {
        resultListener = listener
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
                            arch.cayenne.lib.common.R.color.color_FFFFFF
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
                            arch.cayenne.lib.common.R.color.color_FFFFFF
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

interface OrderDataPage {
    fun getResult(): LongArray
}