package com.xcjh.app.ui.login

import android.os.Bundle
import android.view.View
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.PostLoaginBean
import com.xcjh.app.databinding.ActivityLoaginBinding
import com.xcjh.app.ui.Index.IndexLetterActivity
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.setOnclickNoRepeat

/***
 * 登录
 */

class LoginActivity : BaseActivity<LoginVm, ActivityLoaginBinding>() {

    var type=1//1是手机号登录，2是邮箱登录

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .titleBar(mDatabind.ivBack)
            .init()

        setOnclickNoRepeat(mDatabind.tvphone,mDatabind.tvemail,mDatabind.tvlogin,mDatabind.tvgetcode,mDatabind.tvgo){
            when(it.id){
                R.id.tvphone->{
                    type=1
                    mDatabind.tvphone.setBackgroundResource(R.drawable.shape_r8_6d48fe)
                    mDatabind.tvemail.setBackgroundResource(R.drawable.shape_r8_1a8a91a0)
                    mDatabind.linphone.visibility= View.VISIBLE
                    mDatabind.edtemail.visibility= View.GONE
                }
                R.id.tvemail->{
                    type=2
                    mDatabind.tvemail.setBackgroundResource(R.drawable.shape_r8_6d48fe)
                    mDatabind.tvphone.setBackgroundResource(R.drawable.shape_r8_1a8a91a0)
                    mDatabind.linphone.visibility= View.GONE
                    mDatabind.edtemail.visibility= View.VISIBLE
                }
                R.id.tvlogin->{

                    when(type){//1是手机号登录，2是邮箱登录
                        1->{
                            if (mDatabind.edtphone.text.toString().isEmpty()){

                                myToast(resources.getString(R.string.please_input_phone_num))
                                return@setOnclickNoRepeat
                            }
                            if (mDatabind.edtcode.text.toString().isEmpty()){
                                myToast(resources.getString(R.string.please_input_phone_code))
                                return@setOnclickNoRepeat
                            }
                            mViewModel.getLogin(PostLoaginBean(mDatabind.edtphone.text.toString(),null,mDatabind.edtcode.text.toString()
                            ,null,type))
                        }
                        2->{
                            if (mDatabind.edtemail.text.toString().isEmpty()){

                                myToast(resources.getString(R.string.please_input_email))
                                return@setOnclickNoRepeat
                            }
                            if (mDatabind.edtcode.text.toString().isEmpty()){
                                myToast(resources.getString(R.string.please_input_phone_code))
                                return@setOnclickNoRepeat
                            }
                            mViewModel.getLogin(PostLoaginBean(mDatabind.edtemail.text.toString(),null,mDatabind.edtcode.text.toString()
                                ,null,type))
                        }
                    }

                }
                R.id.tvgetcode->{
                    when(type){//1是手机号登录，2是邮箱登录
                        1->{
                            if (mDatabind.edtphone.text.toString().isEmpty()){

                                myToast(resources.getString(R.string.please_input_phone_num))
                                return@setOnclickNoRepeat
                            }

//                            mViewModel.getLoagin(PostLoaginBean(mDatabind.edtphone.text.toString(),mDatabind.edtcode.text.toString()
//                                ,null,null,type))
                        }
                        2->{
                            if (mDatabind.edtemail.text.toString().isEmpty()){

                                myToast(resources.getString(R.string.please_input_email))
                                return@setOnclickNoRepeat
                            }

//                            mViewModel.getLoagin(PostLoaginBean(mDatabind.edtemail.text.toString(),mDatabind.edtcode.text.toString()
//                                ,null,null,type))
                        }
                    }
                }
                R.id.tvgo->{

                    com.xcjh.base_lib.utils.startNewActivity<IndexLetterActivity>()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        mDatabind.tvgo.text= Constants.PHONE_CODE
    }
    override fun createObserver() {

        mViewModel.logain.observe(this) {
            if (it.isNotEmpty()) {
                //成功
                finish()

            } else {

            }

        }

    }

}