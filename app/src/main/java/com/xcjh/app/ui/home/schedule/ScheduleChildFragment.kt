package com.xcjh.app.ui.home.schedule

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.drake.brv.listener.ItemDifferCallback
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setDifferModels
import com.drake.brv.utils.setup
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.AnchorBean
import com.xcjh.app.bean.HotMatchBean
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.bean.PostSchMatchListBean
import com.xcjh.app.databinding.FrConmentBinding
import com.xcjh.app.databinding.ItemJsBinding
import com.xcjh.app.databinding.ItemSchAllBinding
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.utils.selectTime
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.ReceiveChangeMsg
import com.xcjh.app.websocket.bean.ReceiveChatMsg
import com.xcjh.app.websocket.bean.ReceiveWsBean
import com.xcjh.app.websocket.listener.C2CListener
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.TimeUtil
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.horizontal
import com.xcjh.base_lib.utils.setOnclickNoRepeat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread


/***
 * 赛程
 */
class ScheduleChildFragment : BaseFragment<ScheduleVm, FrConmentBinding>() {

    private val mTabEntities = ArrayList<CustomTabEntity>()
    var listdata: MutableList<MatchBean> = ArrayList<MatchBean>()
    var matchtype: String? = ""
    var matchtypeOld: String? = ""
    var tabName: String? = ""
    var status = 0
    var page = 1
    var pageSize = 1000
    var mview: View? = null
    var index: Int = 0
    var mPosition = 0
    var hasData = false
    var isVisble = false
    lateinit var strTime: String
    var calendarTime: String = ""
    lateinit var endTime: String
    lateinit var strTimeRuslt: String
    lateinit var endTimeResult: String
    var mTabPosition = 0
    var mPushPosition = 0
    var isClick = false
    val animatorSet = AnimatorSet()
    var strTimeZu: MutableList<String> = ArrayList<String>()


    companion object {
        var mTitles: Array<out String>? = null
        private val MATCHTYPE = "matchtype"
        private val STATUS = "status"
        private val TAB = "tab"
        fun newInstance(matchtype: String, status: Int, po: Int): ScheduleChildFragment {
            val args = Bundle()
            args.putString(MATCHTYPE, matchtype);
            args.putInt(STATUS, status);
            args.putInt(TAB, po);
            val fragment = ScheduleChildFragment()
            fragment.arguments = args
            return fragment
        }
    }

    fun initTime() {
        LogUtils.d("本页面tabname=$tabName")
        strTime = TimeUtil.gettimenowYear().toString()
        if ((tabName == resources.getString(R.string.all) || tabName == resources.getString(R.string.foot_scr) ||
                    tabName == resources.getString(R.string.bas_scr))
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

        if (matchtypeOld == "3") {
            if (calendarTime.isNotEmpty()) {
                endTimeResult = TimeUtil.getDateStr(calendarTime, 2).toString()
                if (matchtypeOld == "3") {
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

            mDatabind.recBottom.run {
                distance(0, 0, 0, 15)
            }
            //mDatabind.smartCommon.stateChangedHandler = LeastAnimationStateChangedHandler()
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
                                context.resources.getString(R.string.hafl_rices) + ":" + item.homeHalfScore + "-" + item.awayHalfScore
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
                                            R.color.c_8a91a0
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
                                            R.color.c_fe4848
                                        )
                                    )
                                    binding.tvstatus.text = context.resources.getString(
                                        R.string.main_txt_sbc,
                                        if (item.runTime == null)
                                            "0"
                                        else {
                                            item.runTime
                                        }
                                    )
                                    initAnimation(binding.txtMatchAnimation)
                                }

                                "3" -> {
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_fe4848
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
                                            R.color.c_fe4848
                                        )
                                    )
                                    binding.tvstatus.text = context.resources.getString(
                                        R.string.main_txt_xbc,
                                        if (item.runTime == null)
                                            "0"
                                        else {
                                            item.runTime
                                        }
                                    )
                                    initAnimation(binding.txtMatchAnimation)
                                }

