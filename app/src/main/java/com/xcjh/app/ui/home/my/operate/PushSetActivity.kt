package com.xcjh.app.ui.home.my.operate

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.gyf.immersionbar.ImmersionBar
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.PushBean
import com.xcjh.app.databinding.ActivityPushSetBinding
import com.xcjh.base_lib.utils.getXXPermissionsPush
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 推送设置
 */
class PushSetActivity : BaseActivity<PushSetVm, ActivityPushSetBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .titleBar(mDatabind.titleTop.root)
            .init()
        mDatabind.titleTop.tvTitle.text=resources.getString(R.string.push_txt_title)
        if(XXPermissions.isGranted(this, Permission.POST_NOTIFICATIONS)){
            mViewModel.getInfoPush()

        }

        //设置
        setDate()
        mDatabind.rrPushClick.clickNoRepeat {

            if(!XXPermissions.isGranted(this, Permission.POST_NOTIFICATIONS)){
                getXXPermissionsPush(this){
                    setDate()
                    mViewModel.getInfoPush()
                }

            }
        }

        //关注比赛
        mDatabind.sbPushConcern.clickNoRepeat {
            //[name参数 liveOpen:切换主播开播通知 followMatch:切换关注比赛通知
            mViewModel.setPush("followMatch",mDatabind.sbPushConcern.isChecked)
        }
        //主播开播
        mDatabind.sbPushAnchor.clickNoRepeat {
            //[name参数 liveOpen:切换主播开播通知 followMatch:切换关注比赛通知
            mViewModel.setPush("liveOpen",mDatabind.sbPushAnchor.isChecked)
        }
    }


    fun setDate(){
        if(XXPermissions.isGranted(this, Permission.POST_NOTIFICATIONS)){

            mDatabind.txtPushIsOpen.text=resources.getString(R.string.push_txt_already)
            mDatabind.txtPushIsOpen.setTextColor(ContextCompat.getColor(this,R.color.c_34a853))
            mDatabind.rrPushIsShowConcern.visibility= View.VISIBLE
            mDatabind.rrPushIsShowAnchor.visibility= View.VISIBLE
        }else{
            mDatabind.txtPushIsOpen.text=resources.getString(R.string.push_txt_open)
            mDatabind.txtPushIsOpen.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))
            mDatabind.rrPushIsShowConcern.visibility= View.GONE
            mDatabind.rrPushIsShowAnchor.visibility= View.GONE
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.pushBean.observe(this){
            setState(it)
        }
        mViewModel.switchValue.observe(this){
            if(it.name.equals("followMatch")){
                mDatabind.sbPushConcern.isChecked=it.state
            }else{
                mDatabind.sbPushAnchor.isChecked=it.state
            }
        }
    }

    fun setState(bean: PushBean){
        //	是否开启关注比赛通知 1是 0否
        if(bean.ynFollowMatch==1){
            mDatabind.sbPushConcern.isChecked=true
        }else{
            mDatabind.sbPushConcern.isChecked=false
        }

        //	//是否开启主播开播通知 1是 0否
        if(bean.ynLiveOpen==1){
            mDatabind.sbPushAnchor.isChecked=true
        }else{
            mDatabind.sbPushAnchor.isChecked=false
        }
    }

}