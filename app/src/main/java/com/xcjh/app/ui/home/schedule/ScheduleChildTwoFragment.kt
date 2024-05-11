package com.xcjh.app.ui.home.schedule

import android.animation.Animator
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.SimpleItemAnimator
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.drake.brv.listener.ItemDifferCallback
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setDifferModels
import com.drake.brv.utils.setup
import com.drake.statelayout.StateConfig
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
import com.xcjh.app.utils.SoundManager
import com.xcjh.app.utils.TimeUtil
import com.xcjh.app.utils.judgeLogin
import com.xcjh.app.view.CustomHeader
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.grid
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.view.clickNoRepeat
import kotlinx.android.synthetic.main.item_sch_all.ivsc
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ScheduleChildTwoFragment : BaseFragment<ScheduleVm, FrScheduletwoBinding>() {
    var strTimeZu: MutableList<String> = ArrayList<String>()
    val animatorSet = AnimatorSet()
    var mview: LottieAnimationView? = null
    var mview1: ImageView? = null
    var index: Int = 0
    var page = 1
    var status = ""
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
    //第一个
    var mCurrentOneTabIndex = 0
    //第二个
    var mCurrentTwoTabIndex = 0
    var isResh = false
    var strYes = "'"
    var strNo = "2"
    var iscollect = true

    // 在你的Activity中声明一个成员变量来保存RecyclerView的状态
    private var recyclerViewState: Parcelable? = null

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
            status: String,
            oneindex: Int,
            twoindex: Int,
        ): ScheduleChildTwoFragment {
            val args = Bundle()
            args.putString(MATCHTYPE, matchtype);
            args.putString(COMID, competitionId);
            args.putString(STATUS, status);
            args.putInt(OneIndex, oneindex);
            args.putInt(TwoIndex, twoindex);
            val fragment = ScheduleChildTwoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    fun getCanleTime(): String {
        // 子 Fragment 的逻辑操作
        return calendarTime
    }

    fun initTime() {
        try {


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




            //以前是查询两天的
            //当前日期加2天
            endTime = TimeUtil.addDayEgls("0", 2).toString()
            //当前日期
            strTimeRuslt = TimeUtil.gettimenowYear().toString()
            //结束日期
            endTimeResult = TimeUtil.getDateStr(strTime, 2).toString()

            //现在全部是当前日期
            if (calendarTime.isNotEmpty()) {
                strTime = calendarTime
                endTime = calendarTime
            }else{
                strTime = strTimeRuslt
                endTime = strTimeRuslt
            }
            return
            //=======

            //以前的逻辑可以查询当前日期往后或者往前2天
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
                    Log.i("SSSSss","=====")
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun initView(savedInstanceState: Bundle?) {

        try {
            val bundle = arguments
            if (bundle != null) {
                matchtype = bundle.getString(ScheduleChildTwoFragment.MATCHTYPE)!!
                competitionId = bundle.getString(ScheduleChildTwoFragment.COMID)!!
                status = bundle.getString(ScheduleChildTwoFragment.STATUS)!!
                mOneTabIndex = bundle.getInt(ScheduleChildTwoFragment.OneIndex)
                mTwoTabIndex = bundle.getInt(ScheduleChildTwoFragment.TwoIndex)
            }
            mDatabind.smartCommon.setEnableRefresh(false)
            mDatabind.smartCommon.setEnableOverScrollDrag(true)
            mDatabind.state.apply {
                StateConfig.setRetryIds(R.id.ivEmptyIcon, R.id.txtEmptyName)
                onEmpty {
                    this.findViewById<TextView>(R.id.txtEmptyName).text =
                        resources.getString(R.string.nomatch)
                    this.findViewById<ImageView>(R.id.ivEmptyIcon)
                        .setImageDrawable(resources.getDrawable(R.drawable.ic_empet_all))
                    this.findViewById<ImageView>(R.id.ivEmptyIcon).setOnClickListener { }
                }
            }
            mDatabind.smartCommon.setRefreshHeader(CustomHeader(requireContext()))
            mDatabind.recBottom.run {
                distance(0, 0, 0, 0)
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

                    var time = TimeUtil.getDayOfWeek(item!!.matchTime.toLong(), context)
                    var time1 = TimeUtil.timeStamp2Date(item!!.matchTime.toLong(), null)
                    if (item.focus) {
                        LogUtils.d("收藏了"+bindingAdapterPosition)
                        binding.ivsc.setBackgroundResource(R.drawable.sc_shoucang_icon2)
                    } else {
                        binding.ivsc.setBackgroundResource(R.drawable.sc_shoucang_icon1)
                    }
                    binding.tvmiddletime.text = time
                    when (item.visbleTime) {
                        0 -> {
                            if (time1!!.substring(0, 10) == TimeUtil.gettimenowYear()) {
                                if (strTimeZu.size == 0) {
                                    item.visbleTime = 2
                                    binding.tvmiddletime.visibility = View.GONE
                                } else {
                                    if (strTimeZu[strTimeZu.size - 1] == time) {
                                        binding.tvmiddletime.visibility = View.GONE
                                        item.visbleTime = 2
                                    } else {
                                        binding.tvmiddletime.visibility = View.VISIBLE

                                        item.visbleTime = 1
                                        strTimeZu.add(time)
                                    }
                                }
                            } else {
                                if (strTimeZu.size == 0) {
                                    binding.tvmiddletime.visibility = View.VISIBLE

                                    item.visbleTime = 1
                                    strTimeZu.add(time)
                                } else {
                                    if (strTimeZu[strTimeZu.size - 1] == time) {
                                        binding.tvmiddletime.visibility = View.GONE
                                        item.visbleTime = 2
                                    } else {
                                        binding.tvmiddletime.visibility = View.VISIBLE

                                        item.visbleTime = 1
                                        strTimeZu.add(time)
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
                    binding.txtMatchAnimation.visibility = View.VISIBLE
                    if (item.matchType == "1") {//比赛类型：1：足球；2：篮球,可用值:1,2
                        binding.tvhafl.text =
                            context.resources.getString(R.string.hafl_rices) + "" + item.homeHalfScore + "-" + item.awayHalfScore
                        binding.tvtime.text = time1!!.substring(11, 16)
                        binding.tvname.text = item.competitionName
                        binding.tvnameLeft.text = item.homeName
                        binding.tvnameRight.text = item.awayName
                        Glide.with(context).load(item.homeLogo)
                            .placeholder(R.drawable.def_football).into(binding.tvflagLeft)
                        Glide.with(context).load(item.awayLogo)
                            .placeholder(R.drawable.def_football).into(binding.tvflagRight)
                        binding.ivtype.setBackgroundResource(R.drawable.football)

                        when (item.status) {
                            "0" -> {
                                binding.txtMatchAnimation.visibility = View.GONE
                                binding.tvstatus.visibility = View.GONE
                                //  clearAnimation(binding.txtMatchAnimation)
                            }

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
                                clearAnimation(binding.txtMatchAnimation)
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
                                    R.string.zc
                                )
                                initAnimation(binding.txtMatchAnimation)
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
                                clearAnimation(binding.txtMatchAnimation)

                            }

                            "9" -> {

                                binding.txtMatchAnimation.visibility = View.GONE
                                binding.tvvs.text = "VS"
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
                                    context.resources.getString(R.string.main_txt_tc)
                                clearAnimation(binding.txtMatchAnimation)

                            }

                            "10" -> {
                                binding.txtMatchAnimation.visibility = View.GONE
                                binding.tvvs.text = "VS"
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
                                    context.resources.getString(R.string.main_txt_zd)
                                clearAnimation(binding.txtMatchAnimation)

                            }

                            "11" -> {
                                binding.txtMatchAnimation.visibility = View.GONE
                                binding.tvvs.text = "VS"
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

                                clearAnimation(binding.txtMatchAnimation)
                            }

                            "12" -> {
                                binding.txtMatchAnimation.visibility = View.GONE
                                binding.tvvs.text = "VS"
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
                                clearAnimation(binding.txtMatchAnimation)
                            }

                            "13" -> {
                                binding.txtMatchAnimation.visibility = View.GONE
                                binding.tvvs.text = "VS"
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
                                clearAnimation(binding.txtMatchAnimation)
                            }

                            else -> {
                                LogUtils.d("走这里了 哈哈哈")
                                binding.txtMatchAnimation.visibility = View.GONE
                                binding.tvstatus.visibility = View.VISIBLE
                                binding.tvvs.text = "VS"
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
                                clearAnimation(binding.txtMatchAnimation)
                                val date = Date(item.matchTime.toLong())
                                var formatter =
                                    SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                                binding.tvstatus.text =
                                    context.resources.getString(R.string.main_txt_over)
                            }
                        }
                    } else {
                        binding.tvhafl.text =
                            context.resources.getString(R.string.hafl_rices) + "" + item.awayHalfScore + "-" + item.homeHalfScore
                        binding.tvtime.text = time1!!.substring(11, 16)
                        binding.tvname.text = item.competitionName
                        binding.tvnameLeft.text = item.awayName
                        binding.tvnameRight.text = item.homeName
                        Glide.with(context).load(item.awayLogo)
                            .placeholder(R.drawable.def_basketball).into(binding.tvflagLeft)
                        Glide.with(context).load(item.homeLogo)
                            .placeholder(R.drawable.def_basketball).into(binding.tvflagRight)
                        binding.ivtype.setBackgroundResource(R.drawable.basketball)
                        when (item.status) {
                            "0" -> {
                                binding.txtMatchAnimation.visibility = View.GONE
                                binding.tvstatus.visibility = View.GONE
                                clearAnimation(binding.txtMatchAnimation)
                            }

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
                                clearAnimation(binding.txtMatchAnimation)

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
                                        R.color.c_34a853
                                    )
                                )
                                binding.tvstatus.text =
                                    context.resources.getString(
                                        R.string.main_txt_basketball_phase,
                                        resources.getString(R.string.one)
                                    )
                                initAnimation(binding.txtMatchAnimation)

                            }

                            "3" -> {
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
                                binding.tvstatus.text =context.resources.getString(
                                    R.string.finis
                                )+" "+ context.resources.getString(
                                        R.string.main_txt_basketball_phase,
                                        resources.getString(R.string.one)
                                    )
                                initAnimation(binding.txtMatchAnimation)

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
                                        resources.getString(R.string.two)
                                    )
                                initAnimation(binding.txtMatchAnimation)

                            }

                            "5" -> {
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
                                binding.tvstatus.text = context.resources.getString(
                                    R.string.finis
                                )+" "+
                                    context.resources.getString(
                                        R.string.main_txt_basketball_phase,
                                        resources.getString(R.string.two)
                                    )
                                initAnimation(binding.txtMatchAnimation)

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
                                        resources.getString(R.string.three)
                                    )
                                initAnimation(binding.txtMatchAnimation)

                            }

                            "7" -> {
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
                                binding.tvstatus.text =context.resources.getString(
                                    R.string.finis
                                )+" "+ context.resources.getString(
                                        R.string.main_txt_basketball_phase,
                                        resources.getString(R.string.three)
                                    )
                                initAnimation(binding.txtMatchAnimation)

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
                                        resources.getString(R.string.four)
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
                                clearAnimation(binding.txtMatchAnimation)

                            }

                            "11", "12", "13", "14", "15" -> {
                                binding.txtMatchAnimation.visibility = View.GONE
                                clearAnimation(binding.txtMatchAnimation)
                                binding.tvvs.text = "VS"
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
                                            context.resources.getString(R.string.main_txt_yq)
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

                    if (item.anchorList != null && item.anchorList.isNotEmpty()) {
                        if (item.anchorList.size > 5) {
                            for (i in 0 until item.anchorList.size) {
                                if (i > 4) {
                                    item.anchorList.removeAt(i)
                                }
                            }
                            item.anchorList = item.anchorList
                        }
                        binding.conlive.visibility = View.VISIBLE
                        if (binding.rec.itemDecorationCount == 0) {//加个判断
                            binding.rec.run {
                                grid(5)
                                distance(0, 0, 0, 0)
                            }
                        }

                        binding.rec.setup {
                            addType<AnchorBean>(R.layout.item_js)
                            onBind {
                                var binding1 = getBinding<ItemJsBinding>()
                                var item1 = _data as AnchorBean
                                if (item1.nickName.length > 5) {
                                    binding1.tvname.text = item1.nickName.substring(0, 4) + "..."
                                } else {
                                    binding1.tvname.text = item1.nickName
                                }

                                Glide.with(context).load(item1.userLogo)
                                    .placeholder(R.drawable.default_anchor_icon)
                                    .thumbnail(0.1f) // 设置缩略图大小比例（例如0.1表示原图的十分之一）
                                    .override(64, 64) // 指定目标宽度和高度
                                    .into(binding1.ivhead)
                                binding1.linroot.setOnClickListener {
                                    SoundManager.playMedia()
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


                    binding.ivsc.clickNoRepeat(1000) {
                        judgeLogin {
                            binding.tvcollect.visibility=View.VISIBLE
//                            startAn(binding.tvcollect)
//                            stopAn(binding.tvcollect)
                            mview = binding.tvcollect
                            mview1 = binding.ivsc

                            index = modelPosition
                            if (item!!.focus) {
                                mViewModel.getUnnotice(
                                    item!!.matchId,
                                    item!!.matchType,binding.ivsc,binding.tvcollect,bindingAdapterPosition,
                                    mDatabind.recBottom)


                            } else {
                                // startAn(binding.tvcollect)

                                mViewModel.getNotice(
                                    item!!.matchId,
                                    item!!.matchType,binding.ivsc,binding.tvcollect,bindingAdapterPosition,
                                    mDatabind.recBottom
                                )
//                                myToast(appContext.getString(R.string.collect_success))

                            }
                        }

                    }
                    //点击全部
                    binding.conroot.setOnClickListener {
                        SoundManager.playMedia()
                        MatchDetailActivity.open(
                            matchType = item.matchType,
                            matchId = item.matchId,
                            matchName = "${item.homeName}VS${item.awayName}",
                            anchorId = "",
                            videoUrl = ""
                        )

                    }


                }

                R.id.ivsc.onClick {


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

                //mDatabind.state.showLoading()
                getData(false, true)


            }
            appViewModel.updateganlerTime.observe(this) {

                if (isAdded && mCurrentOneTabIndex == mOneTabIndex && mCurrentTwoTabIndex == mTwoTabIndex) {
                    calendarTime = it
                    getData(true, false)


                }
            }
            appViewModel.updateSchedulePosition.observeForever {

                if (isAdded) {
                    mCurrentOneTabIndex = it.currtOne
                    mCurrentTwoTabIndex = it.currtTwo
                    if (isResh && mCurrentOneTabIndex == mOneTabIndex && mCurrentTwoTabIndex == mTwoTabIndex) {
                        isVisble = true
                        isResh = false
                        getData(true, false)
                    } else {
                        isVisble = false
                    }

                    LogUtils.d("第1及页面索引==" + mCurrentOneTabIndex + "第2及页面索引==" + mCurrentTwoTabIndex)

                }
            }

            appViewModel.updateCollection.observeForever {
                if (isAdded) {
                    if (it) {
                        getData(false, false)
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
            appViewModel.appPushLive.observeForever {
                if (isAdded) {
                    if (mCurrentOneTabIndex == mOneTabIndex && mCurrentTwoTabIndex == mTwoTabIndex) {

                        for (i in 0 until mDatabind.recBottom.models?.size!!) {
                            var bean: MatchBean = mDatabind.recBottom.models!![i] as MatchBean
                            // if (bean.homeHalfScore == "1") {
                            if (bean.matchId == it.matchId) {

                                if (bean.anchorList == null) {
                                    var mybean = AnchorBean()
                                    mybean.liveId = it.id
                                    mybean.userId = it.anchorId
                                    mybean.nickName = it.nickName
                                    mybean.userLogo = it.userLogo
                                    var list = ArrayList<AnchorBean>()
                                    list.add(mybean)
                                    (mDatabind.recBottom.models!![i] as MatchBean).anchorList = list
                                    mDatabind.recBottom.bindingAdapter.notifyItemChanged(i)
                                } else {
                                    if (it.liveStatus == 2) {//开播

                                        var hasdata = false
                                        for (i in 0 until bean.anchorList.size) {
                                            if (bean.anchorList[i].userId == it.anchorId) {
                                                hasdata = true
                                                break
                                            }
                                        }
                                        if (!hasdata) {
                                            var mybean = AnchorBean()
                                            mybean.liveId = it.id
                                            mybean.userId = it.anchorId
                                            mybean.nickName = it.nickName
                                            mybean.userLogo = it.userLogo
                                            bean.anchorList.add(mybean)
                                            mDatabind.recBottom.bindingAdapter.notifyItemChanged(i)
                                            break
                                        }

                                    } else {//关播
                                        for (i in 0 until bean.anchorList.size) {
                                            if (bean.anchorList[i].userId == it.anchorId) {
                                                bean.anchorList.removeAt(i)
                                                mDatabind.recBottom.bindingAdapter.notifyItemChanged(
                                                    i
                                                )
                                                break
                                            }
                                        }
                                        break
                                    }
                                }
                                break
                            }

                        }
                    } else {
                        if (mDatabind.recBottom.models != null) {
                            for (i in 0 until mDatabind.recBottom.models?.size!!) {
                                var bean: MatchBean = mDatabind.recBottom.models!![i] as MatchBean
                                // if (bean.homeHalfScore == "1") {
                                if (bean.matchId == it.matchId) {
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

                    if (mview != null) {
                        mview!!.performClick()
                        mview = null
                        mview1 = null
                    }

                } else {
                    if (isAdded) {
                        if (mCurrentOneTabIndex == mOneTabIndex && mCurrentTwoTabIndex == mTwoTabIndex) {
                            getData(true, false)
                        } else {

                            isResh = true
                        }
                    }
                }
            }

        } catch (e: Exception) {

        }

    }


    override fun lazyLoadData() {
        super.lazyLoadData()
        LogUtils.d("懒加载是否显示点点" + Constants.isLoading)
        getData(Constants.isLoading, false)

    }

    override fun lazyLoadTime(): Long {
        return 0
    }

    fun getData(iLoading: Boolean, isresh: Boolean) {
        try {


            Constants.isLoading = true
            initTime()

//            Log.i("KAIAKI","开始-===="+strTime)
//            Log.i("KAIAKI","结束-===="+endTime)
            mViewModel.getHotMatchDataList(
                isresh,
                iLoading, PostSchMatchListBean(
                    competitionId=competitionId, current=page,
                    endTime=endTime,
                    matchType=matchtype!!, size=pageSize, startTime=strTime,
                    status=status
                )
            )
        } catch (e: Exception) {
        }
    }

    fun clearAnimation(view: LottieAnimationView) {

    }

    fun initAnimation(view: LottieAnimationView) {

    }

    override fun createObserver() {
        try {
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
                                mDatabind.state.showEmpty()
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

                // mAdapter.getItem(index)!!.focus = false
                mview!!.setAnimation("shoucang2.json")
                mview!!.loop(false)
                mview!!.playAnimation()
                mview!!.addAnimatorListener(object :
                    Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        mview!!.visibility=View.INVISIBLE
                        mDatabind.recBottom.bindingAdapter.getModel<MatchBean>(index).focus = false
                        mDatabind.recBottom.bindingAdapter.notifyItemChanged(index)
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })

                //mview1!!.setBackgroundResource(R.drawable.sc_shoucang_icon1)

            }
            mViewModel.noticeData.observe(this) {

                mview!!.setAnimation("shoucang1.json")
                mview!!.loop(false)
                mview!!.playAnimation()

               // mview1!!.setBackgroundResource(R.drawable.sc_shoucang_icon2)
                mview!!.addAnimatorListener(object :
                    Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        mview!!.visibility=View.INVISIBLE
                        mDatabind.recBottom.bindingAdapter.getModel<MatchBean>(index).focus = true
                        mDatabind.recBottom.bindingAdapter.notifyItemChanged(index)
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })
            }
        } catch (e: Exception) {
        }
    }
}
