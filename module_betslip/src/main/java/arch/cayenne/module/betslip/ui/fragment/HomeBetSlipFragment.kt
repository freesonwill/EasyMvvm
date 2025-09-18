package arch.cayenne.module.betslip.ui.fragment

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.animateIndicatorToPosition
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.lib.common.utils.ext.setupViewPagerScroll
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import arch.cayenne.lib.common.utils.ext.startZoomInAnim
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.doSmartAnim
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipDateFilterEnum
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.data.model.HomeSlipShowTypeEnum
import arch.cayenne.module.betslip.databinding.FragmentHomeBetslipBinding
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipFilterViewModel
import arch.cayenne.module.betslip.ui.viewmodel.HomeBetSlipViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

class HomeBetSlipFragment : BaseFragment<HomeBetSlipViewModel, FragmentHomeBetslipBinding>() {

    companion object {
        const val TAG = "HomeBetSlipFragment"
    }
    override val vbClass: KClass<FragmentHomeBetslipBinding> = FragmentHomeBetslipBinding::class
    override val vmClass: KClass<HomeBetSlipViewModel> = HomeBetSlipViewModel::class
    private val betSlipFilterViewModel: BetSlipFilterViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        val array = SkinnableResourceManager.getStringArray(requireContext(),arch.cayenne.lib.res.R.array.bet_slip_menus)
        val list = listOf(
            PagerBean(array[0]) { BetSlipUnsettledFragment() },
            PagerBean(array[1]) { BetSlipConfirmFragment() },
            PagerBean(array[2]) { BetSlipSettledFragment() },
            PagerBean(array[3]) { BetSlipReserveFragment() },
            PagerBean(array[4]) { BetSlipInvalidFragment() },
        )
        setPage(list)
        mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mBinding.root.post {
                    mBinding.viewPager.offscreenPageLimit = list.size
                }
            }

        })
        SportPickerFragment.create(childFragmentManager, mBinding.fragmentSportFilter.id)
    }

    override fun initData() {
        super.initData()
        betSlipFilterViewModel.init()
    }

    override fun initListener() {
        mBinding.ivBack.setOnClickListener {
            // 有tag代表是透過fragment manager添加而來，而非navigation
            if (tag == HomeBetSlipFragment.TAG) {
                parentFragmentManager.popBackStack(HomeBetSlipFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            } else {
                findNavController().navigateUp()
            }
        }
        mBinding.clDateFilter.setOnClickListener {
            mViewModel.setShowType(HomeSlipShowTypeEnum.DATE)
        }
        mBinding.clSportFilter.setOnClickListener {
            mViewModel.setShowType(HomeSlipShowTypeEnum.SPORT)
        }
        mBinding.root.touchBackPressed()
    }

    override suspend fun createObserver() {
        mViewModel.onDateFilter.observe(viewLifecycleOwner) {
            mBinding.tvDateFilter.text = it.title
        }
        mViewModel.onSportFilter.observe(viewLifecycleOwner) { list ->
            val selectedNames = list.map { it.sportName }

            val displayText = when (selectedNames.size) {
                1 -> selectedNames.first()
                else -> selectedNames.joinToString("/")
            }

            mBinding.tvSportFilter.text = displayText
        }
        mViewModel.onShowTypeListener.observeEvent(viewLifecycleOwner, this) {
            when (it) {
                HomeSlipShowTypeEnum.DATE -> showDateFilter()
                HomeSlipShowTypeEnum.SPORT -> showSportFilter()
                HomeSlipShowTypeEnum.NONE -> {
                    hideSportFilter()
                }
            }
            mBinding.clSportFilter.visibility = if (it == HomeSlipShowTypeEnum.SPORT || it == HomeSlipShowTypeEnum.NONE) View.VISIBLE else View.INVISIBLE
            mBinding.clDateFilter.visibility = if (it == HomeSlipShowTypeEnum.DATE || it == HomeSlipShowTypeEnum.NONE) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun setPage(pager: List<PagerBean>) {
        mBinding.viewPager.adapter = PagerAdapter(childFragmentManager, lifecycle, pager)

        mBinding.tabLayout.post {
            val tabLayoutWidth = Resources.getSystem().displayMetrics.widthPixels
            var totalTextWidth = 0

            val tempPaint = TextPaint().apply {
                textSize = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, 15f, Resources.getSystem().displayMetrics
                )
            }

            pager.forEach {
                totalTextWidth += tempPaint.measureText(it.title).toInt() + 18.dp2px
            }

            val shouldDistributeEvenly = totalTextWidth < tabLayoutWidth

            mBinding.tabLayout.tabMode =
                if (shouldDistributeEvenly) TabLayout.MODE_FIXED else TabLayout.MODE_SCROLLABLE
            mBinding.tabLayout.tabGravity =
                if (shouldDistributeEvenly) TabLayout.GRAVITY_FILL else TabLayout.GRAVITY_CENTER

            TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager,false) { tab, position ->
                val textView = TextView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        if (shouldDistributeEvenly) LinearLayout.LayoutParams.MATCH_PARENT else LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    maxLines = 1
                    isSingleLine = true
                    ellipsize = null
                    text = pager[position].title
                    gravity = Gravity.CENTER
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                    if (position == 0) {
                        setTypeface(null, Typeface.BOLD)
                        setTextColor(SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.main_text))
                    } else {
                        setTypeface(null, Typeface.NORMAL)
                        setTextColor(SkinnableResourceManager.getColorStateList(requireContext(), arch.cayenne.lib.common.R.color.secondary_text))
                    }
                    if (shouldDistributeEvenly) {
                        setPadding(0, 0, 0, 0)

                    } else {
                        setPadding(18.dp2px, 0, 18.dp2px, 0)
                    }
//                    setBackgroundColor(if (position % 2 == 0) SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.red_team) else SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.res.R.color.money_color))
                    typeface = Typeface.DEFAULT
                }
                tab.customView = textView
            }.attach()
//            if (shouldDistributeEvenly) {
//                adjustTabSpacing(mBinding.tabLayout)
//            }
        }
        mBinding.root.post {
            (mBinding.tabLayout.getTabAt(0)?.customView as? TextView)?.apply {
                setTypeface(null, Typeface.BOLD)
                setTextColor(SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.main_text))
            }
            mBinding.tabLayout.clearOnTabSelectedListeners()

            mBinding.tabLayout.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
                override fun onTabSelected(tab: TabLayout.Tab,isTabClick:Boolean) {
                    mViewModel.setShowType(HomeSlipShowTypeEnum.NONE)
                        // 动画更新指示器位置
                        mBinding.customIndicator.animateIndicatorToPosition(tab.position)
                        //mBinding.viewPager.doSmartAnim(targetPosition = tab.position)
                        val vp = mBinding.viewPager
                        if(isTabClick) {
//                            vp.setCurrentItem(tab.position, false)
//                            vp.startZoomInAnim()
                            vp.startFadeAnim {
                                vp.setCurrentItem(tab.position, false)
                                it.invoke()
                            }
                        }
                    (tab.customView as? TextView)?.apply {
                        setTypeface(null, Typeface.BOLD)
                        setTextColor(SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.main_text))
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab,isTabClick:Boolean) {
                    (tab.customView as? TextView)?.apply {
                        setTypeface(null, Typeface.NORMAL)
                        setTextColor(SkinnableResourceManager.getColorStateList(requireContext(), arch.cayenne.lib.common.R.color.secondary_text))
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab,isTabClick:Boolean) {
                }
            })
        }
        mBinding.tabLayout.removeAllTips()
        // 自定義滑動行為
        mBinding.viewPager.setupViewPagerScroll(mBinding.tabLayout,mBinding.customIndicator,0.27f)
        mBinding.viewPager.setupHorizontalScrollDegree()
    }

    private fun adjustTabSpacing(tabLayout: TabLayout) {
        tabLayout.post {
            val tabStrip = tabLayout.getChildAt(0) as? LinearLayout ?: return@post
            val tabCount = tabLayout.tabCount
            if (tabCount <= 1) return@post

            // 計算所有 tab 的總寬度
            var totalTabWidth = 0
            for (i in 0 until tabCount) {
                val tabView = tabStrip.getChildAt(i)
                tabView.measure(0, 0)
                totalTabWidth += tabView.measuredWidth
            }

            // 剩餘寬度（預留兩邊 18dp）
            val remaining = tabLayout.measuredWidth - totalTabWidth - 18.dp2px * 2
            val spacing = remaining / (tabCount - 1)

            for (i in 0 until tabCount) {
                val tabView = tabStrip.getChildAt(i)
                val lp = (tabView.layoutParams as LinearLayout.LayoutParams).apply {
                    when (i) {
                        0 -> {
                            marginStart = 18.dp2px
                            marginEnd = spacing / 2
                        }
                        tabCount - 1 -> {
                            marginStart = spacing / 2
                            marginEnd = 18.dp2px
                        }
                        else -> {
                            marginStart = spacing / 2
                            marginEnd = spacing / 2
                        }
                    }
                }
                lp.width = LinearLayout.LayoutParams.WRAP_CONTENT  // 關鍵：改成 wrap_content
                lp.weight = 0f
                tabView.layoutParams = lp
            }

            tabStrip.requestLayout()
        }
    }

    private fun showDateFilter() {
        mViewModel.onDateFilter.value?.let {
            setFilterText(mBinding.tvDateFilter, mBinding.ivDateFilter, true)
            childFragmentManager.setFragmentResultListener(
                Config.KEY_RESULT,
                viewLifecycleOwner
            ) { _, bundle ->
                mViewModel.setShowType(HomeSlipShowTypeEnum.NONE)
                if (bundle.containsKey(Config.VALUE_SELECTED_DATE)) {
                    bundle.getString(Config.VALUE_SELECTED_DATE)?.let { result ->
                        val date = BetSlipDateFilterEnum.valueOf(result)
                        if (date == BetSlipDateFilterEnum.CUSTOM) {
                            val time = bundle.getLong(Config.VALUE_SELECTED_MILLISECOND)
                            mViewModel.setDateFilter(time)
                            betSlipFilterViewModel.setDateTime(
                                startTime = null,
                                endTime = time
                            )
                        } else {
                            mViewModel.setDateFilter(date)
                            betSlipFilterViewModel.setDateTime(
                                startTime = date.startTime(),
                                endTime = date.endTime()
                            )
                        }
                    }
                }
                setFilterText(mBinding.tvDateFilter, mBinding.ivDateFilter, false)
            }
            val time =
                if (it.date == BetSlipDateFilterEnum.CUSTOM && mViewModel.customTime != null) {
                    mViewModel.customTime
                } else {
                    null
                }
            DatePickerFragment.find(this, it.date, time).customShow()
        }
    }

    private fun showSportFilter() {
        mViewModel.onSportFilter.value?.let {
            setFilterText(mBinding.tvSportFilter, mBinding.ivSportFilter, true)
            childFragmentManager.setFragmentResultListener(
                Config.KEY_RESULT,
                viewLifecycleOwner
            ) { _, bundle ->
                mViewModel.setShowType(HomeSlipShowTypeEnum.NONE)
                if (bundle.containsKey(Config.VALUE_SELECTED_SPORT_ID)) {
                    bundle.getIntArray(Config.VALUE_SELECTED_SPORT_ID)?.toList()?.let { ids ->
                        mViewModel.setSportFilter(ids)
                        betSlipFilterViewModel.setIds(-1, ids)
                    }
                }
                setFilterText(mBinding.tvSportFilter, mBinding.ivSportFilter, false)
            }
            SportPickerFragment.create(childFragmentManager, mBinding.fragmentSportFilter.id).show(it.map { bean ->
                bean.sportId
            })
        }
    }

    private fun hideSportFilter() {
        val f = childFragmentManager.findFragmentByTag(SportPickerFragment::class.java.simpleName) as? SportPickerFragment ?: return
        f.collapseView()
        setFilterText(mBinding.tvSportFilter, mBinding.ivSportFilter, false)
    }

    private fun setFilterText(tv: TextView, iv: ImageView, isSelected: Boolean) {
        if (tv.isSelected == isSelected) return
        tv.isSelected = isSelected
        childFragmentManager.clearFragmentResult(Config.KEY_RESULT)
        val animatorSet = AnimatorSet()

        val startColor = tv.currentTextColor
        val endColor = if (isSelected) {
            SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.green_for_white_bg)
        } else {
            SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.main_text)
        }
        val textColorAnimator = ObjectAnimator.ofObject(
            tv,
            "textColor",
            ArgbEvaluator(),
            startColor,
            endColor
        )
        val currentRotation = iv.rotation
        // 假設 0 度是朝下，180 度是朝上
        val targetRotation = if (isSelected) 180f else 0f

        val rotationAnimator = ObjectAnimator.ofFloat(iv, "rotation", currentRotation, targetRotation)

        val arrowStartColor = if (isSelected) {
            SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.secondary_text)
        } else {
            SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.green_for_white_bg)
        }
        val arrowEndColor = if (isSelected) {
            SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.green_for_white_bg)
        } else {
            SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.secondary_text)
        }

        // --- c. ImageView 箭頭顏色漸變動畫 ---
        val arrowColorAnimator = ValueAnimator.ofObject(
            ArgbEvaluator(),
            arrowStartColor,
            arrowEndColor
        )
        arrowColorAnimator.addUpdateListener { animator ->
            val animatedColor = animator.animatedValue as Int
            iv.drawable?.let { drawable ->
                // 確保 drawable 是可變的，這樣 tint 不會影響其他地方使用此 drawable 的 View
                val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
                DrawableCompat.setTint(wrappedDrawable, animatedColor)
                iv.setImageDrawable(wrappedDrawable) // 更新 ImageView
            }
        }
        animatorSet.playTogether(textColorAnimator, rotationAnimator, arrowColorAnimator)
        animatorSet.duration = 100
        animatorSet.interpolator = LinearInterpolator()
        animatorSet.start()
    }

    override fun onResume() {
        super.onResume()
        DatePickerFragment.create(this)
    }
}