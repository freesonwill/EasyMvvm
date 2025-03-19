package com.walisport.app


import android.content.Intent
import android.os.Bundle
import com.example.videoplayer.VideoLandscapeActivity
import com.walisport.app.data.MainViewModel
import com.walisport.app.databinding.ActivityMainBinding
import com.walisport.lib_base.ui.BaseActivity
import com.walisport.lib_base.ui.viewBind
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val mBinding: ActivityMainBinding by viewBind()
    override val mViewModel: MainViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.goToVideo.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, VideoLandscapeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun initListener() {

    }

    override fun createObserver() {
    }

    override fun lazyLoadData() {
    }
}