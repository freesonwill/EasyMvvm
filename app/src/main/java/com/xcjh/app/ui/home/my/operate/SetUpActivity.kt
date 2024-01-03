package com.xcjh.app.ui.home.my.operate

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.gyf.immersionbar.ImmersionBar
import com.kongzue.dialogx.dialogs.BottomDialog
import com.kongzue.dialogx.dialogs.BottomMenu
import com.kongzue.dialogx.interfaces.OnBindView
import com.lxj.xpopup.XPopup
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.LoginInfo
import com.xcjh.app.databinding.ActivitySetUpBinding
import com.xcjh.app.ui.home.my.personal.PersonalDataActivity
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.view.PopupExitLogin
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 设置页面
 */
class SetUpActivity  : BaseActivity<SetUpVm, ActivitySetUpBinding>() {


    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .titleBar(mDatabind.titleTop.root)
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
            var popupExitLogin= PopupExitLogin(this)

            var popwindow = XPopup.Builder(this)
                .hasShadowBg(false)
                .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                .isViewMode(true)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                //                        .isThreeDrag(true) //是否开启三阶拖拽，如果设置enableDrag(false)则无效
                .asCustom(popupExitLogin).show()
            popupExitLogin.popupExitLoginListener=object :PopupExitLogin.PopupExitLoginListener{
                override fun clickClose() {
                    mViewModel.unbindPush()
                    popwindow.dismiss()
                }

            }

//            BottomMenu.show(object :OnBindView<BottomDialog>(R.layout.dialog_login_out){
//                override fun onBind(dialog: BottomDialog?, view: View?) {
//                    var txtDialogCancel= view!!.findViewById<AppCompatTextView>(R.id.txtDialogCancel)
//                    var txtDialogLogin= view!!.findViewById<AppCompatTextView>(R.id.txtDialogLogin)
//                    txtDialogCancel.clickNoRepeat {
//                        dialog!!.dismiss()
//                    }
//                    txtDialogLogin.clickNoRepeat {
//                        mViewModel.unbindPush()
//
//                        dialog!!.dismiss()
//                    }
//                }
//
//            })

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
