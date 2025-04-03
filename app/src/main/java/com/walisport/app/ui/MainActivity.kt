package com.walisport.app.ui


import android.content.Intent
import android.os.Bundle
import com.walisport.app.BuildConfig.token
import com.walisport.app.BuildConfig.uid
import com.walisport.app.data.MainViewModel
import com.walisport.app.databinding.ActivityMainBinding
import com.walisport.lib.base.ui.BaseActivity
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.base.utils.LogUtilsExt.logd
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val mBinding: ActivityMainBinding by viewBind()
    override val mViewModel: MainViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        "uid:$uid, token:$token".logd(TAG)
        startActivity(Intent(this, AppNavActivity::class.java))
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("walisport://login_activity?userId=123"))
        //startActivity(intent)
//        startActivity(Intent(this, HomeActivity::class.java))
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