package com.xcjh.app.ui.home.my.personal

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.UserInfo
import com.xcjh.app.databinding.ActivityPersonalDataBinding
import com.xcjh.app.databinding.FragmentMyUserBinding
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.utils.GlideEngine
import com.xcjh.app.utils.picture.ImageFileCompressEngine
import com.xcjh.app.utils.picture.ImageFileCropEngine
import com.xcjh.app.vm.MainVm
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.bindadapter.CustomBindAdapter.afterTextChanged
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.view.clickNoRepeat
import java.io.File

/**
 * 我的个人资料
 */
class PersonalDataActivity : BaseActivity<PersonalDataVm, ActivityPersonalDataBinding>() {
    var user: UserInfo?=null
    private val mainVm: MainVm by viewModels()
    //是否可以修改
    private var isSave:Boolean=false
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .titleBar(mDatabind.rltTop)
            .navigationBarDarkIcon(true)
            .navigationBarColor(R.color.c_ffffff)
            .init()
        mDatabind.tvTitle.text=resources.getString(R.string.personal_update_title)
        mDatabind.ivBack.clickNoRepeat {
            finish()
        }

          user= CacheUtil.getUser()


        if(user!=null){
            Glide.with(this)
                .load(user!!.head) // 替换为您要加载的图片 URL
                .error(R.drawable.icon_login_my_head)
                .placeholder(R.drawable.icon_login_my_head)
                .into(mDatabind.ivPersonalHead)
            if(user!!.name!=null&&!user!!.name.equals("")){
                mDatabind.txtPersonalNickname.setText(user!!.name)
            }

        }
//        dataIsSave()

        mDatabind.tvOption.clickNoRepeat {

            if(isSave){
                user!!.name=mDatabind.txtPersonalNickname.text.toString().trim()
                mViewModel.setUserInfo( mDatabind.txtPersonalNickname.text.toString().trim(),user!!.head)
            }

        }


        //选择头像
        mDatabind.ivPersonalHead.clickNoRepeat{

            PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setCompressEngine(ImageFileCompressEngine())
                .setSelectionMode(SelectModeConfig.SINGLE)
                    //剪裁
                .setCropEngine(ImageFileCropEngine(this))
                .forResult(object :OnResultCallbackListener<LocalMedia>{
                    override fun onResult(result: ArrayList<LocalMedia>?) {
                        if(result!=null&&result!!.size>0){
                            var path:String=""
                            if(result[0].isCut){
                                if(result[0].cutPath!=null){
                                    path = result[0].cutPath
                                }else{
                                    path = result[0].compressPath
                                }
                            }else{
                                path = result[0].compressPath
                            }
                            Glide.with(this@PersonalDataActivity)
                                .load(path)
                                .placeholder(R.drawable.icon_login_my_head)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(mDatabind.ivPersonalHead)

                            mViewModel.upLoadPic(File(path))
                        }

//                        if (PictureMimeType.isContent(path) && !result[0].isCut && !result[0].isCompressed) Uri.parse(path)
//                        else
//                            path


                    }

                    override fun onCancel() {

                    }

                })
        }
        //编辑名称
//        mDatabind.rlPersonalClickName.clickNoRepeat {
//            startNewActivity<EditProfileActivity> { }
//
//        }

        //监听输入的文字
        mDatabind.txtPersonalNickname.afterTextChanged{
            dataIsSave()
            user!!.name=it
            if(it.isNotEmpty()){
                if(it.length>8){
//                    mDatabind.llDataShowErr.visibility=View.VISIBLE
                }else{

//                    mDatabind.llDataShowErr.visibility=View.GONE
                }
            }else{
//                mDatabind.llDataShowErr.visibility=View.VISIBLE
            }

        }

        //更新用户信息
        appViewModel.userInfo.observe(this){
            user= CacheUtil.getUser()
            if(user!=null){
                if(user!!.name!=null&&!user!!.name.equals("")){
                    Glide.with(this)
                        .load(user!!.head) // 替换为您要加载的图片 URL
                        .error(R.drawable.icon_my_head)
                        .placeholder(R.drawable.icon_my_head)
                        .into(mDatabind.ivPersonalHead)

                    mDatabind.txtPersonalNickname.setText(user!!.name)
                }

            }
        }

    }

    override fun createObserver() {
        super.createObserver()

        mViewModel.update.observe(this){
            dataIsSave()

            if(it.isNotEmpty()){
                user!!.head=it
                Glide.with(this)
                    .load(it) // 替换为您要加载的图片 URL
                    .error(R.drawable.icon_my_head)
                    .placeholder(R.drawable.icon_my_head)
                    .into(mDatabind.ivPersonalHead)
            }else{
                Glide.with(this)
                    .load(user!!.head) // 替换为您要加载的图片 URL
                    .error(R.drawable.icon_my_head)
                    .placeholder(R.drawable.icon_my_head)
                    .into(mDatabind.ivPersonalHead)
            }

//            mainVm.getUserInfo()
        }
        mViewModel.updateData.observe(this){
            myToast(getString(R.string.personal_txt_saved))
            mainVm.getUserInfo()
            mDatabind.tvOption.postDelayed ({
                finish()
            }, 500)

        }


    }

    fun dataIsSave(){
        if(mDatabind.txtPersonalNickname.text.toString().trim().isNotEmpty()&&
            mDatabind.txtPersonalNickname.text.toString().trim().length<=8){
            isSave=true
            mDatabind.tvOption.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.tvOption.background=ContextCompat.getDrawable(this,R.drawable.shape_r28_34a853)
        }else{
            isSave=false
            mDatabind.tvOption.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))
            mDatabind.tvOption.background=ContextCompat.getDrawable(this,R.drawable.shape_r28_f2f3f7)
        }
    }
}