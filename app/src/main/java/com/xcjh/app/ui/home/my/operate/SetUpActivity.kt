package com.xcjh.app.ui.home.my.operate

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.gyf.immersionbar.ImmersionBar
import com.kongzue.dialogx.dialogs.BottomDialog
import com.kongzue.dialogx.dialogs.BottomMenu
import com.kongzue.dialogx.interfaces.OnBindView
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.LoginInfo
import com.xcjh.app.databinding.ActivitySetUpBinding
import com.xcjh.app.ui.home.my.personal.PersonalDataActivity
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 设置页面
 */
class SetUpActivity  : BaseActivity<SetUpVm, ActivitySetUpBinding>() {


    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .titleBar(mDatabind.titleTop.root)
            .navigationBarColor(R.color.c_ffffff)
            .init()
        mDatabind.titleTop.tvTitle.text = resources.getString(R.string.setting)

        mDatabind.rlSetData.clickNoRepeat {
            startNewActivity<PersonalDataActivity>()
        }

        mDatabind.rlSetPush.clickNoRepeat {
            startNewActivity<PushSetActivity>()
        }

        mDatabind.rlSetVibrate.clickNoRepeat {
            startNewActivity<VibrateSetActivity>()
        }
        mDatabind.txtSetExit.clickNoRepeat {
            BottomMenu.show(object :OnBindView<BottomDialog>(R.layout.dialog_login_out){
                override fun onBind(dialog: BottomDialog?, view: View?) {
                    var txtDialogCancel= view!!.findViewById<AppCompatTextView>(R.id.txtDialogCancel)
                    var txtDialogLogin= view!!.findViewById<AppCompatTextView>(R.id.txtDialogLogin)
                    txtDialogCancel.clickNoRepeat {
                        dialog!!.dismiss()
                    }
                    txtDialogLogin.clickNoRepeat {
                        mViewModel.exitLogin()

                        dialog!!.dismiss()
                    }
                }

            })

        }
    }

    override fun createObserver() {
        super.createObserver()
        //退出登录
        mViewModel.exitLive.observe(this){
            if(it){
                CacheUtil.setIsLogin(false, LoginInfo("","", ""))
                finish()
            }

        }
    }
}
