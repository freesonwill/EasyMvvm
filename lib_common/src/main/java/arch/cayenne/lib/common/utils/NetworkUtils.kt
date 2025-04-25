package arch.cayenne.lib.common.utils

import android.Manifest.permission
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.annotation.RequiresPermission
import org.koin.java.KoinJavaComponent.getKoin

/**
 * @author: zhangsan
 * @date: 2025/4/23 16:40
 * @description: 网络工具类
 */
object NetworkUtils {

    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isConnected(): Boolean {
        val info: NetworkInfo? = getActiveNetworkInfo()
        return info != null && info.isConnected
    }

    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    private fun getActiveNetworkInfo(): NetworkInfo? {
        val app = getKoin().get<Application>()
        val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }
}