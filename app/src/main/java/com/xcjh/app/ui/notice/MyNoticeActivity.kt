package com.xcjh.app.ui.notice

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
import com.drake.brv.utils.addModels
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.adapter.SchtitleAdapter
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.AnchorBean
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.databinding.ActivityMynoticeBinding
import com.xcjh.app.databinding.ItemJsBinding
import com.xcjh.app.databinding.ItemSchAllBinding
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.base_lib.Constants.Companion.BASE_PAGE_SIZE
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.TimeUtil
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.horizontal
import com.xcjh.base_lib.utils.vertical
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/***
 * 我的关注比赛
 */

class MyNoticeActivity : BaseActivity<MyNoticeVm, ActivityMynoticeBinding>() {

    private val mAdapter by lazy { SchtitleAdapter() }
    var listdata: MutableList<MatchBean> = ArrayList<MatchBean>()
    var page=1
    var pageSize=10
    var mview: View? = null
    var index: Int = 0
    val animatorSet = AnimatorSet()
    var strTimeZu: MutableList<String> = ArrayList<String>()
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .titleBar(mDatabind.titleTop.root)
            .init()

        mDatabind.titleTop.tvTitle.text = resources.getString(R.string.my_txt_subscribe)
        mDatabind.rec.run {
            vertical()
           // adapter=mAdapter
            distance(0, 0, 0, 16)
        }
        mDatabind.rec.setup {
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
                                        R.color.c_34a853
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
                                        R.color.c_34a853
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
                                        R.color.c_34a853
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
                                        R.color.c_34a853
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

                            "7" -> {
                                binding.tvstatus.visibility = View.VISIBLE
                                binding.txtMatchAnimation.visibility = View.GONE
                                binding.tvvs.text = item.homeScore + "-" + item.awayScore
                                binding.tvstatus.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.c_34a853
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
                                binding.txtMatchAnimation.visibility = View.GONE
                                binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                binding.tvstatus.visibility = View.VISIBLE
                                binding.tvstatus.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.c_34a853
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
                                    )
                                //initAnimation(binding.txtMatchAnimation)

                            }
                            "3" -> {
                                binding.txtMatchAnimation.visibility = View.GONE
                                binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                binding.tvstatus.visibility = View.VISIBLE
                                binding.tvstatus.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.c_34a853
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
                                    )+ context.resources.getString(
                                        R.string.finis
                                    )
                                //initAnimation(binding.txtMatchAnimation)

                            }

                            "4" -> {
                                binding.txtMatchAnimation.visibility = View.GONE
                                binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                binding.tvstatus.visibility = View.VISIBLE
                                binding.tvstatus.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.c_34a853
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
                                // initAnimation(binding.txtMatchAnimation)

                            }
                            "5" -> {
                                binding.txtMatchAnimation.visibility = View.GONE
                                binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                binding.tvstatus.visibility = View.VISIBLE
                                binding.tvstatus.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.c_34a853
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
                                    )+ context.resources.getString(
                                        R.string.finis
                                    )
                                //initAnimation(binding.txtMatchAnimation)

                            }
                            "6" -> {
                                binding.txtMatchAnimation.visibility = View.GONE
                                binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                binding.tvstatus.visibility = View.VISIBLE
                                binding.tvstatus.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.c_34a853
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
                                // initAnimation(binding.txtMatchAnimation)

                            }
                            "7" -> {
                                binding.txtMatchAnimation.visibility = View.GONE
                                binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                binding.tvstatus.visibility = View.VISIBLE
                                binding.tvstatus.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.c_34a853
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
                                    )+ context.resources.getString(
                                        R.string.finis
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
                                        R.color.c_34a853
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
                                // initAnimation(binding.txtMatchAnimation)
                            }

                            "9" -> {
                                binding.txtMatchAnimation.visibility = View.GONE
                                binding.tvvs.text = item.awayScore + "-" + item.homeScore
                                binding.tvstatus.visibility = View.VISIBLE
                                binding.tvstatus.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.c_34a853
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
        mAdapter.isEmptyViewEnable=true
        mViewModel.getMyNoticeList(true)
        mDatabind.smartCommon.setOnRefreshListener {   mViewModel.getMyNoticeList(true) }.
        setOnLoadMoreListener {  mViewModel.getMyNoticeList(false)  }
    }
    fun initAnimation(view: AppCompatTextView){
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

        mViewModel.hotMatchList.observe(this){
            if (it.isSuccess) {
                //成功
                when {
                    //第一页并没有数据 显示空布局界面
                    it.isFirstEmpty -> {
                        if (mDatabind.rec.models?.size != null) {
                            mDatabind.rec.mutable.clear()
                        }
                        mDatabind.smartCommon.finishRefresh()
                        mDatabind.state.showEmpty()
                    }
                    //是第一页
                    it.isRefresh -> {
                        mDatabind.smartCommon.finishRefresh()
                        mDatabind.smartCommon.resetNoMoreData()
                        mDatabind.rec.models = it.listData
                        //mAdapter.submitList(it.listData)
                        //  mAdapter.emptyView
                        //用户类型[1:游客 2:普通用户 3:会员]
                        //  addItemBinder.notifyDataSetChanged()
                        if (it.listData.size<BASE_PAGE_SIZE){
                            mDatabind.smartCommon.finishLoadMoreWithNoMoreData()
                        }
                        mDatabind.state.showContent()


                    }
                    //不是第一页
                    else -> {
                        if (it.listData.isEmpty()) {
//                            mViewBind.smartCommon.setEnableLoadMore(false)
                            mDatabind.smartCommon.finishLoadMoreWithNoMoreData()
                        } else {
//                            mViewBind.smartCommon.setEnableLoadMore(true)
                            mDatabind.smartCommon.finishLoadMore()
//                            addItemBinder.addData(getNewData(it))
                            mDatabind.rec.addModels(it.listData)
                            //mAdapter.addAll(it.listD.lata)
                            //用户类型[1:游客 2:普通用户 3:会员]
                            mDatabind.state.showContent()
                        }

                    }
                }
            } else {
                //失败
                if (it.isRefresh) {
                    mDatabind.smartCommon.finishRefresh()
                    //如果是第一页，则显示错误界面，并提示错误信息
                    mAdapter.submitList(null)
                    //  addItemBinder.setEmptyView(empty)
                } else {
                    mDatabind.smartCommon.finishLoadMore(false)
                }
            }
        }
        mViewModel.unnoticeData.observe(this) {
            mview!!.setBackgroundResource(R.drawable.ic_focus_n)
            //mAdapter.getItem(index)!!.focus = false
            mDatabind.rec.mutable.removeAt(index)
            mDatabind.rec.bindingAdapter.notifyItemRemoved(index) // 通知更新

        }
        mViewModel.noticeData.observe(this) {

        }
    }

}