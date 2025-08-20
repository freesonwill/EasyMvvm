package arch.cayenne.lib.common.utils.ext

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.FragivityFragmentNavigator
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.IAnimationOption
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logw
import arch.cayenne.lib.common.R
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * @author: zhangsan
 * @date: 2025/4/8 18:08
 * @description: Navigation的扩展
 */
object NavigationExt {
    private const val TAG = "NavigationExt"
    private val defaultNavOptions by lazy {
        NavOptions.Builder()
            //.setEnterAnim(R.anim.slide_in_right)  // 新页面进入动画 从右划入
            //.setExitAnim(R.anim.slide_out_left)   // 旧页面退出动画 <--
            //.setPopEnterAnim(R.anim.slide_in_left) // 返回时，新页面进入动画
            //.setPopExitAnim(R.anim.slide_out_right) // 返回时，当前页面退出动画
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
                //优先用 directions 中的，再 navOptions中的
                val isSet = dNavOptions.let { it.enterAnim != -1 || it.exitAnim != -1 || it.popEnterAnim != -1 || it.popExitAnim != -1 }
                val animOption = if(isSet) dNavOptions else navOptions
                val enterAnim = animOption.enterAnim
                val exitAnim = animOption.exitAnim
                val popEnterAnim = animOption.popEnterAnim
                val popExitAnim = animOption.popExitAnim
                // 动画合并
                setEnterAnim(enterAnim)
                setExitAnim(exitAnim)
                setPopEnterAnim(popEnterAnim)
                setPopExitAnim(popExitAnim)

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
        if(isNavigationDebounced("$this,deepLink:$deepLink")) return
        findNavController(viewId).navigate(deepLink, navOptions, navigatorExtras)
    }

    fun Activity.navigate(
        directions: NavDirections,
        navOptions: NavOptions? = defaultNavOptions,
        @IdRes viewId: Int = R.id.nav_host
    ) {
        if(isNavigationDebounced("$this,directions:$directions")) return
        val navController = findNavController(viewId)
        navController.navigate(directions, mergedNavOption(navController,directions, navOptions))
    }

    private fun setupDefaultAnim(args: Bundle,
        enterAnim:IAnimationOption?,
        exitAnim:IAnimationOption?,
        popEnterAnim:IAnimationOption?,
        popExitAnim:IAnimationOption?
    ):Bundle{
        args.putString(FragivityFragmentNavigator.KEY_ENTER_ANIM, enterAnim?.toJson())
        args.putString(FragivityFragmentNavigator.KEY_EXIT_ANIM, exitAnim?.toJson())
        args.putString(FragivityFragmentNavigator.KEY_POP_ENTER_ANIM, popEnterAnim?.toJson())
        args.putString(FragivityFragmentNavigator.KEY_POP_EXIT_ANIM, popExitAnim?.toJson())
        return args
    }

    fun Activity.navigate(
        @IdRes resId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = defaultNavOptions,
        navigatorExtras: Navigator.Extras? = null,
        @IdRes viewId: Int = R.id.nav_host
    ) {
        if(isNavigationDebounced("$this,args:$args")) return
        findNavController(viewId).navigate(resId, args, navOptions, navigatorExtras)
    }

    fun Activity.navigate(
        intent: Intent,
        navOptions: NavOptions = defaultNavOptions,
    ) {
        if(isNavigationDebounced("$this,intent:$intent")) return
        val options = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        startActivity(intent, options.toBundle())
    }

    fun Activity.navigateUp(@IdRes viewId: Int = R.id.nav_host): Boolean {
        return findNavController(viewId).navigateUp()
    }

    fun Activity.popBackStack(@IdRes viewId: Int = R.id.nav_host): Boolean {
        return findNavController(viewId).popBackStack()
    }


    /**
     * 执行防抖的导航操作
     */
    private var lastNavigateTime = 0L
    private fun isNavigationDebounced(reason: String): Boolean {
        val isDebounced = System.currentTimeMillis() - lastNavigateTime < 500L
        if(!isDebounced) {
            lastNavigateTime = System.currentTimeMillis()
        } else {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            "navigation blocked by debounce,lastNavigateTime:${sdf.format(lastNavigateTime)},reason:$reason".logw(TAG)
        }
        return isDebounced
    }

    /** Fragment的默认跳转 **/
    fun Fragment.navigate(
        deepLink: Uri,
        navOptions: NavOptions? = defaultNavOptions,
        navigatorExtras: Navigator.Extras? = null,
        enterAnim:IAnimationOption = AnimationController.routeEnterAnim,
        exitAnim:IAnimationOption = AnimationController.routeExitAnim,
        popEnterAnim:IAnimationOption = AnimationController.routePopEnterAnim,
        popExitAnim:IAnimationOption = AnimationController.routePopExitAnim,
    ) {
        if(isNavigationDebounced("$this,uri:$deepLink")) return
        val args = setupDefaultAnim(Bundle(),enterAnim,exitAnim,popEnterAnim,popExitAnim)
        // 将 Bundle 转 queryString
        val uriWithArgs = deepLink.buildUpon().apply {
            for (key in args.keySet()) {
                val value = args.get(key)?.toString() ?: continue
                appendQueryParameter(key, value)
            }
        }.build()
        //"deepLink--->$deepLink,uriWithArgs:$uriWithArgs".logd(TAG)
        findNavController().navigate(uriWithArgs,navOptions, navigatorExtras)
    }

    fun Fragment.navigate(
        directions: NavDirections,
        navOptions: NavOptions? = defaultNavOptions,
        enterAnim:IAnimationOption = AnimationController.routeEnterAnim,
        exitAnim:IAnimationOption = AnimationController.routeExitAnim,
        popEnterAnim:IAnimationOption = AnimationController.routePopEnterAnim,
        popExitAnim:IAnimationOption = AnimationController.routePopExitAnim,
    ) {
        if(isNavigationDebounced("$this")) return
        val navController = findNavController()
        setupDefaultAnim(directions.arguments,enterAnim,exitAnim,popEnterAnim,popExitAnim)
        navController.navigate(directions, mergedNavOption(navController, directions, navOptions))
    }

    fun Fragment.navigate(
        @IdRes resId: Int,
        args: Bundle = Bundle(),
        navOptions: NavOptions? = defaultNavOptions,
        navigatorExtras: Navigator.Extras? = null,
        enterAnim:IAnimationOption = AnimationController.routeEnterAnim,
        exitAnim:IAnimationOption = AnimationController.routeExitAnim,
        popEnterAnim:IAnimationOption = AnimationController.routePopEnterAnim,
        popExitAnim:IAnimationOption = AnimationController.routePopExitAnim,
    ) {
        if(isNavigationDebounced("$this")) return
        setupDefaultAnim(args,enterAnim,exitAnim,popEnterAnim,popExitAnim)
        findNavController().navigate(resId, args, navOptions, navigatorExtras)
    }

    fun Fragment.navigateUp(): Boolean {
        return findNavController().navigateUp()
    }

    fun Fragment.popBackStack(): Boolean {
        return findNavController().popBackStack()
    }
}