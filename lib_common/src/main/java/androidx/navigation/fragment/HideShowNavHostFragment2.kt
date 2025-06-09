package androidx.navigation.fragment

import androidx.navigation.NavController
import androidx.navigation.plusAssign

/**
 * @author: zhangsan
 * @date: 2025/5/27 14:16
 * @description:
 */
class HideShowNavHostFragment2 : NavHostFragment() {

    override fun onCreateNavController(navController: NavController) {
        navController.navigatorProvider += HideShowFragmentNavigator3(
            requireContext(),
            childFragmentManager,
            id
        )
        super.onCreateNavController(navController)

        /* // 设置 navigation graph（必须在添加 navigator 之后）
         val graphId = navGraphId
         val navInflater = navController.navInflater
         if (graphId != 0) {
             val navGraph = navInflater.inflate(graphId)
             navController.graph = navGraph
         }*/
    }

//    private val navGraphId: Int
//        get() = requireArguments().getInt("android-support-nav:fragment:graph")

}