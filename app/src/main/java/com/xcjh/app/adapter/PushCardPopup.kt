package com.xcjh.app.adapter

import android.animation.Animator
import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.lxj.xpopup.core.PositionPopupView
import com.xcjh.app.R
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.utils.nice.NiceImageView
import com.xcjh.base_lib.utils.view.clickNoRepeat

class PushCardPopup(content: Context,var beingLiveBean: BeingLiveBean) : PositionPopupView(content){

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_push_card
    }

    override fun onCreate() {
        super.onCreate()
        var  ivCardClose=findViewById<LottieAnimationView>(R.id.stateLoadingDelete)
        var  txtDialogClick=findViewById<AppCompatTextView>(R.id.txtDialogClick)
        var  txtCardName=findViewById<AppCompatTextView>(R.id.txtCardName)
        var  txtCardMatch=findViewById<AppCompatTextView>(R.id.txtCardMatch)
        var  ivCardHead=findViewById<NiceImageView>(R.id.ivCardHead)
        var  stateLoadingImg=findViewById<LottieAnimationView>(R.id.stateLoadingImg)
        stateLoadingImg.playAnimation()
        Glide.with(context)
            .load(beingLiveBean.userLogo) // 替换为您要加载的图片 URL
            .error(R.drawable.default_anchor_icon)
            .placeholder(R.drawable.default_anchor_icon)
            .into(ivCardHead)
        txtCardName.text=beingLiveBean.nickName
            //比赛类型 1足球，2篮球
        if(beingLiveBean.matchType.equals("1")){
            txtCardMatch.text=resources.getString(R.string.popup_txt_level_name,"${beingLiveBean.homeTeamName}VS${beingLiveBean.awayTeamName}")
        }else{
            txtCardMatch.text=resources.getString(R.string.popup_txt_level_name,"${beingLiveBean.awayTeamName}VS${beingLiveBean.homeTeamName}")
        }
        ivCardClose.clickNoRepeat {
            pushCardPopupListener!!.clicktClose()
        }

        ivCardClose.addAnimatorListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(animation: Animator) {
                // 在动画开始时执行的操作（可选）
            }

            override fun onAnimationEnd(animation: Animator) {
                // 在动画结束时
                dismiss()
            }

            override fun onAnimationCancel(animation: Animator) {
                // 在动画取消时执行的操作（可选）
            }

            override fun onAnimationRepeat(animation: Animator) {
                // 在动画结束时重新开始动画
            }
        })
        ivCardClose.playAnimation()


        txtDialogClick.clickNoRepeat {
            pushCardPopupListener!!.selectGoto(beingLiveBean)
        }
    }
    var pushCardPopupListener: PushCardPopupListener?=null

    //点击事件
    interface  PushCardPopupListener{
        //关闭
        fun  clicktClose()
        fun selectGoto(beingLiveBean: BeingLiveBean)
    }
}