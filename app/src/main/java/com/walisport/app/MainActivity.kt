package com.walisport.app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.walisport.app.databinding.ActivityMainBinding
import com.walisport.lib_base.data.viewmodel.EmptyViewModel
import com.walisport.lib_base.ui.BaseActivity
import com.walisport.lib_base.ui.viewBind
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<EmptyViewModel, ActivityMainBinding>() {
    override val mBinding: ActivityMainBinding by viewBind()
    override val mViewModel: EmptyViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

    override fun lazyLoadData() {
    }
}