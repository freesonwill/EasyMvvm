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
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.TimeUtil
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.grid
import com.xcjh.base_lib.utils.horizontal
import com.xcjh.base_lib.utils.setOnclickNoRepeat
import com.xcjh.base_lib.utils.vertical
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer


/***
 * 赛程
 */
class ConmentFragment : BaseFragment<ScheduleVm, FrConmentBinding>() {

    private val mTabEntities = ArrayList<CustomTabEntity>()
    var listdata: MutableList<MatchBean> = ArrayList<MatchBean>()
    var matchtype: String? = ""
    var status = 0
    var page = 1
    var pageSize = 1000
    var mview: View? = null
    var index: Int = 0
    var mPosition = 0
    var hasData = false
    var isVisble = false
    var strTime: String = TimeUtil.gettimenowYear().toString()
    var endTime: String = TimeUtil.addDayEgls("0", 2).toString()
    var strTimeRuslt: String = TimeUtil.gettimenowYear().toString()
    var endTimeResult: String = TimeUtil.getDateStr(strTime, 2).toString()
    var mTabPosition = 0
    var mPushPosition = 0
    var isClick = false
    val animatorSet = AnimatorSet()

    companion object {
        var mTitles: Array<out String>? = null
        private val MATCHTYPE = "matchtype"
        private val STATUS = "status"
        private val TAB = "tab"
        fun newInstance(matchtype: String, status: Int, po: Int): ConmentFragment {
            val args = Bundle()
            args.putString(MATCHTYPE, matchtype);
            args.putInt(STATUS, status);
            args.putInt(TAB, po);
            val fragment = ConmentFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun initView(savedInstanceState: Bundle?) {

        try {

            mDatabind.recBottom.run {
                distance(0, 0, 0, 15)
            }
            mDatabind.recBottom.setup {
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
                    LogUtils.d("直播数据" + item.anchorList)

                    when (item.matchType) {//比赛类型：1：足球；2：篮球,可用值:1,2
                        "1" -> {
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

                                "2", "3", "4", "5", "6", "7" -> {
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
                                            R.color.c_F7DA73
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
                                            R.color.c_F7DA73
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
                                            R.color.c_F7DA73
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
                                            R.color.c_f5f5f5
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
                                    binding.txtMatchAnimation.visibility = View.VISIBLE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
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
                                    initAnimation(binding.txtMatchAnimation)

                                }

                                "4", "5" -> {
                                    binding.txtMatchAnimation.visibility = View.VISIBLE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
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
                                    initAnimation(binding.txtMatchAnimation)

                                }

                                "6", "7" -> {
                                    binding.txtMatchAnimation.visibility = View.VISIBLE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
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
                                    initAnimation(binding.txtMatchAnimation)

                                }

                                "8", "9" -> {
                                    binding.txtMatchAnimation.visibility = View.VISIBLE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
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
                                    initAnimation(binding.txtMatchAnimation)


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
                                        context.resources.getString(R.string.main_txt_over)

                                }

                                "11", "12", "13", "14", "15" -> {
                                    binding.txtMatchAnimation.visibility = View.GONE
                                    binding.tvvs.text = item.homeScore + "-" + item.awayScore
                                    binding.tvstatus.visibility = View.VISIBLE
                                    binding.tvstatus.setTextColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.c_F7DA73
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
                    binding.tvhafl.text =
                        context.resources.getString(R.string.hafl_rices) + ":" + item.homeHalfScore + "-" + item.awayHalfScore
                    binding.tvtime.text = time!!.substring(11, 16)
                    binding.tvname.text = item.competitionName
                    binding.tvnameLeft.text = item.homeName
                    binding.tvnameRight.text = item.awayName
                    Glide.with(context).load(item.homeLogo).into(binding.tvflagLeft)
                    Glide.with(context).load(item.awayLogo).into(binding.tvflagRight)
                    if (item.anchorList != null && item.anchorList.isNotEmpty()) {
                        binding.conlive.visibility = View.VISIBLE
                        if (binding.rec.getItemDecorationCount() == 0) {//加个判断
                            binding.rec.run {
                                grid(4)
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
                        }.models = if (item.anchorList.size>4){
                            item.anchorList.subList(0,4)
                        }else{
                            item.anchorList
                        }


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
                        return oldItem == newItem
                    }

                    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                        return oldItem == newItem
                    }

                    override fun getChangePayload(oldItem: Any, newItem: Any): Any? {
                        return true
                    }
                }
            }.models = listdata

            val bundle = arguments
            if (bundle != null) {
                matchtype = bundle.getString(MATCHTYPE)
                status = bundle.getInt(STATUS)
                mTabPosition = bundle.getInt(TAB)
            }
            if (matchtype == "3") {
                strTime = endTimeResult
                endTime = strTimeRuslt
            }
            appViewModel.updateSchedulePosition.observeForever {
                if (isAdded) {
                    mPushPosition = it
                    isVisble = mTabPosition == it
                }
            }
            if (!hasData) {
                mViewModel.getHotMatchData(matchtype!!, status)

            }
            mDatabind.smartCommon.setOnRefreshListener {
                if (!hasData) {
                    mViewModel.getHotMatchData(matchtype!!, status)

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
            mViewModel.getHotMatchData(matchtype!!, status)

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
                page = 1
                isClick = true
                matchtype = list[position].matchType
                mPosition = position
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
            if (isAdded && isVisble) {
                isClick = false
                LogUtils.d(status.toString() + "执行了0轮询" + matchtype + "///" + isVisble)
                mViewModel.getHotMatchDataList(
                    true, PostSchMatchListBean(
                        list[mPosition].competitionId, page,
                        endTime,
                        matchtype!!, pageSize, strTime,
                        status
                    )
                )
            }
        }

        setOnclickNoRepeat(mDatabind.ivMeau) {
            when (it.id) {
                R.id.iv_meau -> {
                    selectTime(requireActivity()) { start, end ->
                        strTime =
                            start.year.toString() + "-" + TimeUtil.checkTimeSingle(start.month) + "-" + TimeUtil.checkTimeSingle(
                                start.day
                            )
                        endTime =
                            end.year.toString() + "-" + TimeUtil.checkTimeSingle(end.month) + "-" + TimeUtil.checkTimeSingle(
                                end.day
                            )
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
                if (matchtype != "3") {
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
            if (it.isSuccess) {

                //成功
                when {
                    //第一页并没有数据 显示空布局界面
                    it.isFirstEmpty -> {
                        if (mDatabind.recBottom.models?.size != null) {
                            mDatabind.recBottom.mutable.clear()
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
                    //  mDatabind.state.showEmpty()
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