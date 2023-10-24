package com.xcjh.app.ui.home.my

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.LoginInfo
import com.xcjh.app.databinding.FragmentMyUserBinding
import com.xcjh.app.ui.home.home.tab.MainRecommendVm
import com.xcjh.app.ui.home.my.operate.*
import com.xcjh.app.ui.home.my.personal.PersonalDataActivity
import com.xcjh.app.ui.login.LoginActivity
import com.xcjh.app.ui.notice.MyNoticeActivity
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.view.clickNoRepeat
import kotlin.random.Random


/**
 * 我的
 */
class MyUserFragment : BaseFragment<MyUseVm, FragmentMyUserBinding>() {


    override fun initView(savedInstanceState: Bundle?) {
        //查看等级任务
        mDatabind.rlClickLevel.clickNoRepeat {
            if(CacheUtil.isLogin()){
                startNewActivity<LevelMissionActivity>()
            }else{
                startNewActivity<LoginActivity>()
            }


        }
        //点击我的名字
        mDatabind.txtMyName.clickNoRepeat {
            if(!CacheUtil.isLogin()){
                startNewActivity<LoginActivity>()
            }


        }



        //是否登录
        mDatabind.ivMyHead.clickNoRepeat {
            if(!CacheUtil.isLogin()){
                startNewActivity<LoginActivity>()
            }
        }
        //我的订阅
        mDatabind.llMyClickSubscribe.clickNoRepeat {
            if(CacheUtil.isLogin()){
                startNewActivity<MyNoticeActivity>()
            }else{
                startNewActivity<LoginActivity>()
            }
        }
        //活动中心
        mDatabind.llMyClickEvents.clickNoRepeat {
            if(CacheUtil.isLogin()){
                startNewActivity<EventsCentreActivity>()
            }else{
                startNewActivity<LoginActivity>()
            }
        }

        //我的关注
        mDatabind.llMyClickFollow.clickNoRepeat {
            if(CacheUtil.isLogin()){
                startNewActivity<MyFollowListActivity>()
            }else{
                startNewActivity<LoginActivity>()
            }
        }

        //观看历史
        mDatabind.llMyClickHistory.clickNoRepeat {
            if(CacheUtil.isLogin()){
                startNewActivity<ViewingHistoryListActivity>()
            }else{
                startNewActivity<LoginActivity>()
            }
        }

        //编辑资料
        mDatabind.rlMyClickEdit.clickNoRepeat {
            if(CacheUtil.isLogin()){
                startNewActivity<PersonalDataActivity>()
            }else{
                startNewActivity<LoginActivity>()
            }
        }

        //联系我们
        mDatabind.rlMyClickContact.clickNoRepeat {
            if(CacheUtil.isLogin()){
                startNewActivity<ContactUsActivity>()
            }else{
                startNewActivity<LoginActivity>()
            }
        }
        //邀请好友
        mDatabind.rlMyClickInvite.clickNoRepeat {
            showCopyLink()
        }


        //退出登录
        mDatabind.rlMyClickLogOut.clickNoRepeat {

            isLoginOut()
        }
        //更新用户信息
        appViewModel.userInfo.observe(this){
            setData()
            mViewModel.getIndividualCenter()

        }

        mViewModel.getIndividualCenter()

        if(CacheUtil.isLogin()){
            mDatabind.rlMyClickLogOut.visibility=View.VISIBLE
        }else{
            mDatabind.rlMyClickLogOut.visibility=View.GONE
        }

        //登录或者登出
        appViewModel.updateLoginEvent.observe(this){
             if(it){
                 mDatabind.rlMyClickLogOut.visibility=View.VISIBLE
            }else{
                 notLogin()

            }
        }


    }


    /**
     * 设置参数
     */
    fun setData(){
        if(CacheUtil.isLogin()){
            if(CacheUtil.getUser()!=null){
                var user=CacheUtil.getUser()
                mDatabind.iiIsShowLeve.visibility= View.VISIBLE
                Glide.with(requireContext())
                    .load(user!!.head) // 替换为您要加载的图片 URL
                    .error(R.drawable.load_head)
                    .placeholder(R.drawable.load_head)
                    .into(mDatabind.ivMyHead)
                mDatabind.txtMyName.text=user!!.name
                mDatabind.txtMyNum.text="Lv.${user!!.lvNum} ${user!!.lvName}"

                if (user!!.lvNum.equals("1")){
                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_yi))
                }else if (user!!.lvNum.equals("2")){
                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_er))
                }else if (user!!.lvNum.equals("3")){
                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_san))
                }else if (user!!.lvNum.equals("4")){
                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_si))
                }else if (user!!.lvNum.equals("5")){
                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_wu))
                }else if (user!!.lvNum.equals("6")){
                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_liu))
                }else if (user!!.lvNum.equals("7")){
                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_qi))
                }else if (user!!.lvNum.equals("8")){
                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_ba))
                }else  {
                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_yi))
                }

            }else{
                notLogin()
            }


        }else{
            notLogin()
        }


    }

    /**
     * 没有登录
     */
    fun notLogin(){
        mDatabind.ivMyHead.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.load_head))
        mDatabind.txtMyName.text=resources.getString(R.string.my_txt_click_login)
        mDatabind.iiIsShowLeve.visibility= View.GONE
        mDatabind.rlMyClickLogOut.visibility=View.GONE
    }

    override fun createObserver() {
        super.createObserver()
        //获取个人中心广告
        mViewModel.advertisement.observe(this){
            mDatabind.ivMyAdvertising.visibility=View.VISIBLE
            Glide.with(requireContext())
                .load(it.imgUrl) // 替换为您要加载的图片 URL
                .transform(RoundedCorners(appContext.dp2px(8))) // 设置圆角半径，单位为像素
                .error(R.drawable.main_banner_load)
                .placeholder(R.drawable.main_banner_load)
                .into(mDatabind.ivMyAdvertising)
        }
        //获取广告失败
        mViewModel.advertisementErr.observe(this){
            mDatabind.ivMyAdvertising.visibility=View.GONE
        }
        //退出登录
        mViewModel.exitLive.observe(this){
            if(it){
                CacheUtil.setIsLogin(false, LoginInfo("","", ""))
            }

        }
    }

    /**
     * 复制成功的弹出框
     */
    @SuppressLint("SuspiciousIndentation")
    fun showCopyLink(){
      var dialogX=CustomDialog.show(object :OnBindView<CustomDialog>(R.layout.item_copy_succeed){
            override fun onBind(dialog: CustomDialog?, v: View?) {
            }

        })
        Handler(Looper.getMainLooper()).postDelayed(
            Runnable {
                        dialogX.dismiss()
                     }, 2000)

    }

    /**
     * 弹出框是否退出
     */
    fun isLoginOut(){
        CustomDialog.show(object :OnBindView<CustomDialog>(R.layout.dialog_login_out){
            override fun onBind(dialog: CustomDialog?, view: View?) {
               var txtOutVerify= view!!.findViewById<AppCompatTextView>(R.id.txtOutVerify)
               var txtOutCancel= view!!.findViewById<AppCompatTextView>(R.id.txtOutCancel)
                txtOutVerify.clickNoRepeat {

                    mViewModel.exitLogin()
                    dialog!!.dismiss()
                }
                txtOutCancel.clickNoRepeat {
                    dialog!!.dismiss()
                }
            }

        })
    }


}