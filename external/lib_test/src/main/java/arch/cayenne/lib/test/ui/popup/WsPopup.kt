package arch.cayenne.lib.test.ui.popup

import android.content.Context
import arch.cayenne.lib.test.R
import com.blankj.utilcode.util.ScreenUtils
import com.lxj.xpopup.core.BottomPopupView

/**
 * @date: 2025/9/17 11:21
 * @description:
 */
class WsPopup(context: Context) : BottomPopupView(context) {

    override fun getImplLayoutId(): Int { return R.layout.demo_ws_popup }
}