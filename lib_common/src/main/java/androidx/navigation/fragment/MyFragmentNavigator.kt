package androidx.navigation.fragment

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

/**
 * @author: zhangsan
 * @date: 2025/6/2 11:54
 * @description:
 */
@Navigator.Name("fragment")
class MyFragmentNavigator(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) : FragmentNavigator(context, fragmentManager, containerId) {

    private var savedIds: MutableSet<String>? = null

    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ) {
        if (fragmentManager.isStateSaved) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already saved its state")
            return
        }
        for (entry in entries) {
            navigate(entry, navOptions, navigatorExtras)
        }
    }

    private fun navigate(
        entry: NavBackStackEntry,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ) {

        //获取saveIds
        val savedIdsField = FragmentNavigator::class.java.getDeclaredField("savedIds")
        savedIdsField.isAccessible = true
        //获取这个属性的值
        savedIds = savedIdsField.get(this) as MutableSet<String>

        val initialNavigation = state.backStack.value.isEmpty()
        val restoreState = (
                navOptions != null && !initialNavigation &&
                        navOptions.shouldRestoreState() &&
                        savedIds!!.remove(entry.id)
                )
        if (restoreState) {
            // Restore back stack does all the work to restore the entry
            fragmentManager.restoreBackStack(entry.id)
            state.push(entry)
            return
        }
        val ft = createFragmentTransaction(entry, navOptions)

        if (!initialNavigation) {
            ft.addToBackStack(entry.id)
        }

        if (navigatorExtras is Extras) {
            for ((key, value) in navigatorExtras.sharedElements) {
                ft.addSharedElement(key, value)
            }
        }
        ft.commit()
        // The commit succeeded, update our view of the world
        state.push(entry)
    }

    private fun createFragmentTransaction(
        entry: NavBackStackEntry,
        navOptions: NavOptions?
    ): FragmentTransaction {
        val destination = entry.destination as Destination
        val args = entry.arguments
        var className = destination.className
        if (className[0] == '.') {
            className = context.packageName + className
        }

        val ft = fragmentManager.beginTransaction()
        //首先获取当前展示的Fragment
        val primaryNavigationFragment = fragmentManager.primaryNavigationFragment
        //将当前展示的Fragment隐藏
        if(primaryNavigationFragment != null) ft.hide(primaryNavigationFragment)
        //获取即将展示的Fragment
        val tag = destination.id.toString()
        var frag = fragmentManager.findFragmentByTag(tag)
        //如果在fragmentManager中能获取到这个Fragment，说明已经创建过这个Fragment
        if (frag != null) {
            ft.show(frag)
        } else {
            //如果没有，就需要创建新的Fragment
            frag = fragmentManager.fragmentFactory.instantiate(context.classLoader, className)
            //将其放入fragmentManager中
            frag.arguments = args

            //注意这里需要加到containerId里，不然不会显示Fragment的UI
            ft.add(containerId,frag, tag)

        }

        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }
        ft.setPrimaryNavigationFragment(frag)
        ft.setReorderingAllowed(true)
        return ft
    }

    companion object {
        private const val TAG = "MyFragmentNavigator"
    }
}
