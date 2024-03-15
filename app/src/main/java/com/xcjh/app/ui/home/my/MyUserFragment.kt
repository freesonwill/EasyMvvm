package com.xcjh.app.ui.home.my

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
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
import com.xcjh.app.ui.home.my.personal.PersonalDataActivity
import com.xcjh.app.ui.login.LoginActivity
import com.xcjh.app.ui.notice.MyNoticeActivity
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.utils.judgeLogin
import com.xcjh.app.web.WebActivity
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.shareText
import com.xcjh.base_lib.utils.view.clickNoRepeat


/**
 * 我的
 */
class MyUserFragment : BaseFragment<MyUseVm, FragmentMyUserBinding>() {

    // 定义属性动画常量
    private val SCALE_X = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.4f, 1.0f)
    private val SCALE_Y = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.4f, 1.0f)

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)//黑色
            .titleBar(mDatabind.rlMyTopyi)
            .navigationBarDarkIcon(true)
            .navigationBarColor(R.color.c_ffffff)
            .init()

        setData()
        //查看等级任务
        mDatabind.rlClickLevel.clickNoRepeat(200) {
            judgeLogin {
                startNewActivity<LevelMissionActivity>()
            }
        }

        //点击增加透明度
        mDatabind.ivMyHead.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.i("SSSSSSSSSs","11111111111111")
                    judgeLogin {
                        mDatabind.ivMyHead.alpha=0.7f
                        mDatabind.txtMyName.alpha=0.7f
                        mDatabind.iiIsShowLeve.alpha=0.7f
                        mDatabind.ivIvLevel.alpha=0.7f
                        mDatabind.txtMyNum.alpha=0.7f
                    }


                }
                MotionEvent.ACTION_UP->{
                    Log.i("SSSSSSSSSs","22222222222222")
                    // 处理抬起事件
                    judgeLogin {

                        mDatabind.ivMyHead.alpha=1f
                        mDatabind.txtMyName.alpha=1f
                        mDatabind.iiIsShowLeve.alpha=1f
                        mDatabind.ivIvLevel.alpha=1f
                        mDatabind.txtMyNum.alpha=1f
                        judgeLogin {
                            startNewActivity<PersonalDataActivity>()
                        }
                    }

                }


            }

            true
        }


        //点击增加透明度
        mDatabind.llClickMy.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        judgeLogin {
                            mDatabind.ivMyHead.alpha=0.3f
                            mDatabind.txtMyName.alpha=0.3f
                            mDatabind.iiIsShowLeve.alpha=0.3f
                            mDatabind.ivIvLevel.alpha=0.3f
                            mDatabind.txtMyNum.alpha=0.3f
                        }


                    }
                      MotionEvent.ACTION_UP->{
                          // 处理抬起事件
                          judgeLogin {

                              mDatabind.ivMyHead.alpha=1f
                              mDatabind.txtMyName.alpha=1f
                              mDatabind.iiIsShowLeve.alpha=1f
                              mDatabind.ivIvLevel.alpha=1f
                              mDatabind.txtMyNum.alpha=1f
                              judgeLogin {
                                  startNewActivity<PersonalDataActivity>()
                              }
                          }

                      }


                }

            true
            }

        //设置
        mDatabind.ivMySet.clickNoRepeat {
            if (CacheUtil.isLogin()) {
                startNewActivity<SetUpActivity>()
            } else {
                startNewActivity<LoginActivity>()
            }


        }

        //点击我的名字
        mDatabind.txtMyName.clickNoRepeat {
            if (!CacheUtil.isLogin()) {
                startNewActivity<LoginActivity>()
            }
        }
        //是否登录
        mDatabind.ivMyHead.clickNoRepeat {
            if (!CacheUtil.isLogin()) {
                startNewActivity<LoginActivity>()
            }
        }
        //我的订阅
        mDatabind.llMyClickSubscribe.clickNoRepeat {

            val animator = ObjectAnimator.ofPropertyValuesHolder(mDatabind.myAnimation, SCALE_X, SCALE_Y)
            animator.duration = 200
            animator.start()

            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    // 动画开始时的操作
                }

                override fun onAnimationEnd(animation: Animator) {
                    // 动画结束时的操作
                    // 在这里可以做一些清理工作或执行其他的逻辑操作
                if (CacheUtil.isLogin()) {
                    startNewActivity<MyNoticeActivity>()
                  } else {
                  startNewActivity<LoginActivity>()
                 }
                }

                override fun onAnimationCancel(animation: Animator) {
                    // 动画被取消时的操作
                }

                override fun onAnimationRepeat(animation: Animator) {
                    // 动画重复播放时的操作
                }
            })

