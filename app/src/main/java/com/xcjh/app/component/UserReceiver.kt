package com.xcjh.app.component

import android.content.Context
import android.content.Intent
import android.util.Log
import com.engagelab.privates.common.component.MTCommonReceiver
import com.engagelab.privates.core.api.MTCorePrivatesApi
import com.engagelab.privates.core.api.WakeMessage
import com.engagelab.privates.push.api.AliasMessage
import com.engagelab.privates.push.api.CustomMessage
import com.engagelab.privates.push.api.MobileNumberMessage
import com.engagelab.privates.push.api.NotificationMessage
import com.engagelab.privates.push.api.PlatformTokenMessage
import com.engagelab.privates.push.api.TagMessage
import com.xcjh.app.component.listener.StatusObserver
import com.xcjh.app.ui.MainActivity
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.base_lib.utils.startNewActivity
import java.util.Arrays

/**
 * 开发者继承MTCommonReceiver，可以获得sdk的方法回调
 *
 *
 * 所有回调均在主线程
 */
class UserReceiver : MTCommonReceiver() {
    /**
     * 应用通知开关状态回调
     * The Crashlytics build ID is missing. This occurs when Crashlytics tooling is absent from your app's build configuration. Please review Crashlytics onboarding instructions and ensure you have a valid Crashlytics account.
     * @param context 不为空
     * @param enable  通知开关是否开，true为打开，false为关闭
     */
    override fun onNotificationStatus(context: Context, enable: Boolean) {
        Log.i(TAG, "onNotificationStatus:$enable")
        //Toast.makeText(context.getApplicationContext(), "onNotificationStatus " + enable, Toast.LENGTH_SHORT).show();
        ExampleGlobal.isNotificationEnable = enable
        if (StatusObserver.getInstance().listener != null) {
            StatusObserver.getInstance().listener.onNotificationStatus(enable)
        }
    }

    /**
     * 长连接状态回调
     *
     * @param context 不为空
     * @param enable  是否连接
     */
    override fun onConnectStatus(context: Context, enable: Boolean) {
        Log.i(TAG, "onConnectState:$enable")
        //Toast.makeText(context.getApplicationContext(), "onConnectStatus " + enable, Toast.LENGTH_SHORT).show();
        ExampleGlobal.isConnectEnable = enable
        if (StatusObserver.getInstance().listener != null) {
            StatusObserver.getInstance().listener.onConnectStatus(enable)
        }
        // 长连接成功可获取registrationId
        if (enable) {
            val registrationId = MTCorePrivatesApi.getRegistrationId(context)
            Log.i(TAG, "registrationId:$registrationId")
        }
    }

    /**
     * 通知消息到达回调
     *
     * @param context             不为空
     * @param notificationMessage 通知消息
     */
    override fun onNotificationArrived(context: Context, notificationMessage: NotificationMessage) {
        Log.i(TAG, "onNotificationArrived:$notificationMessage")
    }

    /**
     * 通知消息在前台不显示
     *
     * @param context             不为空
     * @param notificationMessage 通知消息
     */
    override fun onNotificationUnShow(context: Context, notificationMessage: NotificationMessage) {
        Log.i(TAG, "onNotificationUnShow:$notificationMessage")
    }

    /**
     * 通知消息点击回调
     *
     * @param context             不为空
     * @param notificationMessage 通知消息
     */
    override fun onNotificationClicked(context: Context, notificationMessage: NotificationMessage) {
        Log.i(TAG, "onNotificationClicked:$notificationMessage")
        /*notificationMessage.extras.apply {
            val matchId = getString("matchId")
            val isPureFlow = getString("isPureFlow")
            val matchType = getString("matchType")
            val liveId = getString("liveId")
            val anchorId = getString("anchorId")
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.setClass(context, MatchDetailActivity::class.java)
            intent.apply {
                putExtra("matchType", matchType)
                putExtra("matchId", matchId)
                putExtra("matchName", "")
                putExtra("anchorId", anchorId)
                putExtra("videoUrl", "")
            }
            context.startActivity(intent)

        }*/

        startNewActivity<MainActivity> {
            putExtras(notificationMessage.extras)
           // putExtra("","")
        }
    }

