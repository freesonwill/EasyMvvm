package com.xcjh.base_lib.base.container

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.base_lib.R
import com.xcjh.base_lib.base.activity.BaseActivity
import com.xcjh.base_lib.route.RouteCenter

/**
 *
 * @Description Fragment容器 根据路由地址加载根Fragment
 */
class ContainerFmActivity : BaseActivity() {

    companion object {
        const val FRAGMENT = "fragment"
        const val BUNDLE = "bundle"
    }
    private lateinit var fragment:Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_container)
        ImmersionBar.with(this)
            .statusBarColor(R.color.white)
            .autoDarkModeEnable(true)
            .fitsSystemWindows(true)
            .init()
        val fragmentPath: String? = intent.getStringExtra(FRAGMENT)
        if (fragmentPath.isNullOrBlank()) {
            return
        }
        val args: Bundle? = intent.getBundleExtra(BUNDLE)
        fragment = RouteCenter.navigate(fragmentPath,args) as Fragment
        this.supportFragmentManager.beginTransaction().let { frg ->
            frg.add(R.id.fl_container, fragment)
            frg.commitAllowingStateLoss()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        //dismissLoadingExt()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fragment.onActivityResult(requestCode, resultCode, data)
    }
}