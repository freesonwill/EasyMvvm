package arch.cayenne.module.betslip.ui.fragment

import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextPaint
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.helper.ViewPagerAnimHelper
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
    override val vbClass: KClass<FragmentHomeBetslipBinding> = FragmentHomeBetslipBinding::class
    override val vmClass: KClass<HomeBetSlipViewModel> = HomeBetSlipViewModel::class
    private val betSlipFilterViewModel: BetSlipFilterViewModel by viewModel()
    private val viewPagerAnimHelper by lazy {
        ViewPagerAnimHelper()
    }

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
    }

    override fun initData() {
        super.initData()
        betSlipFilterViewModel.init()
    }

    override fun initListener() {
        mBinding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        mBinding.tvDateFilter.setOnClickListener {
            showDateFilter()
        }
        mBinding.tvSportFilter.setOnClickListener {
            showSportFilter()
        }
    }

    override fun createObserver() {
        mViewModel.onDateFilter.observe(viewLifecycleOwner) {
            mBinding.tvDateFilter.text = it.title
        }
        mViewModel.onSportFilter.observe(viewLifecycleOwner) { list ->
            val selectedNames = list.map { it.sportName }

            val displayText = when (selectedNames.size) {
                1 -> selectedNames.first()
                else -> selectedNames.joinToString(", ")
            }

            mBinding.tvSportFilter.text = displayText
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
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    (tab?.customView as? TextView)?.setTypeface(null, Typeface.BOLD)
                    viewPagerAnimHelper.doDirectViewPagerAnim(
                        targetPosition = tab?.position ?: 0,
                        viewPager = mBinding.viewPager,
                        fakeViewPager = mBinding.ivFaker
                    )
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    (tab?.customView as? TextView)?.setTypeface(null, Typeface.NORMAL)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        }
        mBinding.tabLayout.removeAllTips()
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
                mBinding.clTitle.height + mBinding.clFilter.height + mBinding.tabLayout.height,
                it.map { bean ->
                    bean.sportId
                }
            ).show(childFragmentManager, mBinding.main.id)
        }
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