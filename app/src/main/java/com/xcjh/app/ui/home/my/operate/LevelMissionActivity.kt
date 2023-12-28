package com.xcjh.app.ui.home.my.operate

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.databinding.ActivityLevelMissionBinding
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.vm.MainVm
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 我的等级任务中心
 */
class LevelMissionActivity  : BaseActivity<LevelMissionVm, ActivityLevelMissionBinding>() {
    private val mainVm: MainVm by viewModels()
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(false)//黑色
            .titleBar(mDatabind.titleTop)
            .init()

        //更新用户信息
        appViewModel.userInfo.observe(this){
            data()

        }
        mDatabind.ivLevelClose.clickNoRepeat {
            finish()
        }


        mainVm.getUserInfo()

//        mDatabind.titleTop.tvTitle.text=resources.getString(R.string.level_txt_title)
        //进入首页
        mDatabind.txtLevelClickInteraction.clickNoRepeat {
            appViewModel.mainViewPagerEvent.value=-1
            finish()
        }

        //进入首页
        mDatabind.txtLevelClickWatch.clickNoRepeat {
            appViewModel.mainViewPagerEvent.value=-1
            finish()
        }



    }
    fun data(){
        if(CacheUtil.isLogin()){
            if(CacheUtil.getUser()!=null){
                var user=CacheUtil.getUser()
                Glide.with(this)
                    .load(user!!.head) // 替换为您要加载的图片 URL
                    .error(R.drawable.icon_login_my_head)
                    .placeholder(R.drawable.icon_login_my_head)
                    .into(mDatabind.ivLevelHead)
                mDatabind.txtLevelName.text=user!!.name
                if(user!!.growthValueNext!=null){
                    var result=user!!.growthValueNext!!.toLong().compareTo(user!!.growthValue!!.toLong())
                    //超过最高等级
                    if(result>0){
                        mDatabind.progressLevel.max=(user!!.growthValueNext!!.toFloat())
                        mDatabind.progressLevel.progress=user!!.growthValue!!.toFloat()
//                    mDatabind.progressLevel.progress=150f

                        mDatabind.txtLevelGrow.text=resources.getString(R.string.level_txt_grow,"${(user!!.growthValueNext!!.toFloat()-user!!.growthValue!!.toFloat()).toInt()}")
//                    mDatabind.txtLevelGrow.text=resources.getString(R.string.level_txt_grow,"${(user!!.growthValueNext!!.toFloat()).toInt()}")
                    }else{
                        mDatabind.progressLevel.max=(user!!.growthValueNext!!.toFloat())
                        mDatabind.progressLevel.progress=(user!!.growthValueNext!!.toFloat())
                        mDatabind.txtLevelGrow.text=""
                    }

                    if (user!!.lvNum.equals("1")){
                        mDatabind.ivLevelTxt.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.wd_dj_huangtong_wenzi))
                        mDatabind.ivLevelIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_badge_yi))
                        mDatabind.rlLevelBe.setBackgroundResource(R.drawable.gradual_level_yi)
                        mDatabind.progressLevel.highlightView.color=ContextCompat.getColor(this, R.color.c_cb6741)
                        mDatabind.txtLevelGrow.setTextColor(ContextCompat.getColor(this, R.color.c_a5502f))
                    }else if (user!!.lvNum.equals("2")){
                        mDatabind.ivLevelTxt.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.wd_dj_baiyin_wenzi))
                        mDatabind.ivLevelIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_badge_er))
                        mDatabind.rlLevelBe.setBackgroundResource(R.drawable.gradual_level_er)
                        mDatabind.progressLevel.highlightView.color=ContextCompat.getColor(this, R.color.c_929292)
                        mDatabind.txtLevelGrow.setTextColor(ContextCompat.getColor(this, R.color.c_727272))
                    }else if (user!!.lvNum.equals("3")){
                        mDatabind.ivLevelTxt.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.wd_dj_huangjing_wenzi))
                        mDatabind.ivLevelIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_badge_san))
                        mDatabind.rlLevelBe.setBackgroundResource(R.drawable.gradual_level_san)
                        mDatabind.progressLevel.highlightView.color=ContextCompat.getColor(this, R.color.c_f0a248)
                        mDatabind.txtLevelGrow.setTextColor(ContextCompat.getColor(this, R.color.c_8f612c))
                    }else if (user!!.lvNum.equals("4")){
                        mDatabind.ivLevelTxt.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.wd_dj_bojing_wenzi))
                        mDatabind.ivLevelIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_badge_si))
                        mDatabind.rlLevelBe.setBackgroundResource(R.drawable.gradual_level_si)
                        mDatabind.progressLevel.highlightView.color=ContextCompat.getColor(this, R.color.c_62aeb8)
                        mDatabind.txtLevelGrow.setTextColor(ContextCompat.getColor(this, R.color.c_51858c))
                    }else if (user!!.lvNum.equals("5")){
                        mDatabind.ivLevelTxt.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.wd_dj_zuanshi_wenzi))
                        mDatabind.ivLevelIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_badge_wu))
                        mDatabind.rlLevelBe.setBackgroundResource(R.drawable.gradual_level_wu)
                        mDatabind.progressLevel.highlightView.color=ContextCompat.getColor(this, R.color.c_a474c8)
                        mDatabind.txtLevelGrow.setTextColor(ContextCompat.getColor(this, R.color.c_865aa8))
                    }else if (user!!.lvNum.equals("6")){
                        mDatabind.ivLevelTxt.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.wd_dj_xingyao_wenzi))
                        mDatabind.ivLevelIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_badge_liu))
                        mDatabind.rlLevelBe.setBackgroundResource(R.drawable.gradual_level_liu)
                        mDatabind.progressLevel.highlightView.color=ContextCompat.getColor(this, R.color.c_737ed6)
                        mDatabind.txtLevelGrow.setTextColor(ContextCompat.getColor(this, R.color.c_5a64b7))
                    }else  {
                        mDatabind.ivLevelTxt.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.wd_dj_xingyao_wenzi))
                        mDatabind.ivLevelIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_badge_liu))
                        mDatabind.rlLevelBe.setBackgroundResource(R.drawable.gradual_level_liu)
                        mDatabind.progressLevel.highlightView.color=ContextCompat.getColor(this, R.color.c_737ed6)
                        mDatabind.txtLevelGrow.setTextColor(ContextCompat.getColor(this, R.color.c_5a64b7))
                    }

                }else{
                    mDatabind.progressLevel.visibility= View.GONE
                }
                setLevelShow(user!!.lvNum!!)
            }

        }
    }

    fun setLevelShow(lvNum:String="1"){
        if(lvNum.equals("1")){
            mDatabind.ivShowYi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_current_level))
            mDatabind.ivShowEr.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowSan.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowSi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowWu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowLiu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            var layoutParams = mDatabind.ivShowYi.layoutParams
            layoutParams.width=dp2px(11)
            layoutParams.height=dp2px(11)
            mDatabind.ivShowYi.layoutParams=layoutParams

            var layoutParamsEr = mDatabind.ivShowEr.layoutParams
            layoutParamsEr.width=dp2px(5)
            layoutParamsEr.height=dp2px(5)
            mDatabind.ivShowEr.layoutParams=layoutParamsEr
            mDatabind.ivShowSan.layoutParams=layoutParamsEr
            mDatabind.ivShowSi.layoutParams=layoutParamsEr
            mDatabind.ivShowWu.layoutParams=layoutParamsEr
            mDatabind.ivShowLiu.layoutParams=layoutParamsEr

            mDatabind.txtLevelNameYi.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameEr.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))
            mDatabind.txtLevelNameSan.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))
            mDatabind.txtLevelNameSi.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))
            mDatabind.txtLevelNameWu.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))
            mDatabind.txtLevelNameLiu.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))

            var layoutParamsView = mDatabind.viewLevelShow.layoutParams
            layoutParamsView.width=dp2px(0)
            layoutParamsView.height=dp2px(1)
            mDatabind.viewLevelShow.layoutParams=layoutParamsView


        }else  if(lvNum.equals("2")){
            mDatabind.ivShowYi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowEr.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_current_level))
            mDatabind.ivShowSan.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowSi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowWu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowLiu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            var layoutParams = mDatabind.ivShowEr.layoutParams
            layoutParams.width=dp2px(11)
            layoutParams.height=dp2px(11)
            mDatabind.ivShowEr.layoutParams=layoutParams

            var layoutParamsEr = mDatabind.ivShowYi.layoutParams
            layoutParamsEr.width=dp2px(5)
            layoutParamsEr.height=dp2px(5)
            mDatabind.ivShowYi.layoutParams=layoutParamsEr
            mDatabind.ivShowSan.layoutParams=layoutParamsEr
            mDatabind.ivShowSi.layoutParams=layoutParamsEr
            mDatabind.ivShowWu.layoutParams=layoutParamsEr
            mDatabind.ivShowLiu.layoutParams=layoutParamsEr

            mDatabind.txtLevelNameYi.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameEr.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameSan.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))
            mDatabind.txtLevelNameSi.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))
            mDatabind.txtLevelNameWu.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))
            mDatabind.txtLevelNameLiu.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))

            var layoutParamsView = mDatabind.viewLevelShow.layoutParams
            var width=  mDatabind.llLevelViewYi.width+( mDatabind.llLevelViewEr.width/2)
            layoutParamsView.width=width
            layoutParamsView.height=dp2px(1)
            mDatabind.viewLevelShow.layoutParams=layoutParamsView


        }else  if(lvNum.equals("3")){
            mDatabind.ivShowYi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowEr.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowSan.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_current_level))
            mDatabind.ivShowSi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowWu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowLiu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            var layoutParams = mDatabind.ivShowSan.layoutParams
            layoutParams.width=dp2px(11)
            layoutParams.height=dp2px(11)
            mDatabind.ivShowSan.layoutParams=layoutParams

            var layoutParamsEr = mDatabind.ivShowYi.layoutParams
            layoutParamsEr.width=dp2px(5)
            layoutParamsEr.height=dp2px(5)
            mDatabind.ivShowYi.layoutParams=layoutParamsEr
            mDatabind.ivShowEr.layoutParams=layoutParamsEr
            mDatabind.ivShowSi.layoutParams=layoutParamsEr
            mDatabind.ivShowWu.layoutParams=layoutParamsEr
            mDatabind.ivShowLiu.layoutParams=layoutParamsEr

            mDatabind.txtLevelNameYi.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameEr.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameSan.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameSi.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))
            mDatabind.txtLevelNameWu.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))
            mDatabind.txtLevelNameLiu.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))

            var layoutParamsView = mDatabind.viewLevelShow.layoutParams
            var width=  mDatabind.llLevelViewYi.width+ mDatabind.llLevelViewEr.width+( mDatabind.llLevelViewSan.width/2)
            layoutParamsView.width=width
            layoutParamsView.height=dp2px(1)
            mDatabind.viewLevelShow.layoutParams=layoutParamsView


        }else  if(lvNum.equals("4")){
            mDatabind.ivShowYi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowEr.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowSan.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowSi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_current_level))
            mDatabind.ivShowWu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowLiu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            var layoutParams = mDatabind.ivShowSi.layoutParams
            layoutParams.width=dp2px(11)
            layoutParams.height=dp2px(11)
            mDatabind.ivShowSi.layoutParams=layoutParams

            var layoutParamsEr = mDatabind.ivShowYi.layoutParams
            layoutParamsEr.width=dp2px(5)
            layoutParamsEr.height=dp2px(5)
            mDatabind.ivShowYi.layoutParams=layoutParamsEr
            mDatabind.ivShowEr.layoutParams=layoutParamsEr
            mDatabind.ivShowSan.layoutParams=layoutParamsEr
            mDatabind.ivShowWu.layoutParams=layoutParamsEr
            mDatabind.ivShowLiu.layoutParams=layoutParamsEr

            mDatabind.txtLevelNameYi.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameEr.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameSan.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameSi.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameWu.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))
            mDatabind.txtLevelNameLiu.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))

            var layoutParamsView = mDatabind.viewLevelShow.layoutParams
            var width=  mDatabind.llLevelViewYi.width+ mDatabind.llLevelViewEr.width+
                    mDatabind.llLevelViewSan.width+( mDatabind.llLevelViewSi.width/2)
            layoutParamsView.width=width
            layoutParamsView.height=dp2px(1)
            mDatabind.viewLevelShow.layoutParams=layoutParamsView


        }else  if(lvNum.equals("5")){
            mDatabind.ivShowYi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowEr.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowSan.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowSi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowWu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_current_level))
            mDatabind.ivShowLiu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            var layoutParams = mDatabind.ivShowWu.layoutParams
            layoutParams.width=dp2px(11)
            layoutParams.height=dp2px(11)
            mDatabind.ivShowWu.layoutParams=layoutParams

            var layoutParamsEr = mDatabind.ivShowYi.layoutParams
            layoutParamsEr.width=dp2px(5)
            layoutParamsEr.height=dp2px(5)
            mDatabind.ivShowYi.layoutParams=layoutParamsEr
            mDatabind.ivShowEr.layoutParams=layoutParamsEr
            mDatabind.ivShowSan.layoutParams=layoutParamsEr
            mDatabind.ivShowSi.layoutParams=layoutParamsEr
            mDatabind.ivShowLiu.layoutParams=layoutParamsEr

            mDatabind.txtLevelNameYi.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameEr.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameSan.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameSi.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameWu.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameLiu.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))

            var layoutParamsView = mDatabind.viewLevelShow.layoutParams
            var width=  mDatabind.llLevelViewYi.width+ mDatabind.llLevelViewEr.width+
                    mDatabind.llLevelViewSan.width+mDatabind.llLevelViewSi.width+( mDatabind.llLevelViewWu.width/2)
            layoutParamsView.width=width
            layoutParamsView.height=dp2px(1)
            mDatabind.viewLevelShow.layoutParams=layoutParamsView


        }else{
            mDatabind.ivShowYi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowEr.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowSan.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowSi.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowWu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_icon_bai))
            mDatabind.ivShowLiu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_current_level))
            var layoutParams = mDatabind.ivShowLiu.layoutParams
            layoutParams.width=dp2px(11)
            layoutParams.height=dp2px(11)
            mDatabind.ivShowLiu.layoutParams=layoutParams

            var layoutParamsEr = mDatabind.ivShowYi.layoutParams
            layoutParamsEr.width=dp2px(5)
            layoutParamsEr.height=dp2px(5)
            mDatabind.ivShowYi.layoutParams=layoutParamsEr
            mDatabind.ivShowEr.layoutParams=layoutParamsEr
            mDatabind.ivShowSan.layoutParams=layoutParamsEr
            mDatabind.ivShowSi.layoutParams=layoutParamsEr
            mDatabind.ivShowWu.layoutParams=layoutParamsEr

            mDatabind.txtLevelNameYi.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameEr.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameSan.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameSi.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameWu.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.txtLevelNameLiu.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))

            var layoutParamsView = mDatabind.viewLevelShow.layoutParams
            var width=  mDatabind.llLevelViewYi.width+ mDatabind.llLevelViewEr.width+
                    mDatabind.llLevelViewSan.width+mDatabind.llLevelViewSi.width+
                    mDatabind.llLevelViewWu.width+mDatabind.llLevelViewLiu.width-dp2px(2)
            layoutParamsView.width=width
            layoutParamsView.height=dp2px(1)
            mDatabind.viewLevelShow.layoutParams=layoutParamsView
        }

    }
}