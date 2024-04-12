package com.xcjh.app.ui.home.my.operate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.databinding.ActivityAccountNumberBinding
import com.xcjh.app.databinding.ActivitySetUpBinding
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 账号管理
 */
class AccountNumberActivity  : BaseActivity<AccountNumberVm, ActivityAccountNumberBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .titleBar(mDatabind.titleTop.root)
            .navigationBarDarkIcon(true)
            .navigationBarColor(R.color.c_ffffff)
            .init()
        mDatabind.titleTop.tvTitle.text = resources.getString(R.string.number_txt_title)
        //收到通知其他地方登录
        appViewModel.quitLoginEvent.observe(this){
            finish()
        }
        mDatabind.rlSetPhone.clickNoRepeat {
            if (CacheUtil.isLogin()) {
                if (CacheUtil.getUser() != null) {

                    if(CacheUtil.getUser()!!.tel!!.isEmpty()){
                        startNewActivity<BindFacilityActivity>(){
                            putExtra("type", 0)
                        }
                    }

                }
            }
        }
        //点击邮箱
        mDatabind.rlSetMailbox.clickNoRepeat {
            if (CacheUtil.isLogin()) {
                if (CacheUtil.getUser() != null) {
                    if(CacheUtil.getUser()!!.email!!.isEmpty()){
                        startNewActivity<BindFacilityActivity>(){
                            putExtra("type", 1)
                        }
                    }



                }
            }
        }

        //更新用户信息
        appViewModel.userInfo.observe(this) {
            setData()

        }
    }
    fun setData() {
        if (CacheUtil.isLogin()) {
            if (CacheUtil.getUser() != null) {
                var user = CacheUtil.getUser()
                if(user!!.tel!!.isEmpty()){
                    mDatabind.txtNumberPhone.text=resources.getString(R.string.number_txt_set_not)
                    mDatabind.ivNumberPhoneIcon.visibility=View.VISIBLE
                }else{
                    mDatabind.txtNumberPhone.text=user!!.tel
                    mDatabind.ivNumberPhoneIcon.visibility=View.GONE
                }

                if(user!!.email!!.isEmpty()){
                    mDatabind.txtNumberEmail.text=resources.getString(R.string.number_txt_set_not)
                    mDatabind.ivNumberEmailIcon.visibility=View.VISIBLE
                }else{
                    mDatabind.txtNumberEmail.text=user!!.email
                    mDatabind.ivNumberEmailIcon.visibility=View.GONE
                }
            }

        }


    }

    override fun onResume() {
        super.onResume()
        setData()
    }

}