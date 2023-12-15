package com.xcjh.app.ui.home.schedule

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.drake.brv.listener.ItemDifferCallback
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setDifferModels
import com.drake.brv.utils.setup
import com.drake.statelayout.StateConfig
import com.scwang.smart.refresh.header.ClassicsHeader
import com.xcjh.app.MyApplication
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.AnchorBean
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.bean.PostSchMatchListBean
import com.xcjh.app.databinding.FrScheduletwoBinding
import com.xcjh.app.databinding.ItemJsBinding
import com.xcjh.app.databinding.ItemSchAllBinding
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.TimeUtil
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.horizontal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScheduleChildTwoFragment : BaseFragment<ScheduleVm, FrScheduletwoBinding>() {
    var strTimeZu: MutableList<String> = ArrayList<String>()
    val animatorSet = AnimatorSet()
    var mview: View? = null
    var index: Int = 0
    var page = 1
    var status = 0
    var pageSize = 1000
    var matchtype: String? = ""
    var competitionId: String = ""
    var listdata: MutableList<MatchBean> = ArrayList<MatchBean>()
    lateinit var strTime: String
    var isVisble = false
    var calendarTime: String = ""
    var mTabPosition = 0
    var mPushPosition = 0
    lateinit var endTime: String
    lateinit var strTimeRuslt: String
    lateinit var endTimeResult: String
    var tabName: String? = ""
    var mOneTabIndex = 0
    var mTwoTabIndex = 0
    var mCurrentOneTabIndex = 0
    var mCurrentTwoTabIndex = 0
    var isResh = false

    companion object {
        var mTitles: Array<out String>? = null
        private val MATCHTYPE = "matchtype"
        private val COMID = "competitionId"
        private val STATUS = "status"
        private val OneIndex = "oneIndex"
        private val TwoIndex = "TwoIndex"
        fun newInstance(
            matchtype: String,
            competitionId: String,
            status: Int,
            oneindex: Int,
            twoindex: Int,
        ): ScheduleChildTwoFragment {
            val args = Bundle()
            args.putString(MATCHTYPE, matchtype);
            args.putString(COMID, competitionId);
            args.putInt(STATUS, status);
            args.putInt(OneIndex, oneindex);
            args.putInt(TwoIndex, twoindex);
            val fragment = ScheduleChildTwoFragment()
            fragment.arguments = args
            return fragment
        }
    }


    fun initTime() {
        LogUtils.d("本页面tabname=$tabName")
        strTime = TimeUtil.gettimenowYear().toString()
        if ((mCurrentOneTabIndex != 3 && mCurrentTwoTabIndex == 0) ||
            (mCurrentOneTabIndex == 3 && (mCurrentTwoTabIndex == 0 || mCurrentTwoTabIndex == 1))
        ) {//只取一天
            if (calendarTime.isNotEmpty()) {
                strTime = calendarTime
                endTime = calendarTime
            } else {
                endTime = strTime
            }
            return
        }
        endTime = TimeUtil.addDayEgls("0", 2).toString()
        strTimeRuslt = TimeUtil.gettimenowYear().toString()
        endTimeResult = TimeUtil.getDateStr(strTime, 2).toString()

        if (mCurrentOneTabIndex == 3) {
            if (calendarTime.isNotEmpty()) {
                endTimeResult = TimeUtil.getDateStr(calendarTime, 2).toString()
                if (mCurrentOneTabIndex == 3) {
                    endTime = calendarTime
                    strTime = endTimeResult
                }
            } else {
                strTime = endTimeResult
                endTime = strTimeRuslt
            }
        } else {
            if (calendarTime.isNotEmpty()) {
                strTime = calendarTime
                endTime = TimeUtil.addDayEgls(calendarTime, 2).toString()
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {

        try {
            val bundle = arguments
            if (bundle != null) {
                matchtype = bundle.getString(ScheduleChildTwoFragment.MATCHTYPE)!!
                competitionId = bundle.getString(ScheduleChildTwoFragment.COMID)!!
                status = bundle.getInt(ScheduleChildTwoFragment.STATUS)
                mOneTabIndex = bundle.getInt(ScheduleChildTwoFragment.OneIndex)
                mTwoTabIndex = bundle.getInt(ScheduleChildTwoFragment.TwoIndex)
            }
            mDatabind.state.apply {
                StateConfig.setRetryIds(R.id.ivEmptyIcon, R.id.txtEmptyName)
                onEmpty {
                    this.findViewById<TextView>(R.id.txtEmptyName).text =
                        resources.getString(R.string.nomatch)
                    this.findViewById<ImageView>(R.id.ivEmptyIcon)
                        .setImageDrawable(resources.getDrawable(R.drawable.ic_empet_all))
                }
            }
            mDatabind.smartCommon.setRefreshHeader(ClassicsHeader(requireContext()))
            mDatabind.recBottom.run {
                distance(0, 0, 0, 15)
            }
            (mDatabind.recBottom.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false//防止item刷新的时候闪烁
            mDatabind.recBottom.linear().setup {
                addType<MatchBean>(R.layout.item_sch_all)

                onBind {

                    // findView<TextView>(R.id.tvname).text = getModel<MatchBean>().competitionName
                    var binding = getBinding<ItemSchAllBinding>()
                    var item = _data as MatchBean
                    // 设置item数据

                    var time = TimeUtil.timeStamp2Date(item!!.matchTime.toLong(), null)
                    if (item.focus) {
                        binding.tvcollect.setBackgroundResource(R.drawable.ic_focus_s)
                    } else {
                        binding.tvcollect.setBackgroundResource(R.drawable.ic_focus_n)
                    }
                    binding.tvmiddletime.text = time!!.substring(0, 10)
                    when (item.visbleTime) {
                        0 -> {
                            if (time!!.substring(0, 10) == TimeUtil.gettimenowYear()) {
                                if (strTimeZu.size == 0) {
                                    item.visbleTime = 2
                                    binding.tvmiddletime.visibility = View.GONE
                                } else {
                                    if (strTimeZu[strTimeZu.size - 1] == time!!.substring(0, 10)) {
                                        binding.tvmiddletime.visibility = View.GONE
                                        item.visbleTime = 2
                                    } else {
                                        binding.tvmiddletime.visibility = View.VISIBLE
                                        item.visbleTime = 1
                                        strTimeZu.add(time!!.substring(0, 10))
                                    }
                                }
                            } else {
                                if (strTimeZu.size == 0) {
                                    binding.tvmiddletime.visibility = View.VISIBLE
                                    item.visbleTime = 1
                                    strTimeZu.add(time!!.substring(0, 10))
                                } else {
                                    if (strTimeZu[strTimeZu.size - 1] == time!!.substring(0, 10)) {
                                        binding.tvmiddletime.visibility = View.GONE
                                        item.visbleTime = 2
                                    } else {
                                        binding.tvmiddletime.visibility = View.VISIBLE
                                        item.visbleTime = 1
                                        strTimeZu.add(time!!.substring(0, 10))
                                    }


                                }

                            }
                        }

                        1 -> {//显示
                            binding.tvmiddletime.visibility = View.VISIBLE
                        }

                        2 -> {//不显示
                            binding.tvmiddletime.visibility = View.GONE

                        }
                    }

                    LogUtils.d("直播数据" + item.anchorList)

                    when (item.matchType) {//比赛类型：1：足球；2：篮球,可用值:1,2
                        "1" -> {
                            binding.tvhafl.text =
                                context.resources.getString(R.string.hafl_rices) + "" + item.homeHalfScore + "-" + item.awayHalfScore
                            binding.tvtime.text = time!!.substring(11, 16)
                            binding.tvname.text = item.competitionName
                            binding.tvnameLeft.text = item.homeName
                            binding.tvnameRight.text = item.awayName
                            Glide.with(context).load(item.homeLogo)
                                .placeholder(R.drawable.default_team_logo).into(binding.tvflagLeft)
                            Glide.with(context).load(item.awayLogo)
                                .placeholder(R.drawable.default_team_logo).into(binding.tvflagRight)
                            binding.ivtype.setBackgroundResource(R.drawable.football)
                            when (item.status) {
                                "0" -> binding.tvstatus.visibility = View.GONE
                                "1" -> {
                                    binding.tvvs.text = "VS"
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_94999f
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_37373d
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.main_txt_wks)

                                }

                                "2" -> {
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.txtMatchAnimation.visibility = View.VISIBLE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_e6820c
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_34a853
                                        )
                                    )
                                    binding.tvstatus.text =
                                        if (item.runTime == null)
                                            "0"
                                        else {
                                            item.runTime
                                        }

                                    initAnimation(binding.txtMatchAnimation)
                                }

                                "3" -> {
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_e6820c
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_34a853
                                        )
                                    )
                                    binding.tvstatus.text = context.resources.getString(
                                        R.string.zc
                                    )
                                }

                                "4" -> {
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.txtMatchAnimation.visibility = View.VISIBLE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_e6820c
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_34a853
                                        )
                                    )
                                    binding.tvstatus.text =
                                        if (item.runTime == null)
                                            "0"
                                        else {
                                            item.runTime
                                        }
                                    initAnimation(binding.txtMatchAnimation)
                                }

                                "5", "6" -> {
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.txtMatchAnimation.visibility = View.VISIBLE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_e6820c
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_34a853
                                        )
                                    )
                                    binding.tvstatus.text = context.resources.getString(
                                        R.string.over_time
                                    )
                                    initAnimation(binding.txtMatchAnimation)

                                }

                                "7" -> {
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_e6820c
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_34a853
                                        )
                                    )
                                    binding.tvstatus.text = context.resources.getString(
                                        R.string.main_dqdz
                                    )
                                    initAnimation(binding.txtMatchAnimation)

                                }

                                "8" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_999999
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_34a853
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.main_txt_over)

                                }

                                "9" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_F7DA73
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_37373d
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.main_txt_tc)

                                }

                                "10" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_F7DA73
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_37373d
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.main_txt_zd)

                                }

                                "11" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_8a91a0
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_37373d
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.main_txt_yz)

                                }

                                "12" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_8a91a0
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_37373d
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.main_txt_qx)

                                }

                                "13" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_8a91a0
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_37373d
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.main_txt_dd)

                                }

                                else -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_8a91a0
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_37373d
                                        )
                                    )
                                    val date = Date(item.matchTime.toLong())
                                    var formatter =
                                        SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.main_txt_over)
                                }
                            }
                        }

                        "2" -> {
                            binding.tvhafl.text =
                                context.resources.getString(R.string.hafl_rices) + "" + item.awayHalfScore + "-" + item.homeHalfScore
                            binding.tvtime.text = time!!.substring(11, 16)
                            binding.tvname.text = item.competitionName
                            binding.tvnameLeft.text = item.awayName
                            binding.tvnameRight.text = item.homeName
                            Glide.with(context).load(item.awayLogo)
                                .placeholder(R.drawable.default_team_logo).into(binding.tvflagLeft)
                            Glide.with(context).load(item.homeLogo)
                                .placeholder(R.drawable.default_team_logo).into(binding.tvflagRight)
                            binding.ivtype.setBackgroundResource(R.drawable.basketball)
                            when (item.status) {
                                "0" -> binding.tvstatus.visibility = View.GONE
                                "1" -> {
                                    binding.tvvs.text = "VS"
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_94999f
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_37373d
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.main_txt_wks)

                                }

                                "2" -> {
                                    binding.txtMatchAnimation.visibility = View.VISIBLE
                                    binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_e6820c
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_e6820c
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(
                                            R.string.main_txt_basketball_phase,
                                            "一"
                                        )
                                    initAnimation(binding.txtMatchAnimation)

                                }

                                "3" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_e6820c
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_34a853
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(
                                            R.string.main_txt_basketball_phase,
                                            "一"
                                        ) + context.resources.getString(
                                            R.string.finis
                                        )
                                    //initAnimation(binding.txtMatchAnimation)

                                }

                                "4" -> {
                                    binding.txtMatchAnimation.visibility = View.VISIBLE
                                    binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_e6820c
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_34a853
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(
                                            R.string.main_txt_basketball_phase,
                                            "二"
                                        )
                                    initAnimation(binding.txtMatchAnimation)

                                }

                                "5" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_e6820c
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_34a853
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(
                                            R.string.main_txt_basketball_phase,
                                            "二"
                                        ) + context.resources.getString(
                                            R.string.finis
                                        )
                                    //initAnimation(binding.txtMatchAnimation)

                                }

                                "6" -> {
                                    binding.txtMatchAnimation.visibility = View.VISIBLE
                                    binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_e6820c
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_34a853
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(
                                            R.string.main_txt_basketball_phase,
                                            "三"
                                        )
                                    initAnimation(binding.txtMatchAnimation)

                                }

                                "7" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_e6820c
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_34a853
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(
                                            R.string.main_txt_basketball_phase,
                                            "三"
                                        ) + context.resources.getString(
                                            R.string.finis
                                        )
                                    // initAnimation(binding.txtMatchAnimation)

                                }

                                "8" -> {
                                    binding.txtMatchAnimation.visibility = View.VISIBLE
                                    binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_e6820c
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_34a853
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(
                                            R.string.main_txt_basketball_phase,
                                            "四"
                                        )
                                    initAnimation(binding.txtMatchAnimation)
                                }

                                "9" -> {
                                    binding.txtMatchAnimation.visibility = View.VISIBLE
                                    binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_e6820c
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_34a853
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.over_time)
                                    initAnimation(binding.txtMatchAnimation)
                                }

                                "10" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_999999
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_34a853
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.main_txt_over)

                                }

                                "11", "12", "13", "14", "15" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_999999
                                        )
                                    )
                                    binding.tvvs.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_37373d
                                        )
                                    )
                                    when (item.status) {
                                        "11" -> {
                                            binding.tvstatus.text =
                                                context.resources.getString(R.string.main_txt_zd)
                                        }

                                        "12" -> {
                                            binding.tvstatus.text =
                                                context.resources.getString(R.string.main_txt_qx)
                                        }

                                        "13" -> {
                                            binding.tvstatus.text =
                                                context.resources.getString(R.string.main_txt_zd)
                                        }

                                        "14" -> {
                                            binding.tvstatus.text =
                                                context.resources.getString(R.string.main_txt_yz)
                                        }

                                        "15" -> {
                                            binding.tvstatus.text =
                                                context.resources.getString(R.string.main_txt_dd)
                                        }
                                    }

                                }
                            }
                        }
                    }

                    if (item.anchorList != null && item.anchorList.isNotEmpty()) {
                        binding.conlive.visibility = View.VISIBLE
                        if (binding.rec.itemDecorationCount == 0) {//加个判断
                            binding.rec.run {
                                horizontal()
                                distance(30, 30, 0, 0)
                            }
                        }

                        binding.rec.setup {
                            addType<AnchorBean>(R.layout.item_js)
                            onBind {
                                var binding1 = getBinding<ItemJsBinding>()
                                var item1 = _data as AnchorBean
                                binding1.tvname.text = item1.nickName
                                Glide.with(context).load(item1.userLogo)
                                    .placeholder(R.drawable.icon_avatar).into(binding1.ivhead)
                                binding1.linroot.setOnClickListener {
                                    MatchDetailActivity.open(
                                        matchType = item.matchType,
                                        matchId = item.matchId,
                                        matchName = "${item.homeName}VS${item.awayName}",
                                        anchorId = item1.userId,
                                        videoUrl = ""
                                    )

                                }
                            }
                        }.models = item.anchorList


                    } else {
                        binding.conlive.visibility = View.GONE
                    }

                    binding.relcoolect.setOnClickListener {
                        mview = binding.tvcollect
                        index = modelPosition
                        if (item!!.focus) {
                            mViewModel.getUnnotice(
                                item!!.matchId,
                                item!!.matchType
                            )


                        } else {
                            mViewModel.getNotice(
                                item!!.matchId,
                                item!!.matchType
                            )


                        }


                    }
                    binding.conroot.setOnClickListener {
                        MatchDetailActivity.open(
                            matchType = item.matchType,
                            matchId = item.matchId,
                            matchName = "${item.homeName}VS${item.awayName}",
                            anchorId = "",
                            videoUrl = ""
                        )

                    }


                }
                itemDifferCallback = object : ItemDifferCallback {
                    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                        return (oldItem as MatchBean).matchId == (newItem as MatchBean).matchId
                    }

                    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                        return (oldItem as MatchBean).homeHalfScore == (newItem as MatchBean).homeHalfScore
                    }

                    override fun getChangePayload(oldItem: Any, newItem: Any): Any? {
                        return true
                    }
                }
            }.models = listdata

            mDatabind.smartCommon.setOnRefreshListener {

                mDatabind.state.showLoading()
                getData(true)


            }
            appViewModel.updateganlerTime.observe(this) {

                if (isAdded && mCurrentOneTabIndex == mOneTabIndex && mCurrentTwoTabIndex == mTwoTabIndex) {
                    calendarTime = it
                    getData(true)


                }
            }
            appViewModel.updateSchedulePosition.observeForever {

                if (isAdded) {
                    mCurrentOneTabIndex = it.currtOne
                    mCurrentTwoTabIndex = it.currtTwo
                    if (isResh && mCurrentOneTabIndex == mOneTabIndex && mCurrentTwoTabIndex == mTwoTabIndex) {
                        isVisble = true
                        calendarTime = ""
                        isResh = false
                        mDatabind.smartCommon.autoRefresh()
                    } else {
                        isVisble = false
                    }

                    LogUtils.d("第1及页面索引==" + mCurrentOneTabIndex + "第2及页面索引==" + mCurrentTwoTabIndex)

                }
            }

            appViewModel.updateCollection.observeForever {
                if (isAdded) {
                    if (it) {
                        getData(false)
                    }
                }
            }
            appViewModel.appPushMsg.observeForever {
                if (isAdded) {
                    if (mCurrentOneTabIndex == mOneTabIndex && mCurrentTwoTabIndex == mTwoTabIndex) {
                        for (j in 0 until it.size) {
                            for (i in 0 until mDatabind.recBottom.models?.size!!) {
                                var bean: MatchBean = mDatabind.recBottom.models!![i] as MatchBean
                                // if (bean.homeHalfScore == "1") {

                                if (bean.matchId == it[j].matchId.toString() && bean.matchType == it[j].matchType.toString()) {
                                    bean.awayHalfScore = it[j].awayHalfScore.toString()
                                    bean.awayScore = it[j].awayScore.toString()
                                    bean.homeHalfScore = it[j].homeHalfScore.toString()
                                    bean.homeScore = it[j].homeScore.toString()
                                    bean.runTime = it[j].runTime.toString()
                                    bean.status = it[j].status.toString()
                                    mDatabind.recBottom.bindingAdapter.notifyItemChanged(i)
                                    when (mCurrentOneTabIndex) {//已完成比赛下次要刷新
                                        0, 1, 2 -> {
                                            if ((bean.status == "10" && bean.matchType == "2") ||
                                                (bean.status == "8" && bean.matchType == "1")
                                            ) {
                                                isResh = true
                                            }
                                        }

                                        3 -> {


                                        }

                                    }
                                }
                            }
                        }
                    } else {


                        for (j in 0 until it.size) {
                            for (i in 0 until mDatabind.recBottom.models?.size!!) {
                                var bean: MatchBean = mDatabind.recBottom.models!![i] as MatchBean
                                // if (bean.homeHalfScore == "1") {
                                if (bean.matchId == it[j].matchId.toString() && bean.matchType == it[j].matchType.toString() && bean.status != it[j].status.toString()) {
                                    isResh = true
                                    break
                                }
                            }
                        }
                    }
                }
            }
            appViewModel.updateLoginEvent.observe(this) {
                if (it) {

                } else {
                    if (isAdded) {
                        if (mCurrentOneTabIndex == mOneTabIndex && mCurrentTwoTabIndex == mTwoTabIndex) {
                            mDatabind.smartCommon.autoRefresh()
                        } else {

                            isResh = true
                        }
                    }
                }
            }
        } catch (e: Exception) {

        }

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        //  isVisble = isVisibleToUser
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    override fun onPause() {
        super.onPause()
        // isVisble = false
    }

    override fun onResume() {
        super.onResume()
        LogUtils.d("第1及页面索引==" + mCurrentOneTabIndex + "第2及页面索引==" + mCurrentTwoTabIndex)
//        if (isResh) {
//            isResh = false
//            mDatabind.smartCommon.autoRefresh()
//            //getData(false)
//        }
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        // getData(false)
        mDatabind.smartCommon.autoRefresh()
    }

    override fun lazyLoadTime(): Long {
        return 0
    }

    fun getData(iLoading: Boolean) {
        initTime()
        mViewModel.getHotMatchDataList(
            iLoading, PostSchMatchListBean(
                competitionId, page,
                endTime,
                matchtype!!, pageSize, strTime,
                status
            )
        )
    }

    fun initAnimation(view: AppCompatTextView) {
        animatorSet.removeAllListeners()
        view.clearAnimation()
        val fadeIn =
            ObjectAnimator.ofFloat(
                view,
                "alpha",
                0f,
                1f
            )
        fadeIn.duration = 500
        fadeIn.startDelay = 200 // 延迟200毫秒开始动画

        val fadeOut =
            ObjectAnimator.ofFloat(
                view,
                "alpha",
                1f,
                0f
            )
        fadeOut.duration = 500
        fadeOut.startDelay = 200 // 延迟200毫秒开始动画


        animatorSet.playSequentially(fadeIn, fadeOut) // 顺序播放渐显和渐隐动画
        animatorSet.startDelay = 200 // 延迟200毫秒开始第一次播放动画
        animatorSet.addListener(object : AnimatorListenerAdapter() {


            override fun onAnimationEnd(animation: Animator) {
                // 动画结束时重新播放
                super.onAnimationEnd(animation)
                animatorSet.start()
            }
        })
        animatorSet.start()
    }

    override fun createObserver() {
        val empty = requireActivity().layoutInflater!!.inflate(R.layout.layout_empty, null)


        mViewModel.hotMatchList.observe(this) {
            mDatabind.recBottom.mutable.clear()
            mDatabind.recBottom.scrollToPosition(0)
            mDatabind.recBottom.bindingAdapter.notifyDataSetChanged()
            if (it.isSuccess) {
                strTimeZu.clear()
                //成功
                when {
                    //第一页并没有数据 显示空布局界面
                    it.isFirstEmpty -> {
                        if (mDatabind.recBottom.models?.size != null) {
                            mDatabind.recBottom.mutable.clear()
                            mDatabind.recBottom.bindingAdapter.notifyDataSetChanged()
                        }
                        mDatabind.smartCommon.finishRefresh()
                        mDatabind.state.showEmpty()

                    }
                    //是第一页
                    it.isRefresh -> {


                        mDatabind.smartCommon.finishRefresh()
                        mDatabind.smartCommon.resetNoMoreData()
                        // mAdapter.submitList(null)
                        mDatabind.recBottom.models = it.listData
//                        if (mDatabind.recBottom.models != null && isClick) {
//                            mDatabind.recBottom.mutable.clear()
//                            // listdata.clear()
//                            mDatabind.recBottom.models = it.listData
//                        } else {
//                            mDatabind.recBottom.setDifferModels(it.listData, false)
//                        }
                        mDatabind.state.showContent()

                        //listdata.addAll(it.listData)
                        //it.listData[0].homeName=it.listData[0].homeName+index1


                    }
                    //不是第一页
                    else -> {
                        if (it.listData.isEmpty()) {
                            // mDatabind.smartCommon.setEnableLoadMore(false)
                            mDatabind.smartCommon.finishLoadMoreWithNoMoreData()
                        } else {
                            // mDatabind.smartCommon.setEnableLoadMore(true)
                            mDatabind.smartCommon.finishLoadMore()
                            mDatabind.recBottom.setDifferModels(it.listData, true)
                            mDatabind.state.showContent()
                        }

                    }
                }
            } else {

                //失败
                if (it.isRefresh) {
                    mDatabind.smartCommon.finishRefresh()
                    //如果是第一页，则显示错误界面，并提示错误信息
                    if (mDatabind.recBottom.models != null) {
                        //  mDatabind.recBottom.mutable.clear()
                    }
                    mDatabind.state.showEmpty()
                } else {
                    mDatabind.smartCommon.finishLoadMore(false)
                }
            }
        }
        mViewModel.unnoticeData.observe(this) {
            mview!!.setBackgroundResource(R.drawable.ic_focus_n)
            //mAdapter.getItem(index)!!.focus = false
            mDatabind.recBottom.bindingAdapter.getModel<MatchBean>(index).focus = false
            mDatabind.recBottom.bindingAdapter.notifyItemChanged(index)
        }
        mViewModel.noticeData.observe(this) {
            mview!!.setBackgroundResource(R.drawable.ic_focus_s)
            mDatabind.recBottom.bindingAdapter.getModel<MatchBean>(index).focus = true
            mDatabind.recBottom.bindingAdapter.notifyItemChanged(index)
        }
    }
}