//            mDatabind.myAnimationSubscribe.imageAssetsFolder="subscribe/images"
//            mDatabind.myAnimationSubscribe.setAnimation("subscribe/subscribe_animation.json")
//            mDatabind.myAnimationSubscribe.playAnimation()

        }
        //活动中心
        mDatabind.llMyClickEvents.clickNoRepeat {


            val animator = ObjectAnimator.ofPropertyValuesHolder(mDatabind.myAnimationActivity, SCALE_X, SCALE_Y)
            animator.duration = 200
            animator.start()

            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    // 动画开始时的操作
                }

                override fun onAnimationEnd(animation: Animator) {
                    // 动画结束时的操作
                    // 在这里可以做一些清理工作或执行其他的逻辑操作
                    startNewActivity<EventsCentreActivity>()
                }

                override fun onAnimationCancel(animation: Animator) {
                    // 动画被取消时的操作
                }

                override fun onAnimationRepeat(animation: Animator) {
                    // 动画重复播放时的操作
                }
            })
        }

        //我的关注
        mDatabind.llMyClickFollow.clickNoRepeat {

            val animator = ObjectAnimator.ofPropertyValuesHolder(mDatabind.myAnimationMy, SCALE_X, SCALE_Y)
            animator.duration = 200
            animator.start()

            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    // 动画开始时的操作
                }

                override fun onAnimationEnd(animation: Animator) {
                    // 动画结束时的操作
                    // 在这里可以做一些清理工作或执行其他的逻辑操作
                    if (CacheUtil.isLogin()) {
                        startNewActivity<MyFollowListActivity>()
                    } else {
                        startNewActivity<LoginActivity>()
                    }
                }

                override fun onAnimationCancel(animation: Animator) {
                    // 动画被取消时的操作
                }

                override fun onAnimationRepeat(animation: Animator) {
                    // 动画重复播放时的操作
                }
            })

        }

        //观看历史
        mDatabind.llMyClickHistory.clickNoRepeat {

            val animator = ObjectAnimator.ofPropertyValuesHolder(mDatabind.myAnimationHistory, SCALE_X, SCALE_Y)
            animator.duration = 200
            animator.start()

            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    // 动画开始时的操作
                }

                override fun onAnimationEnd(animation: Animator) {
                    // 动画结束时的操作
                    // 在这里可以做一些清理工作或执行其他的逻辑操作
                    if (CacheUtil.isLogin()) {
                        startNewActivity<ViewingHistoryListActivity>()
                    } else {
                        startNewActivity<LoginActivity>()
                    }

                }

                override fun onAnimationCancel(animation: Animator) {
                    // 动画被取消时的操作
                }

                override fun onAnimationRepeat(animation: Animator) {
                    // 动画重复播放时的操作
                }
            })

        }


        //联系我们
        mDatabind.rlMyClickContact.clickNoRepeat {
            if (CacheUtil.isLogin()) {
                startNewActivity<ContactUsActivity>()
            } else {
                startNewActivity<LoginActivity>()
            }
        }
        //邀请好友
        mDatabind.rlMyClickInvite.clickNoRepeat {
            shareText(requireContext(), ApiComService.SHARE_IP + "#/home")

//          requireContext().copyToClipboard(ApiComService.SHARE_IP)
//            myToast(resources.getString(R.string.my_txt_copy_link))
//
        }
        //广告
        mDatabind.ivMyAdvertising.clickNoRepeat {
            if(mViewModel.advertisement.value!=null){
//                startNewActivity<WebActivity>() {
//                    this.putExtra(Constants.WEB_URL, mViewModel.advertisement.value!!.targetUrl)
//                    this.putExtra(Constants.CHAT_TITLE, getString(R.string.my_app_name))
//                }

                jumpOutUrl(mViewModel.advertisement.value!!.targetUrl)
            }

        }


        //更新用户信息
        appViewModel.userInfo.observe(this) {
            if (isAdded) {
                setData()
                mViewModel.getIndividualCenter()
            }


        }
        if (isAdded) {

            mViewModel.getIndividualCenter()
        }


        //登录或者登出
        appViewModel.updateLoginEvent.observe(this) {
            if (!it) {
                notLogin()
            }
        }


    }


    /**
     * 设置参数
     */
    fun setData() {
//        mDatabind.myAnimationSubscribe.imageAssetsFolder="subscribe/images"
//        mDatabind.myAnimationSubscribe.setAnimation("subscribe/subscribe_animation")
//        mDatabind.myAnimationSubscribe.progress=0f
//        try {
//            // 从 assets 文件夹中加载图片资源
//            val inputStream: InputStream =requireContext().assets.open("subscribe/images/img_0.png")
//
//            val drawable: Drawable = Drawable.createFromStream(inputStream, null)!!
//            mDatabind.myAnimationSubscribe.setImageDrawable(drawable)
//        } catch (e: IOException) {
//            mDatabind.myAnimationSubscribe.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.my_wd_dy_icon))
//        }


        if (CacheUtil.isLogin()) {
            if (CacheUtil.getUser() != null) {
                var user = CacheUtil.getUser()
                mDatabind.iiIsShowLeve.visibility = View.VISIBLE
                Glide.with(requireContext())
                    .load(user!!.head) // 替换为您要加载的图片 URL
                    .error(R.drawable.icon_login_my_head)
                    .placeholder(R.drawable.icon_login_my_head)
                    .circleCrop()
                    .into(mDatabind.ivMyHead)
                mDatabind.txtMyName.text = user!!.name
                mDatabind.txtMyNum.text = "${user!!.lvName}"


                if (user!!.lvNum.equals("1")) {
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_yi))

                    mDatabind.ivIvLevel.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_user_level1
                        )
                    )

                } else if (user!!.lvNum.equals("2")) {
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_er))

                    mDatabind.ivIvLevel.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_user_level2
                        )
                    )

                } else if (user!!.lvNum.equals("3")) {
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_san))

                    mDatabind.ivIvLevel.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_user_level3
                        )
                    )
                } else if (user!!.lvNum.equals("4")) {
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_si))

                    mDatabind.ivIvLevel.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_user_level4
                        )
                    )
                } else if (user!!.lvNum.equals("5")) {
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_wu))

                    mDatabind.ivIvLevel.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_user_level5
                        )
                    )
                } else if (user!!.lvNum.equals("6")) {
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_liu))

                    mDatabind.ivIvLevel.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_user_level6
                        )
                    )
                } else if (user!!.lvNum.equals("7")) {
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_qi))

                    mDatabind.ivIvLevel.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_user_level7
                        )
                    )
                } else if (user!!.lvNum.equals("8")) {
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_ba))

                    mDatabind.ivIvLevel.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_user_level8
                        )
                    )
                } else {
//                    mDatabind.ivMyLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.level_yi))

                    mDatabind.ivIvLevel.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_user_level1
                        )
                    )
                }

            } else {

                notLogin()
            }


        } else {
            notLogin()
        }


    }

    /**
     * 没有登录
     */
    fun notLogin() {
        Glide.with(requireContext())
            .load(R.drawable.icon_my_head) // 替换为您要加载的图片 URL
            .error(R.drawable.icon_my_head)
            .placeholder(R.drawable.icon_my_head)
            .circleCrop()
            .into(mDatabind.ivMyHead)
        mDatabind.txtMyName.text = resources.getString(R.string.my_txt_click_login)
        mDatabind.iiIsShowLeve.visibility = View.GONE

    }

    override fun onResume() {
        super.onResume()
    }

    override fun createObserver() {
        super.createObserver()
        //获取个人中心广告
        mViewModel.advertisement.observe(this) {
//            mDatabind.ivMyAdvertising.visibility = View.VISIBLE
            Glide.with(requireContext())
                .load(it.imgUrl) // 替换为您要加载的图片 URL
                .error(R.drawable.banner_my_icon)
                .placeholder(R.drawable.banner_my_icon)
                .into(mDatabind.ivMyAdvertising)

        }
        //获取广告失败
        mViewModel.advertisementErr.observe(this) {
//            mDatabind.ivMyAdvertising.visibility = View.GONE
        }
        //退出登录
        mViewModel.exitLive.observe(this) {
            if (it) {
                CacheUtil.setIsLogin(false, LoginInfo("", "", ""))
            }

        }


    }

    /**
     * 复制成功的弹出框
     */
    @SuppressLint("SuspiciousIndentation")
    fun showCopyLink() {
        var dialogX =
            CustomDialog.show(object : OnBindView<CustomDialog>(R.layout.item_copy_succeed) {
                override fun onBind(dialog: CustomDialog?, v: View?) {
                }

            })
        Handler(Looper.getMainLooper()).postDelayed(
            Runnable {
                dialogX.dismiss()
            }, 2000
        )

    }

    /**
     * 弹出框是否退出
     */
    fun isLoginOut() {
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
    private fun jumpOutUrl(url: String) {
        if (url.contains("http")) {
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val contentUrl: Uri = Uri.parse(url)
            intent.data = contentUrl
            startActivity(intent)
        }
    }

}