                                "5", "6" -> {
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.txtMatchAnimation.visibility = View.VISIBLE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_fe4848
                                        )
                                    )
                                    binding.tvstatus.text = context.resources.getString(
                                        R.string.main_txt_under,
                                        if (item.runTime == null)
                                            "0"
                                        else {
                                            item.runTime
                                        }
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
                                            R.color.c_fe4848
                                        )
                                    )
                                    binding.tvstatus.text = context.resources.getString(
                                        R.string.main_dqdz
                                    )
                                    // initAnimation(binding.txtMatchAnimation)

                                }

                                "8" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_F7DA73
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
                                context.resources.getString(R.string.hafl_rices) + ":" + item.awayHalfScore + "-" + item.homeHalfScore
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
                                            R.color.c_8a91a0
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.main_txt_wks)

                                }

                                "2", "3" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_fe4848
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(
                                            R.string.main_txt_basketball_phase,
                                            "一"
                                        )
                                    //initAnimation(binding.txtMatchAnimation)

                                }

                                "4", "5" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_fe4848
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(
                                            R.string.main_txt_basketball_phase,
                                            "二"
                                        )
                                    // initAnimation(binding.txtMatchAnimation)

                                }

                                "6", "7" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_fe4848
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(
                                            R.string.main_txt_basketball_phase,
                                            "三"
                                        )
                                    // initAnimation(binding.txtMatchAnimation)

                                }

                                "8" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_fe4848
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(
                                            R.string.main_txt_basketball_phase,
                                            "四"
                                        )
                                    // initAnimation(binding.txtMatchAnimation)
                                }

                                "9" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_fe4848
                                        )
                                    )
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.over_time)
                                    // initAnimation(binding.txtMatchAnimation)
                                }

                                "10" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_F7DA73
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
                                            R.color.c_8a91a0
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

                    binding.tvcollect.setOnClickListener {
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

            val bundle = arguments
            if (bundle != null) {
                matchtype = bundle.getString(MATCHTYPE)
                matchtypeOld = matchtype
                status = bundle.getInt(STATUS)
                mTabPosition = bundle.getInt(TAB)
            }
            initTime()

            appViewModel.updateSchedulePosition.observeForever {
                if (isAdded) {
                    mPushPosition = it
                    isVisble = mTabPosition == it
                    mDatabind.smartCommon.autoRefresh()
                    calendarTime = ""
                }
            }
            if (!hasData) {
                mViewModel.getHotMatchData(matchtypeOld!!, status)

            }
            mDatabind.smartCommon.onRefresh {
                if (!hasData) {
                    mViewModel.getHotMatchData(matchtypeOld!!, status)

                }

            }.showLoading()
            appViewModel.updateLoginEvent.observe(this) {
                if (it) {

                    mDatabind.smartCommon.autoRefresh()

                } else {
                    mDatabind.smartCommon.autoRefresh()

                }
            }
            appViewModel.appPushMsg.observeForever {
                if (isAdded && isVisble) {
                    var listzu: MutableList<MatchBean> = ArrayList<MatchBean>()
                    for (j in 0 until it.size) {
                        for (i in 0 until mDatabind.recBottom.models?.size!!) {
                            var bean: MatchBean = mDatabind.recBottom.models!![i] as MatchBean
                            listzu.add(bean)
                           // if (bean.homeHalfScore == "1") {
                            if (bean.matchId == it[j].matchId.toString() && bean.matchType == it[j].matchType) {
                                bean.awayHalfScore = "0000"
                                bean.awayScore = it[j].awayScore
                                bean.homeHalfScore = it[j].homeHalfScore
                                bean.homeScore = it[j].homeScore
                                bean.runTime = it[j].runTime
                                bean.status = it[j].status
                                mDatabind.recBottom.bindingAdapter.notifyItemChanged(i)
                            }

                        }
                    }
                    thread {
                       // mDatabind.recBottom.setDifferModels(listzu)
                    }

                }
            }

        } catch (e: Exception) {

        }

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

    override fun onResume() {
        super.onResume()
        isVisble = mTabPosition == mPushPosition
        if (!hasData) {
            initTime()
            mViewModel.getHotMatchData(matchtypeOld!!, status)

        }

    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isVisble = isVisibleToUser
    }

    override fun onPause() {
        super.onPause()
        isVisble = false
    }


    private fun initEvent(list: MutableList<HotMatchBean>) {

        tabName = list[mPosition].competitionName
        initTime()
        mViewModel.getHotMatchDataList(
            true, PostSchMatchListBean(
                list[mPosition].competitionId, page,
                endTime,
                matchtype!!, pageSize, strTime,
                status
            )
        )
        // mTitles = resources.getStringArray(R.array.str_sch_top2_tab)
        mTabEntities.clear()
        for (i in 0 until list!!.size) {
            mTabEntities.add(
                TabEntity(
                    list!![i].competitionName,
                    R.drawable.icon_back,
                    R.drawable.icon_back
                )
            )
        }

        mDatabind.recTop.setTabData(mTabEntities)
        mDatabind.recTop.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                LogUtils.d("选中了第几个$position")
                calendarTime = ""
                mDatabind.smartCommon.showLoading()
                page = 1
                isClick = true
                matchtype = list[position].matchType
                mPosition = position
                tabName = list[position].competitionName
                initTime()

                mViewModel.getHotMatchDataList(
                    true, PostSchMatchListBean(
                        list[position].competitionId,
                        page,
                        endTime,
                        list[position].matchType,
                        pageSize,
                        strTime,
                        status
                    )
                )
            }

            override fun onTabReselect(position: Int) {
                LogUtils.d("")
            }

        })
        mDatabind.smartCommon.setOnRefreshListener {
            initTime()
            mDatabind.smartCommon.showLoading()
            if (!hasData) {
                mViewModel.getHotMatchData(matchtype!!, status)

            } else {
                page = 1
                mViewModel.getHotMatchDataList(
                    true, PostSchMatchListBean(

                        list[mPosition].competitionId, page,
                        endTime,
                        matchtype!!, pageSize, strTime,
                        status
                    )
                )
            }
        }.setOnLoadMoreListener {
            tabName = list[mPosition].competitionName
            initTime()
            mViewModel.getHotMatchDataList(
                false, PostSchMatchListBean(
                    list[mPosition].competitionId, page,
                    endTime,
                    matchtype!!, pageSize, strTime,
                    status
                )
            )
        }
        appViewModel.appPolling.observeForever {
//            if (isAdded && isVisble) {
//                tabName=list[mPosition].competitionName
//                isClick = false
//                initTime()
//                LogUtils.d(status.toString() + "执行了0轮询" + matchtype + "///" + isVisble)
//                mViewModel.getHotMatchDataList(
//                    true, PostSchMatchListBean(
//                        list[mPosition].competitionId, page,
//                        endTime,
//                        matchtype!!, pageSize, strTime,
//                        status
//                    )
//                )
//            }
        }

        setOnclickNoRepeat(mDatabind.ivMeau) {
            when (it.id) {
                R.id.iv_meau -> {

                    selectTime(requireActivity(), calendarTime) { start, end ->

                        calendarTime =
                            start.year.toString() + "-" + TimeUtil.checkTimeSingle(start.month) + "-" + TimeUtil.checkTimeSingle(
                                start.day
                            )
                        initTime()
                        mDatabind.smartCommon.showLoading()

//                        endTime =
//                            end.year.toString() + "-" + TimeUtil.checkTimeSingle(end.month) + "-" + TimeUtil.checkTimeSingle(
//                                end.day
//                            )
                        isClick = true
                        mViewModel.getHotMatchDataList(
                            true, PostSchMatchListBean(
                                list[mPosition].competitionId, page, endTime,
                                matchtype!!, pageSize, strTime,
                                status
                            )

                        )

                    }
                }
            }
        }


    }

    override fun createObserver() {
        val empty = requireActivity().layoutInflater!!.inflate(R.layout.layout_empty, null)

        mViewModel.hotMatch.observe(this) {
            if (it.isNotEmpty()) {
                //成功
                hasData = true
                if (matchtypeOld != "3") {
                    var bean = HotMatchBean(
                        "", resources.getString(R.string.all), 0,
                        matchtype.toString()
                    )
                    it.add(0, bean)
                } else {
                    matchtype = "1"
                    var bean1 = HotMatchBean("", resources.getString(R.string.foot_scr), 0, "1")
                    it.add(0, bean1)
                    var bean2 = HotMatchBean("", resources.getString(R.string.bas_scr), 0, "2")
                    it.add(1, bean2)
                }
                initEvent(it)

            } else {

            }

        }

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
                        mDatabind.smartCommon.showEmpty()

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
                        mDatabind.smartCommon.showContent()

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
                            mDatabind.smartCommon.showContent()
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
                    mDatabind.smartCommon.showEmpty()
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