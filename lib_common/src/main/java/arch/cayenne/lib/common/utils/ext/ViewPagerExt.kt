package arch.cayenne.lib.common.utils.ext

import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout

fun TabLayout.removeAllTips() {
    post {
        for (i in 0 until tabCount) {
            getTabAt(i)?.view?.let { tabView ->
                TooltipCompat.setTooltipText(tabView, null)
                tabView.setOnLongClickListener { false }
                tabView.isLongClickable = false
            }
        }
    }
}

/**
 * 设置clip
 *
 * @param b
 */
@JvmOverloads
fun ViewPager2.setClipChilds(b: Boolean) {
    val view = getChildAt(0)
    if (view is RecyclerView) {
        view.clipChildren = b
        view.clipChildren = b
    }
}