    /**
     * 通知消息删除回调
     *
     * @param context             不为空
     * @param notificationMessage 通知消息
     */
    override fun onNotificationDeleted(context: Context, notificationMessage: NotificationMessage) {
        Log.i(TAG, "onNotificationDeleted:$notificationMessage")
    }

    /**
     * 自定义消息回调
     *
     * @param context       不为空
     * @param customMessage 自定义消息
     */
    override fun onCustomMessage(context: Context, customMessage: CustomMessage) {
        Log.i(TAG, "onCustomMessage:$customMessage")
        // 用于演示自定义消息展示
           val intent = Intent()
           intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
           intent.setClass(context, CustomMessageActivity::class.java)
           intent.putExtra("message", customMessage)
           context.startActivity(intent)
    }

    /**
     * 标签消息回调
     *
     * @param context    不为空
     * @param tagMessage 标签消息
     */
    override fun onTagMessage(context: Context, tagMessage: TagMessage) {
        Log.i(TAG, "onTagMessage:$tagMessage")
        val message: String = when (tagMessage.sequence) {
            ExampleGlobal.TAG_ADD -> """
     addTag code:${tagMessage.code}
     tag:
     """.trimIndent() + Arrays.toString(tagMessage.tags)

            ExampleGlobal.TAG_DELETE -> """
     deleteTag code:${tagMessage.code}
     tag:
     """.trimIndent() + Arrays.toString(
                tagMessage.tags
            )

            ExampleGlobal.TAG_UPDATE -> """
     updateTag code:${tagMessage.code}
     tag:
     """.trimIndent() + Arrays.toString(
                tagMessage.tags
            )

            ExampleGlobal.TAG_QUERY -> """
                queryTag code:${tagMessage.code}
                tag:${tagMessage.queryTag},valid:${tagMessage.isQueryTagValid}
                """.trimIndent()

            ExampleGlobal.TAG_DELETE_ALL -> "deleteAllTag code:" + tagMessage.code
            ExampleGlobal.TAG_QUERY_ALL -> """
     queryAllTag code:${tagMessage.code}
     tag:
     """.trimIndent() + Arrays.toString(
                tagMessage.tags
            )

            else -> "tagMessage:$tagMessage"
        }
        //Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 别名消息回调
     *
     * @param context      不为空
     * @param aliasMessage 别名消息
     */
    override fun onAliasMessage(context: Context, aliasMessage: AliasMessage) {
        Log.i(TAG, "onAliasMessage:$aliasMessage")
        val message: String = when (aliasMessage.sequence) {
            ExampleGlobal.ALIAS_SET -> "setAlias code:" + aliasMessage.code + ",alias:" + aliasMessage.alias
            ExampleGlobal.ALIAS_GET -> "getAlias code:" + aliasMessage.code + ",alias:" + aliasMessage.alias
            ExampleGlobal.ALIAS_CLEAR -> "clearAlias code:" + aliasMessage.code + ",alias:" + aliasMessage.alias
            else -> "aliasMessage:$aliasMessage"
        }
        // Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 厂商token消息回调
     *
     * @param context              不为空
     * @param platformTokenMessage 厂商token消息
     */
    override fun onPlatformToken(context: Context, platformTokenMessage: PlatformTokenMessage) {
        Log.i(TAG, "onPlatformToken:$platformTokenMessage")
    }

    /**
     * 移动号码消息回调
     *
     * @param context             不为空
     * @param mobileNumberMessage 移动号码消息
     */
    override fun onMobileNumber(context: Context, mobileNumberMessage: MobileNumberMessage) {
        Log.i(TAG, "onMobileNumber:$mobileNumberMessage")
    }

    /**
     * 被拉起回调
     *
     * @param context     不为空
     * @param wakeMessage 被拉起消息
     */
    override fun onWake(context: Context, wakeMessage: WakeMessage) {
        Log.i(TAG, "onWake:$wakeMessage")
    }

    companion object {
        private const val TAG = "push===UserReceiver"
    }
}