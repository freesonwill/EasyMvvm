package com.xcjh.app.ui.home.my.personal

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
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
import com.xcjh.base_lib.utils.view.clickNoRepeat
import java.io.File

/**
 * 我的个人资料
 */
class PersonalDataActivity : BaseActivity<PersonalDataVm, ActivityPersonalDataBinding>() {
    var user: UserInfo?=null
    private val mainVm: MainVm by viewModels()
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .titleBar(mDatabind.titleTop.root)
            .init()
        mDatabind.titleTop.tvTitle.text=resources.getString(R.string.personal_txt_title)
          user= CacheUtil.getUser()
        if(user!=null){
            Glide.with(this)
                .load(user!!.head) // 替换为您要加载的图片 URL
                .error(R.drawable.icon_avatar)
                .placeholder(R.drawable.icon_avatar)
                .into(mDatabind.ivPersonalHead)
            if(user!!.name!=null&&!user!!.name.equals("")){
                mDatabind.txtPersonalNickname.text=user!!.name
            }

        }


        //选择头像
        mDatabind.llPersonalClickHead.clickNoRepeat{
            com.luck.picture.lib.R.anim.ps_anim_enter
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
                                .placeholder(R.drawable.icon_avatar)
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
        mDatabind.rlPersonalClickName.clickNoRepeat {
            startNewActivity<EditProfileActivity> { }

        }

        //更新用户信息
        appViewModel.userInfo.observe(this){
            user= CacheUtil.getUser()
            if(user!=null){
                if(user!!.name!=null&&!user!!.name.equals("")){
                    Glide.with(this)
                        .load(user!!.head) // 替换为您要加载的图片 URL
                        .error(R.drawable.icon_avatar)
                        .placeholder(R.drawable.icon_avatar)
                        .into(mDatabind.ivPersonalHead)

                    mDatabind.txtPersonalNickname.text=user!!.name
                }

            }
        }

    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.update.observe(this){
            mainVm.getUserInfo()
        }
    }


}