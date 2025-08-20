package androidx.fragment.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
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
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.animation.IAnimationOption
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.R
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
    private val backNavOptionStack = ArrayDeque<AnimationCompose?>()
    private val descendingBackStack = backStack.asReversed()
    // 用于记录Fragment的延迟隐藏状态
    private val fragmentDelayedHideMap = mutableMapOf<String, Boolean>()
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
                    val fragment = let {
                        val index = backStack.size - 1
                        val tag = if(index == -1) null else generateBackStackName(index, backStack[index])
                        val lastFragment = fragmentManager.findFragmentByTag(tag)
                        lastFragment
                    } ?: return@addOnBackStackChangedListener
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
        val (anim1,anim2) = setupAnimation(args,navOptions,ft)
        val (enterAnim,exitAnim,popEnterAnim,popExitAnim) = anim1
        val (enterAnim2,exitAnim2,popEnterAnim2,popExitAnim2) = anim2
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
            execAfterAnim(fragment,enterAnim,enterAnim2,exitAnim,exitAnim2,prevFragment.tag!!) {
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

    private fun setupAnimation(args: Bundle?,
                               navOptions: NavOptions?,
                               ft: FragmentTransaction,
    ):Pair<IntArray,Array<IAnimationOption?>> {
        val deepLinkIntent: Intent? = args?.getParcelable("android-support-nav:controller:deepLinkIntent")
        val enterAnim2:IAnimationOption? = (args?.getString(KEY_ENTER_ANIM) ?: deepLinkIntent?.data?.getQueryParameter(KEY_ENTER_ANIM))?.let {
            IAnimationOption.fromJson(it)
        }
        val exitAnim2:IAnimationOption? = (args?.getString(KEY_EXIT_ANIM) ?: deepLinkIntent?.data?.getQueryParameter(KEY_EXIT_ANIM))?.let {
            IAnimationOption.fromJson(it)
        }
        val popEnterAnim2:IAnimationOption? = (args?.getString(KEY_POP_ENTER_ANIM) ?: deepLinkIntent?.data?.getQueryParameter(KEY_POP_ENTER_ANIM))?.let {
            IAnimationOption.fromJson(it)
        }
        val popExitAnim2:IAnimationOption? = (args?.getString(KEY_POP_EXIT_ANIM) ?: deepLinkIntent?.data?.getQueryParameter(KEY_POP_EXIT_ANIM))?.let {
            IAnimationOption.fromJson(it)
        }

        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1

        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else -1
            exitAnim = if (exitAnim != -1) exitAnim else -1
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else -1
            popExitAnim = if (popExitAnim != -1) popExitAnim else -1
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }
        if(navOptions != null) {
            val currentFragment = fragmentManager.primaryNavigationFragment
            fragmentManager.registerFragmentLifecycleCallbacks(object :FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
                    //"onFragmentViewCreated-->$navOptions,currentFragment:$currentFragment,f:$f".logd(TAG)
                    fm.unregisterFragmentLifecycleCallbacks(this)
                    //your logic
                    if(enterAnim.let { it == -1 && it != R.anim.no_anim }) {
                        enterAnim2?.toAnimation()?.let {
                            (f as? BaseFragment<*, *>)?.apply {
                                it.setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationStart(animation: Animation?) {}
                                    override fun onAnimationEnd(animation: Animation?) { this@apply.onFragmentAnimEnd(true) }
                                    override fun onAnimationRepeat(animation: Animation?) {}
                                })
                            }
                            f.requireView().startAnimation(it)
                        } ?: run { (f as? BaseFragment<*, *>)?.onFragmentAnimEnd(true) }
                    } else {
                        (f as? BaseFragment<*, *>)?.onFragmentAnimEnd(true)
                    }
                    if(exitAnim.let { it == -1 && it != R.anim.no_anim }) {
                        exitAnim2?.toAnimation()?.let {
                            (currentFragment as? BaseFragment<*, *>)?.apply {
                                it.setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationStart(animation: Animation?) {}
                                    override fun onAnimationEnd(animation: Animation?) { this@apply.onFragmentAnimEnd(false) }
                                    override fun onAnimationRepeat(animation: Animation?) {}
                                })
                            }
                            currentFragment?.view?.startAnimation(it)
                        } ?: run { (currentFragment as? BaseFragment<*, *>)?.onFragmentAnimEnd(false) }
                    } else {
                        (currentFragment as? BaseFragment<*, *>)?.onFragmentAnimEnd(false)
                    }
                }
            },false)

        }
        backNavOptionStack.add(navOptions?.let {
            AnimationCompose(enterAnim, enterAnim2, exitAnim, exitAnim2, popEnterAnim, popEnterAnim2, popExitAnim, popExitAnim2)
        })
        "enterAnim:$enterAnim,exitAnim:$exitAnim,popEnterAnim:$popEnterAnim,popExitAnim:$popExitAnim".logd(TAG)
        "enterAnim2:$enterAnim2,\nexitAnim2:$exitAnim2,\npopEnterAnim2:$popEnterAnim2,\npopExitAnim2:$popExitAnim2".logd(TAG)
        //"navOptions-->$navOptions,backNavOptionStack:$backNavOptionStack".logd(TAG)
        return intArrayOf(enterAnim,exitAnim,popEnterAnim,popExitAnim) to arrayOf(enterAnim2,exitAnim2,popEnterAnim2,popExitAnim2)
    }

    private fun parseAnimation(json:String){

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
                              enterAnim2:IAnimationOption?,
                              exitAnim:Int,
                              exitAnim2:IAnimationOption?,
                              tag:String,
                              action:(isImmediate:Boolean)->Unit
    ){
       //"execAfterAnim==>$nextFragment,enterAnim:$enterAnim,exitAnim:$exitAnim,tag:${tag},enterAnimDefault:$enterAnimDefault".logd(TAG)
        if ((exitAnim == -1 || exitAnim == R.anim.no_anim) && (enterAnim != -1 || enterAnim2 != null)) { //没有退出动画需要延时隐藏
            nextFragment.launch(Lifecycle.State.RESUMED, lifecycleScope = nextFragment.lifecycleScope) {
                val duration = if(enterAnim != -1) {
                    val anim = AnimationUtils.loadAnimation(nextFragment.requireContext(), enterAnim)
                    anim.duration
                } else enterAnim2!!.duration
                //"execAfterAnim==>$nextFragment,duration:$duration".logd(TAG)
                delay(duration)
                action(false)
                fragmentDelayedHideMap[tag] = true
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
                //"popBackStack==>$prevFragment,tag:$tag,${fragmentDelayedHideMap[tag]}".logd(TAG)
                if(fragmentDelayedHideMap[tag] == true){
                    val prevFragment = fragmentManager.findFragmentByTag(tag)!!
                    fragmentManager.beginTransaction()
                        .show(prevFragment)
                        .commit()
                }
                fragmentDelayedHideMap.remove(tag)
            }
            val backStackName = generateBackStackName(backStack.size, backStack.last())
            fragmentManager.popBackStack(backStackName, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            mIsPendingPopBackStackOperation = true
        }
        backStack.removeLast()
        exitPopupAnim()
        return true
    }

    /**
     * Animation组合类
     *
     */
    private data class AnimationCompose(
        val enterAnim:Int,
        val enterAnim2:IAnimationOption?,
        val exitAnim:Int,
        val exitAnim2:IAnimationOption?,
        val popEnterAnim:Int,
        val popEnterAnim2:IAnimationOption?,
        val popExitAnim:Int,
        val popExitAnim2:IAnimationOption?,
    )
    /**
     * 退出弹窗动画
     */
    private fun exitPopupAnim() {
        val lastNavOption = backNavOptionStack.removeLast() ?: return
        val popEnterAnim = lastNavOption.popEnterAnim
        val popExitAnim = lastNavOption.popExitAnim
        //"popEnterAnim:$popExitAnim,popExitAnim:$popExitAnim,backNavOptionStack:${backNavOptionStack}".logd(TAG)
        val manager = fragmentManager
        val popEnterAnimDefault = lastNavOption.popEnterAnim2
        val popExitAnimDefault = lastNavOption.popExitAnim2
        manager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                //"onFragmentStarted-->$lastNavOption,currentFragment:$currentFragment,f:$f".logd(TAG)
                fm.unregisterFragmentLifecycleCallbacks(this)
                if (popEnterAnim == -1) popEnterAnimDefault?.let {
                    f.requireView().startAnimation(it.toAnimation())
                }
            }

            override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
                //"onFragmentStopped-->$lastNavOption,currentFragment:$currentFragment,f:$f".logd(TAG)
                if (popExitAnim == -1) popExitAnimDefault?.let { anim ->
                    f.requireView().startAnimation(anim.toAnimation())
                }
            }

            override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                //"onFragmentDestroyed-->$lastNavOption,currentFragment:$currentFragment".logd(TAG)
            }
        },false)
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
