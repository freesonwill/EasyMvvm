package arch.cayenne.lib.common.utils.ext

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.common.R

/**
 * @author: zhangsan
 * @date: 2025/4/8 18:08
 * @description: Navigation的扩展
 */
object NavigationExt {
    private val defaultNavOptions by lazy {
        NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)  // 新页面进入动画 从右划入
            .setExitAnim(R.anim.slide_out_left)   // 旧页面退出动画 <--
            .setPopEnterAnim(R.anim.slide_in_left) // 返回时，新页面进入动画
            .setPopExitAnim(R.anim.slide_out_right) // 返回时，当前页面退出动画
            .build()
    }

    /**
     * directions的配置覆盖navOptions的
     *
     * @param navController
     * @param directions
     * @param navOptions
     * @return
     */
    private fun mergedNavOption(
        navController: NavController,
        directions: NavDirections,
        navOptions: NavOptions?
    ): NavOptions?{
        return navOptions?.let {
            //directions没有navOptions用navOptions
            val dNavOptions = navController.currentDestination?.getAction(directions.actionId)?.navOptions ?: return@let navOptions
            val mergedOptions = NavOptions.Builder().apply {
                // 动画合并
                setEnterAnim(navOptions.enterAnim)
                setExitAnim(navOptions.exitAnim)
                setPopEnterAnim(navOptions.popEnterAnim)
                setPopExitAnim(navOptions.popExitAnim)
                //合并popUpTo、isPopUpToInclusive
                navOptions.takeIf { it.popUpToId != -1 }?.let {
                    setPopUpTo(it.popUpToId, it.isPopUpToInclusive())
                }
                // popUpTo 逻辑：优先用 directions 中的，再 navOptions中的
                dNavOptions.takeIf { it.popUpToId != -1 }?.let {
                    setPopUpTo(it.popUpToId, it.isPopUpToInclusive())
                }
                // 合并launchSingleTop
                setLaunchSingleTop(dNavOptions.shouldLaunchSingleTop())
            }.build()
            mergedOptions
        }
    }

    /** Activity的默认跳转 **/
    fun Activity.navigate(
        deepLink: Uri,
        navOptions: NavOptions? = defaultNavOptions,
        navigatorExtras: Navigator.Extras? = null,
        @IdRes viewId: Int = R.id.nav_host
    ) {
        findNavController(viewId).navigate(deepLink, navOptions, navigatorExtras)
    }

    fun Activity.navigate(
        directions: NavDirections,
        navOptions: NavOptions? = defaultNavOptions,
        @IdRes viewId: Int = R.id.nav_host
    ) {
        val navController = findNavController(viewId)
        navController.navigate(directions, mergedNavOption(navController,directions, navOptions))
    }

    fun Activity.navigate(
        @IdRes resId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = defaultNavOptions,
        navigatorExtras: Navigator.Extras? = null,
        @IdRes viewId: Int = R.id.nav_host
    ) {
        findNavController(viewId).navigate(resId, args, navOptions, navigatorExtras)
    }

    fun Activity.navigate(
        intent: Intent,
        navOptions: NavOptions = defaultNavOptions,
    ) {
        val options = ActivityOptions.makeCustomAnimation(
            this,
            navOptions.enterAnim,
            navOptions.exitAnim,
        )
        startActivity(intent, options.toBundle())
    }

    fun Activity.navigateUp(@IdRes viewId: Int = R.id.nav_host): Boolean {
        return findNavController(viewId).navigateUp()
    }

    fun Activity.popBackStack(@IdRes viewId: Int = R.id.nav_host): Boolean {
        return findNavController(viewId).popBackStack()
    }

    /** Fragment的默认跳转 **/
    fun Fragment.navigate(
        deepLink: Uri,
        navOptions: NavOptions? = defaultNavOptions,
        navigatorExtras: Navigator.Extras? = null
    ) {
        findNavController().navigate(deepLink, navOptions, navigatorExtras)
    }

    fun Fragment.navigate(
        directions: NavDirections,
        navOptions: NavOptions? = defaultNavOptions
    ) {
        val navController = findNavController()
        navController.navigate(directions,mergedNavOption(navController,directions,navOptions))
    }


    fun Fragment.navigate(
        @IdRes resId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = defaultNavOptions,
        navigatorExtras: Navigator.Extras? = null
    ) {
        findNavController().navigate(resId, args, navOptions, navigatorExtras)
    }

    fun Fragment.navigateUp(): Boolean {
        return findNavController().navigateUp()
    }

    fun Fragment.popBackStack(): Boolean {
        return findNavController().popBackStack()
    }
}