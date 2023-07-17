package com.xcjh.base_lib.base.container

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.base_lib.R
import com.xcjh.base_lib.base.activity.BaseActivity

/**
 *
 * @Description Fragment容器
 */
class ContainerFm2Activity : BaseActivity() {

    companion object {
        private lateinit var fragment : Fragment
        private var bundle: Bundle? = null
    }
    fun startContainer2Activity(
        context: Context,
        frag: Fragment,
        mBundle: Bundle? = null,
        reqCode: Int? = null
    ) {
        fragment=frag
        bundle=mBundle
        val intent = Intent(context, ContainerFm2Activity::class.java)
        if (reqCode == null){
            context.startActivity(intent)
        }else{
            (context as Activity).startActivityForResult(intent, reqCode)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_container)
        ImmersionBar.with(this)
            .statusBarColor(R.color.white)
            .autoDarkModeEnable(true)
            .fitsSystemWindows(true)
            .init()

       // Log.e("TAG", "onCreate: ===="+fragment.toString() )
        fragment.arguments = bundle
        this.supportFragmentManager.beginTransaction().let { frg ->
            frg.add(R.id.fl_container, fragment)
            frg.commitAllowingStateLoss()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
       // dismissLoadingExt()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fragment.onActivityResult(requestCode, resultCode, data)
    }
}