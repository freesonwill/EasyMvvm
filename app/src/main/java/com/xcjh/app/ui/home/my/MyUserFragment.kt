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
import com.gyf.immersionbar.ImmersionBar
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.LoginInfo
import com.xcjh.app.databinding.FragmentMyUserBinding
import com.xcjh.app.net.ApiComService
import com.xcjh.app.ui.home.my.operate.*
import com.xcjh.app.ui.login.LoginActivity
import com.xcjh.app.ui.notice.MyNoticeActivity
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.web.WebActivity
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.copyToClipboard
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.shareText
import com.xcjh.base_lib.utils.view.clickNoRepeat


/**
 * 我的
 */
class MyUserFragment : BaseFragment<MyUseVm, FragmentMyUserBinding>() {


    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)//黑色
            .titleBar(mDatabind.rlMyTopyi)
            .navigationBarColor(R.color.c_ffffff)
            .init()
        //查看等级任务
        mDatabind.rlClickLevel.clickNoRepeat {
            if(CacheUtil.isLogin()){
                startNewActivity<LevelMissionActivity>()
            }else{
                startNewActivity<LoginActivity>()
            }


        }
        //设置
        mDatabind.ivMySet.clickNoRepeat {
            if(CacheUtil.isLogin()){
                startNewActivity<SetUpActivity>()
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
            startNewActivity<EventsCentreActivity>()
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
            shareText(requireContext(),ApiComService.SHARE_IP)

//          requireContext().copyToClipboard(ApiComService.SHARE_IP)
//            myToast(resources.getString(R.string.my_txt_copy_link))
//
        }
        //广告
        mDatabind.ivMyAdvertising.clickNoRepeat {
            startNewActivity<WebActivity>() {
                this.putExtra(Constants.WEB_URL,mViewModel.advertisement.value!!.targetUrl)
                this.putExtra(Constants.CHAT_TITLE, getString(R.string.my_app_name))
            }
        }


        //更新用户信息
        appViewModel.userInfo.observe(this){
            setData()
            mViewModel.getIndividualCenter()

        }

        mViewModel.getIndividualCenter()



        //登录或者登出
        appViewModel.updateLoginEvent.observe(this){
             if(!it){
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
                    .error(R.drawable.icon_my_head)
                    .placeholder(R.drawable.icon_my_head)
                    .into(mDatabind.ivMyHead)
                mDatabind.txtMyName.text=user!!.name
                mDatabind.txtMyNum.text="${user!!.lvName}"


                if (user!!.lvNum.equals("1")){
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_yi))

                    mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_user_level1))

                }else if (user!!.lvNum.equals("2")){
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_er))

                    mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_user_level2))

                }else if (user!!.lvNum.equals("3")){
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_san))

                    mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_user_level3))
                }else if (user!!.lvNum.equals("4")){
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_si))

                    mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_user_level4))
                }else if (user!!.lvNum.equals("5")){
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_wu))

                    mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_user_level5))
                }else if (user!!.lvNum.equals("6")){
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_liu))

                    mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_user_level6))
                }else if (user!!.lvNum.equals("7")){
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_qi))

                    mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_user_level7))
                }else if (user!!.lvNum.equals("8")){
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_ba))

                    mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_user_level8))
                }else  {
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_yi))

                    mDatabind.ivIvLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_user_level1))
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
        mDatabind.ivMyHead.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.icon_my_head))
        mDatabind.txtMyName.text=resources.getString(R.string.my_txt_click_login)
        mDatabind.iiIsShowLeve.visibility= View.GONE

     }

    override fun createObserver() {
        super.createObserver()
        //获取个人中心广告
        mViewModel.advertisement.observe(this){
            mDatabind.ivMyAdvertising.visibility=View.VISIBLE
            Glide.with(requireContext())
                .load(it.imgUrl) // 替换为您要加载的图片 URL
                .error(R.drawable.zwt_banner)
                .placeholder(R.drawable.zwt_banner)
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
//        CustomDialog.show(object :OnBindView<CustomDialog>(R.layout.dialog_login_out){
//            override fun onBind(dialog: CustomDialog?, view: View?) {
//               var txtOutVerify= view!!.findViewById<AppCompatTextView>(R.id.txtOutVerify)
//               var txtOutCancel= view!!.findViewById<AppCompatTextView>(R.id.txtOutCancel)
//                txtOutVerify.clickNoRepeat {
//
                    mViewModel.exitLogin()
//                    dialog!!.dismiss()
//                }
//                txtOutCancel.clickNoRepeat {
//                    dialog!!.dismiss()
//                }
//            }
//
//        })
    }


}