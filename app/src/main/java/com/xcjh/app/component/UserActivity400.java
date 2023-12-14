package com.xcjh.app.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.engagelab.privates.push.api.MTPushPrivatesApi;
import com.xcjh.app.R;

/**
 * 用于演示MTPush4.0.0开始-点击通知后activity跳转
 * <p>
 * 确保没有调用{@link MTPushPrivatesApi#configOldPushVersion(Context)}，否则通知点击跳转不会跳转到此页面
 * <p>
 * 不需要调用{@link MTPushPrivatesApi#reportNotificationOpened(Context, String, byte, String)}，sdk内部已做处理
 */
public class UserActivity400 extends Activity {

    private static final String TAG = "push===UserActivity400";

    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent);
        tvMessage = findViewById(R.id.tv_message);
        onIntent(getIntent());
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onIntent(intent);
        Log.d(TAG, "onNewIntent");
    }

    private void onIntent(Intent intent) {
        try {
            Toast.makeText(getApplicationContext(), TAG, Toast.LENGTH_SHORT).show();
            if (intent == null) {
                return;
            }
            String notificationMessage = intent.getStringExtra("message_json");
            if (notificationMessage == null) {
                return;
            }
            Log.d(TAG, "notificationMessage:" + notificationMessage);
            tvMessage.setText(notificationMessage);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}