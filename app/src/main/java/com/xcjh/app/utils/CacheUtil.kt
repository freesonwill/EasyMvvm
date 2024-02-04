package com.xcjh.app.utils

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import com.xcjh.app.appViewModel
import com.xcjh.app.bean.LoginInfo
import com.xcjh.app.bean.MainDataBean
import com.xcjh.app.bean.UserInfo
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.network.cookie.CookieManger

object CacheUtil {
    /**
     * 获取保存的账户信息
     */
    fun getUser(): UserInfo? {
        val kv = MMKV.mmkvWithID("app")
        val userStr = kv.decodeString("user")
        return if (TextUtils.isEmpty(userStr)) {
            null
        } else {
           // Log.e("===", "getUser: ===" + userStr)
            Gson().fromJson(userStr, UserInfo::class.java)
        }
    }

    /**
     * 设置账户信息
     */
    fun setUser(userResponse: UserInfo?) {
        val kv = MMKV.mmkvWithID("app")
        if (userResponse == null) {
            kv.encode("user", "")
            // setIsLogin(false)
        } else {
            kv.encode("user", Gson().toJson(userResponse))
            // setIsLogin(true)
        }

    }

    /**
     * 保存登录成功返回的token
     */
    fun getToken(): String {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeString("jwtToken") ?: ""
    }

    /**
     * 保存登录成功返回的token
     */
    fun saveToken(jwtToken: String?) {
        val kv = MMKV.mmkvWithID("app")
        jwtToken?.let {
            kv.encode("jwtToken", jwtToken)
        }
    }

    /**
     * 是否已经登录
     */
    fun isLogin(): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeBool("login", false)
    }

    /**
     * 设置是否已经登录
     */
    fun setIsLogin(isLogin: Boolean, login: LoginInfo? = null) {

        if (isLogin && login != null) {
            saveToken(login.tokenValue)
            onWsUserLogin(){}
        } else {
            saveToken("")
            onWsUserLoginOut(){}
            CookieManger(appContext).removeAll()
            setUser(null)
        }
        val kv = MMKV.mmkvWithID("app")
        kv.encode("login", isLogin)
        appViewModel.updateLoginEvent.postValue(isLogin)
    }

    /**
     * 是否是第一次启动
     */
    fun isFirst(): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeBool("first", true)
    }

    /**
     * 是否是第一次启动
     */
    fun setFirst(first: Boolean): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.encode("first", first)
    }


    /**
     * 获取搜索历史缓存数据
     */
    fun getSearchHistoryData(): ArrayList<String> {
        val kv = MMKV.mmkvWithID("cache")
        val searchCacheStr = kv.decodeString("history")
        if (!TextUtils.isEmpty(searchCacheStr)) {
            return Gson().fromJson(searchCacheStr, object : TypeToken<ArrayList<String>>() {}.type)
        }
        return arrayListOf()
    }

    fun setSearchHistoryData(searchResponseStr: String) {
        val kv = MMKV.mmkvWithID("cache")
        kv.encode("history", searchResponseStr)
    }



    /**
     * 是否不在提示
     */
    fun isNoPrompts(): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeBool("No_more_prompts", false)
    }

    /**
     * 是否不在提示
     */
    fun setNoPrompts(prompts: Boolean): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.encode("No_more_prompts", prompts)
    }


    /**
     *是否打开主播震动  默认是打开
     */
    fun isAnchorVibrate(): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeBool("anchorVibrate", true)
    }


    /**
     * 设置是否主播开播震动
     */
    fun setAnchorVibrate(anchor: Boolean): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.encode("anchorVibrate", anchor)
    }


    /**
     *是否打开导航栏震动  默认是打开
     */
    fun isNavigationVibrate(): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeBool("navigationVibrate", true)
    }

    /**
     * 设置是否导航栏震动
     */
    fun setNavigationVibrate(navigation: Boolean): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.encode("navigationVibrate", navigation)
    }





}