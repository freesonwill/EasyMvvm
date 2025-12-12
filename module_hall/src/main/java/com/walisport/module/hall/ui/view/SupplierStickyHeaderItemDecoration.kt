package arch.cayenne.module.home.ui.view.decoration

import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.widget.biz.ISkinnableBiz
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SupplierStickyHeaderItemDecoration(
    private val isHeader: (position: Int) -> Boolean,
    private val createHeaderView: (Context, ViewGroup) -> View,
    private val bindHeaderView: (headerView: View, position: Int) -> Unit
) : RecyclerView.ItemDecoration() {

    private val skinManager: SkinnableManager by inject(SkinnableManager::class.java)
    private var skinObserverStarted = false

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        // 應用換膚監聽：由 ItemDecoration 自身訂閱，收到事件時重繪自己
        if (!skinObserverStarted) {
            parent.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                skinManager.skinFlow.collect {
                    parent.invalidateItemDecorations()
                }
            }
            skinObserverStarted = true
        }

        val topChild = parent.getChildAt(0) ?: return
        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) return

        val headerPos = findCurrentHeaderPosition(topChildPosition)
        if (headerPos == -1) return

        val context = parent.context
        val headerView = createHeaderView(context, parent)
        bindHeaderView(headerView, headerPos)

        // 由於 headerView 不在 View 樹上，手動觸發整棵視圖樹的換膚
        forceUpdateSkin(headerView)

        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        headerView.measure(widthSpec, heightSpec)
        headerView.layout(
            0,
            2.dp2px,
            parent.width,
            parent.paddingTop + headerView.measuredHeight
        )
        headerView.draw(c)
    }

    private fun findCurrentHeaderPosition(from: Int): Int {
        for (position in from downTo 0) {
            if (isHeader(position)) return position
        }
        return -1
    }

    private fun getChildInContact(parent: RecyclerView, contactY: Int): View? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child.top <= contactY && child.bottom >= contactY) {
                return child
            }
        }
        return null
    }

    private fun forceUpdateSkin(root: View) {
        if (root is ISkinnableBiz) {
            root.updateSkin(SkinMsgType.SELF)
        }
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                val child = root.getChildAt(i)
                if (child is ViewGroup) {
                    forceUpdateSkin(child)
                } else if (child is ISkinnableBiz) {
                    child.updateSkin(SkinMsgType.SELF)
                } else {
                    // 非 ISkinnableBiz 的普通 View 略過
                }
            }
        }
    }
}
