package com.xcjh.app.ui.home.my.operate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.databinding.ActivityLevelMissionBinding
import com.xcjh.app.databinding.ActivityMyFollowListBinding
import com.xcjh.app.event.AppViewModel
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.utils.TAG
import com.xcjh.base_lib.utils.loge
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 我的等级任务中心
 */
class LevelMissionActivity  : BaseActivity<LevelMissionVm, ActivityLevelMissionBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(false)//黑色
            .titleBar(mDatabind.titleTop.rltTop)
            .init()
        mDatabind.titleTop.tvTitle.text=resources.getString(R.string.level_txt_title)
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

        if(CacheUtil.isLogin()){
            if(CacheUtil.getUser()!=null){
                var user=CacheUtil.getUser()
                Glide.with(this)
                    .load(user!!.head) // 替换为您要加载的图片 URL
                    .error(R.drawable.icon_level_head)
                    .placeholder(R.drawable.icon_level_head)
                    .into(mDatabind.ivLevelHead)
                mDatabind.txtLevelName.text=user!!.name
                if(user!!.growthValueNext!=null){
                    mDatabind.progressLevel.max=(user!!.growthValueNext!!.toFloat()+user!!.growthValue!!.toFloat())
                    mDatabind.progressLevel.progress=user!!.growthValue!!.toFloat()
//                    mDatabind.progressLevel.progress=150f
                    mDatabind.txtLevelShow.text="L${user!!.lvNum}${user!!.lvName}"
//                    mDatabind.txtLevelGrow.text=resources.getString(R.string.level_txt_grow,"${(user!!.growthValueNext!!.toFloat()-user!!.growthValueNext!!.toFloat()).toInt()}")
                    mDatabind.txtLevelGrow.text=resources.getString(R.string.level_txt_grow,"${(user!!.growthValueNext!!.toFloat()).toInt()}")


                    mDatabind.txtLeve.text="Lv.${user!!.lvNum} ${user!!.lvName}"
                    mDatabind.txtLeveName.text="${user!!.lvNum}"
                    if (user!!.lvNum.equals("1")){
                        mDatabind.rlLevelBg.background=ContextCompat.getDrawable(this,R.drawable.level_v_yi)
                        mDatabind.ivLevelV.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_txt_yi))
                        mDatabind.ivLevelIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_yi))

                        mDatabind.txtLeveName.setTextColor(ContextCompat.getColor(this,R.color.c_c5c5c5))
                    }else if (user!!.lvNum.equals("2")){
                        mDatabind.rlLevelBg.background=ContextCompat.getDrawable(this,R.drawable.level_v_er)
                        mDatabind.ivLevelV.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_txt_er))
                        mDatabind.ivLevelIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_er))

                        mDatabind.txtLeveName.setTextColor(ContextCompat.getColor(this,R.color.c_f6d9c8))
                    }else if (user!!.lvNum.equals("3")){
                        mDatabind.rlLevelBg.background=ContextCompat.getDrawable(this,R.drawable.level_v_san)
                        mDatabind.ivLevelV.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_txt_san))
                        mDatabind.ivLevelIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_san))

                        mDatabind.txtLeveName.setTextColor(ContextCompat.getColor(this,R.color.c_d2dcdb))
                    }else if (user!!.lvNum.equals("4")){
                        mDatabind.rlLevelBg.background=ContextCompat.getDrawable(this,R.drawable.level_v_si)
                        mDatabind.ivLevelV.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_txt_si))
                        mDatabind.ivLevelIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_si))

                        mDatabind.txtLeveName.setTextColor(ContextCompat.getColor(this,R.color.c_d0d0d0))
                    }else if (user!!.lvNum.equals("5")){
                        mDatabind.rlLevelBg.background=ContextCompat.getDrawable(this,R.drawable.level_v_wu)
                        mDatabind.ivLevelV.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_txt_wu))
                        mDatabind.ivLevelIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_wu))

                        mDatabind.txtLeveName.setTextColor(ContextCompat.getColor(this,R.color.c_ede2d2))
                    }else if (user!!.lvNum.equals("6")){
                        mDatabind.rlLevelBg.background=ContextCompat.getDrawable(this,R.drawable.level_v_liu)
                        mDatabind.ivLevelV.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_txt_liu))
                        mDatabind.ivLevelIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_liu))

                        mDatabind.txtLeveName.setTextColor(ContextCompat.getColor(this,R.color.c_dbecf0))
                    }else if (user!!.lvNum.equals("7")){
                        mDatabind.rlLevelBg.background=ContextCompat.getDrawable(this,R.drawable.level_v_qi)
                        mDatabind.ivLevelV.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_txt_qi))
                        mDatabind.ivLevelIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_qi))
                        mDatabind.txtLeveName.setTextColor(ContextCompat.getColor(this,R.color.c_d8f0f9))
                    }else if (user!!.lvNum.equals("8")){
                        mDatabind.rlLevelBg.background=ContextCompat.getDrawable(this,R.drawable.level_v_ba)
                        mDatabind.ivLevelV.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_txt_ba))
                        mDatabind.ivLevelIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_ba))
                        mDatabind.txtLeveName.setTextColor(ContextCompat.getColor(this,R.color.c_dbccfb))
                    }else  {
                        mDatabind.rlLevelBg.background=ContextCompat.getDrawable(this,R.drawable.level_v_yi)
                        mDatabind.ivLevelV.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_txt_yi))
                        mDatabind.ivLevelIcon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.level_yi))
                        mDatabind.txtLeveName.setTextColor(ContextCompat.getColor(this,R.color.c_c5c5c5))
                    }

                }else{
                    mDatabind.progressLevel.visibility= View.GONE
                }

            }

        }

    }
}