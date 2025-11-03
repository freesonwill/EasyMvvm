package arch.cayenne.lib.common.utils.biz

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import arch.cayenne.lib.common.data.constants.FragmentResultEnum

/**
 * @date: 2025/11/3 16:32
 * @description: 通用业务
 */
object CommonBiz: IBiz {

    /**
     * 跳转客服
     */
    fun jump2CustomerService(fragment: Fragment) {
        fragment.setFragmentResult(
            FragmentResultEnum.KEY_PAGE.k, bundleOf(
                FragmentResultEnum.KEY_PAGE.k to 3,
                FragmentResultEnum.KEY_CUSTOMER_SERVICE.k to true
            )
        )
    }

}