package com.xcjh.tpInfo.utils

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import com.xcjh.tpInfo.appViewModel
import com.xcjh.tpInfo.bean.LoginInfo
import com.xcjh.tpInfo.bean.UserInfo

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
        appViewModel.updateLoginEvent.postValue(isLogin)
        if (isLogin && login != null) {
            saveToken(login.tokenValue)
        } else {
            saveToken("")
        }
        val kv = MMKV.mmkvWithID("app")
        kv.encode("login", isLogin)
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

}