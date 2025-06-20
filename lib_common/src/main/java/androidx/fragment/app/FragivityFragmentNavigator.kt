package androidx.fragment.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentTransaction.OP_ADD
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import arch.cayenne.lib.base.ui._interface.OnNewIntentListener
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.utils.ext.FragmentExt.plusAssign
import arch.cayenne.lib.common.utils.ext.FragmentExt.replaceAll
import kotlinx.coroutines.delay

@Navigator.Name("fragment")
class FragivityFragmentNavigator(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) : Navigator<FragmentNavigator.Destination>() {

    private val backStack = ArrayDeque<Int>()
    private val descendingBackStack = backStack.asReversed()

    private var mIsPendingAddToBackStackOperation = false
    private var mIsPendingPopBackStackOperation = false
    private val savedIds = mutableSetOf<String>()

    init {
        // Need to cooperate with ReportFragmentManager
        if (fragmentManager is ReportFragmentManager) {
            fragmentManager.addOnBackStackChangedListener {
                if (mIsPendingAddToBackStackOperation) { //增加加入回退栈
                    mIsPendingAddToBackStackOperation = !isBackStackEqual()
                    val size = fragmentManager.fragments.size
                    if (size > 1) {
                        // 切到后台时的生命周期
                        val fragment = fragmentManager.fragments[size - 2]
                        // fragment onPause -> onStop
                        setMaxLifecycle(fragment, Lifecycle.State.STARTED) //onPause
                        fragment.requireView().post {
                            fragment.performStop() //onStop
                            fragment.mState = Fragment.STARTED //避免触发两次onStart
                        }
                    }
                } else if (mIsPendingPopBackStackOperation) { //正在回退
                    mIsPendingPopBackStackOperation = !isBackStackEqual()
                    // 回到前台时的生命周期
                    val fragment = fragmentManager.primaryNavigationFragment
                        ?: return@addOnBackStackChangedListener
                    // fragment (true) ?: onStart : onCreateView -> onResume
                    if (fragment.mState == Fragment.STARTED) {
                        fragment.mState = Fragment.ACTIVITY_CREATED
                    }
                    setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
                }
            }
        }
    }

    override fun createDestination(): FragmentNavigator.Destination {
        return FragmentNavigator.Destination(this)
    }

    private fun createFragment(
        destination: FragmentNavigator.Destination,
        args: Bundle?
    ): Fragment {
        if (destination is FragivityFragmentDestination) {
            return destination.createFragment(args)
        }

        var className = destination.className
        if (className[0] == '.') {
            className = context.packageName + className
        }

        val fragment = fragmentManager.fragmentFactory.instantiate(context.classLoader, className)
        fragment.arguments = args
        return fragment
    }

    override fun navigate(
        destination: FragmentNavigator.Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ): NavDestination? {
        if (fragmentManager.isStateSaved) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already saved its state")
            return null
        }

        val ft = fragmentManager.beginTransaction()

