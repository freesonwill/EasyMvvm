package arch.cayenne.lib.base.data.model

import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.ui.fragment.BaseFragment

/***
 * TabLayout & ViewPager 所用
 * @param page 使用回調方式創建Fragment, 避免頁面留存造成洩漏
 */
open class PagerBean(
    val title: String,
    val page: (() -> Fragment)
)

class PageBeans(
    title: String,
    page: (() -> BaseFragment<*, *>)
) : PagerBean(title, page)