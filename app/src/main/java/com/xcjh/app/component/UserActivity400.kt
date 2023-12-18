package com.xcjh.app.component

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.xcjh.app.R

/**
 * 用于演示MTPush4.0.0开始-点击通知后activity跳转
 *
 *
 * 确保没有调用[MTPushPrivatesApi.configOldPushVersion]，否则通知点击跳转不会跳转到此页面
 *
 *
 * 不需要调用[MTPushPrivatesApi.reportNotificationOpened]，sdk内部已做处理
 */
class UserActivity400 : Activity() {
    private var tvMessage: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intent)
        tvMessage = findViewById(R.id.tv_message)
        onIntent(intent)
        Log.d(TAG, "onCreate")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        onIntent(intent)
        Log.d(TAG, "onNewIntent")
    }

    private fun onIntent(intent: Intent?) {
        try {
            //Toast.makeText(applicationContext, TAG, Toast.LENGTH_SHORT).show()
            if (intent == null) {
                return
            }
            val notificationMessage = intent.getStringExtra("message_json") ?: return
            Log.d(TAG, "notificationMessage:$notificationMessage")
            tvMessage!!.text = notificationMessage
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "push===UserActivity400"
    }
}