package com.xcjh.app.ui.home.my.personal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.databinding.ActivityEditProfileBinding
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.vm.MainVm
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.utils.bindadapter.CustomBindAdapter.afterTextChanged
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 编辑昵称
 */
class EditProfileActivity : BaseActivity<EditProfileVm, ActivityEditProfileBinding>() {
    private val mainVm: MainVm by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .titleBar(mDatabind.titleTop.root)
            .init()


       var  user= CacheUtil.getUser()
        if(user!=null){
            if(user!!.name!=null&&!user!!.name.equals("")){
                mDatabind.editName.setText(user!!.name)
                mDatabind.ivEditClear.visibility=View.VISIBLE
                mDatabind.titleTop.tvOption.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            }else{
                mDatabind.titleTop.tvOption.setTextColor(ContextCompat.getColor(this,R.color.c_5b6271))
            }

        }else{
            mDatabind.titleTop.tvOption.setTextColor(ContextCompat.getColor(this,R.color.c_5b6271))
        }


        mDatabind.titleTop.tvTitle.text=resources.getString(R.string.personal_txt_title)
        mDatabind.titleTop.ivBack.visibility= View.GONE
        mDatabind.titleTop.tvCancel.visibility=View.VISIBLE
        mDatabind.titleTop.tvCancel.clickNoRepeat {
            finish()
        }

        mDatabind.titleTop.tvOption.visibility=View.VISIBLE
        mDatabind.titleTop.tvOption.text=resources.getString(R.string.finish)

        //监听输入的文字
        mDatabind.editName.afterTextChanged{
            if(it.isNotEmpty()){
                mDatabind.titleTop.tvOption.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
                mDatabind.ivEditClear.visibility=View.VISIBLE
            }else{
                mDatabind.titleTop.tvOption.setTextColor(ContextCompat.getColor(this,R.color.c_5b6271))
                mDatabind.ivEditClear.visibility=View.GONE
            }

        }
        //点击完成
        mDatabind.titleTop.tvOption.clickNoRepeat{
                //有文字的时候才能完成
                if( mDatabind.editName.text.toString().trim().isNotEmpty()){
                        mViewModel.setUserInfo(name = mDatabind.editName.text.toString().trim(),head = null)
                }
        }
        mDatabind.ivEditClear.clickNoRepeat {
            mDatabind.editName.setText("")
        }

    }


    override fun createObserver() {
        super.createObserver()

        mViewModel.update.observe(this){
            if(it){
                mainVm.getUserInfo()
                finish()
            }
        }
    }
}