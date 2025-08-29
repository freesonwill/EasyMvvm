package arch.cayenne.module.betslip.ui.fragment

import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextPaint
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.animateIndicatorToPosition
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.setupViewPagerScroll
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipDateFilterEnum
import arch.cayenne.module.betslip.data.constants.Config
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
    private var skipAnyAnim = true
    override fun initView(savedInstanceState: Bundle?) {
        val array = resources.getStringArray(R.array.bet_slip_menus)
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
        mBinding.tvDateFilter.setOnClickListener {
            mViewModel.setSportViewCollapse()
            showDateFilter()
        }
        mBinding.tvSportFilter.setOnClickListener {
            mViewModel.toggleSportView()
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
        mViewModel.isShowSportView.observeEvent(viewLifecycleOwner, this) {
            if (it) {
                showSportFilter()
            } else {
                hideSportFilter()
            }
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

            TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager) { tab, position ->
                val textView = TextView(requireContext()).apply {
                    maxLines = 1
                    isSingleLine = true
                    ellipsize = null
                    text = pager[position].title
                    gravity = Gravity.CENTER
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                    setTextColor(SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.main_text))
                    if (!shouldDistributeEvenly) {
                        setPadding(18.dp2px, 0, 18.dp2px, 0)
                    }
                    typeface = Typeface.DEFAULT
                }
                tab.customView = textView
            }.attach()
        }
        mBinding.root.post {
            mBinding.tabLayout.clearOnTabSelectedListeners()
            mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    mViewModel.setSportViewCollapse()
                    if (skipAnyAnim) {
                        // 动画更新指示器位置
                        mBinding.customIndicator.animateIndicatorToPosition(tab.position, 0)
                        mBinding.viewPager.setCurrentItem(tab.position, false)
                    }
                    (tab.customView as? TextView)?.setTypeface(null, Typeface.BOLD)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    (tab?.customView as? TextView)?.setTypeface(null, Typeface.NORMAL)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        }
        mBinding.tabLayout.removeAllTips()
        // 自定義滑動行為
        mBinding.viewPager.setupViewPagerScroll(mBinding.tabLayout,mBinding.customIndicator,0.27f){
            skipAnyAnim = it
        }
    }

    private fun showDateFilter() {
        mViewModel.onDateFilter.value?.let {
            setFilterText(mBinding.tvDateFilter, true)
            childFragmentManager.setFragmentResultListener(
                Config.KEY_RESULT,
                viewLifecycleOwner
            ) { _, bundle ->
                childFragmentManager.clearFragmentResultListener(Config.KEY_RESULT)
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
                setFilterText(mBinding.tvDateFilter, false)
            }
            val time =
                if (it.date == BetSlipDateFilterEnum.CUSTOM && mViewModel.customTime != null) {
                    mViewModel.customTime
                } else {
                    null
                }
            DatePickerFragment.newInstance(it.date, time).show(childFragmentManager)
        }
    }

    private fun showSportFilter() {
        mViewModel.onSportFilter.value?.let {
            setFilterText(mBinding.tvSportFilter, true)
            childFragmentManager.setFragmentResultListener(
                Config.KEY_RESULT,
                viewLifecycleOwner
            ) { _, bundle ->
                childFragmentManager.clearFragmentResultListener(Config.KEY_RESULT)
                if (bundle.containsKey(Config.VALUE_SELECTED_SPORT_ID)) {
                    bundle.getIntArray(Config.VALUE_SELECTED_SPORT_ID)?.toList()?.let { ids ->
                        mViewModel.setSportFilter(ids)
                        betSlipFilterViewModel.setIds(-1, ids)
                    }
                }
                setFilterText(mBinding.tvSportFilter, false)
            }
            SportPickerFragment.newInstance(
                it.map { bean ->
                    bean.sportId
                }
            ).show(childFragmentManager, mBinding.fragmentSportFilter.id)
        }
    }

    private fun hideSportFilter() {
        val f = childFragmentManager.findFragmentByTag(SportPickerFragment::class.java.simpleName) as? SportPickerFragment ?: return
        f.collapseView()
        setFilterText(mBinding.tvSportFilter, false)
    }

    private fun setFilterText(view: TextView, isSelected: Boolean) {
        if (isSelected) {
            view.setTextColor(
                SkinnableResourceManager.getColorStateList(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.green_for_white_bg
                )
            )
            view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0, 0, R.mipmap.ic_bet_slip_filter_selected, 0
            )
        } else {
            view.setTextColor(
                SkinnableResourceManager.getColorStateList(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.main_text
                )
            )
            view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0, 0, R.mipmap.ic_bet_slip_filter, 0
            )
        }
    }
}