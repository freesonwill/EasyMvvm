package com.xcjh.base_lib.base.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.View

/**
 * @author zobo 2023.02.15
 */
abstract class BaseActivity : AppCompatActivity() {

    /**
     * 泛型的高级特性 泛型实例化
     * 跳转
     */
    inline fun <reified T> startMyActivity(block: Intent.() -> Unit = {}) {
        val intent = Intent(this, T::class.java)
        //把intent实例 传入block 函数类型参数
        intent.block()
        startActivity(intent)
    }

    open fun finishTopClick(view: View?) {
        finish()
    }
}