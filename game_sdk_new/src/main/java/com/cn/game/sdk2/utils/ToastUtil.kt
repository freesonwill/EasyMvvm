package com.cn.game.sdk2.utils

import android.content.Context
import android.widget.Toast

object ToastUtil {
    fun showToast(context: Context,str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }
}