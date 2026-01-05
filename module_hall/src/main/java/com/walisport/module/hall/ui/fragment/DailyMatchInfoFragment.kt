package com.walisport.module.hall.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.DateUtils
import arch.cayenne.lib.common.utils.LanguageUtils
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.sp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ccyToSymbol
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.hall.databinding.FragmentDailyMatchInfoBinding
import com.walisport.module.hall.ui.viewmodel.DailyMatchInfoViewModel
import kotlin.reflect.KClass

/**
 * 每日比赛信息fragment,  用在游戏大厅的“全部”子页面banner区域
 */
class DailyMatchInfoFragment :
    BaseFragment<DailyMatchInfoViewModel, FragmentDailyMatchInfoBinding>() {

    override val vbClass: KClass<FragmentDailyMatchInfoBinding> =
        FragmentDailyMatchInfoBinding::class
    override val vmClass: KClass<DailyMatchInfoViewModel> = DailyMatchInfoViewModel::class

    private var timer: CountDownTimer? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.root.clickNoRepeat {
            navigate(arch.cayenne.lib.res.R.string.nav_module_competition_fragment.deeplink())
        }
    }

    override suspend fun createObserver() {

        mViewModel.dailyBetMatchDataBeanFlow.collect {
            it?.let {
                mBinding.tvTimer.text = DateUtils.formatMillisToHMS(it.remainingTime)
                //启动定时器，每秒对剩余时间进行减一，并更新UI
                var remainingTime = it.remainingTime
                timer?.cancel()
                timer = object : CountDownTimer(remainingTime * 1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        remainingTime--
                        mBinding.tvTimer.text = DateUtils.formatMillisToHMS(remainingTime)
                    }

                    override fun onFinish() {
                        mBinding.tvTimer.text = DateUtils.formatMillisToHMS(0)
                    }
                }
                timer?.start()

                mBinding.tvCurrencySymbol.text = "￥"
                mBinding.tvBonus.text = String.format("%,d", 1457000)

                mBinding.tvMoney.text = String.format("${it.ccy.ccyToSymbol()}%,d", it.myBetScore)

                mBinding.tvRank.text =
                    if (it.myRank == null) {
                        //如果系统语言是中文
                        if (LanguageUtils.isChinese(requireContext())) {
                            "未上榜"
                        } else {
                            "N/A"
                        }
                    } else {
                        if (LanguageUtils.isChinese(requireContext())) {
                            //为“名”字添加spannable，缩小字体
                            val rankStr = it.myRank.toString()
                            val spannable = android.text.SpannableString("${rankStr}名")
                            spannable.setSpan(
                                android.text.style.AbsoluteSizeSpan(11f.sp2px.toInt()),
                                rankStr.length,
                                rankStr.length + 1,
                                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            spannable
                        } else {
                            it.myRank.toString()
                        }
                    }

            }
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getDayMatchDetail()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }


}