package com.xcjh.app.ui.chat

import android.app.ActivityOptions
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.view.KeyEvent
import com.bumptech.glide.Glide
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.databinding.ActivityHomeBinding
import com.xcjh.app.databinding.ActivityImageViewBinding
import com.xcjh.app.utils.loadImageWithGlideMax
import com.xcjh.app.vm.MainVm
import com.xcjh.base_lib.utils.startNewActivity
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 查看图片
 */
class ImageViewActivity : BaseActivity<MainVm, ActivityImageViewBinding>() {
    var mImageUrl:String=""
    companion object {
        fun open(
            imageUrl: String? = null,

        ) {

            startNewActivity<ImageViewActivity> {
                putExtra("imageUrl", imageUrl)
            }

        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        intent.extras?.apply {
            mImageUrl = getString("imageUrl", null)
        }
        //收到通知其他地方登录
        appViewModel.quitLoginEvent.observe(this){
            finish()
        }

        mDatabind.ivViewShow.loadImageWithGlideMax(this,mImageUrl)
        mDatabind.ivViewShow.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.anim_fade_in, android.R.anim.fade_out)

        }
//        mDatabind.scConten.setOnClickListener {
//            finish()
//            overridePendingTransition(R.anim.anim_fade_in, android.R.anim.fade_out)
//        }
        mDatabind.llLayout.setOnClickListener {
            finish()
//            overridePendingTransition(0, android.R.anim.fade_out)
            overridePendingTransition(R.anim.anim_fade_in, android.R.anim.fade_out)
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // 判断是否按下了返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 在这里执行你想要的操作，比如关闭当前活动
            finish();
            overridePendingTransition(R.anim.anim_fade_in, android.R.anim.fade_out)
            return true; // 返回 true 表示事件已经处理，不会继续传递
        }

        return super.onKeyDown(keyCode, event)
    }

}