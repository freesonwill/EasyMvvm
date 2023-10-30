package com.xcjh.app.ui.home.my.operate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
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
import com.xcjh.app.vm.MainVm
import com.xcjh.base_lib.utils.TAG
import com.xcjh.base_lib.utils.loge
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 我的等级任务中心
 */
class LevelMissionActivity  : BaseActivity<LevelMissionVm, ActivityLevelMissionBinding>() {
    private val mainVm: MainVm by viewModels()
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(false)//黑色
            .titleBar(mDatabind.titleTop.rltTop)
            .init()

        //更新用户信息
        appViewModel.userInfo.observe(this){
            data()

        }


        mainVm.getUserInfo()

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



    }
    fun data(){
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
                    mDatabind.progressLevel.max=(user!!.growthValueNext!!.toFloat())
                    mDatabind.progressLevel.progress=user!!.growthValue!!.toFloat()
//                    mDatabind.progressLevel.progress=150f
                    mDatabind.txtLevelShow.text="L${user!!.lvNum}${user!!.lvName}"
                    mDatabind.txtLevelGrow.text=resources.getString(R.string.level_txt_grow,"${(user!!.growthValueNext!!.toFloat()-user!!.growthValue!!.toFloat()).toInt()}")
//                    mDatabind.txtLevelGrow.text=resources.getString(R.string.level_txt_grow,"${(user!!.growthValueNext!!.toFloat()).toInt()}")


//                    mDatabind.txtLeve.text="Lv.${user!!.lvNum} ${user!!.lvName}"
                    mDatabind.txtLeve.text="${user!!.lvName}"

                    if (user!!.lvNum.equals("1")){
                        mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_level_yi))
                    }else if (user!!.lvNum.equals("2")){
                        mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_level_er))
                    }else if (user!!.lvNum.equals("3")){
                        mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_level_san))
                    }else if (user!!.lvNum.equals("4")){
                        mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_level_si))
                    }else if (user!!.lvNum.equals("5")){
                        mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_level_wu))
                    }else if (user!!.lvNum.equals("6")){
                        mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_level_liu))
                    }else if (user!!.lvNum.equals("7")){
                        mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_level_qi))
                    }else if (user!!.lvNum.equals("8")){
                        mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_level_ba))
                    }else  {
                        mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_level_yi))
                    }

                }else{
                    mDatabind.progressLevel.visibility= View.GONE
                }

            }

        }
    }
}