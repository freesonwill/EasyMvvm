package arch.cayenne.lib.common.utils.ext

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

/**
 * @author: zhangsan
 * @date: 2025/4/19 15:11
 * @description: NavigationResultExt的发送扩展
 */
object NavResultExt {

    /**
     * 向目标fragment发送结果
     *
     * @param key
     * @param value
     * @param destinationId  navigation.xml中定义的fragmentID。默认为上一个fragment
     * @param navController 导航控制器，默认为当前Fragment的findNavController()
     *
     *
     * @see observeResult
     *
     * @example
     *  【】FragmentA --> FragmentB
     *  // FragmentB
     *  sendResultTo("key", "data")
     *  findNavController().navigateUp()
     *
     *  //FragmentA
     *  observeResult<String>("key") {//返回到FragmentA才会收到
     *     // 处理结果
     *     val d = it //d为"data"
     *  }
     *
     *  【】FragmentA --> FragmentB --> FragmentC
     *  //FragmentC
     *  sendResultTo("key", "data",R.id.fragmentA)
     *
     *  //FragmentA
     *  observeResult<String>("key") { //返回到FragmentA才会收到
     *    // 处理结果
     *    val d = it //d为"data"
     * }
     */
    fun <T> Fragment.sendResult(key: String, value: T,
                                destinationId: Int? = null,
                                navController:NavController = findNavController()
    ) {
        val handle = if (destinationId == null) {
            navController.previousBackStackEntry?.savedStateHandle
        } else {
            navController.getBackStackEntry(destinationId).savedStateHandle
        }
        handle?.set(key, value)
    }

    /**
     * 监听其他fragment发送过来的result（一次）
     *
     * @see sendResult
     * @param key
     * @param fromId navigation.xml中定义的fragmentID。默认为当前fragment
     * @param navController 导航控制器，默认为当前Fragment的findNavController()
     * @param onResult
     */
    inline fun <reified T> Fragment.observeResultOnce(
        key: String,
        fromId: Int? = null,
        navController: NavController = findNavController(),
        noinline onResult: (T) -> Unit
    ) {
        //移除，避免重复注册
        removeObserver<T>(key, onResult, fromId)
        val handle = if (fromId == null) {
            navController.currentBackStackEntry?.savedStateHandle
        } else {
            navController.getBackStackEntry(fromId).savedStateHandle
        }
        handle?.getLiveData<T>(key)?.apply {
            observe(viewLifecycleOwner, onResult)
            observe(viewLifecycleOwner,object :Observer<T>{
                override fun onChanged(value: T) {
                    removeObserver<T>(key, onResult, fromId)
                    removeObserver(this)
                }
            })
        }
    }

    /**
     * 监听其他fragment发送过来的result
     * 在navigationUp时或者返回收到
     *
     * @see sendResult
     * @param key
     * @param fromId navigation.xml中定义的fragmentID。默认为当前fragment
     * @param navController 导航控制器，默认为当前Fragment的findNavController()
     * @param onResult
     */
    inline fun <reified T> Fragment.observeResult(
        key: String,
        fromId: Int? = null,
        navController: NavController = findNavController(),
        noinline onResult: (T) -> Unit
    ) {
        //移除，避免重复注册
        removeObserver<T>(key, onResult, fromId)
        val handle = if (fromId == null) {
            navController.currentBackStackEntry?.savedStateHandle
        } else {
            navController.getBackStackEntry(fromId).savedStateHandle
        }
        //不能先remove，会移除先set的值
        handle?.getLiveData<T>(key)?.observe(viewLifecycleOwner, onResult)
    }

    /**
     * 移除观察者
     *
     * @param T
     * @param key
     * @param onResult
     * @param fromId
     * @param navController 导航控制器，默认为当前Fragment的findNavController()
     */
    inline fun <reified T> Fragment.removeObserver(
        key: String,
        noinline onResult: (T) -> Unit,
        fromId: Int? = null,
        navController: NavController = findNavController()
    ) {
        val handle = if (fromId == null) {
            navController.currentBackStackEntry?.savedStateHandle
        } else {
            navController.getBackStackEntry(fromId).savedStateHandle
        }
        handle?.getLiveData<T>(key)?.removeObserver(onResult)
    }

    /**
     * 移除所有观察者
     * @param T
     * @param key
     * @param fromId
     * @param navController 导航控制器，默认为当前Fragment的findNavController()
     */
    fun <T> Fragment.removeObservers(key: String,
                                     fromId: Int? = null,
                                     navController: NavController = findNavController()
    ) {
        val handle = if (fromId == null) {
            navController.currentBackStackEntry?.savedStateHandle
        } else {
            navController.getBackStackEntry(fromId).savedStateHandle
        }
        handle?.getLiveData<T>(key)?.removeObservers(viewLifecycleOwner)
    }
}