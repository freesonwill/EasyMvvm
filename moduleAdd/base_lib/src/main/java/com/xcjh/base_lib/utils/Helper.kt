package com.xcjh.base_lib.utils

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.xcjh.base_lib.App
import com.xcjh.base_lib.R
import com.xcjh.base_lib.appContext

/**
 * 任务完成提示
 */
fun myToast(whiteStr: String?, yellowStr: String? = null) {
    val view: View = LayoutInflater.from(appContext).inflate(R.layout.view_toast_my_task, null)
    val tvMsg = view.findViewById<View>(R.id.tvToast) as TextView
    SpanUtil.create()
        .addForeColorSection(whiteStr, ContextCompat.getColor(tvMsg.context, R.color.white))
        .addForeColorSection(
            yellowStr ?: "",
            ContextCompat.getColor(tvMsg.context, R.color.successColor)
        )
        .showIn(tvMsg) //显示到控件TextView中
    val toast = Toast(appContext)
    // toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, DisplayUtils.dp2px(50f))
    toast.setGravity( Gravity.CENTER, 0,0)
    toast.duration = Toast.LENGTH_LONG
    toast.view = view
    toast.show()
}

fun getXXPermissions(activity: Activity, action: () -> Unit = {}){
    XXPermissions.with(activity)
        .permission(Permission.READ_MEDIA_IMAGES)
        .permission(Permission.ACCESS_MEDIA_LOCATION)
        // .permission(Permission.READ_EXTERNAL_STORAGE)
        //  .permission(Permission.WRITE_EXTERNAL_STORAGE)
        .request(object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                if (all) {
                    action.invoke()
                }else{
                    XXPermissions.startPermissionActivity(activity, permissions);
                }
            }

            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                super.onDenied(permissions, never)
                // gotoAppDetailIntent(this@LoginActivity)
                XXPermissions.startPermissionActivity(activity, permissions);
            }
        })
}