        val isPushTo = args?.getBoolean(KEY_PUSH_TO, false) == true
        var enterAnim: Int
        var exitAnim: Int
        var popEnterAnim: Int
        var popExitAnim: Int
        if (isPushTo) {
            enterAnim = args?.getInt(KEY_ENTER_ANIM) ?: -1
            exitAnim = args?.getInt(KEY_EXIT_ANIM) ?: -1
            popEnterAnim = args?.getInt(KEY_POP_ENTER_ANIM) ?: -1
            popExitAnim = args?.getInt(KEY_POP_EXIT_ANIM) ?: -1
        } else {
            enterAnim = navOptions?.enterAnim ?: -1
            exitAnim = navOptions?.exitAnim ?: -1
            popEnterAnim = navOptions?.popEnterAnim ?: -1
            popExitAnim = navOptions?.popExitAnim ?: -1
        }

        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }

        val destId = destination.id
        val initialNavigation = backStack.isEmpty() || isPushTo
        val existingFragment = backStack.indexOf(destId).let { index ->
            if(index != -1) {
                val tag = generateBackStackName(index, destId)
                val existingFragment = fragmentManager.findFragmentByTag(tag)
                return@let existingFragment// 提前返回外部函数
            }
            return@let null
        }
        val fragment = existingFragment ?: createFragment(destination, args)
        if(existingFragment == null) {
            ft.add(containerId, fragment, generateBackStackName(backStack.size, destId))
        } else {
            if(fragment is OnNewIntentListener){
                fragment.arguments = args
                fragment.onNewIntent(Intent().apply { if(args != null) putExtras(args)})
            }
        }

        val prevFragment = if (isPushTo) {
            fragmentManager.fragments.forEach { ft.remove(it) }
            null
        } else {
            fragmentManager.primaryNavigationFragment
        }

        ft.setPrimaryNavigationFragment(fragment)

        val isSingleTopReplacement = !initialNavigation
            && navOptions != null && navOptions.shouldLaunchSingleTop()
            && backStack.last() == destId

        // when popsSelf == true close preFrag as SingleTop
        // see https://github.com/vitaviva/fragivity/issues/26
        val isPopSelf = args?.getBoolean(KEY_POP_SELF, false) == true

        val isAdded: Boolean
        if (initialNavigation) {
            isAdded = true
        } else if (isSingleTopReplacement || isPopSelf) {
            if (prevFragment != null) {
                ft.remove(prevFragment)
                fragment.mTag = generateBackStackName(backStack.size - 1, destId)
                val backStack = fragmentManager.mBackStack
                if (backStack != null && backStack.size > 0) {
                    fragmentManager.mBackStack[backStack.size - 1].mOps
                        .filter { it.mCmd == OP_ADD && it.mFragment == prevFragment }
                        .forEach { it.mFragment = fragment }
                }
            }
            isAdded = false
        } else {
            if(existingFragment == null) {
                ft.addToBackStack(generateBackStackName(backStack.size + 1, destId))
                mIsPendingAddToBackStackOperation = true
                isAdded = true
            }else {
                isAdded = false
            }
        }

        if (isAdded && prevFragment != null) {
            execAfterAnim(fragment,enterAnim) {
                if(it) {
                    ft.hide(prevFragment)
                }else {
                    fragmentManager.beginTransaction()
                        .hide(prevFragment)
                        .commit()
                }
            }
        }

        if (navigatorExtras is FragmentNavigator.Extras) {
            navigatorExtras.sharedElements.forEach { entry ->
                ft.addSharedElement(entry.key, entry.value)
            }
        }

        ft.setReorderingAllowed(true)
        ft.commit()

        if (isPushTo) {
            // pushTo情况下清空返回栈
            fragmentManager.mBackStack?.clear()
        }

        if (isPopSelf) {
            backStack.removeLast()
            backStack.add(destId)
            return destination
        }

        if (isAdded) {
            backStack.add(destId)
            return destination
        }

        return null
    }

    private fun generateBackStackName(backStackIndex: Int, destinationId: Int): String {
        return "${backStackIndex}#${destinationId}"
    }

    private fun getDestinationId(backStackName: String): Int {
        val split = backStackName.split("#")
        if (split.size != 2) {
            throw IllegalStateException(
                "Invalid back stack entry on the "
                    + "NavHostFragment's back stack - use getChildFragmentManager() "
                    + "if you need to do custom FragmentTransactions from within "
                    + "Fragments created via your navigation graph."
            )
        }
        return split[1].toIntOrNull()
            ?: throw java.lang.IllegalStateException(
                "Invalid back stack entry on the "
                    + "NavHostFragment's back stack - use getChildFragmentManager() "
                    + "if you need to do custom FragmentTransactions from within "
                    + "Fragments created via your navigation graph."
            )
    }

    private fun execAfterAnim(nextFragment:Fragment,
                              enterAnim:Int,
                              action:(isImmediate:Boolean)->Unit
    ){
        if (enterAnim != -1) {
            nextFragment.launch(Lifecycle.State.RESUMED, lifecycleScope = nextFragment.lifecycleScope) {
                val anim = AnimationUtils.loadAnimation(nextFragment.requireContext(), enterAnim)
                val duration = anim.duration
                //"execAfterAnim==>$nextFragment,duration:$duration".logd(TAG)
                delay(duration)
                action(false)
                //"execAfterAnim==>$nextFragment".logd(TAG)
            }
        } else {
            action(true)
        }
    }

    /**
     * 回退栈是否相等
     * @return
     */
    private fun isBackStackEqual(): Boolean {
        val fragmentBackStackCount = fragmentManager.backStackEntryCount
        if (backStack.size != fragmentBackStackCount + 1) {
            return false
        }

        var backStackIndex = fragmentBackStackCount - 1
        val backStackIterator = descendingBackStack.iterator()
        while (backStackIterator.hasNext() && backStackIndex >= 0) {
            val destId = backStackIterator.next()
            val fragmentDestId = getDestinationId(
                fragmentManager.getBackStackEntryAt(backStackIndex--).name!!
            )
            if (destId != fragmentDestId) {
                return false
            }
        }
        return true
    }


    override fun popBackStack(): Boolean {
        if (backStack.isEmpty()) {
            return false
        }

        if (fragmentManager.isStateSaved) {
            Log.i(TAG, "Ignoring popBackStack() call: FragmentManager has already saved its state")
            return false
        }

        if (fragmentManager.backStackEntryCount > 0) {
            //注意tag是generateBackStackName(index, backStack[index])，backStackName是generateBackStackName(index+1, backStack[index])
            if(backStack.size > 1){
                val tag = generateBackStackName(backStack.size - 2,
                    backStack[backStack.size - 2])
                val prevFragment = fragmentManager.findFragmentByTag(tag)!!
                fragmentManager.beginTransaction()
                    .show(prevFragment)
                    .commit()
            }
            val backStackName = generateBackStackName(backStack.size, backStack.last())
            fragmentManager.popBackStack(backStackName, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            mIsPendingPopBackStackOperation = true
        }
        backStack.removeLast()
        return true
    }

    override fun onSaveState(): Bundle {
        return bundleOf(KEY_BACK_STACK_IDS to backStack.toIntArray())
    }

    override fun onRestoreState(savedState: Bundle) {
        backStack.replaceAll(savedState.getIntArray(KEY_BACK_STACK_IDS))
    }

    fun restoreTopFragment(destinationId: Int, newBundle: Bundle?) {
        val topFragment = findTopFragment(destinationId) ?: return
        // update args
        topFragment += newBundle
        // run onResume
        setMaxLifecycle(topFragment, Lifecycle.State.STARTED)
        setMaxLifecycle(topFragment, Lifecycle.State.RESUMED)
    }

    private fun findTopFragment(destinationId: Int): Fragment? {
        if (backStack.isEmpty()) return null

        var index = backStack.size - 1
        descendingBackStack.forEach { destId ->
            if (destinationId == destId) {
                return fragmentManager.findFragment(index, destId)
            }
            index--
        }
        return null
    }

    private fun FragmentManager.findFragment(backStackIndex: Int, destinationId: Int): Fragment? {
        return findFragmentByTag(generateBackStackName(backStackIndex, destinationId))
    }

    private fun setMaxLifecycle(fragment: Fragment, state: Lifecycle.State) {
        fragmentManager.commit { setMaxLifecycle(fragment, state) }
    }

    companion object {
        private const val TAG = "FragivityNavigator"
        private const val KEY_BACK_STACK_IDS = "myFragmentNavigator:backStackIds"

        internal const val KEY_POP_SELF = "Fragivity:PopSelf"
        internal const val KEY_PUSH_TO = "Fragivity:PushTo"

        internal const val KEY_ENTER_ANIM = "Fragivity:enterAnim"
        internal const val KEY_EXIT_ANIM = "Fragivity:exitAnim"
        internal const val KEY_POP_ENTER_ANIM = "Fragivity:popEnterAnim"
        internal const val KEY_POP_EXIT_ANIM = "Fragivity:popExitAnim"
    }
}
