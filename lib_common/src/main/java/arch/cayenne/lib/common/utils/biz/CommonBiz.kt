package arch.cayenne.lib.common.utils.biz

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import arch.cayenne.lib.common.data.constants.FragmentResultEnum
import arch.cayenne.lib.common.data.constants.HomePageEnum

/**
 * @date: 2025/11/3 16:32
 * @description: 通用业务
 */
object CommonBiz: IBiz {


    /**
     * 跳转客服
     */
    fun jump2CustomerService(fragment: Fragment) {
        jump2HomePage(fragment,HomePageEnum.ME, FragmentResultEnum.KEY_CUSTOMER_SERVICE.k to true)
    }

    /**
     * 跳转页面
     * @param fragment
     * @param page
     * @param other
     */
    fun jump2HomePage(fragment: Fragment, page:HomePageEnum, vararg other: Pair<String, Any?>) {
        fragment.setFragmentResult(
            FragmentResultEnum.KEY_PAGE.k, bundleOf(
                FragmentResultEnum.KEY_PAGE.k to page.v,
                *other
            )
        )
    }

}