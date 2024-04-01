package com.xcjh.app.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.xcjh.app.R
import com.xcjh.app.bean.MatchBean
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.TimeUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SchtitleAdapter : BaseQuickAdapter<MatchBean, QuickViewHolder>() {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        // 返回一个 ViewHolder
        return QuickViewHolder(R.layout.item_sch_all, parent)
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: MatchBean?) {
        // 设置item数据
        var ivtype: ImageView = holder.getView(R.id.ivtype)
        var ivcollect: ImageView = holder.getView(R.id.tvcollect)
        var tvflag_left: ImageView = holder.getView(R.id.tvflag_left)
        var tvflag_right: ImageView = holder.getView(R.id.tvflag_right)

        var tvtime: TextView = holder.getView(R.id.tvtime)
        var tvname: TextView = holder.getView(R.id.tvname)
        var tvstatus: TextView = holder.getView(R.id.tvstatus)
        var txtMatchAnimation: TextView = holder.getView(R.id.txtMatchAnimation)
        var tvname_left: TextView = holder.getView(R.id.tvname_left)
        var tvname_right: TextView = holder.getView(R.id.tvname_right)
        var tvhafl: TextView = holder.getView(R.id.tvhafl)
        var time = TimeUtil.timeStamp2Date(item!!.matchTime.toLong(), null)
        if (item.focus){
            ivcollect.setBackgroundResource(R.drawable.ic_focus_s)
        }else{
            ivcollect.setBackgroundResource(R.drawable.ic_focus_n)
        }
        LogUtils.d("直播数据"+item.anchorList)

        when (item.matchType) {//比赛类型：1：足球；2：篮球,可用值:1,2
            "1" -> {
                ivtype.setBackgroundResource(R.drawable.football)
                when (item.status) {
                    "0" -> tvstatus.visibility = View.GONE
                    "1" -> {
                        tvstatus.visibility = View.VISIBLE
                        tvstatus.setTextColor(ContextCompat.getColor(context, R.color.c_8a91a0))
                        tvstatus.text =
                            context.resources.getString(R.string.main_txt_wks)

                    }

                    "2", "4" -> {
                        tvstatus.visibility = View.VISIBLE
                        txtMatchAnimation.visibility = View.VISIBLE
                        tvstatus.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.c_pb_bar
                            )
                        )
                        tvstatus.text = context.resources.getString(R.string.main_txt_under, " ")
                        val fadeIn =
                            ObjectAnimator.ofFloat(txtMatchAnimation, "alpha", 0f, 1f)
                        fadeIn.duration = 500
                        fadeIn.startDelay = 200 // 延迟200毫秒开始动画

                        val fadeOut =
                            ObjectAnimator.ofFloat(txtMatchAnimation, "alpha", 1f, 0f)
                        fadeOut.duration = 500
                        fadeOut.startDelay = 200 // 延迟200毫秒开始动画

                        val animatorSet = AnimatorSet()
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
                    "8" -> {
                        tvstatus.visibility = View.VISIBLE
                        tvstatus.setTextColor(ContextCompat.getColor(context, R.color.c_F7DA73))
                        tvstatus.text =
                            context.resources.getString(R.string.main_txt_over)

                    }
                    else -> {
                        tvstatus.visibility=View.VISIBLE
                        tvstatus.setTextColor(ContextCompat.getColor(context,R.color.c_f5f5f5))

                        val date = Date(item.matchTime.toLong())
                        var formatter = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                        tvstatus.text=formatter.format(date)
                    }
                }
            }

            "2" -> {
                ivtype.setBackgroundResource(R.drawable.basketball)
                when (item.status) {
                    "0" -> tvstatus.visibility = View.GONE
                    "1" -> {
                        tvstatus.visibility = View.VISIBLE
                        tvstatus.setTextColor(ContextCompat.getColor(context, R.color.c_8a91a0))
                        tvstatus.text =
                            context.resources.getString(R.string.main_txt_wks)

                    }
                    "2","3" -> {
                        tvstatus.visibility = View.VISIBLE
                        tvstatus.setTextColor(ContextCompat.getColor(context, R.color.c_pb_bar))
                        tvstatus.text =
                            context.resources.getString(R.string.main_txt_basketball_phase, context.resources.getString(R.string.one))

                    }

                    "4","5" -> {
                        tvstatus.visibility = View.VISIBLE
                        tvstatus.setTextColor(ContextCompat.getColor(context, R.color.c_pb_bar))
                        tvstatus.text =
                            context.resources.getString(R.string.main_txt_basketball_phase, context.resources.getString(R.string.two))

                    }

                    "6" ,"7"-> {
                        tvstatus.visibility = View.VISIBLE
                        tvstatus.setTextColor(ContextCompat.getColor(context, R.color.c_pb_bar))
                        tvstatus.text =
                            context.resources.getString(R.string.main_txt_basketball_phase, context.resources.getString(R.string.three))

                    }

                    "8","9" -> {
                        tvstatus.visibility = View.VISIBLE
                        tvstatus.setTextColor(ContextCompat.getColor(context, R.color.c_pb_bar))
                        tvstatus.text =
                            context.resources.getString(R.string.main_txt_basketball_phase, context.resources.getString(R.string.four))

                    }
                    "10" -> {
                        tvstatus.visibility = View.VISIBLE
                        tvstatus.setTextColor(ContextCompat.getColor(context, R.color.c_F7DA73))
                        tvstatus.text =
                            context.resources.getString(R.string.main_txt_over)

                    }

                    "11", "12", "13", "14", "15" -> {


                    }
                }
            }
        }
        tvhafl.text =
            context.resources.getString(R.string.hafl_rices) + ":" + item.homeHalfScore + "-" + item.awayHalfScore
        tvtime.text = time!!.substring(11, 16)
        tvname.text = item.competitionName
        tvname_left.text = item.homeName
        tvname_right.text = item.awayName
        Glide.with(context).load(item.homeLogo).into(tvflag_left)
        Glide.with(context).load(item.awayLogo).into(tvflag_right)

        holder.itemView.setOnClickListener {
            //startNewActivity<ChatActivity>()
        }
    }
}