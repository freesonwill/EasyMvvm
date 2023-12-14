package com.xcjh.app.component;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.engagelab.privates.common.component.MTCommonReceiver;

import java.util.Arrays;
import android.util.Log;
import com.engagelab.privates.core.api.MTCorePrivatesApi;
import com.engagelab.privates.core.api.WakeMessage;
import com.engagelab.privates.push.api.AliasMessage;
import com.engagelab.privates.push.api.CustomMessage;
import com.engagelab.privates.push.api.MobileNumberMessage;
import com.engagelab.privates.push.api.NotificationMessage;
import com.engagelab.privates.push.api.PlatformTokenMessage;
import com.engagelab.privates.push.api.TagMessage;
import com.xcjh.app.component.listener.StatusObserver;

/**
 * 开发者继承MTCommonReceiver，可以获得sdk的方法回调
 * <p>
 * 所有回调均在主线程
 */
public class UserReceiver extends MTCommonReceiver {

    private static final String TAG = "push===UserReceiver";

    /**
     * 应用通知开关状态回调
     *The Crashlytics build ID is missing. This occurs when Crashlytics tooling is absent from your app's build configuration. Please review Crashlytics onboarding instructions and ensure you have a valid Crashlytics account.
     * @param context 不为空
     * @param enable  通知开关是否开，true为打开，false为关闭
     */
    @Override
    public void onNotificationStatus(Context context, boolean enable) {
        Log.i(TAG, "onNotificationStatus:" + enable);
        Toast.makeText(context.getApplicationContext(), "onNotificationStatus " + enable, Toast.LENGTH_SHORT).show();
        ExampleGlobal.isNotificationEnable = enable;
        if (StatusObserver.getInstance().getListener() != null) {
            StatusObserver.getInstance().getListener().onNotificationStatus(enable);
        }
    }

    /**
     * 长连接状态回调
     *
     * @param context 不为空
     * @param enable  是否连接
     */
    @Override
    public void onConnectStatus(Context context, boolean enable) {
        Log.i(TAG, "onConnectState:" + enable);
        Toast.makeText(context.getApplicationContext(), "onConnectStatus " + enable, Toast.LENGTH_SHORT).show();
        ExampleGlobal.isConnectEnable = enable;
        if (StatusObserver.getInstance().getListener() != null) {
            StatusObserver.getInstance().getListener().onConnectStatus(enable);
        }
        // 长连接成功可获取registrationId
        if (enable) {
            String registrationId = MTCorePrivatesApi.getRegistrationId(context);
            Log.i(TAG, "registrationId:" + registrationId);
        }
    }

    /**
     * 通知消息到达回调
     *
     * @param context             不为空
     * @param notificationMessage 通知消息
     */
    @Override
    public void onNotificationArrived(Context context, NotificationMessage notificationMessage) {
        Log.i(TAG, "onNotificationArrived:" + notificationMessage.toString());
    }

    /**
     * 通知消息在前台不显示
     *
     * @param context             不为空
     * @param notificationMessage 通知消息
     */
    @Override
    public void onNotificationUnShow(Context context, NotificationMessage notificationMessage) {
        Log.i(TAG, "onNotificationUnShow:" + notificationMessage.toString());
    }

    /**
     * 通知消息点击回调
     *
     * @param context             不为空
     * @param notificationMessage 通知消息
     */
    @Override
    public void onNotificationClicked(Context context, NotificationMessage notificationMessage) {
        Log.i(TAG, "onNotificationClicked:" + notificationMessage.toString());
    }

    /**
     * 通知消息删除回调
     *
     * @param context             不为空
     * @param notificationMessage 通知消息
     */
    @Override
    public void onNotificationDeleted(Context context, NotificationMessage notificationMessage) {
        Log.i(TAG, "onNotificationDeleted:" + notificationMessage.toString());
    }

    /**
     * 自定义消息回调
     *
     * @param context       不为空
     * @param customMessage 自定义消息
     */
    @Override
    public void onCustomMessage(Context context, CustomMessage customMessage) {
        Log.i(TAG, "onCustomMessage:" + customMessage.toString());
        // 用于演示自定义消息展示
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, CustomMessageActivity.class);
        intent.putExtra("message", customMessage);
        context.startActivity(intent);
    }

    /**
     * 标签消息回调
     *
     * @param context    不为空
     * @param tagMessage 标签消息
     */
    @Override
    public void onTagMessage(Context context, TagMessage tagMessage) {
        Log.i(TAG, "onTagMessage:" + tagMessage.toString());
        String message;
        switch (tagMessage.getSequence()) {
            case ExampleGlobal.TAG_ADD:
                message = "addTag code:" + tagMessage.getCode() + "\ntag:" + Arrays.toString(tagMessage.getTags());
                break;
            case ExampleGlobal.TAG_DELETE:
                message = "deleteTag code:" + tagMessage.getCode() + "\ntag:" + Arrays.toString(tagMessage.getTags());
                break;
            case ExampleGlobal.TAG_UPDATE:
                message = "updateTag code:" + tagMessage.getCode() + "\ntag:" + Arrays.toString(tagMessage.getTags());
                break;
            case ExampleGlobal.TAG_QUERY:
                message = "queryTag code:" + tagMessage.getCode() + "\ntag:" + tagMessage.getQueryTag() + ",valid:" + tagMessage.isQueryTagValid();
                break;
            case ExampleGlobal.TAG_DELETE_ALL:
                message = "deleteAllTag code:" + tagMessage.getCode();
                break;
            case ExampleGlobal.TAG_QUERY_ALL:
                message = "queryAllTag code:" + tagMessage.getCode() + "\ntag:" + Arrays.toString(tagMessage.getTags());
                break;
            default:
                message = "tagMessage:" + tagMessage.toString();
                break;
        }
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 别名消息回调
     *
     * @param context      不为空
     * @param aliasMessage 别名消息
     */
    @Override
    public void onAliasMessage(Context context, AliasMessage aliasMessage) {
        Log.i(TAG, "onAliasMessage:" + aliasMessage.toString());
        String message;
        switch (aliasMessage.getSequence()) {
            case ExampleGlobal.ALIAS_SET:
                message = "setAlias code:" + aliasMessage.getCode() + ",alias:" + aliasMessage.getAlias();
                break;
            case ExampleGlobal.ALIAS_GET:
                message = "getAlias code:" + aliasMessage.getCode() + ",alias:" + aliasMessage.getAlias();
                break;
            case ExampleGlobal.ALIAS_CLEAR:
                message = "clearAlias code:" + aliasMessage.getCode() + ",alias:" + aliasMessage.getAlias();
                break;
            default:
                message = "aliasMessage:" + aliasMessage.toString();
                break;
        }
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 厂商token消息回调
     *
     * @param context              不为空
     * @param platformTokenMessage 厂商token消息
     */
    @Override
    public void onPlatformToken(Context context, PlatformTokenMessage platformTokenMessage) {
        Log.i(TAG, "onPlatformToken:" + platformTokenMessage.toString());
    }

    /**
     * 移动号码消息回调
     *
     * @param context             不为空
     * @param mobileNumberMessage 移动号码消息
     */
    @Override
    public void onMobileNumber(Context context, MobileNumberMessage mobileNumberMessage) {
        Log.i(TAG, "onMobileNumber:" + mobileNumberMessage.toString());
    }

    /**
     * 被拉起回调
     *
     * @param context     不为空
     * @param wakeMessage 被拉起消息
     */
    @Override
    public void onWake(Context context, WakeMessage wakeMessage) {
        Log.i(TAG, "onWake:" + wakeMessage.toString());
    }

}
