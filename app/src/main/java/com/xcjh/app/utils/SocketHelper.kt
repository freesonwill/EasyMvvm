package com.xcjh.app.utils

import com.google.gson.Gson
import com.xcjh.app.R
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.ReceiveWsBean
import com.xcjh.app.websocket.bean.SendCommonWsBean
import com.xcjh.app.websocket.listener.LoginOrOutListener
import com.xcjh.base_lib.App
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.getUUID


/**
 * 登录
 */
fun onWsUserLogin(callback: (Boolean) -> Unit) {
    //先加监听
    MyWsManager.getInstance(App.app)
        ?.setLoginOrOutListener("onWsUserLogin", object : LoginOrOutListener {
            override fun onLoginIn(isOk: Boolean, msg: ReceiveWsBean<*>) {
                callback.invoke(true)
            }

            override fun onLoginOut(isOk: Boolean, msg: ReceiveWsBean<*>) {
                callback.invoke(false)
            }
        })
    MyWsManager.getInstance(App.app)?.sendMessage(
        Gson().toJson(
            SendCommonWsBean(
                5,
                CacheUtil.getUser()?.id,
                CacheUtil.getToken()
            )
        )
    )

}

/**
 * 登出
 */
fun onWsUserLoginOut(callback: (Boolean) -> Unit) {
    //先加监听
    MyWsManager.getInstance(App.app)
        ?.setLoginOrOutListener("onWsUserLoginOut", object : LoginOrOutListener {
            override fun onLoginIn(isOk: Boolean, msg: ReceiveWsBean<*>) {
                callback.invoke(true)
            }

            override fun onLoginOut(isOk: Boolean, msg: ReceiveWsBean<*>) {
                callback.invoke(false)
            }
        })
    //再发送消息
    MyWsManager.getInstance(App.app)?.sendMessage(
        Gson().toJson(
            SendCommonWsBean(
                22,
                CacheUtil.getUser()?.id
            )
        )
    )
}

/**
 * 加入群聊
 */
fun onWsUserEnterRoom(groupId: String) {
    MyWsManager.getInstance(App.app)?.sendMessage(
        Gson().toJson(
            SendCommonWsBean(
                7,
                CacheUtil.getUser()?.id?: getUUID(),
                groupId = groupId,
            )
        )
    )
}

/**
 * 退出群聊
 */
fun onWsUserExitRoom(groupId: String) {
    MyWsManager.getInstance(App.app)?.sendMessage(
        Gson().toJson(
            SendCommonWsBean(
                21,
                CacheUtil.getUser()?.id?: getUUID(),
                groupId = groupId,
            )
        )
    )
}

/**
 * 等级解析
 *
1黑铁  #606060
2黄铜 #E3793A
3青铜  #90A7A3
4白银  #9B9B9B

5黄金 #E2AC61
6铂金 #A6C6CE
7钻石  #8DD8F4  #89A5D1 #B797BC
8超凡    #6B2BF4  #74619C               #6C2FED
 */
fun getLeverNum(level: String?): String {
    val name: String = when (level) {
        "0" -> appContext.getString(R.string.level1)
        "1" -> appContext.getString(R.string.level1)
        "2" -> appContext.getString(R.string.level2)
        "3" -> appContext.getString(R.string.level3)
        "4" -> appContext.getString(R.string.level4)
        "5" -> appContext.getString(R.string.level5)
        "6" -> appContext.getString(R.string.level6)
        else -> appContext.getString(R.string.level6)
    }
    return name
}

fun setLeverColor(level: String?): Int {
    var color = appContext.getColor(R.color.c_v1)
    when (level) {
        "1" -> color = appContext.getColor(R.color.c_v1)
        "2" -> color = appContext.getColor(R.color.c_v2)
        "3" -> color = appContext.getColor(R.color.c_v3)
        "4" -> color = appContext.getColor(R.color.c_v4)
        "5" -> color = appContext.getColor(R.color.c_v5)
        "6" -> color = appContext.getColor(R.color.c_v6)
        "7" -> color = appContext.getColor(R.color.c_v7)
        "8" -> color = appContext.getColor(R.color.c_v8)
    }
    return color
}

fun setLeverDrawable(level: String?): Int {
    var drawable = R.drawable.ic_user_level1
    when (level) {
        "1" -> drawable = R.drawable.ic_user_level1
        "2" -> drawable = R.drawable.ic_user_level2
        "3" -> drawable = R.drawable.ic_user_level3
        "4" -> drawable = R.drawable.ic_user_level4
        "5" -> drawable = R.drawable.ic_user_level5
        "6" -> drawable = R.drawable.ic_user_level6
       /* "7" -> drawable = R.drawable.ic_user_level7
        "8" -> drawable = R.drawable.ic_user_level8*/
    }
    return drawable
}

