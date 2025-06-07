package androidx.navigation.fragment

/**
 * @author: zhangsan
 * @date: 2025/4/4 10:31
 * @description:
 */
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd

@Navigator.Name("fragment")
class HideShowFragmentNavigator2(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) : FragmentNavigator(context, fragmentManager, containerId) {
    companion object {
        private const val TAG = "KeepStateNavigator"

        // 注册方式（在 Activity 中）
        fun setupKeepStateNavigator(
            context: Context,
            fragmentManager: FragmentManager,
            containerId: Int,
            navController: NavController,
        ) {
            val keepStateNavigator = HideShowFragmentNavigator2(
                context,
                fragmentManager,
                containerId
            )
            navController.navigatorProvider.addNavigator(keepStateNavigator)
        }
    }

    private val fragmentTagPrefix = "android:switcher:"

    /*override fun createDestination(): FragmentNavigator.Destination {
        return FragmentNavigator.Destination(this)
    }*/

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination {
        "navigate--->${destination.className}".logd(TAG)
        val tag = fragmentTagPrefix + destination.id
        val transaction = fragmentManager.beginTransaction()

        val currentFragment = fragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }

        var fragment = fragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            fragment = fragmentManager.fragmentFactory.instantiate(
                context.classLoader, destination.className
            ).apply {
                arguments = args
            }
            transaction.add(containerId, fragment, tag)
        } else {
            transaction.show(fragment)
        }

        transaction.setPrimaryNavigationFragment(fragment)
        transaction.setReorderingAllowed(true)
        transaction.commit()

        return destination
    }

    override fun createDestination(): FragmentNavigator.Destination {
        TODO("Not yet implemented")
    }

    /*override fun popBackStack(): Boolean {
        return fragmentManager.popBackStackImmediate()
    }*/
}


