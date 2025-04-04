package com.walisport.app.ui


import android.content.Intent
import android.os.Bundle
import com.walisport.app.BuildConfig.token
import com.walisport.app.BuildConfig.uid
import com.walisport.app.data.MainViewModel
import com.walisport.app.databinding.ActivityMainBinding
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseActivity
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.base.utils.LogUtilsExt.logd
import com.walisport.module.login.databinding.FragmentLoginSecondBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val vbClass: KClass<ActivityMainBinding> = ActivityMainBinding::class
    override val vmClass: KClass<MainViewModel> = MainViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        "uid:$uid, token:$token".logd(TAG)
        mBinding.root.setOnClickListener {
            startActivity(Intent(this, AppNavActivity::class.java))
        }
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("walisport://login_activity?userId=123"))
        //startActivity(intent)
//        startActivity(Intent(this, HomeActivity::class.java))
        startActivity(Intent(this, AppNavActivity::class.java))
        finish()
    }

    override fun initListener() {
    }

    override fun createObserver() {

    }

    companion object {
        val TAG = "MainActivity"
    }
}